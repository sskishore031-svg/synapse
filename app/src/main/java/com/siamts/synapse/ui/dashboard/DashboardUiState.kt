package com.siamts.synapse.ui.dashboard

data class PlanItem(
    val id: String,
    val title: String,
    val category: String,
    val duration: String,
    val iconEmoji: String = "▶",
    val isCompleted: Boolean = false,
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
    val dateText: String = "",
    val userName: String = "",
    val profilePhotoUri: String? = null,
    val overallProgress: Int = 0,
    val progressMessage: String = "Welcome! Every lesson builds your foundation.",
    val topicsDone: Int = 0,
    val totalTopics: Int = 24,
    val quizScore: Int = 0,
    val quizzesAttempted: Int = 0,
    val studyStreakDays: Int = 1,
    val todayPlanTotalTime: String = "0 min",
    val todayPlanItems: List<PlanItem> = emptyList(),
    val recommendedTitle: String = "Linear algebra essentials",
    val recommendedSubtitle: String = "Build a stronger foundation · 15 min",
    val selectedTab: Int = 0,
)
