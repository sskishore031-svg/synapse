package com.siamts.synapse.ui.results

/**
 * UI State for the Quiz Completed / Results Screen ("Learning, leveled up").
 */
data class QuizResultsUiState(
    val headerTag: String = "GRADIENT DESCENT · RESULTS",
    val title: String = "Learning, leveled up",
    val headline: String = "Quiz Completed!",
    val subHeadline: String = "You're building real momentum, Arjun.",
    val scorePercentage: Int = 80,
    val resultTitle: String = "Nicely done!",
    val resultMessage: String = "A strong step toward mastering AI.",
    val xpEarned: Int = 120,
    val correctAnswers: Int = 8,
    val incorrectAnswers: Int = 2,
    val accuracy: String = "80%",
    val timeTaken: String = "04:32",
    val feedbackTitle: String = "A little more practice",
    val feedbackText: String = "Weak topics: learning rate selection and local minima.",
)
