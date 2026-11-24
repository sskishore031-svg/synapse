package com.siamts.synapse.ui.quiz

import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siamts.synapse.data.model.QuizResultDataDto
import com.siamts.synapse.ui.theme.AmberGold
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.PurpleAccent
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.SunsetOrange
import com.siamts.synapse.ui.theme.SunsetOrangeEnd
import com.siamts.synapse.ui.theme.SurfaceBorder
import com.siamts.synapse.ui.theme.SurfacePanel
import com.siamts.synapse.ui.theme.SurfacePanelElevated
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onQuizComplete: ((QuizResultDataDto?) -> Unit)? = null,
    onNavigateToResult: ((subjectId: String, chapterId: String, score: Int, total: Int, resultDto: QuizResultDataDto?) -> Unit)? = null,
    onNavigateToRoute: ((String) -> Unit)? = null,
    onRetryClick: (() -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.navEvent.collect { event ->
            when (event) {
                is QuizNavEvent.NavigateToResult -> {
                    onNavigateToRoute?.invoke("quiz_result_screen/${event.subjectId}/${event.chapterId}/${event.score}/${event.total}")
                    onNavigateToResult?.invoke(event.subjectId, event.chapterId, event.score, event.total, event.resultData)
                    onQuizComplete?.invoke(event.resultData)
                }
            }
        }
    }

    val isLastQuestion = uiState.currentQuestionIndex >= uiState.totalQuestions

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        QuizContent(
            uiState = uiState,
            onOptionSelect = { viewModel.onOptionSelected(it) },
            onNextClick = {
                if (isLastQuestion) {
                    viewModel.submitQuiz { result ->
                        onQuizComplete?.invoke(result)
                    }
                } else {
                    viewModel.nextQuestion()
                }
            },
            onBackClick = onBackClick,
            onRetryClick = {
                onRetryClick?.invoke() ?: viewModel.loadChapterQuiz(uiState.chapterId, viewModel.currentSubjectId)
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun QuizContent(
    uiState: QuizUiState,
    onOptionSelect: (String) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRetryClick: (() -> Unit)? = null,
) {
    val isDark = isSystemInDarkTheme()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isDark) listOf(
                        DarkBackgroundTop,
                        Color(0xFF0C0D12),
                        DarkBackgroundBottom,
                    ) else listOf(
                        Color(0xFFF5F6FA),
                        Color(0xFFF5F6FA),
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Dark Header with safe-area padding
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBackClick) {
                        Text(
                            text = "←",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.topicTag,
                            color = SunsetOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.title,
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(if (isDark) SurfacePanel else Color(0xFF161F42), CircleShape)
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "✕",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            // Sheet Container (Transparent in Dark Mode to reveal deep background)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = if (isDark) Color.Transparent else Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
            ) {
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = SunsetOrange,
                                modifier = Modifier.size(46.dp),
                                strokeWidth = 3.5.dp
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            Text(
                                text = "Loading Chapter Quiz...",
                                color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Retrieving chapter questions and verified options",
                                color = if (isDark) TextSecondary else Color(0xFF64748B),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else if (uiState.questions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) SurfacePanelElevated else Color.White
                            ),
                            border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = "📚", fontSize = 42.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Ready to Generate Quiz",
                                    color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = uiState.errorMessage ?: "Tap below to assemble on-device curriculum MCQs for this chapter.",
                                    color = if (isDark) TextSecondary else Color(0xFF64748B),
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = { onRetryClick?.invoke() },
                                    colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.height(44.dp)
                                ) {
                                    Text(
                                        text = "⚡ Retry / Generate Quiz",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                } else {
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
                            // 1. Question Progress & Timer Box
                            val totalQ = uiState.totalQuestions.coerceAtLeast(1)
                            val currentQ = uiState.currentQuestionIndex.coerceIn(1, totalQ)
                            DashedBorderBox(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = 8.dp
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = "Question $currentQ of $totalQ",
                                            color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = "⏱ ", fontSize = 12.sp)
                                            Text(
                                                text = uiState.timerText,
                                                color = AmberGold,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    LinearProgressIndicator(
                                        progress = { (currentQ.toFloat() / totalQ).coerceIn(0f, 1f) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp),
                                        color = SunsetOrange,
                                        trackColor = if (isDark) SurfaceBorder else Color(0xFFE2E8F0),
                                    )
                                }
                            }

                            // 2. Question Header Box
                            DashedBorderBox(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = 10.dp
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Difficulty Badge
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (isDark) SurfacePanel else Color(0xFFF3E8FF),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (isDark) SurfaceBorder else Color.Transparent,
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = uiState.difficulty,
                                            color = SunsetOrange,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = uiState.questionText,
                                        color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 26.sp,
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = uiState.instructionText,
                                        color = if (isDark) TextSecondary else Color(0xFF64748B),
                                        fontSize = 13.sp,
                                    )
                                }
                            }

                            // 3. Answer Options Box
                            DashedBorderBox(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = 8.dp
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    uiState.options.forEach { option ->
                                        val isSelected = option.id == uiState.selectedOptionId
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    color = if (isSelected) {
                                                        if (isDark) SunsetOrange.copy(alpha = 0.18f) else Color(0xFFEFF6FF)
                                                    } else {
                                                        if (isDark) SurfacePanelElevated else Color.White
                                                    },
                                                    shape = RoundedCornerShape(14.dp)
                                                )
                                                .border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) SunsetOrange else (if (isDark) SurfaceBorder else Color(0xFFE2E8F0)),
                                                    shape = RoundedCornerShape(14.dp)
                                                )
                                                .clickable { onOptionSelect(option.id) }
                                                .padding(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                // Option Label Badge A, B, C, D
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .background(
                                                            color = if (isSelected) SunsetOrange else (if (isDark) SurfacePanel else Color(0xFFF1F5F9)),
                                                            shape = CircleShape
                                                        ),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    Text(
                                                        text = option.optionLabel,
                                                        color = if (isSelected) Color.White else (if (isDark) TextSecondary else Color(0xFF64748B)),
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold,
                                                    )
                                                }

                                                Spacer(modifier = Modifier.width(12.dp))

                                                Text(
                                                    text = option.text,
                                                    color = if (isSelected) {
                                                        if (isDark) AmberGold else SunsetOrange
                                                    } else {
                                                        if (isDark) TextPrimary else Color(0xFF1E293B)
                                                    },
                                                    fontSize = 14.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    modifier = Modifier.weight(1f),
                                                )

                                                if (isSelected) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(22.dp)
                                                            .background(SunsetOrange.copy(alpha = 0.2f), CircleShape)
                                                            .border(1.5.dp, SunsetOrange, CircleShape),
                                                        contentAlignment = Alignment.Center,
                                                    ) {
                                                        Text(
                                                            text = "✓",
                                                            color = SunsetOrange,
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

                            // 4. AI Adaptive Quiz Note Box
                            DashedBorderBox(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = 6.dp
                            ) {
                                Text(
                                    text = uiState.aiNoteText,
                                    color = if (isDark) AmberGold else SunsetOrange,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 5. Action Button Box: Next Question Button
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 4.dp
                        ) {
                            Button(
                                onClick = onNextClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
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
                                                    SunsetOrange,
                                                    SunsetOrangeEnd,
                                                    AmberGold,
                                                )
                                            ),
                                            shape = RoundedCornerShape(16.dp),
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val isLast = uiState.currentQuestionIndex >= uiState.totalQuestions
                                        val btnText = when {
                                            uiState.isSubmitting -> "Submitting..."
                                            isLast -> "Submit Quiz ✓"
                                            else -> "Next question"
                                        }
                                        Text(
                                            text = if (isLast) "✓" else "→",
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = btnText,
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
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuizScreenPreview() {
    SYNAPSETheme {
        QuizContent(
            uiState = QuizUiState(),
            onOptionSelect = {},
            onNextClick = {},
            onBackClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
