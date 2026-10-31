package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.AiTutorHistoryResponse
import com.siamts.synapse.data.model.AiTutorRequest
import com.siamts.synapse.data.model.AiTutorResponse
import com.siamts.synapse.data.remote.AiTutorApi
import com.siamts.synapse.data.remote.RetrofitClient

class AiTutorRepositoryImpl(
    private val api: AiTutorApi = RetrofitClient.aiTutorApi
) : AiTutorRepository {

    override suspend fun askQuestion(
        question: String,
        userId: Int?,
        email: String?
    ): Result<AiTutorResponse> {
        return try {
            val response = api.askQuestion(userId = userId, email = email, request = AiTutorRequest(question))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get answer: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHistory(
        userId: Int?,
        email: String?
    ): Result<AiTutorHistoryResponse> {
        return try {
            val response = api.getHistory(userId = userId, email = email)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get history: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
