package com.siamts.synapse.ui.splash

/**
 * UI State for the Splash Screen following MVVM architecture.
 */
data class SplashUiState(
    val badgeText: String = "YOUR MIND. SUPERCHARGED.",
    val appTitle: String = "Synapse AI",
    val appSubtitle: String = "Intelligent Learning Companion",
    val tagline: String = "Learn Smarter. Grow Faster.",
    val footerText: String = "A little intelligence. Infinite potential.",
    val buttonText: String = "Let's Start",
    val isReady: Boolean = false,
    val isNavigatingToWelcome: Boolean = false,
    val isNavigating: Boolean = false,
    val isAuthenticated: Boolean = false,
)
