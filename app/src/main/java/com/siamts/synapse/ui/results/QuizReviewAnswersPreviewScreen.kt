package com.siamts.synapse.ui.results

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onRetryQuizClick: (() -> Unit)? = null,
    onDashboardClick: (() -> Unit)? = null,
) {
    val reviewQuestions = listOf(
        ReviewQuestionItem(
            id = "1",
            questionNumber = 1,
            questionText = "What is the main goal of gradient descent?",
            userAnswer = "B. Reduce prediction error / loss",
            isCorrect = true,
            explanation = "Gradient descent iteratively adjusts parameters in the direction of steepest descent to minimize the loss function."
        ),
        ReviewQuestionItem(
            id = "2",
            questionNumber = 2,
            questionText = "What happens if the learning rate is too large?",
            userAnswer = "A. The model learns faster",
            correctAnswer = "B. It overshoots the minimum and diverges",
            isCorrect = false,
            explanation = "A large learning rate causes step sizes that overshoot the local minimum, causing instability or divergence."
        ),
        ReviewQuestionItem(
            id = "3",
            questionNumber = 3,
            questionText = "What are the roots of x² - 5x + 6 = 0?",
            userAnswer = "B. x = 2, 3",
            isCorrect = true,
            explanation = "Factoring gives (x - 2)(x - 3) = 0, which yields roots x = 2 and x = 3."
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
                        Text(text = "QUIZ SOLUTIONS & EXPLANATIONS", color = CyanGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
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
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Column {
                                            Text(text = "Your Score: 8 / 10 (80%)", color = Color(0xFF0F172A), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(text = "Great job! 8 correct answers, 2 incorrect.", color = Color(0xFF64748B), fontSize = 11.sp)
                                        }

                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFFF3E8FF), RoundedCornerShape(12.dp))
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(text = "+120 XP", color = PurpleAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    LinearProgressIndicator(
                                        progress = { 0.8f },
                                        modifier = Modifier.fillMaxWidth().height(6.dp),
                                        color = BlueAccent,
                                        trackColor = Color(0xFFE2E8F0),
                                    )
                                }
                            }
                        }

                        // 2. Question Explanations List Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(text = "Detailed Explanations", color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)

                                reviewQuestions.forEach { item ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Text(text = "Q${item.questionNumber}", color = BlueAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                                Box(
                                                    modifier = Modifier
                                                        .background(if (item.isCorrect) Color(0xFFD1FAE5) else Color(0xFFFEE2E2), RoundedCornerShape(8.dp))
                                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = if (item.isCorrect) "✓ Correct" else "✕ Incorrect",
                                                        color = if (item.isCorrect) Color(0xFF059669) else Color(0xFFDC2626),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))

                                            Text(text = item.questionText, color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)

                                            Spacer(modifier = Modifier.height(8.dp))

                                            // User Answer
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(if (item.isCorrect) Color(0xFFEFF6FF) else Color(0xFFFEF2F2), RoundedCornerShape(8.dp))
                                                    .padding(8.dp)
                                            ) {
                                                Text(
                                                    text = "Your Answer: ${item.userAnswer}",
                                                    color = if (item.isCorrect) BlueAccent else Color(0xFFDC2626),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium,
                                                )
                                            }

                                            if (!item.isCorrect && item.correctAnswer != null) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(Color(0xFFECFDF5), RoundedCornerShape(8.dp))
                                                        .padding(8.dp)
                                                ) {
                                                    Text(
                                                        text = "Correct Answer: ${item.correctAnswer}",
                                                        color = Color(0xFF059669),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text(
                                                text = "Explanation: ${item.explanation}",
                                                color = Color(0xFF475569),
                                                fontSize = 11.sp,
                                                lineHeight = 15.sp,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 3. AI Learning Tip Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 6.dp) {
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "✨ ", fontSize = 12.sp)
                                        Text(text = "AI LEARNING TIP", color = BlueAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Review learning rate properties before attempting the next chapter quiz.",
                                        color = Color(0xFF1E293B),
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4. Action Buttons Box
                    DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 4.dp) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .clickable { onRetryQuizClick?.invoke() ?: onBackClick() },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(text = "Retry Quiz", color = BlueAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onDashboardClick?.invoke() ?: onBackClick() },
                                modifier = Modifier.weight(1f).height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                contentPadding = PaddingValues(0.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Brush.horizontalGradient(listOf(PurpleAccent, BlueAccent, CyanAccent)), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(text = "Back to Dashboard →", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
