package com.siamts.synapse.ui.tutor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.LocalThemeIsDark
import com.siamts.synapse.ui.theme.PurpleAccent
import com.siamts.synapse.ui.theme.SunsetOrange
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
    onFormatTwoMark: () -> Unit = {},
    onFormatFiveMark: () -> Unit = {}
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
                // Board Exam 2-Mark Format Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isDark) Color(0xFF1E293B) else Color(0xFFEFF6FF))
                        .clickable { onFormatTwoMark() }
                        .padding(horizontal = 7.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "2M",
                        color = if (isDark) CyanGlow else BlueAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Board Exam 5-Mark Format Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isDark) Color(0xFF1E293B) else Color(0xFFEFF6FF))
                        .clickable { onFormatFiveMark() }
                        .padding(horizontal = 7.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "5M",
                        color = if (isDark) CyanGlow else BlueAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(2.dp))

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
 * Converts text containing Markdown **bold** or `inline code` spans into an [AnnotatedString]
 * with [FontWeight.Bold] or Monospace styling, stripping raw syntax markers.
 */
fun parseMarkdownAnnotatedString(text: String, baseColor: Color): AnnotatedString {
    return buildAnnotatedString {
        // Match both **bold** and `code` spans
        val tokenRegex = Regex("""(\*\*(.*?)\*\*|`([^`]+)`)""")
        var cursor = 0
        val matches = tokenRegex.findAll(text)

        for (match in matches) {
            if (match.range.first > cursor) {
                withStyle(SpanStyle(color = baseColor)) {
                    append(text.substring(cursor, match.range.first))
                }
            }

            val boldContent = match.groups[2]?.value
            val codeContent = match.groups[3]?.value

            if (boldContent != null) {
                withStyle(SpanStyle(color = baseColor, fontWeight = FontWeight.Bold)) {
                    append(boldContent)
                }
            } else if (codeContent != null) {
                withStyle(
                    SpanStyle(
                        color = Color(0xFFFF9500),
                        fontFamily = FontFamily.Monospace,
                        background = Color(0x22FFFFFF)
                    )
                ) {
                    append(" $codeContent ")
                }
            }

            cursor = match.range.last + 1
        }

        if (cursor < text.length) {
            withStyle(SpanStyle(color = baseColor)) {
                append(text.substring(cursor))
            }
        }
    }
}

/**
 * Formats Markdown bold headers, numbered points, and bullet items with comfortable 22.sp line height.
 * Accurately parses bold text, code snippets, and replaces raw asterisks with clean bullet dots.
 */
@Composable
fun AiMarkdownText(text: String, textColor: Color, modifier: Modifier = Modifier) {
    val lines = text.split("\n")
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        var inCodeBlock = false
        val codeBuffer = mutableListOf<String>()

        lines.forEach { line ->
            val trimmed = line.trim()

            if (trimmed.startsWith("```")) {
                if (inCodeBlock) {
                    // Close code block and render
                    inCodeBlock = false
                    val codeContent = codeBuffer.joinToString("\n")
                    codeBuffer.clear()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F1015))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = codeContent,
                            color = Color(0xFFE2E8F0),
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 20.sp
                        )
                    }
                } else {
                    inCodeBlock = true
                }
                return@forEach
            }

            if (inCodeBlock) {
                codeBuffer.add(line)
                return@forEach
            }

            if (trimmed.isBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                return@forEach
            }

            when {
                // Bullet lines starting with "* ", "• ", or "- "
                trimmed.startsWith("* ") || trimmed.startsWith("• ") || trimmed.startsWith("- ") -> {
                    val rawBulletContent = trimmed.removePrefix("* ").removePrefix("• ").removePrefix("- ").trim()
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "•",
                            color = SunsetOrange,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = parseMarkdownAnnotatedString(rawBulletContent, textColor),
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Numbered list item: "1. ", "2. ", etc.
                Regex("""^\d+\.\s+.*""").matches(trimmed) -> {
                    val dotIndex = trimmed.indexOf(". ")
                    val prefix = trimmed.substring(0, dotIndex + 2)
                    val numContent = trimmed.substring(dotIndex + 2).trim()
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = prefix,
                            color = SunsetOrange,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = parseMarkdownAnnotatedString(numContent, textColor),
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Markdown Headers: "# ", "## ", "### "
                trimmed.startsWith("#") -> {
                    val headerContent = trimmed.replace(Regex("""^#+\s*"""), "")
                    Text(
                        text = parseMarkdownAnnotatedString(headerContent, textColor),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )
                }

                // Normal paragraph text (parses inline **bold** and `code` seamlessly)
                else -> {
                    Text(
                        text = parseMarkdownAnnotatedString(trimmed, textColor),
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // Flush remaining code block if closing fence was omitted
        if (inCodeBlock && codeBuffer.isNotEmpty()) {
            val codeContent = codeBuffer.joinToString("\n")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F1015))
                    .padding(12.dp)
            ) {
                Text(
                    text = codeContent,
                    color = Color(0xFFE2E8F0),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

