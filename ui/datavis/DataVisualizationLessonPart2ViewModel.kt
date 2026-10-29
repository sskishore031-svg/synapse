package com.siamts.synapse.ui.datavis

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Data Visualization Lesson Part 2 state.
 */
class DataVisualizationLessonPart2ViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DataVisualizationLessonPart2UiState())
    val uiState: StateFlow<DataVisualizationLessonPart2UiState> = _uiState.asStateFlow()

    fun onSubTabSelected(index: Int) {
        _uiState.update { it.copy(selectedSubTab = index) }
    }
}
