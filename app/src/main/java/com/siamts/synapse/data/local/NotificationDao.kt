package com.siamts.synapse.data.local

import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Entity representing an in-app smart notification stored locally for the Notification Center.
 */
data class NotificationEntity(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val body: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "general", // "daily_challenge", "streak_saver", "mistake_vault", "today_plan", "milestone", "general"
    val isRead: Boolean = false,
    val actionRoute: String? = null, // "quiz_hub", "learn", "mistake_vault", "home", "milestone"
    val iconEmoji: String = "🔔"
)

/**
 * Data Access Object (DAO) for managing local notifications in SYNAPSE AI.
 */
interface NotificationDao {
    suspend fun insertNotification(notification: NotificationEntity): Long
    fun getAllNotificationsFlow(): Flow<List<NotificationEntity>>
    suspend fun getAllNotifications(): List<NotificationEntity>
    suspend fun markAsRead(id: String)
    suspend fun markAllAsRead()
    suspend fun getUnreadCount(): Int
    fun getUnreadCountFlow(): Flow<Int>
    suspend fun deleteNotification(id: String)
    suspend fun clearAll()
}
