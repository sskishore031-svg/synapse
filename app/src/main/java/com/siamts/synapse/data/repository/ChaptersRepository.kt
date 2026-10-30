package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.ChapterCompleteResponse
import com.siamts.synapse.data.model.ChapterDetailResponse
import com.siamts.synapse.data.model.SubjectChaptersResponse

interface ChaptersRepository {
    suspend fun getSubjectChapters(
        subjectId: Int? = null,
        subjectSlug: String? = null,
        userId: Int? = null,
        email: String? = null
    ): Result<SubjectChaptersResponse>

    suspend fun getChapterLesson(
        chapterId: Int,
        userId: Int? = null,
        email: String? = null
    ): Result<ChapterDetailResponse>

    suspend fun completeChapter(
        chapterId: Int,
        userId: Int? = null,
        email: String? = null
    ): Result<ChapterCompleteResponse>
}

