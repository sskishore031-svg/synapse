package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.FlashcardActionResponse
import com.siamts.synapse.data.model.FlashcardsResponse

interface FlashcardsRepository {
    suspend fun getFlashcards(
        userId: Int? = null,
        email: String? = null,
        deck: String? = null,
        subjectId: Int? = null
    ): Result<FlashcardsResponse>

    suspend fun markDifficult(
        cardId: Int,
        userId: Int? = null,
        email: String? = null
    ): Result<FlashcardActionResponse>
}
