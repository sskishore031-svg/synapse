package com.siamts.synapse.data.repository

import com.google.gson.Gson
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.model.ProfileResponse
import com.siamts.synapse.data.model.ProfileSetupRequest
import com.siamts.synapse.data.model.ProfileUpdateRequest
import com.siamts.synapse.data.model.UserDto
import com.siamts.synapse.data.model.UserProfileDto
import com.siamts.synapse.data.remote.ProfileApi
import com.siamts.synapse.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * Clean MVVM implementation of [ProfileRepository] connecting to profile.php and profile_setup.php.
 */
class ProfileRepositoryImpl(
    private val api: ProfileApi = RetrofitClient.profileApi,
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull(),
    private val gson: Gson = Gson()
) : ProfileRepository {

    override suspend fun getProfile(userId: Int?, email: String?): Result<UserProfileDto> =
        withContext(Dispatchers.IO) {
            try {
                // Determine ID / Email from session if not explicitly passed
                val resolvedUser = sessionManager?.getSessionUser()
                val targetId = userId ?: resolvedUser?.id?.takeIf { it > 0 }
                val targetEmail = email?.ifBlank { null } ?: resolvedUser?.email?.ifBlank { null }

                val response = api.getProfile(userId = targetId, email = targetEmail)
                handleProfileResponse(response)
            } catch (e: Exception) {
                Result.failure(mapException(e))
            }
        }

    override suspend fun updateProfile(request: ProfileUpdateRequest): Result<UserProfileDto> =
        withContext(Dispatchers.IO) {
            try {
                // Ensure request contains authenticated user ID or email
                val resolvedUser = sessionManager?.getSessionUser()
                val resolvedRequest = if (request.userId == null && request.email.isNullOrBlank()) {
                    request.copy(
                        userId = resolvedUser?.id?.takeIf { it > 0 },
                        email = resolvedUser?.email?.ifBlank { null }
                    )
                } else {
                    request
                }

                val response = api.updateProfile(resolvedRequest)
                handleProfileResponse(response)
            } catch (e: Exception) {
                Result.failure(mapException(e))
            }
        }

    override suspend fun completeProfileSetup(request: ProfileSetupRequest): Result<UserProfileDto> =
        withContext(Dispatchers.IO) {
            try {
                // Ensure request contains authenticated user ID or email
                val resolvedUser = sessionManager?.getSessionUser()
                val resolvedRequest = if (request.userId == null && request.email.isNullOrBlank()) {
                    request.copy(
                        userId = resolvedUser?.id?.takeIf { it > 0 },
                        email = resolvedUser?.email?.ifBlank { null }
                    )
                } else {
                    request
                }

                val response = api.completeProfileSetup(resolvedRequest)
                handleProfileResponse(response)
            } catch (e: Exception) {
                Result.failure(mapException(e))
            }
        }

    private suspend fun handleProfileResponse(response: Response<ProfileResponse>): Result<UserProfileDto> {
        return if (response.isSuccessful) {
            val body = response.body()
            val user = body?.user
            if (body != null && body.isSuccessful && user != null) {
                // Synchronize updated profile fields with persistent SessionManager
                syncSessionUser(user)
                Result.success(user)
            } else {
                val errorMsg = body?.message ?: "Operation failed without an error message."
                Result.failure(Exception(errorMsg))
            }
        } else {
            val errorMsg = parseErrorMessage(response) ?: "Server error (${response.code()})."
            Result.failure(Exception(errorMsg))
        }
    }

    private suspend fun syncSessionUser(profile: UserProfileDto) {
        try {
            val session = sessionManager ?: SessionManager.getInstanceOrNull()
            if (session != null && profile.id > 0) {
                val userDto = UserDto(
                    id = profile.id,
                    name = profile.name,
                    email = profile.email,
                    phone = profile.phone,
                    bio = profile.bio,
                    course = profile.course,
                    preferredLanguage = profile.preferredLanguage,
                    interests = profile.interests,
                    studyGoal = profile.studyGoal,
                    notificationsEnabled = profile.notificationsEnabled,
                    profilePhoto = profile.profilePhoto,
                    photoUrl = profile.photoUrl
                )
                session.saveSession(userDto)
            }
        } catch (_: Exception) {
            // Non-critical session sync failure ignored
        }
    }

    private fun <T> parseErrorMessage(response: Response<T>): String? {
        return try {
            val errorBody = response.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                val parsed = gson.fromJson(errorBody, ProfileResponse::class.java)
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
            is SocketTimeoutException -> Exception("Connection timed out. Please verify your local server.")
            is ConnectException -> Exception("Unable to connect to local server (synapse_backend). Please ensure XAMPP Apache is running.")
            is IOException -> Exception("Network communication error: ${e.localizedMessage ?: "Check connection"}")
            else -> e
        }
    }
}

