package com.siamts.synapse.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

/**
 * Exact alarm scheduler for offline daily study reminders.
 * Supports Android 6+ through Android 15+ (Samsung One UI / Galaxy A56 5G).
 */
object StudyReminderScheduler {

    const val ACTION_STUDY_ALARM = "com.siamts.synapse.ACTION_STUDY_ALARM"
    const val EXTRA_HOUR = "EXTRA_STUDY_HOUR"
    const val EXTRA_MINUTE = "EXTRA_STUDY_MINUTE"
    const val REQUEST_CODE_STUDY_ALARM = 5001

    /**
     * Schedules exact daily study reminder at specified [hour] (0-23) and [minute] (0-59).
     * If the time for today has already elapsed, sets it for tomorrow.
     */
    fun scheduleDailyStudyReminder(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // If scheduled time has already passed today, advance to tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, StudyAlarmReceiver::class.java).apply {
            action = ACTION_STUDY_ALARM
            putExtra(EXTRA_HOUR, hour)
            putExtra(EXTRA_MINUTE, minute)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_STUDY_ALARM,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            // Fallback for restricted exact alarm permission
            try {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } catch (_: Throwable) {}
        } catch (_: Throwable) {}
    }

    /**
     * Cancels scheduled daily study reminder alarm.
     */
    fun cancelDailyStudyReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, StudyAlarmReceiver::class.java).apply {
            action = ACTION_STUDY_ALARM
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_STUDY_ALARM,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}

