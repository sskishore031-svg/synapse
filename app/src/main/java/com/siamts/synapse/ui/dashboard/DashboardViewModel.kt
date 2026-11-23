package com.siamts.synapse.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.AuthSessionState
import com.siamts.synapse.data.local.ChapterDao
import com.siamts.synapse.data.local.LearningTaskDao
import com.siamts.synapse.data.local.LearningTaskEntity
import com.siamts.synapse.data.local.QuizDao
import com.siamts.synapse.data.local.QuizLocalDatabase
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.HomeRepository
import com.siamts.synapse.data.repository.HomeRepositoryImpl
import com.siamts.synapse.data.repository.SubjectCatalogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel managing Student Dashboard state, real-time stats from local SQLite/Room tables,
 * user-created learning plan tasks, and persistent session sync.
 */
class DashboardViewModel(
    private val repository: HomeRepository = HomeRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull(),
    private val chapterDao: ChapterDao = QuizLocalDatabase.getChapterDao(),
    private val quizDao: QuizDao = QuizLocalDatabase.getInstance(),
    private val learningTaskDao: LearningTaskDao = QuizLocalDatabase.getLearningTaskDao(),
    private val catalogRepo: SubjectCatalogRepository? = try { SubjectCatalogRepository.getInstance() } catch (_: Throwable) { null },
    private val learningPlanApi: com.siamts.synapse.data.remote.LearningPlanApi = com.siamts.synapse.data.remote.RetrofitClient.learningPlanApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DashboardUiState(
            isLoading = false,
            dateText = formatCurrentDate()
        )
    )
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeUserSession()
        observeLessonProgress()
        loadLocalStatsAndTasks()
        loadDashboardData()
    }

    private fun observeLessonProgress() {
        val progressDao = com.siamts.synapse.data.local.SynapseDatabase.getLessonProgressDao()
        viewModelScope.launch(Dispatchers.IO) {
            progressDao.observeProgressForUser("1").collect { progressList ->
                val completedIds = progressList.filter { it.isCompleted }.map { it.lessonId }.toSet()
                val quizRecords = try { quizDao.getAllRecords() } catch (_: Throwable) { emptyList() }
                val quizChapterIds = quizRecords.map { it.chapterId }.toSet()
                val allCompletedLessons = (completedIds + quizChapterIds).filter { it.isNotBlank() }.distinct()
                val totalTopics = 20
                val distinctTopicsDone = allCompletedLessons.size.coerceAtMost(totalTopics)
                val overallPercent = ((distinctTopicsDone.toFloat() / totalTopics.toFloat()) * 100).toInt().coerceIn(0, 100)

                val progressMsg = when {
                    distinctTopicsDone == 0 -> "Welcome! Every lesson builds your foundation."
                    distinctTopicsDone < 5 -> "Great start! Keep building your momentum."
                    distinctTopicsDone < 15 -> "Great work! Every lesson makes a new connection."
                    else -> "Outstanding progress! You're mastering your curriculum."
                }

                _uiState.update { current ->
                    current.copy(
                        overallProgress = overallPercent,
                        topicsDone = distinctTopicsDone,
                        totalTopics = totalTopics,
                        progressMessage = progressMsg
                    )
                }
            }
        }
    }

    private fun formatCurrentDate(): String {
        return try {
            SimpleDateFormat("EEEE, d MMMM", Locale.US).format(Date()).uppercase(Locale.US)
        } catch (_: Throwable) {
            "TODAY"
        }
    }

    private fun observeUserSession() {
        val manager = sessionManager ?: SessionManager.getInstanceOrNull() ?: return
        viewModelScope.launch {
            manager.sessionStateFlow.collect { sessionState ->
                if (sessionState is AuthSessionState.Authenticated) {
                    val realName = sessionState.user.name.takeIf { it.isNotBlank() } ?: "Kishore"
                    val photoUrl = sessionState.user.photoUrl
                    _uiState.update { current ->
                        current.copy(
                            userName = realName,
                            profilePhotoUri = photoUrl
                        )
                    }
                }
            }
        }
    }

    fun loadLocalStatsAndTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            val manager = sessionManager ?: SessionManager.getInstanceOrNull()
            val user = manager?.getSessionUser()
            val userName = user?.name?.takeIf { it.isNotBlank() } ?: _uiState.value.userName.ifBlank { "Kishore" }
            val photo = user?.photoUrl ?: _uiState.value.profilePhotoUri

            val studySessions = try { QuizLocalDatabase.getStudySessionDao().getAllSessions() } catch (_: Throwable) { emptyList() }
            val quizRecords = try { quizDao.getAllRecords() } catch (_: Throwable) { emptyList() }
            val chapters = try { chapterDao.getAllChapters() } catch (_: Throwable) { emptyList() }
            val progressList = try { com.siamts.synapse.data.local.SynapseDatabase.getLessonProgressDao().getProgressForUser("1") } catch (_: Throwable) { emptyList() }

            val completedProgressIds = progressList.filter { it.isCompleted }.map { it.lessonId }
            val completedChapters = chapters.filter { it.isAttempted }.map { it.id }
            val quizChapters = quizRecords.map { it.chapterId }
            val distinctTopicsDone = (completedProgressIds + completedChapters + quizChapters).filter { it.isNotBlank() }.distinct().size.coerceAtMost(20)

            // 1. Streak from DataStore (0 for new users with no completed topics or quizzes)
            val streakDays = if (distinctTopicsDone > 0 || quizRecords.isNotEmpty() || studySessions.isNotEmpty()) {
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

            val progressMsg = when {
                distinctTopicsDone == 0 -> "Welcome! Every lesson builds your foundation."
                distinctTopicsDone < 5 -> "Great start! Keep building your momentum."
                distinctTopicsDone < 15 -> "Great work! Every lesson makes a new connection."
                else -> "Outstanding progress! You're mastering your curriculum."
            }

            // 3. User-created Learning Plan tasks from SQLite table
            val tasks = try { learningTaskDao.getAllTasks() } catch (_: Throwable) { emptyList() }
            val planItems = tasks.map { t ->
                PlanItem(
                    id = t.id.toString(),
                    title = t.title,
                    category = t.category,
                    duration = t.duration,
                    iconEmoji = when (t.category.lowercase(Locale.ROOT)) {
                        "quiz" -> "❓"
                        "revision" -> "🎴"
                        else -> "▶"
                    },
                    isCompleted = t.isCompleted
                )
            }

            val totalMinutes = tasks.sumOf {
                it.duration.filter { c -> c.isDigit() }.toIntOrNull() ?: 15
            }
            val totalTimeText = if (totalMinutes > 0) "${totalMinutes} min" else "0 min"

            _uiState.update { current ->
                current.copy(
                    userName = userName,
                    profilePhotoUri = photo,
                    dateText = formatCurrentDate(),
                    overallProgress = overallPercent,
                    progressMessage = progressMsg,
                    topicsDone = distinctTopicsDone,
                    totalTopics = totalTopics,
                    quizScore = avgScore,
                    quizzesAttempted = quizRecords.size,
                    studyStreakDays = streakDays,
                    todayPlanTotalTime = totalTimeText,
                    todayPlanItems = planItems
                )
            }
        }
    }

    fun addLearningTask(title: String, category: String = "Lesson", duration: String = "15 min") {
        if (title.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            val entity = LearningTaskEntity(
                title = title.trim(),
                category = category.trim().ifBlank { "Lesson" },
                duration = duration.trim().ifBlank { "15 min" },
                isCompleted = false
            )
            learningTaskDao.insertTask(entity)
            loadLocalStatsAndTasks()

            // Dual-Sync: Call POST learning_plan.php
            try {
                val user = sessionManager?.getSessionUser()
                learningPlanApi.addLearningTask(
                    title = title.trim(),
                    category = category.trim().ifBlank { "Lesson" },
                    duration = duration.trim().ifBlank { "15 min" },
                    userId = user?.id,
                    email = user?.email
                )
            } catch (e: Exception) {
                android.util.Log.e("DashboardViewModel", "Remote addLearningTask failed", e)
            }
        }
    }

    fun toggleTaskCompletion(taskIdStr: String, isCompleted: Boolean) {
        val taskId = taskIdStr.toLongOrNull() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            learningTaskDao.updateTaskCompletion(taskId, isCompleted)
            loadLocalStatsAndTasks()

            // Dual-Sync: Call POST learning_plan.php?action=toggle
            try {
                val user = sessionManager?.getSessionUser()
                learningPlanApi.toggleLearningTask(
                    taskId = taskId,
                    isCompleted = if (isCompleted) 1 else 0,
                    userId = user?.id,
                    email = user?.email
                )
            } catch (e: Exception) {
                android.util.Log.e("DashboardViewModel", "Remote toggleLearningTask failed", e)
            }
        }
    }

    fun deleteLearningTask(taskIdStr: String) {
        val taskId = taskIdStr.toLongOrNull() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            learningTaskDao.deleteTask(taskId)
            loadLocalStatsAndTasks()

            // Dual-Sync: Call POST learning_plan.php?action=delete
            try {
                val user = sessionManager?.getSessionUser()
                learningPlanApi.deleteLearningTask(
                    taskId = taskId,
                    userId = user?.id,
                    email = user?.email
                )
            } catch (e: Exception) {
                android.util.Log.e("DashboardViewModel", "Remote deleteLearningTask failed", e)
            }
        }
    }

    fun loadDashboardData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            loadLocalStatsAndTasks()

            val manager = sessionManager ?: SessionManager.getInstanceOrNull()
            val user = manager?.getSessionUser()

            repository.getHomeDashboard(userId = user?.id, email = user?.email).fold(
                onSuccess = { data ->
                    val recTitle = data.brainMap?.recommendationTitle ?: "Start Track 1: Python & Coding Logic"
                    val recSub = data.brainMap?.recommendationSubtitle ?: "Begin your 2-minute daily concept lessons · 15 min"

                    _uiState.update { current ->
                        current.copy(
                            recommendedTitle = recTitle,
                            recommendedSubtitle = recSub
                        )
                    }
                },
                onFailure = {
                    // Local state remains pristine offline
                }
            )
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun onPlanItemClicked(itemId: String) {
        // Toggle plan item completion on click
        val item = _uiState.value.todayPlanItems.firstOrNull { it.id == itemId }
        if (item != null) {
            toggleTaskCompletion(itemId, !item.isCompleted)
        }
    }

    fun onAskAiTutorClicked() {
        // Handled in UI layer
    }

    fun retry() {
        loadDashboardData(forceRefresh = true)
    }

    fun refresh() {
        loadDashboardData(forceRefresh = true)
    }
}
