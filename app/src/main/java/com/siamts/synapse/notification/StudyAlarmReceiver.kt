package com.siamts.synapse.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * BroadcastReceiver triggered by AlarmManager at user's exact preferred study time.
 * Fires a heads-up status bar notification and automatically reschedules for tomorrow.
 */
class StudyAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // 1. Fire high-priority status bar notification
        SynapseNotificationHelper.showNotification(
            context = context,
            title = "🧠 SYNAPSE AI • It's Study Time!",
            message = "Your scheduled study session has started. Open SYNAPSE AI to review chapters, take a quick quiz, or ask your AI Tutor!",
            notificationId = SynapseNotificationHelper.ID_STUDY_REMINDER
        )

        // 2. Automatically reschedule next day's alarm at the same hour & minute
        val hour = intent.getIntExtra(StudyReminderScheduler.EXTRA_HOUR, 19)
        val minute = intent.getIntExtra(StudyReminderScheduler.EXTRA_MINUTE, 0)
        StudyReminderScheduler.scheduleDailyStudyReminder(context, hour, minute)
    }
}

