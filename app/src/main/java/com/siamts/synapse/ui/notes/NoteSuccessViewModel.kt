package com.siamts.synapse.ui.notes

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel managing Note Saved and Downloaded Success Screens state.
 */
class NoteSuccessViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NoteSuccessUiState())
    val uiState: StateFlow<NoteSuccessUiState> = _uiState.asStateFlow()
}
