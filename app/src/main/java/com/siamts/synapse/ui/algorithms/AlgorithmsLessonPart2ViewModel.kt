package com.siamts.synapse.ui.algorithms

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Algorithms Lesson Part 2 state.
 */
class AlgorithmsLessonPart2ViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AlgorithmsLessonPart2UiState())
    val uiState: StateFlow<AlgorithmsLessonPart2UiState> = _uiState.asStateFlow()

    fun onSubTabSelected(index: Int) {
        _uiState.update { it.copy(selectedSubTab = index) }
    }
}
