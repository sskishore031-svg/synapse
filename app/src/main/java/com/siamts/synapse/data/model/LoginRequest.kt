package com.siamts.synapse.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data model for Login request payload sent to synapse_backend/auth/login.php.
 * Supports authentication via email, username/user_id, or phone.
 */
data class LoginRequest(
    @SerializedName("email")
    val email: String? = null,

    @SerializedName("user_id")
    val userId: Int? = null,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("password")
    val password: String
)
