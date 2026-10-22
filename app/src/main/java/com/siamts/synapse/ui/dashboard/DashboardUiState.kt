package com.siamts.synapse.ui.dashboard

data class PlanItem(
    val id: String,
    val title: String,
    val category: String,
    val duration: String,
    val iconEmoji: String,
)

/**
 * UI State for the Student Dashboard Screen following MVVM architecture.
 */
data class DashboardUiState(
    val dateText: String = "MONDAY, 19 OCTOBER",
    val userName: String = "Student",
    val overallProgress: Int = 68,
    val progressMessage: String = "Great work, Arjun. You're ahead of last week.",
    val topicsDone: Int = 24,
    val quizScore: Int = 86,
    val studyStreakDays: Int = 7,
    val todayPlanTotalTime: String = "25 min",
    val todayPlanItems: List<PlanItem> = listOf(
        PlanItem("1", "Understand gradient descent", "AI", "12 min", "▶"),
        PlanItem("2", "Test your neural network skills", "Quiz", "8 min", "❓"),
        PlanItem("3", "Review 10 flashcards", "Revision", "5 min", "🎴"),
    ),
    val recommendedTitle: String = "Linear algebra essentials",
    val recommendedSubtitle: String = "Build a stronger foundation · 15 min",
    val selectedTab: Int = 0,
)
