package com.siamts.synapse.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.siamts.synapse.SynapseApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Local SQLite & In-Memory fallback implementation of QuizDao for 'quiz_records' table.
 */
class QuizLocalDatabase private constructor(context: Context?) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
), QuizDao, ChapterDao {

    // Thread-safe in-memory cache and JVM unit-test fallback
    private val inMemoryRecords = CopyOnWriteArrayList<QuizRecordEntity>()
    private val inMemoryQuestions = java.util.concurrent.ConcurrentHashMap<String, List<com.siamts.synapse.data.model.QuizQuestionDto>>()
    private val inMemoryChapters = CopyOnWriteArrayList<ChapterEntity>()
    private val hasAndroidContext = context != null

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS quiz_records (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                subject_id TEXT NOT NULL,
                chapter_id TEXT NOT NULL,
                score INTEGER NOT NULL,
                total_questions INTEGER NOT NULL,
                completed_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS chapter_questions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                subject_id TEXT NOT NULL,
                chapter_id TEXT NOT NULL,
                question_json TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS subject_chapters (
                id TEXT PRIMARY KEY,
                subject_id TEXT NOT NULL,
                chapter_order INTEGER NOT NULL,
                title TEXT NOT NULL,
                summary TEXT,
                question_count INTEGER NOT NULL,
                estimated_minutes INTEGER NOT NULL,
                total_marks INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("CREATE TABLE IF NOT EXISTS chapter_questions (id INTEGER PRIMARY KEY AUTOINCREMENT, subject_id TEXT NOT NULL, chapter_id TEXT NOT NULL, question_json TEXT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS subject_chapters (id TEXT PRIMARY KEY, subject_id TEXT NOT NULL, chapter_order INTEGER NOT NULL, title TEXT NOT NULL, summary TEXT, question_count INTEGER NOT NULL, estimated_minutes INTEGER NOT NULL, total_marks INTEGER NOT NULL)")
    }

    override suspend fun insertQuizRecord(record: QuizRecordEntity): Long = withContext(Dispatchers.IO) {
        val assignedId = System.currentTimeMillis()
        val recordWithId = record.copy(id = if (record.id > 0) record.id else assignedId)
        inMemoryRecords.add(recordWithId)

        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                val values = ContentValues().apply {
                    put("subject_id", record.subjectId)
                    put("chapter_id", record.chapterId)
                    put("score", record.score)
                    put("total_questions", record.totalQuestions)
                    put("completed_at", record.completedAt)
                }
                return@withContext db.insert("quiz_records", null, values)
            } catch (_: Throwable) {
                // Return assigned in-memory ID if SQLite fails
                return@withContext assignedId
            }
        }
        assignedId
    }

    override suspend fun getAllRecords(): List<QuizRecordEntity> = withContext(Dispatchers.IO) {
        if (!hasAndroidContext) {
            return@withContext inMemoryRecords.toList()
        }

        try {
            val db = readableDatabase
            val cursor = db.query(
                "quiz_records",
                null,
                null,
                null,
                null,
                null,
                "completed_at DESC"
            )
            val list = mutableListOf<QuizRecordEntity>()
            cursor.use { c ->
                val idCol = c.getColumnIndex("id")
                val subCol = c.getColumnIndex("subject_id")
                val chapCol = c.getColumnIndex("chapter_id")
                val scoreCol = c.getColumnIndex("score")
                val totalCol = c.getColumnIndex("total_questions")
                val dateCol = c.getColumnIndex("completed_at")

                while (c.moveToNext()) {
                    list.add(
                        QuizRecordEntity(
                            id = if (idCol >= 0) c.getLong(idCol) else 0L,
                            subjectId = if (subCol >= 0) c.getString(subCol) else "",
                            chapterId = if (chapCol >= 0) c.getString(chapCol) else "",
                            score = if (scoreCol >= 0) c.getInt(scoreCol) else 0,
                            totalQuestions = if (totalCol >= 0) c.getInt(totalCol) else 0,
                            completedAt = if (dateCol >= 0) c.getLong(dateCol) else 0L
                        )
                    )
                }
            }
            if (list.isNotEmpty()) list else inMemoryRecords.toList()
        } catch (_: Throwable) {
            inMemoryRecords.toList()
        }
    }

    override suspend fun getRecordsByChapter(chapterId: String): List<QuizRecordEntity> = withContext(Dispatchers.IO) {
        getAllRecords().filter { it.chapterId == chapterId }
    }

    override suspend fun getRecordsBySubject(subjectId: String): List<QuizRecordEntity> = withContext(Dispatchers.IO) {
        getAllRecords().filter { it.subjectId == subjectId }
    }

    override suspend fun getQuestionsForChapter(
        subjectId: String,
        chapterId: String
    ): List<com.siamts.synapse.data.model.QuizQuestionDto> = withContext(Dispatchers.IO) {
        val cacheKey = "${subjectId.trim().lowercase()}_${chapterId.trim()}"
        val cached = inMemoryQuestions[cacheKey] ?: inMemoryQuestions[chapterId.trim()]
        if (!cached.isNullOrEmpty()) {
            return@withContext cached
        }

        if (hasAndroidContext) {
            try {
                val db = readableDatabase
                val cursor = db.query(
                    "chapter_questions",
                    arrayOf("question_json"),
                    "chapter_id = ? OR (subject_id = ? AND chapter_id = ?)",
                    arrayOf(chapterId.trim(), subjectId.trim(), chapterId.trim()),
                    null,
                    null,
                    "id ASC"
                )
                val list = mutableListOf<com.siamts.synapse.data.model.QuizQuestionDto>()
                val gson = com.google.gson.Gson()
                cursor.use { c ->
                    val jsonCol = c.getColumnIndex("question_json")
                    while (c.moveToNext()) {
                        if (jsonCol >= 0) {
                            val json = c.getString(jsonCol)
                            val dto = gson.fromJson(json, com.siamts.synapse.data.model.QuizQuestionDto::class.java)
                            if (dto != null) list.add(dto)
                        }
                    }
                }
                if (list.isNotEmpty()) {
                    inMemoryQuestions[cacheKey] = list
                    inMemoryQuestions[chapterId.trim()] = list
                    return@withContext list
                }
            } catch (_: Throwable) {}
        }
        emptyList()
    }

    override suspend fun insertQuestionsForChapter(
        subjectId: String,
        chapterId: String,
        questions: List<com.siamts.synapse.data.model.QuizQuestionDto>
    ) = withContext(Dispatchers.IO) {
        if (questions.isEmpty()) return@withContext
        val cacheKey = "${subjectId.trim().lowercase()}_${chapterId.trim()}"
        inMemoryQuestions[cacheKey] = questions
        inMemoryQuestions[chapterId.trim()] = questions

        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                val gson = com.google.gson.Gson()
                db.beginTransaction()
                try {
                    db.delete("chapter_questions", "chapter_id = ?", arrayOf(chapterId.trim()))
                    for (q in questions) {
                        val values = ContentValues().apply {
                            put("subject_id", subjectId.trim())
                            put("chapter_id", chapterId.trim())
                            put("question_json", gson.toJson(q))
                        }
                        db.insert("chapter_questions", null, values)
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
            } catch (_: Throwable) {}
        }
    }

    override suspend fun getChaptersBySubject(subjectId: String): List<ChapterEntity> = withContext(Dispatchers.IO) {
        val trimmed = subjectId.trim()
        val normalized = trimmed.lowercase(java.util.Locale.ROOT)

        val inMemory = inMemoryChapters.filter {
            it.subjectId.equals(trimmed, ignoreCase = true) ||
            it.subjectId.equals(normalized, ignoreCase = true)
        }.sortedBy { it.chapterOrder }

        if (inMemory.isNotEmpty() || !hasAndroidContext) {
            return@withContext inMemory
        }

        try {
            val db = readableDatabase
            val cursor = db.query(
                "subject_chapters",
                null,
                "LOWER(subject_id) = ?",
                arrayOf(normalized),
                null,
                null,
                "chapter_order ASC"
            )
            val list = mutableListOf<ChapterEntity>()
            cursor.use { c ->
                val idCol = c.getColumnIndex("id")
                val subCol = c.getColumnIndex("subject_id")
                val ordCol = c.getColumnIndex("chapter_order")
                val titCol = c.getColumnIndex("title")
                val sumCol = c.getColumnIndex("summary")
                val cntCol = c.getColumnIndex("question_count")
                val minCol = c.getColumnIndex("estimated_minutes")
                val mrkCol = c.getColumnIndex("total_marks")

                while (c.moveToNext()) {
                    list.add(
                        ChapterEntity(
                            id = if (idCol >= 0) c.getString(idCol) else "",
                            subjectId = if (subCol >= 0) c.getString(subCol) else "",
                            chapterOrder = if (ordCol >= 0) c.getInt(ordCol) else 1,
                            title = if (titCol >= 0) c.getString(titCol) else "",
                            summary = if (sumCol >= 0) c.getString(sumCol).orEmpty() else "",
                            questionCount = if (cntCol >= 0) c.getInt(cntCol) else 5,
                            estimatedMinutes = if (minCol >= 0) c.getInt(minCol) else 10,
                            totalMarks = if (mrkCol >= 0) c.getInt(mrkCol) else 50
                        )
                    )
                }
            }
            if (list.isNotEmpty()) {
                inMemoryChapters.addAll(list.filter { item -> inMemoryChapters.none { it.id == item.id } })
            }
            list
        } catch (_: Throwable) {
            inMemory
        }
    }

    override suspend fun insertChapters(chapters: List<ChapterEntity>): Unit = withContext(Dispatchers.IO) {
        for (chap in chapters) {
            inMemoryChapters.removeAll { it.id == chap.id }
            inMemoryChapters.add(chap)
        }

        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                db.beginTransaction()
                try {
                    for (chap in chapters) {
                        val values = ContentValues().apply {
                            put("id", chap.id)
                            put("subject_id", chap.subjectId.trim())
                            put("chapter_order", chap.chapterOrder)
                            put("title", chap.title)
                            put("summary", chap.summary)
                            put("question_count", chap.questionCount)
                            put("estimated_minutes", chap.estimatedMinutes)
                            put("total_marks", chap.totalMarks)
                        }
                        db.insertWithOnConflict("subject_chapters", null, values, SQLiteDatabase.CONFLICT_REPLACE)
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
            } catch (_: Throwable) {}
        }
    }

    override suspend fun getAllChapters(): List<ChapterEntity> = withContext(Dispatchers.IO) {
        if (!hasAndroidContext) {
            return@withContext inMemoryChapters.toList()
        }
        try {
            val db = readableDatabase
            val cursor = db.query("subject_chapters", null, null, null, null, null, "chapter_order ASC")
            val list = mutableListOf<ChapterEntity>()
            cursor.use { c ->
                val idCol = c.getColumnIndex("id")
                val subCol = c.getColumnIndex("subject_id")
                val ordCol = c.getColumnIndex("chapter_order")
                val titCol = c.getColumnIndex("title")
                val sumCol = c.getColumnIndex("summary")
                val cntCol = c.getColumnIndex("question_count")
                val minCol = c.getColumnIndex("estimated_minutes")
                val mrkCol = c.getColumnIndex("total_marks")

                while (c.moveToNext()) {
                    list.add(
                        ChapterEntity(
                            id = if (idCol >= 0) c.getString(idCol) else "",
                            subjectId = if (subCol >= 0) c.getString(subCol) else "",
                            chapterOrder = if (ordCol >= 0) c.getInt(ordCol) else 1,
                            title = if (titCol >= 0) c.getString(titCol) else "",
                            summary = if (sumCol >= 0) c.getString(sumCol).orEmpty() else "",
                            questionCount = if (cntCol >= 0) c.getInt(cntCol) else 5,
                            estimatedMinutes = if (minCol >= 0) c.getInt(minCol) else 10,
                            totalMarks = if (mrkCol >= 0) c.getInt(mrkCol) else 50
                        )
                    )
                }
            }
            list
        } catch (_: Throwable) {
            inMemoryChapters.toList()
        }
    }

    companion object {
        private const val DATABASE_NAME = "synapse_quiz.db"
        private const val DATABASE_VERSION = 3

        @Volatile
        private var INSTANCE: QuizLocalDatabase? = null

        fun getInstance(context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }): QuizDao {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: QuizLocalDatabase(context).also { INSTANCE = it }
            }
        }

        fun getChapterDao(context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }): ChapterDao {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: QuizLocalDatabase(context).also { INSTANCE = it }
            }
        }
    }
}
