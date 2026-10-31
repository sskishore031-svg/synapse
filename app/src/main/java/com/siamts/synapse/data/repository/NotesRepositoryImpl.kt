package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.CreateNoteRequest
import com.siamts.synapse.data.model.GenericNoteActionResponse
import com.siamts.synapse.data.model.NotesListResponse
import com.siamts.synapse.data.model.NotesSummaryResponse
import com.siamts.synapse.data.remote.NotesApi
import com.siamts.synapse.data.remote.RetrofitClient

class NotesRepositoryImpl(
    private val api: NotesApi = RetrofitClient.notesApi
) : NotesRepository {

    override suspend fun getNotesSummary(
        userId: Int?,
        email: String?
    ): Result<NotesSummaryResponse> {
        return try {
            val response = api.getNotesSummary(userId, email)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch notes summary: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNotesList(
        type: String?,
        subjectId: Int?,
        search: String?,
        userId: Int?,
        email: String?
    ): Result<NotesListResponse> {
        return try {
            val response = api.getNotesList(type, subjectId, search, userId, email)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch notes: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createNote(
        title: String,
        content: String,
        subjectId: Int?,
        chapterId: Int?,
        category: String?,
        userId: Int?,
        email: String?
    ): Result<GenericNoteActionResponse> {
        return try {
            val request = CreateNoteRequest(
                title = title,
                content = content,
                subjectId = subjectId,
                chapterId = chapterId,
                category = category
            )
            val response = api.createNote(userId = userId, email = email, request = request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create note: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateNote(
        noteId: Int,
        title: String,
        content: String,
        userId: Int?,
        email: String?
    ): Result<GenericNoteActionResponse> {
        return try {
            val request = com.siamts.synapse.data.model.UpdateNoteRequest(
                noteId = noteId,
                title = title,
                content = content
            )
            val response = api.updateNote(userId = userId, email = email, request = request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update note: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteNote(
        noteId: Int,
        userId: Int?,
        email: String?
    ): Result<GenericNoteActionResponse> {
        return try {
            val response = api.deleteNote(noteId, userId, email)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to delete note: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleSave(
        noteId: Int,
        userId: Int?,
        email: String?
    ): Result<GenericNoteActionResponse> {
        return try {
            val response = api.toggleSave(noteId, userId, email)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to toggle save status: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleDownload(
        noteId: Int,
        userId: Int?,
        email: String?
    ): Result<GenericNoteActionResponse> {
        return try {
            val response = api.toggleDownload(noteId, userId, email)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to toggle download status: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

