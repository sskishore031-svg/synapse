package com.siamts.synapse.data.local

/**
 * Data Access Object (DAO) for persisting and querying quiz attempts from 'quiz_records'.
 */
interface QuizDao {
    suspend fun insertQuizRecord(record: QuizRecordEntity): Long
    suspend fun getAllRecords(): List<QuizRecordEntity>
    suspend fun getRecordsByChapter(chapterId: String): List<QuizRecordEntity>
    suspend fun getRecordsBySubject(subjectId: String): List<QuizRecordEntity>
}
