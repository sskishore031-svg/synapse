package com.siamts.synapse.ui.waves

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Waves Lesson Part 2 state.
 */
class WavesLessonPart2ViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WavesLessonPart2UiState())
    val uiState: StateFlow<WavesLessonPart2UiState> = _uiState.asStateFlow()

    fun onSubTabSelected(index: Int) {
        _uiState.update { it.copy(selectedSubTab = index) }
    }
}
