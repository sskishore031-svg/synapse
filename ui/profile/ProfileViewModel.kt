package com.siamts.synapse.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.SynapseApplication
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.model.ProfileUpdateRequest
import com.siamts.synapse.data.model.UserProfileDto
import com.siamts.synapse.data.remote.RetrofitClient
import com.siamts.synapse.data.repository.ProfileRepository
import com.siamts.synapse.data.repository.ProfileRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

/**
 * Clean MVVM ViewModel managing Profile & Settings state, profile photo upload via upload_profile.php,
 * remote sync via profile.php, and user logout lifecycle.
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
     * Updates profile details (name, phone, bio, email) and uploads new profile photo
     * to upload_profile.php if a local photo was selected or removed.
     */
    fun updateProfile(
        name: String,
        email: String,
        phone: String? = null,
        bio: String? = null,
        photoUri: String? = null,
        removePhoto: Boolean = false,
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val isLocalPhoto = !photoUri.isNullOrBlank() &&
                    (photoUri.startsWith("content://", ignoreCase = true) ||
                     photoUri.startsWith("file://", ignoreCase = true) ||
                     !photoUri.startsWith("http", ignoreCase = true))

            val shouldUploadMultipart = isLocalPhoto || removePhoto

            _uiState.update {
                it.copy(
                    isUpdating = true,
                    isUploadingPhoto = shouldUploadMultipart,
                    errorMessage = null,
                    successMessage = null
                )
            }

            val currentId = _uiState.value.userId.takeIf { it > 0 }
            val photoFile: File? = if (isLocalPhoto) {
                resolveFileFromUri(photoUri!!)
            } else {
                null
            }

            val result = if (photoFile != null || removePhoto) {
                // Upload via multipart/form-data to upload_profile.php
                repository.uploadProfilePhoto(
                    userId = currentId,
                    email = email.trim(),
                    photoFile = photoFile,
                    name = name.trim(),
                    phone = phone?.trim(),
                    bio = bio?.trim(),
                    removePhoto = removePhoto
                )
            } else {
                // Standard JSON update to profile.php
                val request = ProfileUpdateRequest(
                    userId = currentId,
                    email = email.trim(),
                    name = name.trim(),
                    phone = phone?.trim(),
                    bio = bio?.trim()
                )
                repository.updateProfile(request)
            }

            result.onSuccess { updated ->
                applyRemoteProfile(updated)
                _uiState.update {
                    it.copy(
                        isUpdating = false,
                        isUploadingPhoto = false,
                        successMessage = "Profile updated successfully.",
                        errorMessage = null
                    )
                }
                onComplete?.invoke(true)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isUpdating = false,
                        isUploadingPhoto = false,
                        errorMessage = error.localizedMessage ?: "Failed to save profile changes."
                    )
                }
                onComplete?.invoke(false)
            }
        }
    }

    /**
     * Backward compatibility overload for updateProfile without removePhoto flag.
     */
    fun updateProfile(
        name: String,
        email: String,
        phone: String? = null,
        bio: String? = null,
        photoUri: String? = null,
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        updateProfile(
            name = name,
            email = email,
            phone = phone,
            bio = bio,
            photoUri = photoUri,
            removePhoto = false,
            onComplete = onComplete
        )
    }

    /**
     * Removes the user's profile photo via upload_profile.php and clears state.
     */
    fun removeProfilePicture(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, isUploadingPhoto = true, errorMessage = null) }
            val currentId = _uiState.value.userId.takeIf { it > 0 }
            val result = repository.uploadProfilePhoto(
                userId = currentId,
                email = _uiState.value.userEmail.takeIf { it.isNotBlank() },
                photoFile = null,
                name = _uiState.value.userName,
                phone = _uiState.value.userPhone,
                bio = _uiState.value.userBio,
                removePhoto = true
            )
            result.onSuccess { updated ->
                applyRemoteProfile(updated)
                _uiState.update {
                    it.copy(
                        profilePhotoUri = null,
                        isUpdating = false,
                        isUploadingPhoto = false,
                        successMessage = "Profile picture removed successfully.",
                        errorMessage = null
                    )
                }
                onComplete?.invoke()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isUpdating = false,
                        isUploadingPhoto = false,
                        errorMessage = error.localizedMessage ?: "Failed to remove profile photo."
                    )
                }
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

    fun updateStudyGoal(goal: String) {
        _uiState.update { it.copy(studyGoalText = goal) }
        viewModelScope.launch {
            val currentId = _uiState.value.userId.takeIf { it > 0 }
            val request = ProfileUpdateRequest(
                userId = currentId,
                email = _uiState.value.userEmail.takeIf { it.isNotBlank() },
                studyGoal = goal
            )
            repository.updateProfile(request)
        }
    }

    fun updatePreferredLanguage(language: String) {
        _uiState.update { it.copy(languageText = language) }
        viewModelScope.launch {
            val currentId = _uiState.value.userId.takeIf { it > 0 }
            val request = ProfileUpdateRequest(
                userId = currentId,
                email = _uiState.value.userEmail.takeIf { it.isNotBlank() },
                preferredLanguage = language
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

    private fun resolveFileFromUri(uriString: String): File? {
        return try {
            if (uriString.startsWith("file://", ignoreCase = true)) {
                val path = java.net.URI(uriString).path
                val file = File(path)
                if (file.exists() && file.isFile) return file
            }
            val rawFile = File(uriString)
            if (rawFile.exists() && rawFile.isFile) {
                return rawFile
            }
            if (uriString.startsWith("content://", ignoreCase = true)) {
                val context = SynapseApplication.getAppContext()
                val uri = Uri.parse(uriString)
                val mime = context.contentResolver.getType(uri) ?: "image/jpeg"
                val ext = when {
                    mime.contains("png", true) -> ".png"
                    mime.contains("webp", true) -> ".webp"
                    else -> ".jpg"
                }
                val tempFile = File(context.cacheDir, "avatar_upload_${System.currentTimeMillis()}$ext")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                if (tempFile.exists() && tempFile.length() > 0) return tempFile
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    private fun applyRemoteProfile(profile: UserProfileDto) {
        val initials = computeInitials(profile.name)
        val formattedPhoto = RetrofitClient.formatPhotoUrl(profile.photoUrl ?: profile.profilePhoto)
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
                profilePhotoUri = formattedPhoto
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
        val formattedPhoto = RetrofitClient.formatPhotoUrl(photoUrl)
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
                profilePhotoUri = formattedPhoto ?: current.profilePhotoUri
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
