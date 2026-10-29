package com.siamts.synapse.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.AuthSessionState
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.AuthRepository
import com.siamts.synapse.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing authentication state and actions (Login & Signup)
 * following clean MVVM architecture with unidirectional data flow (UDF).
 */
class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val sessionState: StateFlow<AuthSessionState> = (sessionManager ?: SessionManager.getInstanceOrNull())
        ?.sessionStateFlow
        ?.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AuthSessionState.Loading
        ) ?: MutableStateFlow(AuthSessionState.Unauthenticated).asStateFlow()

    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name, errorMessage = null) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onConfirmPasswordChanged(password: String) {
        _uiState.update { it.copy(confirmPassword = password, errorMessage = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onToggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Executes login request against synapse_backend (auth/login.php).
     */
    fun onLogin(
        email: String = _uiState.value.email,
        password: String = _uiState.value.password,
        onSuccess: () -> Unit = {}
    ) {
        val trimmedIdentifier = email.trim()

        if (trimmedIdentifier.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please enter your email or user ID.") }
            return
        }

        if (password.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please enter your password.") }
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )
        }

        viewModelScope.launch {
            val result = authRepository.login(trimmedIdentifier, password)
            if (result.isSuccess) {
                val response = result.getOrNull()
                val user = response?.user
                val token = response?.token

                // Persist session to DataStore Preferences
                val manager = sessionManager ?: SessionManager.getInstanceOrNull()
                if (user != null) {
                    manager?.saveSession(user, token)
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null,
                        successMessage = response?.message ?: "Login successful!",
                        user = user,
                        token = token
                    )
                }
                onSuccess()
            } else {
                val error = result.exceptionOrNull()?.message ?: "Login failed. Please check credentials."
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = error
                    )
                }
            }
        }
    }

    /**
     * Executes signup request against synapse_backend (auth/signup.php).
     */
    fun onSignup(
        name: String = _uiState.value.name,
        email: String = _uiState.value.email,
        password: String = _uiState.value.password,
        confirmPassword: String = _uiState.value.confirmPassword,
        onSuccess: () -> Unit = {}
    ) {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()

        if (trimmedName.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please enter your full name.") }
            return
        }

        if (trimmedEmail.isEmpty() || !emailRegex.matches(trimmedEmail)) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email address.") }
            return
        }

        if (password.isEmpty() || password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters long.") }
            return
        }

        if (password != confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Password and Confirm Password do not match.") }
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )
        }

        viewModelScope.launch {
            val result = authRepository.signup(
                name = trimmedName,
                email = trimmedEmail,
                password = password,
                confirmPassword = confirmPassword
            )

            if (result.isSuccess) {
                val response = result.getOrNull()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null,
                        successMessage = response?.message ?: "Account created successfully!",
                        user = response?.user,
                        token = response?.token
                    )
                }
                onSuccess()
            } else {
                val error = result.exceptionOrNull()?.message ?: "Failed to create account."
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = error
                    )
                }
            }
        }
    }

    fun onRegister(
        name: String = _uiState.value.name,
        email: String = _uiState.value.email,
        password: String = _uiState.value.password,
        confirmPassword: String = _uiState.value.confirmPassword,
        onSuccess: () -> Unit = {}
    ) = onSignup(name, email, password, confirmPassword, onSuccess)

    /**
     * Clears user session and resets UI state upon logout.
     */
    fun logout(onLoggedOut: () -> Unit = {}) {
        viewModelScope.launch {
            val manager = sessionManager ?: SessionManager.getInstanceOrNull()
            manager?.clearSession()
            _uiState.value = AuthUiState()
            onLoggedOut()
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }
}
