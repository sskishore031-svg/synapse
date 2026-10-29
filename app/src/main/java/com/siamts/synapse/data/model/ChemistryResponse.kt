package com.siamts.synapse.data.model

import com.google.gson.annotations.SerializedName

/**
 * Top-level response for Chemistry subject and lesson API.
 */
data class ChemistryResponse(
    @SerializedName("status")
    val status: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: ChemistryDataDto? = null
) {
    val isSuccessful: Boolean
        get() = status.equals("success", ignoreCase = true)
}

data class ChemistryDataDto(
    @SerializedName("student")
    val student: BrainMapStudentDto? = null,

    @SerializedName("subject")
    val subject: ChemistrySubjectDto? = null,

    @SerializedName("activeChapter")
    val activeChapter: ChemistryActiveChapterDto? = null,

    @SerializedName("chapters")
    val chapters: List<ChemistryChapterDto>? = null,

    @SerializedName("quiz")
    val quiz: ChemistryQuizSectionDto? = null
)

data class ChemistrySubjectDto(
    @SerializedName("id")
    val id: Int = 6,

    @SerializedName("name")
    val name: String = "Chemistry",

    @SerializedName("slug")
    val slug: String = "chemistry",

    @SerializedName("iconSymbol")
    val iconSymbol: String = "🧪",

    @SerializedName("colorHex")
    val colorHex: String = "#EF4444",

    @SerializedName("progressPercent")
    val progressPercent: Int = 0,

    @SerializedName("progressSubtext")
    val progressSubtext: String = "0 of 5 lessons completed · Beginner path",

    @SerializedName("totalChapters")
    val totalChapters: Int = 5,

    @SerializedName("completedChapters")
    val completedChapters: Int = 0
)

data class ChemistryActiveChapterDto(
    @SerializedName("id")
    val id: Int = 28,

    @SerializedName("title")
    val title: String = "Chemical Bonding",

    @SerializedName("part1")
    val part1: ChemistryLessonPartDto? = null,

    @SerializedName("part2")
    val part2: ChemistryLessonPartDto? = null
)

data class ChemistryLessonPartDto(
    @SerializedName("partNumber")
    val partNumber: Int = 1,

    @SerializedName("subtitleTag")
    val subtitleTag: String = "Part 1: Foundations",

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

data class ChemistryChapterDto(
    @SerializedName("id")
    val id: String = "",

    @SerializedName("title")
    val title: String = "",

    @SerializedName("order")
    val order: Int = 1,

    @SerializedName("estimatedMinutes")
    val estimatedMinutes: Int = 15,

    @SerializedName("isCompleted")
    val isCompleted: Boolean = false,

    @SerializedName("status")
    val status: String = "LOCKED",

    @SerializedName("subtext")
    val subtext: String = ""
)

data class ChemistryQuizSectionDto(
    @SerializedName("title")
    val title: String = "Chemical Bonding Assessment",

    @SerializedName("questions")
    val questions: List<ChemistryQuizQuestionDto> = emptyList(),

    @SerializedName("latestAttempt")
    val latestAttempt: ChemistryLatestQuizAttemptDto? = null
)

data class ChemistryQuizQuestionDto(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("question")
    val question: String = "",

    @SerializedName("options")
    val options: Map<String, String> = emptyMap(),

    @SerializedName("correctOption")
    val correctOption: String = "A",

    @SerializedName("explanation")
    val explanation: String? = null
)

data class ChemistryLatestQuizAttemptDto(
    @SerializedName("score_percentage")
    val scorePercentage: Int = 0,

    @SerializedName("correct_answers")
    val correctAnswers: Int = 0,

    @SerializedName("total_questions")
    val totalQuestions: Int = 0,

    @SerializedName("created_at")
    val createdAt: String? = null
)

