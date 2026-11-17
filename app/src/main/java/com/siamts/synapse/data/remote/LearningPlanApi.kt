package com.siamts.synapse.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class LearningTaskRemoteDto(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String = "Lesson",
    @SerializedName("duration") val duration: String = "15 min",
    @SerializedName("is_completed") val isCompleted: Boolean = false,
    @SerializedName("created_at") val createdAt: String? = null
)

data class LearningPlanResponse(
    @SerializedName("status") val status: String,
    @SerializedName("success") val success: Boolean = true,
    @SerializedName("message") val message: String? = null,
    @SerializedName("task_id") val taskId: Long? = null,
    @SerializedName("is_completed") val isCompleted: Boolean? = null,
    @SerializedName("tasks") val tasks: List<LearningTaskRemoteDto> = emptyList()
)

interface LearningPlanApi {

    @GET("auth/learning_plan.php")
    suspend fun getLearningPlan(
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<LearningPlanResponse>

    @FormUrlEncoded
    @POST("auth/learning_plan.php?action=add")
    suspend fun addLearningTask(
        @Field("title") title: String,
        @Field("category") category: String = "Lesson",
        @Field("duration") duration: String = "15 min",
        @Field("user_id") userId: Int? = null,
        @Field("email") email: String? = null
    ): Response<LearningPlanResponse>

    @FormUrlEncoded
    @POST("auth/learning_plan.php?action=toggle")
    suspend fun toggleLearningTask(
        @Field("task_id") taskId: Long,
        @Field("is_completed") isCompleted: Int,
        @Field("user_id") userId: Int? = null,
        @Field("email") email: String? = null
    ): Response<LearningPlanResponse>

    @FormUrlEncoded
    @POST("auth/learning_plan.php?action=delete")
    suspend fun deleteLearningTask(
        @Field("task_id") taskId: Long,
        @Field("user_id") userId: Int? = null,
        @Field("email") email: String? = null
    ): Response<LearningPlanResponse>
}

