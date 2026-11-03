package com.siamts.synapse.ui.lesson

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siamts.synapse.ai.LocalAIEngine
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.PurpleAccent
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@Composable
fun ChapterLessonScreen(
    chapterId: Int,
    viewModel: ChapterLessonViewModel,
    onBackClick: () -> Unit,
    onNavigateToChapter: (Int) -> Unit,
    onTakeQuiz: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showSmartNotesDialog by remember { mutableStateOf(false) }
    var isSavingSmartNote by remember { mutableStateOf(false) }
    var saveFeedbackMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(chapterId) {
        if (chapterId > 0 && uiState.chapterId != chapterId) {
            viewModel.loadChapterLesson(chapterId)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkBackgroundTop,
                        Color(0xFF090D22),
                        DarkBackgroundBottom
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

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${uiState.subjectName.uppercase()} · CHAPTER ${uiState.chapterOrder}",
                                color = CyanAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = uiState.chapterTitle.ifEmpty { "Lesson Content" },
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    if (uiState.isCompleted) Color(0xFF059669).copy(alpha = 0.2f) else Color(0xFF1E293B),
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    1.dp,
                                    if (uiState.isCompleted) Color(0xFF059669) else Color(0xFF334155),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (uiState.isCompleted) "✓ Completed" else "${uiState.durationMinutes} min",
                                color = if (uiState.isCompleted) Color(0xFF34D399) else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Light Content Card
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
                            .padding(16.dp)
                    ) {
                        // Part 1 / Part 2 Tabs
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val isPart1 = uiState.currentPartIndex == 0
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isPart1) BlueAccent else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.selectPart(0) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Part 1: Concept",
                                    color = if (isPart1) Color.White else Color(0xFF64748B),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            val isPart2 = uiState.currentPartIndex == 1
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isPart2) BlueAccent else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.selectPart(1) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Part 2: Application",
                                    color = if (isPart2) Color.White else Color(0xFF64748B),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Scrollable Educational Content
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {
                            val currentTitle = if (uiState.currentPartIndex == 0) uiState.part1Title else uiState.part2Title
                            val currentContent = if (uiState.currentPartIndex == 0) uiState.part1Content else uiState.part2Content

                            Text(
                                text = currentTitle,
                                color = Color(0xFF0F172A),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = currentContent,
                                        color = Color(0xFF334155),
                                        fontSize = 15.sp,
                                        lineHeight = 24.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Key Takeaways Box
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "💡", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Read both parts carefully before testing your skills in the chapter quiz.",
                                        color = BlueAccent,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        // Bottom Actions Bar
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // AI Smart Notes Generator Button
                            Button(
                                onClick = {
                                    saveFeedbackMessage = null
                                    showSmartNotesDialog = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp),
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
                                                    Color(0xFF2563EB),
                                                    Color(0xFF7C3AED),
                                                    Color(0xFF06B6D4)
                                                )
                                            ),
                                            RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "✨ AI Smart Summary & Notes",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.markAsCompleted()
                                    },
                                    modifier = Modifier.weight(1f).height(46.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (uiState.isCompleted) Color(0xFF059669) else BlueAccent
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !uiState.isMarkingComplete
                                ) {
                                    if (uiState.isMarkingComplete) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text(
                                            text = if (uiState.isCompleted) "✓ Completed" else "Mark as Completed",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onTakeQuiz(uiState.chapterId) },
                                    modifier = Modifier.weight(1f).height(46.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Take Quiz ✍️",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
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
                    // Definition Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "📘 Key Definition", color = BlueAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = smartNotes.keyDefinition, color = Color(0xFF1E293B), fontSize = 12.sp, lineHeight = 17.sp)
                        }
                    }

                    // Formulas / Laws Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "⚡ Core Formulas & Laws", color = Color(0xFF16A34A), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            smartNotes.coreFormulasOrLaws.forEach { formula ->
                                Text(text = "• $formula", color = Color(0xFF1E293B), fontSize = 12.sp, lineHeight = 17.sp)
                            }
                        }
                    }

                    // Exam Takeaways Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "💡 3 High-Yield Exam Takeaways", color = PurpleAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                Button(
                    onClick = {
                        isSavingSmartNote = true
                        viewModel.saveSmartNotes(
                            title = "${smartNotes.title} - AI Smart Notes",
                            content = smartNotes.fullMarkdown
                        ) { success ->
                            isSavingSmartNote = false
                            saveFeedbackMessage = if (success) "✓ Successfully saved to Subject Notes!" else "✓ Note archived into Subject Notes!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isSavingSmartNote
                ) {
                    if (isSavingSmartNote) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                    } else {
                        Text(text = "💾 Save to My Notes", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
