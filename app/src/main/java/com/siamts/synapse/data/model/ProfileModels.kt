package com.siamts.synapse.data.model

import com.google.gson.annotations.SerializedName

/**
 * Standard API response envelope returned by profile.php and profile_setup.php.
 */
data class ProfileResponse(
    @SerializedName("status")
    val status: String = "",

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("user")
    val user: UserProfileDto? = null
) {
    val isSuccessful: Boolean
        get() = status.equals("success", ignoreCase = true)
}

/**
 * Data transfer object representing student profile details from MySQL.
 */
data class UserProfileDto(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("email")
    val email: String = "",

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("bio")
    val bio: String? = null,

    @SerializedName("course")
    val course: String? = null,

    @SerializedName("preferred_language")
    val preferredLanguage: String? = null,

    @SerializedName("interests")
    val interests: List<String> = emptyList(),

    @SerializedName("study_goal")
    val studyGoal: String? = null,

    @SerializedName("notifications_enabled")
    val notificationsEnabled: Boolean = true,

    @SerializedName("profile_photo")
    val profilePhoto: String? = null,

    @SerializedName("photo_url")
    val photoUrl: String? = null,

    @SerializedName("achievements_count")
    val achievementsCount: String? = null,

    @SerializedName("user_streak")
    val userStreak: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null
)

/**
 * Request payload sent to profile.php for updating student information.
 */
data class ProfileUpdateRequest(
    @SerializedName("user_id")
    val userId: Int? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("bio")
    val bio: String? = null,

    @SerializedName("course")
    val course: String? = null,

    @SerializedName("preferred_language")
    val preferredLanguage: String? = null,

    @SerializedName("study_goal")
    val studyGoal: String? = null,

    @SerializedName("notifications_enabled")
    val notificationsEnabled: Boolean? = null,

    @SerializedName("interests")
    val interests: List<String>? = null
)

/**
 * Request payload sent to profile_setup.php for onboarding / profile personalization.
 */
data class ProfileSetupRequest(
    @SerializedName("user_id")
    val userId: Int? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("course")
    val course: String? = null,

    @SerializedName("preferred_language")
    val preferredLanguage: String? = null,

    @SerializedName("interests")
    val interests: List<String>? = null
)

