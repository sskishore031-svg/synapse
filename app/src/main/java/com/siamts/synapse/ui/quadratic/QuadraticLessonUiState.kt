package com.siamts.synapse.ui.quadratic

/**
 * UI State for the Quadratic Equations Lesson Screen.
 */
data class QuadraticLessonUiState(
    val headerTag: String = "A DECK THAT EVOLVES WITH YOU",
    val title: String = "Quadratic Equations",
    val progressPercent: Int = 80,
    val progressSubtext: String = "4 of 5 lessons completed · Beginner path",
    val selectedSubTab: Int = 0, // 0: Learn, 1: Notes, 2: Quiz
    val headline: String = "What are Quadratic Equations?",
    val conceptText: String = "A quadratic equation is a second-degree polynomial equation in the form:",
    val formulaText: String = "ax² + bx + c = 0",
    val formulaSubtext: String = "where a ≠ 0, and a, b, c are constants.",
    val parabolaBadgeTitle: String = "Parabola",
    val parabolaBadgeSubtext: String = "Opens upward",
    val keyPoints: List<String> = listOf(
        "a determines the direction of the parabola.",
        "The discriminant b² - 4ac tells the nature of roots.",
        "Solutions can be real and distinct or complex roots."
    ),
    val exampleTitle: String = "Quick Example",
    val exampleProblem: String = "Solve: x² - 5x + 6 = 0",
    val exampleFactored: String = "(x - 2)(x - 3) = 0",
    val exampleSolution: String = "x = 2, 3"
)
