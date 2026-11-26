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
        }
    }
}

