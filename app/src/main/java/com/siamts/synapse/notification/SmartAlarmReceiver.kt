package com.siamts.synapse.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.local.SynapseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * BroadcastReceiver triggered by AlarmManager for the 4 scheduled smart notifications:
 * 1. Daily Challenge (10:00 AM)
 * 2. Today's Plan Reminder (4:00 PM - conditional: incomplete tasks exist)
 * 3. Streak Saver (8:00 PM - conditional: 0 completed lessons/quizzes today)
 * 4. Mistake Vault Recall (Every 48 hours - conditional: unmastered mistakes exist)
 */
class SmartAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(SmartNotificationScheduler.EXTRA_NOTIFICATION_TYPE) ?: return

        when (type) {
            SmartNotificationScheduler.TYPE_DAILY_CHALLENGE -> {
                // 1. Daily Challenge at 10:00 AM: fires unconditionally
                SynapseNotificationHelper.showDailyChallengeNotification(context)
                SmartNotificationScheduler.scheduleDailyChallenge(context)
            }

            SmartNotificationScheduler.TYPE_TODAY_PLAN -> {
                // 2. Today's Plan at 4:00 PM: only fires if incomplete tasks exist
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val taskDao = SynapseDatabase.getLearningTaskDao(context)
                        val tasks = taskDao.getAllTasks()
                        val hasIncomplete = tasks.isNotEmpty() && tasks.any { !it.isCompleted }
                        if (hasIncomplete) {
                            SynapseNotificationHelper.showTodayPlanReminderNotification(context)
                        }
                    } catch (_: Throwable) {
                        SynapseNotificationHelper.showTodayPlanReminderNotification(context)
                    }
                }
                SmartNotificationScheduler.scheduleTodayPlanReminder(context)
            }

            SmartNotificationScheduler.TYPE_STREAK_SAVER -> {
                // 3. Streak Saver at 8:00 PM: only fires if 0 lessons/quizzes completed today
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val startOfToday = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.timeInMillis

                        val quizDao = SynapseDatabase.getInstance(context)
                        val quizDoneToday = quizDao.getAllRecords().any { it.completedAt >= startOfToday }

                        val sessionManager = SessionManager.getInstanceOrNull() ?: SessionManager.init(context)
                        val user = sessionManager.getSessionUser()
                        val userId = user?.id?.toString() ?: "1"

                        val progressDao = SynapseDatabase.getLessonProgressDao(context)
                        val progressList = progressDao.getProgressForUser(userId)
                        val lessonDoneToday = progressList.any { it.isCompleted && it.completedAt >= startOfToday }

                        if (!quizDoneToday && !lessonDoneToday) {
                            SynapseNotificationHelper.showStreakSaverNotification(context)
                        }
                    } catch (_: Throwable) {
                        SynapseNotificationHelper.showStreakSaverNotification(context)
                    }
                }
                SmartNotificationScheduler.scheduleStreakSaver(context)
            }

            SmartNotificationScheduler.TYPE_MISTAKE_VAULT -> {
                // 4. Mistake Vault Recall: only fires if unmastered wrong questions exist
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val mistakeCount = SynapseDatabase.getInstance(context).getMistakeCount()
                        if (mistakeCount > 0) {
                            SynapseNotificationHelper.showMistakeVaultRecallNotification(context)
                        }
                    } catch (_: Throwable) {
                        SynapseNotificationHelper.showMistakeVaultRecallNotification(context)
                    }
                }
                SmartNotificationScheduler.scheduleMistakeVaultRecall(context)
            }
        }
    }
}
