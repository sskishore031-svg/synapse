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
), QuizDao, ChapterDao, LearningTaskDao, UserDao, NotesDao, ChatDao {

    // Thread-safe in-memory cache and JVM unit-test fallback
    private val inMemoryRecords = CopyOnWriteArrayList<QuizRecordEntity>()
    private val inMemoryMistakes = CopyOnWriteArrayList<MistakeQuestionEntity>()
    private val inMemoryQuestions = java.util.concurrent.ConcurrentHashMap<String, List<com.siamts.synapse.data.model.QuizQuestionDto>>()
    private val inMemoryChapters = CopyOnWriteArrayList<ChapterEntity>()
    private val inMemoryTasks = CopyOnWriteArrayList<LearningTaskEntity>()
    private val inMemoryUsers = CopyOnWriteArrayList<com.siamts.synapse.data.model.UserDto>()
    private val inMemoryNotes = CopyOnWriteArrayList<NoteEntity>()
    private val inMemorySessions = CopyOnWriteArrayList<ChatSessionEntity>()
    private val inMemoryChatMessages = java.util.concurrent.ConcurrentHashMap<String, List<ChatMessageEntity>>()
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
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS study_notes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                subject_id TEXT,
                chapter_id INTEGER,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                category TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS chat_sessions (
                session_id TEXT PRIMARY KEY,
                user_id INTEGER NOT NULL,
                title TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS chat_messages (
                id TEXT PRIMARY KEY,
                session_id TEXT NOT NULL,
                sender_name TEXT NOT NULL,
                text TEXT NOT NULL,
                time_ago TEXT NOT NULL,
                is_user INTEGER NOT NULL,
                paragraph2 TEXT,
                matched_subject TEXT,
                matched_chapter TEXT,
                matched_topic TEXT,
                is_concept_answer INTEGER NOT NULL DEFAULT 0,
                is_saved_note_card INTEGER NOT NULL DEFAULT 0,
                saved_note_title TEXT,
                action_command TEXT,
                timestamp INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS mistake_vault (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                subject_id TEXT NOT NULL,
                chapter_id TEXT NOT NULL,
                question_text TEXT NOT NULL UNIQUE,
                option_a TEXT NOT NULL,
                option_b TEXT NOT NULL,
                option_c TEXT NOT NULL,
                option_d TEXT NOT NULL,
                correct_option TEXT NOT NULL,
                explanation TEXT,
                question_json TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            // Version 8 migration: Recreate curriculum and question tables for the 4 placement tracks
            db.execSQL("DROP TABLE IF EXISTS quiz_questions")
            db.execSQL("DROP TABLE IF EXISTS subject_chapters")
            db.execSQL("DROP TABLE IF EXISTS chapter_lessons")
            onCreate(db)
        } catch (_: Throwable) {
            try {
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

    override suspend fun insertMistake(mistake: MistakeQuestionEntity): Long = withContext(Dispatchers.IO) {
        val assignedId = if (mistake.id > 0) mistake.id else System.currentTimeMillis()
        val entry = mistake.copy(id = assignedId)
        inMemoryMistakes.removeAll { it.questionText.trim().equals(mistake.questionText.trim(), ignoreCase = true) }
        inMemoryMistakes.add(0, entry)

        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                val values = ContentValues().apply {
                    put("subject_id", mistake.subjectId)
                    put("chapter_id", mistake.chapterId)
                    put("question_text", mistake.questionText)
                    put("option_a", mistake.optionA)
                    put("option_b", mistake.optionB)
                    put("option_c", mistake.optionC)
                    put("option_d", mistake.optionD)
                    put("correct_option", mistake.correctOption)
                    put("explanation", mistake.explanation.orEmpty())
                    put("question_json", mistake.questionJson)
                    put("created_at", mistake.createdAt)
                }
                return@withContext db.insertWithOnConflict(
                    "mistake_vault",
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
                )
            } catch (_: Throwable) {
                return@withContext assignedId
            }
        }
        assignedId
    }

    override suspend fun getAllMistakes(): List<MistakeQuestionEntity> = withContext(Dispatchers.IO) {
        if (!hasAndroidContext) {
            return@withContext inMemoryMistakes.toList()
        }
        try {
            val db = readableDatabase
            val cursor = db.query(
                "mistake_vault",
                null,
                null,
                null,
                null,
                null,
                "created_at DESC"
            )
            val list = mutableListOf<MistakeQuestionEntity>()
            cursor.use { c ->
                val idCol = c.getColumnIndex("id")
                val subCol = c.getColumnIndex("subject_id")
                val chapCol = c.getColumnIndex("chapter_id")
                val qCol = c.getColumnIndex("question_text")
                val aCol = c.getColumnIndex("option_a")
                val bCol = c.getColumnIndex("option_b")
                val cCol = c.getColumnIndex("option_c")
                val dCol = c.getColumnIndex("option_d")
                val ansCol = c.getColumnIndex("correct_option")
                val expCol = c.getColumnIndex("explanation")
                val jsonCol = c.getColumnIndex("question_json")
                val timeCol = c.getColumnIndex("created_at")

                while (c.moveToNext()) {
                    list.add(
                        MistakeQuestionEntity(
                            id = if (idCol >= 0) c.getLong(idCol) else 0L,
                            subjectId = if (subCol >= 0) c.getString(subCol) else "",
                            chapterId = if (chapCol >= 0) c.getString(chapCol) else "1",
                            questionText = if (qCol >= 0) c.getString(qCol) else "",
                            optionA = if (aCol >= 0) c.getString(aCol) else "",
                            optionB = if (bCol >= 0) c.getString(bCol) else "",
                            optionC = if (cCol >= 0) c.getString(cCol) else "",
                            optionD = if (dCol >= 0) c.getString(dCol) else "",
                            correctOption = if (ansCol >= 0) c.getString(ansCol) else "A",
                            explanation = if (expCol >= 0) c.getString(expCol) else null,
                            questionJson = if (jsonCol >= 0) c.getString(jsonCol) else "",
                            createdAt = if (timeCol >= 0) c.getLong(timeCol) else 0L
                        )
                    )
                }
            }
            if (list.isEmpty() && inMemoryMistakes.isNotEmpty()) {
                return@withContext inMemoryMistakes.toList()
            }
            list
        } catch (_: Throwable) {
            inMemoryMistakes.toList()
        }
    }

    override suspend fun getMistakesBySubject(subjectId: String): List<MistakeQuestionEntity> = withContext(Dispatchers.IO) {
        val trimmed = subjectId.trim()
        val all = getAllMistakes()
        all.filter { it.subjectId.equals(trimmed, ignoreCase = true) }
    }

    override suspend fun removeMistake(questionText: String): Int = withContext(Dispatchers.IO) {
        val trimmed = questionText.trim()
        val removedMemory = if (inMemoryMistakes.removeIf { it.questionText.trim().equals(trimmed, ignoreCase = true) }) 1 else 0
        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                val count = db.delete("mistake_vault", "question_text = ?", arrayOf(trimmed))
                return@withContext if (count > 0) count else removedMemory
            } catch (_: Throwable) {
                return@withContext removedMemory
            }
        }
        removedMemory
    }

    override suspend fun getMistakeCount(): Int = withContext(Dispatchers.IO) {
        if (!hasAndroidContext) {
            return@withContext inMemoryMistakes.size
        }
        try {
            val db = readableDatabase
            val cursor = db.rawQuery("SELECT COUNT(*) FROM mistake_vault", null)
            cursor.use { c ->
                if (c.moveToNext()) {
                    val count = c.getInt(0)
                    return@withContext if (count > 0) count else inMemoryMistakes.size
                }
            }
            inMemoryMistakes.size
        } catch (_: Throwable) {
            inMemoryMistakes.size
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

    override suspend fun markChapterCompleted(chapterId: Int): Unit = withContext(Dispatchers.IO) {
        val cidStr = chapterId.toString()
        synchronized(inMemoryChapters) {
            val idx = inMemoryChapters.indexOfFirst { it.id == cidStr }
            if (idx >= 0) {
                inMemoryChapters[idx] = inMemoryChapters[idx].copy(isAttempted = true)
            }
        }
        if (!hasAndroidContext) return@withContext
        try {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("is_attempted", 1)
            }
            db.update("subject_chapters", values, "id = ?", arrayOf(cidStr))
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

    // --- NotesDao Implementation ---
    override suspend fun insertNote(note: NoteEntity): Long = withContext(Dispatchers.IO) {
        val assignedId = if (note.id > 0) note.id else System.currentTimeMillis()
        val noteWithId = note.copy(id = assignedId)
        inMemoryNotes.add(0, noteWithId)

        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                val values = ContentValues().apply {
                    put("user_id", note.userId)
                    put("subject_id", note.subjectId)
                    put("chapter_id", note.chapterId)
                    put("title", note.title)
                    put("content", note.content)
                    put("category", note.category)
                    put("created_at", note.createdAt)
                }
                val rowId = db.insertWithOnConflict("study_notes", null, values, SQLiteDatabase.CONFLICT_REPLACE)
                return@withContext if (rowId > 0) rowId else assignedId
            } catch (_: Throwable) {
                return@withContext assignedId
            }
        }
        assignedId
    }

    override suspend fun getAllNotes(userId: Int): List<NoteEntity> = withContext(Dispatchers.IO) {
        if (!hasAndroidContext) {
            return@withContext inMemoryNotes.filter { it.userId == userId || userId == 1 }
        }

        try {
            val db = readableDatabase
            val cursor = db.query(
                "study_notes",
                null,
                "user_id = ? OR user_id = 1",
                arrayOf(userId.toString()),
                null,
                null,
                "created_at DESC"
            )
            val list = mutableListOf<NoteEntity>()
            cursor.use { c ->
                val idCol = c.getColumnIndex("id")
                val uidCol = c.getColumnIndex("user_id")
                val subCol = c.getColumnIndex("subject_id")
                val chapCol = c.getColumnIndex("chapter_id")
                val titleCol = c.getColumnIndex("title")
                val contentCol = c.getColumnIndex("content")
                val catCol = c.getColumnIndex("category")
                val dateCol = c.getColumnIndex("created_at")

                while (c.moveToNext()) {
                    list.add(
                        NoteEntity(
                            id = if (idCol >= 0) c.getLong(idCol) else 0L,
                            userId = if (uidCol >= 0) c.getInt(uidCol) else userId,
                            subjectId = if (subCol >= 0) c.getString(subCol) else null,
                            chapterId = if (chapCol >= 0 && !c.isNull(chapCol)) c.getInt(chapCol) else null,
                            title = if (titleCol >= 0) c.getString(titleCol) else "",
                            content = if (contentCol >= 0) c.getString(contentCol) else "",
                            category = if (catCol >= 0) c.getString(catCol) else "COMMON",
                            createdAt = if (dateCol >= 0) c.getLong(dateCol) else System.currentTimeMillis()
                        )
                    )
                }
            }
            if (list.isNotEmpty()) return@withContext list
        } catch (_: Throwable) {}

        inMemoryNotes.filter { it.userId == userId || userId == 1 }
    }

    override suspend fun getNotesBySubject(userId: Int, subjectId: String): List<NoteEntity> = withContext(Dispatchers.IO) {
        getAllNotes(userId).filter { it.subjectId.equals(subjectId, ignoreCase = true) }
    }

    override suspend fun getNoteById(noteId: Long): NoteEntity? = withContext(Dispatchers.IO) {
        if (hasAndroidContext) {
            try {
                val db = readableDatabase
                val cursor = db.query(
                    "study_notes",
                    null,
                    "id = ?",
                    arrayOf(noteId.toString()),
                    null,
                    null,
                    null
                )
                cursor.use { c ->
                    if (c.moveToFirst()) {
                        val idCol = c.getColumnIndex("id")
                        val uidCol = c.getColumnIndex("user_id")
                        val subCol = c.getColumnIndex("subject_id")
                        val chapCol = c.getColumnIndex("chapter_id")
                        val titleCol = c.getColumnIndex("title")
                        val contentCol = c.getColumnIndex("content")
                        val catCol = c.getColumnIndex("category")
                        val dateCol = c.getColumnIndex("created_at")

                        return@withContext NoteEntity(
                            id = if (idCol >= 0) c.getLong(idCol) else noteId,
                            userId = if (uidCol >= 0) c.getInt(uidCol) else 1,
                            subjectId = if (subCol >= 0) c.getString(subCol) else null,
                            chapterId = if (chapCol >= 0 && !c.isNull(chapCol)) c.getInt(chapCol) else null,
                            title = if (titleCol >= 0) c.getString(titleCol) else "",
                            content = if (contentCol >= 0) c.getString(contentCol) else "",
                            category = if (catCol >= 0) c.getString(catCol) else "COMMON",
                            createdAt = if (dateCol >= 0) c.getLong(dateCol) else System.currentTimeMillis()
                        )
                    }
                }
            } catch (_: Throwable) {}
        }
        inMemoryNotes.find { it.id == noteId }
    }

    override suspend fun deleteNote(noteId: Long): Int = withContext(Dispatchers.IO) {
        inMemoryNotes.removeIf { it.id == noteId }
        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                return@withContext db.delete("study_notes", "id = ?", arrayOf(noteId.toString()))
            } catch (_: Throwable) {}
        }
        1
    }

    // --- ChatDao Implementation ---

    override suspend fun insertOrUpdateSession(session: ChatSessionEntity) = withContext(Dispatchers.IO) {
        val existingIndex = inMemorySessions.indexOfFirst { it.sessionId == session.sessionId }
        if (existingIndex >= 0) {
            inMemorySessions[existingIndex] = session
        } else {
            inMemorySessions.add(0, session)
        }

        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                val values = ContentValues().apply {
                    put("session_id", session.sessionId)
                    put("user_id", session.userId)
                    put("title", session.title)
                    put("created_at", session.createdAt)
                    put("updated_at", session.updatedAt)
                }
                db.insertWithOnConflict("chat_sessions", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            } catch (_: Throwable) {}
        }
    }

    override suspend fun getAllSessions(userId: Int): List<ChatSessionEntity> = withContext(Dispatchers.IO) {
        if (!hasAndroidContext) {
            return@withContext inMemorySessions.filter { it.userId == userId }.sortedByDescending { it.updatedAt }
        }

        try {
            val db = readableDatabase
            val cursor = db.query(
                "chat_sessions",
                null,
                "user_id = ?",
                arrayOf(userId.toString()),
                null,
                null,
                "updated_at DESC"
            )
            val list = mutableListOf<ChatSessionEntity>()
            cursor.use {
                while (it.moveToNext()) {
                    list.add(
                        ChatSessionEntity(
                            sessionId = it.getString(it.getColumnIndexOrThrow("session_id")),
                            userId = it.getInt(it.getColumnIndexOrThrow("user_id")),
                            title = it.getString(it.getColumnIndexOrThrow("title")),
                            createdAt = it.getLong(it.getColumnIndexOrThrow("created_at")),
                            updatedAt = it.getLong(it.getColumnIndexOrThrow("updated_at"))
                        )
                    )
                }
            }
            if (list.isNotEmpty()) return@withContext list
        } catch (_: Throwable) {}
        inMemorySessions.filter { it.userId == userId }.sortedByDescending { it.updatedAt }
    }

    override suspend fun getSessionById(sessionId: String): ChatSessionEntity? = withContext(Dispatchers.IO) {
        if (hasAndroidContext) {
            try {
                val db = readableDatabase
                val cursor = db.query(
                    "chat_sessions",
                    null,
                    "session_id = ?",
                    arrayOf(sessionId),
                    null,
                    null,
                    null,
                    "1"
                )
                cursor.use {
                    if (it.moveToFirst()) {
                        return@withContext ChatSessionEntity(
                            sessionId = it.getString(it.getColumnIndexOrThrow("session_id")),
                            userId = it.getInt(it.getColumnIndexOrThrow("user_id")),
                            title = it.getString(it.getColumnIndexOrThrow("title")),
                            createdAt = it.getLong(it.getColumnIndexOrThrow("created_at")),
                            updatedAt = it.getLong(it.getColumnIndexOrThrow("updated_at"))
                        )
                    }
                }
            } catch (_: Throwable) {}
        }
        inMemorySessions.find { it.sessionId == sessionId }
    }

    override suspend fun updateSessionTitle(sessionId: String, title: String, updatedAt: Long) = withContext(Dispatchers.IO) {
        val existing = inMemorySessions.find { it.sessionId == sessionId }
        if (existing != null) {
            val updated = existing.copy(title = title, updatedAt = updatedAt)
            val idx = inMemorySessions.indexOf(existing)
            if (idx >= 0) inMemorySessions[idx] = updated
        }

        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                val values = ContentValues().apply {
                    put("title", title)
                    put("updated_at", updatedAt)
                }
                db.update("chat_sessions", values, "session_id = ?", arrayOf(sessionId))
            } catch (_: Throwable) {}
        }
    }

    override suspend fun deleteSession(sessionId: String) = withContext(Dispatchers.IO) {
        inMemorySessions.removeAll { it.sessionId == sessionId }
        inMemoryChatMessages.remove(sessionId)

        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                db.delete("chat_sessions", "session_id = ?", arrayOf(sessionId))
                db.delete("chat_messages", "session_id = ?", arrayOf(sessionId))
            } catch (_: Throwable) {}
        }
    }

    override suspend fun saveMessages(sessionId: String, messages: List<ChatMessageEntity>) = withContext(Dispatchers.IO) {
        inMemoryChatMessages[sessionId] = messages

        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                db.beginTransaction()
                try {
                    db.delete("chat_messages", "session_id = ?", arrayOf(sessionId))
                    for (msg in messages) {
                        val values = ContentValues().apply {
                            put("id", msg.id)
                            put("session_id", sessionId)
                            put("sender_name", msg.senderName)
                            put("text", msg.text)
                            put("time_ago", msg.timeAgo)
                            put("is_user", if (msg.isUser) 1 else 0)
                            put("paragraph2", msg.paragraph2)
                            put("matched_subject", msg.matchedSubject)
                            put("matched_chapter", msg.matchedChapter)
                            put("matched_topic", msg.matchedTopic)
                            put("is_concept_answer", if (msg.isConceptAnswer) 1 else 0)
                            put("is_saved_note_card", if (msg.isSavedNoteCard) 1 else 0)
                            put("saved_note_title", msg.savedNoteTitle)
                            put("action_command", msg.actionCommand)
                            put("timestamp", msg.timestamp)
                        }
                        db.insertWithOnConflict("chat_messages", null, values, SQLiteDatabase.CONFLICT_REPLACE)
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
            } catch (_: Throwable) {}
        }
    }

    override suspend fun getMessagesForSession(sessionId: String): List<ChatMessageEntity> = withContext(Dispatchers.IO) {
        if (!hasAndroidContext) {
            return@withContext inMemoryChatMessages[sessionId].orEmpty()
        }

        try {
            val db = readableDatabase
            val cursor = db.query(
                "chat_messages",
                null,
                "session_id = ?",
                arrayOf(sessionId),
                null,
                null,
                "timestamp ASC"
            )
            val list = mutableListOf<ChatMessageEntity>()
            cursor.use {
                while (it.moveToNext()) {
                    list.add(
                        ChatMessageEntity(
                            id = it.getString(it.getColumnIndexOrThrow("id")),
                            sessionId = sessionId,
                            senderName = it.getString(it.getColumnIndexOrThrow("sender_name")),
                            text = it.getString(it.getColumnIndexOrThrow("text")),
                            timeAgo = it.getString(it.getColumnIndexOrThrow("time_ago")),
                            isUser = it.getInt(it.getColumnIndexOrThrow("is_user")) == 1,
                            paragraph2 = it.getString(it.getColumnIndexOrThrow("paragraph2")),
                            matchedSubject = it.getString(it.getColumnIndexOrThrow("matched_subject")),
                            matchedChapter = it.getString(it.getColumnIndexOrThrow("matched_chapter")),
                            matchedTopic = it.getString(it.getColumnIndexOrThrow("matched_topic")),
                            isConceptAnswer = it.getInt(it.getColumnIndexOrThrow("is_concept_answer")) == 1,
                            isSavedNoteCard = it.getInt(it.getColumnIndexOrThrow("is_saved_note_card")) == 1,
                            savedNoteTitle = it.getString(it.getColumnIndexOrThrow("saved_note_title")),
                            actionCommand = it.getString(it.getColumnIndexOrThrow("action_command")),
                            timestamp = it.getLong(it.getColumnIndexOrThrow("timestamp"))
                        )
                    )
                }
            }
            if (list.isNotEmpty()) return@withContext list
        } catch (_: Throwable) {}
        inMemoryChatMessages[sessionId].orEmpty()
    }

    override suspend fun deleteMessagesForSession(sessionId: String) = withContext(Dispatchers.IO) {
        inMemoryChatMessages.remove(sessionId)
        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                db.delete("chat_messages", "session_id = ?", arrayOf(sessionId))
            } catch (_: Throwable) {}
        }
    }

    companion object {
        private const val DATABASE_NAME = "synapse_quiz.db"
        private const val DATABASE_VERSION = 8

        @Volatile
        private var INSTANCE: QuizLocalDatabase? = null

        fun getInstance(context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }): QuizDao {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: QuizLocalDatabase(context).also { INSTANCE = it }
            }
        }

        fun getNotesDao(context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }): NotesDao {
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

        fun getChatDao(context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }): ChatDao {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: QuizLocalDatabase(context).also { INSTANCE = it }
            }
        }
    }
}
