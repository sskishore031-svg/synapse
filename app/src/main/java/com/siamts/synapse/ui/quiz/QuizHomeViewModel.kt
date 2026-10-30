package com.siamts.synapse.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                val totalAvail = list.sumOf { it.availableQuizzes }
                val totalAtt = list.sumOf { it.attemptedQuizzes }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        subjects = list,
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

