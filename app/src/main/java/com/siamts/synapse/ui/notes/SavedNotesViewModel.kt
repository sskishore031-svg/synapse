package com.siamts.synapse.ui.notes

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Saved & Downloaded Notes search and actions.
 */
class SavedNotesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SavedNotesUiState())
    val uiState: StateFlow<SavedNotesUiState> = _uiState.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onOpenNoteClicked(id: String) {
        // Open note
    }

    fun onDownloadNoteClicked(id: String) {
        // Download note
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }
}
