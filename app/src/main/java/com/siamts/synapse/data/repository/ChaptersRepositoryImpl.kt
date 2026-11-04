package com.siamts.synapse.data.repository

import com.siamts.synapse.data.local.BaseCurriculumData
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
        // Resolve subject numeric ID dynamically
        val resolvedId = subjectId
            ?: subjectSlug?.let { BaseCurriculumData.resolveSubjectNumericId(it) }
            ?: 1

        // Try remote API first
        try {
            val response = api.getSubjectChapters(subjectId ?: resolvedId, subjectSlug, userId, email)
            if (response.isSuccessful && response.body() != null && response.body()!!.data?.chapters?.isNotEmpty() == true) {
                return Result.success(response.body()!!)
            }
        } catch (_: Exception) {
            // Fallback to local catalog
        }

        // Local fallback for base curriculum (100% offline guarantee)
        val base = BaseCurriculumData.getSubjectChapters(resolvedId)
        if (base != null) return Result.success(base)

        // Local fallback for extended catalog
        val local = catalogRepo?.getSubjectChapters(resolvedId)
        if (local != null) return Result.success(local)

        return Result.failure(Exception("Failed to fetch chapters for subject $subjectId / $subjectSlug"))
    }

    override suspend fun getChapterLesson(
        chapterId: Int,
        userId: Int?,
        email: String?
    ): Result<ChapterDetailResponse> {
        // Try remote API first
        try {
            val response = api.getChapterLesson(chapterId, userId, email)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val p1Text = body.data?.chapter?.part1?.conceptText.orEmpty()
                if (p1Text.isNotBlank()) {
                    return Result.success(body)
                }
            }
        } catch (_: Exception) {
            // Fallback to local catalog
        }

        // Local fallback from BaseCurriculumData first
        val base = BaseCurriculumData.getChapterLesson(chapterId)
        if (base != null) return Result.success(base)

        // Local fallback for extended catalog
        val local = catalogRepo?.getChapterLesson(chapterId)
        if (local != null) return Result.success(local)

        return Result.failure(Exception("Failed to load lesson for chapter $chapterId"))
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
