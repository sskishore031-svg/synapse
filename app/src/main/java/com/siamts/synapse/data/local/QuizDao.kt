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
    fun toQuizQuestionDto(): QuizQuestionDto {
        return QuizQuestionDto(
            id = if (id > 0) id.toInt() else questionText.hashCode().coerceAtLeast(1),
            questionNumber = 1,
            question = questionText,
            options = mapOf(
                "A" to optionA,
                "B" to optionB,
                "C" to optionC,
                "D" to optionD
            ),
            correctOption = correctOption,
            explanation = explanation
        )
    }
}

/**
 * Data Access Object (DAO) for persisting and querying quiz attempts from 'quiz_records',
 * cached chapter quiz questions from 'chapter_questions', and Mistake Vault questions.
 */
interface QuizDao {
    suspend fun insertQuizRecord(record: QuizRecordEntity): Long
    suspend fun getAllRecords(): List<QuizRecordEntity>
    suspend fun getRecordsByChapter(chapterId: String): List<QuizRecordEntity>
    suspend fun getRecordsBySubject(subjectId: String): List<QuizRecordEntity>

    // Question Bank caching and retrieval
    suspend fun getQuestionsForChapter(subjectId: String, chapterId: String): List<QuizQuestionDto>
    suspend fun insertQuestionsForChapter(subjectId: String, chapterId: String, questions: List<QuizQuestionDto>)

    // Mistake Vault (Auto-saved incorrect MCQs for re-testing)
    suspend fun insertMistake(mistake: MistakeQuestionEntity): Long
    suspend fun getAllMistakes(): List<MistakeQuestionEntity>
    suspend fun getMistakesBySubject(subjectId: String): List<MistakeQuestionEntity>
    suspend fun removeMistake(questionText: String): Int
    suspend fun getMistakeCount(): Int
}
