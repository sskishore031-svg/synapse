package com.siamts.synapse

import com.siamts.synapse.data.remote.RetrofitClient
import com.siamts.synapse.data.repository.BrainMapRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BrainMapRepositoryTest {

    private lateinit var repository: BrainMapRepositoryImpl

    @Before
    fun setUp() {
        // Point Retrofit to local machine Apache host for JVM test runner
        RetrofitClient.updateBaseUrl("http://localhost/synapse_backend/")
        repository = BrainMapRepositoryImpl()
    }

    @Test
    fun testGetBrainMapAnalytics_withRealStudentData_returnsValidMetrics() = runBlocking {
        val result = repository.getBrainMapAnalytics(userId = 1)

        assertTrue("Expected BrainMap request to succeed: ${result.exceptionOrNull()?.message}", result.isSuccess)
        val data = result.getOrNull()
        assertNotNull(data)

        // Verify summary
        val summary = data?.summary
        assertNotNull(summary)
        assertEquals(6, summary?.totalSubjectsAvailable)
        assertTrue("Total chapters completed should be > 0", (summary?.totalChaptersCompleted ?: 0) > 0)
        assertTrue("Total quizzes attempted should be > 0", (summary?.totalQuizzesAttempted ?: 0) > 0)
        assertTrue("Overall percentage should be > 0", (summary?.overallKnowledgePercentage ?: 0) > 0)

        // Verify nodes
        val nodes = data?.nodes
        assertNotNull(nodes)
        assertEquals(6, nodes?.size)

        val mathNode = nodes?.find { it.title.equals("Mathematics", ignoreCase = true) }
        assertNotNull(mathNode)
        assertEquals("1", mathNode?.id)
        assertTrue("Math percentage should be >= 0", (mathNode?.percentage ?: -1) >= 0)

        val csNode = nodes?.find { it.title.equals("Computer Science", ignoreCase = true) }
        assertNotNull(csNode)
        assertEquals("2", csNode?.id)
        assertTrue("CS percentage should be >= 0", (csNode?.percentage ?: -1) >= 0)
    }

    @Test
    fun testGetBrainMapAnalytics_withEmptyStudentData_returnsZeroState() = runBlocking {
        val result = repository.getBrainMapAnalytics(userId = 6)

        assertTrue("Expected BrainMap request for empty student to succeed", result.isSuccess)
        val data = result.getOrNull()
        assertNotNull(data)

        val summary = data?.summary
        assertNotNull(summary)
        assertEquals(0, summary?.totalChaptersCompleted)
        assertEquals(0, summary?.totalQuizzesAttempted)
        assertEquals(0, summary?.overallKnowledgePercentage)
        assertEquals(0, summary?.averageQuizScore)
        assertNull("Empty student should have null strongest subject", summary?.strongestSubject)
        assertNull("Empty student should have null weakest subject", summary?.weakestSubject)
    }

    @Test
    fun testGetBrainMapAnalytics_withNonExistentStudent_returnsError() = runBlocking {
        val result = repository.getBrainMapAnalytics(userId = 999999)

        assertTrue("Expected non-existent user request to fail", result.isFailure)
        val error = result.exceptionOrNull()?.message
        assertNotNull(error)
        assertTrue(
            "Expected 'User not found' error but got: $error",
            error?.contains("User not found", ignoreCase = true) == true
        )
    }
}

