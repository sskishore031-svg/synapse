package com.siamts.synapse.data.local

/**
 * Entity representing a persistent study note stored in the local SQLite database.
 */
data class NoteEntity(
    val id: Long = 0,
    val userId: Int = 1,
    val subjectId: String? = null,
    val chapterId: Int? = null,
    val title: String,
    val content: String,
    val category: String = "COMMON",
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Data Access Object for local persistent study notes.
 */
interface NotesDao {
    suspend fun insertNote(note: NoteEntity): Long
    suspend fun getAllNotes(userId: Int): List<NoteEntity>
    suspend fun getNotesBySubject(userId: Int, subjectId: String): List<NoteEntity>
    suspend fun getNoteById(noteId: Long): NoteEntity?
    suspend fun deleteNote(noteId: Long): Int
}

