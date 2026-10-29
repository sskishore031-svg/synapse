package com.siamts.synapse.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing Reset Password token verification and submission.
 */
class ResetPasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun setToken(token: String) {
        _uiState.update { it.copy(token = token) }
    }

    fun onNewPasswordChanged(password: String) {
        _uiState.update { it.copy(newPassword = password, errorMessage = null) }
    }

    fun onConfirmPasswordChanged(password: String) {
        _uiState.update { it.copy(confirmPassword = password, errorMessage = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onResetPasswordClick(onResetSuccess: () -> Unit) {
        val token = _uiState.value.token.trim()
        val newPassword = _uiState.value.newPassword
        val confirmPassword = _uiState.value.confirmPassword

        if (newPassword.isEmpty() || newPassword.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters long.") }
            return
        }

        if (newPassword != confirmPassword) {
            _uiState.update { it.copy(errorMessage = "New Password and Confirm Password do not match.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = AuthRepository.resetPassword(token, newPassword)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Password reset successfully. Please log in."
                    )
                }
                onResetSuccess()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Failed to reset password. Please try again."
                    )
                }
            }
        }
    }
}
