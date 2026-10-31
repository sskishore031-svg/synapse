package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.NotificationsResponse
import com.siamts.synapse.data.model.UpdateSettingsRequest
import com.siamts.synapse.data.model.UpdateSettingsResponse

interface NotificationsRepository {
    suspend fun getNotifications(
        userId: Int? = null,
        email: String? = null
    ): Result<NotificationsResponse>

    suspend fun updateSettings(
        request: UpdateSettingsRequest,
        userId: Int? = null,
        email: String? = null
    ): Result<UpdateSettingsResponse>
}
