package com.siamts.synapse.ui.algorithms

/**
 * UI State for Algorithms Lesson Part 2 (Binary Search).
 */
data class AlgorithmsLessonPart2UiState(
    val headerTag: String = "A PATH THAT EVOLVES WITH YOU",
    val title: String = "Computer Science",
    val subtitleTag: String = "Part 2: Step-by-Step",
    val progressPercent: Int = 50,
    val progressSubtext: String = "3 of 5 lessons completed · Beginner path",
    val selectedSubTab: Int = 0,
    val headline: String = "Binary Search",
    val conceptText: String = "Binary search finds a target value in a sorted array by repeatedly dividing the search range in half. It is much faster than linear search for large datasets.",
    val exampleTitle: String = "Example: Find 6 in [2, 4, 6, 8, 10, 12, 14]",
    val keyPoints: List<String> = listOf(
        "Array must be sorted.",
        "Compare with middle element.",
        "Keep halving the search range.",
        "Time complexity: O(log n)."
    ),
    val quickExampleText: String = "Find 6 in [2, 4, 6, 8, 10, 12, 14]\nResult: Found at index 2",
    val aiRecommendation: String = "Try implementing binary search in code and test it with different inputs."
)
