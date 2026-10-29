package com.siamts.synapse.data.remote

import com.siamts.synapse.data.model.ProfileResponse
import com.siamts.synapse.data.model.ProfileSetupRequest
import com.siamts.synapse.data.model.ProfileUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit API interface for Student Profile and Profile Setup backend services.
 */
interface ProfileApi {

    /**
     * Fetches the student's profile details from MySQL via profile.php.
     * Scoped to the authenticated user ID or email.
     */
    @GET("auth/profile.php")
    suspend fun getProfile(
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<ProfileResponse>

    /**
     * Updates student profile details (name, phone, bio, course, preferences) via profile.php.
     */
    @POST("auth/profile.php")
    suspend fun updateProfile(
        @Body request: ProfileUpdateRequest
    ): Response<ProfileResponse>

    /**
     * Submits student personalization / onboarding setup choices via profile_setup.php.
     */
    @POST("auth/profile_setup.php")
    suspend fun completeProfileSetup(
        @Body request: ProfileSetupRequest
    ): Response<ProfileResponse>
}

