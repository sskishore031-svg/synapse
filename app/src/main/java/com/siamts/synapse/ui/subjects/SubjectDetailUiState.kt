package com.siamts.synapse.ui.subjects

import androidx.compose.ui.graphics.Color

data class SubjectChapterDetailItem(
    val chapterId: Int,
    val order: Int,
    val title: String,
    val summary: String,
    val estimatedMinutes: Int = 15,
    val questionCount: Int = 5,
    val isCompleted: Boolean = false,
    val isUnlocked: Boolean = true,
)

data class SubjectDetailUiState(
    val subjectId: String = "",
    val numericId: Int = 1,
    val title: String = "Mathematics",
    val slug: String = "mathematics",
    val category: String = "Formal Sciences",
    val iconSymbol: String = "∑",
    val colorHex: String = "#3B82F6",
    val color: Color = Color(0xFF3B82F6),
    val overview: String = "Fundamental study of quantity, structure, space, algebra, and calculus.",
    val progressPercentage: Int = 0,
    val completedChapters: Int = 0,
    val totalChapters: Int = 4,
    val chapters: List<SubjectChapterDetailItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

