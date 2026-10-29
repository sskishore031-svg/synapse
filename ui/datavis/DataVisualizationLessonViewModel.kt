package com.siamts.synapse.ui.datavis

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Data Visualization Lesson Screen state.
 */
class DataVisualizationLessonViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DataVisualizationLessonUiState())
    val uiState: StateFlow<DataVisualizationLessonUiState> = _uiState.asStateFlow()

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
