package com.siamts.synapse.ui.chemistry

/**
 * UI State for Chemical Bonding Lesson Part 2 (Ionic vs Covalent Bonding) following MVVM architecture.
 */
data class ChemicalBondingLessonPart2UiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val headerTag: String = "A PATH THAT EVOLVES WITH YOU",
    val title: String = "Chemistry",
    val subtitleTag: String = "Part 2: Going Deeper",
    val progressPercent: Int = 64,
    val progressSubtext: String = "3 of 5 lessons completed · Beginner path",
    val selectedSubTab: Int = 0,
    val headline: String = "Ionic vs Covalent Bonding",
    val conceptText: String = "Atoms form chemical bonds to achieve a stable electron configuration. The two main types are ionic bonds (transfer) and covalent bonds (sharing).",
    val keyPoints: List<String> = listOf(
        "Ionic bonding: transfer of electrons (metal + non-metal).",
        "Covalent bonding: sharing of electrons (non-metal + non-metal).",
        "Ionic bonds form lattice structures.",
        "Covalent bonds form molecules."
    ),
    val quickExampleText: String = "What type of bond is in H₂O?\nAnswer: Covalent (electron sharing).",
    val aiRecommendation: String = "Practice drawing Lewis structures for common compounds.",
    val chapterId: Int = 28,
    val isCompleted: Boolean = false
)
