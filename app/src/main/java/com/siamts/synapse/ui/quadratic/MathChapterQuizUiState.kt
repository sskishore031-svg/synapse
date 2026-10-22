package com.siamts.synapse.ui.quadratic

/**
 * UI State for Mathematics Chapter Quiz Screen (100% Completed).
 */
data class MathChapterQuizUiState(
    val headerTag: String = "A PATH THAT EVOLVES WITH YOU",
    val title: String = "Mathematics",
    val subtitleTag: String = "Chapter Quiz",
    val progressPercent: Int = 100,
    val progressSubtext: String = "5 of 5 lessons completed · Beginner path",
    val selectedSubTab: Int = 2, // Learn=0, Notes=1, Quiz=2
    val quizTitle: String = "Chapter Quiz",
    val quizSubtitle: String = "Test what you have learned",
    val durationInfo: String = "10 questions · 10 minutes",
    val typeInfo: String = "Multiple choice & short answer",
    val quizTopics: List<String> = listOf(
        "Factoring quadratic expressions",
        "Solving equations by factoring",
        "Zero product property",
        "Real-world word problems"
    ),
    val successTitle: String = "Great job!",
    val successText: String = "You've completed this chapter. Keep going!"
)
