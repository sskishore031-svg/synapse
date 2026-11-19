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
        assertTrue(student?.name.orEmpty().isNotBlank())
        assertTrue(student?.email.orEmpty().isNotBlank())

        // 2. Verify Overall Learning Progress
        assertEquals(20, data?.totalTopics)

        // 3. Verify Subjects List (4 Placement Tracks)
        val subjects = data?.subjects
        assertNotNull("Subjects list should not be null", subjects)
        assertEquals(4, subjects?.size)

        // 4. Verify Continue Learning
        val continueLearning = data?.continueLearning
        assertNotNull("Continue learning should not be null", continueLearning)
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

