package com.siamts.synapse.ui.lesson

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Lesson Part 2 state and navigation.
 */
class LessonPart2ViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LessonPart2UiState())
    val uiState: StateFlow<LessonPart2UiState> = _uiState.asStateFlow()

    fun onSubTabSelected(index: Int) {
        _uiState.update { it.copy(selectedSubTab = index) }
    }

    fun onNextClicked() {
        // Handle next navigation
    }

    fun onPreviousClicked() {
        // Handle previous navigation
    }
}
