package com.siamts.synapse.ui.chemistry

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Chemical Bonding Lesson Part 2 state.
 */
class ChemicalBondingLessonPart2ViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChemicalBondingLessonPart2UiState())
    val uiState: StateFlow<ChemicalBondingLessonPart2UiState> = _uiState.asStateFlow()

    fun onSubTabSelected(index: Int) {
        _uiState.update { it.copy(selectedSubTab = index) }
    }
}
