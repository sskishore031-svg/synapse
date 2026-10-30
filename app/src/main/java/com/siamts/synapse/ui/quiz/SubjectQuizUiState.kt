package com.siamts.synapse.ui.quiz

import com.siamts.synapse.data.model.HomeSubjectDto
import com.siamts.synapse.data.model.SubjectQuizItemDto

data class SubjectQuizUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val subjectId: Int = 0,
    val subject: HomeSubjectDto? = null,
    val quizzes: List<SubjectQuizItemDto> = emptyList()
)

