package com.siamts.synapse.ui.tutor

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siamts.synapse.ai.VoiceAssistantManager
import com.siamts.synapse.ai.VoiceState
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.DarkBackground
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.LightBackground
import com.siamts.synapse.ui.theme.LightTextPrimary
import com.siamts.synapse.ui.theme.LocalThemeIsDark
import com.siamts.synapse.ui.theme.PurpleAccent
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.welcome.components.BrainGraphic
import com.siamts.synapse.ui.welcome.components.DashedBorderBox
import kotlinx.coroutines.launch

@Composable
fun AiTutorScreen(
    viewModel: AiTutorViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val voiceAssistant = remember(context) { viewModel.getOrCreateVoiceAssistant(context) }

    val voiceState by voiceAssistant.voiceState.collectAsState()
    val audioLevel by voiceAssistant.audioLevel.collectAsState()
    val userSpokenText by voiceAssistant.userSpokenText.collectAsState()
    val aiSpokenText by voiceAssistant.aiSpokenText.collectAsState()
    val isLiveActive by voiceAssistant.isLiveActive.collectAsState()

    var showLiveVoiceOverlay by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showLiveVoiceOverlay = true
            voiceAssistant.startLiveSession()
        } else {
            Toast.makeText(context, "Microphone permission is required for voice chat", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            voiceAssistant.stopLiveSession()
            voiceAssistant.stopSpeaking()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AiTutorContent(
            uiState = uiState,
            onQueryChange = { viewModel.onInputQueryChanged(it) },
            onSendMessage = { viewModel.onSendMessage() },
            onClearChat = { viewModel.onClearChat() },
            onPromptClick = { viewModel.onPromptClicked(it) },
            onSpeakMessage = { messageId, text ->
                viewModel.toggleSpeakMessage(messageId, text, context)
            },
            onMicClick = {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    showLiveVoiceOverlay = true
                    voiceAssistant.startLiveSession()
                } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            },
            onBackClick = onBackClick,
            modifier = Modifier.fillMaxSize(),
        )

        // Gemini Live / ChatGPT Voice Animated Full-Screen Overlay
        LiveVoiceChatOverlay(
            isVisible = showLiveVoiceOverlay,
            voiceState = voiceState,
            audioLevel = audioLevel,
            userSpokenText = userSpokenText,
            aiSpokenText = aiSpokenText,
            onTogglePauseResume = { voiceAssistant.togglePauseResume() },
            onClose = {
                voiceAssistant.stopLiveSession()
                showLiveVoiceOverlay = false
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun AiTutorContent(
    uiState: AiTutorUiState,
    onQueryChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onClearChat: () -> Unit = {},
    onPromptClick: (String) -> Unit,
    onSpeakMessage: (messageId: String, text: String) -> Unit = { _, _ -> },
    onMicClick: () -> Unit = {},
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val isDark = LocalThemeIsDark.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val pageBgGradient = if (isDark) {
        listOf(DarkBackgroundTop, Color(0xFF090D22), DarkBackgroundBottom)
    } else {
        listOf(Color(0xFFE0E7FF), Color(0xFFEEF2F6), LightBackground)
    }
    val bodyBgColor = if (isDark) DarkBackground else Color(0xFFF8FAFC)
    val headerTextColor = if (isDark) TextPrimary else LightTextPrimary
    val inputCardBgColor = if (isDark) Color(0xFF131C38) else Color.White
    val inputTextColor = if (isDark) TextPrimary else LightTextPrimary
    val borderColor = if (isDark) Color(0xFF1E2B58) else Color(0xFFE2E8F0)

    // Smooth auto-scroll to bottom whenever new messages arrive or streaming tokens update
    val lastMessageTextLength = uiState.messages.lastOrNull()?.let {
        it.text.length + (it.paragraph2?.length ?: 0)
    } ?: 0

    LaunchedEffect(uiState.messages.size, lastMessageTextLength, uiState.isGenerating) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(listState.layoutInfo.totalItemsCount.coerceAtLeast(1) - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = pageBgGradient))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Header Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBackClick) {
                        Text(
                            text = "←",
                            color = headerTextColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.headerTag,
                            color = if (isDark) CyanGlow else BlueAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.tutorTitle,
                            color = headerTextColor,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    // New Chat / Clear Chat action button
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isDark) Color(0xFF161F42) else Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                1.dp,
                                if (isDark) Color(0xFF2A3660) else Color(0xFFBFDBFE),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { onClearChat() }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✨", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "New",
                                color = if (isDark) CyanGlow else BlueAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            // Rounded Sheet Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = bodyBgColor,
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    if (uiState.messages.isEmpty()) {
                        // Empty Chat State: Centered Minimal Welcome Emblem (ChatGPT/Gemini Grade)
                        EmptyChatWelcomeEmblem(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            onSubjectClick = { onPromptClick("Tell me about $it") }
                        )
                    } else {
                        // Multi-Turn Conversation Messages List
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Online Neural Status Header
                            item(key = "status_header") {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .background(Color(0xFF10B981), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "SYNAPSE AI · 100% On-Device Neural Inference",
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Conversation Bubbles
                            items(items = uiState.messages, key = { it.id }) { message ->
                                if (message.isUser) {
                                    UserMessageBubble(message = message)
                                } else {
                                    AiMessageBubble(
                                        message = message,
                                        isGenerating = uiState.isGenerating && message == uiState.messages.lastOrNull { !it.isUser },
                                        isSpeaking = uiState.currentlySpeakingMessageId == message.id,
                                        onCopyClick = {
                                            val fullText = if (!message.paragraph2.isNullOrBlank()) {
                                                "${message.text}\n\n${message.paragraph2}"
                                            } else {
                                                message.text
                                            }
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("SYNAPSE AI Response", fullText)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                        },
                                        onSpeakClick = {
                                            val fullText = if (!message.paragraph2.isNullOrBlank()) {
                                                "${message.text}\n\n${message.paragraph2}"
                                            } else {
                                                message.text
                                            }
                                            onSpeakMessage(message.id, fullText)
                                        }
                                    )
                                }
                            }

                            // Bottom Breathing Spacer
                            item(key = "bottom_spacer") {
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Ultra-Clean Chat Input Bar Box (Zero Default Bottom Suggestion Chips)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .imePadding()
                    ) {
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 6.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                val canSend = uiState.inputQuery.isNotBlank() && !uiState.isGenerating

                                OutlinedTextField(
                                    value = uiState.inputQuery,
                                    onValueChange = onQueryChange,
                                    placeholder = {
                                        Text(
                                            text = if (uiState.isGenerating) "SYNAPSE AI is thinking..." else "Ask anything...",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 14.sp,
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BlueAccent,
                                        unfocusedBorderColor = borderColor,
                                        focusedContainerColor = inputCardBgColor,
                                        unfocusedContainerColor = inputCardBgColor,
                                        focusedTextColor = inputTextColor,
                                        unfocusedTextColor = inputTextColor,
                                    ),
                                    singleLine = true,
                                    trailingIcon = {
                                        // Animated Live Voice Mic Launcher
                                        Box(
                                            modifier = Modifier
                                                .padding(end = 4.dp)
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF2563EB).copy(alpha = 0.12f))
                                                .clickable { onMicClick() },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = "🎙️", fontSize = 18.sp)
                                        }
                                    }
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Send Button (Instant 0ms clear & send)
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(
                                            brush = if (canSend) {
                                                Brush.horizontalGradient(listOf(PurpleAccent, BlueAccent, CyanAccent))
                                            } else {
                                                Brush.horizontalGradient(
                                                    listOf(
                                                        Color(0xFF94A3B8).copy(alpha = 0.45f),
                                                        Color(0xFFCBD5E1).copy(alpha = 0.45f)
                                                    )
                                                )
                                            }
                                        )
                                        .clickable(enabled = canSend) { onSendMessage() },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "↑",
                                        color = Color.White.copy(alpha = if (canSend) 1f else 0.6f),
                                        fontSize = 20.sp,
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

/**
 * Centered minimal welcome screen shown when the chat is empty.
 * Displays a glowing neural emblem and "Ask anything or tap the mic to talk".
 */
@Composable
private fun EmptyChatWelcomeEmblem(
    modifier: Modifier = Modifier,
    onSubjectClick: (String) -> Unit
) {
    val isDark = LocalThemeIsDark.current

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Glowing Neural Emblem
        Box(
            modifier = Modifier
                .size(76.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(CyanGlow.copy(alpha = 0.8f), PurpleAccent.copy(alpha = 0.6f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
                .border(2.dp, CyanGlow, CircleShape)
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            BrainGraphic(modifier = Modifier.fillMaxSize())
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "SYNAPSE AI",
            color = if (isDark) TextPrimary else Color(0xFF0F172A),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Ask anything or tap the mic to talk",
            color = if (isDark) CyanGlow else BlueAccent,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "100% On-Device Neural Tutor · Instant Responses",
            color = Color(0xFF64748B),
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Minimal clickable capability chips
        val subjects = listOf("Mathematics", "Computer Science", "Artificial Intelligence", "Physics", "Chemistry")
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            subjects.chunked(2).forEach { rowSubjects ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowSubjects.forEach { subj ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) Color(0xFF131C38) else Color.White)
                                .border(1.dp, if (isDark) Color(0xFF1E2B58) else Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                                .clickable { onSubjectClick(subj) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = subj,
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Modern User Message Bubble (Sleek asymmetric gradient pill aligned right).
 */
@Composable
private fun UserMessageBubble(message: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 4.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF2563EB), Color(0xFF7C3AED))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "You · ${message.timeAgo}",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            modifier = Modifier.padding(end = 4.dp)
        )
    }
}

/**
 * ChatGPT / Gemini Grade Full-Width AI Response Card with glowing avatar, formatted markdown,
 * and copy / read-aloud icon actions.
 */
@Composable
private fun AiMessageBubble(
    message: ChatMessage,
    isGenerating: Boolean,
    isSpeaking: Boolean,
    onCopyClick: () -> Unit,
    onSpeakClick: () -> Unit
) {
    val isDark = LocalThemeIsDark.current
    val cardBg = if (isDark) Color(0xFF0F172A) else Color.White
    val cardBorder = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
    val textColor = if (isDark) TextPrimary else Color(0xFF0F172A)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(1.dp, cardBorder, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        // AI Header row with Avatar + Title + Subject Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
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
                                shape = RoundedCornerShape(10.dp)
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
                PulsingDotsIndicator()
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Text Content
        if (message.text.isEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = "SYNAPSE AI is thinking",
                    color = Color(0xFF64748B),
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic
                )
                PulsingDotsIndicator()
            }
        } else {
            FormattedAiText(text = message.text, textColor = textColor)

            if (!message.paragraph2.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                FormattedAiText(text = message.paragraph2, textColor = textColor)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Toolbar (Timestamp on left, Copy & Read Aloud on right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SYNAPSE AI · ${message.timeAgo}",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Copy Action Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                        .clickable { onCopyClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📋", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Copy",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Read Aloud / Speak Action Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSpeaking) {
                                Color(0xFF10B981).copy(alpha = 0.2f)
                            } else {
                                if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                            }
                        )
                        .clickable { onSpeakClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = if (isSpeaking) "⏹" else "🔊", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isSpeaking) "Stop" else "Listen",
                            color = if (isSpeaking) Color(0xFF10B981) else Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Formats Markdown bold headers, bullet items, and regular text cleanly.
 */
@Composable
private fun FormattedAiText(text: String, textColor: Color) {
    val lines = text.split("\n")
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
                        lineHeight = 20.sp
                    )
                }
                trimmed.startsWith("• ") || trimmed.startsWith("- ") -> {
                    val bulletText = trimmed.removePrefix("• ").removePrefix("- ")
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "•", color = CyanAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = bulletText,
                            color = textColor,
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                    }
                }
                else -> {
                    Text(
                        text = line,
                        color = textColor,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }
            }
        }
    }
}

/**
 * Pulsing typing animation indicator for ChatGPT-style thinking and streaming state.
 */
@Composable
fun PulsingDotsIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val dotAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dotAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, delayMillis = 180, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dotAlpha3 by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, delayMillis = 360, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(BlueAccent.copy(alpha = dotAlpha1), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(PurpleAccent.copy(alpha = dotAlpha2), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(CyanGlow.copy(alpha = dotAlpha3), CircleShape)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AiTutorScreenPreview() {
    SYNAPSETheme {
        AiTutorContent(
            uiState = AiTutorUiState(),
            onQueryChange = {},
            onSendMessage = {},
            onClearChat = {},
            onPromptClick = {},
            onBackClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
