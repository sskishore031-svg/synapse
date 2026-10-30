package com.siamts.synapse.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    onSubjectClick: ((String) -> Unit)? = null,
    onContinueLessonClick: ((subjectSlug: String, chapterId: Int) -> Unit)? = null,
    onAiChatClick: (() -> Unit)? = null,
    onFlashcardsClick: (() -> Unit)? = null,
    onQuizClick: (() -> Unit)? = null,
    onNotesClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null,
    onBrainMapClick: (() -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        uiState = uiState,
        onSearchChange = { query -> viewModel.onSearchQueryChanged(query) },
        onTabSelect = { index ->
            viewModel.onTabSelected(index)
            when (index) {
                1 -> onSubjectClick?.invoke("")
                2 -> onAiChatClick?.invoke()
                3 -> {
                    // Stay in profile tab of home, or open full profile screen
                }
            }
        },
        onActionClick = { id ->
            viewModel.onActionClicked(id)
            when (id) {
                "1" -> onAiChatClick?.invoke()
                "2" -> onNotesClick?.invoke()
                "3" -> onFlashcardsClick?.invoke()
                "4" -> onQuizClick?.invoke()
            }
        },
        onSessionClick = { id ->
            viewModel.onSessionClicked(id)
            val session = uiState.recentSessions.find { it.id == id }
            if (session != null && !session.subjectSlug.isNullOrBlank()) {
                onContinueLessonClick?.invoke(session.subjectSlug, session.chapterId)
            } else if (!uiState.continueSubjectSlug.isNullOrBlank()) {
                onContinueLessonClick?.invoke(uiState.continueSubjectSlug.orEmpty(), uiState.continueChapterId)
            }
        },
        onSubjectCardClick = { slug ->
            onSubjectClick?.invoke(slug)
        },
        onBrainMapCardClick = {
            onBrainMapClick?.invoke()
        },
        onProfileCardClick = {
            onProfileClick?.invoke()
        },
        onRetryClick = {
            viewModel.retry()
        },
        modifier = modifier,
    )
}

