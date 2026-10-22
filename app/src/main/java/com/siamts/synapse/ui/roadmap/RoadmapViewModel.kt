package com.siamts.synapse.ui.roadmap

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Learning Roadmap state and milestone selection.
 */
class RoadmapViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RoadmapUiState())
    val uiState: StateFlow<RoadmapUiState> = _uiState.asStateFlow()

    fun loadChemistryRoadmap() {
        _uiState.update {
            it.copy(
                subjectIcon = "🧪",
                subjectName = "Chemistry",
                progressPercentage = 64,
                milestoneSummary = "3 of 5 lessons completed · Beginner path",
                milestones = listOf(
                    Milestone("1", "Atomic Structure", "Completed · 12 min", MilestoneStatus.COMPLETED),
                    Milestone("2", "Periodic Table", "Completed · 15 min", MilestoneStatus.COMPLETED),
                    Milestone("3", "Chemical Bonding", "Continue learning · 18 min", MilestoneStatus.ACTIVE),
                    Milestone("4", "Acids and Bases", "Unlock after this lesson", MilestoneStatus.LOCKED),
                    Milestone("5", "Chemical Reactions", "Unlock after this lesson", MilestoneStatus.LOCKED),
                ),
                aiRecommendation = "Study chemical bonding types and practice naming compounds for better clarity.",
                activeLessonTitle = "Chemical Bonding"
            )
        }
    }

    fun loadPhysicsRoadmap() {
        _uiState.update {
            it.copy(
                subjectIcon = "⚛",
                subjectName = "Physics",
                progressPercentage = 60,
                milestoneSummary = "3 of 5 lessons completed · Beginner path",
                milestones = listOf(
                    Milestone("1", "Motion and Forces", "Completed · 14 min", MilestoneStatus.COMPLETED),
                    Milestone("2", "Work and Energy", "Completed · 16 min", MilestoneStatus.COMPLETED),
                    Milestone("3", "Waves", "Continue learning · 15 min", MilestoneStatus.ACTIVE),
                    Milestone("4", "Electricity", "Unlock after this lesson", MilestoneStatus.LOCKED),
                    Milestone("5", "Magnetism", "Unlock after this lesson", MilestoneStatus.LOCKED),
                ),
                aiRecommendation = "Review wave properties and solve practice problems for better understanding.",
                activeLessonTitle = "Waves"
            )
        }
    }

    fun loadDataScienceRoadmap() {
        _uiState.update {
            it.copy(
                subjectIcon = "📊",
                subjectName = "Data Science",
                progressPercentage = 40,
                milestoneSummary = "2 of 5 lessons completed · Beginner path",
                milestones = listOf(
                    Milestone("1", "Python for Data Analysis", "Completed · 15 min", MilestoneStatus.COMPLETED),
                    Milestone("2", "Data Cleaning", "Completed · 18 min", MilestoneStatus.COMPLETED),
                    Milestone("3", "Data Visualization", "Continue learning · 17 min", MilestoneStatus.ACTIVE),
                    Milestone("4", "Statistics Basics", "Unlock after this lesson", MilestoneStatus.LOCKED),
                    Milestone("5", "Machine Learning Basics", "Unlock after this lesson", MilestoneStatus.LOCKED),
                ),
                aiRecommendation = "Explore data visualization libraries like Matplotlib and Seaborn for better insights.",
                activeLessonTitle = "Data Visualization"
            )
        }
    }

    fun loadComputerScienceRoadmap() {
        _uiState.update {
            it.copy(
                subjectIcon = "</>",
                subjectName = "Computer Science",
                progressPercentage = 50,
                milestoneSummary = "3 of 5 lessons completed · Beginner path",
                milestones = listOf(
                    Milestone("1", "Programming Basics", "Completed · 14 min", MilestoneStatus.COMPLETED),
                    Milestone("2", "Data Structures", "Completed · 18 min", MilestoneStatus.COMPLETED),
                    Milestone("3", "Algorithms", "Continue learning · 20 min", MilestoneStatus.ACTIVE),
                    Milestone("4", "Operating Systems", "Unlock after this lesson", MilestoneStatus.LOCKED),
                    Milestone("5", "Database Systems", "Unlock after this lesson", MilestoneStatus.LOCKED),
                ),
                aiRecommendation = "Learn about sorting and searching algorithms to improve your problem solving skills.",
                activeLessonTitle = "Algorithms"
            )
        }
    }

    fun loadMathematicsRoadmap() {
        _uiState.update {
            it.copy(
                subjectIcon = "🧮",
                subjectName = "Mathematics",
                progressPercentage = 72,
                milestoneSummary = "4 of 5 lessons completed · Beginner path",
                milestones = listOf(
                    Milestone("1", "Basic Algebra", "Completed · 12 min", MilestoneStatus.COMPLETED),
                    Milestone("2", "Linear Equations", "Completed · 15 min", MilestoneStatus.COMPLETED),
                    Milestone("3", "Systems of Equations", "Completed · 18 min", MilestoneStatus.COMPLETED),
                    Milestone("4", "Quadratic Equations", "Continue learning · 16 min", MilestoneStatus.ACTIVE),
                    Milestone("5", "Geometry Basics", "Unlock after this lesson", MilestoneStatus.LOCKED),
                ),
                aiRecommendation = "Practice more problems on quadratic equations to strengthen your concepts.",
                activeLessonTitle = "Quadratic Equations"
            )
        }
    }

    fun onMilestoneClicked(milestoneId: String) {
        // Handle milestone selection
    }

    fun onContinueLessonClicked() {
        // Start/Continue current lesson
    }
}
