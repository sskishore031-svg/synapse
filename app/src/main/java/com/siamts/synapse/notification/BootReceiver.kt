package com.siamts.synapse.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.siamts.synapse.data.repository.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Restores scheduled study reminder alarms upon device reboot or app update.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    SynapseNotificationHelper.createNotificationChannel(context)
                    val prefsRepo = UserPreferencesRepository.getInstance(context)
                    if (prefsRepo.isStudyReminderEnabled()) {
                        val hour = prefsRepo.getStudyReminderHour()
                        val minute = prefsRepo.getStudyReminderMinute()
                        StudyReminderScheduler.scheduleDailyStudyReminder(context, hour, minute)
                        SmartNotificationScheduler.scheduleAll(context)
                    }
                } catch (_: Throwable) {
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}

