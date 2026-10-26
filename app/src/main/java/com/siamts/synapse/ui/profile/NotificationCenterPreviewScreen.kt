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
import androidx.compose.material3.IconButton
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
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val iconEmoji: String,
    val tagText: String,
    val timeAgo: String,
    val isUnread: Boolean,
    val actionText: String,
)

@Composable
fun NotificationCenterPreviewScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedFilter by remember { mutableStateOf(0) } // 0: All, 1: Reminders, 2: Achievements, 3: System

    val notifications = listOf(
        NotificationItem(
            id = "1",
            title = "Study Reminder",
            message = "Time for your daily 25-minute Mathematics study session!",
            iconEmoji = "⏰",
            tagText = "Study Reminder",
            timeAgo = "10 min ago",
            isUnread = true,
            actionText = "Start Session →"
        ),
        NotificationItem(
            id = "2",
            title = "Achievement Unlocked!",
            message = "You earned the 'Consistent Learner' badge for a 5-day streak!",
            iconEmoji = "🏆",
            tagText = "Achievement",
            timeAgo = "2 hours ago",
            isUnread = true,
            actionText = "View Badges →"
        ),
        NotificationItem(
            id = "3",
            title = "AI Recommendation",
            message = "Try reviewing Quadratic Factoring before your upcoming chapter quiz.",
            iconEmoji = "🧠",
            tagText = "AI Suggestion",
            timeAgo = "Yesterday, 4:30 PM",
            isUnread = false,
            actionText = "Open Lesson →"
        ),
        NotificationItem(
            id = "4",
            title = "New Content Available",
            message = "New lesson added to Computer Science: Graph Traversal & BFS.",
            iconEmoji = "📚",
            tagText = "Course Update",
            timeAgo = "Yesterday, 11:15 AM",
            isUnread = false,
            actionText = "View Course →"
        ),
        NotificationItem(
            id = "5",
            title = "Smart Notes Generated",
            message = "Your AI Smart Notes for Chemical Bonding are ready to download.",
            iconEmoji = "📝",
            tagText = "Smart Notes",
            timeAgo = "12 Oct 2026",
            isUnread = false,
            actionText = "Open Notes →"
        )
    )

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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBackClick) {
                        Text(text = "←", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "UPDATES & REMINDERS", color = CyanGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Notifications", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF161F42), CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "✓✓", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    // Scrollable Area
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. Filter Chips Row Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 4.dp) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                val filterLabels = listOf("All (5)", "Reminders", "Badges", "System")
                                filterLabels.forEachIndexed { idx, fName ->
                                    val isSel = idx == selectedFilter
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(34.dp)
                                            .background(if (isSel) BlueAccent else Color.Transparent, RoundedCornerShape(8.dp))
                                            .clickable { selectedFilter = idx },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = fName,
                                            color = if (isSel) Color.White else Color(0xFF64748B),
                                            fontSize = 11.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // 2. Notifications List Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(text = "Recent Notifications", color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "2 Unread", color = BlueAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                notifications.forEach { item ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (item.isUnread) Color(0xFFEFF6FF) else Color.White
                                        ),
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(
                                                        modifier = Modifier.size(30.dp).background(Color(0xFFE0F2FE), CircleShape),
                                                        contentAlignment = Alignment.Center,
                                                    ) {
                                                        Text(text = item.iconEmoji, fontSize = 14.sp)
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(text = item.title, color = Color(0xFF0F172A), fontSize = 13.sp, fontWeight = FontWeight.Bold)

                                                    if (item.isUnread) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Box(modifier = Modifier.size(7.dp).background(BlueAccent, CircleShape))
                                                    }
                                                }

                                                Text(text = item.timeAgo, color = Color(0xFF94A3B8), fontSize = 10.sp)
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))

                                            Text(
                                                text = item.message,
                                                color = Color(0xFF334155),
                                                fontSize = 12.sp,
                                                lineHeight = 16.sp
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Text(text = item.tagText, color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                                }

                                                Text(
                                                    text = item.actionText,
                                                    color = BlueAccent,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.clickable { }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Mark Read Footer Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 4.dp) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .clickable { },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(text = "✓ Mark All as Read", color = BlueAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotificationCenterPreviewScreenPreview() {
    SYNAPSETheme {
        NotificationCenterPreviewScreen(onBackClick = {}, modifier = Modifier.fillMaxSize())
    }
}
