package com.siamts.synapse.ui.home

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
 * Clean MVVM ViewModel managing Home Screen state, live MySQL data fetching, and interactions.
 */
class HomeViewModel(
    private val repository: HomeRepository = HomeRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getHomeDashboard().fold(
                onSuccess = { data ->
                    val student = data.student
                    val studentName = student?.name?.takeIf { it.isNotBlank() } ?: "Student"
                    val photoUrl = student?.photoUrl ?: student?.profilePhoto
                    val courseText = student?.course?.takeIf { it.isNotBlank() } ?: "B.Tech Computer Science · Year 3"
                    val interestsText = if (!student?.interests.isNullOrEmpty()) {
                        "Interests: " + student?.interests?.joinToString(", ")
                    } else {
                        "Interests: Computer Science, AI, Math"
                    }

                    val sessions = data.recentSessions.map { s ->
                        RecentSession(
                            id = s.id,
                            chapterId = s.chapterId,
                            title = s.title,
                            category = s.category,
                            subjectSlug = s.subjectSlug,
                            timeAgo = s.timeAgo,
                            progress = s.progress
                        )
                    }

                    val subjects = data.subjects.map { sb ->
                        HomeSubjectItem(
                            id = sb.id,
                            name = sb.name,
                            slug = sb.slug,
                            iconSymbol = sb.iconSymbol,
                            colorHex = sb.colorHex,
                            totalChapters = sb.totalChapters,
                            completedChapters = sb.completedChapters,
                            progressPercent = sb.progressPercent
                        )
                    }

                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            errorMessage = null,
                            userName = studentName,
                            greeting = data.greeting.ifBlank { "Welcome Back," },
                            mindPower = if (data.mindPower > 0) data.mindPower else data.overallProgress,
                            profilePhotoUri = photoUrl,
                            course = courseText,
                            interests = interestsText,
                            overallProgress = data.overallProgress,
                            progressMessage = data.progressMessage,
                            topicsDone = data.topicsDone,
                            totalTopics = data.totalTopics,
                            quizScore = data.quizScore,
                            quizzesAttempted = data.quizzesAttempted,
                            latestQuizScore = data.latestQuizScore,
                            studyStreakDays = data.studyStreakDays,
                            continueChapterTitle = data.continueLearning?.chapterTitle,
                            continueSubjectSlug = data.continueLearning?.subjectSlug,
                            continueChapterId = data.continueLearning?.chapterId ?: 0,
                            recentSessions = sessions,
                            subjects = subjects
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

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun onActionClicked(actionId: String) {
        // Navigation callback handled at UI layer
    }

    fun onSessionClicked(sessionId: String) {
        // Navigation callback handled at UI layer
    }

    fun retry() {
        loadHomeData(forceRefresh = true)
    }

    fun refresh() {
        loadHomeData(forceRefresh = true)
    }
}
