package com.siamts.synapse.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.siamts.synapse.data.local.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Handles incoming FCM cloud messages for both foreground and background states.
 * Formats every notification with Sunset Orange (#FF5500) branding and saves it
 * into Room DB (NotificationEntity) for display in the Notification Center sheet.
 */
class SynapseFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Extract title, body, and custom payloads from FCM message or data map
        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "SYNAPSE AI 🚀"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: remoteMessage.data["message"]
            ?: "New learning update available in Synapse AI."

        val type = remoteMessage.data["type"] ?: "fcm_alert"
        val actionRoute = remoteMessage.data["action"]
            ?: remoteMessage.data["actionRoute"]
            ?: remoteMessage.data["payload"]
            ?: "quiz_hub"

        val emoji = remoteMessage.data["iconEmoji"]
            ?: remoteMessage.data["emoji"]
            ?: when (type) {
                "daily_challenge" -> "🚀"
                "streak_saver" -> "🔥"
                "mistake_vault" -> "🧩"
                "today_plan" -> "📣"
                "milestone" -> "⭐"
                else -> "🔔"
            }

        val notificationId = remoteMessage.data["notificationId"]?.toIntOrNull()
            ?: (System.currentTimeMillis() % 100000).toInt()

        // Display status bar notification with Sunset Orange #FF5500 accent & persist in Room DB
        SynapseNotificationHelper.showNotification(
            context = applicationContext,
            title = title,
            message = body,
            notificationId = notificationId,
            payload = actionRoute,
            type = type,
            iconEmoji = emoji,
            saveToHistory = true
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("SynapseFCM", "New Firebase Cloud Messaging device token: $token")

        // Auto-subscribe to default topic if notifications are enabled
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sessionManager = SessionManager.getInstanceOrNull() ?: SessionManager.init(applicationContext)
                val isEnabled = sessionManager.isNotificationsEnabled()
                if (isEnabled) {
                    com.google.firebase.messaging.FirebaseMessaging.getInstance()
                        .subscribeToTopic("synapse_daily_alerts")
                }
            } catch (_: Throwable) {}
        }
    }
}
