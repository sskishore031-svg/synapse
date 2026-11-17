package com.siamts.synapse.ui.tutor.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.BookmarkAdded
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
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
import com.siamts.synapse.ui.tutor.NeuralWaveTypingIndicator
import com.siamts.synapse.ui.welcome.components.BrainGraphic

/**
 * Minimalist ChatGPT-Grade AI Response Bubble (Direct Dark Canvas - Zero Cramped Box Borders).
 * Renders AI replies directly on the obsidian canvas with generous line height (22.sp),
 * formatted Markdown, and an icon-only action row (Listen, Copy, Save Note, Elaborate).
 */
@Composable
fun AiMessageCanvas(
    message: ChatMessage,
    isGenerating: Boolean,
    isSpeaking: Boolean,
    onCopyClick: () -> Unit,
    onSpeakClick: () -> Unit,
    onSaveNote: () -> Unit,
    onElaborate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = LocalThemeIsDark.current
    val textColor = if (isDark) TextPrimary else Color(0xFF0F172A)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp)
    ) {
        // AI Header row: Glowing Avatar + Title + Subject Badge + Typing Wave
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "SYNAPSE AI",
                    color = if (isDark) CyanGlow else BlueAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                if (!message.matchedSubject.isNullOrBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isDark) Color(0xFF1E293B) else Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${message.matchedSubject}${if (message.matchedTopic != null) " · " + message.matchedTopic else ""}",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (isGenerating) {
                NeuralWaveTypingIndicator()
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Direct-on-Canvas Text Content with 22.sp line height
        if (message.text.isEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = "SYNAPSE AI is thinking",
                    color = Color(0xFF64748B),
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic
                )
                NeuralWaveTypingIndicator()
            }
        } else {
            AiMarkdownText(text = message.text, textColor = textColor)

            if (!message.paragraph2.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                AiMarkdownText(text = message.paragraph2, textColor = textColor)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Minimalist Icon-Only Action Row (20.dp icons, zero text wrapping)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message.timeAgo,
                color = Color(0xFF64748B),
                fontSize = 11.sp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Male Voice TTS Listen
                IconButton(
                    onClick = onSpeakClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.AutoMirrored.Rounded.VolumeOff else Icons.AutoMirrored.Rounded.VolumeUp,
                        contentDescription = if (isSpeaking) "Stop Speaking" else "Listen Aloud",
                        tint = if (isSpeaking) Color(0xFF10B981) else Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // 2. Copy Text
                IconButton(
                    onClick = onCopyClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ContentCopy,
                        contentDescription = "Copy Response",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // 3. Save Note (Turns emerald BookmarkAdded once saved!)
                IconButton(
                    onClick = onSaveNote,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (message.isSaved) Icons.Rounded.BookmarkAdded else Icons.Rounded.BookmarkAdd,
                        contentDescription = "Save Note",
                        tint = if (message.isSaved) Color(0xFF10B981) else Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // 4. Elaborate (if concept answer)
                if (message.isConceptAnswer) {
                    IconButton(
                        onClick = onElaborate,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = "Elaborate in detail",
                            tint = if (isDark) CyanGlow else BlueAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Formats Markdown bold headers, numbered points, and bullet items with comfortable 22.sp line height.
 */
@Composable
fun AiMarkdownText(text: String, textColor: Color, modifier: Modifier = Modifier) {
    val lines = text.split("\n")
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        lines.forEach { line ->
            val trimmed = line.trim()
            when {
                trimmed.startsWith("**") && trimmed.endsWith("**") -> {
                    val cleanHeader = trimmed.removeSurrounding("**")
                    Text(
                        text = cleanHeader,
                        color = textColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )
                }
                trimmed.startsWith("• ") || trimmed.startsWith("- ") -> {
                    val bulletText = trimmed.removePrefix("• ").removePrefix("- ")
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "•", color = CyanAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = bulletText,
                            color = textColor,
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
                trimmed.startsWith("1. ") || trimmed.startsWith("2. ") || trimmed.startsWith("3. ") ||
                trimmed.startsWith("4. ") || trimmed.startsWith("5. ") || trimmed.startsWith("6. ") ||
                trimmed.startsWith("7. ") || trimmed.startsWith("8. ") || trimmed.startsWith("9. ") -> {
                    val prefix = trimmed.take(3)
                    val numText = trimmed.drop(3)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = prefix, color = CyanAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = numText,
                            color = textColor,
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
                else -> {
                    if (trimmed.isNotBlank()) {
                        Text(
                            text = line,
                            color = textColor,
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}
