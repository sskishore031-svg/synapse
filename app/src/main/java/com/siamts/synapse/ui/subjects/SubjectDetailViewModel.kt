package com.siamts.synapse.ui.subjects

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.BaseCurriculumData
import com.siamts.synapse.data.repository.ChaptersRepository
import com.siamts.synapse.data.repository.SubjectCatalogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private val chaptersRepo: ChaptersRepository? = null,
    externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope: CoroutineScope = externalScope ?: try {
        viewModelScope
    } catch (_: Throwable) {
        CoroutineScope(Dispatchers.IO)
    }

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
            progressDao.observeProgressForUser("1").collect { progressList ->
                val completedMap = progressList.associateBy { it.lessonId }
                _uiState.update { current ->
                    if (current.chapters.isEmpty()) return@update current
                    val normTrack = com.siamts.synapse.data.local.TrackMapping.normalizeTrackId(current.subjectId.ifBlank { current.title })
                    val updatedChapters = current.chapters.map { ch ->
                        val canonId = com.siamts.synapse.data.local.TrackMapping.canonicalLessonId(ch.chapterId, normTrack)
                        val isDone = completedMap[canonId]?.isCompleted == true || completedMap[ch.chapterId.toString()]?.isCompleted == true
                        ch.copy(isCompleted = isDone)
                    }
                    val completedCount = updatedChapters.count { it.isCompleted }
                    val total = updatedChapters.size.coerceAtLeast(1)
                    val pct = ((completedCount.toFloat() / total.toFloat()) * 100).toInt().coerceIn(0, 100)
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
        _uiState.update { it.copy(isLoading = true, errorMessage = null, subjectId = trimmed) }

        scope.launch {
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

                val existingProgress = try {
                    val progressDao = com.siamts.synapse.data.local.SynapseDatabase.getLessonProgressDao()
                    runCatching { progressDao.getProgressForUser("1") }.getOrDefault(emptyList()).associateBy { it.lessonId }
                } catch (_: Throwable) {
                    emptyMap()
                }
                val normTrack = com.siamts.synapse.data.local.TrackMapping.normalizeTrackId(subject.id.ifBlank { subject.name })

                val chapterItems = subject.chapters.map { ch ->
                    val canonId = com.siamts.synapse.data.local.TrackMapping.canonicalLessonId(ch.chapterId, normTrack)
                    val isDone = existingProgress[canonId]?.isCompleted == true || existingProgress[ch.chapterId.toString()]?.isCompleted == true
                    SubjectChapterDetailItem(
                        chapterId = ch.chapterId,
                        order = ch.chapterOrder,
                        title = ch.title,
                        summary = ch.summary,
                        estimatedMinutes = ch.estimatedMinutes,
                        questionCount = ch.mcqs.size.coerceAtLeast(5),
                        isCompleted = isDone,
                        isUnlocked = true
                    )
                }
                val doneCount = chapterItems.count { it.isCompleted }
                val totalCount = chapterItems.size.coerceAtLeast(1)
                val initialPct = ((doneCount.toFloat() / totalCount.toFloat()) * 100).toInt().coerceIn(0, 100)

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
                        progressPercentage = initialPct,
                        completedChapters = doneCount,
                        totalChapters = chapterItems.size,
                        chapters = chapterItems,
                        errorMessage = null
                    )
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
}
