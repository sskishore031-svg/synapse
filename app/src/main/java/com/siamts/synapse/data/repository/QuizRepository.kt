package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.QuizQuestionsResponse
import com.siamts.synapse.data.model.QuizSubmitRequest
import com.siamts.synapse.data.model.QuizSubmitResponse
import com.siamts.synapse.data.model.QuizSubjectsResponse
import com.siamts.synapse.data.model.SubjectQuizzesResponse

interface QuizRepository {
    suspend fun getQuizSubjects(
        userId: Int? = null,
        email: String? = null
    ): Result<QuizSubjectsResponse>

    suspend fun getSubjectQuizzes(
        subjectId: Int? = null,
        subjectSlug: String? = null,
        userId: Int? = null,
        email: String? = null
    ): Result<SubjectQuizzesResponse>

    suspend fun getQuizQuestions(
        chapterId: Int,
        userId: Int? = null,
        email: String? = null
    ): Result<QuizQuestionsResponse>

    suspend fun submitQuiz(
        chapterId: Int,
        answers: Map<String, String>,
        userId: Int? = null,
        email: String? = null
    ): Result<QuizSubmitResponse>
}

