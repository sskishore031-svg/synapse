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
)

/**
 * UI State for the Your Learning Roadmap Screen following MVVM architecture.
 */
data class RoadmapUiState(
    val headerTag: String = "A PATH THAT EVOLVES WITH YOU",
    val title: String = "Your Learning Roadmap",
    val subjectIcon: String = "🧠",
    val subjectName: String = "Artificial Intelligence",
    val progressPercentage: Int = 60,
    val milestoneSummary: String = "3 of 5 milestones unlocked · Beginner path",
    val milestones: List<Milestone> = listOf(
        Milestone("1", "Introduction to AI", "Completed · 10 min", MilestoneStatus.COMPLETED),
        Milestone("2", "Neural network basics", "Completed · 15 min", MilestoneStatus.COMPLETED),
        Milestone("3", "Gradient descent", "Continue learning · 12 min", MilestoneStatus.ACTIVE),
        Milestone("4", "Backpropagation", "Unlock after this lesson", MilestoneStatus.LOCKED),
        Milestone("5", "Model evaluation", "Coming up next · 18 min", MilestoneStatus.LOCKED),
    ),
    val aiRecommendation: String = "Review derivatives before backpropagation. A 5-minute refresher will help.",
    val activeLessonTitle: String = "Gradient descent"
)
