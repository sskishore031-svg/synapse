package com.siamts.synapse.ui.chemistry

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Chemical Bonding Lesson Screen state.
 */
class ChemicalBondingLessonViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChemicalBondingLessonUiState())
    val uiState: StateFlow<ChemicalBondingLessonUiState> = _uiState.asStateFlow()

    fun onSubTabSelected(index: Int) {
        _uiState.update { it.copy(selectedSubTab = index) }
    }

    fun onNextClicked() {
        // Handle Next lesson transition
    }

    fun onPreviousClicked() {
        // Handle Previous lesson transition
    }
}
