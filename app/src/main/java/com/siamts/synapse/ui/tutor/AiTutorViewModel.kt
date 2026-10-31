package com.siamts.synapse.ui.tutor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.ai.LocalAIEngine
import com.siamts.synapse.data.local.AiChatStorage
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.AiTutorRepository
import com.siamts.synapse.data.repository.AiTutorRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel managing FRIDAY AI Tutor chat state, multi-turn conversation memory,
 * local session persistence, and real-time on-device LocalAIEngine Q&A.
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
                        aiThoughtStatus = "✨ FRIDAY AI · Ready to continue your conversation"
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
                    senderName = "FRIDAY AI",
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
                            senderName = if (dto.senderType == "user") "You" else "FRIDAY AI",
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
        val query = _uiState.value.inputQuery.trim()
        if (query.isBlank()) return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "You",
            text = query,
            timeAgo = "Just now",
            isUser = true
        )

        // 1. Immediately append user message to chat history
        val listWithUser = _uiState.value.messages + userMessage

        _uiState.update {
            it.copy(
                userQuestion = query,
                inputQuery = "",
                messages = listWithUser,
                aiThoughtStatus = "✨ FRIDAY is reasoning with local context..."
            )
        }

        // 2. Generate context-aware on-device response
        val localResponse = localAiEngine.generateResponse(query)

        val aiMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "FRIDAY AI",
            text = localResponse.paragraph1,
            paragraph2 = localResponse.paragraph2.ifBlank { null },
            timeAgo = "Just now",
            isUser = false,
            matchedSubject = localResponse.matchedSubject,
            matchedChapter = localResponse.matchedChapter,
            matchedTopic = localResponse.matchedTopic
        )

        val finalMessages = listWithUser + aiMessage

        val statusText = when {
            localResponse.isGreeting -> "✨ FRIDAY AI · Ready to explore subjects with you"
            localResponse.isFollowUp -> "✨ FRIDAY AI · Context Follow-up (${localResponse.matchedTopic})"
            localResponse.matchedTopic != null -> "✨ FRIDAY AI · On-Device (${localResponse.matchedSubject} · ${localResponse.matchedChapter})"
            else -> "✨ FRIDAY AI · Knowledge Companion"
        }

        _uiState.update {
            it.copy(
                messages = finalMessages,
                aiAnswerParagraph1 = localResponse.paragraph1,
                aiAnswerParagraph2 = localResponse.paragraph2,
                aiThoughtStatus = statusText
            )
        }

        // 3. Persist messages locally
        chatStorage?.saveMessages(currentUserId, finalMessages)

        // 4. Asynchronously sync to backend repository
        viewModelScope.launch {
            try {
                val user = sessionManager?.getSessionUser()
                val email = user?.email ?: "student@synapse.ai"
                repository.askQuestion(question = query, userId = currentUserId, email = email)
            } catch (_: Exception) {
                // Backend offline or unreachable; local on-device engine has already handled it
            }
        }
    }

    fun onClearChat() {
        chatStorage?.clearMessages(currentUserId)
        localAiEngine.resetContext()

        val welcomeMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "FRIDAY AI",
            text = "Good morning! I am your SYNAPSE AI tutor. Which subject or chapter would you like to explore today? (Mathematics, Computer Science, Data Science, AI, Physics, Chemistry)",
            paragraph2 = "Feel free to ask any question across our 6 STEM subjects, or tap a quick prompt below to get started!",
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
                aiThoughtStatus = "✨ FRIDAY AI · New session started. Ready for your questions!"
            )
        }

        chatStorage?.saveMessages(currentUserId, listOf(welcomeMessage))
    }

    fun onPromptClicked(prompt: String) {
        _uiState.update { it.copy(inputQuery = prompt) }
        onSendMessage()
    }
}
