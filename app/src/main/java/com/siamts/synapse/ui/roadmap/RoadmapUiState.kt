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
    val subjectIcon: String = "🧪",
    val subjectName: String = "Chemistry",
    val progressPercentage: Int = 64,
    val milestoneSummary: String = "3 of 5 lessons completed · Beginner path",
    val milestones: List<Milestone> = listOf(
        Milestone("1", "Atomic Structure", "Completed · 12 min", MilestoneStatus.COMPLETED),
        Milestone("2", "Periodic Table", "Completed · 15 min", MilestoneStatus.COMPLETED),
        Milestone("3", "Chemical Bonding", "Continue learning · 18 min", MilestoneStatus.ACTIVE),
        Milestone("4", "Acids and Bases", "Unlock after this lesson", MilestoneStatus.LOCKED),
        Milestone("5", "Chemical Reactions", "Unlock after this lesson", MilestoneStatus.LOCKED),
    ),
    val aiRecommendation: String = "Study chemical bonding types and practice naming compounds for better clarity.",
    val activeLessonTitle: String = "Chemical Bonding"
)
