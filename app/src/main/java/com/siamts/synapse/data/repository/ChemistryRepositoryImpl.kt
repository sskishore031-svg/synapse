package com.siamts.synapse.data.repository

import com.google.gson.Gson
import com.siamts.synapse.data.model.ChemistryDataDto
import com.siamts.synapse.data.model.ChemistryResponse
import com.siamts.synapse.data.remote.ChemistryApi
import com.siamts.synapse.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * Implementation of [ChemistryRepository] communicating with synapse_backend.
 */
class ChemistryRepositoryImpl(
    private val api: ChemistryApi = RetrofitClient.chemistryApi,
    private val gson: Gson = Gson()
) : ChemistryRepository {

    override suspend fun getChemistryLesson(
        userId: Int?,
        email: String?,
        chapterId: Int?
    ): Result<ChemistryDataDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.getChemistryLesson(userId = userId, email = email, chapterId = chapterId)
            handleResponse(response, "Failed to load Chemistry lesson.")
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    override suspend fun completeChapter(
        userId: Int,
        chapterId: Int
    ): Result<ChemistryDataDto> = withContext(Dispatchers.IO) {
        try {
            val body = mapOf("user_id" to userId, "chapter_id" to chapterId)
            val response = api.completeChapter(body)
            handleResponse(response, "Failed to complete Chemistry chapter.")
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    override suspend fun submitQuiz(
        userId: Int,
        chapterId: Int,
        scorePercentage: Int,
        correctAnswers: Int,
        totalQuestions: Int,
        timeTakenSeconds: Int
    ): Result<ChemistryDataDto> = withContext(Dispatchers.IO) {
        try {
            val body: Map<String, Any> = mapOf(
                "user_id" to userId,
                "chapter_id" to chapterId,
                "score_percentage" to scorePercentage,
                "correct_answers" to correctAnswers,
                "total_questions" to totalQuestions,
                "time_taken_seconds" to timeTakenSeconds
            )
            val response = api.submitQuiz(body)
            handleResponse(response, "Failed to submit Chemistry quiz.")
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    private fun handleResponse(
        response: Response<ChemistryResponse>,
        defaultError: String
    ): Result<ChemistryDataDto> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.isSuccessful && body.data != null) {
                Result.success(body.data)
            } else {
                val msg = body?.message ?: defaultError
                Result.failure(Exception(msg))
            }
        } else {
            val errorMsg = parseErrorMessage(response) ?: "Server error (${response.code()})."
            Result.failure(Exception(errorMsg))
        }
    }

    private fun <T> parseErrorMessage(response: Response<T>): String? {
        return try {
            val errorBody = response.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                val parsed = gson.fromJson(errorBody, ChemistryResponse::class.java)
                parsed?.message?.takeIf { it.isNotBlank() }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun mapException(e: Exception): Exception {
        return when (e) {
            is SocketTimeoutException -> Exception("Connection timed out. Please check your local server.")
            is ConnectException -> Exception("Unable to connect to local server (synapse_backend). Please ensure XAMPP Apache is running.")
            is IOException -> Exception("Network error: ${e.localizedMessage ?: "Please check connection"}")
            else -> e
        }
    }
}

