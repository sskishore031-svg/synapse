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
        val initialId = savedStateHandle?.get<String>("subjectId")
            ?: savedStateHandle?.get<String>("subject_id")
        if (!initialId.isNullOrBlank()) {
            loadSubject(initialId)
        }
    }

    fun loadSubject(subjectId: String) {
        val trimmed = subjectId.trim()
        _uiState.update { it.copy(isLoading = true, errorMessage = null, subjectId = trimmed) }

        val repo = catalogRepo ?: try {
            SubjectCatalogRepository.getInstance()
        } catch (_: Throwable) {
            null
        }

        val subject = repo?.getSubjectById(trimmed) ?: BaseCurriculumData.subjects.firstOrNull {
            it.id.equals(trimmed, ignoreCase = true) ||
            it.name.equals(trimmed, ignoreCase = true) ||
            it.slug.equals(trimmed, ignoreCase = true) ||
            it.subjectId.toString() == trimmed ||
            (trimmed.lowercase(Locale.ROOT) == "math" && it.id == "MATH") ||
            (trimmed.lowercase(Locale.ROOT) == "cs" && it.id == "CS") ||
            (trimmed.lowercase(Locale.ROOT) == "ds" && it.id == "DS") ||
            (trimmed.lowercase(Locale.ROOT) == "ai" && it.id == "AI") ||
            (trimmed.lowercase(Locale.ROOT) == "phy" && it.id == "PHY") ||
            (trimmed.lowercase(Locale.ROOT) == "chem" && it.id == "CHEM")
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
