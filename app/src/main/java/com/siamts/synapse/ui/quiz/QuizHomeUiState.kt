package com.siamts.synapse.ui.quiz

import com.siamts.synapse.data.model.QuizSubjectItemDto

data class QuizHomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val subjects: List<QuizSubjectItemDto> = emptyList(),
    val totalQuizzes: Int = 30,
    val totalAttempted: Int = 0
)

