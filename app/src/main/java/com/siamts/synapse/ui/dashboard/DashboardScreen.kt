package com.siamts.synapse.ui.dashboard

import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.siamts.synapse.data.remote.RetrofitClient
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.PurpleAccent
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier,
    onAskAiClick: (() -> Unit)? = null,
    onLearnTabClick: (() -> Unit)? = null,
    onBrainMapClick: (() -> Unit)? = null,
    onFlashcardsClick: (() -> Unit)? = null,
    onQuizTabClick: (() -> Unit)? = null,
    onNotesTabClick: (() -> Unit)? = null,
    onProfileTabClick: (() -> Unit)? = null,
    onNotificationClick: (() -> Unit)? = null,
    onContinueLessonClick: ((subjectSlug: String, chapterId: Int) -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DashboardContent(
        uiState = uiState,
        onTabSelect = { tabIndex ->
            viewModel.onTabSelected(tabIndex)
            if (tabIndex == 1) { // Learn Tab
                onLearnTabClick?.invoke()
            } else if (tabIndex == 2) { // Quiz Tab
                onQuizTabClick?.invoke()
            } else if (tabIndex == 3) { // Notes Tab
                onNotesTabClick?.invoke()
            } else if (tabIndex == 4) { // Profile Tab
                onProfileTabClick?.invoke()
            }
        },
        onPlanItemClick = { itemId ->
            viewModel.onPlanItemClicked(itemId)
            val item = uiState.todayPlanItems.find { it.id == itemId }
            if (item != null) {
                when (item.actionType) {
                    "LESSON" -> onContinueLessonClick?.invoke(item.subjectSlug ?: "mathematics", item.chapterId)
                    "QUIZ" -> onQuizTabClick?.invoke()
                    "FLASHCARDS" -> onFlashcardsClick?.invoke()
                    else -> {
                        if (itemId == "3") onFlashcardsClick?.invoke()
                        else if (itemId == "2") onQuizTabClick?.invoke()
                        else onContinueLessonClick?.invoke("mathematics", 1)
                    }
                }
            } else {
                if (itemId == "3") onFlashcardsClick?.invoke()
            }
        },
        onAskAiClick = {
            viewModel.onAskAiTutorClicked()
            onAskAiClick?.invoke()
        },
        onBrainMapClick = {
            onBrainMapClick?.invoke()
        },
        onNotificationClick = {
            onNotificationClick?.invoke()
        },
        onProfileClick = {
            onProfileTabClick?.invoke()
        },
        modifier = modifier,
    )
}

@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    onTabSelect: (Int) -> Unit,
    onPlanItemClick: (String) -> Unit,
    onAskAiClick: () -> Unit,
    modifier: Modifier = Modifier,
    onBrainMapClick: (() -> Unit)? = null,
    onNotificationClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null,
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
            // Top Dark Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = uiState.dateText,
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Hello, ${uiState.userName} 👋",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Bell Notification Button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF161F42), CircleShape)
                                .clickable { onNotificationClick?.invoke() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = "🔔", fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Top Right Corner Profile Icon (Photo or K Avatar)
                        val formattedPhoto = RetrofitClient.formatPhotoUrl(uiState.profilePhotoUri)
                        if (!formattedPhoto.isNullOrBlank()) {
                            AsyncImage(
                                model = formattedPhoto,
                                contentDescription = "Profile Photo",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, Color(0xFFBAE6FD), CircleShape)
                                    .clickable { onProfileClick?.invoke() },
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFE0F2FE), CircleShape)
                                    .clickable { onProfileClick?.invoke() },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = uiState.userName.firstOrNull()?.uppercase() ?: "K",
                                    color = Color(0xFF0284C7),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Direct FRIDAY AI Chat Query Input Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Color(0xFF161F42), RoundedCornerShape(16.dp))
                        .clickable { onAskAiClick() }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✨", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Ask FRIDAY AI anything...",
                                color = TextMuted,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🎙️", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(BlueAccent, CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "↑",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
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
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. "Your learning journey" Dark Card
                    DashedBorderBox(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = 2.dp
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "Your learning journey",
                                        color = TextSecondary,
                                        fontSize = 13.sp,
                                    )
                                    Text(text = "🔔", fontSize = 16.sp)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "${uiState.overallProgress}%",
                                        color = TextPrimary,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "overall progress",
                                        color = TextSecondary,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(bottom = 4.dp),
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                LinearProgressIndicator(
                                    progress = { uiState.overallProgress / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp),
                                    color = CyanGlow,
                                    trackColor = Color(0xFF334155),
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = uiState.progressMessage,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    }

                    // 2. Stats Grid (3 White Cards)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Card 1
                        DashedBorderBox(
                            modifier = Modifier.weight(1f),
                            contentPadding = 2.dp
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "${uiState.topicsDone}",
                                        color = Color(0xFF0F172A),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "Topics done",
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                        }

                        // Card 2
                        DashedBorderBox(
                            modifier = Modifier.weight(1f),
                            contentPadding = 2.dp
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "${uiState.quizScore}%",
                                        color = Color(0xFF0F172A),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "Quiz score",
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                        }

                        // Card 3
                        DashedBorderBox(
                            modifier = Modifier.weight(1f),
                            contentPadding = 2.dp
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "${uiState.studyStreakDays} days",
                                        color = Color(0xFF0F172A),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "Study streak",
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                        }
                    }

                    // 3. Today's Learning Plan Card
                    DashedBorderBox(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = 2.dp
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "Today's Learning Plan",
                                        color = Color(0xFF0F172A),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = uiState.todayPlanTotalTime,
                                        color = Color(0xFF64748B),
                                        fontSize = 12.sp,
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    uiState.todayPlanItems.forEach { item ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onPlanItemClick(item.id) }
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(Color(0xFFEFF6FF), CircleShape),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Text(text = item.iconEmoji, fontSize = 16.sp)
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = item.title,
                                                    color = Color(0xFF0F172A),
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                )
                                                Text(
                                                    text = "${item.category} · ${item.duration}",
                                                    color = Color(0xFF64748B),
                                                    fontSize = 12.sp,
                                                )
                                            }

                                            Text(
                                                text = "›",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 4. Recommended for You Card
                    DashedBorderBox(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = 2.dp
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Recommended for you",
                                    color = Color(0xFF0F172A),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(Color(0xFFF3E8FF), CircleShape),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(text = "∑", color = PurpleAccent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = uiState.recommendedTitle,
                                            color = Color(0xFF0F172A),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                        Text(
                                            text = uiState.recommendedSubtitle,
                                            color = Color(0xFF64748B),
                                            fontSize = 12.sp,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 5. AI Tutor Banner Card
                    DashedBorderBox(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = 2.dp
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAskAiClick() },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = "✨", fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "A question on your mind?",
                                            color = BlueAccent,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = "Your AI Tutor is one tap away.",
                                            color = Color(0xFF64748B),
                                            fontSize = 12.sp,
                                        )
                                    }
                                }

                                Text(
                                    text = "↗",
                                    color = BlueAccent,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }

                // Floating FRIDAY AI Chat Button overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 12.dp, end = 16.dp)
                        .background(
                            brush = Brush.horizontalGradient(listOf(PurpleAccent, BlueAccent, CyanAccent)),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clickable { onAskAiClick() }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🤖 ", fontSize = 14.sp)
                        Text(
                            text = "Chat with FRIDAY",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardScreenPreview() {
    SYNAPSETheme {
        DashboardContent(
            uiState = DashboardUiState(),
            onTabSelect = {},
            onPlanItemClick = {},
            onAskAiClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
