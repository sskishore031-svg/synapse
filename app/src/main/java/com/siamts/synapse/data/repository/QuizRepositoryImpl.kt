package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.QuizDataDto
import com.siamts.synapse.data.model.QuizQuestionsDataDto
import com.siamts.synapse.data.model.QuizQuestionsResponse
import com.siamts.synapse.data.model.QuizSubjectItemDto
import com.siamts.synapse.data.model.QuizSubjectsDataDto
import com.siamts.synapse.data.model.QuizSubjectsResponse
import com.siamts.synapse.data.model.QuizSubmitRequest
import com.siamts.synapse.data.model.QuizSubmitResponse
import com.siamts.synapse.data.model.SubjectQuizItemDto
import com.siamts.synapse.data.model.SubjectQuizzesDataDto
import com.siamts.synapse.data.model.SubjectQuizzesResponse
import com.siamts.synapse.data.remote.QuizApi
import com.siamts.synapse.data.remote.RetrofitClient

class QuizRepositoryImpl(
    private val api: QuizApi = RetrofitClient.quizApi,
    private val catalogRepo: SubjectCatalogRepository? = try {
        SubjectCatalogRepository.getInstance()
    } catch (_: Throwable) {
        null
    }
) : QuizRepository {

    override suspend fun getQuizSubjects(
        userId: Int?,
        email: String?
    ): Result<QuizSubjectsResponse> {
        val baseResult = try {
            val response = api.getQuizSubjects(userId, email)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else null
        } catch (_: Exception) {
            null
        }

        val baseSubjects = baseResult?.data?.subjects.orEmpty().toMutableList()
        val addedCatalog = catalogRepo?.getAddedSubjects().orEmpty()

        for (cSub in addedCatalog) {
            if (baseSubjects.none { it.id == cSub.subjectId || it.name.equals(cSub.name, ignoreCase = true) }) {
                baseSubjects.add(
                    QuizSubjectItemDto(
                        id = cSub.subjectId,
                        name = cSub.name,
                        slug = cSub.slug,
                        iconSymbol = cSub.iconSymbol,
                        colorHex = cSub.colorHex,
                        availableQuizzes = cSub.chapters.size,
                        attemptedQuizzes = 0,
                        averageScore = 0
                    )
                )
            }
        }

        return Result.success(
            QuizSubjectsResponse(
                status = "success",
                data = QuizSubjectsDataDto(subjects = baseSubjects)
            )
        )
    }

    override suspend fun getSubjectQuizzes(
        subjectId: Int?,
        subjectSlug: String?,
        userId: Int?,
        email: String?
    ): Result<SubjectQuizzesResponse> {
        if (subjectId != null && subjectId >= 100) {
            val catalogSub = catalogRepo?.getSubjectByNumericId(subjectId)
            if (catalogSub != null) {
                val quizzes = catalogSub.chapters.map { ch ->
                    SubjectQuizItemDto(
                        chapterId = ch.chapterId,
                        chapterOrder = ch.chapterOrder,
                        chapterTitle = ch.title,
                        quizTitle = "${ch.title} Quiz",
                        subjectId = catalogSub.subjectId,
                        subjectName = catalogSub.name,
                        subjectSlug = catalogSub.slug,
                        questionCount = ch.mcqs.size.coerceAtLeast(5),
                        durationMinutes = 10,
                        totalMarks = ch.mcqs.size * 10
                    )
                }
                return Result.success(
                    SubjectQuizzesResponse(
                        status = "success",
                        data = SubjectQuizzesDataDto(quizzes = quizzes)
                    )
                )
            }
        }

        return try {
            val response = api.getSubjectQuizzes(subjectId, subjectSlug, userId, email)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch subject quizzes: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getQuizQuestions(
        chapterId: Int,
        userId: Int?,
        email: String?
    ): Result<QuizQuestionsResponse> {
        // 1. If catalog chapter (>= 1000)
        if (chapterId >= 1000) {
            val mcqs = catalogRepo?.getChapterQuiz(chapterId)
            val chapter = catalogRepo?.getChapterById(chapterId)
            if (!mcqs.isNullOrEmpty() && chapter != null) {
                return Result.success(
                    QuizQuestionsResponse(
                        status = "success",
                        data = QuizQuestionsDataDto(
                            quiz = QuizDataDto(
                                title = "${chapter.title} Quiz",
                                chapterId = chapterId,
                                chapterTitle = chapter.title,
                                chapterOrder = chapter.chapterOrder,
                                totalQuestions = mcqs.size
                            ),
                            questions = mcqs
                        )
                    )
                )
            }
        }

        // 2. Try remote API
        return try {
            val response = api.getQuizQuestions(chapterId, userId, email)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch quiz questions: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitQuiz(
        chapterId: Int,
        answers: Map<String, String>,
        userId: Int?,
        email: String?
    ): Result<QuizSubmitResponse> {
        // 1. If catalog chapter (>= 1000), evaluate locally
        if (chapterId >= 1000) {
            val localResult = catalogRepo?.submitLocalQuiz(chapterId, answers)
            if (localResult != null) {
                return Result.success(
                    QuizSubmitResponse(
                        status = "success",
                        message = "Quiz evaluated on-device",
                        data = localResult
                    )
                )
            }
        }

        // 2. Try remote API
        return try {
            val request = QuizSubmitRequest(chapterId = chapterId, answers = answers)
            val response = api.submitQuiz(chapterId = chapterId, userId = userId, email = email, request = request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                // Fallback to local evaluation if available
                val localResult = catalogRepo?.submitLocalQuiz(chapterId, answers)
                Result.success(
                    QuizSubmitResponse(
                        status = "success",
                        message = "Quiz evaluated on-device fallback",
                        data = localResult
                    )
                )
            }
        } catch (e: Exception) {
            val localResult = catalogRepo?.submitLocalQuiz(chapterId, answers)
            Result.success(
                QuizSubmitResponse(
                    status = "success",
                    message = "Quiz evaluated offline",
                    data = localResult
                )
            )
        }
    }
}
