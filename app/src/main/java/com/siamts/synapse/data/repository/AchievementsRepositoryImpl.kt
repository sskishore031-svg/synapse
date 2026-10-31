package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.AchievementsResponse
import com.siamts.synapse.data.remote.AchievementsApi
import com.siamts.synapse.data.remote.RetrofitClient

class AchievementsRepositoryImpl(
    private val api: AchievementsApi = RetrofitClient.achievementsApi
) : AchievementsRepository {

    override suspend fun getAchievements(
        userId: Int?,
        email: String?
    ): Result<AchievementsResponse> {
        return try {
            val response = api.getAchievements(userId = userId, email = email)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load achievements: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
