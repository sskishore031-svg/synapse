package com.siamts.synapse.ui.profile

/**
 * UI State for the Profile & Settings Screen ("Your learning space").
 */
data class ProfileUiState(
    val headerTag: String = "PROFILE & SETTINGS",
    val title: String = "Your learning space",
    val userId: Int = 0,
    val userName: String = "Kishore",
    val userInitials: String = "K",
    val userEmail: String = "kishore@gmail.com",
    val userPhone: String = "+91 98765-43210",
    val userBio: String = "Learning, exploring and building a better version of myself.",
    val userDegree: String = "B.Tech Computer Science · Year 3",
    val userStreak: String = "Year 3 · Learning every day",
    val achievementsCount: String = "8 badges",
    val studyGoalText: String = "25 min / day",
    val languageText: String = "English",
    val interests: List<String> = emptyList(),
    val notificationsEnabled: Boolean = true,
    val appVersionText: String = "Synapse AI · Version 1.0.0",
    val selectedTab: Int = 4,
    val profilePhotoUri: String? = null,
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
