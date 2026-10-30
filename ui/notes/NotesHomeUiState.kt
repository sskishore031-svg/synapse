package com.siamts.synapse.ui.notes

import com.siamts.synapse.data.model.NotesCountsDto
import com.siamts.synapse.data.model.NotesSubjectItemDto

data class NotesHomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val summary: NotesCountsDto = NotesCountsDto(),
    val subjects: List<NotesSubjectItemDto> = emptyList()
)
