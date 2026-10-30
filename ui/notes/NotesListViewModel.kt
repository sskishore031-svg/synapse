package com.siamts.synapse.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.NotesRepository
import com.siamts.synapse.data.repository.NotesRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotesListViewModel(
    private val notesRepository: NotesRepository = NotesRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesListUiState())
    val uiState: StateFlow<NotesListUiState> = _uiState.asStateFlow()

    fun loadNotes(type: String, subjectId: Int? = null, title: String? = null) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    type = type,
                    subjectId = subjectId,
                    title = title ?: "${type.replaceFirstChar { c -> c.uppercase() }} Notes"
                )
            }

            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val result = notesRepository.getNotesList(
                type = type,
                subjectId = subjectId,
                userId = userId,
                email = userEmail
            )

            result.onSuccess { response ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        notes = response.data?.notes.orEmpty()
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = err.localizedMessage ?: "Failed to load notes"
                    )
                }
            }
        }
    }

    fun toggleSave(noteId: Int) {
        viewModelScope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            notesRepository.toggleSave(noteId = noteId, userId = userId, email = userEmail)

            _uiState.update { state ->
                val updated = state.notes.map { n ->
                    if (n.id == noteId) n.copy(isSaved = !n.isSaved) else n
                }
                state.copy(notes = updated)
            }
        }
    }

    fun toggleDownload(noteId: Int) {
        viewModelScope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            notesRepository.toggleDownload(noteId = noteId, userId = userId, email = userEmail)

            _uiState.update { state ->
                val updated = state.notes.map { n ->
                    if (n.id == noteId) n.copy(isDownloaded = !n.isDownloaded) else n
                }
                state.copy(notes = updated)
            }
        }
    }

    fun deleteNote(noteId: Int) {
        viewModelScope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            notesRepository.deleteNote(noteId = noteId, userId = userId, email = userEmail)

            _uiState.update { state ->
                state.copy(notes = state.notes.filter { it.id != noteId })
            }
        }
    }
}
