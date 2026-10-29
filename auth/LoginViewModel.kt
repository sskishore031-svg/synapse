package com.siamts.synapse.ui.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Login Screen state, credentials input, and login action.
 */
class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailOrPhoneChanged(input: String) {
        _uiState.update { it.copy(emailOrPhone = input, errorMessage = null) }
    }

    fun onPasswordChanged(input: String) {
        _uiState.update { it.copy(password = input, errorMessage = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onLoginClicked(onSuccess: () -> Unit) {
        _uiState.update { it.copy(isLoading = true) }
        // Perform login validation / authentication
        _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
        onSuccess()
    }
}
