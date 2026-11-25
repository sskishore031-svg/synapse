package com.siamts.synapse.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siamts.synapse.data.local.NotificationEntity
import com.siamts.synapse.data.local.SynapseDatabase
import com.siamts.synapse.ui.theme.LocalThemeIsDark
import com.siamts.synapse.ui.theme.SunsetOrange
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Modern Notification Center bottom sheet for SYNAPSE AI.
 * Displays notifications stored in Room DB (NotificationEntity),
 * supporting unread badges, filter tabs, mark all read, clear all, and deep link navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenterSheet(
    onDismissRequest: () -> Unit,
    onNavigateAction: (String) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current
    val isDark = LocalThemeIsDark.current
    val coroutineScope = rememberCoroutineScope()
    val notificationDao = remember { SynapseDatabase.getNotificationDao(context) }

    val notifications by notificationDao.getAllNotificationsFlow().collectAsState(initial = emptyList())
    val unreadCount by notificationDao.getUnreadCountFlow().collectAsState(initial = 0)

    var selectedFilterIndex by remember { mutableIntStateOf(0) } // 0: All, 1: Challenges, 2: Reminders, 3: Vault, 4: Milestones

    val filterLabels = listOf("All", "Challenges 🚀", "Reminders 🔥", "Vault 🧩", "Milestones ⭐")

    val filteredNotifications = remember(notifications, selectedFilterIndex) {
        when (selectedFilterIndex) {
            1 -> notifications.filter { it.type == "daily_challenge" }
            2 -> notifications.filter { it.type in listOf("streak_saver", "today_plan", "study_reminder") }
            3 -> notifications.filter { it.type == "mistake_vault" }
            4 -> notifications.filter { it.type == "milestone" }
            else -> notifications
        }
    }

    val sheetBgColor = if (isDark) Color(0xFF13141B) else Color(0xFFF8FAFC)
    val cardBgColor = if (isDark) Color(0xFF1C1D27) else Color.White
    val cardBorderColor = if (isDark) Color(0xFF2E3040) else Color(0xFFE2E8F0)
    val textPrimaryColor = if (isDark) Color.White else Color(0xFF0F172A)
    val textSecondaryColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = sheetBgColor,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .background(Color(0xFFCBD5E1).copy(alpha = 0.5f), RoundedCornerShape(2.dp))
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 16.dp)
        ) {
            // Header Row: Title, Unread Pill, Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Notifications",
                        color = textPrimaryColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(SunsetOrange, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$unreadCount new",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (notifications.isNotEmpty()) {
                        // Mark all read button
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    notificationDao.markAllAsRead()
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DoneAll,
                                contentDescription = "Mark All Read",
                                tint = if (unreadCount > 0) SunsetOrange else textSecondaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Clear all button
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    notificationDao.clearAll()
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteOutline,
                                contentDescription = "Clear All",
                                tint = textSecondaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Close Sheet
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = textSecondaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Filter Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(filterLabels.size) { index ->
                    val isSelected = selectedFilterIndex == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) SunsetOrange else (if (isDark) Color(0xFF1E202E) else Color(0xFFF1F5F9))
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) SunsetOrange else (if (isDark) Color(0xFF2E3040) else Color(0xFFE2E8F0)),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedFilterIndex = index }
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = filterLabels[index],
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else textSecondaryColor
                        )
                    }
                }
            }

            // Notifications List or Empty State
            if (filteredNotifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "✨", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "All Caught Up!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimaryColor
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No notifications right now. Your placement tasks and alerts will appear here.",
                            fontSize = 13.sp,
                            color = textSecondaryColor,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredNotifications, key = { it.id }) { item ->
                        NotificationCardItem(
                            notification = item,
                            isDark = isDark,
                            cardBgColor = cardBgColor,
                            cardBorderColor = cardBorderColor,
                            textPrimaryColor = textPrimaryColor,
                            textSecondaryColor = textSecondaryColor,
                            onItemClick = {
                                coroutineScope.launch {
                                    notificationDao.markAsRead(item.id)
                                }
                                val action = item.actionRoute
                                if (!action.isNullOrBlank()) {
                                    onDismissRequest()
                                    onNavigateAction(action)
                                }
                            },
                            onDelete = {
                                coroutineScope.launch {
                                    notificationDao.deleteNotification(item.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCardItem(
    notification: NotificationEntity,
    isDark: Boolean,
    cardBgColor: Color,
    cardBorderColor: Color,
    textPrimaryColor: Color,
    textSecondaryColor: Color,
    onItemClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) {
                if (isDark) Color(0xFF261D19) else Color(0xFFFFF7ED)
            } else {
                cardBgColor
            }
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (!notification.isRead) SunsetOrange.copy(alpha = 0.5f) else cardBorderColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Emoji Avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        if (isDark) SunsetOrange.copy(alpha = 0.18f) else Color(0xFFFFEDD5),
                        CircleShape
                    )
                    .border(1.dp, SunsetOrange.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = notification.iconEmoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        color = textPrimaryColor,
                        fontSize = 14.sp,
                        fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formatNotificationTime(notification.timestamp),
                            color = textSecondaryColor,
                            fontSize = 11.sp
                        )
                        if (!notification.isRead) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(SunsetOrange, CircleShape)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.body,
                    color = textSecondaryColor,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Action Pill if actionRoute present
                val actionLabel = when (notification.actionRoute) {
                    "quiz_hub" -> "Take Challenge →"
                    "learn" -> "Save Streak →"
                    "mistake_vault" -> "Review Mistakes →"
                    "home" -> "View Today's Tasks →"
                    "milestone" -> "View Milestone →"
                    else -> null
                }

                if (actionLabel != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .background(SunsetOrange.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                            .border(1.dp, SunsetOrange.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = actionLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SunsetOrange
                        )
                    }
                }
            }
        }
    }
}

/**
 * Formats relative timestamp for notifications.
 */
fun formatNotificationTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days == 1L -> "Yesterday"
        days < 7 -> "${days}d ago"
        else -> SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(timestamp))
    }
}
