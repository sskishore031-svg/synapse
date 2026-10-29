package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.ChemistryDataDto

/**
 * Repository interface for Chemistry subject lessons, progress, and quizzes.
 */
interface ChemistryRepository {

    /**
     * Fetches Chemistry subject details, chapter roadmap, and lesson content.
     */
    suspend fun getChemistryLesson(
        userId: Int? = null,
        email: String? = null,
        chapterId: Int? = null
    ): Result<ChemistryDataDto>

    /**
     * Marks a chapter completed in the backend for the current student.
     */
    suspend fun completeChapter(
        userId: Int,
        chapterId: Int
    ): Result<ChemistryDataDto>

    /**
     * Submits student quiz answers to synapse_backend.
     */
    suspend fun submitQuiz(
        userId: Int,
        chapterId: Int,
        scorePercentage: Int,
        correctAnswers: Int,
        totalQuestions: Int,
        timeTakenSeconds: Int = 180
    ): Result<ChemistryDataDto>
}

