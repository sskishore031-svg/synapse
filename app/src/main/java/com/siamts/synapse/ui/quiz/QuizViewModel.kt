package com.siamts.synapse.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.BaseCurriculumData
import com.siamts.synapse.data.local.QuizDao
import com.siamts.synapse.data.local.QuizLocalDatabase
import com.siamts.synapse.data.local.QuizRecordEntity
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.model.QuizQuestionDto
import com.siamts.synapse.data.model.QuizResultDataDto
import com.siamts.synapse.data.repository.QuizRepository
import com.siamts.synapse.data.repository.QuizRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Navigation event emitted upon quiz completion for clean 1-time navigation to result screen.
 */
sealed class QuizNavEvent {
    data class NavigateToResult(
        val subjectId: String,
        val chapterId: String,
        val score: Int,
        val total: Int,
        val resultData: QuizResultDataDto? = null
    ) : QuizNavEvent()
}

/**
 * ViewModel managing Quiz questions, answer selection, local grading, Room record persistence, and navigation.
 */
class QuizViewModel(
    private val quizRepository: QuizRepository = QuizRepositoryImpl(),
    private val quizDao: QuizDao = QuizLocalDatabase.getInstance(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull(),
    private val externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope: CoroutineScope get() = externalScope ?: viewModelScope

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    // Question index (0-based) to selected option index (0..3)
    private val _userSelections = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val userSelections: StateFlow<Map<Int, Int>> = _userSelections.asStateFlow()

    // Current subject and chapter identifiers
    var currentSubjectId: String = "MATH"
        private set
    var currentChapterId: String = "4"
        private set

    // One-time navigation event
    private val _navEvent = MutableSharedFlow<QuizNavEvent>(replay = 0, extraBufferCapacity = 1)
    val navEvent: SharedFlow<QuizNavEvent> = _navEvent.asSharedFlow()

    fun loadChapterQuiz(chapterId: Int) {
        val targetChapterId = if (chapterId > 0) chapterId else 4
        currentChapterId = targetChapterId.toString()

        val parentSub = BaseCurriculumData.subjects.firstOrNull { subj ->
            subj.chapters.any { it.chapterId == targetChapterId }
        }
        currentSubjectId = parentSub?.id ?: "MATH"

        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                chapterId = targetChapterId,
                currentQuestionIndex = 1,
                answersMap = emptyMap(),
                submissionResult = null
            )
        }
        _userSelections.value = emptyMap()

        // 1. Immediately populate from local curriculum questions for 0ms offline display
        val localChapter = BaseCurriculumData.getChapterById(targetChapterId)
        val localQuestions = localChapter?.mcqs.orEmpty()
        if (localQuestions.isNotEmpty()) {
            val firstQ = localQuestions.first()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    topicTag = "${parentSub?.name ?: "CHAPTER"} QUIZ".uppercase(),
                    title = "${localChapter?.title ?: "Chapter"} Quiz",
                    questions = localQuestions,
                    totalQuestions = localQuestions.size,
                    currentQuestionIndex = 1,
                    questionText = firstQ.question,
                    options = buildOptionsFromDto(firstQ),
                    selectedOptionId = null
                )
            }
        }

        // 2. Refresh from backend in background
        scope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val result = quizRepository.getQuizQuestions(
                chapterId = targetChapterId,
                userId = userId,
                email = userEmail
            )

            result.onSuccess { response ->
                val data = response.data
                val quizInfo = data?.quiz
                val questionList = data?.questions.orEmpty()

                if (questionList.isNotEmpty()) {
                    val firstQ = questionList.firstOrNull()
                    val initialOptions = buildOptionsFromDto(firstQ)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            topicTag = quizInfo?.title?.uppercase() ?: "${parentSub?.name ?: "CHAPTER"} QUIZ".uppercase(),
                            title = quizInfo?.title ?: "${localChapter?.title ?: "Chapter"} Quiz",
                            questions = questionList,
                            totalQuestions = questionList.size.coerceAtLeast(1),
                            currentQuestionIndex = 1,
                            questionText = firstQ?.question.orEmpty(),
                            options = initialOptions,
                            selectedOptionId = null
                        )
                    }
                }
            }.onFailure { err ->
                if (localQuestions.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = err.localizedMessage ?: "Failed to load quiz questions"
                        )
                    }
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

        val optionIndex = when (optionId.uppercase()) {
            "A" -> 0
            "B" -> 1
            "C" -> 2
            "D" -> 3
            else -> 0
        }

        val newSelections = _userSelections.value.toMutableMap()
        newSelections[currIdx] = optionIndex
        _userSelections.value = newSelections

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
            val nextIdx = state.currentQuestionIndex
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
        val targetChapterId = if (state.chapterId > 0) state.chapterId else 4
        val subId = currentSubjectId.ifEmpty { "MATH" }
        val chapIdStr = if (currentChapterId.isNotBlank() && currentChapterId != "0") currentChapterId else targetChapterId.toString()

        val questions = state.questions
        val totalQuestions = questions.size.coerceAtLeast(1)
        var correctAnswersCount = 0

        questions.forEachIndexed { index, q ->
            val selectedOption = state.answersMap[q.id]
                ?: _userSelections.value[index]?.let { idx -> listOf("A", "B", "C", "D").getOrNull(idx) }
            if (selectedOption != null && selectedOption.equals(q.correctOption, ignoreCase = true)) {
                correctAnswersCount++
            }
        }

        val incorrectAnswersCount = (totalQuestions - correctAnswersCount).coerceAtLeast(0)
        val scorePercentage: Float = (correctAnswersCount.toFloat() / totalQuestions.toFloat()) * 100f

        val resultDto = QuizResultDataDto(
            studentName = "SYNAPSE Scholar",
            subjectName = state.topicTag.ifBlank { "Mathematics" },
            quizTitle = state.title.ifBlank { "Chapter Quiz" },
            chapterId = targetChapterId,
            score = correctAnswersCount,
            totalMarks = totalQuestions * 10,
            percentage = scorePercentage.toInt(),
            correctAnswers = correctAnswersCount,
            incorrectAnswers = incorrectAnswersCount,
            totalQuestions = totalQuestions,
            attemptDate = "Today",
            passed = scorePercentage >= 60f
        )

        // 1. Save to Room / local SQLite 'quiz_records' table
        scope.launch {
            try {
                val user = sessionManager?.getSessionUser()
                val updatedDto = if (user != null && user.name.isNotBlank()) {
                    resultDto.copy(studentName = user.name)
                } else resultDto

                val record = QuizRecordEntity(
                    subjectId = subId,
                    chapterId = chapIdStr,
                    score = correctAnswersCount,
                    totalQuestions = totalQuestions,
                    completedAt = System.currentTimeMillis()
                )
                quizDao.insertQuizRecord(record)

                // 2. Expose One-Time Navigation Event
                _navEvent.emit(
                    QuizNavEvent.NavigateToResult(
                        subjectId = subId,
                        chapterId = chapIdStr,
                        score = correctAnswersCount,
                        total = totalQuestions,
                        resultData = updatedDto
                    )
                )
            } catch (_: Throwable) {
                // Ensure local db error never blocks quiz completion
                _navEvent.emit(
                    QuizNavEvent.NavigateToResult(
                        subjectId = subId,
                        chapterId = chapIdStr,
                        score = correctAnswersCount,
                        total = totalQuestions,
                        resultData = resultDto
                    )
                )
            }
        }

        _uiState.update {
            it.copy(
                isSubmitting = false,
                submissionResult = resultDto
            )
        }

        // 3. Immediately trigger navigation callback
        onSubmitted?.invoke(resultDto)

        // 4. Fire-and-forget sync to backend in background
        scope.launch {
            try {
                val user = sessionManager?.getSessionUser()
                val userId = user?.id ?: 1
                val userEmail = user?.email ?: "student@synapse.ai"
                val stringAnswers = state.answersMap.mapKeys { it.key.toString() }

                quizRepository.submitQuiz(
                    chapterId = targetChapterId,
                    answers = stringAnswers,
                    userId = userId,
                    email = userEmail
                )
            } catch (_: Throwable) {}
        }
    }

    fun loadSubjectQuiz(subjectName: String) {
        val trimmed = subjectName.trim().lowercase(java.util.Locale.ROOT)
        currentSubjectId = when (trimmed) {
            "mathematics", "math", "1" -> "MATH"
            "computer science", "cs", "2" -> "CS"
            "data science", "ds", "3" -> "DS"
            "artificial intelligence", "ai", "4" -> "AI"
            "physics", "phy", "5" -> "PHY"
            "chemistry", "chem", "6" -> "CHEM"
            "biology", "bio", "101" -> "ext_bio"
            else -> "MATH"
        }

        val mappedChapterId = when (currentSubjectId) {
            "MATH" -> 4
            "CS" -> 8
            "DS" -> 13
            "AI" -> 18
            "PHY" -> 23
            "CHEM" -> 28
            "ext_bio" -> 1011
            else -> 4
        }
        loadChapterQuiz(mappedChapterId)
    }

    fun onPreviousClicked() = previousQuestion()
    fun onNextClicked() = nextQuestion()
    fun onSubmitClicked(onSubmitted: ((QuizResultDataDto) -> Unit)? = null) = submitQuiz(onSubmitted)
}
