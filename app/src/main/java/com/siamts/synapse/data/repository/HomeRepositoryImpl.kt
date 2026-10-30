package com.siamts.synapse.data.repository

import com.siamts.synapse.data.local.AuthSessionState
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.model.HomeDashboardData
import com.siamts.synapse.data.remote.HomeApi
import com.siamts.synapse.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * Clean MVVM Repository implementation for Home Dashboard data.
 */
class HomeRepositoryImpl(
    private val homeApi: HomeApi = RetrofitClient.homeApi,
    private val sessionManager: SessionManager? = runCatching { SessionManager.getInstance() }.getOrNull()
) : HomeRepository {

    override suspend fun getHomeDashboard(userId: Int?, email: String?): Result<HomeDashboardData> =
        withContext(Dispatchers.IO) {
            try {
                var targetUserId = userId
                var targetEmail = email

                // Retrieve active session from SessionManager if not explicitly provided
                if ((targetUserId == null || targetUserId <= 0) && targetEmail.isNullOrBlank()) {
                    sessionManager?.let { sm ->
                        val session = sm.sessionStateFlow.first()
                        if (session is AuthSessionState.Authenticated) {
                            targetUserId = session.user.id
                            targetEmail = session.user.email
                        }
                    }
                }

                // First attempt: /auth/home.php
                val response = try {
                    homeApi.getHomeDashboard(userId = targetUserId, email = targetEmail)
                } catch (e: Exception) {
                    // Fallback attempt: /home.php
                    homeApi.getHomeDashboardRoot(userId = targetUserId, email = targetEmail)
                }

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.status.equals("success", ignoreCase = true) && body.data != null) {
                        return@withContext Result.success(body.data)
                    } else {
                        val errorMsg = body?.message ?: "Failed to retrieve home dashboard."
                        return@withContext Result.failure(Exception(errorMsg))
                    }
                } else {
                    return@withContext Result.failure(
                        Exception("Server returned HTTP error code: ${response.code()}")
                    )
                }
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }
}

