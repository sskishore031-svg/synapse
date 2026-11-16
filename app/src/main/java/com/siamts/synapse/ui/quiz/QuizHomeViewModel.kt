package com.siamts.synapse.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.QuizDao
import com.siamts.synapse.data.local.QuizLocalDatabase
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.QuizRepository
import com.siamts.synapse.data.repository.QuizRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizHomeViewModel(
    private val quizRepository: QuizRepository = QuizRepositoryImpl(),
    private val quizDao: QuizDao = QuizLocalDatabase.getInstance(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull()
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizHomeUiState())
    val uiState: StateFlow<QuizHomeUiState> = _uiState.asStateFlow()

    init {
        loadSubjects()
    }

    fun loadSubjects() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val result = quizRepository.getQuizSubjects(userId = userId, email = userEmail)

            result.onSuccess { response ->
                val list = response.data?.subjects.orEmpty()
                val localRecords = try { quizDao.getAllRecords() } catch (_: Throwable) { emptyList() }

                val enrichedList = list.map { sub ->
                    val subRecords = localRecords.filter { rec ->
                        rec.subjectId.equals(sub.id.toString(), ignoreCase = true) ||
                        rec.subjectId.equals(sub.slug, ignoreCase = true) ||
                        rec.subjectId.equals(sub.name, ignoreCase = true)
                    }
                    if (subRecords.isNotEmpty()) {
                        val distinctChapters = subRecords.map { it.chapterId }.distinct().size
                        val avgScore = subRecords.map { (it.score * 100) / it.totalQuestions.coerceAtLeast(1) }.average().toInt()
                        sub.copy(
                            attemptedQuizzes = distinctChapters.coerceAtLeast(sub.attemptedQuizzes),
                            averageScore = if (avgScore > 0) avgScore else sub.averageScore
                        )
                    } else {
                        sub
                    }
                }

                val totalAvail = enrichedList.sumOf { it.availableQuizzes }
                val totalAtt = enrichedList.sumOf { it.attemptedQuizzes }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        subjects = enrichedList,
                        totalQuizzes = if (totalAvail > 0) totalAvail else 30,
                        totalAttempted = totalAtt
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = err.localizedMessage ?: "Failed to load quiz subjects"
                    )
                }
            }
        }
    }
}

