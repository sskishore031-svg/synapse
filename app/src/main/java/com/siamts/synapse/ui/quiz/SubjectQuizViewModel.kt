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

class SubjectQuizViewModel(
    private val quizRepository: QuizRepository = QuizRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubjectQuizUiState())
    val uiState: StateFlow<SubjectQuizUiState> = _uiState.asStateFlow()

    fun loadSubjectQuizzes(subjectId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, subjectId = subjectId) }

            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val result = quizRepository.getSubjectQuizzes(
                subjectId = subjectId,
                userId = userId,
                email = userEmail
            )

            result.onSuccess { response ->
                val data = response.data
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        subject = data?.subject,
                        quizzes = data?.quizzes.orEmpty()
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = err.localizedMessage ?: "Failed to load quizzes"
                    )
                }
            }
        }
    }
}

