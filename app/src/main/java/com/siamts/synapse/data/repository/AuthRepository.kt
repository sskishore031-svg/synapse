package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.AuthResponse

/**
 * Repository interface defining authentication operations for Synapse app.
 */
interface AuthRepository {

    /**
     * Authenticates user with email / username / user_id / phone and password.
     */
    suspend fun login(identifier: String, password: String): Result<AuthResponse>

    /**
     * Registers a new user account on synapse_backend.
     */
    suspend fun signup(
        name: String,
        email: String,
        password: String,
        confirmPassword: String? = null,
        phone: String? = null
    ): Result<AuthResponse>

    /**
     * Backward-compatible alias to [signup].
     */
    suspend fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String? = null,
        phone: String? = null
    ): Result<AuthResponse> = signup(name, email, password, confirmPassword, phone)

    /**
     * Directly resets password using registered email.
     */
    suspend fun resetPasswordDirect(email: String, newPassword: String): Result<String>

    /**
     * Resets password using verification token.
     */
    suspend fun resetPassword(token: String, newPassword: String): Result<String>

    /**
     * Validates active session against server by user ID.
     */
    suspend fun validateSession(userId: Int): Result<com.siamts.synapse.data.model.UserDto>
}
