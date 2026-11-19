package com.siamts.synapse.ui.quiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.SynapseApplication
import com.siamts.synapse.data.local.BaseCurriculumData
import com.siamts.synapse.data.local.QuizDao
import com.siamts.synapse.data.local.QuizLocalDatabase
import com.siamts.synapse.data.local.QuizRecordEntity
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.model.ExtendedCurriculumCatalog
import com.siamts.synapse.data.model.QuizQuestionDto
import com.siamts.synapse.data.model.QuizResultDataDto
import com.siamts.synapse.data.repository.QuizRepository
import com.siamts.synapse.data.repository.QuizRepositoryImpl
import com.siamts.synapse.data.repository.SubjectCatalogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

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
 * Guarantees zero blank white screens by extracting navigation arguments safely, checking Room DB,
 * and falling back to assets and curriculum catalogs immediately.
 */
class QuizViewModel(
    private val savedStateHandle: SavedStateHandle? = null,
    private val quizRepository: QuizRepository = QuizRepositoryImpl(),
    private val quizDao: QuizDao = QuizLocalDatabase.getInstance(),
    private val catalogRepo: SubjectCatalogRepository? = try {
        SubjectCatalogRepository.getInstance()
    } catch (_: Throwable) {
        null
    },
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

    init {
        val sId = savedStateHandle?.get<String>("subjectId") ?: savedStateHandle?.get<String>("subject_id").orEmpty()
        val cId = savedStateHandle?.get<String>("chapterId") ?: savedStateHandle?.get<String>("chapter_id").orEmpty()
        val chapInt = cId.toIntOrNull() ?: 0
        if (chapInt > 0 || sId.isNotBlank()) {
            loadChapterQuiz(chapInt, sId)
        }
    }

    fun loadChapterQuiz(chapterId: Int) {
        loadChapterQuiz(chapterId, currentSubjectId)
    }

    fun loadChapterQuiz(chapterId: Int, subjectId: String) {
        val decodedSub = try { java.net.URLDecoder.decode(subjectId.trim(), "UTF-8") } catch (_: Throwable) { subjectId.trim() }
        val normSub = if (decodedSub.isNotBlank()) com.siamts.synapse.data.repository.CurriculumRepository.normalizeSubjectId(decodedSub) else ""

        val foundSubject = if (normSub.isNotBlank()) {
            BaseCurriculumData.getSubjectById(normSub)
                ?: catalogRepo?.getSubjectById(normSub)
                ?: catalogRepo?.getAllSubjects()?.firstOrNull {
                    it.id.equals(decodedSub, ignoreCase = true) ||
                    it.name.equals(decodedSub, ignoreCase = true) ||
                    it.slug.equals(decodedSub, ignoreCase = true)
                }
        } else null

        val resolvedSub = when {
            foundSubject != null -> foundSubject.id
            normSub.isNotBlank() -> normSub
            else -> {
                val fromBase = BaseCurriculumData.subjects.firstOrNull { subj ->
                    subj.chapters.any { it.chapterId == chapterId }
                }?.id
                fromBase ?: catalogRepo?.getAllSubjects()?.firstOrNull { subj ->
                    subj.chapters.any { it.chapterId == chapterId }
                }?.id ?: "MATH"
            }
        }
        currentSubjectId = resolvedSub

        val isMistakeVault = chapterId == -999 || decodedSub.equals("MISTAKE_VAULT", ignoreCase = true)
        if (isMistakeVault) {
            scope.launch {
                val mistakes = quizDao.getAllMistakes()
                val vaultQuestions = mistakes.map { it.toQuizQuestionDto() }
                val fQ = vaultQuestions.firstOrNull() ?: QuizQuestionDto(
                    id = 1,
                    questionNumber = 1,
                    question = "All caught up! Zero missed questions remaining in your Mistake Vault.",
                    options = mapOf(
                        "A" to "Keep practicing quizzes to reinforce concepts.",
                        "B" to "Review previous chapters.",
                        "C" to "Well done!",
                        "D" to "All of the above"
                    ),
                    correctOption = "D",
                    explanation = "Your Mistake Vault automatically tracks questions you answer incorrectly and clears them once answered correctly."
                )
                val qList = if (vaultQuestions.isNotEmpty()) vaultQuestions else listOf(fQ)
                currentSubjectId = "MISTAKE_VAULT"
                currentChapterId = "-999"
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        chapterId = -999,
                        currentQuestionIndex = 1,
                        topicTag = "MISTAKE VAULT · RE-TEST",
                        title = "Mistake Vault Re-Test (${qList.size} Questions)",
                        questions = qList,
                        totalQuestions = qList.size,
                        questionText = qList.first().question,
                        options = buildOptionsFromDto(qList.first()),
                        selectedOptionId = null,
                        answersMap = emptyMap(),
                        submissionResult = null
                    )
                }
            }
            return
        }

        val isRevisionTest = chapterId <= 0 || chapterId == 9999
        val targetChapterId = if (isRevisionTest) {
            9999
        } else {
            chapterId
        }
        currentChapterId = targetChapterId.toString()

        val baseChapter = if (isRevisionTest) null else BaseCurriculumData.getChapterById(targetChapterId)
        val catChapter = if (baseChapter == null && !isRevisionTest) catalogRepo?.getChapterById(targetChapterId) else null
        val initialQuestions = when {
            isRevisionTest && foundSubject != null -> {
                val allMcqs = foundSubject.chapters.flatMap { it.mcqs }
                if (allMcqs.isNotEmpty()) allMcqs.take(50) else emptyList()
            }
            baseChapter != null && baseChapter.mcqs.isNotEmpty() -> baseChapter.mcqs
            catChapter != null && catChapter.mcqs.isNotEmpty() -> catChapter.mcqs
            else -> catalogRepo?.getChapterQuiz(targetChapterId).orEmpty()
        }

        val subjectTitle = foundSubject?.name ?: BaseCurriculumData.getSubjectById(currentSubjectId)?.name ?: currentSubjectId
        val chapterTitle = if (isRevisionTest) "Full Subject Revision Test" else (baseChapter?.title ?: catChapter?.title ?: "Chapter $targetChapterId")

        if (initialQuestions.isNotEmpty()) {
            val fQ = initialQuestions.first()
            val topicTag = if (isRevisionTest) "$subjectTitle · 50 QUESTIONS REVISION".uppercase() else "$subjectTitle QUIZ".uppercase()
            val quizTitle = if (isRevisionTest) "$subjectTitle Full Revision Test" else "$chapterTitle Quiz"
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = null,
                    chapterId = targetChapterId,
                    currentQuestionIndex = 1,
                    topicTag = topicTag,
                    title = quizTitle,
                    questions = initialQuestions,
                    totalQuestions = initialQuestions.size,
                    questionText = fQ.question,
                    options = buildOptionsFromDto(fQ),
                    selectedOptionId = null,
                    answersMap = emptyMap(),
                    submissionResult = null
                )
            }
        } else {
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
        }
        _userSelections.value = emptyMap()

        scope.launch(Dispatchers.IO) {
            // Seed base questions if present
            if (initialQuestions.isNotEmpty()) {
                try {
                    quizDao.insertQuestionsForChapter(currentSubjectId, currentChapterId, initialQuestions)
                } catch (_: Throwable) {}
            }

            // Step 1: If questions were not available synchronously, check Room DB & Fallbacks
            if (_uiState.value.questions.isEmpty()) {
                val roomQuestions = try {
                    quizDao.getQuestionsForChapter(currentSubjectId, currentChapterId)
                } catch (_: Throwable) {
                    emptyList()
                }

                var finalQuestions = roomQuestions
                var topicTag = "${currentSubjectId.uppercase()} QUIZ"
                var quizTitle = "Chapter $targetChapterId Quiz"

                // Step 2: Crucial Fallback if Room DB returns 0 questions
                if (finalQuestions.isEmpty()) {
                    // Check if Full Track Mock Quiz
                    val isFullTrackMock = targetChapterId <= 0 || targetChapterId == 999 || decodedSub.contains("MOCK", ignoreCase = true)
                    if (isFullTrackMock) {
                        val trackSub = BaseCurriculumData.getSubjectById(currentSubjectId)
                        val allTrackQuestions = trackSub?.chapters?.flatMap { it.mcqs } ?: emptyList()
                        if (allTrackQuestions.isNotEmpty()) {
                            finalQuestions = allTrackQuestions.shuffled().take(25)
                            topicTag = "${trackSub?.name ?: currentSubjectId} FULL TRACK MOCK".uppercase()
                            quizTitle = "${trackSub?.name ?: "Track"} 25Q Mock Exam"
                        }
                    }

                    // A. Check BaseCurriculumData
                    if (baseChapter != null && baseChapter.mcqs.isNotEmpty()) {
                        finalQuestions = baseChapter.mcqs
                        topicTag = "${BaseCurriculumData.getSubjectById(currentSubjectId)?.name ?: currentSubjectId} QUIZ".uppercase()
                        quizTitle = "${baseChapter.title} Quiz"
                    }

                    // B. Check SubjectCatalogRepository / extended_curriculum_catalog.json
                    if (finalQuestions.isEmpty()) {
                        val assetQuestions = loadQuestionsFromAssets(targetChapterId, currentSubjectId)
                        if (assetQuestions.isNotEmpty()) {
                            finalQuestions = assetQuestions
                            val catSub = catalogRepo?.getSubjectById(currentSubjectId)
                            val catChap = catalogRepo?.getChapterById(targetChapterId)
                            topicTag = "${catSub?.name ?: currentSubjectId} QUIZ".uppercase()
                            quizTitle = "${catChap?.title ?: "Chapter"} Quiz"
                        }
                    }

                    // C. Check catalogRepo directly
                    if (finalQuestions.isEmpty()) {
                        val catMcqs = catalogRepo?.getChapterQuiz(targetChapterId).orEmpty()
                        if (catMcqs.isNotEmpty()) {
                            finalQuestions = catMcqs
                            val catSub = catalogRepo?.getSubjectById(currentSubjectId)
                            val catChap = catalogRepo?.getChapterById(targetChapterId)
                            topicTag = "${catSub?.name ?: currentSubjectId} QUIZ".uppercase()
                            quizTitle = "${catChap?.title ?: "Chapter"} Quiz"
                        }
                    }

                    // D. Dynamic synthesis fallback so questions list is NEVER empty
                    if (finalQuestions.isEmpty()) {
                        val catSub = catalogRepo?.getSubjectById(currentSubjectId)
                        val catChap = catalogRepo?.getChapterById(targetChapterId)
                        val subName = catSub?.name ?: currentSubjectId
                        val chapName = catChap?.title ?: "Chapter $targetChapterId"
                        finalQuestions = generateFallbackQuestions(chapName, subName)
                        topicTag = "$subName Quiz".uppercase()
                        quizTitle = "$chapName Quiz"
                    }

                    // Seed questions into Room DB for instant subsequent retrieval
                    if (finalQuestions.isNotEmpty()) {
                        try {
                            quizDao.insertQuestionsForChapter(currentSubjectId, currentChapterId, finalQuestions)
                        } catch (_: Throwable) {}
                    }
                } else {
                    val catSub = catalogRepo?.getSubjectById(currentSubjectId)
                    val catChap = catalogRepo?.getChapterById(targetChapterId)
                    topicTag = "${catSub?.name ?: currentSubjectId} QUIZ".uppercase()
                    quizTitle = "${catChap?.title ?: "Chapter"} Quiz"
                }

                // Immediately emit questions to StateFlow
                if (finalQuestions.isNotEmpty()) {
                    val firstQ = finalQuestions.first()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            topicTag = topicTag,
                            title = quizTitle,
                            questions = finalQuestions,
                            totalQuestions = finalQuestions.size,
                            currentQuestionIndex = 1,
                            questionText = firstQ.question,
                            options = buildOptionsFromDto(firstQ),
                            selectedOptionId = null,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "No quiz questions available for this chapter."
                        )
                    }
                }
            }

            // Step 3: Non-blocking background server sync if online
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"
            try {
                val result = quizRepository.getQuizQuestions(targetChapterId, userId, userEmail)
                result.onSuccess { response ->
                    val remoteQuestions = response.data?.questions.orEmpty()
                    if (remoteQuestions.isNotEmpty()) {
                        quizDao.insertQuestionsForChapter(currentSubjectId, currentChapterId, remoteQuestions)
                        if (_uiState.value.questions.isEmpty()) {
                            val fQ = remoteQuestions.first()
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    questions = remoteQuestions,
                                    totalQuestions = remoteQuestions.size,
                                    questionText = fQ.question,
                                    options = buildOptionsFromDto(fQ),
                                    errorMessage = null
                                )
                            }
                        }
                    }
                }
            } catch (_: Throwable) {}
        }
    }

    private suspend fun loadQuestionsFromAssets(chapterId: Int, subjectId: String): List<QuizQuestionDto> = withContext(Dispatchers.IO) {
        val context = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null } ?: return@withContext emptyList()
        val gson = com.google.gson.Gson()

        // 1. Check extended_curriculum_catalog.json
        try {
            context.assets.open("extended_curriculum_catalog.json").use { stream ->
                InputStreamReader(stream, Charsets.UTF_8).use { reader ->
                    val catalog = gson.fromJson(reader, ExtendedCurriculumCatalog::class.java)
                    for (subj in catalog.subjects) {
                        for (ch in subj.chapters) {
                            if (ch.chapterId == chapterId || (ch.chapterOrder == chapterId && subj.id.equals(subjectId, true))) {
                                if (ch.mcqs.isNotEmpty()) {
                                    return@withContext ch.mcqs
                                }
                            }
                        }
                    }
                }
            }
        } catch (_: Throwable) {}

        // 2. Check educational_knowledge_base.json
        try {
            context.assets.open("educational_knowledge_base.json").use { stream ->
                InputStreamReader(stream, Charsets.UTF_8).use { reader ->
                    val jsonElement = com.google.gson.JsonParser.parseReader(reader)
                    if (jsonElement.isJsonObject) {
                        val subjectsArray = jsonElement.asJsonObject.getAsJsonArray("subjects")
                        if (subjectsArray != null) {
                            for (sElem in subjectsArray) {
                                val sObj = sElem.asJsonObject
                                val chaps = sObj.getAsJsonArray("chapters")
                                if (chaps != null) {
                                    for (cElem in chaps) {
                                        val cObj = cElem.asJsonObject
                                        val cId = cObj.get("chapter_id")?.asInt ?: 0
                                        if (cId == chapterId) {
                                            val mcqs = cObj.getAsJsonArray("mcqs")
                                            if (mcqs != null && mcqs.size() > 0) {
                                                val typeToken = object : com.google.gson.reflect.TypeToken<List<QuizQuestionDto>>() {}.type
                                                val list: List<QuizQuestionDto> = gson.fromJson(mcqs, typeToken)
                                                if (list.isNotEmpty()) return@withContext list
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (_: Throwable) {}

        emptyList()
    }

    private fun generateFallbackQuestions(chapterTitle: String, subjectName: String): List<QuizQuestionDto> {
        return listOf(
            QuizQuestionDto(
                id = 1,
                questionNumber = 1,
                question = "What is the primary objective of studying $chapterTitle in $subjectName?",
                options = mapOf(
                    "A" to "To understand fundamental core principles and mechanisms.",
                    "B" to "To memorize arbitrary trivia without practical usage.",
                    "C" to "To bypass quantitative conceptual models completely.",
                    "D" to "None of the above."
                ),
                correctOption = "A",
                explanation = "$chapterTitle establishes essential conceptual principles and methods within $subjectName."
            ),
            QuizQuestionDto(
                id = 2,
                questionNumber = 2,
                question = "Which foundational principle governs the core workflow in $chapterTitle?",
                options = mapOf(
                    "A" to "Random trial and error without structure.",
                    "B" to "Systematic validation and domain-specific theoretical models.",
                    "C" to "Ignoring boundary conditions and assumptions.",
                    "D" to "Static unchanging parameters across all environments."
                ),
                correctOption = "B",
                explanation = "Systematic analysis and theoretical models ensure robust problem solving in $chapterTitle."
            ),
            QuizQuestionDto(
                id = 3,
                questionNumber = 3,
                question = "In the context of $chapterTitle, what role do standard metrics play?",
                options = mapOf(
                    "A" to "They have no diagnostic or comparative utility.",
                    "B" to "They provide quantitative benchmarks to assess performance and accuracy.",
                    "C" to "They increase ambiguity and decrease predictability.",
                    "D" to "They replace conceptual reasoning entirely."
                ),
                correctOption = "B",
                explanation = "Quantitative benchmarks enable accurate performance measurement and evaluation."
            ),
            QuizQuestionDto(
                id = 4,
                questionNumber = 4,
                question = "When applying concepts from $chapterTitle, what is the best practice for edge cases?",
                options = mapOf(
                    "A" to "Discard edge cases and assume ideal conditions.",
                    "B" to "Rigorously identify assumptions, constraints, and boundary limits.",
                    "C" to "Rely exclusively on subjective intuition.",
                    "D" to "Avoid testing with non-standard inputs."
                ),
                correctOption = "B",
                explanation = "Rigorous identification of constraints and boundaries prevents failure at edge cases."
            ),
            QuizQuestionDto(
                id = 5,
                questionNumber = 5,
                question = "What is the key takeaway when mastering $chapterTitle?",
                options = mapOf(
                    "A" to "Synthesizing theoretical foundations with hands-on practice.",
                    "B" to "Only focusing on high-level definitions without depth.",
                    "C" to "Skipping prerequisite concepts to speed up study.",
                    "D" to "Treating the subject in complete isolation from $subjectName."
                ),
                correctOption = "A",
                explanation = "Mastery requires combining conceptual understanding with problem-solving practice."
            )
        )
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
            val isCorrect = selectedOption != null && selectedOption.equals(q.correctOption, ignoreCase = true)
            if (isCorrect) {
                correctAnswersCount++
                scope.launch {
                    try {
                        quizDao.removeMistake(q.question)
                    } catch (_: Throwable) {}
                }
            } else {
                scope.launch {
                    try {
                        val mistake = com.siamts.synapse.data.local.MistakeQuestionEntity(
                            subjectId = subId,
                            chapterId = chapIdStr,
                            questionText = q.question,
                            optionA = q.options["A"].orEmpty(),
                            optionB = q.options["B"].orEmpty(),
                            optionC = q.options["C"].orEmpty(),
                            optionD = q.options["D"].orEmpty(),
                            correctOption = q.correctOption,
                            explanation = q.explanation
                        )
                        quizDao.insertMistake(mistake)
                    } catch (_: Throwable) {}
                }
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

                // Real-Time Milestone Notification when score >= 80%
                if (scorePercentage >= 80f) {
                    try {
                        val appContext = SynapseApplication.getAppContext()
                        com.siamts.synapse.notification.SynapseNotificationHelper.showQuizMilestoneNotification(
                            context = appContext,
                            subjectTitle = state.topicTag.ifBlank { "Subject" },
                            chapterTitle = state.title.ifBlank { "Chapter $chapIdStr" },
                            score = correctAnswersCount,
                            total = totalQuestions
                        )
                    } catch (_: Throwable) {}
                }

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

                quizRepository.submitQuizResult(
                    chapterId = targetChapterId,
                    subjectId = subId,
                    score = correctAnswersCount,
                    totalQuestions = totalQuestions,
                    userId = userId,
                    email = userEmail
                )
            } catch (_: Throwable) {}
        }
    }

    fun loadSubjectQuiz(subjectName: String) {
        val trimmed = subjectName.trim()
        val decoded = try { java.net.URLDecoder.decode(trimmed, "UTF-8") } catch (_: Throwable) { trimmed }
        val normId = com.siamts.synapse.data.repository.CurriculumRepository.normalizeSubjectId(decoded)

        val foundSubject = BaseCurriculumData.getSubjectById(normId)
            ?: catalogRepo?.getSubjectById(normId)
            ?: catalogRepo?.getAllSubjects()?.firstOrNull {
                it.id.equals(decoded, ignoreCase = true) ||
                it.name.equals(decoded, ignoreCase = true) ||
                it.slug.equals(decoded, ignoreCase = true)
            }

        val resolvedSubId = foundSubject?.id ?: normId
        val targetChapterId = foundSubject?.chapters?.firstOrNull()?.chapterId ?: when (resolvedSubId) {
            "MATH" -> 4
            "CS" -> 8
            "DS" -> 13
            "AI" -> 18
            "PHY" -> 23
            "CHEM" -> 28
            "ext_bio" -> 1011
            else -> 4
        }
        loadChapterQuiz(targetChapterId, resolvedSubId)
    }

    fun onPreviousClicked() = previousQuestion()
    fun onNextClicked() = nextQuestion()
    fun onSubmitClicked(onSubmitted: ((QuizResultDataDto) -> Unit)? = null) = submitQuiz(onSubmitted)
}
