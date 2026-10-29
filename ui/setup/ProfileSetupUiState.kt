package com.siamts.synapse.ui.setup

/**
 * UI State for the Profile Setup / Personalization Screen ("Make it yours").
 */
data class ProfileSetupUiState(
    val userId: Int = 0,
    val initials: String = "K",
    val name: String = "Kishore",
    val selectedCourse: String = "B.Tech Computer Science · Year 3",
    val preferredLanguage: String = "English",
    val availableInterests: List<String> = listOf(
        "Computer Science",
        "Artificial Intelligence",
        "Mathematics",
        "Data Science",
        "Physics",
        "Chemistry"
    ),
    val selectedInterests: Set<String> = setOf(
        "Computer Science",
        "Artificial Intelligence",
        "Mathematics"
    ),
    val currentStep: Int = 2,
    val totalSteps: Int = 3,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
