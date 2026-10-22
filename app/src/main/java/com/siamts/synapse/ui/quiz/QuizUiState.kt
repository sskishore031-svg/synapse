package com.siamts.synapse.ui.quiz

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
    val topicTag: String = "GRADIENT DESCENT",
    val title: String = "Test your understanding",
    val currentQuestionIndex: Int = 4,
    val totalQuestions: Int = 10,
    val timerText: String = "00:42",
    val difficulty: String = "Medium difficulty",
    val questionText: String = "What is the main goal of gradient descent?",
    val instructionText: String = "Choose one answer.",
    val options: List<QuizOption> = listOf(
        QuizOption("A", "A", "Increase model complexity"),
        QuizOption("B", "B", "Reduce prediction error", true),
        QuizOption("C", "C", "Remove all training data"),
        QuizOption("D", "D", "Make every weight equal"),
    ),
    val selectedOptionId: String? = "B",
    val aiNoteText: String = "✨ AI-generated quiz · Adapted to your level",
)
