package com.siamts.synapse.ui.tutor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.ai.LocalAIEngine
import com.siamts.synapse.ai.LocalAiStreamToken
import com.siamts.synapse.ai.SlmInferenceEngine
import com.siamts.synapse.ai.VoiceAssistantManager
import com.siamts.synapse.data.local.AiChatStorage
import com.siamts.synapse.data.local.ChatSessionEntity
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.AiTutorRepository
import com.siamts.synapse.data.repository.AiTutorRepositoryImpl
import com.siamts.synapse.data.repository.NotesRepository
import com.siamts.synapse.data.repository.NotesRepositoryImpl
import com.siamts.synapse.data.repository.UserPreferencesRepository
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

    // Dynamic length control tracking (lastTopic & lastResponseText)
    var lastTopic: String = ""
        private set
    var lastResponseText: String = ""
        private set

    init {
        loadHistory()
    }

    fun loadHistory() {
        scope.launch {
            val user = sessionManager?.getSessionUser()
            currentUserId = user?.id ?: 1
            val email = user?.email ?: "student@synapse.ai"

            // Sync user preferences with localAiEngine
            // CRITICAL: NEVER inject user?.name from SessionManager into localAiEngine.preferredName!
            // Strictly opt-in custom name only when explicitly requested by user in chat.
            val savedPrefName = userPreferencesRepository?.getChatPreferredName().orEmpty()
            localAiEngine.preferredName = savedPrefName
            localAiEngine.includeFormulas = userPreferencesRepository?.getIncludeFormulas() ?: true
            localAiEngine.includeExamples = userPreferencesRepository?.getIncludeExamples() ?: true
            localAiEngine.useEmojis = userPreferencesRepository?.getUseEmojis() ?: false

            val headerTag = if (savedPrefName.isNotBlank()) {
                "PERSONALIZED FOR ${savedPrefName.uppercase()}"
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

        // --- Dynamic Length Control Checks ("give me short", "short ha sollu", "chinadha thaa") ---
        val lowerQ = query.lowercase(Locale.ROOT).trim()

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

        // B. Quiz Launch Command
        if (isQuizActionQuery(query)) {
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
                text = "🚀 Launching your $subName Quiz now! Get ready to test your conceptual mastery.",
                timeAgo = "Just now",
                isUser = false,
                actionCommand = "NAVIGATE_QUIZ"
            )
            _uiState.update {
                it.copy(
                    messages = it.messages + userMsg + actionMsg,
                    userQuestion = query,
                    isGenerating = false,
                    aiThoughtStatus = "🚀 Launching $subName Quiz"
                )
            }
            chatStorage?.saveMessages(currentUserId, _uiState.value.messages)
            persistCurrentSession(_uiState.value.messages)
            scope.launch {
                _actionEvent.emit(AiUiAction.NavigateToQuiz(subId, subName))
            }
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
            notesRepository.saveNote(
                title = title,
                content = content,
                subjectId = 1,
                chapterId = null,
                category = "SUBJECT",
                userId = uid,
                email = email
            )
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
        return (lower.contains("quiz") && (lower.contains("start") || lower.contains("open") || lower.contains("take") || lower.contains("launch") || lower.contains("test"))) ||
                lower == "quiz" || lower == "take a quiz" || lower == "start quiz"
    }

    private fun isSubjectActionQuery(q: String): Boolean {
        val lower = q.lowercase(Locale.ROOT)
        return (lower.startsWith("open ") || lower.startsWith("go to ") || lower.contains("roadmap")) &&
                (lower.contains("math") || lower.contains("physics") || lower.contains("chemistry") ||
                 lower.contains("computer") || lower.contains("science") || lower.contains("biology") ||
                 lower.contains("social") || lower.contains("commerce") || lower.contains("economics"))
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
            // Class 10
            lower.contains("10") && (lower.contains("soc") || lower.contains("history") || lower.contains("geo") || lower.contains("civic")) -> Pair("1003", "10th Social Science")
            lower.contains("10") && lower.contains("math") -> Pair("1001", "10th Mathematics")
            lower.contains("10") && lower.contains("sci") -> Pair("1002", "10th Science")

            // Class 11
            lower.contains("11") && lower.contains("phys") -> Pair("1102", "11th Physics")
            lower.contains("11") && lower.contains("chem") -> Pair("1103", "11th Chemistry")
            lower.contains("11") && lower.contains("bio") -> Pair("1104", "11th Biology")
            lower.contains("11") && (lower.contains("computer") || hasWord("cs") || lower.contains("python")) -> Pair("1105", "11th Computer Science")
            lower.contains("11") && (lower.contains("comm") || lower.contains("account")) -> Pair("1106", "11th Commerce & Accountancy")
            lower.contains("11") && lower.contains("econ") -> Pair("1107", "11th Economics")
            lower.contains("11") && lower.contains("math") -> Pair("1101", "11th Mathematics")

            // Class 12
            lower.contains("12") && lower.contains("phys") -> Pair("1202", "12th Physics")
            lower.contains("12") && lower.contains("chem") -> Pair("1203", "12th Chemistry")
            lower.contains("12") && lower.contains("bio") -> Pair("1204", "12th Biology")
            lower.contains("12") && (lower.contains("computer") || hasWord("cs") || lower.contains("python")) -> Pair("1205", "12th Computer Science")
            lower.contains("12") && (lower.contains("comm") || lower.contains("account")) -> Pair("1206", "12th Commerce & Accountancy")
            lower.contains("12") && lower.contains("econ") -> Pair("1207", "12th Economics")
            lower.contains("12") && lower.contains("math") -> Pair("1201", "12th Mathematics")

            // Defaults without class prefix (specific subjects first)
            lower.contains("computer") || hasWord("cs") || lower.contains("python") -> Pair("1205", "12th Computer Science")
            lower.contains("social") || lower.contains("history") || lower.contains("geography") || lower.contains("civics") -> Pair("1003", "10th Social Science")
            lower.contains("math") -> Pair("1001", "10th Mathematics")
            lower.contains("phys") -> Pair("1202", "12th Physics")
            lower.contains("chem") -> Pair("1203", "12th Chemistry")
            lower.contains("bio") -> Pair("1204", "12th Biology")
            lower.contains("commerce") || lower.contains("account") -> Pair("1206", "12th Commerce & Accountancy")
            lower.contains("econ") -> Pair("1207", "12th Economics")
            lower.contains("science") -> Pair("1002", "10th Science")
            else -> Pair("1001", "10th Mathematics")
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
