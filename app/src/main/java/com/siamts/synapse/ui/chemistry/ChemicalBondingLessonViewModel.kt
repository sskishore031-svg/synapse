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
 * ViewModel managing Chemical Bonding Lesson Screen state and real backend integration.
 */
class ChemicalBondingLessonViewModel(
    private val repository: ChemistryRepository = ChemistryRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChemicalBondingLessonUiState(isLoading = true))
    val uiState: StateFlow<ChemicalBondingLessonUiState> = _uiState.asStateFlow()

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
        val part1 = data.activeChapter?.part1
        val activeChapter = data.chapters?.find { it.id == "28" }

        _uiState.update { current ->
            current.copy(
                isLoading = false,
                isSuccess = true,
                errorMessage = null,
                title = data.activeChapter?.title ?: current.title,
                progressPercent = subject?.progressPercent ?: current.progressPercent,
                progressSubtext = subject?.progressSubtext ?: current.progressSubtext,
                headline = part1?.headline ?: current.headline,
                conceptText = part1?.conceptText ?: current.conceptText,
                keyPoints = if (!part1?.keyPoints.isNullOrEmpty()) part1.keyPoints else current.keyPoints,
                exampleTitle = part1?.exampleTitle ?: current.exampleTitle,
                exampleProblem = part1?.exampleProblem ?: current.exampleProblem,
                exampleSolution = part1?.exampleSolution ?: current.exampleSolution,
                isCompleted = activeChapter?.isCompleted ?: false
            )
        }
    }

    fun onSubTabSelected(index: Int) {
        _uiState.update { it.copy(selectedSubTab = index) }
    }

    fun onNextClicked() {
        // Handled in screen navigation
    }

    fun onPreviousClicked() {
        // Handled in screen navigation
    }

    fun retry() {
        loadLessonData(forceRefresh = true)
    }

    fun refresh() {
        loadLessonData(forceRefresh = true)
    }
}
