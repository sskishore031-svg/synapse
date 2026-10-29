package com.siamts.synapse.data.model

import com.google.gson.annotations.SerializedName

/**
 * Top-level response for BrainMap analytics endpoint.
 */
data class BrainMapResponse(
    @SerializedName("status")
    val status: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: BrainMapDataDto? = null
) {
    val isSuccessful: Boolean
        get() = status.equals("success", ignoreCase = true)
}

/**
 * Encapsulates full student learning analytics data.
 */
data class BrainMapDataDto(
    @SerializedName("student")
    val student: BrainMapStudentDto? = null,

    @SerializedName("summary")
    val summary: BrainMapSummaryDto? = null,

    @SerializedName("nodes")
    val nodes: List<BrainMapNodeDto>? = null,

    @SerializedName("recentActivities")
    val recentActivities: List<BrainMapActivityDto>? = null
)

data class BrainMapStudentDto(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("email")
    val email: String = ""
)

data class BrainMapSummaryDto(
    @SerializedName("totalSubjectsStudied")
    val totalSubjectsStudied: Int = 0,

    @SerializedName("totalSubjectsAvailable")
    val totalSubjectsAvailable: Int = 6,

    @SerializedName("totalChaptersCompleted")
    val totalChaptersCompleted: Int = 0,

    @SerializedName("totalChaptersAvailable")
    val totalChaptersAvailable: Int = 30,

    @SerializedName("totalQuizzesAttempted")
    val totalQuizzesAttempted: Int = 0,

    @SerializedName("averageQuizScore")
    val averageQuizScore: Int = 0,

    @SerializedName("latestQuizScore")
    val latestQuizScore: Int = 0,

    @SerializedName("overallKnowledgePercentage")
    val overallKnowledgePercentage: Int = 0,

    @SerializedName("overallKnowledgeText")
    val overallKnowledgeText: String = "Every lesson makes a new connection.",

    @SerializedName("strongestSubject")
    val strongestSubject: String? = null,

    @SerializedName("strongestScore")
    val strongestScore: Int? = null,

    @SerializedName("weakestSubject")
    val weakestSubject: String? = null,

    @SerializedName("weakestScore")
    val weakestScore: Int? = null,

    @SerializedName("recommendationTitle")
    val recommendationTitle: String? = "Strengthen your next connection",

    @SerializedName("recommendationText")
    val recommendationText: String? = null
)

data class BrainMapNodeDto(
    @SerializedName("id")
    val id: String = "",

    @SerializedName("title")
    val title: String = "",

    @SerializedName("statusText")
    val statusText: String = "",

    @SerializedName("percentage")
    val percentage: Int = 0,

    @SerializedName("colorHex")
    val colorHex: String = "#3B82F6",

    @SerializedName("iconSymbol")
    val iconSymbol: String? = null,

    @SerializedName("chaptersCompleted")
    val chaptersCompleted: Int = 0,

    @SerializedName("totalChapters")
    val totalChapters: Int = 5,

    @SerializedName("quizzesAttempted")
    val quizzesAttempted: Int = 0,

    @SerializedName("averageQuizScore")
    val averageQuizScore: Int = 0
)

data class BrainMapActivityDto(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("activity_type")
    val activityType: String = "",

    @SerializedName("title")
    val title: String = "",

    @SerializedName("subtitle")
    val subtitle: String? = null,

    @SerializedName("subject_name")
    val subjectName: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null
)

