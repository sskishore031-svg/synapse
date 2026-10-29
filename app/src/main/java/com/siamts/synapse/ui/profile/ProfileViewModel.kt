package com.siamts.synapse.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.model.ProfileUpdateRequest
import com.siamts.synapse.data.model.UserProfileDto
import com.siamts.synapse.data.repository.ProfileRepository
import com.siamts.synapse.data.repository.ProfileRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Clean MVVM ViewModel managing Profile & Settings state, remote sync via profile.php,
 * and user logout lifecycle.
 */
class ProfileViewModel(
    private val repository: ProfileRepository = ProfileRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadSessionAndFetchRemote()
    }

    /**
     * Initializes UI state from local cached session first, then fetches
     * live profile data from synapse_backend profile.php.
     */
    fun loadSessionAndFetchRemote() {
        viewModelScope.launch {
            val manager = sessionManager ?: SessionManager.getInstanceOrNull()
            val cachedUser = manager?.getSessionUser()

            if (cachedUser != null) {
                applyUserData(
                    id = cachedUser.id,
                    name = cachedUser.name.ifBlank { "Student" },
                    email = cachedUser.email.ifBlank { "student@synapse.ai" },
                    phone = cachedUser.phone ?: "+91 98765-43210",
                    bio = cachedUser.bio ?: "Learning, exploring and building a better version of myself.",
                    course = cachedUser.course ?: "B.Tech Computer Science · Year 3",
                    preferredLanguage = cachedUser.preferredLanguage ?: "English",
                    studyGoal = cachedUser.studyGoal ?: "25 min / day",
                    photoUrl = cachedUser.photoUrl
                )
            }

            // Fetch live student profile from MySQL via profile.php
            fetchRemoteProfile(
                userId = cachedUser?.id?.takeIf { it > 0 },
                email = cachedUser?.email?.ifBlank { null }
            )
        }
    }

    /**
     * Fetches real-time student profile from PHP backend.
     */
    fun fetchRemoteProfile(userId: Int? = null, email: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.getProfile(userId = userId, email = email)
            result.onSuccess { profile ->
                applyRemoteProfile(profile)
                _uiState.update { it.copy(isLoading = false, errorMessage = null) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Failed to load profile."
                    )
                }
            }
        }
    }

    /**
     * Updates profile details (name, phone, bio, email, photo) on the backend via profile.php.
     */
    fun updateProfile(
        name: String,
        email: String,
        phone: String? = null,
        bio: String? = null,
        photoUri: String? = null,
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null, successMessage = null) }

            val currentId = _uiState.value.userId.takeIf { it > 0 }
            val request = ProfileUpdateRequest(
                userId = currentId,
                email = email.trim(),
                name = name.trim(),
                phone = phone?.trim(),
                bio = bio?.trim()
            )

            val result = repository.updateProfile(request)
            result.onSuccess { updated ->
                applyRemoteProfile(updated)
                // If local photo picked, prioritize local photo uri
                if (!photoUri.isNullOrBlank()) {
                    _uiState.update { it.copy(profilePhotoUri = photoUri) }
                }
                _uiState.update {
                    it.copy(
                        isUpdating = false,
                        successMessage = "Profile updated successfully.",
                        errorMessage = null
                    )
                }
                onComplete?.invoke(true)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isUpdating = false,
                        errorMessage = error.localizedMessage ?: "Failed to save profile changes."
                    )
                }
                onComplete?.invoke(false)
            }
        }
    }

    /**
     * Updates notification preference in UI and syncs with profile.php.
     */
    fun onToggleNotifications(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }

        viewModelScope.launch {
            val currentId = _uiState.value.userId.takeIf { it > 0 }
            val request = ProfileUpdateRequest(
                userId = currentId,
                email = _uiState.value.userEmail.takeIf { it.isNotBlank() },
                notificationsEnabled = enabled
            )
            repository.updateProfile(request)
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    /**
     * Clears persistent session and triggers navigation callback to Login.
     */
    fun logout(onLoggedOut: () -> Unit = {}) {
        viewModelScope.launch {
            val manager = sessionManager ?: SessionManager.getInstanceOrNull()
            manager?.clearSession()
            onLoggedOut()
        }
    }

    /**
     * Removes only the user's profile picture / photo and updates state.
     */
    fun removeProfilePicture(onComplete: (() -> Unit)? = null) {
        _uiState.update { it.copy(profilePhotoUri = null) }
        viewModelScope.launch {
            val currentId = _uiState.value.userId.takeIf { it > 0 }
            val request = ProfileUpdateRequest(
                userId = currentId,
                email = _uiState.value.userEmail.takeIf { it.isNotBlank() },
                name = _uiState.value.userName,
                phone = _uiState.value.userPhone,
                bio = _uiState.value.userBio
            )
            repository.updateProfile(request)
            _uiState.update {
                it.copy(
                    profilePhotoUri = null,
                    successMessage = "Profile picture removed successfully.",
                    errorMessage = null
                )
            }
            onComplete?.invoke()
        }
    }

    private fun applyRemoteProfile(profile: UserProfileDto) {
        val initials = computeInitials(profile.name)
        _uiState.update { current ->
            current.copy(
                userId = profile.id,
                userName = profile.name.ifBlank { current.userName },
                userInitials = initials,
                userEmail = profile.email.ifBlank { current.userEmail },
                userPhone = profile.phone ?: current.userPhone,
                userBio = profile.bio ?: current.userBio,
                userDegree = profile.course ?: current.userDegree,
                userStreak = profile.userStreak ?: current.userStreak,
                achievementsCount = profile.achievementsCount ?: current.achievementsCount,
                studyGoalText = profile.studyGoal ?: current.studyGoalText,
                languageText = profile.preferredLanguage ?: current.languageText,
                interests = profile.interests,
                notificationsEnabled = profile.notificationsEnabled,
                profilePhotoUri = profile.photoUrl ?: profile.profilePhoto ?: current.profilePhotoUri
            )
        }
    }

    private fun applyUserData(
        id: Int,
        name: String,
        email: String,
        phone: String,
        bio: String,
        course: String,
        preferredLanguage: String,
        studyGoal: String,
        photoUrl: String?
    ) {
        val initials = computeInitials(name)
        _uiState.update { current ->
            current.copy(
                userId = id,
                userName = name,
                userInitials = initials,
                userEmail = email,
                userPhone = phone,
                userBio = bio,
                userDegree = course,
                languageText = preferredLanguage,
                studyGoalText = studyGoal,
                profilePhotoUri = photoUrl ?: current.profilePhotoUri
            )
        }
    }

    private fun computeInitials(name: String): String {
        return name.trim().split(" ").filter { it.isNotEmpty() }.let { parts ->
            if (parts.size >= 2) {
                "${parts.first().first()}${parts.last().first()}".uppercase()
            } else if (parts.isNotEmpty() && parts.first().isNotEmpty()) {
                parts.first().take(2).uppercase()
            } else {
                "S"
            }
        }
    }
}
