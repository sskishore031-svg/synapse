package com.siamts.synapse.ui.tutor

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing AI Tutor chat state and messages.
 */
class AiTutorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AiTutorUiState())
    val uiState: StateFlow<AiTutorUiState> = _uiState.asStateFlow()

    fun onInputQueryChanged(query: String) {
        _uiState.update { it.copy(inputQuery = query) }
    }

    fun onSendMessage() {
        if (_uiState.value.inputQuery.isNotBlank()) {
            _uiState.update { currentState ->
                currentState.copy(
                    userQuestion = currentState.inputQuery,
                    inputQuery = ""
                )
            }
        }
    }

    fun onPromptClicked(prompt: String) {
        _uiState.update { it.copy(inputQuery = prompt) }
    }
}
