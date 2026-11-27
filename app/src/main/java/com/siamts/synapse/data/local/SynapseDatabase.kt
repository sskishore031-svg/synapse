package com.siamts.synapse.data.local

import android.content.Context
import com.siamts.synapse.SynapseApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Primary database controller and curriculum seeder for SYNAPSE AI.
 * Wraps Room / SQLite storage with fallbackToDestructiveMigration semantics,
 * managing the 'quiz_questions', 'subject_chapters', 'chapter_lessons', and 'quiz_records' tables.
 */
object SynapseDatabase {
    const val DATABASE_NAME = "synapse_quiz.db"
    const val DATABASE_VERSION = 10

    @Volatile
    private var isSeedingStarted = false
    private val seederScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun getInstance(context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }): QuizDao {
        return QuizLocalDatabase.getInstance(context)
    }

    fun getChapterDao(context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }): ChapterDao {
        return QuizLocalDatabase.getChapterDao(context)
    }

    fun getLearningTaskDao(context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }): LearningTaskDao {
        return QuizLocalDatabase.getLearningTaskDao(context)
    }

    fun getUserDao(context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }): UserDao {
        return QuizLocalDatabase.getUserDao(context)
    }

    fun getNotesDao(context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }): NotesDao {
        return QuizLocalDatabase.getNotesDao(context)
    }

    fun getChatDao(context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }): ChatDao {
        return QuizLocalDatabase.getChatDao(context)
    }

    fun getStudySessionDao(context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }): StudySessionDao {
        return QuizLocalDatabase.getStudySessionDao(context)
    }

    fun getLessonProgressDao(context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }): LessonProgressDao {
        return QuizLocalDatabase.getLessonProgressDao(context)
    }

    fun getNotificationDao(context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }): NotificationDao {
        return QuizLocalDatabase.getNotificationDao(context)
    }

    /**
     * Asynchronously syncs completed chapter/quiz progress to the PHP/MySQL backend
     * without blocking UI execution.
     */
    fun syncProgressToRemote(
        trackId: String,
        lessonId: String,
        lessonCompleted: Boolean = true,
        quizCompleted: Boolean = false,
        userId: Int = 1,
        reset: Boolean = false,
        chapterIndex: Int = TrackMapping.chapterIndex(lessonId, trackId),
        conceptDone: Boolean = lessonCompleted,
        quizDone: Boolean = quizCompleted
    ) {
        seederScope.launch {
            try {
                val req = com.siamts.synapse.data.remote.SyncProgressRequest(
                    userId = userId,
                    trackId = trackId,
                    lessonId = lessonId,
                    chapterIndex = chapterIndex,
                    conceptDone = if (reset) false else (conceptDone || lessonCompleted),
                    quizDone = if (reset) false else (quizDone || quizCompleted),
                    lessonCompleted = if (reset) false else (conceptDone || lessonCompleted),
                    quizCompleted = if (reset) false else (quizDone || quizCompleted),
                    isCompleted = if (reset) false else (conceptDone || quizDone || lessonCompleted || quizCompleted),
                    reset = reset
                )
                try {
                    com.siamts.synapse.data.remote.RetrofitClient.progressApi.syncProgress(req)
                } catch (_: Throwable) {
                    com.siamts.synapse.data.remote.RetrofitClient.progressApi.syncProgressAuth(req)
                }
            } catch (_: Throwable) {}
        }
    }

    /**
     * Helper to log real-time user study activity asynchronously into local SQLite/Room
     * whenever lessons are read, quizzes taken, or notes crafted.
     */
    fun logSession(
        trackName: String,
        category: String,
        durationMinutes: Int = 15,
        context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }
    ) {
        seederScope.launch {
            try {
                val dao = getStudySessionDao(context)
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                val today = sdf.format(java.util.Date())
                val session = StudySessionEntity(
                    userId = 1,
                    trackName = trackName,
                    category = category,
                    durationMinutes = durationMinutes,
                    studyDate = today,
                    timestamp = System.currentTimeMillis()
                )
                dao.insertSession(session)
            } catch (_: Throwable) {}
        }
    }

    /**
     * Backfills completed quiz attempts from local SQLite quiz_records into LessonProgressDao
     * so that existing quizzes taken in Quiz Hub (or any screen) immediately mark quizDone = true
     * (+10% per completed chapter) across LearnScreen and HomeScreen.
     */
    suspend fun backfillQuizProgressFromQuizRecords(
        userId: String = "1",
        context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }
    ) = kotlinx.coroutines.withContext(Dispatchers.IO) {
        val quizDao = getInstance(context)
        val progressDao = getLessonProgressDao(context)
        val allRecords = try { quizDao.getAllRecords() } catch (_: Throwable) { emptyList() }
        if (allRecords.isEmpty()) return@withContext

        // Group records by canonical trackId (python, sql, ai, aptitude, dsa, java_oops, os_cn, android_dev)
        val recordsByTrack = allRecords.groupBy { rec ->
            TrackMapping.normalizeTrackId(rec.subjectId.ifBlank { rec.chapterId })
        }

        recordsByTrack.forEach { (trackId, records) ->
            if (trackId !in TrackMapping.ALL_TRACK_IDS) return@forEach

            // 1. First credit quizzes that targeted specific chapters
            val creditedChapters = mutableSetOf<Int>()
            records.forEach { rec ->
                val chapNum = rec.chapterId.toIntOrNull()
                val isSpecific = (chapNum != null && chapNum in 10101..10805) ||
                        (chapNum != null && chapNum in 1..5) ||
                        rec.chapterId.contains("chapter", ignoreCase = true)
                if (isSpecific && rec.chapterId != "0" && rec.chapterId != "9999") {
                    val idx = TrackMapping.chapterIndex(rec.chapterId, trackId).coerceIn(1, 5)
                    creditedChapters.add(idx)
                    try {
                        progressDao.markQuizDone(userId = userId, trackId = trackId, chapterIndex = idx)
                        if (userId != "1") {
                            progressDao.markQuizDone(userId = "1", trackId = trackId, chapterIndex = idx)
                        }
                    } catch (_: Throwable) {}
                }
            }

            // 2. Remaining records (e.g. from Quiz Hub general subject quizzes)
            // Each completed quiz awards a quizDone (+10%) to the next uncompleted chapter (1..5)
            val totalQuizzesForTrack = records.size
            var remainingGeneralQuizzes = (totalQuizzesForTrack - creditedChapters.size).coerceAtLeast(0)

            for (idx in 1..5) {
                if (remainingGeneralQuizzes <= 0) break
                val existing = progressDao.getChapterProgress(trackId, idx, userId)
                    ?: progressDao.getChapterProgress(trackId, idx, "1")
                if (existing?.quizDone != true) {
                    try {
                        progressDao.markQuizDone(userId = userId, trackId = trackId, chapterIndex = idx)
                        if (userId != "1") {
                            progressDao.markQuizDone(userId = "1", trackId = trackId, chapterIndex = idx)
                        }
                    } catch (_: Throwable) {}
                    creditedChapters.add(idx)
                    remainingGeneralQuizzes--
                }
            }

            // Guarantee that if totalQuizzesForTrack >= N, at least min(totalQuizzesForTrack, 5) chapters are credited
            val minChaptersToCredit = totalQuizzesForTrack.coerceAtMost(5)
            for (idx in 1..minChaptersToCredit) {
                try {
                    progressDao.markQuizDone(userId = userId, trackId = trackId, chapterIndex = idx)
                    if (userId != "1") {
                        progressDao.markQuizDone(userId = "1", trackId = trackId, chapterIndex = idx)
                    }
                } catch (_: Throwable) {}
            }
        }
    }

    /**
     * Runs a fast background seeder on Dispatchers.IO that populates all subjects,
     * 8+ chapters per subject, lesson contents, and chapter-linked quizzes on startup
     * so there is zero lag and zero empty screens.
     */
    fun seedDatabase(context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }) {
        if (isSeedingStarted) return
        synchronized(this) {
            if (isSeedingStarted) return
            isSeedingStarted = true
        }

        seederScope.launch {
            try {
                UniversityCurriculumGenerator.seedAllCurriculum(context)
            } catch (_: Throwable) {}
            try {
                val user = SessionManager.getInstanceOrNull()?.getSessionUser()
                val uid = (user?.id ?: 1).toString()
                backfillQuizProgressFromQuizRecords(uid, context)
                if (uid != "1") {
                    backfillQuizProgressFromQuizRecords("1", context)
                }
            } catch (_: Throwable) {}
        }
    }
}

