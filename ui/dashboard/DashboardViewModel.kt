package com.siamts.synapse.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.repository.HomeRepository
import com.siamts.synapse.data.repository.HomeRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing Student Dashboard state, live MySQL data fetching, and interactions.
 */
class DashboardViewModel(
    private val repository: HomeRepository = HomeRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getHomeDashboard().fold(
                onSuccess = { data ->
                    val student = data.student
                    val studentName = student?.name?.takeIf { it.isNotBlank() } ?: "Student"
                    val photoUrl = student?.photoUrl ?: student?.profilePhoto

                    val planItems = data.todayPlanItems.map { pi ->
                        PlanItem(
                            id = pi.id,
                            title = pi.title,
                            category = pi.category,
                            duration = pi.duration,
                            iconEmoji = pi.iconEmoji,
                            actionType = pi.actionType,
                            subjectSlug = pi.subjectSlug,
                            chapterId = pi.chapterId
                        )
                    }

                    val recTitle = data.brainMap?.recommendationTitle ?: "Linear algebra essentials"
                    val recSub = data.brainMap?.recommendationSubtitle ?: "Build a stronger foundation · 15 min"

                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            errorMessage = null,
                            dateText = data.dateText.ifBlank { "MONDAY, 19 OCTOBER" },
                            userName = studentName,
                            profilePhotoUri = photoUrl,
                            overallProgress = data.overallProgress,
                            progressMessage = data.progressMessage,
                            topicsDone = data.topicsDone,
                            totalTopics = data.totalTopics,
                            quizScore = data.quizScore,
                            quizzesAttempted = data.quizzesAttempted,
                            studyStreakDays = data.studyStreakDays,
                            todayPlanTotalTime = data.todayPlanTotalTime,
                            todayPlanItems = if (planItems.isNotEmpty()) planItems else current.todayPlanItems,
                            recommendedTitle = recTitle,
                            recommendedSubtitle = recSub
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Failed to load dashboard data."
                        )
                    }
                }
            )
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun onPlanItemClicked(itemId: String) {
        // Handle plan item navigation
    }

    fun onAskAiTutorClicked() {
        // Open AI Tutor Chat
    }

    fun retry() {
        loadDashboardData(forceRefresh = true)
    }

    fun refresh() {
        loadDashboardData(forceRefresh = true)
    }
}