@Composable
fun HomeContent(
    uiState: HomeUiState,
    onSearchChange: (String) -> Unit,
    onTabSelect: (Int) -> Unit,
    onActionClick: (String) -> Unit,
    onSessionClick: (String) -> Unit,
    onSubjectCardClick: (String) -> Unit = {},
    onBrainMapCardClick: () -> Unit = {},
    onProfileCardClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    modifier: Modifier = Modifier,
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
            // Header Section
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
                            text = uiState.greeting,
                            color = TextSecondary,
                            fontSize = 13.sp,
                        )
                        Text(
                            text = "${uiState.userName} 👋",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    DashedBorderBox(
                        modifier = Modifier.clickable { onBrainMapCardClick() },
                        contentPadding = 8.dp
                    ) {
                        Text(
                            text = "⚡ Mind Power: ${uiState.mindPower}%",
                            color = CyanGlow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Input
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text(
                            text = "Ask Synapse AI or search topics...",
                            color = TextMuted,
                            fontSize = 14.sp,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanGlow,
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedContainerColor = Color(0xFF0D1430),
                        unfocusedContainerColor = Color(0xFF0B0F24),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                    ),
                    singleLine = true,
                )
            }

            // Error or Loading Banners
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = CyanGlow,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Syncing dashboard with MySQL...",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            } else if (uiState.errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .background(Color(0xFF451A03), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.errorMessage,
                            color = Color(0xFFFDE68A),
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Retry",
                            color = CyanGlow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { onRetryClick() }
                                .padding(start = 8.dp)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                // If Profile Tab selected (index 3), show Student Profile Card with Continue button
                if (uiState.selectedTab == 3) {
                    item {
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 16.dp,
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { onProfileCardClick() }
                                ) {
                                    val formattedPhoto = RetrofitClient.formatPhotoUrl(uiState.profilePhotoUri)
                                    if (!formattedPhoto.isNullOrBlank()) {
                                        AsyncImage(
                                            model = formattedPhoto,
                                            contentDescription = "Profile Photo",
                                            modifier = Modifier
                                                .padding(end = 12.dp)
                                                .size(52.dp)
                                                .clip(CircleShape)
                                                .border(2.dp, Color(0xFFBAE6FD), CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .padding(end = 12.dp)
                                                .size(52.dp)
                                                .background(Color(0xFFE0F2FE), CircleShape)
                                                .border(2.dp, Color(0xFFBAE6FD), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = uiState.userName.firstOrNull()?.uppercase() ?: "K",
                                                color = Color(0xFF0284C7),
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = uiState.userName,
                                            color = TextPrimary,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = uiState.course,
                                            color = TextSecondary,
                                            fontSize = 13.sp,
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = uiState.interests,
                                    color = CyanAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Dashed Continue Button Box
                                DashedBorderBox(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = 4.dp,
                                ) {
                                    Button(
                                        onClick = { onProfileCardClick() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                        contentPadding = PaddingValues(0.dp),
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    brush = Brush.horizontalGradient(
                                                        colors = listOf(
                                                            PurpleAccent,
                                                            BlueAccent,
                                                            CyanAccent,
                                                        )
                                                    ),
                                                    shape = RoundedCornerShape(16.dp),
                                                ),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "→",
                                                    color = Color.White,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Open Full Profile",
                                                    color = Color.White,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Quick AI Modules
                    item {
                        Column {
                            Text(
                                text = "AI Learning Modules",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                items(uiState.quickActions) { action ->
                                    Card(
                                        modifier = Modifier
                                            .width(140.dp)
                                            .clickable { onActionClick(action.id) },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (action.isHighlighted) Color(0xFF161F42) else Color(0xFF0D1430)
                                        ),
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                        ) {
                                            Text(
                                                text = action.iconEmoji,
                                                fontSize = 28.sp,
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = action.title,
                                                color = TextPrimary,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Text(
                                                text = action.subtitle,
                                                color = TextSecondary,
                                                fontSize = 12.sp,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Continue Learning & Recent Sessions
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Recent Sessions",
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                if (uiState.topicsDone > 0) {
                                    Text(
                                        text = "${uiState.topicsDone}/${uiState.totalTopics} completed",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    if (uiState.recentSessions.isEmpty() && !uiState.isLoading) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1430)),
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Start your learning journey",
                                        color = TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Explore subjects below and complete your first chapter.",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(uiState.recentSessions) { session ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSessionClick(session.id) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF0D1430)
                                ),
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Text(
                                            text = session.title,
                                            color = TextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = session.timeAgo,
                                            color = TextMuted,
                                            fontSize = 12.sp,
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = session.category,
                                        color = CyanAccent,
                                        fontSize = 12.sp,
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    LinearProgressIndicator(
                                        progress = { session.progress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp),
                                        color = CyanGlow,
                                        trackColor = Color(0xFF1E293B),
                                    )

                                    // Add Continue button to first active session card
                                    if (session.id == "1") {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        DashedBorderBox(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentPadding = 4.dp,
                                        ) {
                                            Button(
                                                onClick = { onSessionClick(session.id) },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(44.dp),
                                                shape = RoundedCornerShape(14.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                                contentPadding = PaddingValues(0.dp),
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(
                                                            brush = Brush.horizontalGradient(
                                                                colors = listOf(
                                                                    PurpleAccent,
                                                                    BlueAccent,
                                                                    CyanAccent,
                                                                )
                                                            ),
                                                            shape = RoundedCornerShape(14.dp),
                                                        ),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = "→",
                                                            color = Color.White,
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.Bold,
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                            text = "Continue Learning",
                                                            color = Color.White,
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Bold,
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

                    // Subjects Section from MySQL
                    if (uiState.subjects.isNotEmpty()) {
                        item {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Subjects & Mastery",
                                        color = TextPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "View BrainMap ›",
                                        color = CyanGlow,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable { onBrainMapCardClick() }
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))

                                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(uiState.subjects) { subj ->
                                        Card(
                                            modifier = Modifier
                                                .width(135.dp)
                                                .clickable { onSubjectCardClick(subj.slug) },
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1430))
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(text = subj.iconSymbol, fontSize = 22.sp)
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = subj.name,
                                                    color = TextPrimary,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = "${subj.completedChapters}/${subj.totalChapters} chapters",
                                                    color = TextSecondary,
                                                    fontSize = 11.sp
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                LinearProgressIndicator(
                                                    progress = { subj.progressPercent / 100f },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(4.dp),
                                                    color = CyanGlow,
                                                    trackColor = Color(0xFF1E293B)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Performance & Quiz Card
                    item {
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 14.dp
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Quiz & Performance",
                                        color = TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Take Quiz ›",
                                        color = CyanAccent,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable { onActionClick("4") }
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(text = "${uiState.quizScore}%", color = CyanGlow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Average score", color = TextSecondary, fontSize = 11.sp)
                                    }
                                    Column {
                                        Text(text = "${uiState.quizzesAttempted}", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Quizzes completed", color = TextSecondary, fontSize = 11.sp)
                                    }
                                    Column {
                                        Text(text = "${uiState.studyStreakDays} days", color = Color(0xFFF59E0B), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Study streak", color = TextSecondary, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Bottom Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF070B1B))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val tabs = listOf("Home 🏠", "Learn 📚", "AI Chat 🤖", "Profile 👤")
                tabs.forEachIndexed { index, tabName ->
                    val isSelected = index == uiState.selectedTab
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) Color(0xFF161F42) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onTabSelect(index) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = tabName,
                            color = if (isSelected) CyanGlow else TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    SYNAPSETheme {
        HomeContent(
            uiState = HomeUiState(),
            onSearchChange = {},
            onTabSelect = {},
            onActionClick = {},
            onSessionClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudentProfileScreenPreview() {
    SYNAPSETheme {
        HomeContent(
            uiState = HomeUiState(selectedTab = 3),
            onSearchChange = {},
            onTabSelect = {},
            onActionClick = {},
            onSessionClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
