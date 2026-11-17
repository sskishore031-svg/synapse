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
), QuizDao, ChapterDao, LearningTaskDao, UserDao {

    // Thread-safe in-memory cache and JVM unit-test fallback
    private val inMemoryRecords = CopyOnWriteArrayList<QuizRecordEntity>()
    private val inMemoryQuestions = java.util.concurrent.ConcurrentHashMap<String, List<com.siamts.synapse.data.model.QuizQuestionDto>>()
    private val inMemoryChapters = CopyOnWriteArrayList<ChapterEntity>()
    private val inMemoryTasks = CopyOnWriteArrayList<LearningTaskEntity>()
    private val inMemoryUsers = CopyOnWriteArrayList<com.siamts.synapse.data.model.UserDto>()
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
            CREATE TABLE IF NOT EXISTS quiz_questions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                subject_id TEXT NOT NULL,
                chapter_id TEXT NOT NULL,
                question_text TEXT NOT NULL,
                option_a TEXT NOT NULL,
                option_b TEXT NOT NULL,
                option_c TEXT NOT NULL,
                option_d TEXT NOT NULL,
                correct_option TEXT NOT NULL,
                explanation TEXT,
                question_json TEXT NOT NULL
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
            CREATE TABLE IF NOT EXISTS chapter_lessons (
                chapter_id TEXT PRIMARY KEY,
                subject_id TEXT NOT NULL,
                title TEXT NOT NULL,
                part1_concept TEXT NOT NULL,
                part2_application TEXT NOT NULL,
                exam_tip TEXT,
                formulas_json TEXT,
                syllabus_json TEXT
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
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS learning_plan_tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                category TEXT NOT NULL,
                duration TEXT NOT NULL,
                is_completed INTEGER NOT NULL DEFAULT 0,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                phone TEXT,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS quiz_questions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    subject_id TEXT NOT NULL,
                    chapter_id TEXT NOT NULL,
                    question_text TEXT NOT NULL,
                    option_a TEXT NOT NULL,
                    option_b TEXT NOT NULL,
                    option_c TEXT NOT NULL,
                    option_d TEXT NOT NULL,
                    correct_option TEXT NOT NULL,
                    explanation TEXT,
                    question_json TEXT NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS chapter_lessons (
                    chapter_id TEXT PRIMARY KEY,
                    subject_id TEXT NOT NULL,
                    title TEXT NOT NULL,
                    part1_concept TEXT NOT NULL,
                    part2_application TEXT NOT NULL,
                    exam_tip TEXT,
                    formulas_json TEXT,
                    syllabus_json TEXT
                )
                """.trimIndent()
            )
            db.execSQL("CREATE TABLE IF NOT EXISTS chapter_questions (id INTEGER PRIMARY KEY AUTOINCREMENT, subject_id TEXT NOT NULL, chapter_id TEXT NOT NULL, question_json TEXT NOT NULL)")
            db.execSQL("CREATE TABLE IF NOT EXISTS subject_chapters (id TEXT PRIMARY KEY, subject_id TEXT NOT NULL, chapter_order INTEGER NOT NULL, title TEXT NOT NULL, summary TEXT, question_count INTEGER NOT NULL, estimated_minutes INTEGER NOT NULL, total_marks INTEGER NOT NULL)")
            db.execSQL("CREATE TABLE IF NOT EXISTS learning_plan_tasks (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, category TEXT NOT NULL, duration TEXT NOT NULL, is_completed INTEGER NOT NULL DEFAULT 0, created_at INTEGER NOT NULL)")
            db.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, email TEXT NOT NULL UNIQUE, phone TEXT, created_at INTEGER NOT NULL)")
        } catch (_: Throwable) {
            // Fallback to destructive migration: drop and recreate cleanly
            try {
                db.execSQL("DROP TABLE IF EXISTS quiz_questions")
                db.execSQL("DROP TABLE IF EXISTS chapter_lessons")
                db.execSQL("DROP TABLE IF EXISTS chapter_questions")
                db.execSQL("DROP TABLE IF EXISTS subject_chapters")
                onCreate(db)
            } catch (_: Throwable) {}
        }
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
                val list = mutableListOf<com.siamts.synapse.data.model.QuizQuestionDto>()
                val gson = com.google.gson.Gson()

                // 1. Try quiz_questions table first
                try {
                    val cursorQuiz = db.query(
                        "quiz_questions",
                        arrayOf("question_json"),
                        "chapter_id = ? OR (subject_id = ? AND chapter_id = ?)",
                        arrayOf(chapterId.trim(), subjectId.trim(), chapterId.trim()),
                        null,
                        null,
                        "id ASC"
                    )
                    cursorQuiz.use { c ->
                        val jsonCol = c.getColumnIndex("question_json")
                        while (c.moveToNext()) {
                            if (jsonCol >= 0) {
                                val json = c.getString(jsonCol)
                                val dto = gson.fromJson(json, com.siamts.synapse.data.model.QuizQuestionDto::class.java)
                                if (dto != null) list.add(dto)
                            }
                        }
                    }
                } catch (_: Throwable) {}

                // 2. Fallback to chapter_questions if empty
                if (list.isEmpty()) {
                    val cursorChap = db.query(
                        "chapter_questions",
                        arrayOf("question_json"),
                        "chapter_id = ? OR (subject_id = ? AND chapter_id = ?)",
                        arrayOf(chapterId.trim(), subjectId.trim(), chapterId.trim()),
                        null,
                        null,
                        "id ASC"
                    )
                    cursorChap.use { c ->
                        val jsonCol = c.getColumnIndex("question_json")
                        while (c.moveToNext()) {
                            if (jsonCol >= 0) {
                                val json = c.getString(jsonCol)
                                val dto = gson.fromJson(json, com.siamts.synapse.data.model.QuizQuestionDto::class.java)
                                if (dto != null) list.add(dto)
                            }
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
                    try {
                        db.delete("quiz_questions", "chapter_id = ?", arrayOf(chapterId.trim()))
                    } catch (_: Throwable) {}

                    for (q in questions) {
                        val qJson = gson.toJson(q)
                        val values = ContentValues().apply {
                            put("subject_id", subjectId.trim())
                            put("chapter_id", chapterId.trim())
                            put("question_json", qJson)
                        }
                        db.insert("chapter_questions", null, values)

                        try {
                            val qValues = ContentValues().apply {
                                put("subject_id", subjectId.trim())
                                put("chapter_id", chapterId.trim())
                                put("question_text", q.question)
                                put("option_a", q.options["A"].orEmpty())
                                put("option_b", q.options["B"].orEmpty())
                                put("option_c", q.options["C"].orEmpty())
                                put("option_d", q.options["D"].orEmpty())
                                put("correct_option", q.correctOption)
                                put("explanation", q.explanation.orEmpty())
                                put("question_json", qJson)
                            }
                            db.insert("quiz_questions", null, qValues)
                        } catch (_: Throwable) {}
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

    override suspend fun insertChapterLesson(
        chapterId: Int,
        subjectId: Int,
        title: String,
        part1Title: String,
        part1Concept: String,
        part2Title: String,
        part2Application: String,
        examTip: String
    ): Unit = withContext(Dispatchers.IO) {
        if (!hasAndroidContext) return@withContext
        try {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("chapter_id", chapterId.toString())
                put("subject_id", subjectId.toString())
                put("title", title)
                put("part1_concept", part1Concept)
                put("part2_application", part2Application)
                put("exam_tip", examTip)
            }
            db.insertWithOnConflict("chapter_lessons", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        } catch (_: Throwable) {}
    }

    // =========================================================================
    // LearningTaskDao Implementation ('learning_plan_tasks')
    // =========================================================================

    override suspend fun getAllTasks(): List<LearningTaskEntity> = withContext(Dispatchers.IO) {
        if (!hasAndroidContext) {
            return@withContext inMemoryTasks.toList()
        }

        try {
            val db = readableDatabase
            val cursor = db.query(
                "learning_plan_tasks",
                null,
                null,
                null,
                null,
                null,
                "id DESC"
            )
            val list = mutableListOf<LearningTaskEntity>()
            cursor.use { c ->
                val idCol = c.getColumnIndex("id")
                val titleCol = c.getColumnIndex("title")
                val catCol = c.getColumnIndex("category")
                val durCol = c.getColumnIndex("duration")
                val compCol = c.getColumnIndex("is_completed")
                val dateCol = c.getColumnIndex("created_at")

                while (c.moveToNext()) {
                    list.add(
                        LearningTaskEntity(
                            id = if (idCol >= 0) c.getLong(idCol) else 0L,
                            title = if (titleCol >= 0) c.getString(titleCol) else "",
                            category = if (catCol >= 0) c.getString(catCol) else "Lesson",
                            duration = if (durCol >= 0) c.getString(durCol) else "15 min",
                            isCompleted = if (compCol >= 0) c.getInt(compCol) == 1 else false,
                            createdAt = if (dateCol >= 0) c.getLong(dateCol) else System.currentTimeMillis()
                        )
                    )
                }
            }
            list
        } catch (_: Throwable) {
            inMemoryTasks.toList()
        }
    }

    override suspend fun insertTask(task: LearningTaskEntity): Long = withContext(Dispatchers.IO) {
        val assignedId = System.currentTimeMillis()
        val taskWithId = task.copy(id = if (task.id > 0) task.id else assignedId)
        inMemoryTasks.add(taskWithId)

        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                val values = ContentValues().apply {
                    put("title", task.title)
                    put("category", task.category)
                    put("duration", task.duration)
                    put("is_completed", if (task.isCompleted) 1 else 0)
                    put("created_at", task.createdAt)
                }
                return@withContext db.insert("learning_plan_tasks", null, values)
            } catch (_: Throwable) {
                return@withContext assignedId
            }
        }
        assignedId
    }

    override suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        for (i in inMemoryTasks.indices) {
            if (inMemoryTasks[i].id == taskId) {
                inMemoryTasks[i] = inMemoryTasks[i].copy(isCompleted = isCompleted)
                break
            }
        }

        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                val values = ContentValues().apply {
                    put("is_completed", if (isCompleted) 1 else 0)
                }
                db.update("learning_plan_tasks", values, "id = ?", arrayOf(taskId.toString()))
            } catch (_: Throwable) {}
        }
    }

    override suspend fun deleteTask(taskId: Long) = withContext(Dispatchers.IO) {
        inMemoryTasks.removeAll { it.id == taskId }

        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                db.delete("learning_plan_tasks", "id = ?", arrayOf(taskId.toString()))
            } catch (_: Throwable) {}
        }
    }

    // =========================================================================
    // UserDao Implementation ('users')
    // =========================================================================

    override suspend fun insertOrUpdateUser(name: String, email: String, phone: String?): Long = withContext(Dispatchers.IO) {
        val assignedId = System.currentTimeMillis()
        inMemoryUsers.removeAll { it.email.equals(email, ignoreCase = true) }
        inMemoryUsers.add(
            com.siamts.synapse.data.model.UserDto(
                id = assignedId.toInt(),
                name = name,
                email = email,
                phone = phone
            )
        )

        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                val values = ContentValues().apply {
                    put("name", name)
                    put("email", email)
                    put("phone", phone)
                    put("created_at", System.currentTimeMillis())
                }
                return@withContext db.insertWithOnConflict("users", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            } catch (_: Throwable) {
                return@withContext assignedId
            }
        }
        assignedId
    }

    override suspend fun getUserByEmail(email: String): com.siamts.synapse.data.model.UserDto? = withContext(Dispatchers.IO) {
        val inMem = inMemoryUsers.firstOrNull { it.email.equals(email, ignoreCase = true) }
        if (inMem != null) return@withContext inMem

        if (hasAndroidContext) {
            try {
                val db = readableDatabase
                val cursor = db.query(
                    "users",
                    null,
                    "email = ?",
                    arrayOf(email.trim()),
                    null,
                    null,
                    null,
                    "1"
                )
                cursor.use { c ->
                    if (c.moveToFirst()) {
                        val idCol = c.getColumnIndex("id")
                        val nameCol = c.getColumnIndex("name")
                        val emailCol = c.getColumnIndex("email")
                        val phoneCol = c.getColumnIndex("phone")
                        return@withContext com.siamts.synapse.data.model.UserDto(
                            id = if (idCol >= 0) c.getInt(idCol) else 1,
                            name = if (nameCol >= 0) c.getString(nameCol) else "",
                            email = if (emailCol >= 0) c.getString(emailCol) else email,
                            phone = if (phoneCol >= 0) c.getString(phoneCol) else null
                        )
                    }
                }
            } catch (_: Throwable) {}
        }
        null
    }

    override suspend fun getUserById(userId: Int): com.siamts.synapse.data.model.UserDto? = withContext(Dispatchers.IO) {
        val inMem = inMemoryUsers.firstOrNull { it.id == userId }
        if (inMem != null) return@withContext inMem

        if (hasAndroidContext) {
            try {
                val db = readableDatabase
                val cursor = db.query(
                    "users",
                    null,
                    "id = ?",
                    arrayOf(userId.toString()),
                    null,
                    null,
                    null,
                    "1"
                )
                cursor.use { c ->
                    if (c.moveToFirst()) {
                        val idCol = c.getColumnIndex("id")
                        val nameCol = c.getColumnIndex("name")
                        val emailCol = c.getColumnIndex("email")
                        val phoneCol = c.getColumnIndex("phone")
                        return@withContext com.siamts.synapse.data.model.UserDto(
                            id = if (idCol >= 0) c.getInt(idCol) else userId,
                            name = if (nameCol >= 0) c.getString(nameCol) else "",
                            email = if (emailCol >= 0) c.getString(emailCol) else "",
                            phone = if (phoneCol >= 0) c.getString(phoneCol) else null
                        )
                    }
                }
            } catch (_: Throwable) {}
        }
        null
    }

    companion object {
        private const val DATABASE_NAME = "synapse_quiz.db"
        private const val DATABASE_VERSION = 5

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

        fun getLearningTaskDao(context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }): LearningTaskDao {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: QuizLocalDatabase(context).also { INSTANCE = it }
            }
        }

        fun getUserDao(context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }): UserDao {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: QuizLocalDatabase(context).also { INSTANCE = it }
            }
        }
    }
}
