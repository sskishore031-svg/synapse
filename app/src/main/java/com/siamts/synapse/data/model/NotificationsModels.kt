package com.siamts.synapse.data.model

import com.google.gson.annotations.SerializedName

data class NotificationsResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("user_id")
    val userId: Int = 0,
    @SerializedName("unread_count")
    val unreadCount: Int = 0,
    @SerializedName("notifications")
    val notifications: List<NotificationItemDto> = emptyList()
)

data class NotificationItemDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("type")
    val type: String = "general",
    @SerializedName("is_read")
    val isRead: Boolean = false,
    @SerializedName("created_at")
    val createdAt: String = ""
)

data class UpdateSettingsRequest(
    @SerializedName("study_goal")
    val studyGoal: String? = null,
    @SerializedName("preferred_language")
    val preferredLanguage: String? = null,
    @SerializedName("notifications_enabled")
    val notificationsEnabled: Boolean? = null
)

data class UpdateSettingsResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("settings")
    val settings: UserSettingsDto? = null
)

data class UserSettingsDto(
    @SerializedName("study_goal")
    val studyGoal: String? = null,
    @SerializedName("preferred_language")
    val preferredLanguage: String? = null,
    @SerializedName("notifications_enabled")
    val notificationsEnabled: Boolean = true
)
