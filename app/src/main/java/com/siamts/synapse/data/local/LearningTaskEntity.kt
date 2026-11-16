package com.siamts.synapse.data.local

/**
 * Entity representing a user-created daily learning plan task stored in SQLite 'learning_plan_tasks' table.
 */
data class LearningTaskEntity(
    val id: Long = 0,
    val title: String,
    val category: String = "Lesson", // e.g. Lesson, Quiz, Revision
    val duration: String = "15 min",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

