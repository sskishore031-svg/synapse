package com.siamts.synapse.ui.tutor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.AiTutorRepository
import com.siamts.synapse.data.repository.AiTutorRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing FRIDAY AI Tutor chat state, real-time backend Q&A, and persistent messages.
 */
class AiTutorViewModel(
    private val repository: AiTutorRepository = AiTutorRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull()
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

        _uiState.update {
            it.copy(
                userQuestion = query,
                inputQuery = "",
                aiThoughtStatus = "✨ FRIDAY is analyzing your question..."
            )
        }

        viewModelScope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val email = user?.email ?: "student@synapse.ai"

            val result = repository.askQuestion(question = query, userId = userId, email = email)
            result.onSuccess { res ->
                val data = res.data
                _uiState.update {
                    it.copy(
                        aiAnswerParagraph1 = data?.paragraph1 ?: "Here is what I found about that topic.",
                        aiAnswerParagraph2 = data?.paragraph2 ?: "",
                        aiThoughtStatus = "✨ FRIDAY AI · Ready for next question"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        aiThoughtStatus = "⚠️ Failed to get answer: ${err.localizedMessage}"
                    )
                }
            }
        }
    }

    fun onPromptClicked(prompt: String) {
        _uiState.update { it.copy(inputQuery = prompt) }
        onSendMessage()
    }
}
