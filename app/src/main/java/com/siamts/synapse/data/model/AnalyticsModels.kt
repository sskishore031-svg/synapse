package com.siamts.synapse.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data models for SYNAPSE AI real-time user progress and analytics.
 */
data class AnalyticsResponse(
    @SerializedName("status") val status: String = "success",
    @SerializedName("data") val data: AnalyticsData? = null,
    @SerializedName("message") val message: String? = null
)

data class AnalyticsData(
    @SerializedName("userId") val userId: Int = 1,
    @SerializedName("timeFilter") val timeFilter: String = "this_week",
    @SerializedName("selectedMonth") val selectedMonth: String = "",
    @SerializedName("totalMinutes") val totalMinutes: Int = 0,
    @SerializedName("formattedStudyTime") val formattedStudyTime: String = "0h 0m",
    @SerializedName("averageDailyTime") val averageDailyTime: String = "0m",
    @SerializedName("mostProductiveDay") val mostProductiveDay: String = "None",
    @SerializedName("mostProductiveDayText") val mostProductiveDayText: String = "0m spent",
    @SerializedName("currentStreak") val currentStreak: Int = 0,
    @SerializedName("bestStreak") val bestStreak: Int = 0,
    @SerializedName("weeklyConsistency") val weeklyConsistency: String = "0%",
    @SerializedName("weeklyConsistencyPercentage") val weeklyConsistencyPercentage: Int = 0,
    @SerializedName("completedDailyGoals") val completedDailyGoals: String = "0 / 7",
    @SerializedName("completedDailyGoalsCount") val completedDailyGoalsCount: Int = 0,
    @SerializedName("activeStudyDates") val activeStudyDates: List<String> = emptyList(),
    @SerializedName("monthActiveDays") val monthActiveDays: List<Int> = emptyList(),
    @SerializedName("chartDays") val chartDays: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
    @SerializedName("chartHeights") val chartHeights: List<Float> = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f),
    @SerializedName("chartMinutes") val chartMinutes: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0),
    @SerializedName("timeDistribution") val timeDistribution: List<TimeDistributionItem> = emptyList(),
    @SerializedName("subjectBreakdown") val subjectBreakdown: List<SubjectBreakdownItem> = emptyList(),
    @SerializedName("weeklySubjectComparison") val weeklySubjectComparison: List<WeeklySubjectComparisonItem> = emptyList(),
    @SerializedName("insights") val insights: String = "Start your first micro-lesson, quiz, or smart note to unlock personalized study insights!"
)

data class TimeDistributionItem(
    @SerializedName("label") val label: String,
    @SerializedName("minutes") val minutes: Int = 0,
    @SerializedName("formattedTime") val formattedTime: String = "0m",
    @SerializedName("percentage") val percentage: Int = 0,
    @SerializedName("fraction") val fraction: Float = 0f,
    @SerializedName("colorHex") val colorHex: String = "#FF6B00"
)

data class SubjectBreakdownItem(
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("icon") val icon: String = "📚",
    @SerializedName("colorHex") val colorHex: String = "#FF6B00",
    @SerializedName("minutes") val minutes: Int = 0,
    @SerializedName("formattedTime") val formattedTime: String = "0m",
    @SerializedName("percentage") val percentage: Int = 0,
    @SerializedName("fraction") val fraction: Float = 0f
)

data class WeeklySubjectComparisonItem(
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("colorHex") val colorHex: String = "#FF6B00",
    @SerializedName("heights") val heights: List<Float> = emptyList()
)

data class LogSessionRequest(
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("track_name") val trackName: String,
    @SerializedName("category") val category: String,
    @SerializedName("duration_minutes") val durationMinutes: Int,
    @SerializedName("study_date") val studyDate: String? = null
)
