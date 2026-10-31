package com.siamts.synapse.data.remote

import com.siamts.synapse.data.model.FlashcardActionResponse
import com.siamts.synapse.data.model.FlashcardsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FlashcardsApi {

    @GET("auth/flashcards.php?action=list")
    suspend fun getFlashcards(
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null,
        @Query("deck") deck: String? = null,
        @Query("subject_id") subjectId: Int? = null
    ): Response<FlashcardsResponse>

    @POST("auth/flashcards.php?action=mark_difficult")
    suspend fun markDifficult(
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null,
        @Body body: Map<String, Int>
    ): Response<FlashcardActionResponse>
}
