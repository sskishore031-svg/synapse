package com.siamts.synapse.ui.brainmap

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.model.BrainMapDataDto
import com.siamts.synapse.data.model.BrainMapNodeDto
import com.siamts.synapse.data.repository.BrainMapRepository
import com.siamts.synapse.data.repository.BrainMapRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing Synapse Brain Map state, real analytics retrieval,
 * and student progress node interactions following clean MVVM architecture.
 */
class BrainMapViewModel(
    private val repository: BrainMapRepository = BrainMapRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull()
) : ViewModel() {

    private val _uiState = MutableStateFlow(BrainMapUiState(isLoading = true))
    val uiState: StateFlow<BrainMapUiState> = _uiState.asStateFlow()

    init {
        loadBrainMapAnalytics()
    }

    /**
     * Fetches real-time student learning analytics from synapse_backend.
     * Scoped to the currently authenticated user in SessionManager.
     */
    fun loadBrainMapAnalytics(forceRefresh: Boolean = false) {
        if (_uiState.value.isSuccess && !forceRefresh && !_uiState.value.isLoading) {
            // Prevent unnecessary re-fetching if data is already loaded
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val manager = sessionManager ?: SessionManager.getInstanceOrNull()
            val sessionUser = manager?.getSessionUser()

            val userId = sessionUser?.id?.takeIf { it > 0 }
            val email = sessionUser?.email?.takeIf { it.isNotBlank() }

            val result = repository.getBrainMapAnalytics(userId = userId, email = email)

            if (result.isSuccess) {
                val data = result.getOrNull()
                if (data != null) {
                    applyAnalyticsData(data)
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "No learning analytics received from server."
                        )
                    }
                }
            } else {
                val error = result.exceptionOrNull()?.message ?: "Failed to connect to BrainMap server."
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error
                    )
                }
            }
        }
    }

    private fun applyAnalyticsData(data: BrainMapDataDto) {
        val summary = data.summary
        val remoteNodes = data.nodes.orEmpty()

        val mappedNodes = if (remoteNodes.isNotEmpty()) {
            remoteNodes.map { it.toBrainMapNode() }
        } else {
            defaultNodes()
        }

        val totalChapters = summary?.totalChaptersCompleted ?: 0
        val totalQuizzes = summary?.totalQuizzesAttempted ?: 0
        val isEmptyState = totalChapters == 0 && totalQuizzes == 0

        _uiState.update { current ->
            current.copy(
                isLoading = false,
                isSuccess = true,
                isEmpty = isEmptyState,
                errorMessage = null,
                overallKnowledgePercentage = summary?.overallKnowledgePercentage ?: 0,
                overallKnowledgeText = summary?.overallKnowledgeText ?: current.overallKnowledgeText,
                nodes = mappedNodes,
                recommendationTitle = summary?.recommendationTitle ?: current.recommendationTitle,
                recommendationText = summary?.recommendationText ?: current.recommendationText,
                totalSubjectsStudied = summary?.totalSubjectsStudied ?: 0,
                totalSubjectsAvailable = summary?.totalSubjectsAvailable ?: 6,
                totalChaptersCompleted = totalChapters,
                totalChaptersAvailable = summary?.totalChaptersAvailable ?: 30,
                totalQuizzesAttempted = totalQuizzes,
                averageQuizScore = summary?.averageQuizScore ?: 0,
                latestQuizScore = summary?.latestQuizScore ?: 0,
                strongestSubject = summary?.strongestSubject,
                strongestScore = summary?.strongestScore,
                weakestSubject = summary?.weakestSubject,
                weakestScore = summary?.weakestScore
            )
        }
    }

    private fun BrainMapNodeDto.toBrainMapNode(): BrainMapNode {
        val parsedColor = try {
            val hex = if (colorHex.startsWith("#")) colorHex else "#$colorHex"
            Color(android.graphics.Color.parseColor(hex))
        } catch (_: Exception) {
            Color(0xFF3B82F6)
        }

        return BrainMapNode(
            id = id,
            title = title,
            statusText = statusText,
            percentage = percentage,
            color = parsedColor,
            iconSymbol = iconSymbol ?: "📚",
            chaptersCompleted = chaptersCompleted,
            totalChapters = totalChapters,
            quizzesAttempted = quizzesAttempted,
            averageQuizScore = averageQuizScore
        )
    }

    fun onNodeClicked(nodeId: String) {
        // Find subject and handle interaction / selection
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun retry() {
        loadBrainMapAnalytics(forceRefresh = true)
    }

    fun refresh() {
        loadBrainMapAnalytics(forceRefresh = true)
    }
}
