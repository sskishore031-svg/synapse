package com.siamts.synapse

import com.siamts.synapse.data.remote.RetrofitClient
import com.siamts.synapse.data.repository.HomeRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HomeDashboardRepositoryTest {

    private lateinit var repository: HomeRepositoryImpl

    @Before
    fun setUp() {
        // Point Retrofit to local machine Apache host for JVM test runner
        RetrofitClient.updateBaseUrl("http://localhost/synapse_backend/")
        repository = HomeRepositoryImpl()
    }

    @Test
    fun testGetHomeDashboard_withRealStudent_returnsCompleteDashboard() = runBlocking {
        val result = repository.getHomeDashboard(userId = 1)

        assertTrue("Expected home dashboard request to succeed: ${result.exceptionOrNull()?.message}", result.isSuccess)
        val data = result.getOrNull()
        assertNotNull("Dashboard data should not be null", data)

        // 1. Verify Student Profile
        val student = data?.student
        assertNotNull("Student should not be null", student)
        assertEquals(1, student?.id)
        assertEquals("tharun", student?.name)
        assertEquals("kishore@gmail.com", student?.email)
        assertNotNull("Profile photo should be populated", student?.profilePhoto)

        // 2. Verify Overall Learning Progress
        assertTrue("Topics done should be > 0", (data?.topicsDone ?: 0) > 0)
        assertEquals(30, data?.totalTopics)
        assertTrue("Overall progress should be > 0", (data?.overallProgress ?: 0) > 0)
        assertTrue("Mind power should be > 0", (data?.mindPower ?: 0) > 0)

        // 3. Verify Subjects List
        val subjects = data?.subjects
        assertNotNull("Subjects list should not be null", subjects)
        assertEquals(6, subjects?.size)

        // 4. Verify Continue Learning & Recent Sessions
        val continueLearning = data?.continueLearning
        assertNotNull("Continue learning should not be null", continueLearning)
        assertTrue("Chapter ID should be > 0", (continueLearning?.chapterId ?: 0) > 0)
        assertTrue("Chapter title should not be blank", continueLearning?.chapterTitle.orEmpty().isNotBlank())

        val sessions = data?.recentSessions
        assertNotNull("Recent sessions should not be null", sessions)
        assertTrue("Recent sessions should have items", sessions.orEmpty().isNotEmpty())

        // 5. Verify Quiz and Performance Statistics
        assertTrue("Quizzes attempted should be > 0", (data?.quizzesAttempted ?: 0) > 0)
        assertTrue("Average quiz score should be > 0", (data?.quizScore ?: 0) > 0)
        assertTrue("Latest quiz score should be > 0", (data?.latestQuizScore ?: 0) > 0)

        // 6. Verify BrainMap analytics summary
        val brainMap = data?.brainMap
        assertNotNull("BrainMap summary should not be null", brainMap)
        assertEquals(6, brainMap?.nodes?.size)
    }

    @Test
    fun testGetHomeDashboard_withEmail_returnsCorrectStudent() = runBlocking {
        val result = repository.getHomeDashboard(email = "kishore@gmail.com")

        assertTrue("Expected email-based dashboard request to succeed", result.isSuccess)
        val data = result.getOrNull()
        assertEquals("kishore@gmail.com", data?.student?.email)
    }

    @Test
    fun testGetHomeDashboard_withNonExistentUser_returnsFailure() = runBlocking {
        val result = repository.getHomeDashboard(userId = 99999)

        assertTrue("Expected non-existent user to fail with 404", result.isFailure)
    }
}

