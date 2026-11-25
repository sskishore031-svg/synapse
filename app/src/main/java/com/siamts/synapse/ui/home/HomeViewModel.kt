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
    private val chapterDao: com.siamts.synapse.data.local.ChapterDao = com.siamts.synapse.data.local.QuizLocalDatabase.getChapterDao(),
    private val taskDao: com.siamts.synapse.data.local.LearningTaskDao = com.siamts.synapse.data.local.QuizLocalDatabase.getLearningTaskDao()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = false))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeUserSession()
        observeLessonProgress()
        loadLocalStats()
        loadDailyTasks()
        loadHomeData()
    }

    private fun observeLessonProgress() {
        val progressDao = com.siamts.synapse.data.local.SynapseDatabase.getLessonProgressDao()
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            progressDao.observeProgressForUser("1").collect { progressList ->
                val totalTopics = 20
                // Two-stage completion credits: conceptDone = +0.5 credit (+10%), quizDone = +0.5 credit (+10%)
                val totalCredits = progressList.sumOf { (if (it.conceptDone || it.lessonCompleted) 0.5 else 0.0) + (if (it.quizDone || it.quizCompleted) 0.5 else 0.0) }
                val overallPercent = ((totalCredits / totalTopics.toDouble()) * 100.0).toInt().coerceIn(0, 100)
                val distinctTopicsDone = totalCredits.toInt().coerceAtMost(totalTopics)

                _uiState.update { current ->
                    current.copy(
                        overallProgress = overallPercent,
                        topicsDone = distinctTopicsDone,
                        totalTopics = totalTopics
                    )
                }
            }
        }
    }

    private fun observeUserSession() {
        val manager = sessionManager ?: SessionManager.getInstanceOrNull() ?: return
        viewModelScope.launch {
            manager.sessionStateFlow.collect { sessionState ->
                if (sessionState is com.siamts.synapse.data.local.AuthSessionState.Authenticated) {
                    val realName = sessionState.user.name.takeIf { it.isNotBlank() } ?: "Kishore"
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
            val userName = user?.name?.takeIf { it.isNotBlank() } ?: _uiState.value.userName.ifBlank { "Kishore" }

            val studySessions = try { com.siamts.synapse.data.local.QuizLocalDatabase.getStudySessionDao().getAllSessions() } catch (_: Throwable) { emptyList() }
            val quizRecords = try { quizDao.getAllRecords() } catch (_: Throwable) { emptyList() }
            val chapters = try { chapterDao.getAllChapters() } catch (_: Throwable) { emptyList() }
            val progressList = try { com.siamts.synapse.data.local.SynapseDatabase.getLessonProgressDao().getProgressForUser("1") } catch (_: Throwable) { emptyList() }

            val totalCredits = progressList.sumOf { (if (it.conceptDone || it.lessonCompleted) 0.5 else 0.0) + (if (it.quizDone || it.quizCompleted) 0.5 else 0.0) }
            val distinctTopicsDone = totalCredits.toInt().coerceAtMost(20)

            val streak = if (distinctTopicsDone > 0 || quizRecords.isNotEmpty() || studySessions.isNotEmpty()) {
                val dates = studySessions.map { it.studyDate }.filter { it.isNotBlank() }.distinct().sortedDescending()
                if (dates.isNotEmpty()) {
                    manager?.recordActiveStudyDay() ?: manager?.getStudyStreak() ?: dates.size
                } else {
                    manager?.recordActiveStudyDay() ?: manager?.getStudyStreak() ?: 0
                }
            } else {
                0
            }

            val avgScore = if (quizRecords.isNotEmpty()) {
                (quizRecords.map { (it.score * 100) / it.totalQuestions.coerceAtLeast(1) }.average()).toInt()
            } else {
                0
            }

            val totalTopics = 20
            val overallPercent = if (totalTopics > 0) {
                ((distinctTopicsDone.toFloat() / totalTopics.toFloat()) * 100).toInt().coerceIn(0, 100)
            } else {
                0
            }

            val activeDates = studySessions.map { it.studyDate }.filter { it.isNotBlank() }.distinct()
            val cal = java.util.Calendar.getInstance()
            cal.firstDayOfWeek = java.util.Calendar.MONDAY
            cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY)
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            val currentWeekDates = (0..6).map { offset ->
                val c = cal.clone() as java.util.Calendar
                c.add(java.util.Calendar.DAY_OF_MONTH, offset)
                sdf.format(c.time)
            }
            val weeklyBooleans = currentWeekDates.map { it in activeDates }
            val bestStreak = maxOf(streak, activeDates.size)

            _uiState.update { current ->
                current.copy(
                    userName = userName,
                    overallProgress = overallPercent,
                    topicsDone = distinctTopicsDone,
                    totalTopics = totalTopics,
                    quizScore = avgScore,
                    quizzesAttempted = quizRecords.size,
                    studyStreakDays = streak,
                    bestStreakDays = bestStreak,
                    weeklyActiveDays = weeklyBooleans
                )
            }
        }
    }

    fun loadDailyTasks() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val tasks = try { taskDao.getAllTasks() } catch (_: Throwable) { emptyList() }
            val completedCount = tasks.count { it.isCompleted }
            val goalPct = if (tasks.isNotEmpty()) {
                ((completedCount.toFloat() / tasks.size.toFloat()) * 100).toInt().coerceIn(0, 100)
            } else if (_uiState.value.topicsDone > 0) {
                ((_uiState.value.topicsDone.toFloat() / _uiState.value.totalTopics.toFloat()) * 100).toInt().coerceIn(0, 100)
            } else {
                0
            }
            _uiState.update { current ->
                current.copy(
                    dailyTasks = tasks,
                    todayGoalPercent = goalPct
                )
            }
        }
    }

    fun addStudyTask(title: String, duration: String = "15 min") {
        if (title.isBlank()) return
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val newTask = com.siamts.synapse.data.local.LearningTaskEntity(
                title = title.trim(),
                duration = duration.trim(),
                isCompleted = false
            )
            try {
                taskDao.insertTask(newTask)
                runCatching {
                    com.siamts.synapse.data.remote.RetrofitClient.learningPlanApi.addLearningTask(
                        title = newTask.title,
                        duration = newTask.duration
                    )
                }
            } catch (_: Throwable) {}
            loadDailyTasks()
        }
    }

    fun toggleStudyTask(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                taskDao.updateTaskCompletion(taskId, isCompleted)
                runCatching {
                    com.siamts.synapse.data.remote.RetrofitClient.learningPlanApi.toggleLearningTask(
                        taskId = taskId,
                        isCompleted = if (isCompleted) 1 else 0
                    )
                }
            } catch (_: Throwable) {}
            loadDailyTasks()
        }
    }

    fun deleteStudyTask(taskId: Long) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                taskDao.deleteTask(taskId)
                runCatching {
                    com.siamts.synapse.data.remote.RetrofitClient.learningPlanApi.deleteLearningTask(
                        taskId = taskId
                    )
                }
            } catch (_: Throwable) {}
            loadDailyTasks()
        }
    }

    fun setAddTaskDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showAddTaskDialog = visible) }
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
                    val courseText = user?.course?.takeIf { it.isNotBlank() } ?: student?.course?.takeIf { it.isNotBlank() } ?: "Placement & Micro-Learning Prep"
                    val interestsText = if (!student?.interests.isNullOrEmpty()) {
                        "Interests: " + student?.interests?.joinToString(", ")
                    } else {
                        "Interests: Python, SQL, AI, Aptitude"
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
                            mindPower = current.overallProgress,
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
