package com.siamts.synapse.ui.algorithms

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Algorithms Lesson Screen state and navigation tabs.
 */
class AlgorithmsLessonViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AlgorithmsLessonUiState())
    val uiState: StateFlow<AlgorithmsLessonUiState> = _uiState.asStateFlow()

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
