package com.siamts.synapse.ui.notes

import com.siamts.synapse.data.model.NoteItemDto

data class NotesListUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val type: String = "all",
    val title: String = "Notes",
    val subjectId: Int? = null,
    val notes: List<NoteItemDto> = emptyList()
)

