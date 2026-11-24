package com.siamts.synapse.ui.lesson

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siamts.synapse.ai.LocalAIEngine
import com.siamts.synapse.ai.VoiceAssistantManager
import com.siamts.synapse.data.local.BaseCurriculumData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.PurpleAccent
import com.siamts.synapse.ui.theme.SunsetOrange
import com.siamts.synapse.ui.theme.SunsetOrangeEnd
import com.siamts.synapse.ui.theme.AmberGold
import com.siamts.synapse.ui.theme.AmberGoldEnd
import com.siamts.synapse.ui.theme.SurfacePanel
import com.siamts.synapse.ui.theme.SurfacePanelElevated
import com.siamts.synapse.ui.theme.SurfaceBorder
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

typealias LessonContentViewModel = ChapterLessonViewModel
typealias LearningRoadmapViewModel = com.siamts.synapse.ui.roadmap.RoadmapViewModel

@Composable
fun LessonContentScreen(
    chapterId: Int,
    viewModel: ChapterLessonViewModel,
    onBackClick: () -> Unit,
    onNavigateToChapter: (Int) -> Unit,
    onTakeQuiz: (Int) -> Unit,
    modifier: Modifier = Modifier,
    subjectId: String? = null,
) {
    ChapterLessonScreen(
        chapterId = chapterId,
        viewModel = viewModel,
        onBackClick = onBackClick,
        onNavigateToChapter = onNavigateToChapter,
        onTakeQuiz = onTakeQuiz,
        modifier = modifier,
        subjectId = subjectId,
    )
}

@Composable
fun LearningRoadmapScreen(
    viewModel: com.siamts.synapse.ui.roadmap.RoadmapViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onContinueClick: (() -> Unit)? = null,
    onChapterClick: ((Int) -> Unit)? = null,
) {
    com.siamts.synapse.ui.roadmap.RoadmapScreen(
        viewModel = viewModel,
        onBackClick = onBackClick,
        modifier = modifier,
        onContinueClick = onContinueClick,
        onChapterClick = onChapterClick,
    )
}

