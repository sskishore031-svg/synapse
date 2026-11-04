package com.siamts.synapse.ui.results

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siamts.synapse.data.local.BaseCurriculumData
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.PurpleAccent
import com.siamts.synapse.ui.theme.PurpleGlow
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

/**
 * Dedicated Quiz Result Screen displaying final scores, dynamic badge status,
 * animated accuracy ring, and navigation to review answers, retake, or return to roadmap.
 */
@Composable
fun QuizResultScreen(
    subjectId: String,
    chapterId: String,
    score: Int,
    total: Int,
    onReviewAnswers: () -> Unit,
    onRetakeQuiz: () -> Unit,
    onBackToRoadmap: () -> Unit,
    modifier: Modifier = Modifier,
    subjectTitle: String? = null,
    chapterTitle: String? = null,
) {
    val totalQuestions = total.coerceAtLeast(1)
    val scorePercentage = ((score.toFloat() / totalQuestions.toFloat()) * 100f).toInt().coerceIn(0, 100)

    val resolvedSubjectName = subjectTitle?.ifBlank { null }
        ?: BaseCurriculumData.getSubjectById(subjectId)?.name
        ?: "Mathematics"
    val resolvedChapterName = chapterTitle?.ifBlank { null }
        ?: BaseCurriculumData.getChapterById(chapterId.toIntOrNull() ?: 4)?.title
        ?: "Chapter Quiz"

    val (badgeText, badgeColor, badgeBg) = when {
        scorePercentage >= 80 -> Triple("Mastered 🌟", Color(0xFF059669), Color(0xFFD1FAE5))
        scorePercentage >= 50 -> Triple("Good Effort 👍", Color(0xFFD97706), Color(0xFFFEF3C7))
        else -> Triple("Needs Revision 📚", Color(0xFFE11D48), Color(0xFFFFE4E6))
    }

    var animationTarget by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(scorePercentage) {
        animationTarget = scorePercentage / 100f
    }
    val animatedProgress by animateFloatAsState(
        targetValue = animationTarget,
        animationSpec = tween(durationMillis = 1000),
        label = "scoreRingProgress"
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
            // 1. Top Header with Subject and Chapter Title
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
                    IconButton(onClick = onBackToRoadmap) {
                        Text(
                            text = "←",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${resolvedSubjectName.uppercase()} · CHAPTER $chapterId",
                            color = CyanGlow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = resolvedChapterName,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.size(48.dp))
                }
            }

            // 2. Light Sheet Container
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
                        // Animated Trophy / Result Banner
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 12.dp
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(Color(0xFFEFF6FF), CircleShape)
                                        .border(2.dp, Color(0xFFBFDBFE), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = if (scorePercentage >= 60) "🏆" else "🎯", fontSize = 30.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (scorePercentage >= 80) "Outstanding Performance!" else if (scorePercentage >= 50) "Well Done!" else "Keep Practicing!",
                                    color = Color(0xFF0F172A),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Quiz completed for $resolvedChapterName",
                                    color = Color(0xFF64748B),
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // Animated Score Circle & Badge Card
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 12.dp
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Animated Circular Progress Ring
                                    Box(
                                        modifier = Modifier.size(92.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            progress = { animatedProgress },
                                            modifier = Modifier.fillMaxSize(),
                                            color = BlueAccent,
                                            strokeWidth = 8.dp,
                                            trackColor = Color(0xFFE2E8F0),
                                        )
                                        Text(
                                            text = "$scorePercentage%",
                                            color = BlueAccent,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(18.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "You Scored: $score / $totalQuestions",
                                            color = Color(0xFF0F172A),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (scorePercentage >= 60) "Passing benchmark achieved" else "Passing threshold is 60%",
                                            color = Color(0xFF64748B),
                                            fontSize = 12.sp
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Badge Status Chip
                                        Box(
                                            modifier = Modifier
                                                .background(badgeBg, RoundedCornerShape(12.dp))
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = badgeText,
                                                color = badgeColor,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Score breakdown row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "✓ $score correct",
                                        color = Color(0xFF059669),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "× ${(totalQuestions - score).coerceAtLeast(0)} incorrect",
                                        color = Color(0xFFDC2626),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "📝 $totalQuestions questions",
                                        color = Color(0xFF64748B),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Summary Metrics Cards
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 4.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = "$scorePercentage%", color = Color(0xFF0F172A), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Accuracy", color = Color(0xFF64748B), fontSize = 11.sp)
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = "+${score * 20} XP", color = Color(0xFF7C3AED), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "XP Earned", color = Color(0xFF64748B), fontSize = 11.sp)
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = if (scorePercentage >= 60) "PASSED" else "RETRY",
                                            color = if (scorePercentage >= 60) Color(0xFF059669) else Color(0xFFDC2626),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(text = "Outcome", color = Color(0xFF64748B), fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        // Conceptual Feedback Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = if (scorePercentage >= 80) "🌟 Conceptual Mastery" else if (scorePercentage >= 50) "📖 Solid Foundation" else "⚡ Recommended Review",
                                    color = Color(0xFF0F172A),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (scorePercentage >= 80)
                                        "You have mastered this chapter's topics and are ready to advance to the next roadmap milestone."
                                    else if (scorePercentage >= 50)
                                        "Good effort! Review the detailed answer explanations to turn your missed questions into strengths."
                                    else
                                        "We recommend reviewing the chapter lesson concepts and worked examples before retaking the quiz.",
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action Buttons: Review Answers, Retake Quiz, Back to Roadmap
                    DashedBorderBox(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = 6.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 1. Review Answers Button
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .border(1.dp, BlueAccent, RoundedCornerShape(12.dp))
                                    .clickable { onReviewAnswers() },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "📋", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Review Answers & Explanations",
                                        color = BlueAccent,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // 2. Row: Retake Quiz + Back to Roadmap
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onRetakeQuiz,
                                    modifier = Modifier.weight(1f).height(46.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🔄", fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "Retake Quiz", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }

                                Button(
                                    onClick = onBackToRoadmap,
                                    modifier = Modifier.weight(1f).height(46.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(0.dp)
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
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = "🗺️", fontSize = 14.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = "Back to Roadmap", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
}

@Preview(showBackground = true)
@Composable
fun QuizResultScreenPreview() {
    SYNAPSETheme {
        QuizResultScreen(
            subjectId = "MATH",
            chapterId = "4",
            score = 4,
            total = 5,
            onReviewAnswers = {},
            onRetakeQuiz = {},
            onBackToRoadmap = {}
        )
    }
}
