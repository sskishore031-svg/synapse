package com.siamts.synapse.ui.quiz

import com.siamts.synapse.data.model.QuizSubjectItemDto

data class QuizHomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val subjects: List<QuizSubjectItemDto> = emptyList(),
    val filteredSubjects: List<QuizSubjectItemDto> = emptyList(),
    val selectedClass: String = "All Tracks",
    val availableClasses: List<String> = listOf("All Tracks", "Python & Coding", "SQL & Database", "AI & Tech", "Aptitude & Prep"),
    val totalQuizzes: Int = 30,
    val totalAttempted: Int = 0,
    val mistakeCount: Int = 0
)

