package com.siamts.synapse.ui.profile

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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.siamts.synapse.data.remote.RetrofitClient
import com.siamts.synapse.ui.theme.AmberGold
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.DarkBackground
import com.siamts.synapse.ui.theme.LocalThemeIsDark
import com.siamts.synapse.ui.theme.SunsetOrange
import com.siamts.synapse.ui.theme.SurfaceBorder
import com.siamts.synapse.ui.theme.SurfacePanel
import com.siamts.synapse.ui.theme.SurfacePanelElevated
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onOpenSavedNotesClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onTabSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onMyProfileClick: (() -> Unit)? = null,
    onAchievementsClick: (() -> Unit)? = null,
    onStudyGoalsClick: (() -> Unit)? = null,
    onLanguageClick: (() -> Unit)? = null,
    onNotificationsClick: (() -> Unit)? = null,
    onHelpSupportClick: (() -> Unit)? = null,
    onAboutSynapseClick: (() -> Unit)? = null,
    onProgressAnalyticsClick: (() -> Unit)? = null,
    onRemoveProfilePictureClick: (() -> Unit)? = null,
    onOpenDownloadedNotesClick: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileContent(
        uiState = uiState,
        onToggleNotifications = { viewModel.onToggleNotifications(it) },
        onRetryClick = { viewModel.loadSessionAndFetchRemote() },
        onOpenSavedNotesClick = onOpenSavedNotesClick,
        onOpenDownloadedNotesClick = onOpenDownloadedNotesClick,
        onSettingsClick = onSettingsClick,
        onBackClick = onBackClick,
        onLogoutClick = onLogoutClick,
        onRemoveProfilePictureClick = {
            onRemoveProfilePictureClick?.invoke() ?: viewModel.removeProfilePicture()
        },
        onMyProfileClick = { onMyProfileClick?.invoke() },
        onAchievementsClick = { onAchievementsClick?.invoke() },
        onStudyGoalsClick = { onStudyGoalsClick?.invoke() },
        onLanguageClick = { onLanguageClick?.invoke() },
        onNotificationsClick = { onNotificationsClick?.invoke() },
        onHelpSupportClick = { onHelpSupportClick?.invoke() },
        onAboutSynapseClick = { onAboutSynapseClick?.invoke() },
        onProgressAnalyticsClick = { onProgressAnalyticsClick?.invoke() },
        onTabSelect = { index ->
            viewModel.onTabSelected(index)
            onTabSelect(index)
        },
        modifier = modifier,
    )
}

