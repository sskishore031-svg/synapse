package com.siamts.synapse.data

import com.siamts.synapse.data.model.AuthResponse
import com.siamts.synapse.data.repository.AuthRepository as IAuthRepository
import com.siamts.synapse.data.repository.AuthRepositoryImpl

/**
 * Root repository singleton implementing [IAuthRepository] backed by [AuthRepositoryImpl].
 */
object AuthRepository : IAuthRepository {

    private val delegate: IAuthRepository = AuthRepositoryImpl()

    override suspend fun login(identifier: String, password: String): Result<AuthResponse> =
        delegate.login(identifier, password)

    override suspend fun signup(
        name: String,
        email: String,
        password: String,
        confirmPassword: String?,
        phone: String?
    ): Result<AuthResponse> = delegate.signup(name, email, password, confirmPassword, phone)

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String?,
        phone: String?
    ): Result<AuthResponse> = delegate.signup(name, email, password, confirmPassword, phone)

    override suspend fun resetPasswordDirect(email: String, newPassword: String): Result<String> =
        delegate.resetPasswordDirect(email, newPassword)

    override suspend fun resetPassword(token: String, newPassword: String): Result<String> =
        delegate.resetPassword(token, newPassword)

    override suspend fun validateSession(userId: Int): Result<com.siamts.synapse.data.model.UserDto> =
        delegate.validateSession(userId)

    /**
     * Backward-compatible helper method returning status message string.
     */
    suspend fun registerUser(name: String, email: String, password: String): Result<String> {
        val result = signup(name, email, password, password)
        return if (result.isSuccess) {
            Result.success(result.getOrNull()?.message ?: "Account created successfully!")
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("Failed to register"))
        }
    }
}
