package com.siamts.synapse.ui.auth

/**
 * UI State for the Login / Authentication Screen following MVVM architecture.
 */
data class LoginUiState(
    val emailOrPhone: String = "arjun.mehta@college.edu",
    val password: String = "••••••••••••",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false,
)
