package com.siamts.synapse.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.model.QuizQuestionDto
import com.siamts.synapse.data.model.QuizResultDataDto
import com.siamts.synapse.data.repository.QuizRepository
import com.siamts.synapse.data.repository.QuizRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing Quiz questions, answer selection, and real backend submission.
 */
class QuizViewModel(
    private val quizRepository: QuizRepository = QuizRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull()
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun loadChapterQuiz(chapterId: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    chapterId = chapterId,
                    currentQuestionIndex = 1,
                    answersMap = emptyMap(),
                    submissionResult = null
                )
            }

            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val result = quizRepository.getQuizQuestions(
                chapterId = chapterId,
                userId = userId,
                email = userEmail
            )

            result.onSuccess { response ->
                val data = response.data
                val quizInfo = data?.quiz
                val questionList = data?.questions.orEmpty()

                val firstQ = questionList.firstOrNull()
                val initialOptions = buildOptionsFromDto(firstQ)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        topicTag = quizInfo?.title?.uppercase() ?: "CHAPTER QUIZ",
                        title = quizInfo?.title ?: "Test your understanding",
                        questions = questionList,
                        totalQuestions = questionList.size.coerceAtLeast(1),
                        currentQuestionIndex = 1,
                        questionText = firstQ?.question.orEmpty(),
                        options = initialOptions,
                        selectedOptionId = null
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = err.localizedMessage ?: "Failed to load quiz questions"
                    )
                }
            }
        }
    }

    private fun buildOptionsFromDto(q: QuizQuestionDto?): List<QuizOption> {
        if (q == null) return emptyList()
        return listOf("A", "B", "C", "D").mapNotNull { key ->
            val text = q.options[key]
            if (text != null) {
                QuizOption(
                    id = key,
                    optionLabel = key,
                    text = text,
                    isCorrect = key == q.correctOption
                )
            } else null
        }
    }

    fun selectOption(optionId: String) {
        val currIdx = _uiState.value.currentQuestionIndex - 1
        val questions = _uiState.value.questions
        val currentQ = questions.getOrNull(currIdx)

        val newAnswers = _uiState.value.answersMap.toMutableMap()
        if (currentQ != null) {
            newAnswers[currentQ.id] = optionId
        }

        _uiState.update {
            it.copy(
                selectedOptionId = optionId,
                answersMap = newAnswers
            )
        }
    }

    fun onOptionSelected(optionId: String) = selectOption(optionId)

    fun nextQuestion() {
        val state = _uiState.value
        if (state.currentQuestionIndex < state.totalQuestions) {
            val nextIdx = state.currentQuestionIndex // 0-based is currentQuestionIndex
            val nextQ = state.questions.getOrNull(nextIdx)
            val savedAnswer = if (nextQ != null) state.answersMap[nextQ.id] else null

            _uiState.update {
                it.copy(
                    currentQuestionIndex = state.currentQuestionIndex + 1,
                    questionText = nextQ?.question.orEmpty(),
                    options = buildOptionsFromDto(nextQ),
                    selectedOptionId = savedAnswer
                )
            }
        }
    }

    fun previousQuestion() {
        val state = _uiState.value
        if (state.currentQuestionIndex > 1) {
            val prevIdx = state.currentQuestionIndex - 2
            val prevQ = state.questions.getOrNull(prevIdx)
            val savedAnswer = if (prevQ != null) state.answersMap[prevQ.id] else null

            _uiState.update {
                it.copy(
                    currentQuestionIndex = state.currentQuestionIndex - 1,
                    questionText = prevQ?.question.orEmpty(),
                    options = buildOptionsFromDto(prevQ),
                    selectedOptionId = savedAnswer
                )
            }
        }
    }

    fun submitQuiz(onSubmitted: ((QuizResultDataDto) -> Unit)? = null) {
        val state = _uiState.value
        val chapterId = state.chapterId
        if (chapterId <= 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }

            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val stringAnswers = state.answersMap.mapKeys { it.key.toString() }

            val result = quizRepository.submitQuiz(
                chapterId = chapterId,
                answers = stringAnswers,
                userId = userId,
                email = userEmail
            )

            result.onSuccess { response ->
                val resData = response.data
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        submissionResult = resData
                    )
                }
                if (resData != null) {
                    onSubmitted?.invoke(resData)
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = err.localizedMessage ?: "Failed to submit quiz"
                    )
                }
            }
        }
    }

    // Compatibility method
    fun loadSubjectQuiz(subjectName: String) {
        val mappedChapterId = when (subjectName) {
            "Mathematics" -> 4
            "Computer Science" -> 8
            "Data Science" -> 13
            "Artificial Intelligence" -> 18
            "Physics" -> 23
            "Chemistry" -> 28
            else -> 18
        }
        loadChapterQuiz(mappedChapterId)
    }

    fun onPreviousClicked() = previousQuestion()
    fun onNextClicked() = nextQuestion()
    fun onSubmitClicked(onSubmitted: ((QuizResultDataDto) -> Unit)? = null) = submitQuiz(onSubmitted)
}
