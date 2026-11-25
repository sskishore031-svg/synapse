package com.siamts.synapse.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

/**
 * Intelligent AlarmManager scheduler for SYNAPSE 5 Smart Notifications.
 * Handles exact and idle-friendly alarms across Android 8 through Android 15+.
 */
object SmartNotificationScheduler {

    const val ACTION_SMART_ALARM = "com.siamts.synapse.ACTION_SMART_ALARM"
    const val EXTRA_NOTIFICATION_TYPE = "EXTRA_NOTIFICATION_TYPE"

    const val TYPE_DAILY_CHALLENGE = "daily_challenge"
    const val TYPE_TODAY_PLAN = "today_plan"
    const val TYPE_STREAK_SAVER = "streak_saver"
    const val TYPE_MISTAKE_VAULT = "mistake_vault"

    private const val REQUEST_CODE_DAILY_CHALLENGE = 6001
    private const val REQUEST_CODE_TODAY_PLAN = 6002
    private const val REQUEST_CODE_STREAK_SAVER = 6003
    private const val REQUEST_CODE_MISTAKE_VAULT = 6004

    /**
     * Schedules all 4 recurring smart notification alarms.
     */
    fun scheduleAll(context: Context) {
        scheduleDailyChallenge(context)
        scheduleTodayPlanReminder(context)
        scheduleStreakSaver(context)
        scheduleMistakeVaultRecall(context)
    }

    /**
     * Cancels all scheduled smart notification alarms.
     */
    fun cancelAll(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val types = listOf(
            REQUEST_CODE_DAILY_CHALLENGE to TYPE_DAILY_CHALLENGE,
            REQUEST_CODE_TODAY_PLAN to TYPE_TODAY_PLAN,
            REQUEST_CODE_STREAK_SAVER to TYPE_STREAK_SAVER,
            REQUEST_CODE_MISTAKE_VAULT to TYPE_MISTAKE_VAULT
        )
        for ((code, type) in types) {
            val intent = Intent(context, SmartAlarmReceiver::class.java).apply {
                action = ACTION_SMART_ALARM
                putExtra(EXTRA_NOTIFICATION_TYPE, type)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                code,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }
    }

    /**
     * 1. Daily Challenge (10:00 AM daily)
     */
    fun scheduleDailyChallenge(context: Context) {
        scheduleDailyAlarm(context, hour = 10, minute = 0, requestCode = REQUEST_CODE_DAILY_CHALLENGE, type = TYPE_DAILY_CHALLENGE)
    }

    /**
     * 2. Today's Plan Reminder (4:00 PM / 16:00 daily)
     */
    fun scheduleTodayPlanReminder(context: Context) {
        scheduleDailyAlarm(context, hour = 16, minute = 0, requestCode = REQUEST_CODE_TODAY_PLAN, type = TYPE_TODAY_PLAN)
    }

    /**
     * 3. Streak Saver (8:00 PM / 20:00 daily)
     */
    fun scheduleStreakSaver(context: Context) {
        scheduleDailyAlarm(context, hour = 20, minute = 0, requestCode = REQUEST_CODE_STREAK_SAVER, type = TYPE_STREAK_SAVER)
    }

    /**
     * 4. Mistake Vault Recall (Every 48 hours / 2 days)
     */
    fun scheduleMistakeVaultRecall(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            add(Calendar.HOUR_OF_DAY, 48)
        }

        val intent = Intent(context, SmartAlarmReceiver::class.java).apply {
            action = ACTION_SMART_ALARM
            putExtra(EXTRA_NOTIFICATION_TYPE, TYPE_MISTAKE_VAULT)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_MISTAKE_VAULT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setAlarm(alarmManager, calendar.timeInMillis, pendingIntent)
    }

    private fun scheduleDailyAlarm(context: Context, hour: Int, minute: Int, requestCode: Int, type: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, SmartAlarmReceiver::class.java).apply {
            action = ACTION_SMART_ALARM
            putExtra(EXTRA_NOTIFICATION_TYPE, type)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setAlarm(alarmManager, calendar.timeInMillis, pendingIntent)
    }

    private fun setAlarm(alarmManager: AlarmManager, triggerAtMillis: Long, pendingIntent: PendingIntent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            try {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } catch (_: Throwable) {}
        } catch (_: Throwable) {}
    }
}
