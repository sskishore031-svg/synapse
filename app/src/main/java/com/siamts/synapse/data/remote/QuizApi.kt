package com.siamts.synapse.data.remote

import com.siamts.synapse.data.model.QuizQuestionsResponse
import com.siamts.synapse.data.model.QuizSubmitRequest
import com.siamts.synapse.data.model.QuizSubmitResponse
import com.siamts.synapse.data.model.QuizSubjectsResponse
import com.siamts.synapse.data.model.SubjectQuizzesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface QuizApi {

    @GET("auth/quiz.php?action=subjects")
    suspend fun getQuizSubjects(
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<QuizSubjectsResponse>

    @GET("auth/quiz.php?action=subject_quizzes")
    suspend fun getSubjectQuizzes(
        @Query("subject_id") subjectId: Int? = null,
        @Query("subject_slug") subjectSlug: String? = null,
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<SubjectQuizzesResponse>

    @GET("auth/quiz.php?action=questions")
    suspend fun getQuizQuestions(
        @Query("chapter_id") chapterId: Int,
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<QuizQuestionsResponse>

    @POST("auth/quiz.php?action=submit")
    suspend fun submitQuiz(
        @Query("chapter_id") chapterId: Int,
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null,
        @Body request: QuizSubmitRequest
    ): Response<QuizSubmitResponse>
}

