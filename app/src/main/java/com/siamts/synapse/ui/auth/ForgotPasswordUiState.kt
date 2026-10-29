package com.siamts.synapse.ui.auth

/**
 * UI State for Direct In-App Reset Password Screen.
 */
data class ForgotPasswordUiState(
    val email: String = "kishore@gmail.com",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
)
