package com.siamts.synapse.ui.lesson

/**
 * UI State for the Lesson Detail Screen ("Gradient descent").
 */
data class LessonUiState(
    val subjectTag: String = "ARTIFICIAL INTELLIGENCE",
    val lessonTitle: String = "Gradient descent",
    val lessonInfo: String = "Lesson 3 of 5 · 12 min",
    val completionPercentage: Int = 60,
    val selectedSubTab: Int = 0, // 0: Learn, 1: Notes, 2: Quiz
    val headline: String = "Small steps. Less error.",
    val description: String = "Gradient descent updates a model's weights in the direction that reduces its prediction error.",
    val keyPoints: List<String> = listOf(
        "The gradient tells you which way to move.",
        "The learning rate sets your step size."
    ),
    val exampleTitle: String = "A quick example",
    val exampleContent: String = "If w = 4, gradient = 2 and rate = 0.1,\nthen w_new = 4 - 0.1 × 2 = 3.8.",
)
