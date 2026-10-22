package com.siamts.synapse.ui.quadratic

data class NoteSummaryItem(
    val id: String,
    val title: String,
    val subtitle: String,
)

/**
 * UI State for Mathematics Chapter Notes Screen.
 */
data class MathNotesUiState(
    val headerTag: String = "A PATH THAT EVOLVES WITH YOU",
    val title: String = "Mathematics",
    val subtitleTag: String = "Notes",
    val progressPercent: Int = 88,
    val progressSubtext: String = "4 of 5 lessons completed · Beginner path",
    val selectedSubTab: Int = 1, // Learn=0, Notes=1, Quiz=2
    val headline: String = "My Notes",
    val noteItems: List<NoteSummaryItem> = listOf(
        NoteSummaryItem("1", "Key Formulas", "Quadratic formula, factoring rules"),
        NoteSummaryItem("2", "Steps to Factor", "Step-by-step method"),
        NoteSummaryItem("3", "Common Mistakes", "Things to avoid while solving"),
        NoteSummaryItem("4", "Practice Tips", "Quick tips for better understanding"),
    ),
    val rememberBannerText: String = "Factoring works only when the quadratic expression can be written as a product of two binomials with integer coefficients."
)
