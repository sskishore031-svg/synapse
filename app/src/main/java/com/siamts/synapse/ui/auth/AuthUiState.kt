package com.siamts.synapse.ui.auth

import com.siamts.synapse.data.model.UserDto

/**
 * UI State for Authentication (Login and Sign Up) flows following MVVM architecture.
 */
data class AuthUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val user: UserDto? = null,
    val token: String? = null
)

