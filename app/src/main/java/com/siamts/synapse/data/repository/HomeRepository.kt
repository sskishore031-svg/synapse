package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.HomeDashboardData

/**
 * Repository interface for retrieving Home Dashboard learning data.
 */
interface HomeRepository {
    suspend fun getHomeDashboard(userId: Int? = null, email: String? = null): Result<HomeDashboardData>
}

