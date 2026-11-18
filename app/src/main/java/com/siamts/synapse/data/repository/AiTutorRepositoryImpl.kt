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
        email: String?,
        preferredName: String?,
        includeFormulas: Boolean?,
        includeExamples: Boolean?,
        useEmojis: Boolean?,
        consecutiveUnclear: Int?
    ): Result<AiTutorResponse> {
        return try {
            val response = api.askQuestion(
                userId = userId,
                email = email,
                request = AiTutorRequest(
                    question = question,
                    preferredName = preferredName,
                    includeFormulas = includeFormulas,
                    includeExamples = includeExamples,
                    useEmojis = useEmojis,
                    consecutiveUnclear = consecutiveUnclear
                )
            )
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
