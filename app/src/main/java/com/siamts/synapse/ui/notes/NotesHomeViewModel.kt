package com.siamts.synapse.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.local.SynapseDatabase
import com.siamts.synapse.data.model.NoteItemDto
import com.siamts.synapse.data.model.NotesCountsDto
import com.siamts.synapse.data.repository.NotesRepository
import com.siamts.synapse.data.repository.NotesRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

typealias NotesViewModel = NotesHomeViewModel
typealias NotesScreenViewModel = NotesHomeViewModel

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

            // 1. Load Room SQLite notes from NotesDao
            val localRoomNotes = withContext(Dispatchers.IO) {
                try {
                    val dao = SynapseDatabase.getNotesDao()
                    dao.getAllNotes(userId).map { entity ->
                        NoteItemDto(
                            id = entity.id.toInt(),
                            userId = entity.userId,
                            subjectId = entity.subjectId?.toIntOrNull(),
                            chapterId = entity.chapterId,
                            title = entity.title,
                            content = entity.content,
                            category = entity.category,
                            isSaved = true,
                            isDownloaded = true,
                            subjectName = entity.subjectId ?: "General Notes",
                            subjectSlug = entity.subjectId?.lowercase() ?: "general",
                            subjectIcon = "📝",
                            createdAt = "Today",
                            updatedAt = "Today"
                        )
                    }
                } catch (_: Throwable) {
                    emptyList()
                }
            }

            // 2. Load Repository all notes
            val repoNotesResult = notesRepository.getNotesList(
                type = "all",
                subjectId = null,
                search = null,
                userId = userId,
                email = userEmail
            )
            val repoNotes = repoNotesResult.getOrNull()?.data?.notes.orEmpty()

            // 3. Merge and deduplicate by title / id
            val combinedNotes = (localRoomNotes + repoNotes).distinctBy { it.id.toString() + "_" + it.title.trim().lowercase() }

            // 4. Calculate live Room StateFlow reactive counts
            val subjectNotesCount = combinedNotes.count {
                it.category.contains("SUBJECT", ignoreCase = true) ||
                it.category.contains("Subject Notes", ignoreCase = true)
            }
            val commonNotesCount = combinedNotes.count {
                it.category.contains("COMMON", ignoreCase = true)
            }
            val savedNotesCount = combinedNotes.count {
                it.isSaved || it.category.isNotBlank()
            }
            val downloadedNotesCount = combinedNotes.count {
                it.isDownloaded || it.isSaved
            }

            val summaryResult = notesRepository.getNotesSummary(userId = userId, email = userEmail)
            val remoteSummary = summaryResult.getOrNull()?.data?.summary ?: NotesCountsDto()
            val subjects = summaryResult.getOrNull()?.data?.subjects.orEmpty()

            // Ensure counters are never wiped out by remote zeros when notes exist
            val finalCounts = NotesCountsDto(
                subjectNotesCount = maxOf(subjectNotesCount, remoteSummary.subjectNotesCount),
                commonNotesCount = maxOf(commonNotesCount, remoteSummary.commonNotesCount),
                savedNotesCount = maxOf(savedNotesCount, remoteSummary.savedNotesCount),
                downloadedNotesCount = maxOf(downloadedNotesCount, remoteSummary.downloadedNotesCount)
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    summary = finalCounts,
                    subjects = subjects,
                    allNotes = combinedNotes,
                    filteredNotes = if (it.activeFilter == "ALL") combinedNotes else it.filteredNotes
                )
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
