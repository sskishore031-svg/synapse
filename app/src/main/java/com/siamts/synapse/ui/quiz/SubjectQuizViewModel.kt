package com.siamts.synapse.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.CurriculumRepository
import com.siamts.synapse.data.repository.QuizRepository
import com.siamts.synapse.data.repository.QuizRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLDecoder

class SubjectQuizViewModel(
    private val quizRepository: QuizRepository = QuizRepositoryImpl(),
    private val curriculumRepo: CurriculumRepository = CurriculumRepository.getInstance(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubjectQuizUiState())
    val uiState: StateFlow<SubjectQuizUiState> = _uiState.asStateFlow()

    fun loadSubjectQuizzes(subjectId: Int) {
        loadSubjectQuizzes(subjectId, null)
    }

    fun loadSubjectQuizzes(subjectId: Int, subjectName: String?) {
        loadSubjectQuizzes(subjectId.toString(), subjectName)
    }

    fun loadSubjectQuizzes(subjectId: String, subjectName: String? = null) {
        val decodedId = try {
            URLDecoder.decode(subjectId, "UTF-8").trim()
        } catch (_: Throwable) {
            subjectId.trim()
        }
        val decodedName = subjectName?.let {
            try {
                URLDecoder.decode(it, "UTF-8").trim()
            } catch (_: Throwable) {
                it.trim()
            }
        }
        val numId = decodedId.toIntOrNull() ?: 1

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, subjectId = numId) }

            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val result = quizRepository.getSubjectQuizzes(
                subjectId = numId,
                subjectSlug = decodedName ?: decodedId,
                userId = userId,
                email = userEmail
            )

            result.onSuccess { response ->
                val data = response.data
                val quizzes = data?.quizzes.orEmpty()
                if (quizzes.isNotEmpty()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            subject = data?.subject,
                            quizzes = quizzes
                        )
                    }
                } else {
                    // Immediate local fallback to guarantee zero empty lists
                    val fallbackQuizzes = curriculumRepo.getSubjectQuizzes(decodedId, decodedName)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            quizzes = fallbackQuizzes
                        )
                    }
                }
            }.onFailure {
                // Immediate local fallback to guarantee zero empty lists
                val fallbackQuizzes = curriculumRepo.getSubjectQuizzes(decodedId, decodedName)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        quizzes = fallbackQuizzes
                    )
                }
            }
        }
    }
}

