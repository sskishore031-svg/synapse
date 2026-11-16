package com.siamts.synapse.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.siamts.synapse.data.local.SessionManager

/**
 * WorkManager CoroutineWorker executing 24-hour background study reminders.
 */
class StudyReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val sessionManager = SessionManager.getInstanceOrNull()
        val isRemindersEnabled = sessionManager?.isStudyRemindersEnabled() ?: true

        if (isRemindersEnabled) {
            SynapseNotificationHelper.showStudyReminderNotification(applicationContext)
        }

        return Result.success()
    }
}

