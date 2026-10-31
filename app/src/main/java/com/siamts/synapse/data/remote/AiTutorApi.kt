package com.siamts.synapse.data.remote

import com.siamts.synapse.data.model.AiTutorHistoryResponse
import com.siamts.synapse.data.model.AiTutorRequest
import com.siamts.synapse.data.model.AiTutorResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AiTutorApi {

    @POST("auth/ai_tutor.php?action=chat")
    suspend fun askQuestion(
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null,
        @Body request: AiTutorRequest
    ): Response<AiTutorResponse>

    @GET("auth/ai_tutor.php?action=history")
    suspend fun getHistory(
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<AiTutorHistoryResponse>
}
