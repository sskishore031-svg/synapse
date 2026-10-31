package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.AiTutorHistoryResponse
import com.siamts.synapse.data.model.AiTutorResponse

interface AiTutorRepository {
    suspend fun askQuestion(
        question: String,
        userId: Int? = null,
        email: String? = null
    ): Result<AiTutorResponse>

    suspend fun getHistory(
        userId: Int? = null,
        email: String? = null
    ): Result<AiTutorHistoryResponse>
}
