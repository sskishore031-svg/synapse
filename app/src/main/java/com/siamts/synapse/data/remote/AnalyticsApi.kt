package com.siamts.synapse.data.remote

import com.siamts.synapse.data.model.AnalyticsResponse
import com.siamts.synapse.data.model.LogSessionRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit API interface for Synapse AI Progress & Analytics services.
 */
interface AnalyticsApi {

    @GET("auth/analytics.php")
    suspend fun getAnalytics(
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null,
        @Query("time_filter") timeFilter: String = "this_week",
        @Query("month") month: String? = null
    ): Response<AnalyticsResponse>

    @GET("analytics.php")
    suspend fun getAnalyticsRoot(
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null,
        @Query("time_filter") timeFilter: String = "this_week",
        @Query("month") month: String? = null
    ): Response<AnalyticsResponse>

    @POST("auth/analytics.php")
    suspend fun logSession(
        @Query("action") action: String = "log_session",
        @Body request: LogSessionRequest
    ): Response<AnalyticsResponse>

    @POST("analytics.php")
    suspend fun logSessionRoot(
        @Query("action") action: String = "log_session",
        @Body request: LogSessionRequest
    ): Response<AnalyticsResponse>
}
