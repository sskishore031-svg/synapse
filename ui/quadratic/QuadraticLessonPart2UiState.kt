package com.siamts.synapse.ui.quadratic

/**
 * UI State for Quadratic Equations Lesson Part 2 (Step-by-Step Factoring).
 */
data class QuadraticLessonPart2UiState(
    val headerTag: String = "A PATH THAT EVOLVES WITH YOU",
    val title: String = "Mathematics",
    val subtitleTag: String = "Part 2: Step-by-Step",
    val progressPercent: Int = 72,
    val progressSubtext: String = "4 of 5 lessons completed · Beginner path",
    val selectedSubTab: Int = 0,
    val headline: String = "Factoring Quadratic Equations",
    val conceptText: String = "Factoring is a quick way to solve quadratic equations of the form ax² + bx + c = 0. Find two numbers that multiply to ac and add to b, then write the equation as a product of binomials.",
    val formulaBoxText: String = "(x - x₁)(x - x₂) = 0",
    val exampleProblem: String = "Example: x² - 5x + 6 = 0",
    val exampleSteps: List<String> = listOf(
        "Find two numbers: -2 and -3 ((-2) × (-3) = 6, (-2) + (-3) = -5)",
        "Factor: (x - 2)(x - 3) = 0",
        "Roots: x = 2, x = 3"
    ),
    val keyPoints: List<String> = listOf(
        "Find two numbers that multiply to ac and add to b.",
        "Factor the quadratic equation.",
        "The roots are the x-intercepts of the graph."
    ),
    val quickExampleText: String = "Solve: x² - 5x + 6 = 0\nRoots: x = 2, 3",
    val aiRecommendation: String = "Practice more factoring problems with different coefficients and check your answers."
)
