package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.ProfileSetupRequest
import com.siamts.synapse.data.model.ProfileUpdateRequest
import com.siamts.synapse.data.model.UserProfileDto

/**
 * Repository interface defining operations for student profile management and onboarding setup.
 */
interface ProfileRepository {

    /**
     * Retrieves the authenticated student's profile information from MySQL via profile.php.
     *
     * @param userId Optional student user ID.
     * @param email Optional student registered email.
     * @return [Result] containing [UserProfileDto] on success, or an Exception on failure.
     */
    suspend fun getProfile(userId: Int? = null, email: String? = null): Result<UserProfileDto>

    /**
     * Updates student profile details (name, phone, bio, course, preferences) via profile.php.
     *
     * @param request The [ProfileUpdateRequest] containing updated fields.
     * @return [Result] containing updated [UserProfileDto] on success, or an Exception on failure.
     */
    suspend fun updateProfile(request: ProfileUpdateRequest): Result<UserProfileDto>

    /**
     * Submits student personalization preferences (course, preferred language, interests) via profile_setup.php.
     *
     * @param request The [ProfileSetupRequest] containing onboarding preferences.
     * @return [Result] containing updated [UserProfileDto] on success, or an Exception on failure.
     */
    suspend fun completeProfileSetup(request: ProfileSetupRequest): Result<UserProfileDto>
}

