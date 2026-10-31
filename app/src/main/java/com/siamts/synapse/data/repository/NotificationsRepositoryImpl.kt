package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.NotificationsResponse
import com.siamts.synapse.data.model.UpdateSettingsRequest
import com.siamts.synapse.data.model.UpdateSettingsResponse
import com.siamts.synapse.data.remote.NotificationsApi
import com.siamts.synapse.data.remote.RetrofitClient

class NotificationsRepositoryImpl(
    private val api: NotificationsApi = RetrofitClient.notificationsApi
) : NotificationsRepository {

    override suspend fun getNotifications(
        userId: Int?,
        email: String?
    ): Result<NotificationsResponse> {
        return try {
            val response = api.getNotifications(userId = userId, email = email)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load notifications: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSettings(
        request: UpdateSettingsRequest,
        userId: Int?,
        email: String?
    ): Result<UpdateSettingsResponse> {
        return try {
            val response = api.updateSettings(userId = userId, email = email, body = request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update settings: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
