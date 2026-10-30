package com.siamts.synapse.data.model

import com.google.gson.annotations.SerializedName

data class LessonDetailResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: LessonDetailDataDto? = null
)

data class LessonDetailDataDto(
    @SerializedName("subject_id")
    val subjectId: Int = 4,
    @SerializedName("subject_name")
    val subjectName: String = "Artificial Intelligence",
    @SerializedName("subject_slug")
    val subjectSlug: String = "artificial-intelligence",
    @SerializedName("subject_tag")
    val subjectTag: String = "ARTIFICIAL INTELLIGENCE",
    @SerializedName("chapter_id")
    val chapterId: Int = 18,
    @SerializedName("chapter_order")
    val chapterOrder: Int = 3,
    @SerializedName("total_chapters")
    val totalChapters: Int = 5,
    @SerializedName("lesson_title")
    val lessonTitle: String = "Gradient descent",
    @SerializedName("lesson_info")
    val lessonInfo: String = "Lesson 3 of 5 · 12 min",
    @SerializedName("completion_percentage")
    val completionPercentage: Int = 60,
    @SerializedName("is_completed")
    val isCompleted: Boolean = false,
    @SerializedName("is_bookmarked")
    val isBookmarked: Boolean = false,
    @SerializedName("previous_chapter_id")
    val previousChapterId: Int? = null,
    @SerializedName("next_chapter_id")
    val nextChapterId: Int? = null,
    @SerializedName("part1")
    val part1: LessonPart1Dto? = null,
    @SerializedName("part2")
    val part2: LessonPart2Dto? = null
)

data class LessonPart1Dto(
    @SerializedName("headline")
    val headline: String = "Small steps. Less error.",
    @SerializedName("description")
    val description: String = "Gradient descent updates a model's weights in the direction that reduces its prediction error.",
    @SerializedName("key_points")
    val keyPoints: List<String> = emptyList(),
    @SerializedName("example_title")
    val exampleTitle: String = "A quick example",
    @SerializedName("example_content")
    val exampleContent: String = "If w = 4, gradient = 2 and rate = 0.1,\nthen w_new = 4 - 0.1 × 2 = 3.8."
)

data class LessonPart2Dto(
    @SerializedName("part_tag")
    val partTag: String = "PART 2 · THE LEARNING RATE",
    @SerializedName("headline")
    val headline: String = "Choose the right step size.",
    @SerializedName("description")
    val description: String = "A small rate learns slowly. A large rate can overshoot the minimum and increase loss.",
    @SerializedName("formula_header")
    val formulaHeader: String = "Another step · J(w) = ½(w - 2)² · rate = 0.1",
    @SerializedName("formula_calculation")
    val formulaCalculation: String = "w_new = 3.8 - 0.1 × 1.8 = 3.62",
    @SerializedName("steps")
    val steps: List<StepValueDto> = emptyList(),
    @SerializedName("formula_footer")
    val formulaFooter: String = "Gradient = w - 2; each update lowers loss.",
    @SerializedName("takeaway_title")
    val takeawayTitle: String = "Key takeaway",
    @SerializedName("takeaway_content")
    val takeawayContent: String = "Recompute the gradient each step. If loss oscillates, try a smaller rate."
)

data class StepValueDto(
    @SerializedName("weight")
    val weight: String = "4.00",
    @SerializedName("loss")
    val loss: String = "Loss 2.00",
    @SerializedName("isCurrent")
    val isCurrent: Boolean = false
)

data class LessonBookmarkResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: LessonBookmarkDataDto? = null
)

data class LessonBookmarkDataDto(
    @SerializedName("chapter_id")
    val chapterId: Int = 0,
    @SerializedName("part_number")
    val partNumber: Int = 1,
    @SerializedName("is_bookmarked")
    val isBookmarked: Boolean = false
)

