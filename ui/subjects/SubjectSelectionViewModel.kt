package com.siamts.synapse.ui.subjects

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Subject Selection state and search.
 */
class SubjectSelectionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SubjectSelectionUiState())
    val uiState: StateFlow<SubjectSelectionUiState> = _uiState.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onSubjectClicked(subjectId: String) {
        // Handle subject selection
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }
}
