package com.siamts.synapse.data.model

import com.google.gson.annotations.SerializedName

data class AchievementsResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("user_id")
    val userId: Int = 0,
    @SerializedName("email")
    val email: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("total_badges")
    val totalBadges: Int = 0,
    @SerializedName("badges_count_text")
    val badgesCountText: String = "",
    @SerializedName("achievements")
    val achievements: List<AchievementBadgeDto> = emptyList()
)

data class AchievementBadgeDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("icon")
    val icon: String = "🏆",
    @SerializedName("is_unlocked")
    val isUnlocked: Boolean = true,
    @SerializedName("unlocked_at")
    val unlockedAt: String? = null
)
