package com.siamts.synapse.data.local

import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for real-time two-stage lesson and track completion progress.
 */
interface LessonProgressDao {
    suspend fun saveProgress(progress: LessonProgressEntity)
    suspend fun markLessonCompleted(lessonId: String, trackId: String, userId: String = "1"): LessonProgressEntity
    suspend fun markConceptCompleted(lessonId: String, trackId: String, userId: String = "1"): LessonProgressEntity = markLessonCompleted(lessonId, trackId, userId)
    suspend fun markConceptDone(userId: String = "1", trackId: String, chapterIndex: Int): LessonProgressEntity
    suspend fun markQuizCompleted(lessonId: String, trackId: String, userId: String = "1"): LessonProgressEntity
    suspend fun markQuizDone(userId: String = "1", trackId: String, chapterIndex: Int): LessonProgressEntity
    suspend fun getProgress(lessonId: String, userId: String = "1"): LessonProgressEntity?
    fun getProgressCached(lessonId: String, userId: String = "1"): LessonProgressEntity? = null
    suspend fun getChapterProgress(trackId: String, chapterIndex: Int, userId: String = "1"): LessonProgressEntity?
    fun observeChapterProgress(trackId: String, chapterIndex: Int, userId: String = "1"): Flow<LessonProgressEntity?>
    suspend fun resetChapterProgress(trackId: String, chapterIndex: Int, userId: String = "1"): LessonProgressEntity
    suspend fun getProgressForUser(userId: String = "1"): List<LessonProgressEntity>
    fun observeProgressForUser(userId: String = "1"): Flow<List<LessonProgressEntity>>
    suspend fun getCompletedLessonsForTrack(trackId: String, userId: String = "1"): List<LessonProgressEntity>
    suspend fun resetTrackProgress(trackId: String, userId: String = "1")
    suspend fun clearAllProgress()
}
