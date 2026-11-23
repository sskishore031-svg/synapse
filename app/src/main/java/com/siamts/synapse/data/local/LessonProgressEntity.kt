package com.siamts.synapse.data.local

/**
 * Canonical Room / SQLite Entity for tracking individual chapter & quiz completion
 * across the 4 Placement Tracks.
 */
data class LessonProgressEntity(
    val lessonId: String,
    val trackId: String,
    val userId: String = "1",
    val lessonCompleted: Boolean = false,
    val quizCompleted: Boolean = false,
    val isCompleted: Boolean = false,
    val completedAt: Long = System.currentTimeMillis()
)
