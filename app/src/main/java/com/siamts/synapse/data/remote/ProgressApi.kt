package com.siamts.synapse.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class SyncProgressRequest(
    @SerializedName("user_id") val userId: Int = 1,
    @SerializedName("track_id") val trackId: String,
    @SerializedName("lesson_id") val lessonId: String,
    @SerializedName("lesson_completed") val lessonCompleted: Boolean = true,
    @SerializedName("quiz_completed") val quizCompleted: Boolean = false,
    @SerializedName("is_completed") val isCompleted: Boolean = true
)

data class SyncProgressResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null
)

interface ProgressApi {
    @POST("sync_progress.php")
    suspend fun syncProgress(@Body request: SyncProgressRequest): Response<SyncProgressResponse>

    @POST("auth/sync_progress.php")
    suspend fun syncProgressAuth(@Body request: SyncProgressRequest): Response<SyncProgressResponse>

    @GET("sync_progress.php")
    suspend fun getProgress(@Query("user_id") userId: Int = 1): Response<Map<String, Any>>

    @GET("auth/sync_progress.php")
    suspend fun getProgressAuth(@Query("user_id") userId: Int = 1): Response<Map<String, Any>>
}
