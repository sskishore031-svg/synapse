package com.siamts.synapse.ui.auth

/**
 * UI State for the Create Account / Sign Up Screen.
 */
data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
)
