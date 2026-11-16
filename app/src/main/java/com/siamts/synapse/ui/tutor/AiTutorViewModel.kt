package com.siamts.synapse.ui.tutor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.ai.LocalAIEngine
import com.siamts.synapse.ai.LocalAiStreamToken
import com.siamts.synapse.ai.VoiceAssistantManager
import com.siamts.synapse.data.local.AiChatStorage
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.AiTutorRepository
import com.siamts.synapse.data.repository.AiTutorRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel managing SYNAPSE AI Tutor chat state, multi-turn conversation memory,
 * local session persistence, real-time on-device LocalAIEngine reasoning, progressive
 * Kotlin Flow token streaming, and two-way Live Voice conversation.
 */
class AiTutorViewModel(
    private val repository: AiTutorRepository = AiTutorRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull(),
    private val localAiEngine: LocalAIEngine = LocalAIEngine.getInstance(),
    private val chatStorage: AiChatStorage? = try { AiChatStorage.getInstance() } catch (_: Exception) { null },
    private val externalScope: kotlinx.coroutines.CoroutineScope? = null
) : ViewModel() {

    private val scope: kotlinx.coroutines.CoroutineScope get() = externalScope ?: viewModelScope

    private val _uiState = MutableStateFlow(AiTutorUiState())
    val uiState: StateFlow<AiTutorUiState> = _uiState.asStateFlow()

    private var currentUserId: Int = 1
    private var streamingJob: Job? = null
    private var voiceAssistant: VoiceAssistantManager? = null

    init {
        loadHistory()
    }

    fun loadHistory() {
        scope.launch {
            val user = sessionManager?.getSessionUser()
            currentUserId = user?.id ?: 1
            val email = user?.email ?: "student@synapse.ai"
            val studentName = user?.name?.ifBlank { "STUDENT" } ?: "STUDENT"

            _uiState.update { it.copy(headerTag = "PERSONALIZED FOR ${studentName.uppercase()}") }

            // 1. Check local session persistence
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

            // 2. Query remote backend history; if empty, leave empty so minimal welcome emblem displays cleanly
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
                    _uiState.update { it.copy(messages = emptyList()) }
                }
            }.onFailure {
                _uiState.update { it.copy(messages = emptyList()) }
            }
        }
    }

    fun onInputQueryChanged(query: String) {
        _uiState.update { it.copy(inputQuery = query) }
    }

    /**
     * Executes instantaneous message sending (0ms UI lag) and starts fast on-device token streaming.
     */
    fun onSendMessage(overrideQuery: String? = null) {
        if (_uiState.value.isGenerating) return

        val query = (overrideQuery ?: _uiState.value.inputQuery).trim()
        if (query.isBlank()) return

        // 1. Instantly clear input query on Main thread with 0ms lag
        _uiState.update { it.copy(inputQuery = "") }

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

                        // Save conversation history to local storage
                        chatStorage?.saveMessages(currentUserId, finalizedMessages)

                        // Async remote sync
                        try {
                            val user = sessionManager?.getSessionUser()
                            val email = user?.email ?: "student@synapse.ai"
                            repository.askQuestion(question = query, userId = currentUserId, email = email)
                        } catch (_: Exception) {
                            // Offline fallback handled on-device
                        }
                    }
                }
            }
        }
    }

    /**
     * Clears the current chat session completely, returning to the centered clean welcome emblem.
     */
    fun onClearChat() {
        streamingJob?.cancel()
        chatStorage?.clearMessages(currentUserId)
        localAiEngine.resetContext()
        voiceAssistant?.stopSpeaking()

        _uiState.update {
            it.copy(
                messages = emptyList(),
                inputQuery = "",
                userQuestion = "",
                aiAnswerParagraph1 = "",
                aiAnswerParagraph2 = "",
                aiThoughtStatus = "✨ SYNAPSE AI · New session started. Ready for your questions!",
                isGenerating = false,
                currentlySpeakingMessageId = null
            )
        }
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

    override fun onCleared() {
        super.onCleared()
        streamingJob?.cancel()
        voiceAssistant?.destroy()
        voiceAssistant = null
    }
}
