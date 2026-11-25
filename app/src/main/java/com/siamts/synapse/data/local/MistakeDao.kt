package com.siamts.synapse.data.local

/**
 * DAO interface for Mistake Vault operations.
 */
interface MistakeDao {
    suspend fun insertMistake(mistake: MistakeQuestionEntity): Long
    suspend fun getAllMistakes(): List<MistakeQuestionEntity>
    suspend fun getMistakesBySubject(subjectId: String): List<MistakeQuestionEntity>
    suspend fun removeMistake(questionText: String): Int
    suspend fun getMistakeCount(): Int
}
