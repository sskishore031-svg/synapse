package com.siamts.synapse.ui.lesson

data class ChapterLessonUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val chapterId: Int = 0,
    val chapterTitle: String = "",
    val subjectName: String = "",
    val subjectSlug: String = "",
    val chapterOrder: Int = 1,
    val durationMinutes: Int = 15,
    val isCompleted: Boolean = false,
    val currentPartIndex: Int = 0, // 0 for Part 1, 1 for Part 2
    val part1Title: String = "",
    val part1Content: String = "",
    val part2Title: String = "",
    val part2Content: String = "",
    val previousChapterId: Int? = null,
    val nextChapterId: Int? = null,
    val isMarkingComplete: Boolean = false,
    val completionSuccess: Boolean = false
)

