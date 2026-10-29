package com.siamts.synapse.ui.auth

/**
 * UI State for Reset Password Screen.
 */
data class ResetPasswordUiState(
    val token: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
)
