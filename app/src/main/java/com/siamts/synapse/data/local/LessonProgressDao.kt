package com.siamts.synapse.data.local

import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for real-time lesson and track completion progress.
 */
interface LessonProgressDao {
    suspend fun saveProgress(progress: LessonProgressEntity)
    suspend fun markLessonCompleted(lessonId: String, trackId: String, userId: String = "1"): LessonProgressEntity
    suspend fun markQuizCompleted(lessonId: String, trackId: String, userId: String = "1"): LessonProgressEntity
    suspend fun getProgress(lessonId: String, userId: String = "1"): LessonProgressEntity?
    fun getProgressCached(lessonId: String, userId: String = "1"): LessonProgressEntity? = null
    suspend fun getProgressForUser(userId: String = "1"): List<LessonProgressEntity>
    fun observeProgressForUser(userId: String = "1"): Flow<List<LessonProgressEntity>>
    suspend fun getCompletedLessonsForTrack(trackId: String, userId: String = "1"): List<LessonProgressEntity>
    suspend fun resetTrackProgress(trackId: String, userId: String = "1")
    suspend fun clearAllProgress()
}
