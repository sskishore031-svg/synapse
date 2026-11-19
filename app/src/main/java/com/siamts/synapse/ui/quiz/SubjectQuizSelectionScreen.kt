package com.siamts.synapse.ui.quiz

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siamts.synapse.ui.theme.AmberGold
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.SunsetOrange
import com.siamts.synapse.ui.theme.SurfaceBorder
import com.siamts.synapse.ui.theme.SurfacePanel
import com.siamts.synapse.ui.theme.SurfacePanelElevated
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@Composable
fun SubjectQuizSelectionScreen(
    subjectId: Int,
    subjectName: String,
    viewModel: SubjectQuizViewModel,
    onBackClick: () -> Unit,
    onStartQuiz: (chapterId: Int, subjectId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(subjectId, subjectName) {
        if (uiState.quizzes.isEmpty() || uiState.subjectId != subjectId) {
            viewModel.loadSubjectQuizzes(subjectId, subjectName)
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
                            text = "${subjectName.uppercase()} QUIZZES",
                            color = AmberGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Select Chapter Quiz",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(modifier = Modifier.size(40.dp))
                }
            }

            // Dark Content Body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Transparent)
            ) {
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SunsetOrange)
                    }
                } else if (uiState.quizzes.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No chapter quizzes found for this subject.",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Full Subject Revision Test (50 Questions)
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
                                    .clickable { onStartQuiz(0, subjectId.toString()) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfacePanel),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(AmberGold.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "FULL TRACK REVISION",
                                                color = AmberGold,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Text(
                                            text = "50 MCQs",
                                            color = TextSecondary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "$subjectName · Comprehensive Revision Test",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = TextPrimary
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "Comprehensive 50-Question Placement Level Practice across all chapters.",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = TextSecondary
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = { onStartQuiz(0, subjectId.toString()) },
                                        modifier = Modifier.fillMaxWidth().height(42.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SunsetOrange
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = "Take 50-Question Revision Test",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }

                        items(uiState.quizzes) { quiz ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
                                    .clickable { onStartQuiz(quiz.chapterId, subjectId.toString()) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfacePanelElevated),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(SurfacePanel, RoundedCornerShape(8.dp))
                                                .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "CHAPTER ${quiz.chapterOrder}",
                                                color = SunsetOrange,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        if (quiz.isAttempted && quiz.bestScore != null) {
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFF22C55E).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = "Best: ${quiz.bestScore}%",
                                                    color = Color(0xFF4ADE80),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = quiz.chapterTitle.ifBlank { quiz.quizTitle },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = TextPrimary
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "${quiz.questionCount.coerceAtLeast(5)} Questions • Multiple Choice",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextSecondary
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = { onStartQuiz(quiz.chapterId, subjectId.toString()) },
                                        modifier = Modifier.fillMaxWidth().height(42.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (quiz.isAttempted) SurfacePanel else SunsetOrange
                                        ),
                                        border = if (quiz.isAttempted) androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder) else null,
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = if (quiz.isAttempted) "Retake Quiz" else "Start Quiz",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
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

@Composable
fun SubjectQuizSelectionScreen(
    subjectId: Int,
    subjectName: String,
    viewModel: SubjectQuizViewModel,
    onBackClick: () -> Unit,
    onStartQuiz: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    SubjectQuizSelectionScreen(
        subjectId = subjectId,
        subjectName = subjectName,
        viewModel = viewModel,
        onBackClick = onBackClick,
        onStartQuiz = { chapterId, _ -> onStartQuiz(chapterId) },
        modifier = modifier
    )
}

@Composable
fun SubjectQuizSelectionScreen(
    subjectId: String,
    subjectName: String,
    viewModel: SubjectQuizViewModel,
    onBackClick: () -> Unit,
    onStartQuiz: (chapterId: Int, subjectId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val numericId = subjectId.toIntOrNull() ?: 0
    SubjectQuizSelectionScreen(
        subjectId = numericId,
        subjectName = subjectName,
        viewModel = viewModel,
        onBackClick = onBackClick,
        onStartQuiz = onStartQuiz,
        modifier = modifier
    )
}

