package com.siamts.synapse.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.model.NotesCountsDto
import com.siamts.synapse.data.repository.NotesRepository
import com.siamts.synapse.data.repository.NotesRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotesHomeViewModel(
    private val notesRepository: NotesRepository = NotesRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesHomeUiState())
    val uiState: StateFlow<NotesHomeUiState> = _uiState.asStateFlow()

    init {
        loadNotesSummary()
    }

    fun loadNotesSummary() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val result = notesRepository.getNotesSummary(userId = userId, email = userEmail)

            result.onSuccess { response ->
                val data = response.data
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        summary = data?.summary ?: NotesCountsDto(),
                        subjects = data?.subjects.orEmpty()
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = err.localizedMessage ?: "Failed to load notes summary"
                    )
                }
            }
        }
    }

    fun createQuickNote(
        title: String,
        content: String,
        subjectId: Int? = null,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val result = notesRepository.createNote(
                title = title,
                content = content,
                subjectId = subjectId,
                chapterId = null,
                userId = userId,
                email = userEmail
            )

            result.onSuccess {
                loadNotesSummary()
                onSuccess?.invoke()
            }
        }
    }
}

