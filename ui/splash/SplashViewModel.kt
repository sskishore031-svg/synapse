package com.siamts.synapse.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing Splash Screen timer, initialization, and navigation state.
 */
class SplashViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        startSplashTimer()
    }

    private fun startSplashTimer() {
        viewModelScope.launch {
            // Simulate initialization delay (e.g. 2 seconds)
            delay(2000L)
            _uiState.update { currentState ->
                currentState.copy(
                    isReady = true,
                    isNavigatingToWelcome = true,
                )
            }
        }
    }
}
