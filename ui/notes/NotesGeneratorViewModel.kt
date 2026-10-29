package com.siamts.synapse.ui.notes

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing AI Notes Generator input, document upload, and save state.
 */
class NotesGeneratorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotesGeneratorUiState())
    val uiState: StateFlow<NotesGeneratorUiState> = _uiState.asStateFlow()

    fun onTopicInputChanged(topic: String) {
        _uiState.update { it.copy(topicInput = topic) }
    }

    fun onGenerateNotesClicked() {
        if (_uiState.value.topicInput.isNotBlank()) {
            _uiState.update { it.copy(noteTitle = it.topicInput) }
        }
    }

    fun onSaveClicked() {
        _uiState.update { it.copy(isSaved = true) }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }
}
