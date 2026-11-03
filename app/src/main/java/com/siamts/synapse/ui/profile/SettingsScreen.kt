package com.siamts.synapse.ui.profile

import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
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
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.DarkBackground
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.LightBackground
import com.siamts.synapse.ui.theme.LightBorder
import com.siamts.synapse.ui.theme.LightTextPrimary
import com.siamts.synapse.ui.theme.LightTextSecondary
import com.siamts.synapse.ui.theme.PurpleAccent
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.theme.ThemeViewModel
import com.siamts.synapse.ui.welcome.components.BrainGraphic
import com.siamts.synapse.ui.welcome.components.DashedBorderBox
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
    var showThemeModal by remember { mutableStateOf(false) }

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
                    // SECTION 1: APPEARANCE & THEME (3D Interactive Selector)
                    // =========================================================
                    SettingsSectionHeader(
                        title = "Appearance & Theme",
                        color = sectionHeaderColor
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showThemeModal = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBgColor),
                        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(
                                            brush = if (isDark) {
                                                Brush.linearGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
                                            } else {
                                                Brush.linearGradient(listOf(Color(0xFFEFF6FF), Color(0xFFDBEAFE)))
                                            },
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isDark) CyanAccent.copy(alpha = 0.5f) else BlueAccent.copy(alpha = 0.3f),
                                            RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val icon = when (activeTheme) {
                                        ThemeMode.LIGHT -> "☀️"
                                        ThemeMode.DARK -> "🌙"
                                        ThemeMode.SYSTEM -> "⚙️"
                                    }
                                    Text(text = icon, fontSize = 22.sp)
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Text(
                                        text = "Theme Selection",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = primaryTextColor
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${activeTheme.title} active",
                                        fontSize = 12.sp,
                                        color = if (isDark) CyanAccent else BlueAccent,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (isDark) Color(0xFF1A274E) else Color(0xFFEEF2FF),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Customize 3D 🎨",
                                    color = if (isDark) CyanGlow else BlueAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
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
                    // SECTION 4: ABOUT SYNAPSE AI (With Brain Graphic)
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        color = if (isDark) Color(0xFF161F42) else Color(0xFFEFF6FF),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                BrainGraphic(modifier = Modifier.size(36.dp))
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "SYNAPSE AI Companion",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = primaryTextColor
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Version 1.0.0 · Jetpack Compose & On-Device Engine",
                                    fontSize = 12.sp,
                                    color = secondaryTextColor
                                )
                            }
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

        // =============================================================
        // 3D CARD-STYLE THEME SELECTOR MODAL BOTTOM SHEET
        // =============================================================
        if (showThemeModal) {
            ModalBottomSheet(
                onDismissRequest = { showThemeModal = false },
                sheetState = sheetState,
                containerColor = if (isDark) Color(0xFF0C132C) else Color.White,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Customize Appearance",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryTextColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Interactive 3D Preview Cards · Instant Recomposition",
                            fontSize = 12.sp,
                            color = secondaryTextColor
                        )
                    }

                    // 1. Light Mode 3D Preview Card
                    ThemePreview3DCard(
                        title = "Light Mode",
                        subtitle = "Crisp daytime UI with elevated card surfaces",
                        icon = "☀️",
                        isSelected = activeTheme == ThemeMode.LIGHT,
                        previewMode = ThemeMode.LIGHT,
                        onSelect = {
                            themeViewModel.setThemeMode(ThemeMode.LIGHT)
                            Toast.makeText(context, "Theme set to Light Mode", Toast.LENGTH_SHORT).show()
                            coroutineScope.launch { sheetState.hide(); showThemeModal = false }
                        }
                    )

                    // 2. Dark Mode 3D Preview Card
                    ThemePreview3DCard(
                        title = "Dark Mode",
                        subtitle = "Sleek cyber-neon dark aesthetic with glowing accents",
                        icon = "🌙",
                        isSelected = activeTheme == ThemeMode.DARK,
                        previewMode = ThemeMode.DARK,
                        onSelect = {
                            themeViewModel.setThemeMode(ThemeMode.DARK)
                            Toast.makeText(context, "Theme set to Dark Mode", Toast.LENGTH_SHORT).show()
                            coroutineScope.launch { sheetState.hide(); showThemeModal = false }
                        }
                    )

                    // 3. System Default Split/Dual-Tone 3D Card
                    ThemePreview3DCard(
                        title = "System Default",
                        subtitle = "Auto dual-tone mode synchronized with device appearance",
                        icon = "⚙️",
                        isSelected = activeTheme == ThemeMode.SYSTEM,
                        previewMode = ThemeMode.SYSTEM,
                        onSelect = {
                            themeViewModel.setThemeMode(ThemeMode.SYSTEM)
                            Toast.makeText(context, "Theme set to System Default", Toast.LENGTH_SHORT).show()
                            coroutineScope.launch { sheetState.hide(); showThemeModal = false }
                        }
                    )
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

