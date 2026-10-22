package com.siamts.synapse.ui.flashcards

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Flashcard flips, progress, and difficulty markings.
 */
class FlashcardsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FlashcardsUiState())
    val uiState: StateFlow<FlashcardsUiState> = _uiState.asStateFlow()

    fun onFlipCard() {
        _uiState.update { it.copy(isFlipped = !it.isFlipped) }
    }

    fun onNextCard() {
        _uiState.update { currentState ->
            val nextIndex = if (currentState.currentCardIndex < currentState.totalCards) {
                currentState.currentCardIndex + 1
            } else {
                1
            }
            currentState.copy(
                currentCardIndex = nextIndex,
                isFlipped = false
            )
        }
    }

    fun onPreviousCard() {
        _uiState.update { currentState ->
            val prevIndex = if (currentState.currentCardIndex > 1) {
                currentState.currentCardIndex - 1
            } else {
                currentState.totalCards
            }
            currentState.copy(
                currentCardIndex = prevIndex,
                isFlipped = false
            )
        }
    }

    fun onToggleMarkDifficult() {
        _uiState.update { it.copy(isMarkedDifficult = !it.isMarkedDifficult) }
    }
}
