package com.siamts.synapse.data.remote

import com.siamts.synapse.data.model.HomeDashboardResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API interface for Synapse Home Dashboard services.
 */
interface HomeApi {

    @GET("auth/home.php")
    suspend fun getHomeDashboard(
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<HomeDashboardResponse>

    @GET("home.php")
    suspend fun getHomeDashboardRoot(
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<HomeDashboardResponse>
}

