package com.siamts.synapse.ui.lesson

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.BaseCurriculumData
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.local.SynapseDatabase
import com.siamts.synapse.data.local.TrackMapping
import com.siamts.synapse.data.repository.ChaptersRepository
import com.siamts.synapse.data.repository.ChaptersRepositoryImpl
import com.siamts.synapse.data.repository.NotesRepository
import com.siamts.synapse.data.repository.NotesRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
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

    private var progressJob: Job? = null

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

        val normTrack = TrackMapping.normalizeTrackId(resolvedSubject?.name.orEmpty().ifBlank { subjectId.orEmpty() })
        val chapIndex = TrackMapping.chapterIndex(targetChapterId, normTrack)
        val canonId = TrackMapping.canonicalLessonNumericId(normTrack, chapIndex).toString()

        // 0. Immediately reset chapter state to prevent cross-chapter completion leakage
        _uiState.update {
            it.copy(
                chapterId = targetChapterId,
                isLoading = true,
                isCompleted = false,
                conceptDone = false,
                quizDone = false,
                isMarkingComplete = false,
                completionSuccess = false
            )
        }

        // 1. Reactive DB observation for this exact chapter
        progressJob?.cancel()
        progressJob = scope.launch {
            val user = sessionManager?.getSessionUser()
            val userIdStr = (user?.id ?: 1).toString()
            val dao = SynapseDatabase.getLessonProgressDao()

            // Immediate check from cache
            val initial = dao.getProgressCached(canonId, userIdStr)
                ?: dao.getChapterProgress(normTrack, chapIndex, userIdStr)
            _uiState.update {
                it.copy(
                    isCompleted = initial?.isCompleted == true,
                    conceptDone = initial?.conceptDone == true || initial?.lessonCompleted == true,
                    quizDone = initial?.quizDone == true || initial?.quizCompleted == true
                )
            }

            // Real-time flow observation
            dao.observeChapterProgress(normTrack, chapIndex, userIdStr).collect { prog ->
                _uiState.update {
                    it.copy(
                        isCompleted = prog?.isCompleted == true,
                        conceptDone = prog?.conceptDone == true || prog?.lessonCompleted == true,
                        quizDone = prog?.quizDone == true || prog?.quizCompleted == true
                    )
                }
            }
        }

        // 2. Immediately apply local curriculum lesson synchronously for instant offline display
        val localLesson = BaseCurriculumData.getChapterLesson(targetChapterId)
            ?: (if (resolvedSubject?.chapters?.isNotEmpty() == true) {
                resolvedSubject.chapters.firstOrNull()?.let { BaseCurriculumData.getChapterLesson(it.chapterId) }
            } else null)

        if (localLesson?.data != null) {
            applyLessonData(targetChapterId, localLesson.data)
        }

        // 3. Background sync with backend
        scope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

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
        val normTrack = TrackMapping.normalizeTrackId(resolvedSubName)
        val chapIndex = TrackMapping.chapterIndex(resolvedCid, normTrack)
        val canonId = TrackMapping.canonicalLessonNumericId(normTrack, chapIndex).toString()

        val cachedProg = SynapseDatabase.getLessonProgressDao().getProgressCached(canonId, "1")
        val isDone = cachedProg?.isCompleted == true || chap?.isCompleted == true
        val cDone = cachedProg?.conceptDone == true || cachedProg?.lessonCompleted == true
        val qDone = cachedProg?.quizDone == true || cachedProg?.quizCompleted == true

        val resolvedPrevId = chap?.previousChapterId ?: if (chapIndex > 1) TrackMapping.canonicalLessonNumericId(normTrack, chapIndex - 1) else null
        val resolvedNextId = chap?.nextChapterId ?: if (chapIndex < 5) TrackMapping.canonicalLessonNumericId(normTrack, chapIndex + 1) else null

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
                chapterOrder = chap?.chapterOrder ?: chapIndex,
                durationMinutes = chap?.estimatedMinutes ?: 15,
                isCompleted = isDone,
                conceptDone = cDone,
                quizDone = qDone,
                currentPartIndex = 0,
                part1Title = p1Title,
                part1Content = p1Content,
                part2Title = p2Title,
                part2Content = p2Content,
                syllabus = syllabusPoints,
                coreFormulas = formulaPoints,
                previousChapterId = resolvedPrevId,
                nextChapterId = resolvedNextId
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
                    SynapseDatabase.logSession(
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
            val subName = _uiState.value.subjectName.ifBlank { "Python & Coding Logic" }
            val normTrack = TrackMapping.normalizeTrackId(subName)
            val chapIndex = TrackMapping.chapterIndex(cid, normTrack)
            val canonLessonId = TrackMapping.canonicalLessonNumericId(normTrack, chapIndex).toString()

            // 1. Mark in Room DB / LessonProgressDao
            try {
                SynapseDatabase.getLessonProgressDao().markConceptDone(
                    userId = userId.toString(),
                    trackId = normTrack,
                    chapterIndex = chapIndex
                )
            } catch (_: Throwable) {}

            // 2. Automatically log Focused Study session for Progress & Analytics
            try {
                SynapseDatabase.logSession(
                    trackName = TrackMapping.getTrackTitle(normTrack),
                    category = "Focused Study",
                    durationMinutes = 15
                )
            } catch (_: Throwable) {}

            // 3. Asynchronously sync to backend
            try {
                SynapseDatabase.syncProgressToRemote(
                    trackId = normTrack,
                    lessonId = canonLessonId,
                    chapterIndex = chapIndex,
                    conceptDone = true,
                    quizDone = _uiState.value.quizDone,
                    userId = userId
                )
            } catch (_: Throwable) {}

            _uiState.update {
                it.copy(
                    isMarkingComplete = false,
                    isCompleted = true,
                    conceptDone = true,
                    completionSuccess = true
                )
            }
            onCompleted?.invoke()
        }
    }

    /**
     * Start Again / Reset Current Chapter:
     * Clears conceptDone and quizDone for this chapter in Room DB and syncs to backend,
     * immediately resetting the state and allowing the user to practice fresh.
     */
    fun resetCurrentChapter(onReset: (() -> Unit)? = null) {
        val cid = _uiState.value.chapterId
        if (cid <= 0) return

        viewModelScope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val subName = _uiState.value.subjectName.ifBlank { "Python & Coding Logic" }
            val normTrack = TrackMapping.normalizeTrackId(subName)
            val chapIndex = TrackMapping.chapterIndex(cid, normTrack)
            val canonLessonId = TrackMapping.canonicalLessonNumericId(normTrack, chapIndex).toString()

            // 1. Reset in Room DB / LessonProgressDao
            try {
                SynapseDatabase.getLessonProgressDao().resetChapterProgress(
                    trackId = normTrack,
                    chapterIndex = chapIndex,
                    userId = userId.toString()
                )
            } catch (_: Throwable) {}

            // 2. Sync reset to remote backend
            try {
                SynapseDatabase.syncProgressToRemote(
                    trackId = normTrack,
                    lessonId = canonLessonId,
                    chapterIndex = chapIndex,
                    reset = true,
                    userId = userId
                )
            } catch (_: Throwable) {}

            _uiState.update {
                it.copy(
                    isCompleted = false,
                    conceptDone = false,
                    quizDone = false,
                    completionSuccess = false
                )
            }
            onReset?.invoke()
        }
    }

    fun resetTrackCompletion() {
        val subName = _uiState.value.subjectName.ifBlank { "Python & Coding Logic" }
        val normTrack = TrackMapping.normalizeTrackId(subName)
        viewModelScope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            try {
                SynapseDatabase.getLessonProgressDao().resetTrackProgress(normTrack, userId.toString())
            } catch (_: Throwable) {}
            _uiState.update { it.copy(isCompleted = false, conceptDone = false, quizDone = false) }
        }
    }
}
