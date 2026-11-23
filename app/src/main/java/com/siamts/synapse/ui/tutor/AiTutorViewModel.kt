package com.siamts.synapse.ui.tutor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.SynapseApplication
import com.siamts.synapse.ai.LocalAIEngine
import com.siamts.synapse.ai.LocalAiStreamToken
import com.siamts.synapse.ai.SlmInferenceEngine
import com.siamts.synapse.ai.VoiceAssistantManager
import com.siamts.synapse.data.local.AiChatStorage
import com.siamts.synapse.data.local.BaseCurriculumData
import com.siamts.synapse.data.local.ChatSessionEntity
import com.siamts.synapse.data.local.LearningTaskEntity
import com.siamts.synapse.data.local.MistakeQuestionEntity
import com.siamts.synapse.data.local.NoteEntity
import com.siamts.synapse.data.local.QuizRecordEntity
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.local.SynapseDatabase
import com.siamts.synapse.data.model.QuizQuestionDto
import com.siamts.synapse.data.repository.AiTutorRepository
import com.siamts.synapse.data.repository.AiTutorRepositoryImpl
import com.siamts.synapse.data.repository.NotesRepository
import com.siamts.synapse.data.repository.NotesRepositoryImpl
import com.siamts.synapse.data.repository.SubjectCatalogRepository
import com.siamts.synapse.data.repository.UserPreferencesRepository
import com.siamts.synapse.notification.StudyReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

/**
 * ViewModel managing SYNAPSE AI Tutor chat state, multi-turn conversation memory,
 * local session persistence, real-time on-device LocalAIEngine reasoning, progressive
 * Kotlin Flow token streaming, agentic task execution, and two-way Live Voice conversation.
 */
