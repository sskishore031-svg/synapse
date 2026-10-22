package com.siamts.synapse.ui.splash

/**
 * UI State for the Splash Screen following MVVM architecture.
 */
data class SplashUiState(
    val isReady: Boolean = false,
    val appTitle: String = "SYNAPSE AI",
    val tagline: String = "Supercharge Your Mind",
    val isNavigatingToWelcome: Boolean = false,
)
