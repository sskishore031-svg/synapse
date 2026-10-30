package com.siamts.synapse.ui.roadmap

enum class MilestoneStatus {
    COMPLETED,
    ACTIVE,
    LOCKED
}

data class Milestone(
    val id: String,
    val title: String,
    val subtitle: String,
    val status: MilestoneStatus,
    val chapterId: Int = 0
)

/**
 * UI State for the Your Learning Roadmap Screen following Clean MVVM architecture.
 */
data class RoadmapUiState(
    val headerTag: String = "A PATH THAT EVOLVES WITH YOU",
    val title: String = "Your Learning Roadmap",
    val subjectId: Int = 4, // Default AI
    val subjectIcon: String = "🧠",
    val subjectName: String = "Artificial Intelligence",
    val progressPercentage: Int = 0,
    val milestoneSummary: String = "Loading roadmap...",
    val milestones: List<Milestone> = emptyList(),
    val aiRecommendation: String = "Complete chapters in sequence to build deep comprehension.",
    val activeLessonTitle: String = "",
    val activeChapterId: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
