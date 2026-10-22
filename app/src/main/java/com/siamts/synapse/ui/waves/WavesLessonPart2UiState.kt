package com.siamts.synapse.ui.waves

/**
 * UI State for Waves Lesson Part 2 (Understanding Waves).
 */
data class WavesLessonPart2UiState(
    val headerTag: String = "A PATH THAT EVOLVES WITH YOU",
    val title: String = "Physics",
    val subtitleTag: String = "Part 2: Going Deeper",
    val progressPercent: Int = 68,
    val progressSubtext: String = "3 of 5 lessons completed · Beginner path",
    val selectedSubTab: Int = 0,
    val headline: String = "Understanding Waves",
    val conceptText: String = "A wave transfers energy from one place to another without transferring matter. A wave type has properties: wavelength, frequency and amplitude.",
    val properties: List<String> = listOf(
        "Wavelength (λ): distance between two consecutive peaks.",
        "Frequency (f): number of waves per second.",
        "Amplitude: maximum displacement from the rest position.",
        "Wave Speed (v): v = f × λ"
    ),
    val exampleCalculation: String = "If a wave has a frequency of 5 Hz and a wavelength of 2 m, find its speed:\nv = f × λ = 5 × 2 = 10 m/s",
    val keyPoints: List<String> = listOf(
        "Wavelength is the distance between peaks.",
        "Frequency is the number of waves per second.",
        "Amplitude measures wave height.",
        "Speed = frequency × wavelength"
    ),
    val aiRecommendation: String = "Solve more wave problems with different frequencies and wavelengths."
)
