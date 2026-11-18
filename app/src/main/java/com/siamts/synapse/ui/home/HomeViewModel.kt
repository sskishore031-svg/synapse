package com.siamts.synapse.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.SessionManager
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
    private val repository: HomeRepository = HomeRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull(),
    private val quizDao: com.siamts.synapse.data.local.QuizDao = com.siamts.synapse.data.local.QuizLocalDatabase.getInstance(),
    private val chapterDao: com.siamts.synapse.data.local.ChapterDao = com.siamts.synapse.data.local.QuizLocalDatabase.getChapterDao()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = false))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeUserSession()
        loadLocalStats()
        loadHomeData()
    }

    private fun observeUserSession() {
        val manager = sessionManager ?: SessionManager.getInstanceOrNull() ?: return
        viewModelScope.launch {
            manager.sessionStateFlow.collect { sessionState ->
                if (sessionState is com.siamts.synapse.data.local.AuthSessionState.Authenticated) {
                    val realName = sessionState.user.name.takeIf { it.isNotBlank() } ?: "Student"
                    _uiState.update { current ->
                        current.copy(
                            userName = realName,
                            profilePhotoUri = sessionState.user.photoUrl
                        )
                    }
                }
            }
        }
    }

    private fun loadLocalStats() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val manager = sessionManager ?: SessionManager.getInstanceOrNull()
            val user = manager?.getSessionUser()
            val userName = user?.name?.takeIf { it.isNotBlank() } ?: _uiState.value.userName.ifBlank { "Student" }

            val quizRecords = try { quizDao.getAllRecords() } catch (_: Throwable) { emptyList() }
            val chapters = try { chapterDao.getAllChapters() } catch (_: Throwable) { emptyList() }

            val completedChapters = chapters.filter { it.isAttempted }.map { it.id }
            val quizChapters = quizRecords.map { it.chapterId }
            val distinctTopicsDone = (completedChapters + quizChapters).filter { it.isNotBlank() }.distinct().size

            val streak = if (distinctTopicsDone > 0 || quizRecords.isNotEmpty()) {
                manager?.recordActiveStudyDay() ?: manager?.getStudyStreak() ?: 0
            } else {
                0
            }

            val avgScore = if (quizRecords.isNotEmpty()) {
                (quizRecords.map { (it.score * 100) / it.totalQuestions.coerceAtLeast(1) }.average()).toInt()
            } else {
                0
            }

            val totalTopics = 24
            val overallPercent = if (totalTopics > 0) {
                ((distinctTopicsDone.toFloat() / totalTopics.toFloat()) * 100).toInt().coerceIn(0, 100)
            } else {
                0
            }

            _uiState.update { current ->
                current.copy(
                    userName = userName,
                    overallProgress = overallPercent,
                    topicsDone = distinctTopicsDone,
                    totalTopics = totalTopics,
                    quizScore = avgScore,
                    quizzesAttempted = quizRecords.size,
                    studyStreakDays = streak
                )
            }
        }
    }

    fun loadHomeData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val manager = sessionManager ?: SessionManager.getInstanceOrNull()
            val user = manager?.getSessionUser()

            repository.getHomeDashboard(userId = user?.id, email = user?.email).fold(
                onSuccess = { data ->
                    val student = data.student
                    val studentName = user?.name?.takeIf { it.isNotBlank() } ?: student?.name?.takeIf { it.isNotBlank() } ?: _uiState.value.userName.ifBlank { "Student" }
                    val photoUrl = user?.photoUrl ?: student?.photoUrl ?: student?.profilePhoto
                    val courseText = user?.course?.takeIf { it.isNotBlank() } ?: student?.course?.takeIf { it.isNotBlank() } ?: "B.Tech Computer Science · Year 3"
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
                            progressPercent = sb.progressPercent,
                            isInterested = sb.isInterested
                        )
                    }

                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            errorMessage = null,
                            userName = studentName,
                            greeting = data.greeting.ifBlank { "Welcome Back," },
                            mindPower = if (current.overallProgress > 0) current.overallProgress else data.mindPower,
                            profilePhotoUri = photoUrl,
                            course = courseText,
                            interests = interestsText,
                            progressMessage = data.progressMessage,
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
                            errorMessage = null // Keep local dashboard pristine offline
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
