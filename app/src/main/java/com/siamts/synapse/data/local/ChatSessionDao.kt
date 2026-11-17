package com.siamts.synapse.data.local

import com.siamts.synapse.ui.tutor.ChatMessage
import java.util.UUID

/**
 * Room / SQLite entity representing a persistent multi-turn chat session.
 */
data class ChatSessionEntity(
    val sessionId: String = UUID.randomUUID().toString(),
    val userId: Int = 1,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Entity representing an individual message linked to a specific chat session.
 */
data class ChatMessageEntity(
    val id: String = UUID.randomUUID().toString(),
    val sessionId: String,
    val senderName: String = "SYNAPSE AI",
    val text: String,
    val timeAgo: String = "Just now",
    val isUser: Boolean,
    val paragraph2: String? = null,
    val matchedSubject: String? = null,
    val matchedChapter: String? = null,
    val matchedTopic: String? = null,
    val isConceptAnswer: Boolean = false,
    val isSavedNoteCard: Boolean = false,
    val savedNoteTitle: String? = null,
    val actionCommand: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toChatMessage(): ChatMessage {
        return ChatMessage(
            id = id,
            senderName = senderName,
            text = text,
            timeAgo = timeAgo,
            isUser = isUser,
            paragraph2 = paragraph2,
            matchedSubject = matchedSubject,
            matchedChapter = matchedChapter,
            matchedTopic = matchedTopic,
            isConceptAnswer = isConceptAnswer,
            isSavedNoteCard = isSavedNoteCard,
            savedNoteTitle = savedNoteTitle,
            actionCommand = actionCommand,
            timestamp = timestamp
        )
    }

    companion object {
        fun fromChatMessage(sessionId: String, msg: ChatMessage): ChatMessageEntity {
            return ChatMessageEntity(
                id = msg.id,
                sessionId = sessionId,
                senderName = msg.senderName,
                text = msg.text,
                timeAgo = msg.timeAgo,
                isUser = msg.isUser,
                paragraph2 = msg.paragraph2,
                matchedSubject = msg.matchedSubject,
                matchedChapter = msg.matchedChapter,
                matchedTopic = msg.matchedTopic,
                isConceptAnswer = msg.isConceptAnswer,
                isSavedNoteCard = msg.isSavedNoteCard,
                savedNoteTitle = msg.savedNoteTitle,
                actionCommand = msg.actionCommand,
                timestamp = msg.timestamp
            )
        }
    }
}

/**
 * Data Access Object for persisting and managing multi-turn chat sessions and message histories.
 */
interface ChatDao {
    suspend fun insertOrUpdateSession(session: ChatSessionEntity)
    suspend fun getAllSessions(userId: Int): List<ChatSessionEntity>
    suspend fun getSessionById(sessionId: String): ChatSessionEntity?
    suspend fun updateSessionTitle(sessionId: String, title: String, updatedAt: Long = System.currentTimeMillis())
    suspend fun deleteSession(sessionId: String)
    suspend fun saveMessages(sessionId: String, messages: List<ChatMessageEntity>)
    suspend fun getMessagesForSession(sessionId: String): List<ChatMessageEntity>
    suspend fun deleteMessagesForSession(sessionId: String)
}

