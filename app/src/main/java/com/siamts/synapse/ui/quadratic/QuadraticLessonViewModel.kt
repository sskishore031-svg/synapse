package com.siamts.synapse.ui.quadratic

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Quadratic Equations Lesson Screen state and navigation tabs.
 */
class QuadraticLessonViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QuadraticLessonUiState())
    val uiState: StateFlow<QuadraticLessonUiState> = _uiState.asStateFlow()

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
