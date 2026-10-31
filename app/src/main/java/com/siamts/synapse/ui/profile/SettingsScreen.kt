package com.siamts.synapse.ui.profile

import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.siamts.synapse.data.local.ThemeMode
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.DarkBackground
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.LightBackground
import com.siamts.synapse.ui.theme.LightBorder
import com.siamts.synapse.ui.theme.LightTextPrimary
import com.siamts.synapse.ui.theme.LightTextSecondary
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.theme.ThemeViewModel
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel = viewModel()
) {
    val context = LocalContext.current

    val activeTheme by themeViewModel.themeMode.collectAsState()
    val isDark = when (activeTheme) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    // Dynamic semantic colors based on active theme
    val pageBgGradient = if (isDark) {
        listOf(DarkBackgroundTop, Color(0xFF090D22), DarkBackgroundBottom)
    } else {
        listOf(Color(0xFFE0E7FF), Color(0xFFEEF2F6), LightBackground)
    }

    val bodyBgColor = if (isDark) DarkBackground else Color(0xFFF8FAFC)
    val cardBgColor = if (isDark) Color(0xFF131C38) else Color.White
    val cardBorderColor = if (isDark) Color(0xFF1E2B58) else LightBorder
    val primaryTextColor = if (isDark) TextPrimary else LightTextPrimary
    val secondaryTextColor = if (isDark) TextSecondary else LightTextSecondary
    val sectionHeaderColor = if (isDark) CyanAccent else BlueAccent

    var dailyReminderEnabled by remember { mutableStateOf(true) }
    var soundEffectsEnabled by remember { mutableStateOf(true) }
    var offlineSyncEnabled by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = pageBgGradient))
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Text(
                            text = "←",
                            color = primaryTextColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "PREFERENCES & CONFIG",
                            color = if (isDark) CyanAccent else BlueAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Settings",
                            color = primaryTextColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                color = if (isDark) Color(0xFF161F42) else Color(0xFFE2E8F0),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚙️", fontSize = 16.sp)
                    }
                }
            }

            // Main Body Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = bodyBgColor,
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // =========================================================
                    // SECTION 1: APPEARANCE & THEME
                    // =========================================================
                    SettingsSectionHeader(
                        title = "Appearance & Theme",
                        color = sectionHeaderColor
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBgColor),
                        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Choose Theme Mode",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = primaryTextColor
                            )

                            // Theme Options List
                            ThemeOptionItem(
                                title = ThemeMode.SYSTEM.title,
                                description = ThemeMode.SYSTEM.description,
                                icon = "⚙️",
                                isSelected = activeTheme == ThemeMode.SYSTEM,
                                isDark = isDark,
                                onSelect = {
                                    themeViewModel.setThemeMode(ThemeMode.SYSTEM)
                                    Toast.makeText(
                                        context,
                                        "Theme set to System Default",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )

                            ThemeOptionItem(
                                title = ThemeMode.LIGHT.title,
                                description = ThemeMode.LIGHT.description,
                                icon = "☀️",
                                isSelected = activeTheme == ThemeMode.LIGHT,
                                isDark = isDark,
                                onSelect = {
                                    themeViewModel.setThemeMode(ThemeMode.LIGHT)
                                    Toast.makeText(
                                        context,
                                        "Theme set to Light Mode",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )

                            ThemeOptionItem(
                                title = ThemeMode.DARK.title,
                                description = ThemeMode.DARK.description,
                                icon = "🌙",
                                isSelected = activeTheme == ThemeMode.DARK,
                                isDark = isDark,
                                onSelect = {
                                    themeViewModel.setThemeMode(ThemeMode.DARK)
                                    Toast.makeText(
                                        context,
                                        "Theme set to Dark Mode",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }

                    // =========================================================
                    // SECTION 2: APP PREFERENCES
                    // =========================================================
                    SettingsSectionHeader(
                        title = "App Preferences",
                        color = sectionHeaderColor
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBgColor),
                        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            SettingsToggleRow(
                                title = "Daily Study Reminders",
                                subtitle = "Receive daily focus prompts at 7:00 PM",
                                checked = dailyReminderEnabled,
                                onCheckedChange = { dailyReminderEnabled = it },
                                primaryTextColor = primaryTextColor,
                                secondaryTextColor = secondaryTextColor
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            SettingsToggleRow(
                                title = "Sound Effects & Haptics",
                                subtitle = "Play subtle vibrations on quiz interactions",
                                checked = soundEffectsEnabled,
                                onCheckedChange = { soundEffectsEnabled = it },
                                primaryTextColor = primaryTextColor,
                                secondaryTextColor = secondaryTextColor
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            SettingsToggleRow(
                                title = "Offline Mode Auto-Sync",
                                subtitle = "Auto-cache study guides for offline reading",
                                checked = offlineSyncEnabled,
                                onCheckedChange = { offlineSyncEnabled = it },
                                primaryTextColor = primaryTextColor,
                                secondaryTextColor = secondaryTextColor
                            )
                        }
                    }

                    // =========================================================
                    // SECTION 3: STORAGE & CACHE
                    // =========================================================
                    SettingsSectionHeader(
                        title = "Storage & Cache",
                        color = sectionHeaderColor
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBgColor),
                        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    try {
                                        context.cacheDir.deleteRecursively()
                                    } catch (_: Exception) {}
                                    Toast.makeText(context, "Cache cleared successfully!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Clear Local Cache",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = primaryTextColor
                                )
                                Text(
                                    text = "Free up temporary cache & offline images",
                                    fontSize = 12.sp,
                                    color = secondaryTextColor
                                )
                            }
                            Text(
                                text = "🧹 Clear",
                                color = if (isDark) CyanAccent else BlueAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // =========================================================
                    // SECTION 4: ABOUT SYNAPSE AI
                    // =========================================================
                    SettingsSectionHeader(
                        title = "About Synapse AI",
                        color = sectionHeaderColor
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBgColor),
                        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "SYNAPSE AI: Intelligent Learning Companion",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = primaryTextColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Version 1.0.0 · Powered by Jetpack Compose & On-Device LocalAIEngine",
                                fontSize = 12.sp,
                                color = secondaryTextColor
                            )
                        }
                    }

                    // =========================================================
                    // SECTION 5: ACCOUNT & LOGOUT
                    // =========================================================
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLogoutDialog = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) Color(0xFF38131B) else Color(0xFFFEF2F2)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDark) Color(0xFF6B1D2F) else Color(0xFFFECACA)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🚪", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Logout from Synapse AI",
                                color = Color(0xFFEF4444),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text(text = "Confirm Logout", fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to log out of your Synapse AI account?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            onLogoutClick()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Text("Logout", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun ThemeOptionItem(
    title: String,
    description: String,
    icon: String,
    isSelected: Boolean,
    isDark: Boolean,
    onSelect: () -> Unit
) {
    val activeBorderColor = if (isDark) CyanAccent else BlueAccent
    val inactiveBorderColor = if (isDark) Color(0xFF223058) else LightBorder
    val itemBgColor = if (isSelected) {
        if (isDark) Color(0xFF19284F) else Color(0xFFEFF6FF)
    } else {
        if (isDark) Color(0xFF0F172E) else Color(0xFFFAFAFA)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(itemBgColor, RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) activeBorderColor else inactiveBorderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onSelect)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = icon, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = if (isDark) TextPrimary else LightTextPrimary
                    )
                    Text(
                        text = description,
                        fontSize = 11.sp,
                        color = if (isDark) TextSecondary else LightTextSecondary
                    )
                }
            }

            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = if (isDark) CyanAccent else BlueAccent,
                    unselectedColor = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
                )
            )
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String, color: Color) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = color,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    primaryTextColor: Color,
    secondaryTextColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = primaryTextColor
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = secondaryTextColor
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = BlueAccent
            )
        )
    }
}
