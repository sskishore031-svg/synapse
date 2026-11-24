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
        observeSessionChanges()
    }

    private fun observeSessionChanges() {
        viewModelScope.launch {
            val manager = sessionManager ?: SessionManager.getInstanceOrNull()
            manager?.sessionStateFlow?.collect { sessionState ->
                if (sessionState is com.siamts.synapse.data.local.AuthSessionState.Authenticated) {
                    val user = sessionState.user
                    applyUserData(
                        id = user.id,
                        name = user.name.ifBlank { "Student" },
                        email = user.email.ifBlank { "student@synapse.ai" },
                        phone = user.phone ?: "+91 98765-43210",
                        bio = user.bio ?: "Learning, exploring and building a better version of myself.",
                        course = user.course ?: "B.Tech Computer Science · Year 3",
                        preferredLanguage = user.preferredLanguage ?: "English",
                        studyGoal = user.studyGoal ?: "25 min / day",
                        photoUrl = user.photoUrl
                    )
                }
            }
        }
    }

    /**
     * Initializes UI state from local cached session first, then fetches
     * live profile data from synapse_backend profile.php if session exists.
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

                // Only fetch remote profile if we have a concrete user ID or email
                if (cachedUser.id > 0 || cachedUser.email.isNotBlank()) {
                    fetchRemoteProfile(
                        userId = cachedUser.id.takeIf { it > 0 },
                        email = cachedUser.email.ifBlank { null }
                    )
                }
            }
        }
    }

    /**
     * Fetches real-time student profile from PHP backend.
     */
    fun fetchRemoteProfile(userId: Int? = null, email: String? = null) {
        if (userId == null && email.isNullOrBlank()) {
            return
        }
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

            if (removePhoto) {
                sessionManager?.updateUserPhoto(null)
            } else if (!photoUri.isNullOrBlank()) {
                sessionManager?.updateUserPhoto(photoUri)
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
                if (!photoUri.isNullOrBlank() && !removePhoto) {
                    _uiState.update { it.copy(profilePhotoUri = photoUri) }
                } else if (removePhoto) {
                    _uiState.update { it.copy(profilePhotoUri = null) }
                }
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
     * Updates notification preference in UI, persists locally, schedules reminders, and syncs with profile.php.
     */
    fun onToggleNotifications(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }

        viewModelScope.launch {
            try {
                sessionManager?.setNotificationsEnabled(enabled)
                val appContext = SynapseApplication.getAppContext()
                if (enabled) {
                    com.siamts.synapse.notification.SynapseNotificationHelper.scheduleStudyReminders(appContext)
                } else {
                    com.siamts.synapse.notification.SynapseNotificationHelper.cancelStudyReminders(appContext)
                }
            } catch (_: Throwable) {}

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
            _uiState.update { ProfileUiState() }
            onLoggedOut()
        }
    }

    private fun resolveFileFromUri(uriString: String): File? {
        return try {
            val context = SynapseApplication.getAppContext()
            val avatarsDir = File(context.filesDir, "avatars").apply { if (!exists()) mkdirs() }
            val userId = _uiState.value.userId.takeIf { it > 0 } ?: 1
            val permanentAvatar = File(avatarsDir, "avatar_$userId.jpg")

            if (uriString.startsWith("file://", ignoreCase = true)) {
                val path = java.net.URI(uriString).path
                val file = File(path)
                if (file.exists() && file.isFile) {
                    file.copyTo(permanentAvatar, overwrite = true)
                    return permanentAvatar
                }
            }
            val rawFile = File(uriString)
            if (rawFile.exists() && rawFile.isFile) {
                rawFile.copyTo(permanentAvatar, overwrite = true)
                return permanentAvatar
            }
            if (uriString.startsWith("content://", ignoreCase = true)) {
                val uri = Uri.parse(uriString)
                context.contentResolver.openInputStream(uri)?.use { input ->
                    permanentAvatar.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                if (permanentAvatar.exists() && permanentAvatar.length() > 0) {
                    return permanentAvatar
                }
            }
            if (permanentAvatar.exists() && permanentAvatar.length() > 0) {
                return permanentAvatar
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    private fun applyRemoteProfile(profile: UserProfileDto) {
        val effectiveName = profile.name.ifBlank { _uiState.value.userName }
        val initials = computeInitials(effectiveName)
        val effectivePhoto = profile.photoUrl ?: profile.profilePhoto ?: profile.avatarUrl
        val formattedPhoto = RetrofitClient.formatPhotoUrl(effectivePhoto)
        if (!formattedPhoto.isNullOrBlank()) {
            viewModelScope.launch {
                sessionManager?.updateUserPhoto(formattedPhoto)
            }
        }
        _uiState.update { current ->
            current.copy(
                userId = if (profile.id > 0) profile.id else current.userId,
                userName = effectiveName,
                userInitials = initials,
                userEmail = profile.email.ifBlank { current.userEmail },
                userPhone = profile.phone ?: current.userPhone,
                userBio = profile.bio ?: current.userBio,
                userDegree = profile.course ?: current.userDegree,
                userStreak = profile.userStreak ?: current.userStreak,
                achievementsCount = profile.achievementsCount ?: current.achievementsCount,
                studyGoalText = profile.studyGoal ?: current.studyGoalText,
                languageText = profile.preferredLanguage ?: current.languageText,
                interests = if (profile.interests.isNotEmpty()) profile.interests else current.interests,
                notificationsEnabled = profile.notificationsEnabled,
                profilePhotoUri = formattedPhoto ?: current.profilePhotoUri
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
        val effectiveName = name.ifBlank { "Student" }
        val initials = computeInitials(effectiveName)
        val formattedPhoto = RetrofitClient.formatPhotoUrl(photoUrl)
        val localAvatarFile = File(SynapseApplication.getAppContext().filesDir, "avatars/avatar_$id.jpg")
        _uiState.update { current ->
            val resolvedPhoto = formattedPhoto
                ?: (if (localAvatarFile.exists() && localAvatarFile.length() > 0) localAvatarFile.toURI().toString() else null)
                ?: current.profilePhotoUri
            current.copy(
                userId = id,
                userName = effectiveName,
                userInitials = initials,
                userEmail = email.ifBlank { current.userEmail },
                userPhone = phone,
                userBio = bio,
                userDegree = course,
                languageText = preferredLanguage,
                studyGoalText = studyGoal,
                profilePhotoUri = resolvedPhoto
            )
        }
    }

    private fun computeInitials(name: String): String {
        val parts = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        return when {
            parts.size >= 2 -> "${parts.first().first()}${parts.last().first()}".uppercase()
            parts.isNotEmpty() && parts.first().length >= 2 -> parts.first().take(2).uppercase()
            parts.isNotEmpty() && parts.first().isNotEmpty() -> parts.first().take(1).uppercase()
            else -> "ST"
        }
    }
}
