package com.siamts.synapse.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.model.ProfileSetupRequest
import com.siamts.synapse.data.repository.ProfileRepository
import com.siamts.synapse.data.repository.ProfileRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Clean MVVM ViewModel managing Profile Setup state, user input, interest selections,
 * and remote onboarding sync via profile_setup.php.
 */
class ProfileSetupViewModel(
    private val repository: ProfileRepository = ProfileRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState: StateFlow<ProfileSetupUiState> = _uiState.asStateFlow()

    init {
        loadSessionDefaults()
    }

    private fun loadSessionDefaults() {
        viewModelScope.launch {
            val session = sessionManager ?: SessionManager.getInstanceOrNull()
            val user = session?.getSessionUser()
            if (user != null) {
                val userInitials = if (user.name.isNotBlank()) {
                    user.name.trim().take(1).uppercase()
                } else "S"

                _uiState.update { current ->
                    current.copy(
                        userId = user.id,
                        name = user.name.ifBlank { current.name },
                        initials = userInitials,
                        selectedCourse = user.course ?: current.selectedCourse,
                        preferredLanguage = user.preferredLanguage ?: current.preferredLanguage
                    )
                }
            }
        }
    }

    fun onNameChanged(name: String) {
        val initials = if (name.isNotBlank()) name.trim().take(1).uppercase() else "S"
        _uiState.update { it.copy(name = name, initials = initials) }
    }

    fun onCourseSelected(course: String) {
        _uiState.update { it.copy(selectedCourse = course) }
    }

    fun onLanguageSelected(language: String) {
        _uiState.update { it.copy(preferredLanguage = language) }
    }

    fun onToggleInterest(interest: String) {
        _uiState.update { currentState ->
            val updated = currentState.selectedInterests.toMutableSet()
            if (updated.contains(interest)) {
                updated.remove(interest)
            } else {
                updated.add(interest)
            }
            currentState.copy(selectedInterests = updated)
        }
    }

    /**
     * Submits student personalization choices to profile_setup.php and calls [onSuccess].
     */
    fun completeSetup(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val state = _uiState.value
            val request = ProfileSetupRequest(
                userId = state.userId.takeIf { it > 0 },
                name = state.name.trim(),
                course = state.selectedCourse.trim(),
                preferredLanguage = state.preferredLanguage.trim(),
                interests = state.selectedInterests.toList()
            )

            val result = repository.completeProfileSetup(request)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true, errorMessage = null) }
                onSuccess()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Failed to save profile setup."
                    )
                }
            }
        }
    }
}
