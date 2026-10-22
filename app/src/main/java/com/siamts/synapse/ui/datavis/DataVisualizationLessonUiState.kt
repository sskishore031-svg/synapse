package com.siamts.synapse.ui.datavis

/**
 * UI State for the Data Visualization Lesson Screen.
 */
data class DataVisualizationLessonUiState(
    val headerTag: String = "A PATH THAT EVOLVES WITH YOU",
    val title: String = "Data Visualization",
    val progressPercent: Int = 48,
    val progressSubtext: String = "2 of 5 lessons completed · Beginner path",
    val selectedSubTab: Int = 0, // 0: Learn, 1: Notes, 2: Quiz
    val headline: String = "What is Data Visualization?",
    val conceptText: String = "Data visualization is the graphical representation of data. It helps us understand patterns, trends and insights more easily.",
    val chartTitle: String = "Monthly Sales Comparison",
    val keyPoints: List<String> = listOf(
        "Charts make data easier to understand.",
        "Helps identify trends and outliers.",
        "Used in business, science and research."
    ),
    val exampleTitle: String = "Quick Example",
    val exampleText: String = "Compare monthly sales of three products. Product A has the highest sales in May."
)
