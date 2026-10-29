package com.siamts.synapse.ui.auth

/**
 * UI State for the Login / Authentication Screen following MVVM architecture.
 */
data class LoginUiState(
    val emailOrPhone: String = "kishore@gmail.com",
    val password: String = "••••••••••••",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false,
)
