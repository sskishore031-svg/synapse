package com.siamts.synapse.ui.profile

/**
 * UI State for the Profile & Settings Screen ("Your learning space").
 */
data class ProfileUiState(
    val headerTag: String = "PROFILE & SETTINGS",
    val title: String = "Your learning space",
    val userName: String = "Arjun Mehta",
    val userInitials: String = "AM",
    val userDegree: String = "B.Tech Computer Science",
    val userStreak: String = "Year 3 · Learning every day",
    val achievementsCount: String = "8 badges",
    val studyGoalText: String = "25 min / day",
    val languageText: String = "English",
    val notificationsEnabled: Boolean = true,
    val appVersionText: String = "Synapse AI · Version 1.0.0",
    val selectedTab: Int = 4,
)
