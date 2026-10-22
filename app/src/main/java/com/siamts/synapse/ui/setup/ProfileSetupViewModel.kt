package com.siamts.synapse.ui.setup

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Profile Setup state, user input, and interest selections.
 */
class ProfileSetupViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState: StateFlow<ProfileSetupUiState> = _uiState.asStateFlow()

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name) }
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
}
