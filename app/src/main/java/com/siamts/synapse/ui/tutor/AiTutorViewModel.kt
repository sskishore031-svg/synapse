package com.siamts.synapse.ui.tutor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.ai.LocalAIEngine
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.AiTutorRepository
import com.siamts.synapse.data.repository.AiTutorRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing FRIDAY AI Tutor chat state, real-time on-device LocalAIEngine Q&A, and persistent messages.
 */
class AiTutorViewModel(
    private val repository: AiTutorRepository = AiTutorRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull(),
    private val localAiEngine: LocalAIEngine = LocalAIEngine.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiTutorUiState())
    val uiState: StateFlow<AiTutorUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val email = user?.email ?: "student@synapse.ai"
            val studentName = user?.name?.ifBlank { "STUDENT" } ?: "STUDENT"

            _uiState.update { it.copy(headerTag = "PERSONALIZED FOR ${studentName.uppercase()}") }

            val historyResult = repository.getHistory(userId = userId, email = email)
            historyResult.onSuccess { response ->
                val msgs = response.data?.messages.orEmpty()
                if (msgs.isNotEmpty()) {
                    // Find latest user and AI message
                    val lastUser = msgs.lastOrNull { it.senderType == "user" }
                    val lastAi = msgs.lastOrNull { it.senderType == "ai" }
                    if (lastUser != null && lastAi != null) {
                        val parts = lastAi.message.split("\n\n")
                        _uiState.update {
                            it.copy(
                                userQuestion = lastUser.message,
                                aiAnswerParagraph1 = parts.getOrNull(0) ?: lastAi.message,
                                aiAnswerParagraph2 = parts.getOrNull(1) ?: "",
                                aiThoughtStatus = "✨ FRIDAY AI · Ready to help you learn"
                            )
                        }
                    }
                }
            }
        }
    }

    fun onInputQueryChanged(query: String) {
        _uiState.update { it.copy(inputQuery = query) }
    }

    fun onSendMessage() {
        val query = _uiState.value.inputQuery.trim()
        if (query.isBlank()) return

        // 1. Instantly generate on-device response from local dataset
        val localResponse = localAiEngine.generateResponse(query)

        val statusText = when {
            localResponse.isGreeting -> "✨ FRIDAY AI · Ready to explore subjects with you"
            localResponse.matchedTopic != null -> "✨ FRIDAY AI · On-Device (${localResponse.matchedSubject} · ${localResponse.matchedChapter})"
            else -> "✨ FRIDAY AI · Knowledge Companion"
        }

        _uiState.update {
            it.copy(
                userQuestion = query,
                inputQuery = "",
                aiAnswerParagraph1 = localResponse.paragraph1,
                aiAnswerParagraph2 = localResponse.paragraph2,
                aiThoughtStatus = statusText
            )
        }

        // 2. Asynchronously sync to backend repository without disrupting on-device response
        viewModelScope.launch {
            try {
                val user = sessionManager?.getSessionUser()
                val userId = user?.id ?: 1
                val email = user?.email ?: "student@synapse.ai"
                repository.askQuestion(question = query, userId = userId, email = email)
            } catch (_: Exception) {
                // Backend offline or unreachable; on-device engine has already successfully answered
            }
        }
    }

    fun onPromptClicked(prompt: String) {
        _uiState.update { it.copy(inputQuery = prompt) }
        onSendMessage()
    }
}
