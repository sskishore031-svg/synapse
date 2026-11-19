package com.siamts.synapse.ui.profile

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.UserPreferencesRepository
import com.siamts.synapse.notification.StudyReminderScheduler
import com.siamts.synapse.notification.SynapseNotificationHelper
import com.siamts.synapse.ui.theme.AmberGold
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.SunsetOrange
import com.siamts.synapse.ui.theme.SunsetOrangeEnd
import com.siamts.synapse.ui.theme.SurfaceBorder
import com.siamts.synapse.ui.theme.SurfacePanel
import com.siamts.synapse.ui.theme.SurfacePanelElevated
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox
import kotlinx.coroutines.launch

@Composable
fun NotificationsSettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    initialEnabled: Boolean = true,
    onToggleNotifications: (Boolean) -> Unit = {},
) {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sessionManager = remember {
        SessionManager.getInstanceOrNull() ?: SessionManager.init(context)
    }
    val userPreferencesRepo = remember {
        UserPreferencesRepository.getInstance(context)
    }

    val persistedReminders by sessionManager.studyRemindersEnabledFlow.collectAsState(initial = true)
    val persistedNotifications by sessionManager.notificationsEnabledFlow.collectAsState(initial = initialEnabled)
    val formattedStudyTime by userPreferencesRepo.studyReminderFormattedTimeFlow.collectAsState(initial = "07:00 PM")
    val currentStudyHour by userPreferencesRepo.studyReminderHourFlow.collectAsState(initial = 19)
    val currentStudyMinute by userPreferencesRepo.studyReminderMinuteFlow.collectAsState(initial = 0)
    val snackbarHostState = remember { SnackbarHostState() }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            SynapseNotificationHelper.scheduleStudyReminders(context)
            StudyReminderScheduler.scheduleDailyStudyReminder(context, currentStudyHour, currentStudyMinute)
            coroutineScope.launch {
                sessionManager.setStudyRemindersEnabled(true)
                sessionManager.setNotificationsEnabled(true)
                userPreferencesRepo.setStudyReminderEnabled(true)
            }
            val sent = SynapseNotificationHelper.showTestNotification(context)
            if (sent) {
                Toast.makeText(context, "⚡ Notification sent! Check status bar.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Please allow notification permission in Android Settings.", Toast.LENGTH_SHORT).show()
        }
    }

    var enableAll by remember { mutableStateOf(initialEnabled) }
    var studyReminders by remember { mutableStateOf(true) }
    var newContent by remember { mutableStateOf(true) }
    var achievements by remember { mutableStateOf(true) }

    LaunchedEffect(persistedNotifications) {
        enableAll = persistedNotifications
    }
    LaunchedEffect(persistedReminders) {
        studyReminders = persistedReminders
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkBackgroundTop,
                        Color(0xFF090D22),
                        DarkBackgroundBottom,
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Box
            DashedBorderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentPadding = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBackClick) {
                        Text(text = "←", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(text = "PROFILE & SETTINGS", color = SunsetOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Notifications", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Light Sheet Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(color = if (isDark) Color.Transparent else Color(0xFFF8FAFC), shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Master Toggle
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                                border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column {
                                        Text(text = "Enable Notifications", color = if (isDark) TextPrimary else Color(0xFF0F172A), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Receive learning updates and reminders", color = if (isDark) TextSecondary else Color(0xFF64748B), fontSize = 12.sp)
                                    }
                                    Switch(
                                        checked = enableAll,
                                        onCheckedChange = { isChecked ->
                                            enableAll = isChecked
                                            onToggleNotifications(isChecked)
                                            coroutineScope.launch {
                                                sessionManager.setNotificationsEnabled(isChecked)
                                            }
                                            if (isChecked) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                                                ) {
                                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                                } else {
                                                    SynapseNotificationHelper.scheduleStudyReminders(context)
                                                }
                                            } else {
                                                SynapseNotificationHelper.cancelStudyReminders(context)
                                            }
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = SunsetOrange,
                                            uncheckedTrackColor = if (isDark) Color(0xFF25221F) else Color(0xFFE2E8F0)
                                        )
                                    )
                                }
                            }
                        }

                        // Specific Notifications
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                                border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
                            ) {
                                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                    NotificationToggleRow(
                                        title = "Daily Study Reminders",
                                        subtitle = "Gentle prompts to keep your learning streak",
                                        checked = studyReminders && enableAll,
                                        enabled = enableAll,
                                        isDark = isDark,
                                        onCheckedChange = { isChecked ->
                                            studyReminders = isChecked
                                            coroutineScope.launch {
                                                sessionManager.setStudyRemindersEnabled(isChecked)
                                                userPreferencesRepo.setStudyReminderEnabled(isChecked)
                                            }
                                            if (isChecked) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                                                ) {
                                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                                } else {
                                                    SynapseNotificationHelper.scheduleStudyReminders(context)
                                                    StudyReminderScheduler.scheduleDailyStudyReminder(context, currentStudyHour, currentStudyMinute)
                                                }
                                            } else {
                                                SynapseNotificationHelper.cancelStudyReminders(context)
                                                StudyReminderScheduler.cancelDailyStudyReminder(context)
                                            }
                                        }
                                    )

                                    if (studyReminders && enableAll) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                                .clickable {
                                                    TimePickerDialog(
                                                        context,
                                                        { _, selectedHour, selectedMinute ->
                                                            coroutineScope.launch {
                                                                userPreferencesRepo.setStudyReminderTime(selectedHour, selectedMinute)
                                                                sessionManager.setStudyReminderTime(selectedHour, selectedMinute)
                                                            }
                                                            StudyReminderScheduler.scheduleDailyStudyReminder(context, selectedHour, selectedMinute)
                                                            val newFormatted = UserPreferencesRepository.formatTime(selectedHour, selectedMinute)
                                                            coroutineScope.launch {
                                                                snackbarHostState.showSnackbar("⏰ Daily study reminder set for $newFormatted")
                                                            }
                                                            Toast.makeText(context, "⏰ Daily study reminder set for $newFormatted", Toast.LENGTH_SHORT).show()
                                                        },
                                                        currentStudyHour,
                                                        currentStudyMinute,
                                                        false
                                                    ).show()
                                                }
                                                .background(if (isDark) SurfacePanel else Color(0xFFF1F5F9), RoundedCornerShape(10.dp))
                                                .border(1.dp, if (isDark) SurfaceBorder else SunsetOrange.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = "⏰", fontSize = 18.sp)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(text = "Preferred Study Time", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = if (isDark) TextPrimary else Color(0xFF0F172A))
                                                    Text(text = "Exact alarm & daily alert", fontSize = 10.sp, color = if (isDark) TextSecondary else Color(0xFF64748B))
                                                }
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(SunsetOrange.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Text(text = formattedStudyTime, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SunsetOrange)
                                                }
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(text = "✏️", fontSize = 12.sp)
                                            }
                                        }
                                    }

                                    NotificationToggleRow(
                                        title = "New Chapters & Quizzes",
                                        subtitle = "Notifications when fresh content is unlocked",
                                        checked = newContent && enableAll,
                                        enabled = enableAll,
                                        isDark = isDark,
                                        onCheckedChange = { newContent = it }
                                    )

                                    NotificationToggleRow(
                                        title = "Milestone Achievements",
                                        subtitle = "Alerts when you earn badges or level up",
                                        checked = achievements && enableAll,
                                        enabled = enableAll,
                                        isDark = isDark,
                                        onCheckedChange = { achievements = it }
                                    )
                                }
                            }
                        }

                        // Test Notification Action Button (For Viva Demo)
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Button(
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                                    ) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        val sent = SynapseNotificationHelper.showTestNotification(context)
                                        if (sent) {
                                            Toast.makeText(context, "⚡ Notification sent! Check status bar.", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Unable to post notification. Check permissions.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)
                            ) {
                                Text(
                                    text = "⚡ Send Test Notification",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        )
    }
}

@Composable
private fun NotificationToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean,
    isDark: Boolean = false,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (enabled) (if (isDark) TextPrimary else Color(0xFF0F172A)) else (if (isDark) TextSecondary else Color(0xFF94A3B8)),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = subtitle, color = if (isDark) TextSecondary else Color(0xFF94A3B8), fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SunsetOrange,
                uncheckedTrackColor = if (isDark) Color(0xFF25221F) else Color(0xFFE2E8F0)
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotificationsSettingsScreenPreview() {
    SYNAPSETheme {
        NotificationsSettingsScreen(onBackClick = {}, modifier = Modifier.fillMaxSize())
    }
}
