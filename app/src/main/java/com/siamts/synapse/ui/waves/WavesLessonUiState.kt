package com.siamts.synapse.ui.waves

/**
 * UI State for the Waves Lesson Screen.
 */
data class WavesLessonUiState(
    val headerTag: String = "A PATH THAT EVOLVES WITH YOU",
    val title: String = "Waves",
    val progressPercent: Int = 68,
    val progressSubtext: String = "3 of 5 lessons completed · Beginner path",
    val selectedSubTab: Int = 0, // 0: Learn, 1: Notes, 2: Quiz
    val headline: String = "What are Waves?",
    val conceptText: String = "Waves are disturbances that transfer energy from one place to another without transferring matter. They can be mechanical or electromagnetic.",
    val keyPoints: List<String> = listOf(
        "Wavelength (λ): distance between two peaks.",
        "Amplitude: maximum displacement.",
        "Frequency (f): number of waves per second.",
        "Relationship: v = f × λ"
    ),
    val exampleTitle: String = "Quick Example",
    val exampleProblem: String = "A sound wave has a frequency of 440 Hz and wavelength of 0.8 m. Find its speed.",
    val exampleSolution: String = "v = f × λ = 440 × 0.8 = 352 m/s"
)
