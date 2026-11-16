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

            if (hasLocalSession) {
                val effectiveUser = sessionUser ?: com.siamts.synapse.data.model.UserDto(
                    id = 1,
                    name = "Student",
                    email = "student@synapse.ai"
                )
                _sessionState.value = AuthSessionState.Authenticated(effectiveUser)
                _uiState.update { it.copy(isAuthenticated = true) }

                // Non-blocking background session refresh if userId > 0
                if (sessionUser != null && sessionUser.id > 0) {
                    launch {
                        try {
                            val validationResult = authRepository.validateSession(sessionUser.id)
                            if (validationResult.isSuccess) {
                                val freshUser = validationResult.getOrNull()
                                if (freshUser != null) {
                                    manager.saveSession(freshUser)
                                    _sessionState.value = AuthSessionState.Authenticated(freshUser)
                                }
                            }
                        } catch (_: Exception) {
                            // Maintain cached session on network timeout/failure
                        }
                    }
                }
            } else {
                _sessionState.value = AuthSessionState.Unauthenticated
                _uiState.update { it.copy(isAuthenticated = false) }
            }

            // Ensure branding animation display time of 1500ms
            val elapsed = System.currentTimeMillis() - startTime
            val remainingDelay = (1500L - elapsed).coerceAtLeast(0L)
            if (remainingDelay > 0) {
                delay(remainingDelay)
            }

            val authenticated = _uiState.value.isAuthenticated || (manager?.isLoggedIn() == true)
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
