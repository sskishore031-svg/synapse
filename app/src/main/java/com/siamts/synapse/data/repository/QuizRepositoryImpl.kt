package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.QuizQuestionsResponse
import com.siamts.synapse.data.model.QuizSubmitRequest
import com.siamts.synapse.data.model.QuizSubmitResponse
import com.siamts.synapse.data.model.QuizSubjectsResponse
import com.siamts.synapse.data.model.SubjectQuizzesResponse
import com.siamts.synapse.data.remote.QuizApi
import com.siamts.synapse.data.remote.RetrofitClient

class QuizRepositoryImpl(
    private val api: QuizApi = RetrofitClient.quizApi
) : QuizRepository {

    override suspend fun getQuizSubjects(
        userId: Int?,
        email: String?
    ): Result<QuizSubjectsResponse> {
        return try {
            val response = api.getQuizSubjects(userId, email)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch quiz subjects: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSubjectQuizzes(
        subjectId: Int?,
        subjectSlug: String?,
        userId: Int?,
        email: String?
    ): Result<SubjectQuizzesResponse> {
        return try {
            val response = api.getSubjectQuizzes(subjectId, subjectSlug, userId, email)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch subject quizzes: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getQuizQuestions(
        chapterId: Int,
        userId: Int?,
        email: String?
    ): Result<QuizQuestionsResponse> {
        return try {
            val response = api.getQuizQuestions(chapterId, userId, email)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch quiz questions: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitQuiz(
        chapterId: Int,
        answers: Map<String, String>,
        userId: Int?,
        email: String?
    ): Result<QuizSubmitResponse> {
        return try {
            val request = QuizSubmitRequest(chapterId = chapterId, answers = answers)
            val response = api.submitQuiz(chapterId = chapterId, userId = userId, email = email, request = request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Quiz submission failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

