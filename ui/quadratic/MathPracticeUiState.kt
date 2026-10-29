package com.siamts.synapse.ui.quadratic

data class PracticeQuestionItem(
    val id: String,
    val expression: String,
    val hintText: String,
)

/**
 * UI State for Mathematics Practice Questions Screen.
 */
data class MathPracticeUiState(
    val headerTag: String = "A PATH THAT EVOLVES WITH YOU",
    val title: String = "Mathematics",
    val subtitleTag: String = "Practice Questions",
    val progressPercent: Int = 88,
    val progressSubtext: String = "4 of 5 lessons completed · Beginner path",
    val selectedSubTab: Int = 2, // Learn=0, Notes=1, Quiz=2
    val headline: String = "Practice Questions",
    val conceptText: String = "Solve the following quadratic equations by factoring.",
    val questions: List<PracticeQuestionItem> = listOf(
        PracticeQuestionItem("1", "x² - 7x + 12 = 0", "Numbers that multiply to 12 and add to -7: -3 and -4"),
        PracticeQuestionItem("2", "x² + 6x + 9 = 0", "Numbers that multiply to 9 and add to 6: 3 and 3"),
        PracticeQuestionItem("3", "2x² - 5x - 3 = 0", "Factor by grouping: (2x + 1)(x - 3) = 0"),
        PracticeQuestionItem("4", "x² - 4x - 12 = 0", "Numbers that multiply to -12 and add to -4: -6 and 2"),
    ),
    val quickHintText: String = "Look for two numbers that multiply to c and add to b."
)
