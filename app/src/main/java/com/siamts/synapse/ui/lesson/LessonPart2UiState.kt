package com.siamts.synapse.ui.lesson

data class StepValue(
    val weight: String,
    val loss: String,
    val isCurrent: Boolean = false,
)

/**
 * UI State for Lesson Part 2 ("The Learning Rate").
 */
data class LessonPart2UiState(
    val subjectTag: String = "ARTIFICIAL INTELLIGENCE",
    val lessonTitle: String = "Gradient descent",
    val lessonInfo: String = "Lesson 3 of 5 · 12 min",
    val completionPercentage: Int = 68,
    val selectedSubTab: Int = 0,
    val partTag: String = "PART 2 · THE LEARNING RATE",
    val headline: String = "Choose the right step size.",
    val description: String = "A small rate learns slowly. A large rate can overshoot the minimum and increase loss.",
    val formulaHeader: String = "Another step · J(w) = ½(w - 2)² · rate = 0.1",
    val formulaCalculation: String = "w_new = 3.8 - 0.1 × 1.8 = 3.62",
    val steps: List<StepValue> = listOf(
        StepValue("4.00", "Loss 2.00"),
        StepValue("3.80", "Loss 1.62"),
        StepValue("3.62", "Loss 1.31", true),
    ),
    val formulaFooter: String = "Gradient = w - 2; each update lowers loss.",
    val takeawayTitle: String = "Key takeaway",
    val takeawayContent: String = "Recompute the gradient each step. If loss oscillates, try a smaller rate.",
)
