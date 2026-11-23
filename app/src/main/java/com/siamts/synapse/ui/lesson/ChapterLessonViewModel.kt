package com.siamts.synapse.ui.lesson

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.BaseCurriculumData
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.ChaptersRepository
import com.siamts.synapse.data.repository.ChaptersRepositoryImpl
import com.siamts.synapse.data.repository.NotesRepository
import com.siamts.synapse.data.repository.NotesRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChapterLessonViewModel(
    private val savedStateHandle: SavedStateHandle? = null,
    private val chaptersRepository: ChaptersRepository = ChaptersRepositoryImpl(),
    private val notesRepository: NotesRepository = NotesRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull(),
    private val externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope: CoroutineScope get() = externalScope ?: viewModelScope

    private val _uiState = MutableStateFlow(ChapterLessonUiState())
    val uiState: StateFlow<ChapterLessonUiState> = _uiState.asStateFlow()

    init {
        val navChapterId = savedStateHandle?.get<Int>("chapterId")
            ?: savedStateHandle?.get<String>("chapterId")?.toIntOrNull()
            ?: savedStateHandle?.get<Int>("chapter_id")
            ?: savedStateHandle?.get<String>("chapter_id")?.toIntOrNull()
        val navSubjectId = savedStateHandle?.get<String>("subjectId")
            ?: savedStateHandle?.get<String>("subject_id")
        if (navChapterId != null && navChapterId > 0) {
            loadChapterLesson(navChapterId, navSubjectId)
        }
    }

    fun loadChapterLesson(chapterId: Int, subjectId: String? = null) {
        val resolvedSubject = if (!subjectId.isNullOrBlank()) {
            BaseCurriculumData.getSubjectById(subjectId)
        } else null
        val targetChapterId = if (chapterId > 0) {
            chapterId
        } else {
            resolvedSubject?.chapters?.firstOrNull()?.chapterId ?: 1
        }

        // 1. Immediately apply local curriculum lesson synchronously for instant offline display
        val localLesson = BaseCurriculumData.getChapterLesson(targetChapterId)
            ?: (if (resolvedSubject?.chapters?.isNotEmpty() == true) {
                resolvedSubject.chapters.firstOrNull()?.let { BaseCurriculumData.getChapterLesson(it.chapterId) }
            } else null)

        if (localLesson?.data != null) {
            applyLessonData(targetChapterId, localLesson.data)
        }

        // 2. Background sync with backend
        scope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val norm = com.siamts.synapse.data.local.TrackMapping.normalizeTrackId(resolvedSubject?.name.orEmpty())
            val cId = com.siamts.synapse.data.local.TrackMapping.canonicalLessonId(targetChapterId, norm)
            val isDone = runCatching {
                com.siamts.synapse.data.local.SynapseDatabase.getLessonProgressDao().getProgress(cId, userId.toString())?.isCompleted == true
            }.getOrDefault(false)
            if (isDone) {
                _uiState.update { it.copy(isCompleted = true) }
            }

            val result = chaptersRepository.getChapterLesson(
                chapterId = targetChapterId,
                userId = userId,
                email = userEmail
            )

            val response = result.getOrNull()
            if (response?.data != null && response.data.chapter?.part1?.conceptText?.isNotBlank() == true) {
                applyLessonData(targetChapterId, response.data)
            }
        }
    }

    private fun applyLessonData(targetChapterId: Int, data: com.siamts.synapse.data.model.ChapterLessonData) {
        val chap = data.chapter
        val sub = data.subject

        val p1 = chap?.part1
        val p2 = chap?.part2

        val syllabusPoints = p1?.keyPoints.orEmpty().ifEmpty {
            BaseCurriculumData.getChapterById(targetChapterId)?.syllabus.orEmpty()
        }
        val formulaPoints = p2?.keyPoints.orEmpty().ifEmpty {
            BaseCurriculumData.getChapterById(targetChapterId)?.coreFormulas.orEmpty()
        }

        val p1Title = p1?.headline?.ifBlank { "${chap?.title ?: "Chapter"} Fundamentals" }
            ?: "${chap?.title ?: "Chapter"} Fundamentals"

        val p1Content = p1?.conceptText?.ifBlank {
            syllabusPoints.joinToString(separator = "\n\n") { "• $it" }
                .ifBlank { "Core concepts and definitions for ${chap?.title ?: "this lesson"}." }
        } ?: syllabusPoints.joinToString(separator = "\n\n") { "• $it" }
            .ifBlank { "Core concepts and definitions for ${chap?.title ?: "this lesson"}." }

        val p2Title = p2?.headline?.ifBlank { "${chap?.title ?: "Chapter"} Applications & Formulas" }
            ?: "${chap?.title ?: "Chapter"} Applications & Formulas"

        val p2Content = p2?.conceptText?.ifBlank {
            formulaPoints.joinToString(separator = "\n\n") { "• $it" }
                .ifBlank { "Worked practical examples, analytical derivations, and problem solving steps." }
        } ?: formulaPoints.joinToString(separator = "\n\n") { "• $it" }
            .ifBlank { "Worked practical examples, analytical derivations, and problem solving steps." }

        val resolvedCid = chap?.id ?: targetChapterId
        val resolvedSubName = sub?.name.orEmpty().ifEmpty {
            BaseCurriculumData.getSubjectById((sub?.id ?: 1).toString())?.name
                ?: BaseCurriculumData.subjects.firstOrNull { s -> s.chapters.any { it.chapterId == targetChapterId } }?.name
                ?: "Curriculum Subject"
        }
        val normTrack = com.siamts.synapse.data.local.TrackMapping.normalizeTrackId(resolvedSubName)
        val canonId = com.siamts.synapse.data.local.TrackMapping.canonicalLessonId(resolvedCid, normTrack)
        val isAlreadyDone = chap?.isCompleted == true ||
            (com.siamts.synapse.data.local.SynapseDatabase.getLessonProgressDao().getProgressCached(canonId)?.isCompleted == true)

        _uiState.update {
            it.copy(
                isLoading = false,
                chapterId = resolvedCid,
                chapterTitle = chap?.title.orEmpty().ifEmpty {
                    BaseCurriculumData.getChapterById(targetChapterId)?.title ?: "Lesson"
                },
                subjectId = sub?.id ?: 1,
                subjectName = resolvedSubName,
                subjectSlug = sub?.slug.orEmpty(),
                chapterOrder = chap?.chapterOrder ?: 1,
                durationMinutes = chap?.estimatedMinutes ?: 15,
                isCompleted = isAlreadyDone,
                currentPartIndex = 0,
                part1Title = p1Title,
                part1Content = p1Content,
                part2Title = p2Title,
                part2Content = p2Content,
                syllabus = syllabusPoints,
                coreFormulas = formulaPoints,
                previousChapterId = chap?.previousChapterId,
                nextChapterId = chap?.nextChapterId
            )
        }
    }

    fun selectPart(partIndex: Int) {
        _uiState.update { it.copy(currentPartIndex = partIndex) }
    }

    fun setCheatSheetMode(enabled: Boolean) {
        _uiState.update { it.copy(isCheatSheetMode = enabled) }
    }

    fun saveSmartNotes(
        title: String,
        content: String,
        onDone: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"
            val state = _uiState.value

            val result = notesRepository.createNote(
                title = title,
                content = content,
                subjectId = state.subjectId,
                chapterId = state.chapterId,
                category = "Subject Notes",
                userId = userId,
                email = userEmail
            )
            val isSuccess = result.isSuccess
            if (isSuccess) {
                try {
                    val subName = state.subjectName.ifBlank { "Python & Coding Logic" }
                    com.siamts.synapse.data.local.SynapseDatabase.logSession(
                        trackName = subName,
                        category = "Note Making",
                        durationMinutes = 5
                    )
                } catch (_: Throwable) {}
            }
            onDone(isSuccess)
        }
    }

    fun markAsCompleted(onCompleted: (() -> Unit)? = null) {
        val cid = _uiState.value.chapterId
        if (cid <= 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isMarkingComplete = true) }

            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val subName = _uiState.value.subjectName.ifBlank { "Python & Coding Logic" }
            val normTrack = com.siamts.synapse.data.local.TrackMapping.normalizeTrackId(subName)
            val canonLessonId = com.siamts.synapse.data.local.TrackMapping.canonicalLessonId(cid, normTrack)

            // 1. Mark in Room DB / LessonProgressDao
            try {
                com.siamts.synapse.data.local.SynapseDatabase.getLessonProgressDao().markLessonCompleted(
                    lessonId = canonLessonId,
                    trackId = normTrack,
                    userId = userId.toString()
                )
            } catch (_: Throwable) {}

            // 2. Automatically log Focused Study session for Progress & Analytics
            try {
                com.siamts.synapse.data.local.SynapseDatabase.logSession(
                    trackName = com.siamts.synapse.data.local.TrackMapping.getTrackTitle(normTrack),
                    category = "Focused Study",
                    durationMinutes = 15
                )
            } catch (_: Throwable) {}

            // 3. Asynchronously sync to backend
            try {
                com.siamts.synapse.data.local.SynapseDatabase.syncProgressToRemote(
                    trackId = normTrack,
                    lessonId = canonLessonId,
                    lessonCompleted = true,
                    quizCompleted = false,
                    userId = userId
                )
            } catch (_: Throwable) {}

            val result = chaptersRepository.completeChapter(
                chapterId = cid,
                userId = userId,
                email = userEmail
            )

            result.onSuccess {
                try {
                    val subName = _uiState.value.subjectName.ifBlank { "Python & Coding Logic" }
                    com.siamts.synapse.data.local.SynapseDatabase.logSession(
                        trackName = subName,
                        category = "Focused Study",
                        durationMinutes = 15
                    )
                } catch (_: Throwable) {}
                _uiState.update {
                    it.copy(
                        isMarkingComplete = false,
                        isCompleted = true,
                        completionSuccess = true
                    )
                }
                onCompleted?.invoke()
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isMarkingComplete = false,
                        errorMessage = err.localizedMessage ?: "Failed to update completion status"
                    )
                }
            }
        }
    }

    fun resetTrackCompletion() {
        _uiState.update { it.copy(isCompleted = false) }
    }
}