@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    onToggleNotifications: (Boolean) -> Unit,
    onOpenSavedNotesClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onTabSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onRemoveProfilePictureClick: (() -> Unit)? = null,
    onRetryClick: (() -> Unit)? = null,
    onMyProfileClick: (() -> Unit)? = null,
    onAchievementsClick: (() -> Unit)? = null,
    onStudyGoalsClick: (() -> Unit)? = null,
    onLanguageClick: (() -> Unit)? = null,
    onNotificationsClick: (() -> Unit)? = null,
    onHelpSupportClick: (() -> Unit)? = null,
    onAboutSynapseClick: (() -> Unit)? = null,
    onProgressAnalyticsClick: (() -> Unit)? = null,
    onOpenDownloadedNotesClick: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
) {
    val isDark = LocalThemeIsDark.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    val pageBgGradient = if (isDark) {
        listOf(DarkBackgroundTop, Color(0xFF090D22), DarkBackgroundBottom)
    } else {
        listOf(Color(0xFFF5F6FA), Color(0xFFF0F2F8), Color(0xFFE9ECF5))
    }
    val cardBg = if (isDark) SurfacePanel else MaterialTheme.colorScheme.surface
    val cardBorder = if (isDark) SurfaceBorder else Color(0xFFE4E7F0)
    val textPrimaryColor = if (isDark) TextPrimary else MaterialTheme.colorScheme.onSurface
    val textSecondaryColor = if (isDark) TextSecondary else MaterialTheme.colorScheme.onSurfaceVariant
    val elevatedBg = if (isDark) SurfacePanelElevated else Color(0xFFEEF1F7)
    val headerTextColor = textPrimaryColor

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = pageBgGradient))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Dark Header Box (DashedBorderBox)
            DashedBorderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentPadding = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = uiState.headerTag,
                            color = AmberGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.title,
                            color = headerTextColor,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                color = if (isDark) Color(0xFF161F42) else Color(0xFFEEF1F7),
                                shape = CircleShape
                            )
                            .border(1.dp, cardBorder, CircleShape)
                            .clickable { onSettingsClick?.invoke() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "⚙️",
                            fontSize = 16.sp,
                        )
                    }
                }
            }

            // Main Content Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    if (uiState.isLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp),
                            color = SunsetOrange
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    if (!uiState.errorMessage.isNullOrBlank()) {
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = uiState.errorMessage,
                                    color = Color(0xFFEF4444),
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "Retry",
                                    color = SunsetOrange,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { onRetryClick?.invoke() }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    // Scrollable Area
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. User Profile Summary Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, cardBorder, RoundedCornerShape(18.dp)),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable { onMyProfileClick?.invoke() }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(CircleShape)
                                                .background(SunsetOrange.copy(alpha = 0.2f), CircleShape)
                                                .border(1.dp, cardBorder, CircleShape),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            if (!uiState.profilePhotoUri.isNullOrEmpty()) {
                                                AsyncImage(
                                                    model = RetrofitClient.resolveImageModel(uiState.profilePhotoUri) ?: uiState.profilePhotoUri,
                                                    contentDescription = "Profile Photo",
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .clip(CircleShape),
                                                    contentScale = ContentScale.Crop,
                                                )
                                            } else {
                                                val initials = uiState.userInitials.ifBlank {
                                                    val parts = uiState.userName.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
                                                    when {
                                                        parts.size >= 2 -> "${parts.first().first()}${parts.last().first()}".uppercase()
                                                        parts.isNotEmpty() -> parts.first().take(2).uppercase()
                                                        else -> "K"
                                                    }
                                                }
                                                Text(
                                                    text = initials,
                                                    color = SunsetOrange,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = uiState.userName,
                                                color = textPrimaryColor,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = uiState.userEmail,
                                                color = textSecondaryColor,
                                                fontSize = 12.sp,
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = uiState.userStreak,
                                                color = AmberGold,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(elevatedBg, CircleShape)
                                            .border(1.dp, cardBorder, CircleShape)
                                            .clickable { onMyProfileClick?.invoke() },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(text = "✏️", fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        // 2. Options List Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, cardBorder, RoundedCornerShape(18.dp)),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                    ) {
                                    // Item 1: My Profile
                                    ProfileOptionRow(
                                        icon = "👤",
                                        title = "My Profile",
                                        textColor = textPrimaryColor,
                                        mutedColor = textSecondaryColor,
                                        onClick = { onMyProfileClick?.invoke() },
                                    )

                                    // Item 2: Achievements
                                    ProfileOptionRow(
                                        icon = "🏅",
                                        title = "Achievements",
                                        trailingText = uiState.achievementsCount,
                                        textColor = textPrimaryColor,
                                        mutedColor = textSecondaryColor,
                                        onClick = { onAchievementsClick?.invoke() },
                                    )

                                    // Item 3: Study Goals
                                    ProfileOptionRow(
                                        icon = "🎯",
                                        title = "Study Goals",
                                        trailingText = uiState.studyGoalText,
                                        textColor = textPrimaryColor,
                                        mutedColor = textSecondaryColor,
                                        onClick = { onStudyGoalsClick?.invoke() },
                                    )

                                    // Item 5: Saved Notes
                                    ProfileOptionRow(
                                        icon = "🔖",
                                        title = "Saved Notes",
                                        textColor = textPrimaryColor,
                                        mutedColor = textSecondaryColor,
                                        onClick = onOpenSavedNotesClick,
                                    )

                                    // Item 5b: Downloaded Notes
                                    ProfileOptionRow(
                                        icon = "💾",
                                        title = "Downloaded Notes",
                                        textColor = textPrimaryColor,
                                        mutedColor = textSecondaryColor,
                                        onClick = { onOpenDownloadedNotesClick?.invoke() ?: onOpenSavedNotesClick() },
                                    )

                                    // Item 6: Notifications Switch
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onNotificationsClick?.invoke() }
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = "🔔", fontSize = 16.sp)
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = "Notifications",
                                                color = textPrimaryColor,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                        }

                                        Switch(
                                            checked = uiState.notificationsEnabled,
                                            onCheckedChange = onToggleNotifications,
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = SunsetOrange,
                                            )
                                        )
                                    }

                                    // Item 7: About Synapse AI
                                    ProfileOptionRow(
                                        icon = "ℹ️",
                                        title = "About Synapse AI",
                                        textColor = textPrimaryColor,
                                        mutedColor = textSecondaryColor,
                                        onClick = { onAboutSynapseClick?.invoke() },
                                    )
                                }
                            }
                        }

                        // 4. Logout Button Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 4.dp
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .background(cardBg, RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                    .clickable { showLogoutDialog = true },
                                contentAlignment = Alignment.Center,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "↳ ", color = Color(0xFFEF4444), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "Logout",
                                        color = Color(0xFFEF4444),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }

                        // 5. Remove Profile Picture Button Box
                        if (!uiState.profilePhotoUri.isNullOrEmpty()) {
                            DashedBorderBox(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = 4.dp
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp)
                                        .background(cardBg, RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                    .clickable { onRemoveProfilePictureClick?.invoke() },
                                contentAlignment = Alignment.Center,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🗑 ", fontSize = 14.sp)
                                    Text(
                                        text = "Remove Profile Picture",
                                        color = Color(0xFFDC2626),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }

                        // 6. Version Footer Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 6.dp
                        ) {
                            Text(
                                text = uiState.appVersionText,
                                color = textSecondaryColor,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }


        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = {
                    Text(
                        text = "Confirm Logout",
                        fontWeight = FontWeight.Bold,
                        color = textPrimaryColor
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to log out of your SYNAPSE AI account?",
                        color = textSecondaryColor,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            onLogoutClick()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Yes, Logout", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancel", color = textSecondaryColor, fontWeight = FontWeight.Medium)
                    }
                },
                containerColor = elevatedBg,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun ProfileOptionRow(
    icon: String,
    title: String,
    trailingText: String? = null,
    textColor: Color = TextPrimary,
    mutedColor: Color = TextSecondary,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (trailingText != null) {
                Text(
                    text = trailingText,
                    color = mutedColor,
                    fontSize = 12.sp,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = "›",
                color = mutedColor,
                fontSize = 18.sp,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    SYNAPSETheme {
        ProfileContent(
            uiState = ProfileUiState(),
            onToggleNotifications = {},
            onOpenSavedNotesClick = {},
            onLogoutClick = {},
            onTabSelect = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
