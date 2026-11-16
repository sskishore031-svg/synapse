package com.siamts.synapse.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.siamts.synapse.MainActivity
import com.siamts.synapse.R
import java.util.concurrent.TimeUnit

/**
 * Production-ready notification helper for SYNAPSE AI.
 * Handles system notification channel creation, heads-up status bar banners,
 * WorkManager daily reminders, and real-time offline milestone triggers.
 */
object SynapseNotificationHelper {

    const val CHANNEL_ID = "synapse_study_channel"
    private const val CHANNEL_NAME = "SYNAPSE AI Study Reminders & Alerts"
    private const val CHANNEL_DESCRIPTION = "Daily offline revision prompts, quiz milestones, and smart study reminders"

    private const val WORK_NAME_DAILY_REMINDER = "synapse_daily_study_reminder"

    const val ID_TEST_NOTIFICATION = 1001
    const val ID_STUDY_REMINDER = 2001
    const val ID_QUIZ_MILESTONE = 3001
    const val ID_NOTE_SAVED = 4001

    /**
     * Initializes the high-importance Notification Channel on Android 8.0+ (API 26+).
     * Must be called at app startup (SynapseApplication / MainActivity).
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return

            // Avoid recreating if already exists with same properties
            val existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (existingChannel == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = CHANNEL_DESCRIPTION
                    enableLights(true)
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 250, 150, 250)
                    lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                    setShowBadge(true)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    /**
     * Reusable method to post a system status bar notification with heads-up banner.
     * Verifies runtime permissions on Android 13+ (Tiramisu API 33+).
     */
    fun showNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = ID_TEST_NOTIFICATION,
        payload: String? = null
    ): Boolean {
        // 1. Check runtime permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }

        // 2. Ensure channel is created
        createNotificationChannel(context)

        // 3. Prepare pending intent to open MainActivity
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (!payload.isNullOrBlank()) {
                putExtra("NOTIFICATION_PAYLOAD", payload)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 4. Large icon bitmap for crisp brand appearance
        val largeIconBitmap = try {
            BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
        } catch (_: Throwable) {
            null
        }

        // 5. Build rich notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .apply {
                if (largeIconBitmap != null) {
                    setLargeIcon(largeIconBitmap)
                }
            }
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // 6. Post notification
        return try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
            true
        } catch (_: SecurityException) {
            false
        } catch (_: Throwable) {
            false
        }
    }

    /**
     * Instant test notification for live examiner demo.
     */
    fun showTestNotification(context: Context): Boolean {
        return showNotification(
            context = context,
            title = "⚡ SYNAPSE AI • Offline Engine Active",
            message = "Status bar notifications & study alerts are operational! 100% on-device AI is ready for your revision.",
            notificationId = ID_TEST_NOTIFICATION
        )
    }

    /**
     * Daily background study reminder.
     */
    fun showStudyReminderNotification(context: Context): Boolean {
        val reminderMessages = listOf(
            "Time for a quick 5-minute revision! Master today's key concepts in AI, Math, and CS.",
            "Keep your study streak alive! Practice with quick MCQs and chapter summaries.",
            "Your learning roadmap awaits. Complete a chapter today to boost your proficiency!",
            "Offline AI Tutor is ready. Explore new topics or test yourself with a quick quiz."
        )
        val selectedMessage = reminderMessages.random()

        return showNotification(
            context = context,
            title = "🧠 SYNAPSE AI • Daily Study Goal",
            message = selectedMessage,
            notificationId = ID_STUDY_REMINDER
        )
    }

    /**
     * Real-time milestone trigger when user completes a quiz with >= 80% score.
     */
    fun showQuizMilestoneNotification(
        context: Context,
        subjectTitle: String,
        chapterTitle: String,
        score: Int,
        total: Int
    ): Boolean {
        return showNotification(
            context = context,
            title = "🏆 Chapter Mastered!",
            message = "Outstanding work! You scored $score/$total in $chapterTitle ($subjectTitle). Keep up your learning streak!",
            notificationId = ID_QUIZ_MILESTONE
        )
    }

    /**
     * Real-time notification when user saves AI smart study notes.
     */
    fun showNoteSavedNotification(context: Context, noteTitle: String): Boolean {
        return showNotification(
            context = context,
            title = "📝 Note Saved to Synapse",
            message = "\"$noteTitle\" is ready for offline revision.",
            notificationId = ID_NOTE_SAVED
        )
    }

    /**
     * Schedules 24-hour periodic background study reminder via WorkManager.
     */
    fun scheduleStudyReminders(context: Context) {
        val reminderWork = PeriodicWorkRequestBuilder<StudyReminderWorker>(24, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME_DAILY_REMINDER,
            ExistingPeriodicWorkPolicy.UPDATE,
            reminderWork
        )
    }

    /**
     * Cancels scheduled background study reminder.
     */
    fun cancelStudyReminders(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_DAILY_REMINDER)
    }
}

