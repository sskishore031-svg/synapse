package com.siamts.synapse.ui.subjects

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.BaseCurriculumData
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.ChaptersRepository
import com.siamts.synapse.data.repository.SubjectCatalogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * ViewModel for SubjectDetailScreen that dynamically resolves any subject (e.g., MATH, CS, DS, AI, PHY, CHEM)
 * from SavedStateHandle or direct parameter, loading real curriculum data and chapters offline without
 * falling back to a fixed subject.
 */
class SubjectDetailViewModel(
    private val savedStateHandle: SavedStateHandle? = null,
    private val catalogRepo: SubjectCatalogRepository? = try {
        SubjectCatalogRepository.getInstance()
    } catch (_: Throwable) {
        null
    },
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull(),
    private val chaptersRepo: ChaptersRepository? = null,
    externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope: CoroutineScope = externalScope ?: try {
        val d = Dispatchers.Main
        d.isDispatchNeeded(kotlin.coroutines.EmptyCoroutineContext)
        viewModelScope
    } catch (_: Throwable) {
        CoroutineScope(Dispatchers.Unconfined)
    }

    private var loadJob: kotlinx.coroutines.Job? = null

    private val _uiState = MutableStateFlow(SubjectDetailUiState())
    val uiState: StateFlow<SubjectDetailUiState> = _uiState.asStateFlow()

    init {
        observeLessonProgress()
        val initialId = savedStateHandle?.get<String>("subjectId")
            ?: savedStateHandle?.get<String>("subject_id")
        if (!initialId.isNullOrBlank()) {
            loadSubject(initialId)
        }
    }

    private fun observeLessonProgress() {
        val progressDao = try {
            com.siamts.synapse.data.local.SynapseDatabase.getLessonProgressDao()
        } catch (_: Throwable) {
            null
        } ?: return
        scope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = (user?.id ?: 1).toString()
            progressDao.observeProgressForUser(userId).collect { progressList ->
                val progressByTrackAndIndex = progressList.associateBy {
                    "${com.siamts.synapse.data.local.TrackMapping.normalizeTrackId(it.trackId)}_${it.chapterIndex}"
                }
                val progressByLessonId = progressList.associateBy { it.lessonId }

                _uiState.update { current ->
                    if (current.chapters.isEmpty()) return@update current
                    val normTrack = com.siamts.synapse.data.local.TrackMapping.normalizeTrackId(current.subjectId.ifBlank { current.title })
                    val updatedChapters = current.chapters.map { ch ->
                        val chIdx = com.siamts.synapse.data.local.TrackMapping.chapterIndex(ch.chapterId, normTrack)
                        val canonNumericId = com.siamts.synapse.data.local.TrackMapping.canonicalLessonNumericId(normTrack, chIdx).toString()
                        val prog = progressByTrackAndIndex["${normTrack}_$chIdx"]
                            ?: progressByLessonId[canonNumericId]
                            ?: progressByLessonId[ch.chapterId.toString()]
                            ?: progressByLessonId[com.siamts.synapse.data.local.TrackMapping.canonicalLessonKey(normTrack, chIdx)]

                        val cDone = prog?.conceptDone == true || prog?.lessonCompleted == true
                        val qDone = prog?.quizDone == true || prog?.quizCompleted == true
                        val isDone = cDone || qDone
                        ch.copy(isCompleted = isDone, conceptDone = cDone, quizDone = qDone)
                    }
                    val total = updatedChapters.size.coerceAtLeast(1)
                    val totalCredits = updatedChapters.sumOf { (if (it.conceptDone) 0.5 else 0.0) + (if (it.quizDone) 0.5 else 0.0) }
                    val pct = ((totalCredits / total.toDouble()) * 100.0).toInt().coerceIn(0, 100)
                    val completedCount = updatedChapters.count { it.conceptDone && it.quizDone }
                    current.copy(
                        completedChapters = completedCount,
                        progressPercentage = pct,
                        chapters = updatedChapters
                    )
                }
            }
        }
    }

    fun loadSubject(subjectId: String) {
        val trimmed = subjectId.trim()
        loadJob?.cancel()

        val repo = catalogRepo ?: try {
            SubjectCatalogRepository.getInstance()
        } catch (_: Throwable) {
            null
        }

        val subject = repo?.getSubjectById(trimmed)
            ?: BaseCurriculumData.getSubjectById(trimmed)
            ?: BaseCurriculumData.subjects.firstOrNull {
                it.id.equals(trimmed, ignoreCase = true) ||
                it.name.equals(trimmed, ignoreCase = true) ||
                it.slug.equals(trimmed, ignoreCase = true) ||
                it.subjectId.toString() == trimmed
            }

        if (subject != null) {
            val parsedColor = try {
                Color(android.graphics.Color.parseColor(subject.colorHex))
            } catch (_: Throwable) {
                when (subject.id) {
                    "MATH" -> Color(0xFF3B82F6)
                    "CS" -> Color(0xFF10B981)
                    "DS" -> Color(0xFF8B5CF6)
                    "AI" -> Color(0xFF2563EB)
                    "PHY" -> Color(0xFFD97706)
                    "CHEM" -> Color(0xFFEF4444)
                    else -> Color(0xFF3B82F6)
                }
            }

            val chapterItems = subject.chapters.map { ch ->
                SubjectChapterDetailItem(
                    chapterId = ch.chapterId,
                    order = ch.chapterOrder,
                    title = ch.title,
                    summary = ch.summary,
                    estimatedMinutes = ch.estimatedMinutes,
                    questionCount = ch.mcqs.size.coerceAtLeast(5),
                    isCompleted = false,
                    isUnlocked = true
                )
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    subjectId = subject.id.ifBlank { subject.subjectId.toString() },
                    numericId = subject.subjectId,
                    title = subject.name,
                    slug = subject.slug,
                    category = subject.category,
                    iconSymbol = subject.iconSymbol,
                    colorHex = subject.colorHex,
                    color = parsedColor,
                    overview = subject.description.ifBlank { "Comprehensive curriculum and hands-on modules in ${subject.name}." },
                    progressPercentage = 0,
                    completedChapters = 0,
                    totalChapters = chapterItems.size,
                    chapters = chapterItems,
                    errorMessage = null
                )
            }

            loadJob = scope.launch {
                val existingProgress = try {
                    val progressDao = com.siamts.synapse.data.local.SynapseDatabase.getLessonProgressDao()
                    val user = sessionManager?.getSessionUser()
                    val userId = (user?.id ?: 1).toString()
                    runCatching { progressDao.getProgressForUser(userId) }.getOrDefault(emptyList())
                } catch (_: Throwable) {
                    emptyList()
                }
                if (_uiState.value.numericId != subject.subjectId) return@launch
                if (existingProgress.isNotEmpty()) {
                    val normTrack = com.siamts.synapse.data.local.TrackMapping.normalizeTrackId(subject.id.ifBlank { subject.name })
                    val progressByTrackAndIndex = existingProgress.associateBy {
                        "${com.siamts.synapse.data.local.TrackMapping.normalizeTrackId(it.trackId)}_${it.chapterIndex}"
                    }
                    val progressByLessonId = existingProgress.associateBy { it.lessonId }

                    val updatedChapters = subject.chapters.map { ch ->
                        val chIdx = com.siamts.synapse.data.local.TrackMapping.chapterIndex(ch.chapterId, normTrack)
                        val canonNumericId = com.siamts.synapse.data.local.TrackMapping.canonicalLessonNumericId(normTrack, chIdx).toString()
                        val prog = progressByTrackAndIndex["${normTrack}_$chIdx"]
                            ?: progressByLessonId[canonNumericId]
                            ?: progressByLessonId[ch.chapterId.toString()]
                            ?: progressByLessonId[com.siamts.synapse.data.local.TrackMapping.canonicalLessonKey(normTrack, chIdx)]

                        val cDone = prog?.conceptDone == true || prog?.lessonCompleted == true
                        val qDone = prog?.quizDone == true || prog?.quizCompleted == true
                        val isDone = cDone || qDone
                        SubjectChapterDetailItem(
                            chapterId = ch.chapterId,
                            order = ch.chapterOrder,
                            title = ch.title,
                            summary = ch.summary,
                            estimatedMinutes = ch.estimatedMinutes,
                            questionCount = ch.mcqs.size.coerceAtLeast(5),
                            isCompleted = isDone,
                            conceptDone = cDone,
                            quizDone = qDone,
                            isUnlocked = true
                        )
                    }
                    val totalCount = updatedChapters.size.coerceAtLeast(1)
                    val totalCredits = updatedChapters.sumOf { (if (it.conceptDone) 0.5 else 0.0) + (if (it.quizDone) 0.5 else 0.0) }
                    val pct = ((totalCredits / totalCount.toDouble()) * 100.0).toInt().coerceIn(0, 100)
                    val doneCount = updatedChapters.count { it.conceptDone && it.quizDone }
                    _uiState.update { current ->
                        if (current.numericId != subject.subjectId) return@update current
                        current.copy(
                            completedChapters = doneCount,
                            progressPercentage = pct,
                            chapters = updatedChapters
                        )
                    }
                }
            }
        } else {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Subject '$subjectId' could not be loaded."
                )
            }
        }
    }
}
