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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
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

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier,
    onAskAiClick: (() -> Unit)? = null,
    onAiChatQuerySubmit: ((String) -> Unit)? = null,
    onLearnTabClick: (() -> Unit)? = null,
    onBrainMapClick: (() -> Unit)? = null,
    onFlashcardsClick: (() -> Unit)? = null,
    onQuizTabClick: (() -> Unit)? = null,
    onNotesTabClick: (() -> Unit)? = null,
    onProfileTabClick: (() -> Unit)? = null,
    onNotificationClick: (() -> Unit)? = null,
    onProgressAnalyticsClick: (() -> Unit)? = null,
    onContinueLessonClick: ((subjectSlug: String, chapterId: Int) -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DashboardContent(
        uiState = uiState,
        onAiChatQuerySubmit = onAiChatQuerySubmit,
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
        onAddTask = { title, cat, dur ->
            viewModel.addLearningTask(title, cat, dur)
        },
        onToggleTask = { id, completed ->
            viewModel.toggleTaskCompletion(id, completed)
        },
        onDeleteTask = { id ->
            viewModel.deleteLearningTask(id)
        },
        onProgressAnalyticsClick = onProgressAnalyticsClick,
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
    onAiChatQuerySubmit: ((String) -> Unit)? = null,
    onBrainMapClick: (() -> Unit)? = null,
    onNotificationClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null,
    onProgressAnalyticsClick: (() -> Unit)? = null,
    onAddTask: (title: String, category: String, duration: String) -> Unit = { _, _, _ -> },
    onToggleTask: (id: String, completed: Boolean) -> Unit = { _, _ -> },
    onDeleteTask: (id: String) -> Unit = {},
) {
    val isDark = LocalThemeIsDark.current
    var showAddTaskDialog by remember { mutableStateOf(false) }
    val pageBgGradient = if (isDark) {
        listOf(DarkBackgroundTop, Color(0xFF090D22), DarkBackgroundBottom)
    } else {
        listOf(Color(0xFFE0E7FF), Color(0xFFEEF2F6), LightBackground)
    }
    val headerTextColor = if (isDark) TextPrimary else LightTextPrimary
    val controlBgColor = if (isDark) Color(0xFF161F42) else Color.White
    val controlBorderColor = if (isDark) Color.Transparent else Color(0xFFE2E8F0)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = pageBgGradient))
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
                            color = if (isDark) TextMuted else BlueAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Hello, ${uiState.userName} 👋",
                            color = headerTextColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Bell Notification Button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(controlBgColor, CircleShape)
                                .border(1.dp, controlBorderColor, CircleShape)
                                .clickable { onNotificationClick?.invoke() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = "🔔", fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Top Right Corner Profile Icon (Photo or K Avatar)
                        val photoModel = RetrofitClient.resolveImageModel(uiState.profilePhotoUri)
                        if (photoModel != null) {
                            AsyncImage(
                                model = photoModel,
                                contentDescription = "Profile Photo",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, SunsetOrange, CircleShape)
                                    .clickable { onProfileClick?.invoke() },
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(SunsetOrange.copy(alpha = 0.2f), CircleShape)
                                    .border(1.dp, SurfaceBorder, CircleShape)
                                    .clickable { onProfileClick?.invoke() },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = uiState.userName.trim().firstOrNull()?.uppercase() ?: "K",
                                    color = SunsetOrange,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
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
                        .border(1.dp, Color(0xFFFF6A00).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .clickable { onAskAiClick() }
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
                            color = TextSecondary,
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

            // Content Container (Design 4 Transparent on Warm Dark Background)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(color = Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. "Your learning journey" Card (Design 4 SurfacePanel)
                    DashedBorderBox(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = 2.dp
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfacePanel),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
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
                                            color = TextPrimary,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = if (uiState.overallProgress == 100) "Goal achieved! Outstanding discipline." else uiState.progressMessage,
                                            color = TextSecondary,
                                            fontSize = 12.sp,
                                        )
                                    }

                                    Box(
                                        modifier = Modifier.size(64.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        androidx.compose.material3.CircularProgressIndicator(
                                            progress = { (uiState.overallProgress / 100f).coerceIn(0f, 1f) },
                                            modifier = Modifier.fillMaxSize(),
                                            color = SunsetOrange,
                                            trackColor = Color(0xFF1E212D),
                                            strokeWidth = 6.dp
                                        )
                                        Text(
                                            text = "${uiState.overallProgress}%",
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 2. Stats Grid (3 Design 4 Cards)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Card 1: Topics done
                        DashedBorderBox(
                            modifier = Modifier.weight(1f),
                            contentPadding = 2.dp
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfacePanelElevated),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "${uiState.topicsDone}",
                                        color = TextPrimary,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "Topics done",
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                        }

                        // Card 2: Quiz score
                        DashedBorderBox(
                            modifier = Modifier.weight(1f),
                            contentPadding = 2.dp
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfacePanelElevated),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "${uiState.quizScore}%",
                                        color = AmberGold,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "Quiz score",
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                        }

                        // Card 3: Study streak
                        DashedBorderBox(
                            modifier = Modifier.weight(1f),
                            contentPadding = 2.dp
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfacePanelElevated),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "${uiState.studyStreakDays} days",
                                        color = SunsetOrange,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "Study streak",
                                        color = TextSecondary,
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
                            colors = CardDefaults.cardColors(containerColor = SurfacePanel),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column {
                                        Text(
                                            text = "Today's Task",
                                            color = TextPrimary,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = "Organize daily tasks & placement goals",
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .border(1.dp, SunsetOrange.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                                                .background(SurfacePanelElevated, RoundedCornerShape(10.dp))
                                                .clickable { showAddTaskDialog = true }
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        ) {
                                            Text(
                                                text = "+ Add a task",
                                                color = SunsetOrange,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                if (uiState.todayPlanItems.isEmpty()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = "📝", fontSize = 28.sp)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "No tasks added yet",
                                            color = TextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Tap '+ Add a task' to plan your 2-min micro-lessons for today!",
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Box(
                                            modifier = Modifier
                                                .border(1.dp, SunsetOrange.copy(alpha = 0.8f), RoundedCornerShape(10.dp))
                                                .background(SurfacePanelElevated, RoundedCornerShape(10.dp))
                                                .clickable { showAddTaskDialog = true }
                                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = "+ Add Today's Plan",
                                                color = SunsetOrange,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        uiState.todayPlanItems.forEach { item ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(
                                                        color = SurfacePanelElevated,
                                                        shape = RoundedCornerShape(12.dp)
                                                    )
                                                    .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Checkbox to mark complete
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .background(
                                                            color = if (item.isCompleted) SunsetOrange else Color.Transparent,
                                                            shape = CircleShape
                                                        )
                                                        .border(
                                                            width = 1.5.dp,
                                                            color = if (item.isCompleted) SunsetOrange else Color(0xFF6B7280),
                                                            shape = CircleShape
                                                        )
                                                        .clickable { onToggleTask(item.id, !item.isCompleted) },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (item.isCompleted) {
                                                        Text(text = "✓", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }

                                                Spacer(modifier = Modifier.width(10.dp))

                                                Column(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clickable { onToggleTask(item.id, !item.isCompleted) }
                                                ) {
                                                    Text(
                                                        text = item.title,
                                                        color = if (item.isCompleted) Color(0xFF6B7280) else TextPrimary,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        textDecoration = if (item.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = "${item.category} • ${item.duration}",
                                                        color = AmberGold,
                                                        fontSize = 11.sp
                                                    )
                                                }

                                                // Delete task button
                                                IconButton(
                                                    onClick = { onDeleteTask(item.id) },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Text(text = "✕", color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Progress & Analytics Card (Home Screen Bottom Option)
                    DashedBorderBox(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = 2.dp
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onProgressAnalyticsClick?.invoke() },
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfacePanelElevated),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
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
                                            color = TextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Study time, tracks breakdown & streaks",
                                            color = TextSecondary,
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
            }


        }
    }

    if (showAddTaskDialog) {
        var newTaskTitle by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf("Lesson") }
        var selectedDuration by remember { mutableStateOf("15 min") }

        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = {
                Text(
                    text = "Add Learning Plan Task",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Task Title",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        placeholder = { Text("e.g. Master Linear Algebra", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = if (isDark) SurfacePanel else Color.White,
                            unfocusedContainerColor = if (isDark) SurfacePanel else Color.White,
                            focusedBorderColor = SunsetOrange,
                            unfocusedBorderColor = if (isDark) SurfaceBorder else Color(0xFFCBD5E1),
                            focusedTextColor = if (isDark) TextPrimary else Color(0xFF0F172A),
                            unfocusedTextColor = if (isDark) TextPrimary else Color(0xFF0F172A),
                            cursorColor = SunsetOrange
                        )
                    )

                    Text(
                        text = "Task Type",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) TextPrimary else Color(0xFF1E293B)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Lesson", "Quiz", "Revision").forEach { cat ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (selectedCategory == cat) SunsetOrange else (if (isDark) SurfacePanel else Color(0xFFF1F5F9)),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedCategory = cat }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    color = if (selectedCategory == cat) Color.White else (if (isDark) TextSecondary else Color(0xFF334155)),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = "Estimated Duration",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) TextPrimary else Color(0xFF1E293B)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("10 min", "15 min", "25 min", "45 min").forEach { dur ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (selectedDuration == dur) SunsetOrange else (if (isDark) SurfacePanel else Color(0xFFF1F5F9)),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedDuration = dur }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = dur,
                                    color = if (selectedDuration == dur) Color.White else (if (isDark) TextSecondary else Color(0xFF334155)),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            onAddTask(newTaskTitle, selectedCategory, selectedDuration)
                            showAddTaskDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Add Task", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("Cancel", color = if (isDark) TextSecondary else Color(0xFF64748B))
                }
            },
            containerColor = if (isDark) SurfacePanelElevated else Color.White,
            shape = RoundedCornerShape(16.dp)
        )
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
