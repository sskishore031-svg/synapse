package com.siamts.synapse.data.model

import com.google.gson.annotations.SerializedName

/**
 * Top-level network response from /home.php or /auth/home.php
 */
data class HomeDashboardResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: HomeDashboardData? = null
)

/**
 * Complete, student-scoped learning dashboard data
 */
data class HomeDashboardData(
    @SerializedName("student")
    val student: HomeStudentDto? = null,
    @SerializedName("dateText")
    val dateText: String = "",
    @SerializedName("greeting")
    val greeting: String = "Welcome Back,",
    @SerializedName("mindPower")
    val mindPower: Int = 0,
    @SerializedName("overallProgress")
    val overallProgress: Int = 0,
    @SerializedName("progressMessage")
    val progressMessage: String = "",
    @SerializedName("topicsDone")
    val topicsDone: Int = 0,
    @SerializedName("totalTopics")
    val totalTopics: Int = 0,
    @SerializedName("quizScore")
    val quizScore: Int = 0,
    @SerializedName("quizzesAttempted")
    val quizzesAttempted: Int = 0,
    @SerializedName("latestQuizScore")
    val latestQuizScore: Int = 0,
    @SerializedName("latestQuizTitle")
    val latestQuizTitle: String? = null,
    @SerializedName("studyStreakDays")
    val studyStreakDays: Int = 0,
    @SerializedName("todayPlanTotalTime")
    val todayPlanTotalTime: String = "25 min",
    @SerializedName("todayPlanItems")
    val todayPlanItems: List<HomePlanItemDto> = emptyList(),
    @SerializedName("continueLearning")
    val continueLearning: ContinueLearningDto? = null,
    @SerializedName("recentSessions")
    val recentSessions: List<HomeSessionDto> = emptyList(),
    @SerializedName("subjects")
    val subjects: List<HomeSubjectDto> = emptyList(),
    @SerializedName("recentActivities")
    val recentActivities: List<HomeActivityDto> = emptyList(),
    @SerializedName("brainMap")
    val brainMap: HomeBrainMapDto? = null
)

data class HomeStudentDto(
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
    @SerializedName("profile_photo")
    val profilePhoto: String? = null,
    @SerializedName("photo_url")
    val photoUrl: String? = null
)

data class HomePlanItemDto(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("category")
    val category: String = "",
    @SerializedName("duration")
    val duration: String = "",
    @SerializedName("iconEmoji")
    val iconEmoji: String = "",
    @SerializedName("actionType")
    val actionType: String? = null,
    @SerializedName("subjectSlug")
    val subjectSlug: String? = null,
    @SerializedName("chapterId")
    val chapterId: Int = 0
)

data class ContinueLearningDto(
    @SerializedName("chapter_id")
    val chapterId: Int = 0,
    @SerializedName("chapter_title")
    val chapterTitle: String = "",
    @SerializedName("chapter_order")
    val chapterOrder: Int = 1,
    @SerializedName("estimated_minutes")
    val estimatedMinutes: Int = 15,
    @SerializedName("subject_id")
    val subjectId: Int = 0,
    @SerializedName("subject_name")
    val subjectName: String = "",
    @SerializedName("subject_slug")
    val subjectSlug: String = "",
    @SerializedName("icon_symbol")
    val iconSymbol: String = "📖",
    @SerializedName("color_hex")
    val colorHex: String = "#3B82F6",
    @SerializedName("progress")
    val progress: Float = 0.5f,
    @SerializedName("subtext")
    val subtext: String = ""
)

data class HomeSessionDto(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("chapter_id")
    val chapterId: Int = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("category")
    val category: String = "",
    @SerializedName("subject_slug")
    val subjectSlug: String? = null,
    @SerializedName("timeAgo")
    val timeAgo: String = "",
    @SerializedName("progress")
    val progress: Float = 0f
)

data class HomeSubjectDto(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("slug")
    val slug: String = "",
    @SerializedName("icon_symbol")
    val iconSymbol: String = "📚",
    @SerializedName("color_hex")
    val colorHex: String = "#3B82F6",
    @SerializedName("total_chapters")
    val totalChapters: Int = 0,
    @SerializedName("completed_chapters")
    val completedChapters: Int = 0,
    @SerializedName("progress_percent")
    val progressPercent: Int = 0,
    @SerializedName("knowledge_percent")
    val knowledgePercent: Int = 0
)

data class HomeActivityDto(
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
    val createdAt: String = ""
)

data class HomeBrainMapDto(
    @SerializedName("overallKnowledgePercentage")
    val overallKnowledgePercentage: Int = 0,
    @SerializedName("strongestSubject")
    val strongestSubject: String = "",
    @SerializedName("strongestScore")
    val strongestScore: Int = 0,
    @SerializedName("weakestSubject")
    val weakestSubject: String = "",
    @SerializedName("weakestScore")
    val weakestScore: Int = 0,
    @SerializedName("recommendationTitle")
    val recommendationTitle: String = "",
    @SerializedName("recommendationSubtitle")
    val recommendationSubtitle: String = "",
    @SerializedName("nodes")
    val nodes: List<HomeBrainMapNodeDto> = emptyList()
)

data class HomeBrainMapNodeDto(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("percentage")
    val percentage: Int = 0,
    @SerializedName("colorHex")
    val colorHex: String = "#8B5CF6",
    @SerializedName("iconSymbol")
    val iconSymbol: String = "🧠",
    @SerializedName("chaptersCompleted")
    val chaptersCompleted: Int = 0,
    @SerializedName("totalChapters")
    val totalChapters: Int = 0,
    @SerializedName("averageQuizScore")
    val averageQuizScore: Int = 0
)

