package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.GenericNoteActionResponse
import com.siamts.synapse.data.model.NotesListResponse
import com.siamts.synapse.data.model.NotesSummaryResponse

interface NotesRepository {
    suspend fun getNotesSummary(
        userId: Int? = null,
        email: String? = null
    ): Result<NotesSummaryResponse>

    suspend fun getNotesList(
        type: String? = null,
        subjectId: Int? = null,
        userId: Int? = null,
        email: String? = null
    ): Result<NotesListResponse>

    suspend fun createNote(
        title: String,
        content: String,
        subjectId: Int? = null,
        chapterId: Int? = null,
        userId: Int? = null,
        email: String? = null
    ): Result<GenericNoteActionResponse>

    suspend fun deleteNote(
        noteId: Int,
        userId: Int? = null,
        email: String? = null
    ): Result<GenericNoteActionResponse>

    suspend fun toggleSave(
        noteId: Int,
        userId: Int? = null,
        email: String? = null
    ): Result<GenericNoteActionResponse>

    suspend fun toggleDownload(
        noteId: Int,
        userId: Int? = null,
        email: String? = null
    ): Result<GenericNoteActionResponse>
}

