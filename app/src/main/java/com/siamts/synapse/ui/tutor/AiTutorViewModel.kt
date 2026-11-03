package com.siamts.synapse.ui.tutor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.ai.LocalAIEngine
import com.siamts.synapse.data.local.AiChatStorage
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.AiTutorRepository
import com.siamts.synapse.data.repository.AiTutorRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * ViewModel managing SYNAPSE AI Tutor chat state, multi-turn conversation memory,
 * local session persistence, real-time on-device LocalAIEngine reasoning, and
 * ChatGPT-style progressive streaming typing effect.
 */
class AiTutorViewModel(
    private val repository: AiTutorRepository = AiTutorRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull(),
    private val localAiEngine: LocalAIEngine = LocalAIEngine.getInstance(),
    private val chatStorage: AiChatStorage? = try { AiChatStorage.getInstance() } catch (_: Exception) { null }
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiTutorUiState())
    val uiState: StateFlow<AiTutorUiState> = _uiState.asStateFlow()

    private var currentUserId: Int = 1
    private var streamingJob: Job? = null

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            val user = sessionManager?.getSessionUser()
            currentUserId = user?.id ?: 1
            val email = user?.email ?: "student@synapse.ai"
            val studentName = user?.name?.ifBlank { "STUDENT" } ?: "STUDENT"

            _uiState.update { it.copy(headerTag = "PERSONALIZED FOR ${studentName.uppercase()}") }

            // 1. First check local session persistence
            val localSavedMessages = chatStorage?.loadMessages(currentUserId).orEmpty()
            if (localSavedMessages.isNotEmpty()) {
                val lastUser = localSavedMessages.lastOrNull { it.isUser }
                val lastAi = localSavedMessages.lastOrNull { !it.isUser }

                _uiState.update {
                    it.copy(
                        messages = localSavedMessages,
                        userQuestion = lastUser?.text ?: it.userQuestion,
                        aiAnswerParagraph1 = lastAi?.text ?: it.aiAnswerParagraph1,
                        aiAnswerParagraph2 = lastAi?.paragraph2 ?: it.aiAnswerParagraph2,
                        aiThoughtStatus = "✨ SYNAPSE AI · Ready to continue your conversation"
                    )
                }
                return@launch
            }

            // 2. Fall back to remote backend history or default conversation
            val defaultInitialMessages = listOf(
                ChatMessage(
                    id = UUID.randomUUID().toString(),
                    senderName = "You",
                    text = "How does gradient descent help a neural network learn?",
                    timeAgo = "Earlier",
                    isUser = true
                ),
                ChatMessage(
                    id = UUID.randomUUID().toString(),
                    senderName = "SYNAPSE AI",
                    text = "📘 **Concept & Definition** (Artificial Intelligence · Neural Networks)\n\nThink of it like walking downhill in the fog: you take small steps in the direction that slopes downward the most.",
                    paragraph2 = "⚡ **Key Formula & Takeaway**\n\nA neural network adjusts its weights using W := W - η·∇W(Loss) to minimize prediction error. The learning rate controls how big each step is.",
                    timeAgo = "Just now",
                    isUser = false,
                    matchedSubject = "Artificial Intelligence",
                    matchedChapter = "Neural Networks",
                    matchedTopic = "Gradient Descent"
                )
            )

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
                    _uiState.update { it.copy(messages = converted) }
                    chatStorage?.saveMessages(currentUserId, converted)
                } else {
                    _uiState.update { it.copy(messages = defaultInitialMessages) }
                    chatStorage?.saveMessages(currentUserId, defaultInitialMessages)
                }
            }.onFailure {
                _uiState.update { it.copy(messages = defaultInitialMessages) }
                chatStorage?.saveMessages(currentUserId, defaultInitialMessages)
            }
        }
    }

    fun onInputQueryChanged(query: String) {
        _uiState.update { it.copy(inputQuery = query) }
    }

    fun onSendMessage() {
        if (_uiState.value.isGenerating) return

        val query = _uiState.value.inputQuery.trim()
        if (query.isBlank()) return

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

        val listWithUserAndAi = _uiState.value.messages + userMessage + initialAiMessage

        _uiState.update {
            it.copy(
                userQuestion = query,
                inputQuery = "",
                messages = listWithUserAndAi,
                isGenerating = true,
                aiThoughtStatus = "✨ SYNAPSE AI is thinking..."
            )
        }

        streamingJob?.cancel()
        streamingJob = viewModelScope.launch {
            // 1. Generate on-device response in Default background dispatcher
            val localResponse = withContext(Dispatchers.Default) {
                localAiEngine.generateResponse(query)
            }

            // 2. Realistic neural processing pause before streaming starts
            delay(180L)

            // 3. Progressive streaming for paragraph 1 word-by-word
            val p1Words = localResponse.paragraph1.split(Regex("\\s+")).filter { it.isNotEmpty() }
            val p1Builder = StringBuilder()

            for (i in p1Words.indices) {
                if (i > 0) p1Builder.append(" ")
                p1Builder.append(p1Words[i])
                val currentP1 = p1Builder.toString()

                _uiState.update { state ->
                    val updatedMessages = state.messages.map { msg ->
                        if (msg.id == aiMessageId) {
                            msg.copy(
                                text = currentP1,
                                matchedSubject = localResponse.matchedSubject,
                                matchedChapter = localResponse.matchedChapter,
                                matchedTopic = localResponse.matchedTopic
                            )
                        } else msg
                    }
                    state.copy(
                        messages = updatedMessages,
                        aiAnswerParagraph1 = currentP1
                    )
                }
                delay(24L)
            }

            // 4. Progressive streaming for paragraph 2 word-by-word (if non-blank)
            val p2Text = localResponse.paragraph2.trim()
            if (p2Text.isNotEmpty()) {
                delay(40L)
                val p2Words = p2Text.split(Regex("\\s+")).filter { it.isNotEmpty() }
                val p2Builder = StringBuilder()

                for (i in p2Words.indices) {
                    if (i > 0) p2Builder.append(" ")
                    p2Builder.append(p2Words[i])
                    val currentP2 = p2Builder.toString()

                    _uiState.update { state ->
                        val updatedMessages = state.messages.map { msg ->
                            if (msg.id == aiMessageId) {
                                msg.copy(paragraph2 = currentP2)
                            } else msg
                        }
                        state.copy(
                            messages = updatedMessages,
                            aiAnswerParagraph2 = currentP2
                        )
                    }
                    delay(24L)
                }
            }

            // 5. Finalize status and message state
            val statusText = when {
                localResponse.isGreeting -> "✨ SYNAPSE AI · Ready to explore subjects with you"
                localResponse.isFollowUp -> "✨ SYNAPSE AI · Context Follow-up (${localResponse.matchedTopic})"
                localResponse.matchedTopic != null -> "✨ SYNAPSE AI · On-Device (${localResponse.matchedSubject} · ${localResponse.matchedChapter})"
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
                matchedTopic = localResponse.matchedTopic
            )

            val finalizedMessages = _uiState.value.messages.map {
                if (it.id == aiMessageId) completedAiMessage else it
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

            // 6. Persist conversation history locally
            chatStorage?.saveMessages(currentUserId, finalizedMessages)

            // 7. Asynchronously sync to backend repository
            try {
                val user = sessionManager?.getSessionUser()
                val email = user?.email ?: "student@synapse.ai"
                repository.askQuestion(question = query, userId = currentUserId, email = email)
            } catch (_: Exception) {
                // Offline fallback - on-device engine already handled response completely
            }
        }
    }

    fun onClearChat() {
        streamingJob?.cancel()
        chatStorage?.clearMessages(currentUserId)
        localAiEngine.resetContext()

        val welcomeMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "SYNAPSE AI",
            text = "Good morning! I am your SYNAPSE AI tutor. Which subject or chapter would you like to explore today? (Mathematics, Computer Science, Data Science, AI, Physics, Chemistry, Biology, History, Geography)",
            paragraph2 = "Feel free to ask any question across our academic subjects, or tap a quick prompt below to get started!",
            timeAgo = "Just now",
            isUser = false
        )

        _uiState.update {
            it.copy(
                messages = listOf(welcomeMessage),
                inputQuery = "",
                userQuestion = "",
                aiAnswerParagraph1 = welcomeMessage.text,
                aiAnswerParagraph2 = welcomeMessage.paragraph2.orEmpty(),
                aiThoughtStatus = "✨ SYNAPSE AI · New session started. Ready for your questions!",
                isGenerating = false
            )
        }

        chatStorage?.saveMessages(currentUserId, listOf(welcomeMessage))
    }

    fun onPromptClicked(prompt: String) {
        if (_uiState.value.isGenerating) return
        _uiState.update { it.copy(inputQuery = prompt) }
        onSendMessage()
    }
}
