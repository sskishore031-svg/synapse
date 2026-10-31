package com.siamts.synapse.ui.notes

import com.siamts.synapse.data.model.NoteItemDto
import com.siamts.synapse.data.model.NotesCountsDto
import com.siamts.synapse.data.model.NotesSubjectItemDto

/**
 * UI State for Notes Organizer Home Screen with real-time category filtering.
 */
data class NotesHomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val summary: NotesCountsDto = NotesCountsDto(),
    val subjects: List<NotesSubjectItemDto> = emptyList(),
    val activeFilter: String = "ALL", // "ALL", "SUBJECT", "COMMON", "SAVED", "DOWNLOADED"
    val filteredNotes: List<NoteItemDto> = emptyList(),
    val isFilteredLoading: Boolean = false,
    val searchQuery: String = ""
)
