package com.siamts.synapse.ui.results

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel managing Quiz Results state and navigation.
 */
class QuizResultsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QuizResultsUiState())
    val uiState: StateFlow<QuizResultsUiState> = _uiState.asStateFlow()

    fun onReviewAnswersClicked() {
        // Review quiz answers logic
    }

    fun onShareClicked() {
        // Share result logic
    }
}
