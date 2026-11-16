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
    },
    private val curriculumRepo: CurriculumRepository = CurriculumRepository.getInstance()
) : QuizRepository {

    override suspend fun getQuizSubjects(
        userId: Int?,
        email: String?
    ): Result<QuizSubjectsResponse> {
        val remoteSubjects = try {
            val response = api.getQuizSubjects(userId, email)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.data?.subjects.orEmpty()
            } else emptyList()
        } catch (_: Exception) {
            emptyList()
        }

        val subjectList = remoteSubjects.toMutableList()
        val allLocalSubjects = catalogRepo?.getAllSubjects().orEmpty()

        for (cSub in allLocalSubjects) {
            if (subjectList.none { it.id == cSub.subjectId || it.name.equals(cSub.name, ignoreCase = true) }) {
                subjectList.add(
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
                data = QuizSubjectsDataDto(subjects = subjectList)
            )
        )
    }

    override suspend fun getSubjectQuizzes(
        subjectId: Int?,
        subjectSlug: String?,
        userId: Int?,
        email: String?
    ): Result<SubjectQuizzesResponse> {
        // Try remote first, only accepting non-empty quizzes
        try {
            val response = api.getSubjectQuizzes(subjectId, subjectSlug, userId, email)
            if (response.isSuccessful && response.body() != null) {
                val quizzes = response.body()!!.data?.quizzes.orEmpty()
                if (quizzes.isNotEmpty()) {
                    return Result.success(response.body()!!)
                }
            }
        } catch (_: Exception) {
            // Fall through to local fallback
        }

        // Guaranteed on-device resolution across all 50+ subjects
        val sId = subjectId?.toString() ?: subjectSlug.orEmpty()
        val sName = subjectSlug ?: subjectId?.toString()
        val quizzes = curriculumRepo.getSubjectQuizzes(sId, sName)
        if (quizzes.isNotEmpty()) {
            return Result.success(
                SubjectQuizzesResponse(
                    status = "success",
                    data = SubjectQuizzesDataDto(quizzes = quizzes)
                )
            )
        }

        return Result.failure(Exception("Subject not found for quiz retrieval"))
    }

    override suspend fun getQuizQuestions(
        chapterId: Int,
        userId: Int?,
        email: String?
    ): Result<QuizQuestionsResponse> {
        // 1. Try remote API
        try {
            val response = api.getQuizQuestions(chapterId, userId, email)
            if (response.isSuccessful && response.body() != null) {
                val questions = response.body()!!.data?.questions.orEmpty()
                if (questions.isNotEmpty()) {
                    return Result.success(response.body()!!)
                }
            }
        } catch (_: Exception) {
            // Remote failed, proceed to local question bank
        }

        // 2. Local question bank fallback (works for all chapters in all subjects)
        val cachedMcqs = try {
            com.siamts.synapse.data.local.QuizLocalDatabase.getInstance()
                .getQuestionsForChapter("", chapterId.toString())
        } catch (_: Throwable) { emptyList() }
        if (cachedMcqs.isNotEmpty()) {
            return Result.success(
                QuizQuestionsResponse(
                    status = "success",
                    data = QuizQuestionsDataDto(
                        quiz = QuizDataDto(
                            title = "Chapter $chapterId Quiz",
                            chapterId = chapterId,
                            chapterTitle = "Chapter $chapterId",
                            chapterOrder = chapterId,
                            totalQuestions = cachedMcqs.size
                        ),
                        questions = cachedMcqs
                    )
                )
            )
        }

        val baseChapter = com.siamts.synapse.data.local.BaseCurriculumData.getChapterById(chapterId)
        val mcqs = baseChapter?.mcqs?.ifEmpty { null } ?: catalogRepo?.getChapterQuiz(chapterId)
        val chapter = baseChapter ?: catalogRepo?.getChapterById(chapterId)
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

        return Result.failure(Exception("No quiz questions available for chapter $chapterId"))
    }

    override suspend fun submitQuiz(
        chapterId: Int,
        answers: Map<String, String>,
        userId: Int?,
        email: String?
    ): Result<QuizSubmitResponse> {
        // Try remote submission first
        try {
            val request = QuizSubmitRequest(chapterId = chapterId, answers = answers)
            val response = api.submitQuiz(chapterId = chapterId, userId = userId, email = email, request = request)
            if (response.isSuccessful && response.body() != null) {
                return Result.success(response.body()!!)
            }
        } catch (_: Exception) {
            // Remote submission failed or offline; fall back to local grading
        }

        // Offline on-device grading
        val localResult = catalogRepo?.submitLocalQuiz(chapterId, answers)
        return Result.success(
            QuizSubmitResponse(
                status = "success",
                message = "Quiz evaluated on-device",
                data = localResult
            )
        )
    }
}
