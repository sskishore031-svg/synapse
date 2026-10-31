package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.FlashcardActionResponse
import com.siamts.synapse.data.model.FlashcardsResponse
import com.siamts.synapse.data.remote.FlashcardsApi
import com.siamts.synapse.data.remote.RetrofitClient

class FlashcardsRepositoryImpl(
    private val api: FlashcardsApi = RetrofitClient.flashcardsApi
) : FlashcardsRepository {

    override suspend fun getFlashcards(
        userId: Int?,
        email: String?,
        deck: String?,
        subjectId: Int?
    ): Result<FlashcardsResponse> {
        return try {
            val response = api.getFlashcards(userId = userId, email = email, deck = deck, subjectId = subjectId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load flashcards: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markDifficult(
        cardId: Int,
        userId: Int?,
        email: String?
    ): Result<FlashcardActionResponse> {
        return try {
            val response = api.markDifficult(userId = userId, email = email, body = mapOf("card_id" to cardId))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to mark difficult: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
