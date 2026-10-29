package com.siamts.synapse.ui.welcome

/**
 * UI State for the Welcome / Onboarding Screen following MVVM architecture.
 */
data class WelcomeUiState(
    val badgeText: String = "YOUR MIND. SUPERCHARGED.",
    val title: String = "Synapse AI",
    val subtitle: String = "Intelligent Learning Companion",
    val secondaryTagline: String = "Learn Smarter. Grow Faster.",
    val footerText: String = "A little intelligence. Infinite potential.",
    val buttonText: String = "Let's Start",
    val currentPage: Int = 0,
    val totalPages: Int = 3,
    val isNavigatingToNext: Boolean = false
)
