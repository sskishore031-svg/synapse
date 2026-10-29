package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.BrainMapDataDto

/**
 * Repository interface defining operations for retrieving BrainMap analytics.
 */
interface BrainMapRepository {

    /**
     * Retrieves real-time student learning analytics, subject progress, and connection nodes.
     *
     * @param userId The unique ID of the student.
     * @param email The registered email of the student.
     * @return Result containing [BrainMapDataDto] on success, or an exception on error.
     */
    suspend fun getBrainMapAnalytics(userId: Int? = null, email: String? = null): Result<BrainMapDataDto>
}

