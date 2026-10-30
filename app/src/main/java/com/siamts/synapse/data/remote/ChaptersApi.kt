package com.siamts.synapse.data.remote

import com.siamts.synapse.data.model.ChapterCompleteResponse
import com.siamts.synapse.data.model.ChapterDetailResponse
import com.siamts.synapse.data.model.SubjectChaptersResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ChaptersApi {

    @GET("auth/chapters.php?action=list")
    suspend fun getSubjectChapters(
        @Query("subject_id") subjectId: Int? = null,
        @Query("subject_slug") subjectSlug: String? = null,
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<SubjectChaptersResponse>

    @GET("auth/chapters.php?action=lesson")
    suspend fun getChapterLesson(
        @Query("chapter_id") chapterId: Int,
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<ChapterDetailResponse>

    @FormUrlEncoded
    @POST("auth/chapters.php?action=complete")
    suspend fun completeChapter(
        @Field("chapter_id") chapterId: Int,
        @Field("user_id") userId: Int? = null,
        @Field("email") email: String? = null
    ): Response<ChapterCompleteResponse>
}