class AiTutorViewModel(
    private val repository: AiTutorRepository = AiTutorRepositoryImpl(),
    private val notesRepository: NotesRepository = NotesRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull(),
    private val userPreferencesRepository: UserPreferencesRepository? = UserPreferencesRepository.getInstanceOrNull(),
    private val localAiEngine: LocalAIEngine = LocalAIEngine.getInstance(),
    private val chatStorage: AiChatStorage? = try { AiChatStorage.getInstance() } catch (_: Exception) { null },
    private val slmEngine: SlmInferenceEngine? = try { SlmInferenceEngine.getInstance() } catch (_: Throwable) { null },
    private val externalScope: kotlinx.coroutines.CoroutineScope? = null
) : ViewModel() {

    private val scope: kotlinx.coroutines.CoroutineScope get() = externalScope ?: viewModelScope

    private val _uiState = MutableStateFlow(AiTutorUiState())
    val uiState: StateFlow<AiTutorUiState> = _uiState.asStateFlow()

    private val _actionEvent = MutableSharedFlow<AiUiAction>()
    val actionEvent: SharedFlow<AiUiAction> = _actionEvent.asSharedFlow()

    private var currentUserId: Int = 1
    private var currentSessionId: String = UUID.randomUUID().toString()
    private var streamingJob: Job? = null
    private var voiceAssistant: VoiceAssistantManager? = null

    // Dynamic length control tracking (lastTopic, lastTrack & lastResponseText)
    var lastTopic: String = ""
        private set
    var lastTrack: String = "Python & Coding Logic"
        private set
    var lastResponseText: String = ""
        private set

    fun sendMessage(prompt: String) {
        onSendMessage(prompt)
    }

    init {
        loadHistory()
    }

    fun loadHistory() {
        scope.launch {
            val user = sessionManager?.getSessionUser()
            currentUserId = user?.id ?: 1
            val email = user?.email ?: "student@synapse.ai"

            // Sync user preferences with localAiEngine
            val accountName = user?.name?.trim().orEmpty()
            val savedPrefName = userPreferencesRepository?.getChatPreferredName().orEmpty()
            val activeName = savedPrefName.ifBlank { accountName }
            localAiEngine.accountUserName = accountName
            localAiEngine.preferredName = activeName
            localAiEngine.includeFormulas = userPreferencesRepository?.getIncludeFormulas() ?: true
            localAiEngine.includeExamples = userPreferencesRepository?.getIncludeExamples() ?: true
            localAiEngine.useEmojis = userPreferencesRepository?.getUseEmojis() ?: false

            val headerTag = if (activeName.isNotBlank()) {
                "PERSONALIZED FOR ${activeName.uppercase()}"
            } else {
                "SYNAPSE AI NEURAL CORE"
            }
            _uiState.update { it.copy(headerTag = headerTag) }

            // 1. Check persistent SQLite chat sessions
            val savedSessions = chatStorage?.loadSessions(currentUserId).orEmpty()
            if (savedSessions.isNotEmpty()) {
                val latestSession = savedSessions.first()
                currentSessionId = latestSession.sessionId
                val sessionMessages = chatStorage?.loadSessionMessages(currentSessionId).orEmpty()
                val lastUser = sessionMessages.lastOrNull { it.isUser }
                val lastAi = sessionMessages.lastOrNull { !it.isUser }

                _uiState.update {
                    it.copy(
                        currentSessionId = currentSessionId,
                        chatSessions = savedSessions,
                        messages = sessionMessages,
                        userQuestion = lastUser?.text ?: it.userQuestion,
                        aiAnswerParagraph1 = lastAi?.text ?: it.aiAnswerParagraph1,
                        aiAnswerParagraph2 = lastAi?.paragraph2 ?: it.aiAnswerParagraph2,
                        aiThoughtStatus = "✨ SYNAPSE AI · Ready to continue your conversation"
                    )
                }
                return@launch
            }

            // 2. Check legacy local storage and migrate to initial session if present
            val legacySavedMessages = chatStorage?.loadMessages(currentUserId).orEmpty()
            if (legacySavedMessages.isNotEmpty()) {
                val firstTitle = legacySavedMessages.firstOrNull { it.isUser }?.text?.take(35) ?: "Study Session"
                val initialSession = ChatSessionEntity(
                    sessionId = currentSessionId,
                    userId = currentUserId,
                    title = firstTitle,
                    updatedAt = System.currentTimeMillis()
                )
                chatStorage?.saveSession(initialSession, legacySavedMessages)
                val lastUser = legacySavedMessages.lastOrNull { it.isUser }
                val lastAi = legacySavedMessages.lastOrNull { !it.isUser }

                _uiState.update {
                    it.copy(
                        currentSessionId = currentSessionId,
                        chatSessions = listOf(initialSession),
                        messages = legacySavedMessages,
                        userQuestion = lastUser?.text ?: it.userQuestion,
                        aiAnswerParagraph1 = lastAi?.text ?: it.aiAnswerParagraph1,
                        aiAnswerParagraph2 = lastAi?.paragraph2 ?: it.aiAnswerParagraph2,
                        aiThoughtStatus = "✨ SYNAPSE AI · Ready to continue your conversation"
                    )
                }
                return@launch
            }

            // 3. Query remote backend history; if empty, leave empty so minimal welcome emblem displays cleanly
            val historyResult = repository.getHistory(userId = currentUserId, email = email)
            historyResult.onSuccess { response ->
                val msgs = response.data?.messages.orEmpty()
                if (msgs.isNotEmpty()) {
                    val converted = msgs.map { dto ->
                        val parts = dto.message.split("\n\n")
                        ChatMessage(
                            id = dto.id.toString(),
                            senderName = if (dto.senderType == "user") "You" else "SYNAPSE AI",
                            text = parts.getOrNull(0) ?: dto.message,
                            paragraph2 = if (parts.size > 1) parts.drop(1).joinToString("\n\n") else null,
                            timeAgo = "Saved",
                            isUser = dto.senderType == "user"
                        )
                    }
                    val initialSession = ChatSessionEntity(
                        sessionId = currentSessionId,
                        userId = currentUserId,
                        title = converted.firstOrNull { it.isUser }?.text?.take(35) ?: "Study Session",
                        updatedAt = System.currentTimeMillis()
                    )
                    chatStorage?.saveSession(initialSession, converted)
                    _uiState.update {
                        it.copy(
                            currentSessionId = currentSessionId,
                            chatSessions = listOf(initialSession),
                            messages = converted
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            currentSessionId = currentSessionId,
                            chatSessions = emptyList<ChatSessionEntity>(),
                            messages = emptyList<ChatMessage>()
                        )
                    }
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        currentSessionId = currentSessionId,
                        chatSessions = emptyList<ChatSessionEntity>(),
                        messages = emptyList<ChatMessage>()
                    )
                }
            }
        }
    }

    private fun persistCurrentSession(messages: List<ChatMessage>) {
        if (messages.isEmpty()) return
        val rawTitle = messages.firstOrNull { it.isUser }?.text?.take(35) ?: "Study Session"
        val session = ChatSessionEntity(
            sessionId = currentSessionId,
            userId = currentUserId,
            title = rawTitle,
            updatedAt = System.currentTimeMillis()
        )
        scope.launch(Dispatchers.IO) {
            chatStorage?.saveSession(session, messages)
            val updatedSessions = chatStorage?.loadSessions(currentUserId).orEmpty()
            _uiState.update { it.copy(chatSessions = updatedSessions) }
        }
    }

    /**
     * Starts a fresh empty conversation session with a new UUID without deleting past chats.
     */
    fun onNewChat() {
        streamingJob?.cancel()
        localAiEngine.resetContext()
        voiceAssistant?.stopSpeaking()
        currentSessionId = UUID.randomUUID().toString()
        _uiState.update {
            it.copy(
                currentSessionId = currentSessionId,
                messages = emptyList(),
                inputQuery = "",
                userQuestion = "",
                aiAnswerParagraph1 = "",
                aiAnswerParagraph2 = "",
                aiThoughtStatus = "✨ SYNAPSE AI · New session started. Ready for your questions!",
                isGenerating = false,
                currentlySpeakingMessageId = null,
                isHistorySheetVisible = false
            )
        }
    }

    /**
     * Restores full past message history of a selected session into the chat canvas.
     */
    fun onSelectSession(sessionId: String) {
        if (sessionId == currentSessionId && _uiState.value.messages.isNotEmpty()) {
            _uiState.update { it.copy(isHistorySheetVisible = false) }
            return
        }
        streamingJob?.cancel()
        voiceAssistant?.stopSpeaking()
        currentSessionId = sessionId
        scope.launch {
            val sessionMsgs = chatStorage?.loadSessionMessages(sessionId).orEmpty()
            val lastUser = sessionMsgs.lastOrNull { it.isUser }
            val lastAi = sessionMsgs.lastOrNull { !it.isUser }
            _uiState.update {
                it.copy(
                    currentSessionId = sessionId,
                    messages = sessionMsgs,
                    userQuestion = lastUser?.text ?: "",
                    aiAnswerParagraph1 = lastAi?.text ?: "",
                    aiAnswerParagraph2 = lastAi?.paragraph2 ?: "",
                    isGenerating = false,
                    currentlySpeakingMessageId = null,
                    isHistorySheetVisible = false,
                    aiThoughtStatus = "✨ SYNAPSE AI · Resumed conversation"
                )
            }
        }
    }

    /**
     * Deletes a past chat session from SQLite.
     */
    fun onDeleteSession(sessionId: String) {
        scope.launch {
            chatStorage?.deleteSession(sessionId)
            val updatedSessions = chatStorage?.loadSessions(currentUserId).orEmpty()
            _uiState.update { it.copy(chatSessions = updatedSessions) }
            if (currentSessionId == sessionId) {
                if (updatedSessions.isNotEmpty()) {
                    onSelectSession(updatedSessions.first().sessionId)
                } else {
                    onNewChat()
                }
            }
        }
    }

    fun toggleHistorySheet(visible: Boolean? = null) {
        _uiState.update { it.copy(isHistorySheetVisible = visible ?: !it.isHistorySheetVisible) }
    }

    fun onHoldRecordingStart() {
        _uiState.update {
            it.copy(
                isHoldRecording = true,
                isRecordingLocked = false,
                recordingDurationSeconds = 0,
                recordedSpokenText = ""
            )
        }
    }

    fun onHoldRecordingLock() {
        _uiState.update { it.copy(isRecordingLocked = true) }
    }

    fun onHoldRecordingCancel() {
        _uiState.update {
            it.copy(
                isHoldRecording = false,
                isRecordingLocked = false,
                recordingDurationSeconds = 0,
                recordedSpokenText = ""
            )
        }
    }

    fun onHoldRecordingSpeechUpdated(text: String) {
        _uiState.update { it.copy(recordedSpokenText = text) }
    }

    fun onHoldRecordingSubmit(spokenText: String? = null) {
        val finalText = (spokenText ?: _uiState.value.recordedSpokenText).trim()
        _uiState.update {
            it.copy(
                isHoldRecording = false,
                isRecordingLocked = false,
                recordingDurationSeconds = 0,
                recordedSpokenText = ""
            )
        }
        if (finalText.isNotBlank()) {
            onSendMessage(finalText)
        }
    }

    fun onHoldRecordingTimerTick() {
        _uiState.update { it.copy(recordingDurationSeconds = it.recordingDurationSeconds + 1) }
    }

    fun onInputQueryChanged(query: String) {
        _uiState.update { it.copy(inputQuery = query) }
    }

    /**
     * Executes instantaneous message sending (0ms UI lag), checks for Agentic In-App Task commands,
     * or starts fast on-device token streaming.
     */
    fun onSendMessage(overrideQuery: String? = null) {
        if (_uiState.value.isGenerating) return

        val query = (overrideQuery ?: _uiState.value.inputQuery).trim()
        if (query.isBlank()) return

        // 1. Instantly clear input query on Main thread with 0ms lag
        _uiState.update { it.copy(inputQuery = "") }

        val lowerQ = query.lowercase(Locale.ROOT).trim()

        // --- Active In-Chat Quiz Input Interceptor ---
        val currentQuiz = _uiState.value.activeQuizSession
        if (currentQuiz != null && !currentQuiz.isCompleted) {
            if (isQuizCancellation(lowerQ)) {
                cancelActiveQuiz(query)
                return
            }
            val currentQ = currentQuiz.questions.getOrNull(currentQuiz.currentIndex)
            val detectedOption = parseOptionFromInput(query, currentQ?.options)
            if (detectedOption != null) {
                submitQuizAnswer(detectedOption)
                return
            }
        }

        // --- Deterministic Command Handlers (0ms lag, pre-LLM execution) ---
        if (handleUserNameQuery(query)) return
        if (handleNavigationCommand(query)) return
        if (handleAlarmCommand(query)) return
        if (handleChapterNoteSaving(query)) return

        // --- Board Exam Format Shortcuts (2M and 5M) ---
        if (isTwoMarkQuery(lowerQ)) {
            val extracted = extractTopicFromExamQuery(query)
            formatAsTwoMark(customTopic = extracted.ifBlank { null })
            return
        }

        if (isFiveMarkQuery(lowerQ)) {
            val extracted = extractTopicFromExamQuery(query)
            formatAsFiveMark(customTopic = extracted.ifBlank { null })
            return
        }

        if (isLengthControlShortenQuery(lowerQ)) {
            val userMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                senderName = "You",
                text = query,
                timeAgo = "Just now",
                isUser = true
            )
            val topicToSummarize = lastTopic.ifBlank {
                _uiState.value.messages.lastOrNull { !it.isUser && it.matchedTopic != null }?.matchedTopic ?: "Academic Concept"
            }
            val shortText = buildString {
                appendLine("* **Core Concept:** $topicToSummarize focuses on foundational mechanisms, syntax, and computational logic.")
                appendLine("* **Key Rule / Syntax:** Standard programming construct or formula with complexity constraints.")
                append("* **Placement Takeaway:** High-yield summary point essential for technical and coding interview rounds.")
            }
            val aiMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                senderName = "SYNAPSE AI",
                text = shortText,
                timeAgo = "Just now",
                isUser = false,
                matchedTopic = topicToSummarize,
                isConceptAnswer = true
            )
            val newMessages = _uiState.value.messages + userMsg + aiMsg
            _uiState.update {
                it.copy(
                    messages = newMessages,
                    userQuestion = query,
                    aiAnswerParagraph1 = shortText,
                    aiAnswerParagraph2 = "",
                    aiThoughtStatus = "✓ Compressed Summary ($topicToSummarize)",
                    isGenerating = false
                )
            }
            persistCurrentSession(newMessages)
            return
        }

        // --- Agentic In-App Task Execution Checks ---
        // A. Automatic Note Saving & Generation Command
        if (isNoteSavingQuery(query)) {
            val userMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                senderName = "You",
                text = query,
                timeAgo = "Just now",
                isUser = true
            )
            val lastAi = _uiState.value.messages.lastOrNull { !it.isUser && !it.isSavedNoteCard && it.text.isNotBlank() }
            val extractedTopic = if (query.contains("for ", ignoreCase = true)) {
                query.substringAfter("for ", "").substringBefore(" in ").trim()
            } else ""
            val rawTopic = if (extractedTopic.isNotBlank()) extractedTopic else (lastAi?.matchedTopic ?: lastAi?.matchedChapter ?: "Placement Prep Concept")
            val noteTitle = "$rawTopic - Smart Study Notes"
            val noteContent = buildString {
                appendLine("# $noteTitle")
                if (!lastAi?.matchedSubject.isNullOrBlank()) {
                    appendLine("**Track:** ${lastAi?.matchedSubject}")
                }
                appendLine()
                appendLine("### Key Principles & Interview Tips")
                if (lastAi?.text?.isNotBlank() == true) {
                    appendLine(lastAi.text)
                } else {
                    appendLine("- Core logic, syntax patterns, and complexity analysis for placement preparation.")
                    appendLine("- Step-by-step mechanisms and essential boundary conditions.")
                }
                appendLine()
                appendLine("### Important Formulas / Key Dates / Definitions")
                if (!lastAi?.paragraph2.isNullOrBlank()) {
                    appendLine(lastAi.paragraph2)
                } else {
                    appendLine("- Canonical governing formula / standard definition with SI units clearly stated.")
                    appendLine("- Conservation laws and critical equilibrium constraints.")
                }
                appendLine()
                appendLine("### 2-Mark & 5-Mark Revision Summary")
                appendLine("1. **2-Mark Focus:** Precise textbook definitions and unit statements for full marks.")
                appendLine("2. **5-Mark Focus:** Comprehensive derivations, structural steps, and board exam presentations.")
            }

            scope.launch(Dispatchers.IO) {
                val user = sessionManager?.getSessionUser()
                val uid = user?.id ?: currentUserId
                val email = user?.email ?: "student@synapse.ai"
                try {
                    SynapseDatabase.getNotesDao().insertNote(
                        NoteEntity(
                            userId = uid,
                            subjectId = "101",
                            title = noteTitle,
                            content = noteContent,
                            category = "SAVED",
                            trackName = lastAi?.matchedSubject ?: "Python & Coding Logic",
                            isBookmarked = true,
                            isOfflineCached = true
                        )
                    )
                } catch (_: Throwable) {}
                notesRepository.saveNote(
                    title = noteTitle,
                    content = noteContent,
                    subjectId = 1001,
                    category = "SUBJECT",
                    userId = uid,
                    email = email
                )
            }

            val updatedPrior = _uiState.value.messages.map {
                if (lastAi != null && it.id == lastAi.id) it.copy(isSaved = true) else it
            }

            val confirmMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                senderName = "SYNAPSE AI",
                text = "Done! I've generated and saved exam-ready revision notes for **$rawTopic** to your Notes tab.",
                timeAgo = "Just now",
                isUser = false,
                isSavedNoteCard = true,
                savedNoteTitle = noteTitle
            )

            val newMessages = updatedPrior + userMsg + confirmMsg

            _uiState.update {
                it.copy(
                    messages = newMessages,
                    userQuestion = query,
                    isGenerating = false,
                    aiThoughtStatus = "✓ Note Saved Successfully"
                )
            }
            persistCurrentSession(newMessages)
            return
        }

        // B. In-Chat Interactive 10-Question Quiz Command (Stay inside chat)
        if (isQuizActionQuery(query)) {
            startInChatQuiz(query)
            return
        }

        // C. Subject / Roadmap Navigation Command
        if (isSubjectActionQuery(query)) {
            val (subId, subName) = resolveSubjectFromQuery(query)
            val userMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                senderName = "You",
                text = query,
                timeAgo = "Just now",
                isUser = true
            )
            val actionMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                senderName = "SYNAPSE AI",
                text = "🗺️ Opening $subName roadmap now. Here you can explore structured chapter progressions and milestones.",
                timeAgo = "Just now",
                isUser = false,
                actionCommand = "NAVIGATE_SUBJECT"
            )
            _uiState.update {
                it.copy(
                    messages = it.messages + userMsg + actionMsg,
                    userQuestion = query,
                    isGenerating = false,
                    aiThoughtStatus = "🗺️ Opening $subName Roadmap"
                )
            }
            chatStorage?.saveMessages(currentUserId, _uiState.value.messages)
            persistCurrentSession(_uiState.value.messages)
            scope.launch {
                _actionEvent.emit(AiUiAction.NavigateToSubject(subId, subName))
            }
            return
        }

        // D. Notes View Navigation Command
        if (isNotesActionQuery(query)) {
            val userMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                senderName = "You",
                text = query,
                timeAgo = "Just now",
                isUser = true
            )
            val actionMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                senderName = "SYNAPSE AI",
                text = "📝 Opening your Saved Study Notes now.",
                timeAgo = "Just now",
                isUser = false,
                actionCommand = "NAVIGATE_NOTES"
            )
            _uiState.update {
                it.copy(
                    messages = it.messages + userMsg + actionMsg,
                    userQuestion = query,
                    isGenerating = false,
                    aiThoughtStatus = "📝 Opening Notes"
                )
            }
            chatStorage?.saveMessages(currentUserId, _uiState.value.messages)
            persistCurrentSession(_uiState.value.messages)
            scope.launch {
                _actionEvent.emit(AiUiAction.NavigateToNotes)
            }
            return
        }

        // E. Progress Check Command
        if (isProgressActionQuery(query)) {
            val userMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                senderName = "You",
                text = query,
                timeAgo = "Just now",
                isUser = true
            )
            scope.launch(Dispatchers.IO) {
                val records = try {
                    com.siamts.synapse.data.local.SynapseDatabase.getInstance().getAllRecords()
                } catch (_: Throwable) { emptyList() }

                val topicsDone = records.map { it.chapterId }.distinct().size.coerceAtLeast(records.size).coerceAtLeast(3)
                val avgScore = if (records.isNotEmpty()) (records.sumOf { it.score } * 100 / records.sumOf { it.totalQuestions }.coerceAtLeast(1)) else 85
                val streak = 3

                val actionMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    senderName = "SYNAPSE AI",
                    text = "📊 **Here is your current academic progress:**\n• **Topics Mastered:** $topicsDone chapters\n• **Average Quiz Score:** $avgScore%\n• **Study Streak:** $streak days active\n\nKeep up the great momentum! Tap below to explore deeper analytics.",
                    timeAgo = "Just now",
                    isUser = false,
                    actionCommand = "SHOW_PROGRESS"
                )

                _uiState.update {
                    it.copy(
                        messages = it.messages + userMsg + actionMsg,
                        userQuestion = query,
                        isGenerating = false,
                        aiThoughtStatus = "📊 Academic Progress Summary"
                    )
                }
                chatStorage?.saveMessages(currentUserId, _uiState.value.messages)
                persistCurrentSession(_uiState.value.messages)
                _actionEvent.emit(AiUiAction.ShowProgressSummary(topicsDone, avgScore, streak))
            }
            return
        }

        // F. Profile Command
        if (isProfileActionQuery(query)) {
            val userMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                senderName = "You",
                text = query,
                timeAgo = "Just now",
                isUser = true
            )
            val actionMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                senderName = "SYNAPSE AI",
                text = "👤 Opening your Profile now.",
                timeAgo = "Just now",
                isUser = false,
                actionCommand = "NAVIGATE_PROFILE"
            )
            _uiState.update {
                it.copy(
                    messages = it.messages + userMsg + actionMsg,
                    userQuestion = query,
                    isGenerating = false,
                    aiThoughtStatus = "👤 Opening Profile"
                )
            }
            chatStorage?.saveMessages(currentUserId, _uiState.value.messages)
            persistCurrentSession(_uiState.value.messages)
            scope.launch {
                _actionEvent.emit(AiUiAction.NavigateToProfile)
            }
            return
        }

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "You",
            text = query,
            timeAgo = "Just now",
            isUser = true
        )

        val aiMessageId = UUID.randomUUID().toString()
        val initialAiMessage = ChatMessage(
            id = aiMessageId,
            senderName = "SYNAPSE AI",
            text = "",
            paragraph2 = null,
            timeAgo = "Just now",
            isUser = false
        )

        // 2. Immediately append messages with zero delay
        _uiState.update {
            it.copy(
                messages = it.messages + userMessage + initialAiMessage,
                userQuestion = query,
                isGenerating = true,
                aiThoughtStatus = "✨ SYNAPSE AI is thinking..."
            )
        }

        // 3. Fast streaming on Dispatchers.Default (15ms per word)
        streamingJob?.cancel()
        streamingJob = scope.launch(Dispatchers.Default) {
            val isSlmReady = slmEngine != null && slmEngine.isModelAvailable()
            if (isSlmReady) {
                _uiState.update { it.copy(aiThoughtStatus = "🧠 SYNAPSE SLM is thinking...") }
                val ragContext = localAiEngine.retrieveContext(query)
                var accumulated = ""
                slmEngine?.generateResponseStream(query, ragContext, delayPerWordMs = 15L)?.collect { partialText ->
                    accumulated = partialText
                    val updatedMessages = _uiState.value.messages.map { msg ->
                        if (msg.id == aiMessageId) {
                            msg.copy(
                                text = accumulated,
                                paragraph2 = null
                            )
                        } else msg
                    }
                    _uiState.update {
                        it.copy(
                            messages = updatedMessages,
                            aiAnswerParagraph1 = accumulated,
                            aiAnswerParagraph2 = ""
                        )
                    }
                }

                if (accumulated.isNotBlank()) {
                    val completedAiMessage = ChatMessage(
                        id = aiMessageId,
                        senderName = "SYNAPSE AI",
                        text = accumulated,
                        paragraph2 = null,
                        timeAgo = "Just now",
                        isUser = false,
                        matchedSubject = "On-Device SLM",
                        matchedChapter = "Neural Inference",
                        matchedTopic = "Gemma-2B / Qwen"
                    )

                    val finalizedMessages = _uiState.value.messages.map {
                        if (it.id == aiMessageId) completedAiMessage else it
                    }

                    _uiState.update {
                        it.copy(
                            messages = finalizedMessages,
                            aiAnswerParagraph1 = accumulated,
                            aiAnswerParagraph2 = "",
                            aiThoughtStatus = "🧠 SYNAPSE SLM · On-Device Neural Model",
                            isGenerating = false
                        )
                    }

                    chatStorage?.saveMessages(currentUserId, finalizedMessages)
                    persistCurrentSession(finalizedMessages)

                    try {
                        val user = sessionManager?.getSessionUser()
                        val email = user?.email ?: "student@synapse.ai"
                        repository.askQuestion(question = query, userId = currentUserId, email = email)
                    } catch (_: Exception) {}
                    return@launch
                }
            }

            localAiEngine.streamResponse(query, delayPerWordMs = 15L).collect { streamEvent ->
                when (streamEvent) {
                    is LocalAiStreamToken.Thinking -> {
                        _uiState.update { it.copy(aiThoughtStatus = streamEvent.status) }
                    }
                    is LocalAiStreamToken.Token -> {
                        val updatedMessages = _uiState.value.messages.map { msg ->
                            if (msg.id == aiMessageId) {
                                msg.copy(
                                    text = streamEvent.paragraph1,
                                    paragraph2 = streamEvent.paragraph2.ifBlank { null },
                                    matchedSubject = streamEvent.response.matchedSubject,
                                    matchedChapter = streamEvent.response.matchedChapter,
                                    matchedTopic = streamEvent.response.matchedTopic
                                )
                            } else msg
                        }
                        _uiState.update {
                            it.copy(
                                messages = updatedMessages,
                                aiAnswerParagraph1 = streamEvent.paragraph1,
                                aiAnswerParagraph2 = streamEvent.paragraph2
                            )
                        }
                    }
                    is LocalAiStreamToken.Completed -> {
                        val localResponse = streamEvent.response
                        val statusText = when {
                            localResponse.isGreeting -> "✨ SYNAPSE AI · Ready to explore subjects with you"
                            localResponse.isTopicSuggestion -> "✨ SYNAPSE AI · Curriculum Guide (${localResponse.matchedSubject ?: "Catalog"})"
                            localResponse.isFollowUp -> "✨ SYNAPSE AI · Context Follow-up (${localResponse.matchedTopic})"
                            localResponse.matchedTopic != null -> "✨ SYNAPSE AI · ${localResponse.matchedSubject} · ${localResponse.matchedChapter}"
                            else -> "✨ SYNAPSE AI · Knowledge Companion"
                        }

                        val completedAiMessage = ChatMessage(
                            id = aiMessageId,
                            senderName = "SYNAPSE AI",
                            text = localResponse.paragraph1,
                            paragraph2 = localResponse.paragraph2.ifBlank { null },
                            timeAgo = "Just now",
                            isUser = false,
                            matchedSubject = localResponse.matchedSubject,
                            matchedChapter = localResponse.matchedChapter,
                            matchedTopic = localResponse.matchedTopic,
                            isConceptAnswer = (localResponse.matchedTopic != null || localResponse.confidenceScore >= 12.0)
                        )

                        val finalizedMessages = _uiState.value.messages.map {
                            if (it.id == aiMessageId) completedAiMessage else it
                        }

                        if (!localResponse.matchedTopic.isNullOrBlank()) {
                            lastTopic = localResponse.matchedTopic
                            lastResponseText = localResponse.paragraph1 + if (localResponse.paragraph2.isNotBlank()) "\n" + localResponse.paragraph2 else ""
                        }
                        if (!localResponse.matchedSubject.isNullOrBlank()) {
                            lastTrack = localResponse.matchedSubject
                        }

                        // Handle name capture or preference persistence
                        if (localResponse.isNameCapture && !localResponse.capturedName.isNullOrBlank()) {
                            val newName = localResponse.capturedName
                            localAiEngine.preferredName = newName
                            scope.launch(Dispatchers.IO) {
                                userPreferencesRepository?.setChatPreferredName(newName)
                                userPreferencesRepository?.setPreferredName(newName)
                            }
                            _uiState.update { it.copy(headerTag = "PERSONALIZED FOR ${newName.uppercase()}") }
                        }
                        if (localResponse.isPreferenceUpdate) {
                            scope.launch(Dispatchers.IO) {
                                userPreferencesRepository?.setIncludeFormulas(localAiEngine.includeFormulas)
                                userPreferencesRepository?.setIncludeExamples(localAiEngine.includeExamples)
                                userPreferencesRepository?.setUseEmojis(localAiEngine.useEmojis)
                            }
                        }

                        _uiState.update {
                            it.copy(
                                messages = finalizedMessages,
                                aiAnswerParagraph1 = localResponse.paragraph1,
                                aiAnswerParagraph2 = localResponse.paragraph2,
                                aiThoughtStatus = statusText,
                                isGenerating = false
                            )
                        }

                        // Save conversation history to local storage
                        chatStorage?.saveMessages(currentUserId, finalizedMessages)
                        persistCurrentSession(finalizedMessages)

                        // Async remote sync
                        try {
                            val user = sessionManager?.getSessionUser()
                            val email = user?.email ?: "student@synapse.ai"
                            repository.askQuestion(
                                question = query,
                                userId = currentUserId,
                                email = email,
                                preferredName = localAiEngine.preferredName,
                                includeFormulas = localAiEngine.includeFormulas,
                                includeExamples = localAiEngine.includeExamples,
                                useEmojis = localAiEngine.useEmojis,
                                consecutiveUnclear = localAiEngine.consecutiveUnclearCount
                            )
                        } catch (_: Exception) {
                            // Local fallback handled on-device
                        }
                    }
                }
            }
        }
    }

    /**
     * Clears the active chat session by starting a fresh one without deleting past sessions.
     */
    fun onClearChat() {
        onNewChat()
    }

    fun onPromptClicked(prompt: String) {
        if (_uiState.value.isGenerating) return
        onSendMessage(prompt)
    }

    /**
     * Obtains or initializes the singleton VoiceAssistantManager for live speech and audio TTS playback.
     */
    fun getOrCreateVoiceAssistant(context: Context): VoiceAssistantManager {
        return voiceAssistant ?: VoiceAssistantManager(
            context = context.applicationContext,
            localAiEngine = localAiEngine,
            onConversationTurn = { userQuery, aiResponseText ->
                onVoiceTurnCompleted(userQuery, aiResponseText)
            }
        ).also { voiceAssistant = it }
    }

    private fun onVoiceTurnCompleted(userQuery: String, aiResponseText: String) {
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "You",
            text = userQuery,
            timeAgo = "Just now",
            isUser = true
        )
        val aiMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "SYNAPSE AI",
            text = aiResponseText,
            timeAgo = "Just now",
            isUser = false
        )
        val newMessages = _uiState.value.messages + userMsg + aiMsg
        _uiState.update { it.copy(messages = newMessages) }
        chatStorage?.saveMessages(currentUserId, newMessages)
        persistCurrentSession(newMessages)
    }

    /**
     * Speaks or toggles the speech of an individual message bubble aloud.
     */
    fun toggleSpeakMessage(messageId: String, text: String, context: Context) {
        val va = getOrCreateVoiceAssistant(context)
        if (_uiState.value.currentlySpeakingMessageId == messageId) {
            va.stopSpeaking()
            _uiState.update { it.copy(currentlySpeakingMessageId = null) }
        } else {
            _uiState.update { it.copy(currentlySpeakingMessageId = messageId) }
            va.speakDirect(text) {
                _uiState.update {
                    if (it.currentlySpeakingMessageId == messageId) {
                        it.copy(currentlySpeakingMessageId = null)
                    } else it
                }
            }
        }
    }

    fun onSaveNoteClicked(message: ChatMessage) {
        val rawTopic = message.matchedTopic ?: message.matchedChapter ?: "Study Concept"
        val title = "$rawTopic - AI Revision Summary"
        val content = buildString {
            appendLine("# $title")
            if (!message.matchedSubject.isNullOrBlank()) {
                appendLine("**Subject:** ${message.matchedSubject}")
            }
            appendLine()
            appendLine(message.text)
            if (!message.paragraph2.isNullOrBlank()) {
                appendLine()
                appendLine(message.paragraph2)
            }
        }

        scope.launch(Dispatchers.IO) {
            val user = sessionManager?.getSessionUser()
            val uid = user?.id ?: currentUserId
            val email = user?.email ?: "student@synapse.ai"
            try {
                val subName = message.matchedSubject ?: "Python & Coding Logic"
                SynapseDatabase.getNotesDao().insertNote(
                    NoteEntity(
                        userId = uid,
                        subjectId = "101",
                        title = title,
                        content = content,
                        category = "SAVED",
                        trackName = subName,
                        isBookmarked = true,
                        isOfflineCached = true
                    )
                )
            } catch (_: Throwable) {}
            notesRepository.saveNote(
                title = title,
                content = content,
                subjectId = 1,
                chapterId = null,
                category = "SUBJECT",
                userId = uid,
                email = email
            )
            try {
                val subName = message.matchedSubject ?: "Python & Coding Logic"
                com.siamts.synapse.data.local.SynapseDatabase.logSession(
                    trackName = subName,
                    category = "Note Making",
                    durationMinutes = 5
                )
            } catch (_: Throwable) {}
        }

        val updatedMessages = _uiState.value.messages.map {
            if (it.id == message.id) it.copy(isSaved = true) else it
        }

        val confirmMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "SYNAPSE AI",
            text = "Done! I've saved crisp study notes for **$rawTopic** to your Notes tab.",
            timeAgo = "Just now",
            isUser = false,
            isSavedNoteCard = true,
            savedNoteTitle = title
        )

        val newMessages = updatedMessages + confirmMsg
        _uiState.update { it.copy(messages = newMessages) }
        persistCurrentSession(newMessages)
    }

    fun onElaborateClicked(message: ChatMessage) {
        val topic = message.matchedTopic ?: message.matchedChapter ?: message.text.take(30)
        onSendMessage("Elaborate on $topic in detail")
    }

    private fun isNoteSavingQuery(q: String): Boolean {
        val lower = q.lowercase(Locale.ROOT)
        return lower.contains("generate notes") || lower.contains("notes generate") ||
                lower.contains("notes generate pani kudu") || lower.contains("generate note") ||
                lower.contains("notes eduthu kudu") || lower.contains("save note") ||
                lower.contains("save notes") || lower.contains("save this note") ||
                lower.contains("save this as a note") || lower.contains("save this as note") ||
                lower.contains("save to notes") || lower.contains("save this to notes") ||
                lower.contains("make a note") || lower.contains("make notes") ||
                lower.contains("take notes") || lower.contains("take note") ||
                lower.startsWith("notes for ") || lower.contains("notes for ")
    }

    private fun isQuizActionQuery(q: String): Boolean {
        val lower = q.lowercase(Locale.ROOT)
        return lower.contains("quiz") || lower.contains("test me") ||
                lower.contains("practice test") || lower.contains("test my") ||
                lower.contains("ask me 10") || lower.contains("10 questions") ||
                lower.contains("give me 10") || lower.contains("mcq") ||
                lower == "quiz" || lower == "take a quiz" || lower == "start quiz"
    }

    private fun isQuizCancellation(lower: String): Boolean {
        return lower == "cancel" || lower == "stop" || lower == "quit" ||
               lower == "exit" || lower == "stop quiz" || lower == "cancel quiz" ||
               lower == "quit quiz" || lower == "end quiz"
    }

    fun cancelActiveQuiz(userQuery: String = "Cancel quiz") {
        val session = _uiState.value.activeQuizSession ?: return
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "You",
            text = userQuery,
            timeAgo = "Just now",
            isUser = true
        )
        val cancelMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "SYNAPSE AI",
            text = "Quiz cancelled. Let me know which topic you would like to study next!",
            timeAgo = "Just now",
            isUser = false
        )
        val newMessages = _uiState.value.messages + userMsg + cancelMsg
        _uiState.update {
            it.copy(
                messages = newMessages,
                activeQuizSession = null,
                aiThoughtStatus = "Quiz ended"
            )
        }
        persistCurrentSession(newMessages)
    }

    private fun parseOptionFromInput(input: String, options: Map<String, String>?): String? {
        val trimmed = input.trim()
        val upper = trimmed.uppercase(Locale.ROOT)
        if (upper in setOf("A", "B", "C", "D")) return upper
        if (upper.startsWith("OPTION ") && upper.removePrefix("OPTION ").trim() in setOf("A", "B", "C", "D")) {
            return upper.removePrefix("OPTION ").trim()
        }
        if (trimmed == "1") return "A"
        if (trimmed == "2") return "B"
        if (trimmed == "3") return "C"
        if (trimmed == "4") return "D"

        if (options != null) {
            val lowerInput = trimmed.lowercase(Locale.ROOT)
            for ((k, v) in options) {
                val lowerV = v.lowercase(Locale.ROOT)
                if (lowerInput == lowerV || (lowerInput.length > 3 && lowerV.contains(lowerInput))) {
                    return k
                }
            }
        }
        return null
    }

    private fun startInChatQuiz(query: String) {
        val (subId, subName) = resolveSubjectFromQuery(query)
        val subject = BaseCurriculumData.getSubjectById(subId)
            ?: BaseCurriculumData.subjects.firstOrNull { it.name.equals(subName, ignoreCase = true) }
            ?: BaseCurriculumData.subjects.firstOrNull()

        // Check if user specifically requested a chapter (e.g. "chapter 1", "chapter 2", etc.)
        val chapterRegex = Regex("chapter\\s*(\\d+)", RegexOption.IGNORE_CASE)
        val chMatch = chapterRegex.find(query)
        val reqChapterNum = chMatch?.groupValues?.getOrNull(1)?.toIntOrNull()

        val allMcqs = mutableListOf<QuizQuestionDto>()
        if (subject != null) {
            if (reqChapterNum != null) {
                val targetChapter = subject.chapters.getOrNull(reqChapterNum - 1)
                    ?: subject.chapters.firstOrNull { it.chapterId % 10 == reqChapterNum }
                if (targetChapter != null && targetChapter.mcqs.isNotEmpty()) {
                    allMcqs.addAll(targetChapter.mcqs)
                }
            }
            // Fill up from other chapters in subject
            for (ch in subject.chapters) {
                for (q in ch.mcqs) {
                    if (allMcqs.none { it.id == q.id }) {
                        allMcqs.add(q)
                    }
                }
            }
        }

        // Fallback: If still under 10, pull across all 4 subjects
        if (allMcqs.size < 10) {
            for (s in BaseCurriculumData.subjects) {
                for (ch in s.chapters) {
                    for (q in ch.mcqs) {
                        if (allMcqs.none { it.id == q.id }) {
                            allMcqs.add(q)
                        }
                    }
                }
            }
        }

        val selectedQuestions = if (allMcqs.size >= 10) allMcqs.shuffled().take(10) else allMcqs
        if (selectedQuestions.isEmpty()) {
            onSendMessage("Explain $subName")
            return
        }

        val session = InChatQuizSession(
            subjectId = subId,
            subjectName = subName,
            questions = selectedQuestions,
            currentIndex = 0,
            score = 0
        )

        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "You",
            text = query,
            timeAgo = "Just now",
            isUser = true
        )

        val introMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "SYNAPSE AI",
            text = "🎯 **SYNAPSE Interactive Quiz: $subName**\n\n10 Placement-Grade Multiple Choice Questions.\nSelect an option below or type/speak A, B, C, or D!",
            timeAgo = "Just now",
            isUser = false,
            matchedSubject = subName
        )

        val q1 = selectedQuestions[0]
        val q1Msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "SYNAPSE AI",
            text = "**Question 1 of ${selectedQuestions.size}**\n\n${q1.question}",
            timeAgo = "Just now",
            isUser = false,
            isQuizQuestion = true,
            quizOptions = q1.options,
            quizQuestionIndex = 0,
            quizTotalQuestions = selectedQuestions.size,
            matchedSubject = subName
        )

        val newMessages = _uiState.value.messages + userMsg + introMsg + q1Msg
        _uiState.update {
            it.copy(
                messages = newMessages,
                activeQuizSession = session,
                userQuestion = query,
                isGenerating = false,
                aiThoughtStatus = "🎯 In-Chat Quiz: $subName (Question 1/${selectedQuestions.size})"
            )
        }
        chatStorage?.saveMessages(currentUserId, newMessages)
        persistCurrentSession(newMessages)
    }

    fun submitQuizAnswer(optionKey: String) {
        val session = _uiState.value.activeQuizSession ?: return
        if (session.isCompleted || session.currentIndex >= session.questions.size) return

        val q = session.questions[session.currentIndex]
        val cleanOption = optionKey.trim().uppercase(Locale.ROOT)
        val selectedText = q.options[cleanOption] ?: cleanOption
        val isCorrect = cleanOption.equals(q.correctOption.trim(), ignoreCase = true)

        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "You",
            text = "Option $cleanOption: $selectedText",
            timeAgo = "Just now",
            isUser = true
        )

        val newScore = if (isCorrect) session.score + 1 else session.score
        val newIndex = session.currentIndex + 1
        val answerRecord = InChatQuizAnswer(
            questionNumber = newIndex,
            question = q.question,
            selectedOption = cleanOption,
            correctOption = q.correctOption,
            isCorrect = isCorrect,
            explanation = q.explanation.orEmpty()
        )
        val updatedAnswers = session.userAnswers + answerRecord

        val evalText = if (isCorrect) {
            "✅ **Correct!** (+1 Point)\n\n${q.explanation.orEmpty()}\n\n📊 *Current Score: $newScore / $newIndex*"
        } else {
            val correctText = q.options[q.correctOption] ?: q.correctOption
            "❌ **Incorrect!**\n\nThe correct answer is **Option ${q.correctOption}**: $correctText\n\n💡 **Explanation:** ${q.explanation.orEmpty()}\n\n📊 *Current Score: $newScore / $newIndex*"
        }

        val evalMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "SYNAPSE AI",
            text = evalText,
            timeAgo = "Just now",
            isUser = false,
            matchedSubject = session.subjectName
        )

        val nextMessages = mutableListOf<ChatMessage>()
        nextMessages.add(userMsg)
        nextMessages.add(evalMsg)

        val updatedSession: InChatQuizSession
        if (newIndex < session.questions.size) {
            val nextQ = session.questions[newIndex]
            val nextMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                senderName = "SYNAPSE AI",
                text = "**Question ${newIndex + 1} of ${session.questions.size}**\n\n${nextQ.question}",
                timeAgo = "Just now",
                isUser = false,
                isQuizQuestion = true,
                quizOptions = nextQ.options,
                quizQuestionIndex = newIndex,
                quizTotalQuestions = session.questions.size,
                matchedSubject = session.subjectName
            )
            nextMessages.add(nextMsg)
            updatedSession = session.copy(
                currentIndex = newIndex,
                score = newScore,
                userAnswers = updatedAnswers
            )
        } else {
            // Quiz Completed
            val pct = (newScore * 100) / session.questions.size
            val mistakes = updatedAnswers.filter { !it.isCorrect }
            val mistakeSummary = mistakes.map { "• ${it.question}\n  *Correct: Option ${it.correctOption}* — ${it.explanation}" }

            val scorecardText = buildString {
                appendLine("• **Final Score:** $newScore / ${session.questions.size} ($pct%)")
                val grade = when {
                    pct >= 90 -> "🌟 Outstanding Mastery! Placement Assessment Ready."
                    pct >= 70 -> "👍 Great Performance! Solid Conceptual Foundation."
                    pct >= 50 -> "📈 Good Effort — Review weak areas to master the topic."
                    else -> "📚 Needs Revision — Re-read key chapter notes and retry."
                }
                appendLine("• **Assessment:** $grade\n")
                if (mistakes.isNotEmpty()) {
                    appendLine("⚠️ **Weak Areas & Identified Mistakes (${mistakes.size} questions):**")
                    mistakes.forEachIndexed { i, m ->
                        appendLine("${i + 1}. ${m.question}")
                        appendLine("   *Correct Answer (${m.correctOption}):* ${m.explanation}\n")
                    }
                } else {
                    appendLine("✨ **Zero Mistakes!** Perfect 100% score across all 10 questions!")
                }
            }.trim()

            val scorecardMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                senderName = "SYNAPSE AI",
                text = scorecardText,
                timeAgo = "Just now",
                isUser = false,
                isQuizScorecard = true,
                quizScore = newScore,
                quizTotal = session.questions.size,
                quizSubject = session.subjectName,
                quizMistakes = mistakeSummary
            )
            nextMessages.add(scorecardMsg)
            updatedSession = session.copy(
                currentIndex = newIndex,
                score = newScore,
                userAnswers = updatedAnswers,
                isCompleted = true
            )

            // Save record to local database
            scope.launch(Dispatchers.IO) {
                try {
                    SynapseDatabase.getInstance().insertQuizRecord(
                        QuizRecordEntity(
                            subjectId = session.subjectId,
                            chapterId = "chat_quiz_10",
                            score = newScore,
                            totalQuestions = session.questions.size,
                            completedAt = System.currentTimeMillis()
                        )
                    )
                    for (m in mistakes) {
                        SynapseDatabase.getInstance().insertMistake(
                            MistakeQuestionEntity(
                                subjectId = session.subjectId,
                                chapterId = "chat_quiz_10",
                                questionText = m.question,
                                optionA = "",
                                optionB = "",
                                optionC = "",
                                optionD = "",
                                correctOption = m.correctOption,
                                explanation = m.explanation
                            )
                        )
                    }
                } catch (_: Throwable) {}
            }
        }

        val finalMessages = _uiState.value.messages + nextMessages
        _uiState.update {
            it.copy(
                messages = finalMessages,
                activeQuizSession = updatedSession,
                aiThoughtStatus = if (updatedSession.isCompleted) "🏆 Quiz Complete ($newScore/${session.questions.size})"
                                  else "🎯 In-Chat Quiz: ${session.subjectName} (${newIndex + 1}/${session.questions.size})"
            )
        }
        chatStorage?.saveMessages(currentUserId, finalMessages)
        persistCurrentSession(finalMessages)
    }

    fun saveQuizScorecardAndMistakes(message: ChatMessage) {
        if (message.isQuizScoreSaved) return
        val subject = message.quizSubject ?: "Placement Assessment"
        val noteTitle = "$subject Quiz Scorecard (${message.quizScore}/${message.quizTotal})"
        val noteContent = buildString {
            appendLine("# $noteTitle")
            appendLine("**Subject:** $subject")
            appendLine("**Score:** ${message.quizScore} / ${message.quizTotal} (${(message.quizScore * 100) / message.quizTotal}%)")
            appendLine("**Date:** ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(java.util.Date())}")
            appendLine()
            appendLine("### Detailed Score & Review")
            appendLine(message.text)
        }

        scope.launch(Dispatchers.IO) {
            try {
                val user = sessionManager?.getSessionUser()
                val uid = user?.id ?: currentUserId
                val email = user?.email ?: "student@synapse.ai"
                notesRepository.saveNote(
                    title = noteTitle,
                    content = noteContent,
                    subjectId = 101,
                    category = "QUIZ_SCORECARD",
                    userId = uid,
                    email = email
                )
            } catch (_: Exception) {}
        }

        val updatedMessages = _uiState.value.messages.map {
            if (it.id == message.id) it.copy(isQuizScoreSaved = true) else it
        }

        val confirmMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "SYNAPSE AI",
            text = "Done! I've saved your quiz scorecard and weak areas to your Notes tab as **$noteTitle**.",
            timeAgo = "Just now",
            isUser = false,
            isSavedNoteCard = true,
            savedNoteTitle = noteTitle
        )

        val newMessages = updatedMessages + confirmMsg
        _uiState.update { it.copy(messages = newMessages) }
        persistCurrentSession(newMessages)
    }

    private fun handleUserNameQuery(query: String): Boolean {
        val lower = query.lowercase(Locale.ROOT).trim()
        val normalized = lower.replace(Regex("[^a-zA-Z0-9\\s]"), " ").trim()

        // 1. Check if user is asking for their own name: "say my name", "what is my name", etc.
        if (localAiEngine.isUserAskingOwnName(lower, normalized)) {
            val user = sessionManager?.getSessionUser()
            val activeName = localAiEngine.getEffectiveUserName().ifBlank { user?.name?.trim().orEmpty() }
            val replyText = if (activeName.isNotBlank()) {
                "Your name is **$activeName**! 😊 How can I help you with your placement preparation today, $activeName?"
            } else {
                "I don't know your name yet! What should I call you? You can tell me *'My name is Kishore'* or *'Call me Kishore'*."
            }

            val userMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                senderName = "You",
                text = query,
                timeAgo = "Just now",
                isUser = true
            )
            val aiMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                senderName = "SYNAPSE AI",
                text = replyText,
                timeAgo = "Just now",
                isUser = false,
                actionCommand = "USER_NAME_REPLY"
            )
            val newMessages = _uiState.value.messages + userMsg + aiMsg
            _uiState.update {
                it.copy(
                    messages = newMessages,
                    userQuestion = query,
                    aiAnswerParagraph1 = replyText,
                    aiAnswerParagraph2 = "",
                    aiThoughtStatus = if (activeName.isNotBlank()) "✨ User Name: $activeName" else "✨ Awaiting Name",
                    isGenerating = false
                )
            }
            chatStorage?.saveMessages(currentUserId, newMessages)
            persistCurrentSession(newMessages)
            return true
        }

        // 2. Check if user is directly introducing / setting their name: "my name is Kishore", "call me Kishore", "i am Kishore"
        val callMeMatch = Regex(
            """^(?:(?:hello|hi|hey)\s+)?(?:from\s+now\s+on\s+)?(?:call\s+me\s+(?:by\s+my\s+name\s+|by\s+name\s+|with\s+my\s+name\s+)?|my\s+name\s+(?:is\s+)?|name\s+is\s+|i\s+am\s+|i['’]m\s+|myself\s+|this\s+is\s+)([a-zA-Z]+(?:\s+[a-zA-Z]+)?)""",
            RegexOption.IGNORE_CASE
        ).find(query.trim())
        if (callMeMatch != null) {
            val candidate = callMeMatch.groupValues[1].trim()
            val stopWords = setOf("by", "my", "name", "a", "an", "the", "asking", "studying", "learning", "ready", "ok", "okay", "fine", "what", "who", "say")
            if (!stopWords.contains(candidate.lowercase(Locale.ROOT)) && candidate.length >= 2) {
                val newName = candidate.split(" ")
                    .joinToString(" ") { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() } }

                localAiEngine.preferredName = newName
                localAiEngine.isWaitingForName = false
                scope.launch(Dispatchers.IO) {
                    userPreferencesRepository?.setChatPreferredName(newName)
                    userPreferencesRepository?.setPreferredName(newName)
                }
                _uiState.update { it.copy(headerTag = "PERSONALIZED FOR ${newName.uppercase()}") }

                val userMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    senderName = "You",
                    text = query,
                    timeAgo = "Just now",
                    isUser = true
                )
                val replyText = "Got it, **$newName**! From now on, I will call you $newName. 😊 What placement topic shall we dive into today?"
                val aiMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    senderName = "SYNAPSE AI",
                    text = replyText,
                    timeAgo = "Just now",
                    isUser = false,
                    actionCommand = "NAME_CAPTURED"
                )
                val newMessages = _uiState.value.messages + userMsg + aiMsg
                _uiState.update {
                    it.copy(
                        messages = newMessages,
                        userQuestion = query,
                        aiAnswerParagraph1 = replyText,
                        aiAnswerParagraph2 = "",
                        aiThoughtStatus = "✨ Saved Name: $newName",
                        isGenerating = false
                    )
                }
                chatStorage?.saveMessages(currentUserId, newMessages)
                persistCurrentSession(newMessages)
                return true
            }
        }

        return false
    }

    private fun handleNavigationCommand(query: String): Boolean {
        val lower = query.lowercase(Locale.ROOT).trim()
        val cleanLower = lower.replace(Regex("[^a-z0-9 ]"), " ").trim()

        val action: AiUiAction
        val replyText: String
        val thoughtStatus: String
        val actionCmd: String

        when {
            cleanLower in listOf("home", "home screen", "open home", "open home screen", "go to home", "go to home screen", "navigate to home", "take me to home") ||
            ((cleanLower.contains("open") || cleanLower.contains("go to") || cleanLower.contains("show")) && cleanLower.contains("home")) -> {
                action = AiUiAction.NavigateToHome
                replyText = "🏠 Opening your **Home** screen now."
                thoughtStatus = "🏠 Navigating to Home Screen"
                actionCmd = "NAVIGATE_HOME"
            }
            cleanLower in listOf("learn", "learn screen", "open learn", "open learn screen", "go to learn", "go to learn screen", "tracks", "track catalog", "open track", "open tracks") ||
            ((cleanLower.contains("open") || cleanLower.contains("go to") || cleanLower.contains("show")) && (cleanLower.contains("learn") || cleanLower.contains("tracks") || cleanLower.contains("catalog"))) -> {
                action = AiUiAction.NavigateToLearn
                replyText = "📖 Opening your **Learn** tracks catalog now."
                thoughtStatus = "📖 Navigating to Learn Screen"
                actionCmd = "NAVIGATE_LEARN"
            }
            cleanLower in listOf("quiz", "quiz screen", "quiz hub", "open quiz", "open quiz screen", "open quiz hub", "go to quiz", "go to quiz hub") ||
            ((cleanLower.contains("open") || cleanLower.contains("go to")) && (cleanLower.contains("quiz hub") || cleanLower.contains("quiz screen") || (cleanLower.contains("quiz") && !cleanLower.contains("start") && !cleanLower.contains("take") && !cleanLower.contains("test") && !cleanLower.contains("practice")))) -> {
                action = AiUiAction.NavigateToQuiz(null, null)
                replyText = "❓ Opening the **Quiz Hub** now."
                thoughtStatus = "❓ Navigating to Quiz Hub"
                actionCmd = "NAVIGATE_QUIZ"
            }
            cleanLower in listOf("notes", "notes screen", "saved notes", "open notes", "open notes screen", "go to notes", "go to notes screen", "view notes", "show notes", "show my notes") ||
            ((cleanLower.contains("open") || cleanLower.contains("go to") || cleanLower.contains("view") || cleanLower.contains("show")) && cleanLower.contains("notes") && !isNoteSavingQuery(query)) -> {
                action = AiUiAction.NavigateToNotes
                replyText = "📝 Opening your Saved Study Notes now."
                thoughtStatus = "📝 Opening Notes"
                actionCmd = "NAVIGATE_NOTES"
            }
            cleanLower in listOf("profile", "profile screen", "my profile", "open profile", "open profile screen", "go to profile", "view profile", "profile and settings") ||
            ((cleanLower.contains("open") || cleanLower.contains("go to") || cleanLower.contains("view")) && cleanLower.contains("profile")) -> {
                action = AiUiAction.NavigateToProfile
                replyText = "👤 Opening your **Profile & Settings** now."
                thoughtStatus = "👤 Navigating to Profile"
                actionCmd = "NAVIGATE_PROFILE"
            }
            cleanLower in listOf("analytics", "progress", "analytics screen", "progress screen", "open analytics", "open progress", "go to analytics", "go to progress", "view analytics", "progress and analytics") ||
            ((cleanLower.contains("open") || cleanLower.contains("go to") || cleanLower.contains("view") || cleanLower.contains("show")) && (cleanLower.contains("analytics") || cleanLower.contains("progress"))) -> {
                action = AiUiAction.NavigateToProgress
                replyText = "📊 Opening your **Progress & Analytics** dashboard now."
                thoughtStatus = "📊 Navigating to Analytics"
                actionCmd = "SHOW_PROGRESS"
            }
            else -> return false
        }

        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "You",
            text = query,
            timeAgo = "Just now",
            isUser = true
        )
        val actionMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "SYNAPSE AI",
            text = replyText,
            timeAgo = "Just now",
            isUser = false,
            actionCommand = actionCmd
        )
        val newMessages = _uiState.value.messages + userMsg + actionMsg
        _uiState.update {
            it.copy(
                messages = newMessages,
                userQuestion = query,
                isGenerating = false,
                aiThoughtStatus = thoughtStatus
            )
        }
        chatStorage?.saveMessages(currentUserId, newMessages)
        persistCurrentSession(newMessages)
        scope.launch {
            _actionEvent.emit(action)
        }
        return true
    }

    private fun handleAlarmCommand(query: String): Boolean {
        val lower = query.lowercase(Locale.ROOT).trim()
        val isAlarmIntent = lower.contains("alarm") || lower.contains("remind me") || lower.contains("reminder") ||
                lower.startsWith("remind ") || lower.contains("set reminder")
        if (!isAlarmIntent) return false

        var parsedHour: Int? = null
        var parsedMinute: Int? = null

        val colonOrDotRegex = Regex("""(\d{1,2})[:.](\d{2})\s*(am|pm)?""", RegexOption.IGNORE_CASE)
        val matchColon = colonOrDotRegex.find(lower)
        if (matchColon != null) {
            val h = matchColon.groupValues[1].toIntOrNull()
            val m = matchColon.groupValues[2].toIntOrNull()
            val ampm = matchColon.groupValues.getOrNull(3)?.lowercase(Locale.ROOT)
            if (h != null && m != null) {
                parsedMinute = m
                parsedHour = when {
                    ampm == "pm" && h < 12 -> h + 12
                    ampm == "am" && h == 12 -> 0
                    else -> h
                }
            }
        }

        if (parsedHour == null) {
            val hourOnlyRegex = Regex("""(\d{1,2})\s*(am|pm)""", RegexOption.IGNORE_CASE)
            val matchHour = hourOnlyRegex.find(lower)
            if (matchHour != null) {
                val h = matchHour.groupValues[1].toIntOrNull()
                val ampm = matchHour.groupValues[2].lowercase(Locale.ROOT)
                if (h != null) {
                    parsedMinute = 0
                    parsedHour = when {
                        ampm == "pm" && h < 12 -> h + 12
                        ampm == "am" && h == 12 -> 0
                        else -> h
                    }
                }
            }
        }

        val hour = parsedHour ?: 9
        val minute = parsedMinute ?: 0

        val period = if (hour >= 12) "PM" else "AM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val formattedTime = String.format(Locale.ROOT, "%d:%02d %s", displayHour, minute, period)

        val context = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }
        if (context != null) {
            try {
                StudyReminderScheduler.scheduleDailyStudyReminder(context, hour, minute)
            } catch (_: Throwable) {}
        }

        scope.launch(Dispatchers.IO) {
            try {
                val taskDao = SynapseDatabase.getLearningTaskDao(context)
                taskDao.insertTask(
                    LearningTaskEntity(
                        title = "Study Alarm ($formattedTime)",
                        category = "Study Alarm",
                        duration = "15 min",
                        isCompleted = false
                    )
                )
            } catch (_: Throwable) {}
        }

        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "You",
            text = query,
            timeAgo = "Just now",
            isUser = true
        )
        val replyText = "⏰ Study alarm set for **$formattedTime**! I'll remind you right on time so you can stay consistent with your daily study goals."
        val aiMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "SYNAPSE AI",
            text = replyText,
            timeAgo = "Just now",
            isUser = false,
            actionCommand = "SET_ALARM"
        )
        val newMessages = _uiState.value.messages + userMsg + aiMsg
        _uiState.update {
            it.copy(
                messages = newMessages,
                userQuestion = query,
                isGenerating = false,
                aiThoughtStatus = "⏰ Alarm Set for $formattedTime"
            )
        }
        chatStorage?.saveMessages(currentUserId, newMessages)
        persistCurrentSession(newMessages)
        return true
    }

    private fun handleChapterNoteSaving(query: String): Boolean {
        val lower = query.lowercase(Locale.ROOT).trim()

        val hasSaveIntent = lower.contains("save") || lower.contains("bookmark") ||
                lower.contains("add to notes") || lower.contains("save panu") ||
                (lower.contains("note") && (lower.contains("create") || lower.contains("make") || lower.contains("take")))
        val hasChapterMention = lower.contains("chapter") || lower.contains("ch ") ||
                Regex("""\bch\d\b""").containsMatchIn(lower) ||
                lower.contains("1st") || lower.contains("2nd") || lower.contains("3rd") || lower.contains("4th") || lower.contains("5th") ||
                lower.contains("first") || lower.contains("second") || lower.contains("third") || lower.contains("fourth") || lower.contains("fifth")

        if (!hasSaveIntent || !hasChapterMention) return false

        val trackNum = when {
            lower.contains("python") || lower.contains("coding") || lower.contains("logic") || lower.contains("track 1") -> 101
            lower.contains("sql") || lower.contains("db") || lower.contains("database") || lower.contains("rdbms") || lower.contains("track 2") -> 102
            lower.contains("ai") || lower.contains("tech") || lower.contains("modern") || lower.contains("track 3") -> 103
            lower.contains("aptitude") || lower.contains("quant") || lower.contains("interview") || lower.contains("track 4") -> 104
            else -> {
                when {
                    lastTrack.contains("SQL", ignoreCase = true) -> 102
                    lastTrack.contains("AI", ignoreCase = true) -> 103
                    lastTrack.contains("Aptitude", ignoreCase = true) -> 104
                    else -> 101
                }
            }
        }

        val chNum = when {
            Regex("""(?:chapter|ch\.?)\s*1\b|1st\s+chapter|first\s+chapter|\bch1\b""", RegexOption.IGNORE_CASE).containsMatchIn(lower) -> 1
            Regex("""(?:chapter|ch\.?)\s*2\b|2nd\s+chapter|second\s+chapter|\bch2\b""", RegexOption.IGNORE_CASE).containsMatchIn(lower) -> 2
            Regex("""(?:chapter|ch\.?)\s*3\b|3rd\s+chapter|third\s+chapter|\bch3\b""", RegexOption.IGNORE_CASE).containsMatchIn(lower) -> 3
            Regex("""(?:chapter|ch\.?)\s*4\b|4th\s+chapter|fourth\s+chapter|\bch4\b""", RegexOption.IGNORE_CASE).containsMatchIn(lower) -> 4
            Regex("""(?:chapter|ch\.?)\s*5\b|5th\s+chapter|fifth\s+chapter|\bch5\b""", RegexOption.IGNORE_CASE).containsMatchIn(lower) -> 5
            lower.contains("1st") || lower.contains("first") -> 1
            lower.contains("2nd") || lower.contains("second") -> 2
            lower.contains("3rd") || lower.contains("third") -> 3
            lower.contains("4th") || lower.contains("fourth") -> 4
            lower.contains("5th") || lower.contains("fifth") -> 5
            lower.contains("1") -> 1
            lower.contains("2") -> 2
            lower.contains("3") -> 3
            lower.contains("4") -> 4
            lower.contains("5") -> 5
            else -> 1
        }

        val chapterId = (trackNum * 100) + chNum
        val chapter = BaseCurriculumData.getChapterById(chapterId)
            ?: SubjectCatalogRepository.getInstanceOrNull()?.getChapterById(chapterId)
        val subject = BaseCurriculumData.getSubjectById(trackNum.toString())
            ?: SubjectCatalogRepository.getInstanceOrNull()?.getSubjectByNumericId(trackNum)

        val trackName = subject?.name ?: when (trackNum) {
            101 -> "Python & Coding Logic"
            102 -> "SQL & Database Management"
            103 -> "AI & Modern Tech Concepts"
            else -> "Aptitude & Interview Prep"
        }
        val chapterTitle = chapter?.title ?: "Chapter $chNum Fundamentals"
        val noteTitle = "Chapter $chNum: $chapterTitle"

        val noteContent = buildString {
            appendLine("# $trackName - Chapter $chNum: $chapterTitle")
            appendLine()
            if (!chapter?.summary.isNullOrBlank()) {
                appendLine("### Summary")
                appendLine(chapter?.summary)
                appendLine()
            }
            if (!chapter?.part1Content.isNullOrBlank()) {
                appendLine("### Part 1: Core Fundamentals")
                appendLine(chapter?.part1Content)
                appendLine()
            }
            if (!chapter?.part2Content.isNullOrBlank()) {
                appendLine("### Part 2: Practical Applications & Cheat Sheet")
                appendLine(chapter?.part2Content)
                appendLine()
            }
            if (!chapter?.syllabus.isNullOrEmpty()) {
                appendLine("### Key Concepts")
                chapter?.syllabus?.forEach { appendLine("• $it") }
                appendLine()
            }
            if (!chapter?.coreFormulas.isNullOrEmpty()) {
                appendLine("### Core Rules / Syntax")
                chapter?.coreFormulas?.forEach { appendLine("• `$it`") }
                appendLine()
            }
            if (!chapter?.examTip.isNullOrBlank()) {
                appendLine("### 🎯 Placement Interview Tip")
                appendLine(chapter?.examTip)
            }
        }

        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "You",
            text = query,
            timeAgo = "Just now",
            isUser = true
        )

        val confirmMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "SYNAPSE AI",
            text = "📝 Done! I've saved full study notes for **$trackName — Chapter $chNum: $chapterTitle** to your Notes tab under **Subject Notes**.",
            timeAgo = "Just now",
            isUser = false,
            isSavedNoteCard = true,
            savedNoteTitle = noteTitle
        )

        val newMessages = _uiState.value.messages + userMsg + confirmMsg
        _uiState.update {
            it.copy(
                messages = newMessages,
                userQuestion = query,
                isGenerating = false,
                aiThoughtStatus = "✓ Notes Saved ($trackName Ch $chNum)"
            )
        }
        chatStorage?.saveMessages(currentUserId, newMessages)
        persistCurrentSession(newMessages)

        scope.launch(Dispatchers.IO) {
            val user = sessionManager?.getSessionUser()
            val uid = user?.id ?: currentUserId
            val email = user?.email ?: "student@synapse.ai"
            try {
                SynapseDatabase.getNotesDao().insertNote(
                    NoteEntity(
                        userId = uid,
                        subjectId = trackNum.toString(),
                        chapterId = chapterId,
                        title = noteTitle,
                        content = noteContent,
                        category = "SUBJECT",
                        trackName = trackName,
                        isBookmarked = true,
                        isOfflineCached = true
                    )
                )
            } catch (_: Throwable) {}
            try {
                notesRepository.saveNote(
                    title = noteTitle,
                    content = noteContent,
                    subjectId = trackNum,
                    category = "SUBJECT",
                    userId = uid,
                    email = email
                )
            } catch (_: Throwable) {}
        }

        return true
    }

    private fun isSubjectActionQuery(q: String): Boolean {
        val lower = q.lowercase(Locale.ROOT)
        return (lower.startsWith("open ") || lower.startsWith("go to ") || lower.contains("roadmap")) &&
                (lower.contains("python") || lower.contains("sql") || lower.contains("database") ||
                 lower.contains("ai") || lower.contains("aptitude") || lower.contains("interview") ||
                 lower.contains("coding") || lower.contains("tech") || lower.contains("track") ||
                 lower.contains("computer") || lower.contains("science") || lower.contains("math"))
    }

    private fun isNotesActionQuery(q: String): Boolean {
        val lower = q.lowercase(Locale.ROOT)
        return (lower.contains("notes") && (lower.contains("show") || lower.contains("view") || lower.contains("open") || lower.contains("go to") || lower.contains("my"))) &&
                !isNoteSavingQuery(q)
    }

    private fun isProgressActionQuery(q: String): Boolean {
        val lower = q.lowercase(Locale.ROOT)
        return lower.contains("progress") || lower.contains("my stats") || lower.contains("analytics") || lower.contains("streak") || lower.contains("check progress")
    }

    private fun isProfileActionQuery(q: String): Boolean {
        val lower = q.lowercase(Locale.ROOT)
        return lower.contains("open profile") || lower.contains("view profile") || lower.contains("my profile")
    }

    private fun resolveSubjectFromQuery(q: String): Pair<String, String> {
        val lower = q.lowercase(Locale.ROOT)
        val words = lower.split(Regex("[^a-zA-Z0-9]+")).filter { it.isNotBlank() }
        fun hasWord(w: String) = words.contains(w)

        return when {
            lower.contains("python") || lower.contains("coding") || lower.contains("logic") || hasWord("py") ->
                Pair("101", "Python & Coding Logic")
            lower.contains("sql") || lower.contains("database") || lower.contains("dbms") || lower.contains("rdbms") || lower.contains("query") ->
                Pair("102", "SQL & Database Management")
            lower.contains("ai") || lower.contains("tech") || lower.contains("modern") || lower.contains("machine learning") || lower.contains("ml") || lower.contains("neural") || lower.contains("cloud") || lower.contains("cyber") ->
                Pair("103", "AI & Modern Tech Concepts")
            lower.contains("aptitude") || lower.contains("interview") || lower.contains("quant") || lower.contains("math") || lower.contains("reasoning") || lower.contains("puzzle") || lower.contains("prep") ->
                Pair("104", "Aptitude & Interview Prep")
            else -> Pair("101", "Python & Coding Logic")
        }
    }

    private fun isLengthControlShortenQuery(lower: String): Boolean {
        return lower == "short" || lower == "give me short" || lower == "short ha sollu" ||
               lower == "make it brief" || lower == "chinadha thaa" || lower == "brief" ||
               lower == "summarize" || lower == "in short" || lower == "reply shortly"
    }

    private fun isLengthControlElaborateQuery(lower: String): Boolean {
        return lower == "elaborate" || lower == "explain in detail" || lower == "in detail" ||
               lower == "more detail" || lower == "deep dive" || lower == "detailed explanation"
    }

    fun formatAsTwoMark(message: ChatMessage? = null, customTopic: String? = null) {
        val topic = customTopic?.ifBlank { null }
            ?: message?.matchedTopic
            ?: message?.matchedChapter
            ?: lastTopic.ifBlank {
                _uiState.value.messages.lastOrNull { !it.isUser && it.matchedTopic != null }?.matchedTopic ?: "Academic Concept"
            }
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "You",
            text = "Give 2 mark answer for $topic",
            timeAgo = "Just now",
            isUser = true
        )
        val twoMarkText = buildString {
            appendLine("* **Definition & Core Rule:** $topic states the fundamental syntax, logic, or principle essential for placement technical rounds.")
            appendLine("* **Core Syntax / Formula:** Canonical programming expression or mathematical formulation with clear parameters.")
            append("* **Placement Interview Key:** The critical edge-case or efficiency trade-off interviewers evaluate.")
        }
        val aiMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "SYNAPSE AI",
            text = twoMarkText,
            timeAgo = "Just now",
            isUser = false,
            matchedTopic = topic,
            isConceptAnswer = true
        )
        val newMessages = _uiState.value.messages + userMsg + aiMsg
        _uiState.update {
            it.copy(
                messages = newMessages,
                userQuestion = userMsg.text,
                aiAnswerParagraph1 = twoMarkText,
                aiAnswerParagraph2 = "",
                aiThoughtStatus = "✓ 2-Mark Board Exam Format ($topic)",
                isGenerating = false
            )
        }
        persistCurrentSession(newMessages)
    }

    fun formatAsFiveMark(message: ChatMessage? = null, customTopic: String? = null) {
        val topic = customTopic?.ifBlank { null }
            ?: message?.matchedTopic
            ?: message?.matchedChapter
            ?: lastTopic.ifBlank {
                _uiState.value.messages.lastOrNull { !it.isUser && it.matchedTopic != null }?.matchedTopic ?: "Academic Concept"
            }
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "You",
            text = "Give 5 mark answer for $topic",
            timeAgo = "Just now",
            isUser = true
        )
        val fiveMarkText = buildString {
            appendLine("* **1. Principle & Theoretical Framework:**")
            appendLine("$topic is a foundational concept in the Tamil Nadu State Board syllabus governing analytical and empirical physical transformations.")
            appendLine()
            appendLine("* **2. Working Mechanism & Detailed Explanation:**")
            appendLine("Under standard conditions, the system transitions through well-defined thermodynamic/mathematical states where equilibrium is maintained at each step.")
            appendLine()
            appendLine("* **3. Mathematical Derivation & Core Formula:**")
            appendLine("Standard governing equation: Phi = sum(lambda_i * x_i), where each variable corresponds to canonical physical properties in SI units.")
            appendLine()
            appendLine("* **4. Practical Applications & Illustrative Example:**")
            appendLine("Widely applied in engineering systems, scientific instrumentation, chemical syntheses, and real-world technology.")
            appendLine()
            appendLine("* **5. Conclusion & Exam Takeaways:**")
            append("Remember to include a neatly labeled schematic diagram, step-by-step derivation, and final units for complete 5 marks.")
        }
        val aiMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "SYNAPSE AI",
            text = fiveMarkText,
            timeAgo = "Just now",
            isUser = false,
            matchedTopic = topic,
            isConceptAnswer = true
        )
        val newMessages = _uiState.value.messages + userMsg + aiMsg
        _uiState.update {
            it.copy(
                messages = newMessages,
                userQuestion = userMsg.text,
                aiAnswerParagraph1 = fiveMarkText,
                aiAnswerParagraph2 = "",
                aiThoughtStatus = "✓ 5-Mark Board Exam Format ($topic)",
                isGenerating = false
            )
        }
        persistCurrentSession(newMessages)
    }

    private fun isTwoMarkQuery(lower: String): Boolean {
        return lower == "2m" || lower == "2 mark" || lower == "2 marks" ||
               lower == "give 2 mark answer" || lower == "give 2 mark" ||
               lower.contains("2 mark") || lower.contains("2 marks") ||
               lower.contains("two mark") || lower.contains("two marks") ||
               lower.startsWith("2m ") || lower.endsWith(" 2m")
    }

    private fun isFiveMarkQuery(lower: String): Boolean {
        return lower == "5m" || lower == "5 mark" || lower == "5 marks" ||
               lower == "give 5 mark answer" || lower == "give 5 mark" ||
               lower.contains("5 mark") || lower.contains("5 marks") ||
               lower.contains("five mark") || lower.contains("five marks") ||
               lower.startsWith("5m ") || lower.endsWith(" 5m")
    }

    private fun extractTopicFromExamQuery(query: String): String {
        val lower = query.lowercase(Locale.ROOT)
        return when {
            lower.contains(" for ") -> query.substringAfter(" for ", "").trim()
            lower.contains(" on ") -> query.substringAfter(" on ", "").trim()
            lower.contains(" about ") -> query.substringAfter(" about ", "").trim()
            else -> ""
        }
    }

    override fun onCleared() {
        super.onCleared()
        streamingJob?.cancel()
        voiceAssistant?.destroy()
        voiceAssistant = null
    }
}
