package com.siamts.synapse.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data model for Signup/Registration request payload sent to synapse_backend/auth/signup.php.
 */
data class SignupRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("confirm_password")
    val confirmPassword: String? = null,

    @SerializedName("phone")
    val phone: String? = null
)

