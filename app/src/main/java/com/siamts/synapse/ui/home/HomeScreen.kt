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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.siamts.synapse.data.local.SynapseDatabase
import com.siamts.synapse.data.remote.RetrofitClient
import com.siamts.synapse.ui.components.NotificationCenterSheet
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.DarkBackground
import com.siamts.synapse.ui.theme.LightBackground
import com.siamts.synapse.ui.theme.LightTextPrimary
import com.siamts.synapse.ui.theme.LightTextSecondary
import com.siamts.synapse.ui.theme.LocalThemeIsDark
import com.siamts.synapse.ui.theme.PurpleAccent
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.SunsetOrange
import com.siamts.synapse.ui.theme.SunsetOrangeEnd
import com.siamts.synapse.ui.theme.AmberGold
import com.siamts.synapse.ui.theme.AmberGoldEnd
import com.siamts.synapse.ui.theme.AmberAccent
import com.siamts.synapse.ui.theme.AmberGlow
import com.siamts.synapse.ui.theme.SurfacePanel
import com.siamts.synapse.ui.theme.SurfacePanelElevated
import com.siamts.synapse.ui.theme.SurfaceBorder
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    onSubjectClick: ((String) -> Unit)? = null,
    onContinueLessonClick: ((subjectSlug: String, chapterId: Int) -> Unit)? = null,
    onAiChatClick: (() -> Unit)? = null,
    onAiChatQuerySubmit: ((String) -> Unit)? = null,
    onFlashcardsClick: (() -> Unit)? = null,
    onQuizClick: (() -> Unit)? = null,
    onNotesClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null,
    onBrainMapClick: (() -> Unit)? = null,
    onProgressAnalyticsClick: (() -> Unit)? = null,
    onStreakCalendarClick: (() -> Unit)? = null,
    onToggleTheme: (() -> Unit)? = null,
    onNotificationActionRoute: ((String) -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showNotificationSheet by remember { mutableStateOf(false) }

    HomeContent(
        uiState = uiState,
        onToggleTheme = onToggleTheme,
        onNotificationClick = { showNotificationSheet = true },
        onSearchChange = { query -> viewModel.onSearchQueryChanged(query) },
        onTabSelect = { index ->
            viewModel.onTabSelected(index)
            when (index) {
                0 -> { /* Already on Home */ }
                1 -> onSubjectClick?.invoke("")
                2 -> onQuizClick?.invoke()
                3 -> onNotesClick?.invoke()
                4 -> onProfileClick?.invoke()
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
        onAddTaskClick = { viewModel.setAddTaskDialogVisible(true) },
        onTaskToggle = { taskId, isCompleted -> viewModel.toggleStudyTask(taskId, isCompleted) },
        onTaskDelete = { taskId -> viewModel.deleteStudyTask(taskId) },
        onConfirmAddTask = { title, duration ->
            viewModel.addStudyTask(title, duration)
            viewModel.setAddTaskDialogVisible(false)
        },
        onDismissAddTask = { viewModel.setAddTaskDialogVisible(false) },
        onAiChatClick = { onAiChatClick?.invoke() },
        onAiChatQuerySubmit = onAiChatQuerySubmit,
        onProgressAnalyticsClick = onProgressAnalyticsClick,
        onStreakCalendarClick = onStreakCalendarClick,
        onContinueLessonClick = onContinueLessonClick,
        modifier = modifier,
    )

    if (showNotificationSheet) {
        NotificationCenterSheet(
            onDismissRequest = { showNotificationSheet = false },
            onNavigateAction = { route ->
                showNotificationSheet = false
                when (route) {
                    "quiz_hub" -> onQuizClick?.invoke()
                    "learn" -> onSubjectClick?.invoke("")
                    "mistake_vault" -> onNotificationActionRoute?.invoke("mistake_vault")
                    "home" -> { /* Already on Home */ }
                    "milestone" -> onNotificationActionRoute?.invoke("milestone")
                    else -> onNotificationActionRoute?.invoke(route)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
    onAddTaskClick: () -> Unit = {},
    onTaskToggle: (Long, Boolean) -> Unit = { _, _ -> },
    onTaskDelete: (Long) -> Unit = {},
    onConfirmAddTask: (String, String) -> Unit = { _, _ -> },
    onDismissAddTask: () -> Unit = {},
    onAiChatClick: () -> Unit = {},
    onAiChatQuerySubmit: ((String) -> Unit)? = null,
    onProgressAnalyticsClick: (() -> Unit)? = null,
    onStreakCalendarClick: (() -> Unit)? = null,
    onContinueLessonClick: ((String, Int) -> Unit)? = null,
    onToggleTheme: (() -> Unit)? = null,
    onNotificationClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val isDark = LocalThemeIsDark.current
    val pageBgGradient = if (isDark) {
        listOf(DarkBackgroundTop, Color(0xFF0C0D12), DarkBackgroundBottom)
    } else {
        listOf(Color(0xFFF5F6FA), Color(0xFFF5F6FA))
    }
    val cardBg = if (isDark) SurfacePanel else Color.White
    val cardBorder = if (isDark) SurfaceBorder else Color(0xFFE4E7F0)
    val elevatedBg = if (isDark) SurfacePanelElevated else Color(0xFFEEF1F7)
    val textPrimaryColor = if (isDark) TextPrimary else Color(0xFF11131A)
    val textSecondaryColor = if (isDark) TextSecondary else Color(0xFF5A6075)
    val headerTextColor = textPrimaryColor
    var showStreakSheet by remember { mutableStateOf(false) }
    var showTopicsDoneSheet by remember { mutableStateOf(false) }
    var showQuizScoreSheet by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = pageBgGradient))
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
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 10.dp)
                    ) {
                        Text(
                            text = "Hello, ${uiState.userName.ifBlank { "Kishore" }}! 👋",
                            color = headerTextColor,
                            fontSize = if (uiState.userName.length > 15) 18.sp else 22.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Daily Micro-Learning & Placement Companion",
                            color = SunsetOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Sleek Small Theme Toggle Icon in top-right corner
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(elevatedBg, CircleShape)
                                .border(1.dp, cardBorder, CircleShape)
                                .clickable { onToggleTheme?.invoke() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = if (isDark) "🌙" else "☀️",
                                fontSize = 18.sp
                            )
                        }

                        // Bell Notification Button with Real-Time Unread Badge
                        val context = LocalContext.current
                        val unreadNotifsCount by remember {
                            SynapseDatabase.getNotificationDao(context).getUnreadCountFlow()
                        }.collectAsState(initial = 0)

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(elevatedBg, CircleShape)
                                .border(1.dp, cardBorder, CircleShape)
                                .clickable { onNotificationClick?.invoke() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = "🔔", fontSize = 16.sp)
                            if (unreadNotifsCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(top = 4.dp, end = 4.dp)
                                        .size(9.dp)
                                        .background(SunsetOrange, CircleShape)
                                        .border(1.5.dp, elevatedBg, CircleShape)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sunset Amber Interactive AI Search Bar (Tap anywhere opens AI Chat)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(if (isDark) Color(0xFF13141B) else Color.White, RoundedCornerShape(16.dp))
                        .border(
                            1.dp,
                            if (isDark) Color(0xFFFF6A00).copy(alpha = 0.4f) else cardBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onAiChatClick() }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(SunsetOrange.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "✨", fontSize = 15.sp, color = SunsetOrange)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Chat with SYNAPSE — Ask any placement doubt...",
                            color = textSecondaryColor,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier.size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎙️", fontSize = 16.sp)
                        }
                    }
                }
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
                                    val formattedPhoto = RetrofitClient.resolveImageModel(uiState.profilePhotoUri)
                                    if (formattedPhoto != null) {
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

                                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                        Text(
                                            text = uiState.userName.ifBlank { "Kishore" },
                                            color = TextPrimary,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = uiState.course,
                                            color = TextSecondary,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
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
                    // 1. Top Micro-Learning Goal & Stats Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "TODAY'S OVERALL PROGRESS",
                                            color = SunsetOrange,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${uiState.overallProgress}% Completed",
                                            color = textPrimaryColor,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (uiState.overallProgress == 100) "Goal achieved! Outstanding discipline." else "Master 2-min daily concepts & tasks",
                                            color = textSecondaryColor,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Box(
                                        modifier = Modifier.size(64.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            progress = { (uiState.overallProgress / 100f).coerceIn(0f, 1f) },
                                            modifier = Modifier.fillMaxSize(),
                                            color = SunsetOrange,
                                            trackColor = if (isDark) Color(0xFF1E212D) else Color(0xFFE2E8F0),
                                            strokeWidth = 6.dp
                                        )
                                        Text(
                                            text = "${uiState.overallProgress}%",
                                            color = textPrimaryColor,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // 3 Metric Badges: '0 Topics done', '0% Quiz score', '0 days Study streak'
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Metric 1: Topics done (Interactive Progress Sheet Trigger)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(elevatedBg, RoundedCornerShape(12.dp))
                                            .border(
                                                1.dp,
                                                Color(0xFF3B82F6).copy(alpha = 0.5f),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .clickable { showTopicsDoneSheet = true }
                                            .padding(vertical = 10.dp, horizontal = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "${uiState.topicsDone}",
                                                    color = textPrimaryColor,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(text = "📚", fontSize = 11.sp)
                                            }
                                            Text(
                                                text = "Topics done",
                                                color = textSecondaryColor,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    // Metric 2: Quiz score (Interactive Performance Sheet Trigger)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(elevatedBg, RoundedCornerShape(12.dp))
                                            .border(
                                                1.dp,
                                                AmberGold.copy(alpha = 0.5f),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .clickable { showQuizScoreSheet = true }
                                            .padding(vertical = 10.dp, horizontal = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "${uiState.quizScore}%",
                                                    color = AmberGold,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(text = "🎯", fontSize = 11.sp)
                                            }
                                            Text(
                                                text = "Quiz score",
                                                color = textSecondaryColor,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    // Metric 3: Study streak (Interactive Streak Sheet Trigger)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(elevatedBg, RoundedCornerShape(12.dp))
                                            .border(
                                                1.dp,
                                                SunsetOrange.copy(alpha = 0.6f),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .clickable { showStreakSheet = true }
                                            .padding(vertical = 10.dp, horizontal = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "${uiState.studyStreakDays} days",
                                                    color = SunsetOrange,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(text = "🔥", fontSize = 12.sp)
                                            }
                                            Text(
                                                text = "Study streak",
                                                color = textSecondaryColor,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 2. Daily Study Tasks Organizer
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Today's Task",
                                        color = textPrimaryColor,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Organize daily tasks & placement goals",
                                        color = textSecondaryColor,
                                        fontSize = 12.sp
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .border(1.dp, SunsetOrange.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                                        .background(elevatedBg, RoundedCornerShape(12.dp))
                                        .clickable { onAddTaskClick() }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "+ Add a task",
                                        color = SunsetOrange,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (uiState.dailyTasks.isEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = cardBg),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "📋", fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "No tasks added yet",
                                                color = textPrimaryColor,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "Tap '+ Add a task' to plan your 2-min micro-lessons for today!",
                                                color = textSecondaryColor,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    uiState.dailyTasks.forEach { task ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(14.dp),
                                            colors = CardDefaults.cardColors(containerColor = cardBg),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (task.isCompleted) Color(0xFF059669) else cardBorder
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Checkbox(
                                                    checked = task.isCompleted,
                                                    onCheckedChange = { isChecked ->
                                                        onTaskToggle(task.id, isChecked)
                                                    },
                                                    colors = CheckboxDefaults.colors(
                                                        checkedColor = SunsetOrange,
                                                        uncheckedColor = Color(0xFF6B7280)
                                                    )
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = task.title,
                                                        color = if (task.isCompleted) Color(0xFF6B7280) else textPrimaryColor,
                                                        fontSize = 14.sp,
                                                        fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                                                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                                    )
                                                    Text(
                                                        text = "⏱ ${task.duration} · ${task.category}",
                                                        color = if (task.isCompleted) Color(0xFF059669) else AmberGold,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                                Text(
                                                    text = "✕",
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier
                                                        .clickable { onTaskDelete(task.id) }
                                                        .padding(8.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Quick AI Modules
                    item {
                        Column {
                            Text(
                                text = "AI Learning Modules",
                                color = textPrimaryColor,
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
                                            containerColor = if (isDark) {
                                                if (action.isHighlighted) Color(0xFF161F42) else Color(0xFF0D1430)
                                            } else {
                                                if (action.isHighlighted) Color(0xFFFFF7ED) else cardBg
                                            }
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
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
                                                color = textPrimaryColor,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Text(
                                                text = action.subtitle,
                                                color = textSecondaryColor,
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
                                    color = textPrimaryColor,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                if (uiState.topicsDone > 0) {
                                    Text(
                                        text = "${uiState.topicsDone}/${uiState.totalTopics} completed",
                                        color = textSecondaryColor,
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onContinueLessonClick?.invoke("PYTHON", 1)
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Start Track 1: Python & Coding Logic",
                                            color = textPrimaryColor,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    brush = Brush.horizontalGradient(listOf(SunsetOrange, SunsetOrangeEnd)),
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "Start ›",
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Begin your 2-minute daily concept lessons · 15 min",
                                        color = textSecondaryColor,
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
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
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
                                            color = textPrimaryColor,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = session.timeAgo,
                                            color = textSecondaryColor,
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
                                        trackColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0),
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
                                        color = textPrimaryColor,
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
                                            colors = CardDefaults.cardColors(containerColor = cardBg),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(text = subj.iconSymbol, fontSize = 22.sp)
                                                    if (subj.isInterested) {
                                                        Text(
                                                            text = "★ Interested",
                                                            color = CyanGlow,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = subj.name,
                                                    color = textPrimaryColor,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = "${subj.completedChapters}/${subj.totalChapters} chapters",
                                                    color = textSecondaryColor,
                                                    fontSize = 11.sp
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                LinearProgressIndicator(
                                                    progress = { subj.progressPercent / 100f },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(4.dp),
                                                    color = CyanGlow,
                                                    trackColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
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
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Quiz & Performance",
                                        color = textPrimaryColor,
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
                                        Text(text = "Average score", color = textSecondaryColor, fontSize = 11.sp)
                                    }
                                    Column {
                                        Text(text = "${uiState.quizzesAttempted}", color = textPrimaryColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Quizzes completed", color = textSecondaryColor, fontSize = 11.sp)
                                    }
                                    Column {
                                        Text(text = "${uiState.studyStreakDays} days", color = Color(0xFFF59E0B), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Study streak", color = textSecondaryColor, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    DashedBorderBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        contentPadding = 2.dp
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onProgressAnalyticsClick?.invoke() },
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .background(SunsetOrange.copy(alpha = 0.15f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "📊", fontSize = 18.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Progress & Analytics",
                                            color = textPrimaryColor,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Study time, tracks breakdown & streaks",
                                            color = textSecondaryColor,
                                            fontSize = 11.sp,
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .background(
                                            brush = Brush.horizontalGradient(listOf(SunsetOrange, SunsetOrangeEnd)),
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .clickable { onProgressAnalyticsClick?.invoke() }
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "View Analytics ›",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }


        }
    }

    // Interactive Detail Popup Sheets for Topics Done, Quiz Score, and Study Streak
    if (showTopicsDoneSheet) {
        TopicsDoneBottomSheet(
            onDismissRequest = { showTopicsDoneSheet = false },
            topicsDone = uiState.topicsDone,
            totalTopics = uiState.totalTopics,
            overallProgress = uiState.overallProgress,
            subjects = uiState.subjects,
            onSubjectClick = { slug ->
                showTopicsDoneSheet = false
                onSubjectCardClick(slug)
            },
            onExploreAllClick = {
                showTopicsDoneSheet = false
                onTabSelect(1)
            }
        )
    }

    if (showQuizScoreSheet) {
        QuizScoreBottomSheet(
            onDismissRequest = { showQuizScoreSheet = false },
            quizScore = uiState.quizScore,
            quizzesAttempted = uiState.quizzesAttempted,
            latestQuizScore = uiState.latestQuizScore,
            onTakeQuizClick = {
                showQuizScoreSheet = false
                onTabSelect(2)
            }
        )
    }

    if (showStreakSheet) {
        StudyStreakBottomSheet(
            onDismissRequest = { showStreakSheet = false },
            studyStreakDays = uiState.studyStreakDays,
            bestStreakDays = uiState.bestStreakDays,
            weeklyActiveDays = uiState.weeklyActiveDays,
            onViewCalendarClick = {
                showStreakSheet = false
                onStreakCalendarClick?.invoke() ?: onProgressAnalyticsClick?.invoke()
            }
        )
    }

    // Add Daily Study Task Dialog
    if (uiState.showAddTaskDialog) {
        var taskTitleInput by remember { mutableStateOf("") }
        var selectedDuration by remember { mutableStateOf("15 min") }
        val durationOptions = listOf("10 min", "15 min", "30 min", "45 min")

        AlertDialog(
            onDismissRequest = onDismissAddTask,
            containerColor = if (isDark) SurfacePanelElevated else Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "Add Daily Study Task",
                    color = if (isDark) TextPrimary else Color(0xFF0F172A),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Task Title / Concept",
                        color = if (isDark) TextSecondary else Color(0xFF475569),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = taskTitleInput,
                        onValueChange = { taskTitleInput = it },
                        placeholder = { Text(text = "e.g. Master Python List Comprehensions", color = if (isDark) TextSecondary else Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = if (isDark) TextPrimary else Color(0xFF0F172A),
                            unfocusedTextColor = if (isDark) TextPrimary else Color(0xFF0F172A),
                            focusedBorderColor = SunsetOrange,
                            unfocusedBorderColor = if (isDark) SurfaceBorder else Color(0xFFCBD5E1),
                            focusedContainerColor = if (isDark) SurfacePanel else Color(0xFFF8FAFC),
                            unfocusedContainerColor = if (isDark) SurfacePanel else Color(0xFFF8FAFC)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Estimated Study Duration",
                        color = if (isDark) TextSecondary else Color(0xFF475569),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        durationOptions.forEach { dur ->
                            val isSel = selectedDuration == dur
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSel) SunsetOrange else (if (isDark) SurfacePanel else Color(0xFFF1F5F9)),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedDuration = dur }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dur,
                                    color = if (isSel) Color.White else (if (isDark) TextSecondary else Color(0xFF334155)),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (taskTitleInput.isNotBlank()) {
                            onConfirmAddTask(taskTitleInput, selectedDuration)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                    shape = RoundedCornerShape(10.dp),
                    enabled = taskTitleInput.isNotBlank()
                ) {
                    Text(text = "Add Task", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissAddTask) {
                    Text(text = "Cancel", color = if (isDark) TextSecondary else Color(0xFF64748B))
                }
            }
        )
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
