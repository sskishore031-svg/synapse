package com.siamts.synapse.ui.roadmap

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.BaseCurriculumData
import com.siamts.synapse.data.local.LessonProgressEntity
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.local.SynapseDatabase
import com.siamts.synapse.data.local.TrackMapping
import com.siamts.synapse.data.model.SubjectChaptersData
import com.siamts.synapse.data.repository.ChaptersRepository
import com.siamts.synapse.data.repository.ChaptersRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing Learning Roadmap state dynamically with real-time Room DB
 * reactive progress observation and sequential chapter unlocking across all 8 tracks.
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

    private var progressJob: Job? = null
    private var lastLoadedChaptersData: SubjectChaptersData? = null
    private var lastTargetId: Int = 1
    private var lastNormTrack: String = TrackMapping.TRACK_PYTHON

    init {
        val navSubjectId = savedStateHandle?.get<String>("subjectId")
            ?: savedStateHandle?.get<String>("subject_id")
            ?: savedStateHandle?.get<Int>("subjectId")?.toString()
        if (!navSubjectId.isNullOrBlank()) {
            loadSubjectRoadmap(navSubjectId)
        }
    }

    fun loadSubjectRoadmap(subjectIdentifier: String) {
        val normTrack = TrackMapping.normalizeTrackId(subjectIdentifier)
        val numericId = BaseCurriculumData.resolveSubjectNumericId(normTrack)
        loadSubjectRoadmap(subjectId = numericId, subjectSlug = normTrack)
    }

    fun loadSubjectRoadmap(subjectId: Int? = null, subjectSlug: String? = null) {
        val normTrack = TrackMapping.normalizeTrackId(subjectSlug.orEmpty().ifBlank { subjectId?.toString().orEmpty() })
        val targetId = subjectId ?: BaseCurriculumData.resolveSubjectNumericId(normTrack)

        lastNormTrack = normTrack
        lastTargetId = targetId

        // 1. Immediately apply local curriculum data for instant offline display
        val localData = BaseCurriculumData.getSubjectChapters(targetId)
        if (localData?.data != null) {
            lastLoadedChaptersData = localData.data
            applyChaptersWithProgress(targetId, normTrack, localData.data)
        }

        // 2. Start reactive Room DB observation for this user and track
        startProgressObservation(targetId, normTrack)

        // 3. Background sync with backend
        scope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val result = chaptersRepository.getSubjectChapters(
                subjectId = targetId,
                subjectSlug = normTrack,
                userId = userId,
                email = userEmail
            )

            val response = result.getOrNull()
            if (response?.data != null && response.data.chapters.isNotEmpty()) {
                lastLoadedChaptersData = response.data
                val dao = try {
                    SynapseDatabase.getLessonProgressDao()
                } catch (_: Throwable) {
                    null
                }
                val userIdStr = userId.toString()
                val freshCached = if (dao != null) {
                    try {
                        (dao.getProgressForUser(userIdStr) + dao.getProgressForUser("1")).distinctBy { "${it.trackId}_${it.chapterIndex}" }
                    } catch (_: Throwable) {
                        emptyList()
                    }
                } else emptyList()
                applyChaptersWithProgress(targetId, normTrack, response.data, freshCached)
            }
        }
    }

    private fun startProgressObservation(targetId: Int, normTrack: String) {
        progressJob?.cancel()
        progressJob = scope.launch {
            val user = sessionManager?.getSessionUser()
            val userIdStr = (user?.id ?: 1).toString()
            val dao = try {
                SynapseDatabase.getLessonProgressDao()
            } catch (_: Throwable) {
                null
            } ?: return@launch

            // Immediate synchronous check from local database/memory
            val cachedProgress = try {
                (dao.getProgressForUser(userIdStr) + dao.getProgressForUser("1"))
            } catch (_: Throwable) {
                emptyList()
            }
            lastLoadedChaptersData?.let { data ->
                applyChaptersWithProgress(targetId, normTrack, data, cachedProgress)
            }

            // Real-time reactive Flow observation
            dao.observeProgressForUser(userIdStr).collect { progressList ->
                lastLoadedChaptersData?.let { data ->
                    applyChaptersWithProgress(targetId, normTrack, data, progressList)
                }
            }
        }
    }

    private fun applyChaptersWithProgress(
        targetId: Int,
        normTrack: String,
        data: SubjectChaptersData,
        progressList: List<LessonProgressEntity>? = null
    ) {
        val subject = data.subject
        val chapters = data.chapters

        // Build lookup maps for fast matching by trackId_chapterIndex and canonical lessonId
        val progressByTrackAndIndex = progressList?.associateBy {
            "${TrackMapping.normalizeTrackId(it.trackId)}_${it.chapterIndex}"
        }.orEmpty()
        val progressByLessonId = progressList?.associateBy { it.lessonId }.orEmpty()

        var completedCount = 0
        var activeChapId = 0
        var activeChapTitle = ""
        var isPreviousChapterDone = true // Chapter 1 (index 0) is ALWAYS unlocked

        val milestones = chapters.mapIndexed { idx, chap ->
            val chapIndex = idx + 1 // 1-based chapter index (1..5)
            val chapIntId = if (chap.chapterId > 0) chap.chapterId else TrackMapping.canonicalLessonNumericId(normTrack, chapIndex)
            val canonNumericId = TrackMapping.canonicalLessonNumericId(normTrack, chapIndex).toString()

            // Lookup progress strictly using unified key (normTrack, chapIndex)
            val prog = progressByTrackAndIndex["${normTrack}_$chapIndex"]
                ?: progressByLessonId[canonNumericId]
                ?: progressByLessonId[chapIntId.toString()]
                ?: progressByLessonId[chap.id]

            // Two-stage chapter completion check:
            // isDone = concept completed (Mark as Done) OR quiz completed
            // isFullyDone = both concept AND quiz completed
            val cDone = prog?.conceptDone == true || prog?.lessonCompleted == true
            val qDone = prog?.quizDone == true || prog?.quizCompleted == true
            val isDone = cDone || qDone || prog?.isCompleted == true || chap.isCompleted
            val isFullyDone = (cDone && qDone)

            // All 40 chapters are 100% unlocked across all tracks
            val status = if (isDone) {
                completedCount++
                MilestoneStatus.COMPLETED
            } else if (activeChapId == 0) {
                activeChapId = chapIntId
                activeChapTitle = chap.title
                MilestoneStatus.ACTIVE
            } else {
                MilestoneStatus.LOCKED
            }

            val sub = if (isDone) {
                "✓ Completed · ${chap.estimatedMinutes} min"
            } else if (status == MilestoneStatus.ACTIVE) {
                "Start / Continue · ${chap.estimatedMinutes} min"
            } else {
                "Chapter $chapIndex · ${chap.estimatedMinutes} min"
            }

            Milestone(
                id = chapIntId.toString(),
                title = chap.title,
                subtitle = sub,
                status = status,
                chapterId = chapIntId
            )
        }

        // If all chapters are completed, allow re-practicing starting from chapter 1
        if (activeChapId == 0 && chapters.isNotEmpty()) {
            val first = chapters.first()
            activeChapId = if (first.chapterId > 0) first.chapterId else (first.id.toIntOrNull() ?: TrackMapping.canonicalLessonNumericId(normTrack, 1))
            activeChapTitle = first.title
        }

        // Two-stage progress percentage matching LearnScreen (+10% per stage)
        val totalCredits = chapters.mapIndexed { idx, chap ->
            val chapIndex = idx + 1
            val chapIntId = if (chap.chapterId > 0) chap.chapterId else TrackMapping.canonicalLessonNumericId(normTrack, chapIndex)
            val canonNumericId = TrackMapping.canonicalLessonNumericId(normTrack, chapIndex).toString()
            val prog = progressByTrackAndIndex["${normTrack}_$chapIndex"]
                ?: progressByLessonId[canonNumericId]
                ?: progressByLessonId[chapIntId.toString()]
                ?: progressByLessonId[chap.id]
            val cDone = prog?.conceptDone == true || prog?.lessonCompleted == true
            val qDone = prog?.quizDone == true || prog?.quizCompleted == true
            (if (cDone) 0.5 else 0.0) + (if (qDone) 0.5 else 0.0)
        }.sum()

        val total = chapters.size.coerceAtLeast(1)
        val percent = ((totalCredits / total.toDouble()) * 100.0).toInt().coerceIn(0, 100)
        val sName = subject?.name?.takeIf { it.isNotBlank() }
            ?: BaseCurriculumData.getSubjectById(targetId.toString())?.name
            ?: TrackMapping.getTrackTitle(normTrack)
        val sIcon = subject?.iconSymbol?.takeIf { it.isNotBlank() }
            ?: BaseCurriculumData.getSubjectById(targetId.toString())?.iconSymbol
            ?: "📚"

        _uiState.update {
            it.copy(
                isLoading = false,
                subjectId = subject?.id ?: targetId,
                subjectIcon = sIcon,
                subjectName = sName,
                title = "$sName Roadmap",
                progressPercentage = percent,
                milestoneSummary = "$completedCount of $total milestones completed · $percent%",
                milestones = milestones,
                aiRecommendation = "Focus on mastering each chapter's core concepts before attempting quizzes.",
                activeLessonTitle = activeChapTitle,
                activeChapterId = activeChapId
            )
        }
    }

    fun loadArtificialIntelligenceRoadmap() = loadSubjectRoadmap(subjectId = 103)
    fun loadMathematicsRoadmap() = loadSubjectRoadmap(subjectId = 101)
    fun loadComputerScienceRoadmap() = loadSubjectRoadmap(subjectId = 102)
    fun loadDataScienceRoadmap() = loadSubjectRoadmap(subjectId = 105)
    fun loadPhysicsRoadmap() = loadSubjectRoadmap(subjectId = 107)
    fun loadChemistryRoadmap() = loadSubjectRoadmap(subjectId = 108)

    fun onMilestoneClicked(milestoneId: String) {}
    fun onContinueLessonClicked() {}
}
