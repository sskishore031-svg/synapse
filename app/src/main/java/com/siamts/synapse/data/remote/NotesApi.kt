package com.siamts.synapse.data.remote

import com.siamts.synapse.data.model.CreateNoteRequest
import com.siamts.synapse.data.model.GenericNoteActionResponse
import com.siamts.synapse.data.model.NotesListResponse
import com.siamts.synapse.data.model.NotesSummaryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NotesApi {

    @GET("auth/notes.php?action=home_summary")
    suspend fun getNotesSummary(
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<NotesSummaryResponse>

    @GET("auth/notes.php?action=list")
    suspend fun getNotesList(
        @Query("type") type: String? = null, // "all", "subject", "common", "saved", "downloaded"
        @Query("subject_id") subjectId: Int? = null,
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<NotesListResponse>

    @POST("auth/notes.php?action=create")
    suspend fun createNote(
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null,
        @Body request: CreateNoteRequest
    ): Response<GenericNoteActionResponse>

    @FormUrlEncoded
    @POST("auth/notes.php?action=delete")
    suspend fun deleteNote(
        @Field("note_id") noteId: Int,
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<GenericNoteActionResponse>

    @FormUrlEncoded
    @POST("auth/notes.php?action=toggle_save")
    suspend fun toggleSave(
        @Field("note_id") noteId: Int,
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<GenericNoteActionResponse>

    @FormUrlEncoded
    @POST("auth/notes.php?action=toggle_download")
    suspend fun toggleDownload(
        @Field("note_id") noteId: Int,
        @Query("user_id") userId: Int? = null,
        @Query("email") email: String? = null
    ): Response<GenericNoteActionResponse>
}

