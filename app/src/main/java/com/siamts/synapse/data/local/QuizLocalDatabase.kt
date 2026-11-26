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
), QuizDao, ChapterDao, LearningTaskDao, UserDao, NotesDao, ChatDao, StudySessionDao, LessonProgressDao, NotificationDao {

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
    private val inMemoryStudySessions = CopyOnWriteArrayList<StudySessionEntity>()
    private val inMemoryLessonProgress = java.util.concurrent.ConcurrentHashMap<String, LessonProgressEntity>()
    private val inMemoryNotifications = CopyOnWriteArrayList<NotificationEntity>()
    private val progressStateFlow = kotlinx.coroutines.flow.MutableStateFlow<List<LessonProgressEntity>>(emptyList())
    private val notesStateFlow = kotlinx.coroutines.flow.MutableStateFlow<Long>(0L)
    private val notificationsStateFlow = kotlinx.coroutines.flow.MutableStateFlow<List<NotificationEntity>>(emptyList())
    private val unreadCountStateFlow = kotlinx.coroutines.flow.MutableStateFlow<Int>(0)
    private var isNotificationsLoaded = false
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
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS study_sessions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                track_name TEXT NOT NULL,
                category TEXT NOT NULL,
                duration_minutes INTEGER NOT NULL,
                study_date TEXT NOT NULL,
                timestamp INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS lesson_progress (
                lesson_id TEXT NOT NULL,
                track_id TEXT NOT NULL,
                user_id TEXT NOT NULL,
                chapter_index INTEGER NOT NULL DEFAULT 1,
                concept_done INTEGER NOT NULL DEFAULT 0,
                quiz_done INTEGER NOT NULL DEFAULT 0,
                lesson_completed INTEGER NOT NULL DEFAULT 0,
                quiz_completed INTEGER NOT NULL DEFAULT 0,
                is_completed INTEGER NOT NULL DEFAULT 0,
                completed_at INTEGER NOT NULL,
                PRIMARY KEY (user_id, track_id, chapter_index)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS notifications (
                id TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                body TEXT NOT NULL,
                timestamp INTEGER NOT NULL,
                type TEXT NOT NULL,
                is_read INTEGER NOT NULL DEFAULT 0,
                action_route TEXT,
                icon_emoji TEXT NOT NULL DEFAULT '🔔'
            )
            """.trimIndent()
        )
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        try { db.execSQL("ALTER TABLE lesson_progress ADD COLUMN chapter_index INTEGER NOT NULL DEFAULT 1") } catch (_: Throwable) {}
        try { db.execSQL("ALTER TABLE lesson_progress ADD COLUMN concept_done INTEGER NOT NULL DEFAULT 0") } catch (_: Throwable) {}
        try { db.execSQL("ALTER TABLE lesson_progress ADD COLUMN quiz_done INTEGER NOT NULL DEFAULT 0") } catch (_: Throwable) {}
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            // Version 10 migration: Recreate curriculum, questions, and in-chat quiz tables for 4 placement tracks with fresh zero-default state
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
        notesStateFlow.value = System.currentTimeMillis()

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

    override fun observeAllNotes(userId: Int): kotlinx.coroutines.flow.Flow<List<NoteEntity>> = kotlinx.coroutines.flow.flow {
        emit(getAllNotes(userId))
        notesStateFlow.collect {
            emit(getAllNotes(userId))
        }
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
        notesStateFlow.value = System.currentTimeMillis()
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

    // ==========================================
    // StudySessionDao Implementation (Zero-Default Live Tracking)
    // ==========================================

    override suspend fun insertSession(session: StudySessionEntity): Long = withContext(Dispatchers.IO) {
        if (!hasAndroidContext) {
            val newId = (inMemoryStudySessions.size + 1).toLong()
            val created = session.copy(id = newId)
            inMemoryStudySessions.add(created)
            return@withContext newId
        }
        try {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("user_id", session.userId)
                put("track_name", session.trackName)
                put("category", session.category)
                put("duration_minutes", session.durationMinutes)
                put("study_date", session.studyDate)
                put("timestamp", session.timestamp)
            }
            val id = db.insert("study_sessions", null, values)
            inMemoryStudySessions.add(session.copy(id = id))
            id
        } catch (_: Throwable) {
            val newId = (inMemoryStudySessions.size + 1).toLong()
            inMemoryStudySessions.add(session.copy(id = newId))
            newId
        }
    }

    override suspend fun getAllSessions(): List<StudySessionEntity> = withContext(Dispatchers.IO) {
        if (!hasAndroidContext) {
            return@withContext inMemoryStudySessions.toList()
        }
        val list = mutableListOf<StudySessionEntity>()
        try {
            val db = readableDatabase
            val cursor = db.query("study_sessions", null, null, null, null, null, "id DESC")
            cursor.use { c ->
                val idIdx = c.getColumnIndexOrThrow("id")
                val uIdx = c.getColumnIndexOrThrow("user_id")
                val tIdx = c.getColumnIndexOrThrow("track_name")
                val catIdx = c.getColumnIndexOrThrow("category")
                val durIdx = c.getColumnIndexOrThrow("duration_minutes")
                val dIdx = c.getColumnIndexOrThrow("study_date")
                val tsIdx = c.getColumnIndexOrThrow("timestamp")
                while (c.moveToNext()) {
                    list.add(
                        StudySessionEntity(
                            id = c.getLong(idIdx),
                            userId = c.getInt(uIdx),
                            trackName = c.getString(tIdx),
                            category = c.getString(catIdx),
                            durationMinutes = c.getInt(durIdx),
                            studyDate = c.getString(dIdx),
                            timestamp = c.getLong(tsIdx)
                        )
                    )
                }
            }
            list
        } catch (_: Throwable) {
            inMemoryStudySessions.toList()
        }
    }

    override suspend fun getSessionsByDate(date: String): List<StudySessionEntity> = withContext(Dispatchers.IO) {
        getAllSessions().filter { it.studyDate == date }
    }

    override suspend fun getSessionsBetween(startDate: String, endDate: String): List<StudySessionEntity> = withContext(Dispatchers.IO) {
        getAllSessions().filter { it.studyDate in startDate..endDate }
    }

    override suspend fun getSessionsForMonth(yearMonth: String): List<StudySessionEntity> = withContext(Dispatchers.IO) {
        getAllSessions().filter { it.studyDate.startsWith(yearMonth) }
    }

    override suspend fun getSessionsByTrack(trackName: String): List<StudySessionEntity> = withContext(Dispatchers.IO) {
        getAllSessions().filter { it.trackName.equals(trackName, ignoreCase = true) }
    }

    override suspend fun getAllActiveDates(): List<String> = withContext(Dispatchers.IO) {
        val dates = mutableSetOf<String>()
        // From study sessions
        getAllSessions().forEach { dates.add(it.studyDate) }
        // From quiz records
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        getAllRecords().forEach { dates.add(sdf.format(java.util.Date(it.completedAt))) }
        // From study notes
        getAllNotes(1).forEach { dates.add(sdf.format(java.util.Date(it.createdAt))) }
        dates.filter { it.isNotBlank() }.sorted()
    }

    override suspend fun clearSessions() = withContext(Dispatchers.IO) {
        inMemoryStudySessions.clear()
        if (hasAndroidContext) {
            try {
                writableDatabase.delete("study_sessions", null, null)
            } catch (_: Throwable) {}
        }
        Unit
    }

    // ==========================================
    // LessonProgressDao Implementation ('lesson_progress')
    // ==========================================

    override suspend fun saveProgress(progress: LessonProgressEntity) = withContext(Dispatchers.IO) {
        val normTrack = TrackMapping.normalizeTrackId(progress.trackId)
        val idx = progress.chapterIndex.coerceIn(1, 5)
        val numericId = TrackMapping.canonicalLessonNumericId(normTrack, idx).toString()
        val canonicalKey = TrackMapping.canonicalLessonKey(normTrack, idx)

        val entityToSave = progress.copy(
            trackId = normTrack,
            chapterIndex = idx,
            lessonId = if (progress.lessonId.isNotBlank()) progress.lessonId else numericId
        )

        inMemoryLessonProgress["${progress.userId}_${normTrack}_$idx"] = entityToSave
        inMemoryLessonProgress["${progress.userId}_${entityToSave.lessonId}"] = entityToSave
        inMemoryLessonProgress["${progress.userId}_$numericId"] = entityToSave
        inMemoryLessonProgress["${progress.userId}_$canonicalKey"] = entityToSave
        inMemoryLessonProgress["1_${normTrack}_$idx"] = entityToSave
        inMemoryLessonProgress["1_$numericId"] = entityToSave

        progressStateFlow.value = inMemoryLessonProgress.values.distinctBy { "${it.userId}_${it.trackId}_${it.chapterIndex}" }

        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                val values = ContentValues().apply {
                    put("lesson_id", entityToSave.lessonId)
                    put("track_id", normTrack)
                    put("user_id", progress.userId)
                    put("chapter_index", idx)
                    put("concept_done", if (entityToSave.conceptDone || entityToSave.lessonCompleted) 1 else 0)
                    put("quiz_done", if (entityToSave.quizDone || entityToSave.quizCompleted) 1 else 0)
                    put("lesson_completed", if (entityToSave.conceptDone || entityToSave.lessonCompleted) 1 else 0)
                    put("quiz_completed", if (entityToSave.quizDone || entityToSave.quizCompleted) 1 else 0)
                    put("is_completed", if (entityToSave.isCompleted || entityToSave.conceptDone || entityToSave.quizDone) 1 else 0)
                    put("completed_at", entityToSave.completedAt)
                }
                db.insertWithOnConflict("lesson_progress", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            } catch (_: Throwable) {}
        }
        Unit
    }

    override suspend fun markConceptDone(
        userId: String,
        trackId: String,
        chapterIndex: Int
    ): LessonProgressEntity = withContext(Dispatchers.IO) {
        val normTrack = TrackMapping.normalizeTrackId(trackId)
        val idx = chapterIndex.coerceIn(1, 5)
        val canonicalId = TrackMapping.canonicalLessonNumericId(normTrack, idx).toString()
        val existing = getProgress(canonicalId, userId) ?: getChapterProgress(normTrack, idx, userId)
        val updated = (existing ?: LessonProgressEntity(
            lessonId = canonicalId,
            trackId = normTrack,
            userId = userId,
            chapterIndex = idx
        )).copy(
            conceptDone = true,
            lessonCompleted = true,
            isCompleted = true,
            completedAt = System.currentTimeMillis()
        )
        saveProgress(updated)

        // Backwards compatibility with ChapterDao
        markChapterCompleted(canonicalId.toIntOrNull() ?: 1)
        updated
    }

    override suspend fun markQuizDone(
        userId: String,
        trackId: String,
        chapterIndex: Int
    ): LessonProgressEntity = withContext(Dispatchers.IO) {
        val normTrack = TrackMapping.normalizeTrackId(trackId)
        val idx = chapterIndex.coerceIn(1, 5)
        val canonicalId = TrackMapping.canonicalLessonNumericId(normTrack, idx).toString()
        val existing = getProgress(canonicalId, userId) ?: getChapterProgress(normTrack, idx, userId)
        val updated = (existing ?: LessonProgressEntity(
            lessonId = canonicalId,
            trackId = normTrack,
            userId = userId,
            chapterIndex = idx
        )).copy(
            quizDone = true,
            quizCompleted = true,
            isCompleted = true,
            completedAt = System.currentTimeMillis()
        )
        saveProgress(updated)

        // Backwards compatibility with ChapterDao
        markChapterCompleted(canonicalId.toIntOrNull() ?: 1)
        updated
    }

    override suspend fun markLessonCompleted(
        lessonId: String,
        trackId: String,
        userId: String
    ): LessonProgressEntity = withContext(Dispatchers.IO) {
        val normTrack = TrackMapping.normalizeTrackId(trackId)
        val idx = TrackMapping.chapterIndex(lessonId, normTrack)
        markConceptDone(userId, normTrack, idx)
    }

    override suspend fun markQuizCompleted(
        lessonId: String,
        trackId: String,
        userId: String
    ): LessonProgressEntity = withContext(Dispatchers.IO) {
        val normTrack = TrackMapping.normalizeTrackId(trackId)
        val idx = TrackMapping.chapterIndex(lessonId, normTrack)
        markQuizDone(userId, normTrack, idx)
    }

    override fun getProgressCached(lessonId: String, userId: String): LessonProgressEntity? {
        val direct = inMemoryLessonProgress["${userId}_$lessonId"]
        if (direct != null) return direct
        val normTrack = TrackMapping.getTrackForChapter(lessonId)
        val idx = TrackMapping.chapterIndex(lessonId, normTrack)
        return inMemoryLessonProgress["${userId}_${normTrack}_$idx"]
            ?: inMemoryLessonProgress["1_${normTrack}_$idx"]
            ?: inMemoryLessonProgress["1_$lessonId"]
    }

    override suspend fun getChapterProgress(
        trackId: String,
        chapterIndex: Int,
        userId: String
    ): LessonProgressEntity? = withContext(Dispatchers.IO) {
        val normTrack = TrackMapping.normalizeTrackId(trackId)
        val idx = chapterIndex.coerceIn(1, 5)
        val key = "${userId}_${normTrack}_$idx"
        inMemoryLessonProgress[key]?.let { return@withContext it }
        val numericId = TrackMapping.canonicalLessonNumericId(normTrack, idx).toString()
        getProgress(numericId, userId)
    }

    override fun observeChapterProgress(
        trackId: String,
        chapterIndex: Int,
        userId: String
    ): kotlinx.coroutines.flow.Flow<LessonProgressEntity?> {
        val normTrack = TrackMapping.normalizeTrackId(trackId)
        val idx = chapterIndex.coerceIn(1, 5)
        return kotlinx.coroutines.flow.flow {
            val initial = getChapterProgress(normTrack, idx, userId) ?: getChapterProgress(normTrack, idx, "1")
            emit(initial)
            progressStateFlow.collect { list ->
                val found = list.firstOrNull {
                    (it.userId == userId || it.userId == "1") &&
                    TrackMapping.normalizeTrackId(it.trackId) == normTrack &&
                    it.chapterIndex == idx
                }
                emit(found)
            }
        }
    }

    override suspend fun resetChapterProgress(
        trackId: String,
        chapterIndex: Int,
        userId: String
    ): LessonProgressEntity = withContext(Dispatchers.IO) {
        val normTrack = TrackMapping.normalizeTrackId(trackId)
        val idx = chapterIndex.coerceIn(1, 5)
        val numericId = TrackMapping.canonicalLessonNumericId(normTrack, idx).toString()
        val resetEntity = LessonProgressEntity(
            lessonId = numericId,
            trackId = normTrack,
            userId = userId,
            chapterIndex = idx,
            conceptDone = false,
            quizDone = false,
            lessonCompleted = false,
            quizCompleted = false,
            isCompleted = false,
            completedAt = System.currentTimeMillis()
        )

        val keysToRemove = listOf(
            "${userId}_${normTrack}_$idx",
            "${userId}_$numericId",
            "${userId}_${TrackMapping.canonicalLessonKey(normTrack, idx)}",
            "1_${normTrack}_$idx",
            "1_$numericId"
        )
        keysToRemove.forEach { inMemoryLessonProgress.remove(it) }

        progressStateFlow.value = inMemoryLessonProgress.values.distinctBy { "${it.userId}_${it.trackId}_${it.chapterIndex}" }

        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                db.delete("lesson_progress", "track_id = ? AND (lesson_id = ? OR chapter_index = ?) AND (user_id = ? OR user_id = '1')", arrayOf(normTrack, numericId, idx.toString(), userId))
            } catch (_: Throwable) {}
        }
        resetEntity
    }

    override suspend fun getProgress(
        lessonId: String,
        userId: String
    ): LessonProgressEntity? = withContext(Dispatchers.IO) {
        val cached = getProgressCached(lessonId, userId)
        if (cached != null) return@withContext cached

        val normTrack = TrackMapping.getTrackForChapter(lessonId)
        val idx = TrackMapping.chapterIndex(lessonId, normTrack)
        val numericId = TrackMapping.canonicalLessonNumericId(normTrack, idx).toString()

        if (hasAndroidContext) {
            try {
                val db = readableDatabase
                val cursor = db.query(
                    "lesson_progress",
                    null,
                    "(lesson_id = ? OR lesson_id = ?) AND (user_id = ? OR user_id = '1')",
                    arrayOf(lessonId, numericId, userId),
                    null, null, "completed_at DESC"
                )
                cursor.use { c ->
                    if (c.moveToFirst()) {
                        val lcIdx = c.getColumnIndex("lesson_completed")
                        val qcIdx = c.getColumnIndex("quiz_completed")
                        val cdIdx = c.getColumnIndex("concept_done")
                        val qdIdx = c.getColumnIndex("quiz_done")
                        val lComp = (lcIdx >= 0 && c.getInt(lcIdx) == 1) || (cdIdx >= 0 && c.getInt(cdIdx) == 1)
                        val qComp = (qcIdx >= 0 && c.getInt(qcIdx) == 1) || (qdIdx >= 0 && c.getInt(qdIdx) == 1)
                        val icIdx = c.getColumnIndex("is_completed")
                        val isComp = (icIdx >= 0 && c.getInt(icIdx) == 1) || lComp || qComp
                        val caIdx = c.getColumnIndex("completed_at")
                        val entity = LessonProgressEntity(
                            lessonId = c.getString(c.getColumnIndexOrThrow("lesson_id")),
                            trackId = c.getString(c.getColumnIndexOrThrow("track_id")),
                            userId = c.getString(c.getColumnIndexOrThrow("user_id")),
                            chapterIndex = idx,
                            conceptDone = lComp,
                            quizDone = qComp,
                            lessonCompleted = lComp,
                            quizCompleted = qComp,
                            isCompleted = isComp,
                            completedAt = if (caIdx >= 0) c.getLong(caIdx) else System.currentTimeMillis()
                        )
                        saveProgress(entity)
                        return@withContext entity
                    }
                }
            } catch (_: Throwable) {}
        }
        null
    }

    override suspend fun getProgressForUser(
        userId: String
    ): List<LessonProgressEntity> = withContext(Dispatchers.IO) {
        val memList = inMemoryLessonProgress.values.filter { (it.userId == userId || it.userId == "1") && (it.isCompleted || it.conceptDone || it.quizDone) }.distinctBy { "${it.trackId}_${it.chapterIndex}" }
        if (!hasAndroidContext || memList.isNotEmpty()) {
            return@withContext memList
        }
        val list = mutableListOf<LessonProgressEntity>()
        try {
            val db = readableDatabase
            val cursor = db.query(
                "lesson_progress",
                null,
                "(user_id = ? OR user_id = '1') AND (is_completed = 1 OR concept_done = 1 OR quiz_done = 1)",
                arrayOf(userId),
                null, null, "completed_at ASC"
            )
            cursor.use { c ->
                val lid = c.getColumnIndexOrThrow("lesson_id")
                val tid = c.getColumnIndexOrThrow("track_id")
                val uid = c.getColumnIndexOrThrow("user_id")
                val lcIdx = c.getColumnIndex("lesson_completed")
                val qcIdx = c.getColumnIndex("quiz_completed")
                val cdIdx = c.getColumnIndex("concept_done")
                val qdIdx = c.getColumnIndex("quiz_done")
                val icIdx = c.getColumnIndex("is_completed")
                val ca = c.getColumnIndexOrThrow("completed_at")
                while (c.moveToNext()) {
                    val lComp = (lcIdx >= 0 && c.getInt(lcIdx) == 1) || (cdIdx >= 0 && c.getInt(cdIdx) == 1)
                    val qComp = (qcIdx >= 0 && c.getInt(qcIdx) == 1) || (qdIdx >= 0 && c.getInt(qdIdx) == 1)
                    val rawTrack = c.getString(tid)
                    val rawLesson = c.getString(lid)
                    val idx = TrackMapping.chapterIndex(rawLesson, rawTrack)
                    val entity = LessonProgressEntity(
                        lessonId = rawLesson,
                        trackId = TrackMapping.normalizeTrackId(rawTrack),
                        userId = c.getString(uid),
                        chapterIndex = idx,
                        conceptDone = lComp,
                        quizDone = qComp,
                        lessonCompleted = lComp,
                        quizCompleted = qComp,
                        isCompleted = (icIdx >= 0 && c.getInt(icIdx) == 1) || lComp || qComp,
                        completedAt = c.getLong(ca)
                    )
                    list.add(entity)
                    saveProgress(entity)
                }
            }
            if (list.isNotEmpty()) {
                progressStateFlow.value = inMemoryLessonProgress.values.distinctBy { "${it.userId}_${it.trackId}_${it.chapterIndex}" }
            }
        } catch (_: Throwable) {}
        list.distinctBy { "${it.trackId}_${it.chapterIndex}" }
    }

    override fun observeProgressForUser(
        userId: String
    ): kotlinx.coroutines.flow.Flow<List<LessonProgressEntity>> {
        return kotlinx.coroutines.flow.flow {
            val initial = (getProgressForUser(userId) + getProgressForUser("1")).distinctBy { "${it.trackId}_${it.chapterIndex}" }
            emit(initial)
            progressStateFlow.collect { list ->
                val filtered = list.filter { (it.userId == userId || it.userId == "1") && (it.isCompleted || it.conceptDone || it.quizDone) }.distinctBy { "${it.trackId}_${it.chapterIndex}" }
                emit(filtered)
            }
        }
    }

    override suspend fun getCompletedLessonsForTrack(
        trackId: String,
        userId: String
    ): List<LessonProgressEntity> = withContext(Dispatchers.IO) {
        val norm = TrackMapping.normalizeTrackId(trackId)
        getProgressForUser(userId).filter { TrackMapping.normalizeTrackId(it.trackId) == norm && it.isCompleted }
    }

    override suspend fun resetTrackProgress(
        trackId: String,
        userId: String
    ) = withContext(Dispatchers.IO) {
        val norm = TrackMapping.normalizeTrackId(trackId)
        val keysToRemove = inMemoryLessonProgress.filter { 
            (it.value.userId == userId || it.value.userId == "1") && TrackMapping.normalizeTrackId(it.value.trackId) == norm 
        }.keys
        keysToRemove.forEach { inMemoryLessonProgress.remove(it) }
        progressStateFlow.value = inMemoryLessonProgress.values.distinctBy { "${it.userId}_${it.trackId}_${it.chapterIndex}" }

        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                db.delete("lesson_progress", "track_id = ? AND (user_id = ? OR user_id = '1')", arrayOf(norm, userId))
            } catch (_: Throwable) {}
        }
        Unit
    }

    override suspend fun clearAllProgress() = withContext(Dispatchers.IO) {
        inMemoryLessonProgress.clear()
        progressStateFlow.value = emptyList()
        synchronized(inMemoryChapters) {
            for (i in inMemoryChapters.indices) {
                inMemoryChapters[i] = inMemoryChapters[i].copy(isAttempted = false)
            }
        }
        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                db.delete("lesson_progress", null, null)
                db.execSQL("UPDATE subject_chapters SET is_attempted = 0")
            } catch (_: Throwable) {}
        }
        Unit
    }

    private fun loadNotificationsFromDbIfNeeded() {
        if (isNotificationsLoaded || !hasAndroidContext) return
        try {
            val db = readableDatabase
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS notifications (
                    id TEXT PRIMARY KEY,
                    title TEXT NOT NULL,
                    body TEXT NOT NULL,
                    timestamp INTEGER NOT NULL,
                    type TEXT NOT NULL,
                    is_read INTEGER NOT NULL DEFAULT 0,
                    action_route TEXT,
                    icon_emoji TEXT NOT NULL DEFAULT '🔔'
                )
                """.trimIndent()
            )
            val cursor = db.rawQuery(
                "SELECT id, title, body, timestamp, type, is_read, action_route, icon_emoji FROM notifications ORDER BY timestamp DESC",
                null
            )
            val list = mutableListOf<NotificationEntity>()
            cursor.use { c ->
                while (c.moveToNext()) {
                    list.add(
                        NotificationEntity(
                            id = c.getString(0),
                            title = c.getString(1),
                            body = c.getString(2),
                            timestamp = c.getLong(3),
                            type = c.getString(4),
                            isRead = c.getInt(5) == 1,
                            actionRoute = if (c.isNull(6)) null else c.getString(6),
                            iconEmoji = if (c.isNull(7)) "🔔" else c.getString(7)
                        )
                    )
                }
            }
            inMemoryNotifications.clear()
            inMemoryNotifications.addAll(list)
            notificationsStateFlow.value = list
            unreadCountStateFlow.value = list.count { !it.isRead }
            isNotificationsLoaded = true
        } catch (_: Throwable) {}
    }

    override suspend fun insertNotification(notification: NotificationEntity): Long = withContext(Dispatchers.IO) {
        loadNotificationsFromDbIfNeeded()
        inMemoryNotifications.removeIf { it.id == notification.id }
        inMemoryNotifications.add(0, notification)
        notificationsStateFlow.value = inMemoryNotifications.toList()
        unreadCountStateFlow.value = inMemoryNotifications.count { !it.isRead }

        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                val values = ContentValues().apply {
                    put("id", notification.id)
                    put("title", notification.title)
                    put("body", notification.body)
                    put("timestamp", notification.timestamp)
                    put("type", notification.type)
                    put("is_read", if (notification.isRead) 1 else 0)
                    put("action_route", notification.actionRoute)
                    put("icon_emoji", notification.iconEmoji)
                }
                db.insertWithOnConflict("notifications", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            } catch (_: Throwable) {
                1L
            }
        } else {
            1L
        }
    }

    override fun getAllNotificationsFlow(): kotlinx.coroutines.flow.Flow<List<NotificationEntity>> {
        loadNotificationsFromDbIfNeeded()
        return notificationsStateFlow
    }

    override suspend fun getAllNotifications(): List<NotificationEntity> = withContext(Dispatchers.IO) {
        loadNotificationsFromDbIfNeeded()
        inMemoryNotifications.toList()
    }

    override suspend fun markAsRead(id: String) = withContext(Dispatchers.IO) {
        loadNotificationsFromDbIfNeeded()
        val index = inMemoryNotifications.indexOfFirst { it.id == id }
        if (index != -1) {
            val updated = inMemoryNotifications[index].copy(isRead = true)
            inMemoryNotifications[index] = updated
            notificationsStateFlow.value = inMemoryNotifications.toList()
            unreadCountStateFlow.value = inMemoryNotifications.count { !it.isRead }
        }
        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                val values = ContentValues().apply { put("is_read", 1) }
                db.update("notifications", values, "id = ?", arrayOf(id))
            } catch (_: Throwable) {}
        }
        Unit
    }

    override suspend fun markAllAsRead() = withContext(Dispatchers.IO) {
        loadNotificationsFromDbIfNeeded()
        for (i in inMemoryNotifications.indices) {
            inMemoryNotifications[i] = inMemoryNotifications[i].copy(isRead = true)
        }
        notificationsStateFlow.value = inMemoryNotifications.toList()
        unreadCountStateFlow.value = 0
        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                val values = ContentValues().apply { put("is_read", 1) }
                db.update("notifications", values, null, null)
            } catch (_: Throwable) {}
        }
        Unit
    }

    override suspend fun getUnreadCount(): Int = withContext(Dispatchers.IO) {
        loadNotificationsFromDbIfNeeded()
        inMemoryNotifications.count { !it.isRead }
    }

    override fun getUnreadCountFlow(): kotlinx.coroutines.flow.Flow<Int> {
        loadNotificationsFromDbIfNeeded()
        return unreadCountStateFlow
    }

    override suspend fun deleteNotification(id: String) = withContext(Dispatchers.IO) {
        loadNotificationsFromDbIfNeeded()
        inMemoryNotifications.removeIf { it.id == id }
        notificationsStateFlow.value = inMemoryNotifications.toList()
        unreadCountStateFlow.value = inMemoryNotifications.count { !it.isRead }
        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                db.delete("notifications", "id = ?", arrayOf(id))
            } catch (_: Throwable) {}
        }
        Unit
    }

    override suspend fun clearAll() = withContext(Dispatchers.IO) {
        inMemoryNotifications.clear()
        notificationsStateFlow.value = emptyList()
        unreadCountStateFlow.value = 0
        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                db.delete("notifications", null, null)
            } catch (_: Throwable) {}
        }
        Unit
    }

    companion object {
        private const val DATABASE_NAME = "synapse_quiz.db"
        private const val DATABASE_VERSION = 10

        @Volatile
        private var INSTANCE: QuizLocalDatabase? = null

        fun getStudySessionDao(context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }): StudySessionDao {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: QuizLocalDatabase(context).also { INSTANCE = it }
            }
        }

        fun getLessonProgressDao(context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }): LessonProgressDao {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: QuizLocalDatabase(context).also { INSTANCE = it }
            }
        }

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

        fun getNotificationDao(context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }): NotificationDao {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: QuizLocalDatabase(context).also { INSTANCE = it }
            }
        }
    }
}
