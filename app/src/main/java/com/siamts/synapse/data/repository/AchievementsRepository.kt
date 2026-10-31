package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.AchievementsResponse

interface AchievementsRepository {
    suspend fun getAchievements(
        userId: Int? = null,
        email: String? = null
    ): Result<AchievementsResponse>
}
