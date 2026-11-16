package com.siamts.synapse.ui.quiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.repository.CurriculumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLDecoder

/**
 * ViewModel managing dynamic chapter list retrieval for "Select Chapter Quiz" / "QuizChapterListScreen".
 * Guarantees zero blank screens by reading SavedStateHandle safely and leveraging CurriculumRepository.
 */
class QuizChapterViewModel(
    savedStateHandle: SavedStateHandle? = null,
    private val curriculumRepo: CurriculumRepository = CurriculumRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubjectQuizUiState())
    val uiState: StateFlow<SubjectQuizUiState> = _uiState.asStateFlow()

    init {
        val sId = savedStateHandle?.get<String>("subjectId")
            ?: savedStateHandle?.get<String>("subject_id").orEmpty()
        val sName = savedStateHandle?.get<String>("subjectName")
            ?: savedStateHandle?.get<String>("subject_name").orEmpty()

        if (sId.isNotBlank() || sName.isNotBlank()) {
            loadChapters(sId, sName)
        }
    }

    fun loadChapters(subjectId: String, subjectName: String? = null) {
        val decodedId = try {
            URLDecoder.decode(subjectId, "UTF-8").trim()
        } catch (_: Throwable) {
            subjectId.trim()
        }
        val decodedName = subjectName?.let {
            try {
                URLDecoder.decode(it, "UTF-8").trim()
            } catch (_: Throwable) {
                it.trim()
            }
        }
        val numId = decodedId.toIntOrNull() ?: 1

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, subjectId = numId) }
            val quizzes = curriculumRepo.getSubjectQuizzes(decodedId, decodedName)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    quizzes = quizzes,
                    subjectId = numId
                )
            }
        }
    }
}

