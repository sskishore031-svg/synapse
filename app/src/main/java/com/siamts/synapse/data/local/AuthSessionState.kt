package com.siamts.synapse.data.local

import com.siamts.synapse.data.model.UserDto

/**
 * Sealed hierarchy representing the user's persistent authentication state.
 */
sealed interface AuthSessionState {
    /**
     * Checking persistent storage on app startup.
     */
    data object Loading : AuthSessionState

    /**
     * User has an active, valid session.
     */
    data class Authenticated(val user: UserDto) : AuthSessionState

    /**
     * No active session found or user logged out.
     */
    data object Unauthenticated : AuthSessionState
}

