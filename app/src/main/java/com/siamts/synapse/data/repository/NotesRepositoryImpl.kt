package com.siamts.synapse.data.repository

import com.siamts.synapse.data.model.CreateNoteRequest
import com.siamts.synapse.data.model.GenericNoteActionResponse
import com.siamts.synapse.data.model.NoteItemDto
import com.siamts.synapse.data.model.NotesCountsDto
import com.siamts.synapse.data.model.NotesListDataDto
import com.siamts.synapse.data.model.NotesListResponse
import com.siamts.synapse.data.model.NotesSubjectItemDto
import com.siamts.synapse.data.model.NotesSummaryDataDto
import com.siamts.synapse.data.model.NotesSummaryResponse
import com.siamts.synapse.data.remote.NotesApi
import com.siamts.synapse.data.remote.RetrofitClient
import java.util.Locale

class NotesRepositoryImpl(
    private val api: NotesApi = RetrofitClient.notesApi,
    private val catalogRepo: SubjectCatalogRepository? = try {
        SubjectCatalogRepository.getInstance()
    } catch (_: Throwable) {
        null
    }
) : NotesRepository {

    // In-memory offline cache of study notes seeded from local curriculum bank
    companion object {
        private val localNotesCache: MutableList<NoteItemDto> = mutableListOf()
        private var isSeeded = false

        @Synchronized
        fun ensureSeeded(catalogRepo: SubjectCatalogRepository?) {
            if (!isSeeded) {
                val initialNotes = catalogRepo?.getAllStudyNotes().orEmpty()
                localNotesCache.addAll(initialNotes)
                isSeeded = true
            }
        }
    }

    init {
        ensureSeeded(catalogRepo)
    }

    override suspend fun getNotesSummary(
        userId: Int?,
        email: String?
    ): Result<NotesSummaryResponse> {
        // Try remote API first
        try {
            val response = api.getNotesSummary(userId, email)
            if (response.isSuccessful && response.body() != null) {
                return Result.success(response.body()!!)
            }
        } catch (_: Exception) {
            // Proceed to local offline fallback
        }

        // Local offline summary calculation
        ensureSeeded(catalogRepo)
        val allNotes = synchronized(localNotesCache) { localNotesCache.toList() }

        val subjectNotesCount = allNotes.count { it.category.equals("SUBJECT", ignoreCase = true) }
        val commonNotesCount = allNotes.count { it.category.equals("COMMON", ignoreCase = true) }
        val savedNotesCount = allNotes.count { it.isSaved }
        val downloadedNotesCount = allNotes.count { it.isDownloaded }

        val counts = NotesCountsDto(
            subjectNotesCount = subjectNotesCount,
            commonNotesCount = commonNotesCount,
            savedNotesCount = savedNotesCount,
            downloadedNotesCount = downloadedNotesCount
        )

        val allSubjects = catalogRepo?.getAllSubjects().orEmpty()
        val subjectItems = allSubjects.map { sub ->
            val count = allNotes.count { it.subjectId == sub.subjectId }
            NotesSubjectItemDto(
                id = sub.subjectId,
                name = sub.name,
                slug = sub.slug,
                iconSymbol = sub.iconSymbol,
                colorHex = sub.colorHex,
                notesCount = count
            )
        }

        return Result.success(
            NotesSummaryResponse(
                status = "success",
                data = NotesSummaryDataDto(
                    summary = counts,
                    subjects = subjectItems
                )
            )
        )
    }

    override suspend fun getNotesList(
        type: String?,
        subjectId: Int?,
        search: String?,
        userId: Int?,
        email: String?
    ): Result<NotesListResponse> {
        // Try remote API first
        try {
            val response = api.getNotesList(type, subjectId, search, userId, email)
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!.data
                if (data != null && data.notes.isNotEmpty()) {
                    return Result.success(response.body()!!)
                }
            }
        } catch (_: Exception) {
            // Proceed to local offline fallback
        }

        // Local offline notes filtering
        ensureSeeded(catalogRepo)
        var filtered = synchronized(localNotesCache) { localNotesCache.toList() }

        val filterType = type?.lowercase(Locale.ROOT) ?: "all"
        filtered = when (filterType) {
            "saved" -> filtered.filter { it.isSaved }
            "downloaded" -> filtered.filter { it.isDownloaded }
            "common" -> filtered.filter { it.category.equals("COMMON", ignoreCase = true) }
            "subject" -> filtered.filter { it.category.equals("SUBJECT", ignoreCase = true) }
            else -> filtered
        }

        if (subjectId != null) {
            filtered = filtered.filter { it.subjectId == subjectId }
        }

        if (!search.isNullOrBlank()) {
            val q = search.trim().lowercase(Locale.ROOT)
            filtered = filtered.filter {
                it.title.lowercase(Locale.ROOT).contains(q) ||
                it.content.lowercase(Locale.ROOT).contains(q) ||
                it.subjectName.lowercase(Locale.ROOT).contains(q) ||
                (it.chapterTitle?.lowercase(Locale.ROOT)?.contains(q) == true)
            }
        }

        return Result.success(
            NotesListResponse(
                status = "success",
                data = NotesListDataDto(
                    type = filterType,
                    total = filtered.size,
                    notes = filtered
                )
            )
        )
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
        val targetSubject = subjectId?.let { catalogRepo?.getSubjectByNumericId(it) }
        val newId = System.currentTimeMillis().toInt() and 0x7FFFFFFF

        val newNote = NoteItemDto(
            id = newId,
            userId = userId ?: 1,
            subjectId = subjectId,
            chapterId = chapterId,
            title = title,
            content = content,
            category = category ?: "COMMON",
            isSaved = true,
            isDownloaded = true,
            subjectName = targetSubject?.name ?: "General Notes",
            subjectSlug = targetSubject?.slug ?: "general",
            subjectIcon = targetSubject?.iconSymbol ?: "📝",
            subjectColor = targetSubject?.colorHex ?: "#8B5CF6",
            chapterTitle = chapterId?.let { catalogRepo?.getChapterById(it)?.title },
            createdAt = "Today",
            updatedAt = "Today"
        )

        synchronized(localNotesCache) {
            localNotesCache.add(0, newNote)
        }

        try {
            val request = CreateNoteRequest(
                title = title,
                content = content,
                subjectId = subjectId,
                chapterId = chapterId,
                category = category
            )
            api.createNote(userId = userId, email = email, request = request)
        } catch (_: Exception) {
            // Ignored when offline
        }

        return Result.success(
            GenericNoteActionResponse(
                status = "success",
                message = "Note created successfully on-device"
            )
        )
    }

    override suspend fun updateNote(
        noteId: Int,
        title: String,
        content: String,
        userId: Int?,
        email: String?
    ): Result<GenericNoteActionResponse> {
        synchronized(localNotesCache) {
            val idx = localNotesCache.indexOfFirst { it.id == noteId }
            if (idx >= 0) {
                val current = localNotesCache[idx]
                localNotesCache[idx] = current.copy(
                    title = title,
                    content = content,
                    updatedAt = "Today"
                )
            }
        }

        try {
            val request = com.siamts.synapse.data.model.UpdateNoteRequest(
                noteId = noteId,
                title = title,
                content = content
            )
            api.updateNote(userId = userId, email = email, request = request)
        } catch (_: Exception) {
            // Ignored when offline
        }

        return Result.success(
            GenericNoteActionResponse(
                status = "success",
                message = "Note updated successfully"
            )
        )
    }

    override suspend fun deleteNote(
        noteId: Int,
        userId: Int?,
        email: String?
    ): Result<GenericNoteActionResponse> {
        synchronized(localNotesCache) {
            localNotesCache.removeAll { it.id == noteId }
        }

        try {
            api.deleteNote(noteId, userId, email)
        } catch (_: Exception) {
            // Ignored when offline
        }

        return Result.success(
            GenericNoteActionResponse(
                status = "success",
                message = "Note deleted successfully"
            )
        )
    }

    override suspend fun toggleSave(
        noteId: Int,
        userId: Int?,
        email: String?
    ): Result<GenericNoteActionResponse> {
        var isNowSaved = true
        synchronized(localNotesCache) {
            val idx = localNotesCache.indexOfFirst { it.id == noteId }
            if (idx >= 0) {
                val current = localNotesCache[idx]
                isNowSaved = !current.isSaved
                localNotesCache[idx] = current.copy(isSaved = isNowSaved)
            }
        }

        try {
            api.toggleSave(noteId, userId, email)
        } catch (_: Exception) {
            // Ignored when offline
        }

        return Result.success(
            GenericNoteActionResponse(
                status = "success",
                message = if (isNowSaved) "Note saved to favorites" else "Note removed from saved"
            )
        )
    }

    override suspend fun toggleDownload(
        noteId: Int,
        userId: Int?,
        email: String?
    ): Result<GenericNoteActionResponse> {
        var isNowDownloaded = true
        synchronized(localNotesCache) {
            val idx = localNotesCache.indexOfFirst { it.id == noteId }
            if (idx >= 0) {
                val current = localNotesCache[idx]
                isNowDownloaded = !current.isDownloaded
                localNotesCache[idx] = current.copy(isDownloaded = isNowDownloaded)
            }
        }

        try {
            api.toggleDownload(noteId, userId, email)
        } catch (_: Exception) {
            // Ignored when offline
        }

        return Result.success(
            GenericNoteActionResponse(
                status = "success",
                message = if (isNowDownloaded) "Note saved for offline reading" else "Note removed from downloads"
            )
        )
    }
}
