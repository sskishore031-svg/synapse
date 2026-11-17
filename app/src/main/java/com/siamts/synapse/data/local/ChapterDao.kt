package com.siamts.synapse.data.local

/**
 * Data Access Object (DAO) for querying and persisting subject chapters and lessons.
 */
interface ChapterDao {
    suspend fun getChaptersBySubject(subjectId: String): List<ChapterEntity>
    suspend fun insertChapters(chapters: List<ChapterEntity>)
    suspend fun getAllChapters(): List<ChapterEntity>
    suspend fun insertChapterLesson(
        chapterId: Int,
        subjectId: Int,
        title: String,
        part1Title: String,
        part1Concept: String,
        part2Title: String,
        part2Application: String,
        examTip: String
    )
    suspend fun markChapterCompleted(chapterId: Int)
}
