package com.siamts.synapse.data.remote

import com.siamts.synapse.data.model.NotificationsResponse
import com.siamts.synapse.data.model.UpdateSettingsRequest
import com.siamts.synapse.data.model.UpdateSettingsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NotificationsApi {

    @GET("auth/notifications.php")
    suspend fun getNotifications(
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<NotificationsResponse>

    @POST("auth/update_settings.php")
    suspend fun updateSettings(
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null,
        @Body body: UpdateSettingsRequest
    ): Response<UpdateSettingsResponse>
}
