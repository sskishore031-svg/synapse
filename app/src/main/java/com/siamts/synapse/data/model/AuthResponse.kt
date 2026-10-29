package com.siamts.synapse.data.model

import com.google.gson.annotations.SerializedName

/**
 * User data transfer object returned by synapse_backend authentication endpoints.
 */
data class UserDto(
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
    val interests: List<String>? = emptyList(),

    @SerializedName("study_goal")
    val studyGoal: String? = null,

    @SerializedName("notifications_enabled")
    val notificationsEnabled: Boolean? = true,

    @SerializedName("profile_photo")
    val profilePhoto: String? = null,

    @SerializedName("photo_url")
    val photoUrl: String? = null,

    @SerializedName("last_login")
    val lastLogin: String? = null
)

/**
 * Standard response envelope returned by Synapse AI backend endpoints.
 */
data class AuthResponse(
    @SerializedName("status")
    val status: String = "",

    @SerializedName("success")
    val success: Boolean? = null,

    @SerializedName("message")
    val message: String = "",

    @SerializedName("user")
    val user: UserDto? = null,

    @SerializedName("token")
    val token: String? = null
) {
    val isSuccessful: Boolean
        get() = status.equals("success", ignoreCase = true) || success == true
}

