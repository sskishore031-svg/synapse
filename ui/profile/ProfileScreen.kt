package com.siamts.synapse.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
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
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileContent(
        uiState = uiState,
        onToggleNotifications = { viewModel.onToggleNotifications(it) },
        onRetryClick = { viewModel.loadSessionAndFetchRemote() },
        onOpenSavedNotesClick = onOpenSavedNotesClick,
        onLogoutClick = onLogoutClick,
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
    onRetryClick: (() -> Unit)? = null,
    onMyProfileClick: (() -> Unit)? = null,
    onAchievementsClick: (() -> Unit)? = null,
    onStudyGoalsClick: (() -> Unit)? = null,
    onLanguageClick: (() -> Unit)? = null,
    onNotificationsClick: (() -> Unit)? = null,
    onHelpSupportClick: (() -> Unit)? = null,
    onAboutSynapseClick: (() -> Unit)? = null,
    onProgressAnalyticsClick: (() -> Unit)? = null,
) {
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
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.title,
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF161F42), CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "⚙️",
                            fontSize = 16.sp,
                        )
                    }
                }
            }

            // Light Sheet Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    if (uiState.isLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp),
                            color = BlueAccent
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
                                    color = Color(0xFFDC2626),
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "Retry",
                                    color = BlueAccent,
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
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                                .background(Color(0xFFE0E7FF), CircleShape),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            if (!uiState.profilePhotoUri.isNullOrEmpty()) {
                                                AsyncImage(
                                                    model = uiState.profilePhotoUri,
                                                    contentDescription = "Profile Photo",
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .clip(CircleShape),
                                                    contentScale = ContentScale.Crop,
                                                )
                                            } else {
                                                Text(
                                                    text = uiState.userInitials,
                                                    color = Color(0xFF3730A3),
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = uiState.userName,
                                                color = Color(0xFF0F172A),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = uiState.userDegree,
                                                color = Color(0xFF64748B),
                                                fontSize = 12.sp,
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = uiState.userStreak,
                                                color = Color(0xFF94A3B8),
                                                fontSize = 11.sp,
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(Color(0xFFF1F5F9), CircleShape)
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
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                        onClick = { onMyProfileClick?.invoke() },
                                    )

                                    // Item 2: Achievements
                                    ProfileOptionRow(
                                        icon = "🏅",
                                        title = "Achievements",
                                        trailingText = uiState.achievementsCount,
                                        onClick = { onAchievementsClick?.invoke() },
                                    )

                                    // Item 3: Study Goals
                                    ProfileOptionRow(
                                        icon = "🎯",
                                        title = "Study Goals",
                                        trailingText = uiState.studyGoalText,
                                        onClick = { onStudyGoalsClick?.invoke() },
                                    )

                                    // Item 4: Language
                                    ProfileOptionRow(
                                        icon = "🔤",
                                        title = "Language",
                                        trailingText = uiState.languageText,
                                        onClick = { onLanguageClick?.invoke() },
                                    )

                                    // Item 5: Saved & Downloaded Notes
                                    ProfileOptionRow(
                                        icon = "📝",
                                        title = "Saved & Downloaded Notes",
                                        onClick = onOpenSavedNotesClick,
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
                                                color = Color(0xFF0F172A),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                        }

                                        Switch(
                                            checked = uiState.notificationsEnabled,
                                            onCheckedChange = onToggleNotifications,
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = BlueAccent,
                                            )
                                        )
                                    }

                                    // Item 7: Help & Support
                                    ProfileOptionRow(
                                        icon = "❓",
                                        title = "Help & Support",
                                        onClick = { onHelpSupportClick?.invoke() },
                                    )

                                    // Item 8: About Synapse AI
                                    ProfileOptionRow(
                                        icon = "ℹ️",
                                        title = "About Synapse AI",
                                        onClick = { onAboutSynapseClick?.invoke() },
                                    )
                                }
                            }
                        }

                        // 3. Progress & Analytics Button Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 4.dp
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .clickable { onProgressAnalyticsClick?.invoke() },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "Progress & Analytics",
                                    color = BlueAccent,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                )
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
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFFFEE2E2), RoundedCornerShape(12.dp))
                                    .clickable { onLogoutClick() },
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

                        // 5. Version Footer Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 6.dp
                        ) {
                            Text(
                                text = uiState.appVersionText,
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }

            // Bottom Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val navItems = listOf(
                    Triple("Home", "🏠", 0),
                    Triple("Learn", "📖", 1),
                    Triple("Quiz", "❓", 2),
                    Triple("Notes", "📝", 3),
                    Triple("Profile", "👤", 4)
                )

                navItems.forEach { (label, icon, index) ->
                    val isSelected = index == uiState.selectedTab
                    Column(
                        modifier = Modifier
                            .clickable { onTabSelect(index) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = icon,
                            fontSize = 18.sp,
                            color = if (isSelected) BlueAccent else Color(0xFF94A3B8)
                        )
                        Text(
                            text = label,
                            color = if (isSelected) BlueAccent else Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileOptionRow(
    icon: String,
    title: String,
    trailingText: String? = null,
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
                color = Color(0xFF0F172A),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (trailingText != null) {
                Text(
                    text = trailingText,
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = "›",
                color = Color(0xFF94A3B8),
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
