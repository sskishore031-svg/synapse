package com.siamts.synapse.ui.roadmap

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.BaseCurriculumData
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.ChaptersRepository
import com.siamts.synapse.data.repository.ChaptersRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing Learning Roadmap state dynamically with backend integration.
 */
class RoadmapViewModel(
    private val savedStateHandle: SavedStateHandle? = null,
    private val chaptersRepository: ChaptersRepository = ChaptersRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull(),
    private val externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope: CoroutineScope get() = externalScope ?: viewModelScope

    private val _uiState = MutableStateFlow(RoadmapUiState())
    val uiState: StateFlow<RoadmapUiState> = _uiState.asStateFlow()

    init {
        val navSubjectId = savedStateHandle?.get<String>("subjectId")
            ?: savedStateHandle?.get<String>("subject_id")
            ?: savedStateHandle?.get<Int>("subjectId")?.toString()
        if (!navSubjectId.isNullOrBlank()) {
            loadSubjectRoadmap(navSubjectId)
        }
    }

    fun loadSubjectRoadmap(subjectIdentifier: String) {
        val numericId = BaseCurriculumData.resolveSubjectNumericId(subjectIdentifier)
        loadSubjectRoadmap(subjectId = numericId, subjectSlug = subjectIdentifier)
    }

    fun loadSubjectRoadmap(subjectId: Int? = null, subjectSlug: String? = null) {
        val targetId = subjectId
            ?: subjectSlug?.let { BaseCurriculumData.resolveSubjectNumericId(it) }
            ?: 1

        // 1. Immediately apply local curriculum data synchronously for instant offline display
        val localData = BaseCurriculumData.getSubjectChapters(targetId)
        if (localData?.data != null) {
            applyChaptersData(targetId, localData.data)
        }

        // 2. Background sync with backend
        scope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val result = chaptersRepository.getSubjectChapters(
                subjectId = targetId,
                subjectSlug = subjectSlug,
                userId = userId,
                email = userEmail
            )

            val response = result.getOrNull()
            if (response?.data != null) {
                applyChaptersData(targetId, response.data)
            }
        }
    }

    private fun applyChaptersData(targetId: Int, data: com.siamts.synapse.data.model.SubjectChaptersData) {
        val subject = data.subject
        val chapters = data.chapters

        var completedCount = 0
        var activeChapId = 0
        var activeChapTitle = ""

        val milestones = chapters.mapIndexed { idx, chap ->
            val chapIntId = if (chap.chapterId > 0) chap.chapterId else (chap.id.toIntOrNull() ?: 1)
            val status = when {
                chap.isCompleted -> {
                    completedCount++
                    MilestoneStatus.COMPLETED
                }
                chap.isUnlocked || idx == 0 -> {
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
        val sName = subject?.name ?: BaseCurriculumData.getSubjectById(targetId.toString())?.name ?: "Mathematics"
        val sIcon = subject?.iconSymbol ?: BaseCurriculumData.getSubjectById(targetId.toString())?.iconSymbol ?: "∑"

        _uiState.update {
            it.copy(
                isLoading = false,
                subjectId = subject?.id ?: targetId,
                subjectIcon = sIcon,
                subjectName = sName,
                title = "$sName Roadmap",
                progressPercentage = percent,
                milestoneSummary = "$completedCount of $total milestones completed · Beginner path",
                milestones = milestones,
                aiRecommendation = "Focus on mastering each chapter's core concepts before attempting quizzes.",
                activeLessonTitle = activeChapTitle,
                activeChapterId = activeChapId
            )
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
