package com.siamts.synapse.data.local

/**
 * Data Access Object (DAO) for persisting and querying real user study activity
 * from the 'study_sessions' table.
 */
interface StudySessionDao {
    suspend fun insertSession(session: StudySessionEntity): Long
    suspend fun getAllSessions(): List<StudySessionEntity>
    suspend fun getSessionsByDate(date: String): List<StudySessionEntity>
    suspend fun getSessionsBetween(startDate: String, endDate: String): List<StudySessionEntity>
    suspend fun getSessionsByTrack(trackName: String): List<StudySessionEntity>
    suspend fun getAllActiveDates(): List<String>
    suspend fun clearSessions()
}
