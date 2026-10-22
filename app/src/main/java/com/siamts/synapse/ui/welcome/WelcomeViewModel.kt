package com.siamts.synapse.ui.welcome

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel for the Welcome Screen managing UI state and user interactions.
 */
class WelcomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WelcomeUiState())
    val uiState: StateFlow<WelcomeUiState> = _uiState.asStateFlow()

    fun onStartClicked() {
        _uiState.update { currentState ->
            val nextPage = (currentState.currentPage + 1) % currentState.totalPages
            currentState.copy(
                currentPage = nextPage,
                isNavigatingToNext = true
            )
        }
    }

    fun onPageSelected(pageIndex: Int) {
        if (pageIndex in 0 until _uiState.value.totalPages) {
            _uiState.update { it.copy(currentPage = pageIndex) }
        }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(isNavigatingToNext = false) }
    }
}
