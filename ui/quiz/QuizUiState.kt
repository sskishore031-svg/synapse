package com.siamts.synapse.ui.quiz

import com.siamts.synapse.data.model.QuizQuestionDto
import com.siamts.synapse.data.model.QuizResultDataDto

data class QuizOption(
    val id: String,
    val optionLabel: String, // A, B, C, D
    val text: String,
    val isCorrect: Boolean = false,
)

/**
 * UI State for the Quiz Screen ("Test your understanding").
 */
data class QuizUiState(
    val chapterId: Int = 0,
    val topicTag: String = "SYNAPSE QUIZ",
    val title: String = "Test your understanding",
    val currentQuestionIndex: Int = 1,
    val totalQuestions: Int = 3,
    val timerText: String = "10:00",
    val difficulty: String = "Standard difficulty",
    val questionText: String = "",
    val instructionText: String = "Choose one answer.",
    val options: List<QuizOption> = emptyList(),
    val selectedOptionId: String? = null,
    val aiNoteText: String = "✨ Curriculum-aligned questions from Synapse AI",
    val questions: List<QuizQuestionDto> = emptyList(),
    val answersMap: Map<Int, String> = emptyMap(),
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val submissionResult: QuizResultDataDto? = null,
    val errorMessage: String? = null
)
