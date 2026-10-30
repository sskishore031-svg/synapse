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

/**
 * ViewModel managing Saved & Downloaded Notes search and actions.
 */
class SavedNotesViewModel(
    private val notesRepository: NotesRepository = NotesRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedNotesUiState())
    val uiState: StateFlow<SavedNotesUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val savedRes = notesRepository.getNotesList(
                type = "saved",
                search = _uiState.value.searchQuery.takeIf { it.isNotBlank() },
                userId = userId,
                email = userEmail
            )

            val downloadedRes = notesRepository.getNotesList(
                type = "downloaded",
                search = _uiState.value.searchQuery.takeIf { it.isNotBlank() },
                userId = userId,
                email = userEmail
            )

            val savedItems = savedRes.getOrNull()?.data?.notes?.map { note ->
                SavedNoteItem(
                    id = note.id.toString(),
                    title = note.title,
                    subtitle = "${note.subjectName} · ${if (note.category == "COMMON") "General" else "Subject"}",
                    statusText = if (note.isDownloaded) "Saved · Downloaded" else "Saved · Cloud",
                    buttonAction = if (note.isDownloaded) "Open" else "Download"
                )
            }.orEmpty()

            val downloadedItems = downloadedRes.getOrNull()?.data?.notes?.map { note ->
                DownloadedFileItem(
                    id = note.id.toString(),
                    title = note.title,
                    subtitle = "${note.subjectName} · Available offline",
                    offlineBadge = "Available offline",
                    buttonAction = "Open"
                )
            }.orEmpty()

            _uiState.update {
                it.copy(
                    savedNotes = if (savedItems.isNotEmpty() || _uiState.value.searchQuery.isNotBlank()) savedItems else it.savedNotes,
                    downloadedFiles = if (downloadedItems.isNotEmpty() || _uiState.value.searchQuery.isNotBlank()) downloadedItems else it.downloadedFiles
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadData()
    }

    fun onOpenNoteClicked(id: String) {
        // Open note
    }

    fun onDownloadNoteClicked(id: String) {
        val noteId = id.toIntOrNull() ?: return
        viewModelScope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"
            notesRepository.toggleDownload(noteId = noteId, userId = userId, email = userEmail)
            loadData()
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }
}
