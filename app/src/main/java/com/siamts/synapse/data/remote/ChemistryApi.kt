package com.siamts.synapse.data.remote

import com.siamts.synapse.data.model.ChemistryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit API interface for Chemistry learning modules, lesson parts, and quizzes.
 */
interface ChemistryApi {

    /**
     * Retrieves Chemistry subject data, chapter progress, and lesson contents.
     */
    @GET("auth/chemistry.php")
    suspend fun getChemistryLesson(
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null,
        @Query("chapter_id") chapterId: Int? = null
    ): Response<ChemistryResponse>

    /**
     * Marks a Chemistry chapter completed for the authenticated student.
     */
    @POST("auth/chemistry.php?action=complete_chapter")
    suspend fun completeChapter(
        @Body body: Map<String, Int>
    ): Response<ChemistryResponse>

    /**
     * Submits student quiz answers and saves score to MySQL.
     */
    @POST("auth/chemistry.php?action=submit_quiz")
    suspend fun submitQuiz(
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<ChemistryResponse>
}

