package com.siamts.synapse.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.model.NoteItemDto
import com.siamts.synapse.data.repository.NotesRepository
import com.siamts.synapse.data.repository.NotesRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

            val localRoomNotes = kotlinx.coroutines.withContext(Dispatchers.IO) {
                try {
                    val dao = com.siamts.synapse.data.local.SynapseDatabase.getNotesDao()
                    dao.getAllNotes(userId).map { entity ->
                        NoteItemDto(
                            id = entity.id.toInt(),
                            userId = entity.userId,
                            subjectId = entity.subjectId?.toIntOrNull(),
                            chapterId = entity.chapterId,
                            title = entity.title,
                            content = entity.content,
                            category = entity.category,
                            isSaved = entity.isBookmarked,
                            isDownloaded = entity.isOfflineCached,
                            subjectName = entity.trackName ?: entity.subjectId ?: "General Notes",
                            subjectSlug = (entity.trackName ?: entity.subjectId)?.lowercase() ?: "general",
                            subjectIcon = "📝",
                            createdAt = "Today",
                            updatedAt = "Today"
                        )
                    }
                } catch (_: Throwable) {
                    emptyList()
                }
            }

            val filteredLocal = when (type.lowercase()) {
                "subject" -> localRoomNotes.filter { it.category.contains("SUBJECT", true) || it.category.contains("Subject", true) }
                "common" -> localRoomNotes.filter { it.category.contains("COMMON", true) }
                "saved" -> localRoomNotes.filter { it.isSaved }
                "downloaded" -> localRoomNotes.filter { it.isDownloaded }
                else -> localRoomNotes
            }

            val result = notesRepository.getNotesList(
                type = type,
                subjectId = subjectId,
                search = _uiState.value.searchQuery.takeIf { it.isNotBlank() },
                userId = userId,
                email = userEmail
            )

            result.onSuccess { response ->
                val combined = (filteredLocal + response.data?.notes.orEmpty()).distinctBy { it.id.toString() + "_" + it.title.trim().lowercase() }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        notes = combined
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        notes = filteredLocal,
                        errorMessage = if (filteredLocal.isNotEmpty()) null else (err.localizedMessage ?: "No notes found")
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

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        viewModelScope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val result = notesRepository.getNotesList(
                type = _uiState.value.type,
                subjectId = _uiState.value.subjectId,
                search = query.takeIf { it.isNotBlank() },
                userId = userId,
                email = userEmail
            )
            result.onSuccess { response ->
                _uiState.update {
                    it.copy(notes = response.data?.notes.orEmpty())
                }
            }
        }
    }

    fun updateNote(noteId: Int, title: String, content: String) {
        viewModelScope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val res = notesRepository.updateNote(
                noteId = noteId,
                title = title,
                content = content,
                userId = userId,
                email = userEmail
            )
            if (res.isSuccess) {
                _uiState.update { state ->
                    val updated = state.notes.map { n ->
                        if (n.id == noteId) n.copy(title = title, content = content) else n
                    }
                    state.copy(notes = updated)
                }
            }
        }
    }
}

