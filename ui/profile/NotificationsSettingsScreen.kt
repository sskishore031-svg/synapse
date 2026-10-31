package com.siamts.synapse.ui.profile

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@Composable
fun NotificationsSettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    initialEnabled: Boolean = true,
    onToggleNotifications: (Boolean) -> Unit = {},
) {
    var enableAll by remember { mutableStateOf(initialEnabled) }
    var studyReminders by remember { mutableStateOf(true) }
    var newContent by remember { mutableStateOf(true) }
    var achievements by remember { mutableStateOf(true) }

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
                        Text(text = "PROFILE & SETTINGS", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
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
                    .background(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
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
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column {
                                        Text(text = "Enable Notifications", color = Color(0xFF0F172A), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Receive learning updates and reminders", color = Color(0xFF64748B), fontSize = 12.sp)
                                    }
                                    Switch(
                                        checked = enableAll,
                                        onCheckedChange = {
                                            enableAll = it
                                            onToggleNotifications(it)
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BlueAccent)
                                    )
                                }
                            }
                        }

                        // Specific Notifications
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                    NotificationToggleRow(
                                        title = "Daily Study Reminders",
                                        subtitle = "Gentle prompts to keep your learning streak",
                                        checked = studyReminders && enableAll,
                                        enabled = enableAll,
                                        onCheckedChange = { studyReminders = it }
                                    )

                                    NotificationToggleRow(
                                        title = "New Chapters & Quizzes",
                                        subtitle = "Notifications when fresh content is unlocked",
                                        checked = newContent && enableAll,
                                        enabled = enableAll,
                                        onCheckedChange = { newContent = it }
                                    )

                                    NotificationToggleRow(
                                        title = "Milestone Achievements",
                                        subtitle = "Alerts when you earn badges or level up",
                                        checked = achievements && enableAll,
                                        enabled = enableAll,
                                        onCheckedChange = { achievements = it }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = if (enabled) Color(0xFF0F172A) else Color(0xFF94A3B8), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, color = Color(0xFF94A3B8), fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BlueAccent)
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
