package com.siamts.synapse.ui.chemistry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.model.ChemistryDataDto
import com.siamts.synapse.data.repository.ChemistryRepository
import com.siamts.synapse.data.repository.ChemistryRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing Chemical Bonding Lesson Part 2 state and completion persistence.
 */
class ChemicalBondingLessonPart2ViewModel(
    private val repository: ChemistryRepository = ChemistryRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChemicalBondingLessonPart2UiState(isLoading = true))
    val uiState: StateFlow<ChemicalBondingLessonPart2UiState> = _uiState.asStateFlow()

    init {
        loadLessonData()
    }

    fun loadLessonData(forceRefresh: Boolean = false) {
        if (_uiState.value.isSuccess && !forceRefresh && !_uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val user = (sessionManager ?: SessionManager.getInstanceOrNull())?.getSessionUser()
            val userId = user?.id?.takeIf { it > 0 }
            val email = user?.email?.takeIf { it.isNotBlank() }

            val result = repository.getChemistryLesson(userId = userId, email = email, chapterId = 28)

            if (result.isSuccess) {
                val data = result.getOrNull()
                if (data != null) {
                    applyBackendData(data)
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "No lesson data received.") }
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Failed to connect to Chemistry server."
                    )
                }
            }
        }
    }

    private fun applyBackendData(data: ChemistryDataDto) {
        val subject = data.subject
        val part2 = data.activeChapter?.part2
        val activeChapter = data.chapters?.find { it.id == "28" }

        _uiState.update { current ->
            current.copy(
                isLoading = false,
                isSuccess = true,
                errorMessage = null,
                progressPercent = subject?.progressPercent ?: current.progressPercent,
                progressSubtext = subject?.progressSubtext ?: current.progressSubtext,
                headline = part2?.headline ?: current.headline,
                conceptText = part2?.conceptText ?: current.conceptText,
                keyPoints = if (!part2?.keyPoints.isNullOrEmpty()) part2.keyPoints else current.keyPoints,
                quickExampleText = part2?.quickExampleText ?: current.quickExampleText,
                aiRecommendation = part2?.aiRecommendation ?: current.aiRecommendation,
                isCompleted = activeChapter?.isCompleted ?: false
            )
        }
    }

    fun onSubTabSelected(index: Int) {
        _uiState.update { it.copy(selectedSubTab = index) }
    }

    /**
     * Marks the chapter completed in MySQL via the PHP backend and updates local progress.
     */
    fun completeChapter(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val user = (sessionManager ?: SessionManager.getInstanceOrNull())?.getSessionUser()
            val userId = user?.id ?: 1

            val result = repository.completeChapter(userId = userId, chapterId = 28)
            if (result.isSuccess) {
                val data = result.getOrNull()
                if (data != null) applyBackendData(data)
                _uiState.update { it.copy(isCompleted = true) }
                onSuccess()
            }
        }
    }

    fun retry() {
        loadLessonData(forceRefresh = true)
    }

    fun refresh() {
        loadLessonData(forceRefresh = true)
    }
}
