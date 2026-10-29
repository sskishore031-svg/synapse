package com.siamts.synapse.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.AuthRepository
import com.siamts.synapse.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing Login Screen state, credentials input, and login action.
 */
class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull()
) : ViewModel() {

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

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onLoginClicked(onSuccess: () -> Unit) {
        val identifier = _uiState.value.emailOrPhone.trim()
        val password = _uiState.value.password

        if (identifier.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please enter your email or user ID.") }
            return
        }

        if (password.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please enter your password.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = authRepository.login(identifier, password)
            if (result.isSuccess) {
                val response = result.getOrNull()
                val user = response?.user
                val token = response?.token

                // Persist session into DataStore Preferences
                val manager = sessionManager ?: SessionManager.getInstanceOrNull()
                if (user != null) {
                    manager?.saveSession(user, token)
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        errorMessage = null
                    )
                }
                onSuccess()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Invalid email/user ID or password."
                    )
                }
            }
        }
    }
}
