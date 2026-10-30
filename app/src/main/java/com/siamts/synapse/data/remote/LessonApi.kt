package com.siamts.synapse.data.remote

import com.siamts.synapse.data.model.ChapterCompleteResponse
import com.siamts.synapse.data.model.LessonBookmarkResponse
import com.siamts.synapse.data.model.LessonDetailResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LessonApi {

    @GET("auth/lesson.php?action=get")
    suspend fun getLessonDetails(
        @Query("chapter_id") chapterId: Int? = null,
        @Query("subject_id") subjectId: Int? = null,
        @Query("subject_slug") subjectSlug: String? = null,
        @Query("part") part: Int? = null,
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<LessonDetailResponse>

    @FormUrlEncoded
    @POST("auth/lesson.php?action=complete")
    suspend fun completeLesson(
        @Field("chapter_id") chapterId: Int,
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<ChapterCompleteResponse>

    @FormUrlEncoded
    @POST("auth/lesson.php?action=toggle_bookmark")
    suspend fun toggleBookmark(
        @Field("chapter_id") chapterId: Int,
        @Field("part") part: Int = 1,
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<LessonBookmarkResponse>
}

