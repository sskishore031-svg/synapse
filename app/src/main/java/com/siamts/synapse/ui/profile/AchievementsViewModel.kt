package com.siamts.synapse.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.model.AchievementBadgeDto
import com.siamts.synapse.data.repository.AchievementsRepository
import com.siamts.synapse.data.repository.AchievementsRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AchievementsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val totalBadgesText: String = "8 Badges Earned",
    val badges: List<AchievementBadgeDto> = emptyList()
)

class AchievementsViewModel(
    private val repository: AchievementsRepository = AchievementsRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AchievementsUiState(isLoading = true))
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    init {
        loadAchievements()
    }

    fun loadAchievements() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val email = user?.email ?: "student@synapse.ai"

            val result = repository.getAchievements(userId = userId, email = email)
            result.onSuccess { response ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        totalBadgesText = "${response.totalBadges} Badges Earned",
                        badges = response.achievements
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = err.localizedMessage ?: "Failed to load achievements."
                    )
                }
            }
        }
    }
}
