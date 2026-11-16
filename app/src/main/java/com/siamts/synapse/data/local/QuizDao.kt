package com.siamts.synapse.data.local

import com.siamts.synapse.data.model.QuizQuestionDto

/**
 * Data Access Object (DAO) for persisting and querying quiz attempts from 'quiz_records'
 * and cached chapter quiz questions from 'chapter_questions'.
 */
interface QuizDao {
    suspend fun insertQuizRecord(record: QuizRecordEntity): Long
    suspend fun getAllRecords(): List<QuizRecordEntity>
    suspend fun getRecordsByChapter(chapterId: String): List<QuizRecordEntity>
    suspend fun getRecordsBySubject(subjectId: String): List<QuizRecordEntity>

    // Question Bank caching and retrieval
    suspend fun getQuestionsForChapter(subjectId: String, chapterId: String): List<QuizQuestionDto>
    suspend fun insertQuestionsForChapter(subjectId: String, chapterId: String, questions: List<QuizQuestionDto>)
}
