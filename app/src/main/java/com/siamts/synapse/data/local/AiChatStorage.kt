package com.siamts.synapse.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.siamts.synapse.SynapseApplication
import com.siamts.synapse.ui.tutor.ChatMessage

/**
 * Local storage manager for persisting ongoing AI conversation sessions and message history.
 * Delegates to [ChatDao] for SQLite persistent session history, while maintaining
 * fast in-memory and SharedPreferences fallbacks.
 */
class AiChatStorage(private val context: Context) {

    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val chatDao: ChatDao get() = SynapseDatabase.getChatDao(context)

    fun saveMessages(userId: Int, messages: List<ChatMessage>) {
        try {
            val json = gson.toJson(messages)
            prefs.edit().putString(KEY_MESSAGES_PREFIX + userId, json).apply()
        } catch (_: Exception) {
            // Ignore write failures gracefully
        }
    }

    fun loadMessages(userId: Int): List<ChatMessage> {
        val json = prefs.getString(KEY_MESSAGES_PREFIX + userId, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<ChatMessage>>() {}.type
            gson.fromJson<List<ChatMessage>>(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun clearMessages(userId: Int) {
        prefs.edit().remove(KEY_MESSAGES_PREFIX + userId).apply()
    }

    suspend fun saveSession(session: ChatSessionEntity, messages: List<ChatMessage>) {
        try {
            chatDao.insertOrUpdateSession(session)
            val entities = messages.map { ChatMessageEntity.fromChatMessage(session.sessionId, it) }
            chatDao.saveMessages(session.sessionId, entities)
            saveMessages(session.userId, messages)
        } catch (_: Exception) {}
    }

    suspend fun loadSessions(userId: Int): List<ChatSessionEntity> {
        return try {
            chatDao.getAllSessions(userId)
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun loadSessionMessages(sessionId: String): List<ChatMessage> {
        return try {
            chatDao.getMessagesForSession(sessionId).map { it.toChatMessage() }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun deleteSession(sessionId: String) {
        try {
            chatDao.deleteSession(sessionId)
        } catch (_: Exception) {}
    }

    companion object {
        private const val PREF_NAME = "synapse_ai_chat_pref"
        private const val KEY_MESSAGES_PREFIX = "chat_history_user_"

        @Volatile
        private var instance: AiChatStorage? = null

        fun getInstance(context: Context? = null): AiChatStorage {
            return instance ?: synchronized(this) {
                val ctx = context?.applicationContext ?: try {
                    SynapseApplication.getAppContext()
                } catch (_: Exception) {
                    null
                }
                if (ctx != null) {
                    instance ?: AiChatStorage(ctx).also { instance = it }
                } else {
                    instance ?: AiChatStorage(context ?: throw IllegalStateException("Context required")).also { instance = it }
                }
            }
        }
    }
}
