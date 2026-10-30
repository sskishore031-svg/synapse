package com.siamts.synapse.data.model

import com.google.gson.annotations.SerializedName

data class QuizSubjectsResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: QuizSubjectsDataDto? = null
)

data class QuizSubjectsDataDto(
    @SerializedName("subjects")
    val subjects: List<QuizSubjectItemDto> = emptyList()
)

data class QuizSubjectItemDto(
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
    @SerializedName("available_quizzes")
    val availableQuizzes: Int = 0,
    @SerializedName("attempted_quizzes")
    val attemptedQuizzes: Int = 0,
    @SerializedName("average_score")
    val averageScore: Int = 0
)

data class SubjectQuizzesResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: SubjectQuizzesDataDto? = null
)

data class SubjectQuizzesDataDto(
    @SerializedName("subject")
    val subject: HomeSubjectDto? = null,
    @SerializedName("quizzes")
    val quizzes: List<SubjectQuizItemDto> = emptyList()
)

data class SubjectQuizItemDto(
    @SerializedName("chapter_id")
    val chapterId: Int = 0,
    @SerializedName("chapter_order")
    val chapterOrder: Int = 1,
    @SerializedName("chapter_title")
    val chapterTitle: String = "",
    @SerializedName("quiz_title")
    val quizTitle: String = "",
    @SerializedName("subject_id")
    val subjectId: Int = 0,
    @SerializedName("subject_name")
    val subjectName: String = "",
    @SerializedName("subject_slug")
    val subjectSlug: String = "",
    @SerializedName("question_count")
    val questionCount: Int = 3,
    @SerializedName("duration_minutes")
    val durationMinutes: Int = 10,
    @SerializedName("total_marks")
    val totalMarks: Int = 100,
    @SerializedName("is_attempted")
    val isAttempted: Boolean = false,
    @SerializedName("best_score")
    val bestScore: Int? = null,
    @SerializedName("attempts_count")
    val attemptsCount: Int = 0
)

data class QuizQuestionsResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: QuizQuestionsDataDto? = null
)

data class QuizQuestionsDataDto(
    @SerializedName("quiz")
    val quiz: QuizDataDto? = null,
    @SerializedName("questions")
    val questions: List<QuizQuestionDto> = emptyList()
)

data class QuizDataDto(
    @SerializedName("title")
    val title: String = "",
    @SerializedName("subject_id")
    val subjectId: Int = 0,
    @SerializedName("subject_name")
    val subjectName: String = "",
    @SerializedName("subject_slug")
    val subjectSlug: String = "",
    @SerializedName("chapter_id")
    val chapterId: Int = 0,
    @SerializedName("chapter_title")
    val chapterTitle: String = "",
    @SerializedName("chapter_order")
    val chapterOrder: Int = 1,
    @SerializedName("total_questions")
    val totalQuestions: Int = 0,
    @SerializedName("duration_minutes")
    val durationMinutes: Int = 10,
    @SerializedName("total_marks")
    val totalMarks: Int = 100
)

data class QuizQuestionDto(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("question_number")
    val questionNumber: Int = 1,
    @SerializedName("question")
    val question: String = "",
    @SerializedName("options")
    val options: Map<String, String> = emptyMap(),
    @SerializedName("correct_option")
    val correctOption: String = "A",
    @SerializedName("explanation")
    val explanation: String? = null
)

data class QuizSubmitRequest(
    @SerializedName("chapter_id")
    val chapterId: Int,
    @SerializedName("answers")
    val answers: Map<String, String> // question_id -> selected_option
)

data class QuizSubmitResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: QuizResultDataDto? = null
)

data class QuizResultDataDto(
    @SerializedName("student_name")
    val studentName: String = "",
    @SerializedName("subject_name")
    val subjectName: String = "",
    @SerializedName("quiz_title")
    val quizTitle: String = "",
    @SerializedName("chapter_id")
    val chapterId: Int = 0,
    @SerializedName("score")
    val score: Int = 0,
    @SerializedName("total_marks")
    val totalMarks: Int = 100,
    @SerializedName("percentage")
    val percentage: Int = 0,
    @SerializedName("correct_answers")
    val correctAnswers: Int = 0,
    @SerializedName("incorrect_answers")
    val incorrectAnswers: Int = 0,
    @SerializedName("total_questions")
    val totalQuestions: Int = 0,
    @SerializedName("attempt_date")
    val attemptDate: String = "",
    @SerializedName("passed")
    val passed: Boolean = false
)

