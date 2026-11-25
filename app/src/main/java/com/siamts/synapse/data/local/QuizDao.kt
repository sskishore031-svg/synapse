package com.siamts.synapse.data.local

import com.siamts.synapse.data.model.QuizQuestionDto

/**
 * Entity representing an incorrectly answered question stored in the Mistake Vault
 * for targeted re-testing and mastery.
 */
data class MistakeQuestionEntity(
    val id: Long = 0,
    val subjectId: String,
    val chapterId: String = "1",
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: String,
    val explanation: String? = null,
    val questionJson: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val correctOptionIndex: Int
        get() = when (correctOption.trim().uppercase()) {
            "A" -> 0
            "B" -> 1
            "C" -> 2
            "D" -> 3
            else -> 0
        }

    fun toQuizQuestionDto(fallbackLookup: ((String) -> QuizQuestionDto?)? = null): QuizQuestionDto {
        var a = optionA
        var b = optionB
        var c = optionC
        var d = optionD
        var ans = correctOption.ifBlank { "A" }
        var exp = explanation

        if (a.isBlank() || b.isBlank() || c.isBlank() || d.isBlank()) {
            val matched = fallbackLookup?.invoke(questionText)
            if (matched != null) {
                if (a.isBlank()) a = matched.options["A"].orEmpty()
                if (b.isBlank()) b = matched.options["B"].orEmpty()
                if (c.isBlank()) c = matched.options["C"].orEmpty()
                if (d.isBlank()) d = matched.options["D"].orEmpty()
                if (ans.isBlank()) ans = matched.correctOption
                if (exp.isNullOrBlank()) exp = matched.explanation
            }
        }

        return QuizQuestionDto(
            id = if (id > 0) id.toInt() else questionText.hashCode().coerceAtLeast(1),
            questionNumber = 1,
            question = questionText,
            options = mapOf(
                "A" to a,
                "B" to b,
                "C" to c,
                "D" to d
            ),
            correctOption = ans,
            explanation = exp
        )
    }
}

/**
 * Data Access Object (DAO) for persisting and querying quiz attempts from 'quiz_records',
 * cached chapter quiz questions from 'chapter_questions', and Mistake Vault questions.
 */
interface QuizDao : MistakeDao {
    suspend fun insertQuizRecord(record: QuizRecordEntity): Long
    suspend fun getAllRecords(): List<QuizRecordEntity>
    suspend fun getRecordsByChapter(chapterId: String): List<QuizRecordEntity>
    suspend fun getRecordsBySubject(subjectId: String): List<QuizRecordEntity>

    // Question Bank caching and retrieval
    suspend fun getQuestionsForChapter(subjectId: String, chapterId: String): List<QuizQuestionDto>
    suspend fun insertQuestionsForChapter(subjectId: String, chapterId: String, questions: List<QuizQuestionDto>)
}
