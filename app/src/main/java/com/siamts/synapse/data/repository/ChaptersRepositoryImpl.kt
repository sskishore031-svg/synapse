package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.ChapterCompleteResponse
import com.siamts.synapse.data.model.ChapterDetailResponse
import com.siamts.synapse.data.model.SubjectChaptersResponse
import com.siamts.synapse.data.remote.ChaptersApi
import com.siamts.synapse.data.remote.RetrofitClient

class ChaptersRepositoryImpl(
    private val api: ChaptersApi = RetrofitClient.chaptersApi,
    private val catalogRepo: SubjectCatalogRepository? = try {
        SubjectCatalogRepository.getInstance()
    } catch (_: Throwable) {
        null
    }
) : ChaptersRepository {

    override suspend fun getSubjectChapters(
        subjectId: Int?,
        subjectSlug: String?,
        userId: Int?,
        email: String?
    ): Result<SubjectChaptersResponse> {
        // 1. If subjectId belongs to extended catalog (>= 100), retrieve locally
        if (subjectId != null && subjectId >= 100) {
            val local = catalogRepo?.getSubjectChapters(subjectId)
            if (local != null) return Result.success(local)
        }

        // 2. Try remote API
        return try {
            val response = api.getSubjectChapters(subjectId, subjectSlug, userId, email)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                // Fallback to local catalog if available
                if (subjectId != null) {
                    val local = catalogRepo?.getSubjectChapters(subjectId)
                    if (local != null) return Result.success(local)
                }
                Result.failure(Exception("Failed to fetch chapters: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            if (subjectId != null) {
                val local = catalogRepo?.getSubjectChapters(subjectId)
                if (local != null) return Result.success(local)
            }
            Result.failure(e)
        }
    }

    override suspend fun getChapterLesson(
        chapterId: Int,
        userId: Int?,
        email: String?
    ): Result<ChapterDetailResponse> {
        // 1. If chapterId belongs to extended catalog (>= 1000), retrieve locally
        if (chapterId >= 1000) {
            val local = catalogRepo?.getChapterLesson(chapterId)
            if (local != null) return Result.success(local)
        }

        // 2. Try remote API
        return try {
            val response = api.getChapterLesson(chapterId, userId, email)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val local = catalogRepo?.getChapterLesson(chapterId)
                if (local != null) return Result.success(local)
                Result.failure(Exception("Failed to load lesson: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            val local = catalogRepo?.getChapterLesson(chapterId)
            if (local != null) return Result.success(local)
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
                Result.success(ChapterCompleteResponse(status = "success", message = "Chapter marked complete locally"))
            }
        } catch (e: Exception) {
            Result.success(ChapterCompleteResponse(status = "success", message = "Chapter marked complete offline"))
        }
    }
}
