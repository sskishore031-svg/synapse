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

    fun setFilter(filter: String, subjectId: Int? = null) {
        val upper = filter.uppercase()
        _uiState.update { it.copy(activeFilter = upper) }
        if (upper != "ALL") {
            loadFilteredNotes(type = upper.lowercase(), subjectId = subjectId)
        }
    }

    fun loadFilteredNotes(type: String, subjectId: Int? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isFilteredLoading = true) }

            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val result = notesRepository.getNotesList(
                type = type,
                subjectId = subjectId,
                search = _uiState.value.searchQuery.takeIf { it.isNotBlank() },
                userId = userId,
                email = userEmail
            )

            result.onSuccess { response ->
                _uiState.update {
                    it.copy(
                        isFilteredLoading = false,
                        filteredNotes = response.data?.notes.orEmpty()
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isFilteredLoading = false,
                        errorMessage = err.localizedMessage ?: "Failed to load notes"
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (_uiState.value.activeFilter != "ALL") {
            loadFilteredNotes(type = _uiState.value.activeFilter.lowercase())
        }
    }

    fun createQuickNote(
        title: String,
        content: String,
        category: String = "COMMON",
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
                category = category,
                userId = userId,
                email = userEmail
            )

            result.onSuccess {
                loadNotesSummary()
                if (_uiState.value.activeFilter != "ALL") {
                    loadFilteredNotes(type = _uiState.value.activeFilter.lowercase())
                }
                onSuccess?.invoke()
            }
        }
    }

    fun toggleSave(noteId: Int) {
        viewModelScope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            notesRepository.toggleSave(noteId = noteId, userId = userId, email = userEmail)
            loadNotesSummary()
            if (_uiState.value.activeFilter != "ALL") {
                loadFilteredNotes(type = _uiState.value.activeFilter.lowercase())
            }
        }
    }

    fun toggleDownload(noteId: Int) {
        viewModelScope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            notesRepository.toggleDownload(noteId = noteId, userId = userId, email = userEmail)
            loadNotesSummary()
            if (_uiState.value.activeFilter != "ALL") {
                loadFilteredNotes(type = _uiState.value.activeFilter.lowercase())
            }
        }
    }
}
