package com.siamts.synapse.ui.dashboard

data class PlanItem(
    val id: String,
    val title: String,
    val category: String,
    val duration: String,
    val iconEmoji: String,
    val actionType: String? = null,
    val subjectSlug: String? = null,
    val chapterId: Int = 0,
)

/**
 * UI State for the Student Dashboard Screen following MVVM architecture.
 */
data class DashboardUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val dateText: String = "MONDAY, 19 OCTOBER",
    val userName: String = "Student",
    val profilePhotoUri: String? = null,
    val overallProgress: Int = 68,
    val progressMessage: String = "Great work! Every lesson makes a new connection.",
    val topicsDone: Int = 16,
    val totalTopics: Int = 30,
    val quizScore: Int = 86,
    val quizzesAttempted: Int = 0,
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
