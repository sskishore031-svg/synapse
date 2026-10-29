package com.siamts.synapse.data.repository

import com.google.gson.Gson
import com.siamts.synapse.data.model.AuthResponse
import com.siamts.synapse.data.model.LoginRequest
import com.siamts.synapse.data.model.SignupRequest
import com.siamts.synapse.data.model.UserDto
import com.siamts.synapse.data.remote.AuthApi
import com.siamts.synapse.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * Implementation of [AuthRepository] communicating with synapse_backend via [AuthApi].
 */
class AuthRepositoryImpl(
    private val api: AuthApi = RetrofitClient.authApi,
    private val gson: Gson = Gson()
) : AuthRepository {

    override suspend fun login(identifier: String, password: String): Result<AuthResponse> =
        withContext(Dispatchers.IO) {
            try {
                val trimmed = identifier.trim()
                val userId = trimmed.toIntOrNull()
                val isPhone = trimmed.startsWith("+") || (trimmed.all { it.isDigit() || it == '-' || it == ' ' } && trimmed.length >= 7 && userId == null)

                val request = when {
                    userId != null -> LoginRequest(userId = userId, password = password)
                    isPhone -> LoginRequest(phone = trimmed, password = password)
                    else -> LoginRequest(email = trimmed, password = password)
                }

                val response = api.login(request)
                handleResponse(response, defaultError = "Invalid email/user ID or password.")
            } catch (e: Exception) {
                Result.failure(mapException(e))
            }
        }

    override suspend fun signup(
        name: String,
        email: String,
        password: String,
        confirmPassword: String?,
        phone: String?
    ): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val request = SignupRequest(
                name = name.trim(),
                email = email.trim(),
                password = password,
                confirmPassword = confirmPassword?.takeIf { it.isNotBlank() },
                phone = phone?.takeIf { it.isNotBlank() }
            )

            val response = api.signup(request)
            handleResponse(response, defaultError = "Failed to create account.")
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    override suspend fun resetPasswordDirect(email: String, newPassword: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val map = mapOf(
                    "email" to email.trim(),
                    "new_password" to newPassword,
                    "confirm_password" to newPassword
                )
                val response = api.forgotPassword(map)
                if (response.isSuccessful && response.body()?.isSuccessful == true) {
                    Result.success(response.body()?.message ?: "Password reset successfully. Please log in.")
                } else {
                    val errMsg = parseErrorMessage(response) ?: "Failed to reset password."
                    Result.failure(Exception(errMsg))
                }
            } catch (e: Exception) {
                Result.failure(mapException(e))
            }
        }

    override suspend fun resetPassword(token: String, newPassword: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val map = mapOf(
                    "token" to token.trim(),
                    "new_password" to newPassword,
                    "confirm_password" to newPassword
                )
                val response = api.resetPassword(map)
                if (response.isSuccessful && response.body()?.isSuccessful == true) {
                    Result.success(response.body()?.message ?: "Password updated successfully.")
                } else {
                    val errMsg = parseErrorMessage(response) ?: "Failed to reset password."
                    Result.failure(Exception(errMsg))
                }
            } catch (e: Exception) {
                Result.failure(mapException(e))
            }
        }

    override suspend fun validateSession(userId: Int): Result<UserDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.getProfile(userId = userId)
            val body = response.body()
            if (response.isSuccessful && body?.user != null) {
                Result.success(body.user)
            } else if (response.code() == 404) {
                Result.failure(Exception("Session expired or user account not found."))
            } else {
                val errMsg = parseErrorMessage(response) ?: "Session validation failed."
                Result.failure(Exception(errMsg))
            }
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    private fun handleResponse(
        response: Response<AuthResponse>,
        defaultError: String
    ): Result<AuthResponse> {
        val body = response.body()
        return if (response.isSuccessful && body != null && body.isSuccessful) {
            Result.success(body)
        } else {
            val errorMessage = parseErrorMessage(response) ?: body?.message ?: defaultError
            Result.failure(Exception(errorMessage))
        }
    }

    private fun <T> parseErrorMessage(response: Response<T>): String? {
        return try {
            val errorBody = response.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                val parsed = gson.fromJson(errorBody, AuthResponse::class.java)
                parsed?.message?.takeIf { it.isNotBlank() }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun mapException(e: Exception): Exception {
        return when (e) {
            is SocketTimeoutException -> Exception("Connection timed out. Please check your local server.")
            is ConnectException -> Exception("Unable to connect to local server (synapse_backend). Please ensure XAMPP Apache is running at http://10.0.2.2/.")
            is IOException -> Exception("Network error: ${e.localizedMessage ?: "Please check connection"}")
            else -> e
        }
    }
}