@Composable
fun ChapterLessonScreen(
    chapterId: Int,
    viewModel: ChapterLessonViewModel,
    onBackClick: () -> Unit,
    onNavigateToChapter: (Int) -> Unit,
    onTakeQuiz: (Int) -> Unit,
    modifier: Modifier = Modifier,
    subjectId: String? = null,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = isSystemInDarkTheme() || (MaterialTheme.colorScheme.background.luminance() < 0.5f)

    var showSmartNotesDialog by remember { mutableStateOf(false) }
    var isSavingSmartNote by remember { mutableStateOf(false) }
    var isNotesSavedTemporarily by remember { mutableStateOf(false) }
    var saveFeedbackMessage by remember { mutableStateOf<String?>(null) }
    var isTtsSpeaking by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose {
            try {
                VoiceAssistantManager.getInstance(context).stopSpeaking()
            } catch (_: Throwable) {}
        }
    }

    LaunchedEffect(chapterId, subjectId) {
        if (chapterId > 0 && (uiState.chapterId != chapterId || uiState.part1Content.isBlank())) {
            viewModel.loadChapterLesson(chapterId, subjectId)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isDark) listOf(
                        DarkBackgroundTop,
                        Color(0xFF0C0D12),
                        DarkBackgroundBottom
                    ) else listOf(
                        Color(0xFFF5F6FA),
                        Color(0xFFF5F6FA)
                    )
                )
            )
    ) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CyanAccent)
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header Box
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
                                color = TextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${uiState.subjectName.uppercase()} · CHAPTER ${uiState.chapterOrder}",
                                color = CyanAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = uiState.chapterTitle.ifEmpty { "Lesson Content" },
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    if (uiState.isCompleted) Color(0xFF059669).copy(alpha = 0.2f) else SurfacePanelElevated,
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    1.dp,
                                    if (uiState.isCompleted) Color(0xFF059669) else SurfaceBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (uiState.isCompleted) "✓ Completed" else "${uiState.durationMinutes} min",
                                color = if (uiState.isCompleted) Color(0xFF34D399) else SunsetOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }

                // Content Container (Design 4 Seamless Dark Theme)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(color = Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // High-Utility Mode Toggle: Full Concept Guide | Quick Cheat Sheet
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfacePanelElevated, RoundedCornerShape(14.dp))
                                .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val isFull = !uiState.isCheatSheetMode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isFull) Brush.horizontalGradient(listOf(SunsetOrange, SunsetOrangeEnd)) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.setCheatSheetMode(false) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Full Concept Guide",
                                    color = if (isFull) Color.White else TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            val isCheat = uiState.isCheatSheetMode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isCheat) Brush.horizontalGradient(listOf(AmberGold, AmberGoldEnd)) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.setCheatSheetMode(true) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "⚡ Quick Cheat Sheet",
                                    color = if (isCheat) Color.White else TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (!uiState.isCheatSheetMode) {
                            // Spacious Full-Width Educational Reader (4 Structured Sections, 16.sp & 24.sp line-height)
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Section 1: Concept Overview & Real-Life Analogy
                                SectionHeader(title = "1. Concept Overview & Real-Life Analogy", icon = "💡")
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfacePanel),
                                    border = BorderStroke(1.dp, SurfaceBorder)
                                ) {
                                    Column(modifier = Modifier.padding(18.dp)) {
                                        Text(
                                            text = uiState.chapterTitle.ifEmpty { "Core Concept" },
                                            color = TextPrimary,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = uiState.part1Content,
                                            color = TextPrimary,
                                            fontSize = 16.sp,
                                            lineHeight = 24.sp
                                        )
                                    }
                                }

                                // Section 2: Core Syntax / Formula / Code Example
                                SectionHeader(title = "2. Core Syntax / Formula / Code Example", icon = "⚡")
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF08090C)),
                                    border = BorderStroke(1.dp, SurfaceBorder)
                                ) {
                                    Column(modifier = Modifier.padding(18.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "CODE / FORMULA SPECIFICATION",
                                                color = SunsetOrange,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.sp
                                            )
                                            Text(
                                                text = "SYNTAX",
                                                color = AmberGold,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        val codeOrFormulaText = if (uiState.coreFormulas.isNotEmpty()) {
                                            uiState.coreFormulas.joinToString("\n\n") { "• $it" }
                                        } else {
                                            uiState.part2Content
                                        }
                                        Text(
                                            text = codeOrFormulaText,
                                            color = TextPrimary,
                                            fontSize = 14.sp,
                                            lineHeight = 22.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                // Section 3: Key Takeaways & Placement Interview Tips
                                SectionHeader(title = "3. Key Takeaways & Placement Interview Tips", icon = "🎯")
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfacePanelElevated),
                                    border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.4f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(18.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = "🎯", fontSize = 18.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "High-Yield Placement Principles",
                                                color = AmberGold,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (uiState.syllabus.isNotEmpty()) {
                                            uiState.syllabus.forEach { point ->
                                                Row(verticalAlignment = Alignment.Top) {
                                                    Text(text = "• ", color = SunsetOrange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                    Text(text = point, color = TextPrimary, fontSize = 14.sp, lineHeight = 20.sp)
                                                }
                                            }
                                        } else {
                                            Text(
                                                text = "Master time & space complexity, edge-case validation, and clean idioms for technical interview coding rounds.",
                                                color = TextSecondary,
                                                fontSize = 14.sp,
                                                lineHeight = 22.sp
                                            )
                                        }
                                    }
                                }

                                // Section 4: Quick Self-Check Q&A
                                SectionHeader(title = "4. Quick Self-Check Q&A", icon = "❓")
                                QuickSelfCheckSection(chapterId = uiState.chapterId)

                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        } else {
                            // 5-Min Quick Revision Cheat Sheet
                            ChapterCheatSheetView(
                                uiState = uiState,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Bottom Actions Bar (with 12.dp margin and natural flat orange color gradient buttons)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Row 1: Save to Smart Notes + Listen (TTS)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        saveFeedbackMessage = null
                                        showSmartNotesDialog = true
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(
                                                        SunsetOrange,
                                                        SunsetOrangeEnd
                                                    )
                                                ),
                                                RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "📝 Save Smart Notes",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        val tts = VoiceAssistantManager.getInstance(context)
                                        if (isTtsSpeaking) {
                                            tts.stopSpeaking()
                                            isTtsSpeaking = false
                                        } else {
                                            val rawText = if (uiState.isCheatSheetMode) {
                                                "${uiState.chapterTitle}. Key Concepts: " + uiState.coreFormulas.joinToString(". ")
                                            } else if (uiState.currentPartIndex == 0) {
                                                "${uiState.part1Title}. ${uiState.part1Content}"
                                            } else {
                                                "${uiState.part2Title}. ${uiState.part2Content}"
                                            }
                                            val cleanText = rawText.replace(Regex("[#*`•⚡💡✨📘🎯]"), "").trim()
                                            if (cleanText.isNotBlank()) {
                                                isTtsSpeaking = true
                                                tts.speak(cleanText) {
                                                    isTtsSpeaking = false
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isTtsSpeaking) Color(0xFFEF4444) else SurfacePanelElevated
                                    ),
                                    border = BorderStroke(1.dp, SurfaceBorder),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = if (isTtsSpeaking) "⏹️ Stop Audio" else "🔊 Listen (TTS)",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            // Row 2: Mark Done + Take 5-Min Quiz
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.markAsCompleted()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (uiState.isCompleted) Color(0xFF059669) else SunsetOrange
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !uiState.isMarkingComplete
                                ) {
                                    if (uiState.isMarkingComplete) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                                    } else {
                                        Text(
                                            text = if (uiState.isCompleted) "✓ Mark Done" else "Mark Done",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onTakeQuiz(uiState.chapterId) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(
                                                        AmberGold,
                                                        AmberGoldEnd
                                                    )
                                                ),
                                                RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Take 5-Min Quiz ✍️",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }

                            // Track Completion State
                            if (uiState.nextChapterId == null && uiState.isCompleted) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD1FAE5))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "✓ TRACK COMPLETED!",
                                            color = Color(0xFF065F46),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "You've completed all lessons in ${uiState.subjectName}!",
                                            color = Color(0xFF047857),
                                            fontSize = 11.sp,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            val firstChapterId = BaseCurriculumData.getSubjectById(
                                                uiState.subjectSlug.ifBlank { uiState.subjectId.toString() }
                                            )?.chapters?.firstOrNull()?.chapterId ?: (uiState.chapterId - 4)

                                            Button(
                                                onClick = { onNavigateToChapter(firstChapterId) },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(36.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Review Again", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }

                                            Button(
                                                onClick = {
                                                    viewModel.resetTrackCompletion()
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(36.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64748B)),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Reset Track", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }

                            // Prev / Next Chapter Navigation
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if (uiState.previousChapterId != null) {
                                    Button(
                                        onClick = { onNavigateToChapter(uiState.previousChapterId!!) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(text = "← Previous", color = Color(0xFF1E293B), fontSize = 12.sp)
                                    }
                                } else {
                                    Spacer(modifier = Modifier.width(1.dp))
                                }

                                if (uiState.nextChapterId != null) {
                                    Button(
                                        onClick = { onNavigateToChapter(uiState.nextChapterId!!) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(text = "Next Chapter →", color = Color(0xFF1E293B), fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }

    // AI Smart Notes Dialog
    if (showSmartNotesDialog) {
        val smartNotes = remember(uiState.chapterTitle, uiState.subjectName) {
            LocalAIEngine.getInstance().generateSmartNotes(
                chapterTitle = uiState.chapterTitle.ifBlank { "Core Chapter" },
                subjectName = uiState.subjectName.ifBlank { "Subject" },
                lessonText = uiState.part1Content
            )
        }

        AlertDialog(
            onDismissRequest = { showSmartNotesDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "✨ ", fontSize = 18.sp)
                    Text(
                        text = "AI Smart Summary: ${smartNotes.title}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (isNotesSavedTemporarily || saveFeedbackMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFD1FAE5), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "✓ Notes saved to My Notes!",
                                color = Color(0xFF065F46),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Executive Summary Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "📘 Key Points & Core Concepts", color = BlueAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = smartNotes.keyDefinition, color = Color(0xFF1E293B), fontSize = 12.sp, lineHeight = 17.sp)
                        }
                    }

                    // Key Equations / Rules Box Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "⚡ Important Formulas / Key Dates / Definitions", color = Color(0xFF16A34A), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            smartNotes.coreFormulasOrLaws.forEach { formula ->
                                Text(text = "• $formula", color = Color(0xFF1E293B), fontSize = 12.sp, lineHeight = 17.sp)
                            }
                        }
                    }

                    // High-Yield Placement & Interview Revision Points Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "💡 High-Yield Placement & Interview Revision Points", color = PurpleAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            smartNotes.examTakeaways.forEachIndexed { idx, takeaway ->
                                Text(text = "${idx + 1}. $takeaway", color = Color(0xFF1E293B), fontSize = 12.sp, lineHeight = 17.sp)
                                if (idx < smartNotes.examTakeaways.size - 1) Spacer(modifier = Modifier.height(3.dp))
                            }
                        }
                    }

                    if (saveFeedbackMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFD1FAE5), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = saveFeedbackMessage!!,
                                color = Color(0xFF065F46),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Share with Friend Button
                    androidx.compose.material3.OutlinedButton(
                        onClick = {
                            com.siamts.synapse.util.ShareUtils.shareNote(
                                context = context,
                                title = "${smartNotes.title} - AI Smart Notes",
                                content = smartNotes.fullMarkdown,
                                subject = uiState.subjectName
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BlueAccent),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📤", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Share", color = BlueAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Save to My Notes Button
                    Button(
                        onClick = {
                            isSavingSmartNote = true
                            viewModel.saveSmartNotes(
                                title = "${smartNotes.title} - AI Smart Notes",
                                content = smartNotes.fullMarkdown
                            ) { success ->
                                isSavingSmartNote = false
                                isNotesSavedTemporarily = true
                                saveFeedbackMessage = "✓ Notes saved to My Notes!"
                                try {
                                    com.siamts.synapse.notification.SynapseNotificationHelper.showNoteSavedNotification(
                                        context = context,
                                        noteTitle = smartNotes.title
                                    )
                                } catch (_: Throwable) {}
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "✓ Notes saved to My Notes!",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                                coroutineScope.launch {
                                    delay(2000)
                                    isNotesSavedTemporarily = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isNotesSavedTemporarily) Color(0xFF059669) else BlueAccent
                        ),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !isSavingSmartNote
                    ) {
                        if (isSavingSmartNote) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                        } else if (isNotesSavedTemporarily) {
                            Text(text = "✓ Saved", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Text(text = "💾 Save to My Notes", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showSmartNotesDialog = false }) {
                    Text(text = "Close", color = Color(0xFF64748B))
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String, icon: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
    ) {
        Text(text = icon, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun QuickSelfCheckSection(chapterId: Int) {
    val quizData = remember(chapterId) {
        BaseCurriculumData.getChapterById(chapterId)?.mcqs?.take(2).orEmpty()
    }

    if (quizData.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfacePanelElevated),
            border = BorderStroke(1.dp, SurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Q: What is the core invariant of this topic?",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "A: State must be kept consistent, edge cases handled, and algorithmic complexity minimized.",
                    color = AmberGold,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            quizData.forEachIndexed { idx, q ->
                var revealed by remember(q.id) { mutableStateOf(false) }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfacePanelElevated),
                    border = BorderStroke(1.dp, SurfaceBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Check ${idx + 1}: ${q.question}",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (!revealed) {
                            TextButton(
                                onClick = { revealed = true },
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "💡 Tap to reveal answer",
                                    color = SunsetOrange,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SurfacePanel, RoundedCornerShape(8.dp))
                                    .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "✓ Correct Answer: Option ${q.correctOption}",
                                        color = AmberGold,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (!q.explanation.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = q.explanation.orEmpty(),
                                            color = TextSecondary,
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp
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
}

/**
 * High-Speed 5-Minute Quick Revision Cheat Sheet for Last-Minute Board Exam Prep.
 * Displays Must-Know Formulas, One-Line Definitions, Memory Tricks, and 2M/5M Expected Questions.
 */
@Composable
fun ChapterCheatSheetView(
    uiState: ChapterLessonUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfacePanel),
            border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "⚡", fontSize = 26.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "5-Minute Placement Cheat Sheet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = AmberGold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "High-speed last-minute revision · Key code patterns, syntax & interview questions",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Section 1: Must-Know Formulas / Equations / Code Patterns
        val formulas = uiState.coreFormulas.ifEmpty {
            listOf(
                "Core syntax pattern for ${uiState.chapterTitle}",
                "Time & space complexity invariants",
                "Edge-case validation and error bounds"
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfacePanel),
            border = BorderStroke(1.dp, SurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⚡", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Must-Know Syntax & Key Patterns",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }
                formulas.forEach { f ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfacePanelElevated, RoundedCornerShape(10.dp))
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = f,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // Section 2: One-Line Textbook Definitions
        val definitions = uiState.syllabus.ifEmpty {
            listOf(
                "${uiState.chapterTitle}: Fundamental concept in career tracks.",
                "Core Principle: Essential for technical coding rounds and data operations.",
                "Production Standard: Modern clean architecture and optimal complexity."
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfacePanel),
            border = BorderStroke(1.dp, SurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📖", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "One-Line Core Definitions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }
                definitions.forEach { d ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "•", color = SunsetOrange, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = d,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // Section 3: Memory Tricks & Mnemonics
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfacePanel),
            border = BorderStroke(1.dp, SurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💡", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Placement Tips & Trivia",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AmberGold
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfacePanelElevated, RoundedCornerShape(10.dp))
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Interview Pro-Tip: State the brute force approach first, then explain the optimal time/space tradeoff before writing code.",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Section 4: Top Expected Placement Interview Questions
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfacePanel),
            border = BorderStroke(1.dp, SurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎯", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Top Expected Placement Interview Questions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SunsetOrange
                    )
                }
                // High-Yield Question
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfacePanelElevated, RoundedCornerShape(10.dp))
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Core Technical Question:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunsetOrange
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Explain the fundamental mechanism of ${uiState.chapterTitle} and how it handles edge cases in production.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Highlight time & space complexity · Mention edge case validations for full placement score.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                // 5-Mark Question
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "5-Mark Question:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7C3AED)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Explain the complete theoretical derivation and applications of ${uiState.chapterTitle} with a neat labeled diagram.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Side headings · Principle · Step-by-step derivation · Practical applications & conclusion.",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
