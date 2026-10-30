package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.ChapterCompleteResponse
import com.siamts.synapse.data.model.ChapterDetailResponse
import com.siamts.synapse.data.model.SubjectChaptersResponse
import com.siamts.synapse.data.remote.ChaptersApi
import com.siamts.synapse.data.remote.RetrofitClient

class ChaptersRepositoryImpl(
    private val api: ChaptersApi = RetrofitClient.chaptersApi
) : ChaptersRepository {

    override suspend fun getSubjectChapters(
        subjectId: Int?,
        subjectSlug: String?,
        userId: Int?,
        email: String?
    ): Result<SubjectChaptersResponse> {
        return try {
            val response = api.getSubjectChapters(subjectId, subjectSlug, userId, email)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch chapters: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getChapterLesson(
        chapterId: Int,
        userId: Int?,
        email: String?
    ): Result<ChapterDetailResponse> {
        return try {
            val response = api.getChapterLesson(chapterId, userId, email)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load lesson: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun completeChapter(
        chapterId: Int,
        userId: Int?,
        email: String?
    ): Result<ChapterCompleteResponse> {
        return try {
            val response = api.completeChapter(chapterId, userId, email)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to complete chapter: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

