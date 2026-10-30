package com.siamts.synapse.ui.roadmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.ChaptersRepository
import com.siamts.synapse.data.repository.ChaptersRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing Learning Roadmap state dynamically with backend integration.
 */
class RoadmapViewModel(
    private val chaptersRepository: ChaptersRepository = ChaptersRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoadmapUiState())
    val uiState: StateFlow<RoadmapUiState> = _uiState.asStateFlow()

    fun loadSubjectRoadmap(subjectId: Int? = null, subjectSlug: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val result = chaptersRepository.getSubjectChapters(
                subjectId = subjectId,
                subjectSlug = subjectSlug,
                userId = userId,
                email = userEmail
            )

            result.onSuccess { response ->
                val data = response.data
                val subject = data?.subject
                val chapters = data?.chapters.orEmpty()

                var completedCount = 0
                var activeChapId = 0
                var activeChapTitle = ""

                val milestones = chapters.map { chap ->
                    val chapIntId = if (chap.chapterId > 0) chap.chapterId else (chap.id.toIntOrNull() ?: 1)
                    val status = when {
                        chap.isCompleted -> {
                            completedCount++
                            MilestoneStatus.COMPLETED
                        }
                        chap.isUnlocked -> {
                            if (activeChapId == 0) {
                                activeChapId = chapIntId
                                activeChapTitle = chap.title
                            }
                            MilestoneStatus.ACTIVE
                        }
                        else -> MilestoneStatus.LOCKED
                    }

                    val sub = when (status) {
                        MilestoneStatus.COMPLETED -> "Completed · ${chap.estimatedMinutes} min"
                        MilestoneStatus.ACTIVE -> "Continue learning · ${chap.estimatedMinutes} min"
                        MilestoneStatus.LOCKED -> "Locked · Complete previous chapter"
                    }

                    Milestone(
                        id = chapIntId.toString(),
                        title = chap.title,
                        subtitle = sub,
                        status = status,
                        chapterId = chapIntId
                    )
                }

                if (activeChapId == 0 && chapters.isNotEmpty()) {
                    val first = chapters.first()
                    activeChapId = if (first.chapterId > 0) first.chapterId else (first.id.toIntOrNull() ?: 1)
                    activeChapTitle = first.title
                }

                val total = chapters.size.coerceAtLeast(1)
                val percent = (completedCount * 100) / total

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        subjectId = subject?.id ?: subjectId ?: 4,
                        subjectIcon = subject?.iconSymbol ?: "🧠",
                        subjectName = subject?.name ?: "Artificial Intelligence",
                        progressPercentage = percent,
                        milestoneSummary = "$completedCount of $total milestones completed · Beginner path",
                        milestones = milestones,
                        aiRecommendation = "Focus on mastering each chapter's core concepts before attempting quizzes.",
                        activeLessonTitle = activeChapTitle,
                        activeChapterId = activeChapId
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Failed to load chapters"
                    )
                }
            }
        }
    }

    fun loadArtificialIntelligenceRoadmap() = loadSubjectRoadmap(subjectId = 4)
    fun loadMathematicsRoadmap() = loadSubjectRoadmap(subjectId = 1)
    fun loadComputerScienceRoadmap() = loadSubjectRoadmap(subjectId = 2)
    fun loadDataScienceRoadmap() = loadSubjectRoadmap(subjectId = 3)
    fun loadPhysicsRoadmap() = loadSubjectRoadmap(subjectId = 5)
    fun loadChemistryRoadmap() = loadSubjectRoadmap(subjectId = 6)

    fun onMilestoneClicked(milestoneId: String) {}
    fun onContinueLessonClicked() {}
}
