package com.siamts.synapse.ui.chemistry

/**
 * UI State for the Chemical Bonding Lesson Screen.
 */
data class ChemicalBondingLessonUiState(
    val headerTag: String = "A PATH THAT EVOLVES WITH YOU",
    val title: String = "Chemical Bonding",
    val progressPercent: Int = 64,
    val progressSubtext: String = "3 of 5 lessons completed · Beginner path",
    val selectedSubTab: Int = 0, // 0: Learn, 1: Notes, 2: Quiz
    val headline: String = "What is Chemical Bonding?",
    val conceptText: String = "Chemical bonding is the force of attraction between atoms that helps them form molecules and compounds. Atoms bond to achieve a stable electron configuration.",
    val keyPoints: List<String> = listOf(
        "Ionic bonds: transfer of electrons (metal + non-metal).",
        "Covalent bonds: sharing of electrons (non-metal + non-metal).",
        "Forms stable compounds and molecules."
    ),
    val exampleTitle: String = "Quick Example",
    val exampleProblem: String = "Sodium chloride (NaCl) forms by transfer of one electron from Na to Cl.",
    val exampleSolution: String = "Na⁺ + Cl⁻ → NaCl"
)
