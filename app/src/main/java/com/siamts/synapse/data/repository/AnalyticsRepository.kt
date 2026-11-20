package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.AnalyticsData

/**
 * Clean MVVM Repository interface for User Progress & Analytics.
 */
interface AnalyticsRepository {
    suspend fun getAnalytics(
        userId: Int? = null,
        email: String? = null,
        timeFilter: String = "this_week",
        month: String? = null
    ): Result<AnalyticsData>

    suspend fun logSession(
        trackName: String,
        category: String,
        durationMinutes: Int,
        studyDate: String? = null
    ): Result<Unit>
}
