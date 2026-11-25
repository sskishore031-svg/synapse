package com.siamts.synapse.data.local

/**
 * Canonical Room / SQLite Entity for tracking individual chapter & quiz completion
 * across the Placement Tracks.
 *
 * Supports Two-Stage Chapter & Subject Completion Percentage:
 * - conceptDone: +10% (0.5 chapter credit) immediately when the user taps "Mark as Done" in chapter reader
 * - quizDone: +10% (0.5 chapter credit) immediately when the user finishes that chapter's quiz
 * - isCompleted: true when both stages or at least one stage is active
 */
data class LessonProgressEntity(
    val lessonId: String,
    val trackId: String,
    val userId: String = "1",
    val conceptDone: Boolean = false,
    val quizDone: Boolean = false,
    val lessonCompleted: Boolean = conceptDone,
    val quizCompleted: Boolean = quizDone,
    val isCompleted: Boolean = (conceptDone || quizDone || lessonCompleted || quizCompleted),
    val completedAt: Long = System.currentTimeMillis()
)
