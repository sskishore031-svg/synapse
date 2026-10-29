package com.siamts.synapse.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.AuthSessionState
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.AuthRepository
import com.siamts.synapse.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing Splash Screen timer, session validation, and startup navigation state.
 */
class SplashViewModel(
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull(),
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    private val _sessionState = MutableStateFlow<AuthSessionState>(AuthSessionState.Loading)
    val sessionState: StateFlow<AuthSessionState> = _sessionState.asStateFlow()

    init {
        checkSessionAndStartTimer()
    }

    private fun checkSessionAndStartTimer() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val manager = sessionManager ?: SessionManager.getInstanceOrNull()

            val hasLocalSession = manager?.isLoggedIn() ?: false
            val sessionUser = manager?.getSessionUser()

            if (hasLocalSession && sessionUser != null && sessionUser.id > 0) {
                // Active local session exists, validate against server
                try {
                    val validationResult = authRepository.validateSession(sessionUser.id)
                    if (validationResult.isSuccess) {
                        val freshUser = validationResult.getOrNull()
                        if (freshUser != null) {
                            manager.saveSession(freshUser)
                            _sessionState.value = AuthSessionState.Authenticated(freshUser)
                        } else {
                            _sessionState.value = AuthSessionState.Authenticated(sessionUser)
                        }
                    } else {
                        val errorMsg = validationResult.exceptionOrNull()?.message.orEmpty()
                        if (errorMsg.contains("expired", ignoreCase = true) ||
                            errorMsg.contains("not found", ignoreCase = true) ||
                            errorMsg.contains("404")
                        ) {
                            // User account was deleted or invalidated on server
                            manager.clearSession()
                            _sessionState.value = AuthSessionState.Unauthenticated
                        } else {
                            // Temporary network failure or offline: keep user signed in offline
                            _sessionState.value = AuthSessionState.Authenticated(sessionUser)
                        }
                    }
                } catch (_: Exception) {
                    // Graceful fallback: maintain cached session during network error
                    _sessionState.value = AuthSessionState.Authenticated(sessionUser)
                }
            } else {
                _sessionState.value = AuthSessionState.Unauthenticated
            }

            // Ensure branding animation has minimum display time (1200ms)
            val elapsed = System.currentTimeMillis() - startTime
            val remainingDelay = (1200L - elapsed).coerceAtLeast(0L)
            if (remainingDelay > 0) {
                delay(remainingDelay)
            }

            val authenticated = _sessionState.value is AuthSessionState.Authenticated
            _uiState.update { currentState ->
                currentState.copy(
                    isReady = true,
                    isNavigating = true,
                    isNavigatingToWelcome = !authenticated,
                    isAuthenticated = authenticated
                )
            }
        }
    }
}
