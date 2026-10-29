package com.siamts.synapse.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing Direct In-App Reset Password validation and API submission.
 */
class ForgotPasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
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

    fun onResetPasswordClick(onSuccess: () -> Unit) {
        val email = _uiState.value.email.trim()
        val newPassword = _uiState.value.newPassword
        val confirmPassword = _uiState.value.confirmPassword

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email address.") }
            return
        }

        if (newPassword.isEmpty() || newPassword.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters long.") }
            return
        }

        if (newPassword != confirmPassword) {
            _uiState.update { it.copy(errorMessage = "New Password and Confirm Password do not match.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

        viewModelScope.launch {
            val result = AuthRepository.resetPasswordDirect(email, newPassword)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Password reset successfully. Please log in with your new password."
                    )
                }
                onSuccess()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Failed to reset password."
                    )
                }
            }
        }
    }
}
