package com.siamts.synapse

import com.siamts.synapse.data.remote.RetrofitClient
import com.siamts.synapse.data.repository.ChemistryRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ChemistryRepositoryTest {

    private lateinit var repository: ChemistryRepositoryImpl

    @Before
    fun setUp() {
        RetrofitClient.updateBaseUrl("http://localhost/synapse_backend/")
        repository = ChemistryRepositoryImpl()
    }

    @Test
    fun testGetChemistryLesson_returnsRealSubjectAndLessonData() = runBlocking {
        val result = repository.getChemistryLesson(userId = 1, chapterId = 28)

        assertTrue("Expected Chemistry lesson request to succeed: ${result.exceptionOrNull()?.message}", result.isSuccess)
        val data = result.getOrNull()
        assertNotNull(data)

        // Verify Subject
        val subject = data?.subject
        assertNotNull(subject)
        assertEquals("Chemistry", subject?.name)
        assertEquals(5, subject?.totalChapters)

        // Verify Active Chapter & Lessons
        val activeChapter = data?.activeChapter
        assertNotNull(activeChapter)
        assertEquals("Chemical Bonding", activeChapter?.title)

        val part1 = activeChapter?.part1
        assertNotNull(part1)
        assertEquals("What is Chemical Bonding?", part1?.headline)
        assertFalse("Key points in Part 1 should not be empty", part1?.keyPoints.isNullOrEmpty())

        val part2 = activeChapter?.part2
        assertNotNull(part2)
        assertEquals("Ionic vs Covalent Bonding", part2?.headline)
        assertFalse("Key points in Part 2 should not be empty", part2?.keyPoints.isNullOrEmpty())

        // Verify 5 Chapters
        val chapters = data?.chapters
        assertNotNull(chapters)
        assertEquals(5, chapters?.size)
        assertTrue("Chapters should contain Chemical Bonding", chapters?.any { it.title == "Chemical Bonding" } == true)

        // Verify Quiz
        val quiz = data?.quiz
        assertNotNull(quiz)
        assertFalse("Quiz questions should be available", quiz?.questions.isNullOrEmpty())
    }

    @Test
    fun testCompleteChapter_updatesChapterCompletion() = runBlocking {
        val result = repository.completeChapter(userId = 1, chapterId = 28)

        assertTrue("Expected completeChapter to succeed: ${result.exceptionOrNull()?.message}", result.isSuccess)
        val data = result.getOrNull()
        assertNotNull(data)

        val chapters = data?.chapters
        val chemicalBonding = chapters?.find { it.id == "28" }
        assertNotNull(chemicalBonding)
        assertTrue("Chemical Bonding should be marked completed", chemicalBonding?.isCompleted == true)
    }

    @Test
    fun testSubmitQuiz_recordsScoreSuccessfully() = runBlocking {
        val result = repository.submitQuiz(
            userId = 1,
            chapterId = 28,
            scorePercentage = 85,
            correctAnswers = 3,
            totalQuestions = 3,
            timeTakenSeconds = 120
        )

        assertTrue("Expected submitQuiz to succeed: ${result.exceptionOrNull()?.message}", result.isSuccess)
        val data = result.getOrNull()
        assertNotNull(data)
    }
}

