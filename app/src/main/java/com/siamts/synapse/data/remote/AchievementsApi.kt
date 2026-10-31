package com.siamts.synapse.data.remote

import com.siamts.synapse.data.model.AchievementsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AchievementsApi {

    @GET("auth/achievements.php")
    suspend fun getAchievements(
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<AchievementsResponse>
}
