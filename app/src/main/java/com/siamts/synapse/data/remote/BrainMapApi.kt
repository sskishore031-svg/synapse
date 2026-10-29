package com.siamts.synapse.data.remote

import com.siamts.synapse.data.model.BrainMapResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API interface for BrainMap student learning analytics.
 */
interface BrainMapApi {

    /**
     * Fetches real-time student learning analytics, subject progress, and node connection data.
     * Scoped to the authenticated student ID or email.
     */
    @GET("auth/brainmap.php")
    suspend fun getBrainMapAnalytics(
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<BrainMapResponse>
}

