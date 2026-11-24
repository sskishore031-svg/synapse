package com.siamts.synapse.ui.results

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siamts.synapse.ui.theme.AmberGold
import com.siamts.synapse.ui.theme.AmberGoldEnd
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.LocalThemeIsDark
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

data class ReviewQuestionItem(
    val id: String,
    val questionNumber: Int,
    val questionText: String,
    val userAnswer: String,
    val correctAnswer: String? = null,
    val isCorrect: Boolean,
    val explanation: String,
)

@Composable
fun QuizReviewAnswersPreviewScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    reviewQuestions: List<ReviewQuestionItem>? = null,
    scoreText: String? = null,
    summarySubtext: String? = null,
    accuracyPercent: Float = 0.8f,
    xpEarned: String = "+120 XP",
    onRetryQuizClick: (() -> Unit)? = null,
    onBackToChaptersClick: (() -> Unit)? = null,
    onDashboardClick: (() -> Unit)? = null,
) {
    val defaultQuestions = listOf(
        ReviewQuestionItem(
            id = "1",
            questionNumber = 1,
            questionText = "What is the main goal of gradient descent in machine learning?",
            userAnswer = "B. Reduce prediction error / loss iteratively",
            correctAnswer = "B. Reduce prediction error / loss iteratively",
            isCorrect = true,
            explanation = "Gradient descent iteratively adjusts parameters in the direction of steepest descent to minimize the loss function."
        ),
        ReviewQuestionItem(
            id = "2",
            questionNumber = 2,
            questionText = "What happens if the model learning rate is configured too large?",
            userAnswer = "A. The model learns faster and converges immediately",
            correctAnswer = "B. It overshoots the minimum and diverges",
            isCorrect = false,
            explanation = "A large learning rate causes step sizes that overshoot the local minimum, causing instability or divergence."
        ),
        ReviewQuestionItem(
            id = "3",
            questionNumber = 3,
            questionText = "What are the roots of the quadratic equation x² - 5x + 6 = 0?",
            userAnswer = "B. x = 2 and x = 3",
            correctAnswer = "B. x = 2 and x = 3",
            isCorrect = true,
            explanation = "Factoring gives (x - 2)(x - 3) = 0, which yields roots x = 2 and x = 3."
        )
    )

    val isDark = LocalThemeIsDark.current
    val questionsToDisplay = if (!reviewQuestions.isNullOrEmpty()) reviewQuestions else defaultQuestions
    val correctCount = questionsToDisplay.count { it.isCorrect }
    val totalCount = questionsToDisplay.size.coerceAtLeast(1)
    val calculatedScoreText = scoreText ?: "Your Score: $correctCount / $totalCount (${(correctCount * 100) / totalCount}%)"
    val calculatedSubtext = summarySubtext ?: "$correctCount correct answers, ${totalCount - correctCount} incorrect."
    val calculatedAccuracy = if (scoreText != null) accuracyPercent else (correctCount.toFloat() / totalCount.toFloat())

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isDark) listOf(
                        DarkBackgroundTop,
                        Color(0xFF090D22),
                        DarkBackgroundBottom,
                    ) else listOf(
                        Color(0xFFF5F6FA),
                        Color(0xFFF5F6FA)
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
                        Text(
                            text = "QUIZ SOLUTIONS & EXPLANATIONS",
                            color = AmberGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Review Answers", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF161F42), CircleShape)
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "•••", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            }

            // Dark Content Body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(color = if (isDark) Color.Transparent else Color(0xFFF8FAFC), shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    // Scrollable Review Content
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. Score Summary Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                                border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Column {
                                            Text(
                                                text = calculatedScoreText,
                                                color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = calculatedSubtext,
                                                color = if (isDark) TextSecondary else Color(0xFF64748B),
                                                fontSize = 11.sp
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .background(if (isDark) SunsetOrange.copy(alpha = 0.2f) else Color(0xFFF3E8FF), RoundedCornerShape(12.dp))
                                                .border(1.dp, if (isDark) SunsetOrange.copy(alpha = 0.4f) else Color.Transparent, RoundedCornerShape(12.dp))
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(text = xpEarned, color = if (isDark) SunsetOrange else PurpleAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    LinearProgressIndicator(
                                        progress = { calculatedAccuracy.coerceIn(0f, 1f) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp),
                                        color = SunsetOrange,
                                        trackColor = if (isDark) SurfaceBorder else Color(0xFFE2E8F0),
                                    )
                                }
                            }
                        }

                        // 2. Question Explanations List Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "Detailed Solutions & Explanations",
                                    color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                questionsToDisplay.forEach { item ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                                        border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0)),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Text(
                                                    text = "Question ${item.questionNumber}",
                                                    color = SunsetOrange,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )

                                                Box(
                                                    modifier = Modifier
                                                        .background(
                                                            if (item.isCorrect) (if (isDark) Color(0xFF059669).copy(alpha = 0.25f) else Color(0xFFD1FAE5)) else (if (isDark) Color(0xFFDC2626).copy(alpha = 0.25f) else Color(0xFFFEE2E2)),
                                                            RoundedCornerShape(8.dp)
                                                        )
                                                        .border(
                                                            1.dp,
                                                            if (item.isCorrect) (if (isDark) Color(0xFF059669).copy(alpha = 0.5f) else Color.Transparent) else (if (isDark) Color(0xFFDC2626).copy(alpha = 0.5f) else Color.Transparent),
                                                            RoundedCornerShape(8.dp)
                                                        )
                                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                                ) {
                                                    Text(
                                                        text = if (item.isCorrect) "✓ Correct" else "✕ Incorrect",
                                                        color = if (item.isCorrect) (if (isDark) Color(0xFF34D399) else Color(0xFF059669)) else (if (isDark) Color(0xFFF87171) else Color(0xFFDC2626)),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text(
                                                text = item.questionText,
                                                color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                lineHeight = 20.sp
                                            )

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // User Answer
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(
                                                        if (item.isCorrect) (if (isDark) Color(0xFF059669).copy(alpha = 0.15f) else Color(0xFFEFF6FF)) else (if (isDark) Color(0xFFDC2626).copy(alpha = 0.15f) else Color(0xFFFEF2F2)),
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                    .border(
                                                        1.dp,
                                                        if (item.isCorrect) (if (isDark) Color(0xFF059669).copy(alpha = 0.4f) else Color(0xFFBFDBFE)) else (if (isDark) Color(0xFFDC2626).copy(alpha = 0.4f) else Color(0xFFFECACA)),
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                    .padding(10.dp)
                                            ) {
                                                Text(
                                                    text = "Your Answer: ${item.userAnswer}",
                                                    color = if (item.isCorrect) (if (isDark) Color(0xFF34D399) else BlueAccent) else (if (isDark) Color(0xFFF87171) else Color(0xFFDC2626)),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                )
                                            }

                                            // Correct Answer (Highlighted in soft green)
                                            if (!item.isCorrect && item.correctAnswer != null) {
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(if (isDark) Color(0xFF059669).copy(alpha = 0.2f) else Color(0xFFECFDF5), RoundedCornerShape(8.dp))
                                                        .border(1.dp, if (isDark) Color(0xFF059669).copy(alpha = 0.5f) else Color(0xFFA7F3D0), RoundedCornerShape(8.dp))
                                                        .padding(10.dp)
                                                ) {
                                                    Text(
                                                        text = "Correct Answer: ${item.correctAnswer}",
                                                        color = if (isDark) Color(0xFF34D399) else Color(0xFF059669),
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // Explanation
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(if (isDark) SurfacePanel else Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                                    .border(1.dp, if (isDark) SurfaceBorder else Color.Transparent, RoundedCornerShape(8.dp))
                                                    .padding(10.dp)
                                            ) {
                                                Text(
                                                    text = "💡 Explanation: ${item.explanation}",
                                                    color = if (isDark) TextSecondary else Color(0xFF334155),
                                                    fontSize = 12.sp,
                                                    lineHeight = 17.sp,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 3. AI Learning Tip Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 6.dp) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color(0xFFEFF6FF)),
                                border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color.Transparent)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "✨ ", fontSize = 12.sp)
                                        Text(
                                            text = "SYNAPSE AI INSIGHT",
                                            color = AmberGold,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Review incorrect items carefully before attempting chapter quizzes to reinforce long-term memory.",
                                        color = if (isDark) TextSecondary else Color(0xFF1E293B),
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4. Action Buttons Box: Retake Quiz / Back to Chapters
                    DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 4.dp) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .background(if (isDark) SurfacePanelElevated else Color.White, RoundedCornerShape(12.dp))
                                    .border(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .clickable { onRetryQuizClick?.invoke() ?: onBackClick() },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "Retake Quiz 🔄",
                                    color = if (isDark) SunsetOrange else BlueAccent,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {
                                    onBackToChaptersClick?.invoke()
                                        ?: onDashboardClick?.invoke()
                                        ?: onBackClick()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                contentPadding = PaddingValues(0.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.horizontalGradient(listOf(SunsetOrange, SunsetOrangeEnd, AmberGold)),
                                            RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "Back to Chapters →",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuizReviewAnswersPreviewScreenPreview() {
    SYNAPSETheme {
        QuizReviewAnswersPreviewScreen(onBackClick = {}, modifier = Modifier.fillMaxSize())
    }
}
