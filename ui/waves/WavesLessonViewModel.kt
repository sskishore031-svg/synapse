package com.siamts.synapse.ui.waves

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Waves Lesson Screen state.
 */
class WavesLessonViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WavesLessonUiState())
    val uiState: StateFlow<WavesLessonUiState> = _uiState.asStateFlow()

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
