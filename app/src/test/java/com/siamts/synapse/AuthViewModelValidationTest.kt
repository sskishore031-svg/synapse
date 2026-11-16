package com.siamts.synapse

import com.siamts.synapse.data.model.AuthResponse
import com.siamts.synapse.data.model.UserDto
import com.siamts.synapse.data.repository.AuthRepository
import com.siamts.synapse.ui.auth.AuthViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthViewModelValidationTest {

    private lateinit var viewModel: AuthViewModel

    private val fakeRepository = object : AuthRepository {
        override suspend fun login(identifier: String, password: String): Result<AuthResponse> {
            return Result.success(AuthResponse(status = "success", message = "Login OK"))
        }

        override suspend fun signup(
            name: String,
            email: String,
            password: String,
            confirmPassword: String?,
            phone: String?
        ): Result<AuthResponse> {
            return Result.success(AuthResponse(status = "success", message = "Register OK"))
        }

        override suspend fun resetPasswordDirect(email: String, newPassword: String): Result<String> =
            Result.success("OK")

        override suspend fun resetPassword(token: String, newPassword: String): Result<String> =
            Result.success("OK")

        override suspend fun validateSession(userId: Int): Result<UserDto> =
            Result.success(UserDto(id = userId, name = "Test User", email = "test@example.com"))
    }

    @Before
    fun setup() {
        viewModel = AuthViewModel(fakeRepository)
    }

    @Test
    fun testEmptyLoginIdentifier_setsErrorMessage() {
        viewModel.onLogin(email = "", password = "password123")
        val state = viewModel.uiState.value
        assertEquals("Please enter your email or user ID.", state.errorMessage)
        assertFalse(state.isLoading)
    }

    @Test
    fun testEmptyLoginPassword_setsErrorMessage() {
        viewModel.onLogin(email = "test@gmail.com", password = "")
        val state = viewModel.uiState.value
        assertEquals("Please enter your password.", state.errorMessage)
        assertFalse(state.isLoading)
    }

    @Test
    fun testEmptyRegisterName_setsErrorMessage() {
        viewModel.onRegister(name = "", email = "test@gmail.com", password = "password123", confirmPassword = "password123", phone = "9876543210")
        val state = viewModel.uiState.value
        assertEquals("Please enter your full name.", state.errorMessage)
        assertFalse(state.isLoading)
    }

    @Test
    fun testInvalidEmail_setsErrorMessage() {
        viewModel.onRegister(name = "John Doe", email = "invalid-email-format", password = "password123", confirmPassword = "password123", phone = "9876543210")
        val state = viewModel.uiState.value
        assertEquals("Please enter a valid @gmail.com address", state.errorMessage)
        assertFalse(state.isLoading)
    }

    @Test
    fun testNonGmail_setsErrorMessage() {
        viewModel.onRegister(name = "John Doe", email = "test@yahoo.com", password = "password123", confirmPassword = "password123", phone = "9876543210")
        val state = viewModel.uiState.value
        assertEquals("Please enter a valid @gmail.com address", state.errorMessage)
        assertFalse(state.isLoading)
    }

    @Test
    fun testInvalidPhone_setsErrorMessage() {
        viewModel.onRegister(name = "John Doe", email = "test@gmail.com", password = "password123", confirmPassword = "password123", phone = "123")
        val state = viewModel.uiState.value
        assertEquals("Please enter a valid 10-digit phone number.", state.errorMessage)
        assertFalse(state.isLoading)
    }

    @Test
    fun testShortPassword_setsErrorMessage() {
        viewModel.onRegister(name = "John Doe", email = "test@gmail.com", password = "123", confirmPassword = "123", phone = "9876543210")
        val state = viewModel.uiState.value
        assertEquals("Password must be at least 6 characters long.", state.errorMessage)
        assertFalse(state.isLoading)
    }

    @Test
    fun testPasswordMismatch_setsErrorMessage() {
        viewModel.onRegister(name = "John Doe", email = "test@gmail.com", password = "password123", confirmPassword = "mismatch123", phone = "9876543210")
        val state = viewModel.uiState.value
        assertEquals("Password and Confirm Password do not match.", state.errorMessage)
        assertFalse(state.isLoading)
    }

    @Test
    fun testTogglePasswordVisibility() {
        assertFalse(viewModel.uiState.value.isPasswordVisible)
        viewModel.onTogglePasswordVisibility()
        assertTrue(viewModel.uiState.value.isPasswordVisible)
        viewModel.onTogglePasswordVisibility()
        assertFalse(viewModel.uiState.value.isPasswordVisible)
    }
}
