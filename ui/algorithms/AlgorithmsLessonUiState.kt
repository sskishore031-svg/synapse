package com.siamts.synapse.ui.algorithms

/**
 * UI State for the Algorithms Lesson Screen.
 */
data class AlgorithmsLessonUiState(
    val headerTag: String = "A PATH THAT EVOLVES WITH YOU",
    val title: String = "Algorithms",
    val progressPercent: Int = 60,
    val progressSubtext: String = "3 of 5 lessons completed · Beginner path",
    val selectedSubTab: Int = 0, // 0: Learn, 1: Notes, 2: Quiz
    val headline: String = "What is an Algorithm?",
    val conceptText: String = "An algorithm is a step-by-step set of instructions to solve a problem or perform a task. It must be clear, finite and effective.",
    val exampleHeader: String = "Step-by-Step Example: Linear Search",
    val steps: List<String> = listOf(
        "Start from the first element.",
        "Compare with the target value.",
        "If match, return the index.",
        "If not, move to the next element.",
        "Repeat until found or end."
    ),
    val exampleTitle: String = "Quick Example",
    val exampleTarget: String = "Linear Search (target = 7)",
    val exampleArray: String = "Array: [3, 5, 7, 2, 8] → Found at index 2"
)
