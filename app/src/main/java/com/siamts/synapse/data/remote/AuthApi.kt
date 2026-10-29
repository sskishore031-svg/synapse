package com.siamts.synapse.data.remote

import com.siamts.synapse.data.model.AuthResponse
import com.siamts.synapse.data.model.LoginRequest
import com.siamts.synapse.data.model.SignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit API interface for authentication operations against synapse_backend.
 */
interface AuthApi {

    @POST("auth/login.php")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @GET("auth/profile.php")
    suspend fun getProfile(
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<AuthResponse>

    @POST("auth/signup.php")
    suspend fun signup(
        @Body request: SignupRequest
    ): Response<AuthResponse>

    @POST("auth/register.php")
    suspend fun register(
        @Body request: SignupRequest
    ): Response<AuthResponse>

    @POST("auth/forgot_password.php")
    suspend fun forgotPassword(
        @Body body: Map<String, String>
    ): Response<AuthResponse>

    @POST("auth/reset_password.php")
    suspend fun resetPassword(
        @Body body: Map<String, String>
    ): Response<AuthResponse>
}

