package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.ChapterCompleteResponse
import com.siamts.synapse.data.model.LessonBookmarkResponse
import com.siamts.synapse.data.model.LessonDetailResponse

interface LessonRepository {
    suspend fun getLessonDetails(
        chapterId: Int? = null,
        subjectId: Int? = null,
        subjectSlug: String? = null,
        part: Int? = null,
        userId: Int? = null,
        email: String? = null
    ): Result<LessonDetailResponse>

    suspend fun completeLesson(
        chapterId: Int,
        userId: Int? = null,
        email: String? = null
    ): Result<ChapterCompleteResponse>

    suspend fun toggleBookmark(
        chapterId: Int,
        part: Int = 1,
        userId: Int? = null,
        email: String? = null
    ): Result<LessonBookmarkResponse>
}

