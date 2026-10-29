package com.siamts.synapse.data.repository

import com.google.gson.Gson
import com.siamts.synapse.data.model.BrainMapDataDto
import com.siamts.synapse.data.model.BrainMapResponse
import com.siamts.synapse.data.remote.BrainMapApi
import com.siamts.synapse.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * Implementation of [BrainMapRepository] fetching real data from synapse_backend.
 */
class BrainMapRepositoryImpl(
    private val api: BrainMapApi = RetrofitClient.brainMapApi,
    private val gson: Gson = Gson()
) : BrainMapRepository {

    override suspend fun getBrainMapAnalytics(
        userId: Int?,
        email: String?
    ): Result<BrainMapDataDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.getBrainMapAnalytics(userId = userId, email = email)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccessful && body.data != null) {
                    Result.success(body.data)
                } else {
                    val message = body?.message ?: "Failed to load BrainMap analytics."
                    Result.failure(Exception(message))
                }
            } else {
                val errorMessage = parseErrorMessage(response) ?: "Server error (${response.code()})."
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    private fun <T> parseErrorMessage(response: Response<T>): String? {
        return try {
            val errorBody = response.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                val parsed = gson.fromJson(errorBody, BrainMapResponse::class.java)
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

