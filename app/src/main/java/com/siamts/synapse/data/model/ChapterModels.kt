package com.siamts.synapse.data.model

import com.google.gson.annotations.SerializedName

data class SubjectChaptersResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: SubjectChaptersData? = null
)

data class SubjectChaptersData(
    @SerializedName("subject")
    val subject: HomeSubjectDto? = null,
    @SerializedName("chapters")
    val chapters: List<SubjectChapterDto> = emptyList()
)

data class SubjectChapterDto(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("chapter_id")
    val chapterId: Int = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("order")
    val order: Int = 1,
    @SerializedName("chapter_order")
    val chapterOrder: Int = 1,
    @SerializedName("estimatedMinutes")
    val estimatedMinutes: Int = 15,
    @SerializedName("isCompleted")
    val isCompleted: Boolean = false,
    @SerializedName("isUnlocked")
    val isUnlocked: Boolean = false,
    @SerializedName("status")
    val status: String = "LOCKED", // "COMPLETED", "ACTIVE", "LOCKED"
    @SerializedName("subtext")
    val subtext: String = ""
)

data class ChapterDetailResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: ChapterLessonData? = null
)

data class ChapterLessonData(
    @SerializedName("subject")
    val subject: HomeSubjectDto? = null,
    @SerializedName("chapter")
    val chapter: ChapterDetailDto? = null
)

data class ChapterDetailDto(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("chapter_order")
    val chapterOrder: Int = 1,
    @SerializedName("estimated_minutes")
    val estimatedMinutes: Int = 15,
    @SerializedName("is_completed")
    val isCompleted: Boolean = false,
    @SerializedName("previous_chapter_id")
    val previousChapterId: Int? = null,
    @SerializedName("previous_chapter_title")
    val previousChapterTitle: String? = null,
    @SerializedName("next_chapter_id")
    val nextChapterId: Int? = null,
    @SerializedName("next_chapter_title")
    val nextChapterTitle: String? = null,
    @SerializedName("part1")
    val part1: LessonPartDto? = null,
    @SerializedName("part2")
    val part2: LessonPartDto? = null
)

data class LessonPartDto(
    @SerializedName("partNumber")
    val partNumber: Int = 1,
    @SerializedName("subtitleTag")
    val subtitleTag: String = "",
    @SerializedName("headline")
    val headline: String = "",
    @SerializedName("conceptText")
    val conceptText: String = "",
    @SerializedName("keyPoints")
    val keyPoints: List<String> = emptyList(),
    @SerializedName("exampleTitle")
    val exampleTitle: String = "Quick Example",
    @SerializedName("exampleProblem")
    val exampleProblem: String? = null,
    @SerializedName("exampleSolution")
    val exampleSolution: String? = null,
    @SerializedName("quickExampleText")
    val quickExampleText: String? = null,
    @SerializedName("aiRecommendation")
    val aiRecommendation: String? = null
)

data class ChapterCompleteResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: ChapterCompleteDataDto? = null
)

data class ChapterCompleteDataDto(
    @SerializedName("chapter_id")
    val chapterId: Int = 0,
    @SerializedName("is_completed")
    val isCompleted: Boolean = true,
    @SerializedName("subject_id")
    val subjectId: Int = 0,
    @SerializedName("subject_name")
    val subjectName: String = "",
    @SerializedName("completed_chapters")
    val completedChapters: Int = 0,
    @SerializedName("total_chapters")
    val totalChapters: Int = 0,
    @SerializedName("progress_percent")
    val progressPercent: Int = 0
)

