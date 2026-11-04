package com.siamts.synapse.data.local

/**
 * Entity representing a quiz attempt saved in Room / local SQLite 'quiz_records' table.
 */
data class QuizRecordEntity(
    val id: Long = 0,
    val subjectId: String = "",
    val chapterId: String = "",
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val completedAt: Long = System.currentTimeMillis()
) {
    val percentage: Float
        get() = if (totalQuestions > 0) (score.toFloat() / totalQuestions) * 100f else 0f
}