/**
 * 3D-styled theme preview card with depth, elevation, shadows, and spring animation on tap.
 */
@Composable
private fun ThemePreview3DCard(
    title: String,
    subtitle: String,
    icon: String,
    isSelected: Boolean,
    previewMode: ThemeMode,
    onSelect: () -> Unit
) {
    // Spring bounce scale animation when selected
    val scaleAnim by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "Theme3DCardScale"
    )

    val cardShape = RoundedCornerShape(18.dp)

    val cardBorder = when {
        isSelected && previewMode == ThemeMode.DARK -> androidx.compose.foundation.BorderStroke(2.dp, CyanGlow)
        isSelected && previewMode == ThemeMode.LIGHT -> androidx.compose.foundation.BorderStroke(2.dp, BlueAccent)
        isSelected -> androidx.compose.foundation.BorderStroke(2.dp, PurpleAccent)
        else -> androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155).copy(alpha = 0.3f))
    }

    val cardBg = when (previewMode) {
        ThemeMode.LIGHT -> Color(0xFFFFFFFF)
        ThemeMode.DARK -> Color(0xFF0F172E)
        ThemeMode.SYSTEM -> Color(0xFF1E293B)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scaleAnim)
            .shadow(
                elevation = if (isSelected) 8.dp else 3.dp,
                shape = cardShape,
                ambientColor = if (previewMode == ThemeMode.DARK) CyanGlow else Color.Black,
                spotColor = if (isSelected) BlueAccent else Color.Gray
            )
            .background(cardBg, cardShape)
            .border(cardBorder, cardShape)
            .clickable(onClick = onSelect)
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = icon, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (previewMode == ThemeMode.LIGHT) Color(0xFF0F172A) else Color.White
                        )
                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            color = if (previewMode == ThemeMode.LIGHT) Color(0xFF64748B) else Color(0xFF94A3B8)
                        )
                    }
                }

                // Selected Spring-Animated Tick Mark
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(
                                color = if (previewMode == ThemeMode.DARK) CyanGlow else BlueAccent,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓",
                            color = if (previewMode == ThemeMode.DARK) Color.Black else Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 3D Miniature Mock UI Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        brush = when (previewMode) {
                            ThemeMode.LIGHT -> Brush.horizontalGradient(
                                listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0))
                            )
                            ThemeMode.DARK -> Brush.horizontalGradient(
                                listOf(Color(0xFF090D1E), Color(0xFF161F42))
                            )
                            ThemeMode.SYSTEM -> Brush.horizontalGradient(
                                listOf(Color(0xFFF1F5F9), Color(0xFF090D1E))
                            )
                        },
                        shape = RoundedCornerShape(10.dp)
                    )
                    .border(
                        1.dp,
                        if (previewMode == ThemeMode.DARK) Color(0xFF1E2B58) else Color(0xFFCBD5E1),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    if (previewMode == ThemeMode.DARK) CyanGlow else BlueAccent,
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(6.dp)
                                .background(
                                    if (previewMode == ThemeMode.LIGHT) Color(0xFF94A3B8) else Color(0xFF475569),
                                    RoundedCornerShape(3.dp)
                                )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(14.dp)
                            .background(
                                if (previewMode == ThemeMode.DARK) Color(0xFF223058) else Color(0xFFDBEAFE),
                                RoundedCornerShape(6.dp)
                            )
                    )
                }
            }
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
