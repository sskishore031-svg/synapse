package com.siamts.synapse.ui.datavis

/**
 * UI State for Data Visualization Lesson Part 2 (Choosing the Right Visualization).
 */
data class DataVisualizationLessonPart2UiState(
    val headerTag: String = "A PATH THAT EVOLVES WITH YOU",
    val title: String = "Data Science",
    val subtitleTag: String = "Part 2: Going Deeper",
    val progressPercent: Int = 48,
    val progressSubtext: String = "2 of 5 lessons completed · Beginner path",
    val selectedSubTab: Int = 0,
    val headline: String = "Choosing the Right Visualization",
    val conceptText: String = "Different types of charts help you answer different questions. The right visualization depends on what you want to compare, show or find.",
    val keyPoints: List<String> = listOf(
        "Bar chart → comparisons",
        "Line chart → trends over time",
        "Scatter plot → relationships & outliers"
    ),
    val quickExampleText: String = "Which chart would you use to show sales trend across months?\nAnswer: Line chart",
    val aiRecommendation: String = "Try creating your own chart using a dataset and write what insights you find."
)
