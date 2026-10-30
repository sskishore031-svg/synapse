package com.siamts.synapse.data.model

import com.google.gson.annotations.SerializedName

data class NotesSummaryResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: NotesSummaryDataDto? = null
)

data class NotesSummaryDataDto(
    @SerializedName("summary")
    val summary: NotesCountsDto? = null,
    @SerializedName("subjects")
    val subjects: List<NotesSubjectItemDto> = emptyList()
)

data class NotesCountsDto(
    @SerializedName("subject_notes_count")
    val subjectNotesCount: Int = 0,
    @SerializedName("common_notes_count")
    val commonNotesCount: Int = 0,
    @SerializedName("saved_notes_count")
    val savedNotesCount: Int = 0,
    @SerializedName("downloaded_notes_count")
    val downloadedNotesCount: Int = 0
)

data class NotesSubjectItemDto(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("slug")
    val slug: String = "",
    @SerializedName("icon_symbol")
    val iconSymbol: String = "📚",
    @SerializedName("color_hex")
    val colorHex: String = "#3B82F6",
    @SerializedName("notes_count")
    val notesCount: Int = 0
)

data class NotesListResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: NotesListDataDto? = null
)

data class NotesListDataDto(
    @SerializedName("type")
    val type: String = "all",
    @SerializedName("total")
    val total: Int = 0,
    @SerializedName("notes")
    val notes: List<NoteItemDto> = emptyList()
)

data class NoteItemDto(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("user_id")
    val userId: Int = 0,
    @SerializedName("subject_id")
    val subjectId: Int? = null,
    @SerializedName("chapter_id")
    val chapterId: Int? = null,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("content")
    val content: String = "",
    @SerializedName("category")
    val category: String = "COMMON",
    @SerializedName("is_saved")
    val isSaved: Boolean = true,
    @SerializedName("is_downloaded")
    val isDownloaded: Boolean = false,
    @SerializedName("subject_name")
    val subjectName: String = "General / Common",
    @SerializedName("subject_slug")
    val subjectSlug: String? = null,
    @SerializedName("subject_icon")
    val subjectIcon: String = "📝",
    @SerializedName("subject_color")
    val subjectColor: String = "#8B5CF6",
    @SerializedName("chapter_title")
    val chapterTitle: String? = null,
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = ""
)

data class CreateNoteRequest(
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("subject_id")
    val subjectId: Int? = null,
    @SerializedName("chapter_id")
    val chapterId: Int? = null
)

data class UpdateNoteRequest(
    @SerializedName("note_id")
    val noteId: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String
)

data class GenericNoteActionResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String? = null
)

