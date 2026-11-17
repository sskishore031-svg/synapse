package com.siamts.synapse.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class SubjectActionResponse(
    @SerializedName("status") val status: String,
    @SerializedName("success") val success: Boolean = true,
    @SerializedName("message") val message: String? = null,
    @SerializedName("subject_id") val subjectId: String? = null,
    @SerializedName("is_added") val isAdded: Boolean = true,
    @SerializedName("active_subjects") val activeSubjects: List<String> = emptyList()
)

interface SubjectsApi {

    @GET("auth/subjects.php")
    suspend fun getActiveSubjects(
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<SubjectActionResponse>

    @FormUrlEncoded
    @POST("auth/subjects.php?action=add_subject")
    suspend fun addSubject(
        @Field("subject_id") subjectId: String,
        @Field("user_id") userId: Int? = null,
        @Field("email") email: String? = null
    ): Response<SubjectActionResponse>

    @FormUrlEncoded
    @POST("auth/subjects.php?action=remove_subject")
    suspend fun removeSubject(
        @Field("subject_id") subjectId: String,
        @Field("user_id") userId: Int? = null,
        @Field("email") email: String? = null
    ): Response<SubjectActionResponse>
}

