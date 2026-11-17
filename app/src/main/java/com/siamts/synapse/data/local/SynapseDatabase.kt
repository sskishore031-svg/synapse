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
    const val DATABASE_VERSION = 5

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

