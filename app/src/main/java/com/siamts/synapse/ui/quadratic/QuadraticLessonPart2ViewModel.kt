package com.siamts.synapse.ui.quadratic

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Quadratic Lesson Part 2 state.
 */
class QuadraticLessonPart2ViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QuadraticLessonPart2UiState())
    val uiState: StateFlow<QuadraticLessonPart2UiState> = _uiState.asStateFlow()

    fun onSubTabSelected(index: Int) {
        _uiState.update { it.copy(selectedSubTab = index) }
    }
}
