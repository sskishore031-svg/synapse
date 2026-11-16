package com.siamts.synapse.data.local

/**
 * Data Access Object (DAO) for querying and persisting subject chapters.
 */
interface ChapterDao {
    suspend fun getChaptersBySubject(subjectId: String): List<ChapterEntity>
    suspend fun insertChapters(chapters: List<ChapterEntity>)
    suspend fun getAllChapters(): List<ChapterEntity>
}

