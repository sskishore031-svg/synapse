package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.ChapterCompleteResponse
import com.siamts.synapse.data.model.LessonBookmarkResponse
import com.siamts.synapse.data.model.LessonDetailResponse
import com.siamts.synapse.data.remote.LessonApi
import com.siamts.synapse.data.remote.RetrofitClient

class LessonRepositoryImpl(
    private val api: LessonApi = RetrofitClient.lessonApi
) : LessonRepository {

    override suspend fun getLessonDetails(
        chapterId: Int?,
        subjectId: Int?,
        subjectSlug: String?,
        part: Int?,
        userId: Int?,
        email: String?
    ): Result<LessonDetailResponse> {
        return try {
            val response = api.getLessonDetails(
                chapterId = chapterId,
                subjectId = subjectId,
                subjectSlug = subjectSlug,
                part = part,
                userId = userId,
                email = email
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch lesson: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun completeLesson(
        chapterId: Int,
        userId: Int?,
        email: String?
    ): Result<ChapterCompleteResponse> {
        return try {
            val response = api.completeLesson(
                chapterId = chapterId,
                userId = userId,
                email = email
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to complete lesson: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleBookmark(
        chapterId: Int,
        part: Int,
        userId: Int?,
        email: String?
    ): Result<LessonBookmarkResponse> {
        return try {
            val response = api.toggleBookmark(
                chapterId = chapterId,
                part = part,
                userId = userId,
                email = email
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to bookmark lesson: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

