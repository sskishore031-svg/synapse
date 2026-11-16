package com.siamts.synapse.data.local

/**
 * Local Room / SQLite Entity representing a subject chapter in SYNAPSE AI.
 */
data class ChapterEntity(
    val id: String,
    val subjectId: String,
    val chapterOrder: Int,
    val title: String,
    val summary: String = "",
    val questionCount: Int = 5,
    val estimatedMinutes: Int = 10,
    val totalMarks: Int = 50,
    val isAttempted: Boolean = false,
    val bestScore: Int? = null
)

