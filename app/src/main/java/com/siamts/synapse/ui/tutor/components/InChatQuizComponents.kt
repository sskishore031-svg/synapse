package com.siamts.synapse.ui.tutor.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.BookmarkAdded
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.LocalThemeIsDark
import com.siamts.synapse.ui.theme.PurpleAccent
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.tutor.ChatMessage
import com.siamts.synapse.ui.welcome.components.BrainGraphic

/**
 * Interactive In-Chat Quiz Question Card.
 * Renders the question number, subject badge, question text, and 4 selectable option chips (A, B, C, D).
 */
@Composable
fun InChatQuizQuestionCard(
    message: ChatMessage,
    onSelectOption: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalThemeIsDark.current
    val cardBg = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardBorder = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
    val textPrimary = if (isDark) TextPrimary else Color(0xFF0F172A)
    val textSecondary = Color(0xFF64748B)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Header Row: Brain/Quiz Icon + Question Counter + Subject Pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(
                            brush = Brush.linearGradient(listOf(PurpleAccent, CyanGlow)),
                            shape = CircleShape
                        )
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BrainGraphic(modifier = Modifier.fillMaxSize())
                }

                Text(
                    text = "QUESTION ${message.quizQuestionIndex + 1} OF ${message.quizTotalQuestions}",
                    color = if (isDark) CyanGlow else BlueAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            if (!message.matchedSubject.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .background(
                            color = if (isDark) Color(0xFF1E293B) else Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = message.matchedSubject,
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Question Body
        Text(
            text = message.text.removePrefix("**Question ${message.quizQuestionIndex + 1} of ${message.quizTotalQuestions}**").trim(),
            color = textPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        // 4 Selectable Option Buttons
        val options = message.quizOptions ?: emptyMap()
        val optionKeys = listOf("A", "B", "C", "D")

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            optionKeys.forEach { key ->
                val optionText = options[key] ?: return@forEach
                val optionBg = if (isDark) Color(0xFF162032) else Color(0xFFFFFFFF)
                val optionBorder = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(optionBg)
                        .border(1.dp, optionBorder, RoundedCornerShape(12.dp))
                        .clickable { onSelectOption(key) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Option letter badge (e.g. A, B, C, D)
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                color = if (isDark) Color(0xFF1E293B) else Color(0xFFEFF6FF),
                                shape = CircleShape
                            )
                            .border(1.dp, if (isDark) CyanGlow.copy(alpha = 0.4f) else BlueAccent.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = key,
                            color = if (isDark) CyanGlow else BlueAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = optionText,
                        color = textPrimary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Helpful hint row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "💡 Tap an option or speak A, B, C, D",
                color = textSecondary,
                fontSize = 11.sp
            )
            Text(
                text = message.timeAgo,
                color = textSecondary,
                fontSize = 11.sp
            )
        }
    }
}

/**
 * Interactive In-Chat Quiz Scorecard Card.
 * Displays final score, percentage, mastery appraisal, weak concepts breakdown,
 * and a prominent button to save the score and mistakes to the Notes Organizer.
 */
@Composable
fun InChatQuizScorecardCard(
    message: ChatMessage,
    onSaveScoreAndMistakes: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalThemeIsDark.current
    val cardBg = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardBorder = if (isDark) PurpleAccent.copy(alpha = 0.5f) else BlueAccent.copy(alpha = 0.4f)
    val textPrimary = if (isDark) TextPrimary else Color(0xFF0F172A)
    val textSecondary = Color(0xFF64748B)

    val score = message.quizScore
    val total = if (message.quizTotal > 0) message.quizTotal else 10
    val pct = (score * 100) / total

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(1.5.dp, cardBorder, RoundedCornerShape(18.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Trophy Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            brush = Brush.linearGradient(listOf(PurpleAccent, CyanGlow)),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "Quiz Complete!",
                        color = textPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = message.quizSubject ?: "Placement Assessment",
                        color = textSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            // Score Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            if (pct >= 70) listOf(Color(0xFF059669), Color(0xFF10B981))
                            else if (pct >= 50) listOf(Color(0xFFD97706), Color(0xFFF59E0B))
                            else listOf(Color(0xFFDC2626), Color(0xFFEF4444))
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$score / $total ($pct%)",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Performance Assessment Text
        val gradeText = when {
            pct >= 90 -> "🌟 Outstanding Mastery! Placement Assessment Ready."
            pct >= 70 -> "👍 Great Performance! Solid Conceptual Foundation."
            pct >= 50 -> "📈 Good Effort — Review weak areas to master the topic."
            else -> "📚 Needs Revision — Re-read the chapter guides and retry."
        }
        Text(
            text = gradeText,
            color = if (isDark) CyanGlow else BlueAccent,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        // Summary Text / Weak Topics
        Text(
            text = message.text,
            color = textPrimary,
            fontSize = 13.sp,
            lineHeight = 19.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Save Score & Mistakes Button
        Button(
            onClick = onSaveScoreAndMistakes,
            enabled = !message.isQuizScoreSaved,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (message.isQuizScoreSaved) Color(0xFF059669) else BlueAccent,
                disabledContainerColor = Color(0xFF059669)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (message.isQuizScoreSaved) Icons.Rounded.BookmarkAdded else Icons.Rounded.BookmarkAdd,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (message.isQuizScoreSaved) "✓ Saved to Notes Organizer" else "📝 Save Score & Mistakes to Notes",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
