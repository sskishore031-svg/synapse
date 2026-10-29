package com.siamts.synapse

import com.siamts.synapse.data.remote.RetrofitClient
import com.siamts.synapse.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthRepositoryTest {

    private lateinit var authRepository: AuthRepositoryImpl

    @Before
    fun setUp() {
        // When running on host machine JVM test runner, use localhost instead of 10.0.2.2
        RetrofitClient.updateBaseUrl("http://localhost/synapse_backend/")
        authRepository = AuthRepositoryImpl()
    }

    @Test
    fun testLoginSuccess_returnsValidUserData() = runBlocking {
        val result = authRepository.login("kishore@gmail.com", "password123")

        assertTrue("Expected login to succeed: ${result.exceptionOrNull()?.message}", result.isSuccess)
        val response = result.getOrNull()
        assertNotNull(response)
        assertEquals("success", response?.status)
        assertEquals("kishore@gmail.com", response?.user?.email)
    }

    @Test
    fun testLoginFailure_returnsErrorMessage() = runBlocking {
        val result = authRepository.login("kishore@gmail.com", "wrongpassword12345")

        assertTrue("Expected login to fail", result.isFailure)
        val errorMessage = result.exceptionOrNull()?.message
        assertNotNull(errorMessage)
        assertTrue(
            "Expected invalid credentials error message but got: $errorMessage",
            errorMessage?.contains("Invalid", ignoreCase = true) == true
        )
    }

    @Test
    fun testValidateSessionSuccess_returnsUserDto() = runBlocking {
        val result = authRepository.validateSession(1)
        assertTrue("Expected validateSession to succeed: ${result.exceptionOrNull()?.message}", result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals(1, user?.id)
        assertEquals("kishore@gmail.com", user?.email)
    }

    @Test
    fun testRegisterDuplicateEmail_returnsConflictError() = runBlocking {
        val result = authRepository.signup(
            name = "Test User",
            email = "kishore@gmail.com",
            password = "password123",
            confirmPassword = "password123"
        )

        assertTrue("Expected registration with existing email to fail", result.isFailure)
        val errorMessage = result.exceptionOrNull()?.message
        assertNotNull(errorMessage)
        assertTrue(
            "Expected duplicate email error message but got: $errorMessage",
            errorMessage?.contains("already exists", ignoreCase = true) == true
        )
    }
}
