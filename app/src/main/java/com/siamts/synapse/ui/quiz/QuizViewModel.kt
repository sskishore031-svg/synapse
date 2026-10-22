package com.siamts.synapse.ui.quiz

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Quiz question selection, timer, and progress.
 */
class QuizViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun onOptionSelected(optionId: String) {
        _uiState.update { it.copy(selectedOptionId = optionId) }
    }

    fun onNextQuestionClicked() {
        _uiState.update { currentState ->
            val nextIndex = if (currentState.currentQuestionIndex < currentState.totalQuestions) {
                currentState.currentQuestionIndex + 1
            } else {
                1
            }
            currentState.copy(
                currentQuestionIndex = nextIndex,
                selectedOptionId = null,
            )
        }
    }
}
