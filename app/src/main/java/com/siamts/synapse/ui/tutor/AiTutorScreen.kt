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
import androidx.compose.ui.text.style.TextOverflow
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
    onNavigateToQuiz: ((subjectId: String?, subjectName: String?) -> Unit)? = null,
    onNavigateToSubject: ((subjectId: String) -> Unit)? = null,
    onNavigateToNotes: (() -> Unit)? = null,
    onNavigateToProfile: (() -> Unit)? = null,
    onNavigateToProgress: (() -> Unit)? = null,
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

    LaunchedEffect(viewModel) {
        viewModel.actionEvent.collect { action ->
            when (action) {
                is AiUiAction.NavigateToQuiz -> {
                    onNavigateToQuiz?.invoke(action.subjectId, action.subjectName)
                }
                is AiUiAction.NavigateToSubject -> {
                    onNavigateToSubject?.invoke(action.subjectId)
                }
                is AiUiAction.NavigateToNotes -> {
                    onNavigateToNotes?.invoke()
                }
                is AiUiAction.NavigateToProfile -> {
                    onNavigateToProfile?.invoke()
                }
                is AiUiAction.NavigateToProgress -> {
                    onNavigateToProgress?.invoke()
                }
                is AiUiAction.ShowProgressSummary -> {
                    Toast.makeText(
                        context,
                        "Progress: ${action.topicsDone} topics mastered · ${action.avgScore}% avg · ${action.streak} day streak",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

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
            onSaveNote = { message ->
                viewModel.onSaveNoteClicked(message)
            },
            onElaborate = { message ->
                viewModel.onElaborateClicked(message)
            },
            onOpenNotes = {
                onNavigateToNotes?.invoke()
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
    onSaveNote: (ChatMessage) -> Unit = {},
    onElaborate: (ChatMessage) -> Unit = {},
    onOpenNotes: () -> Unit = {},
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
    val inputCardBgColor = if (isDark) Color(0xFF0D1322) else Color.White
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
            // Top Header Bar (Glowing Emblem + Live Offline Badge + Sleek New Chat Pill)
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
                            text = "SYNAPSE AI",
                            color = headerTextColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(
                                    color = if (isDark) Color(0xFF0D1E28) else Color(0xFFECFDF5),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isDark) Color(0xFF059669).copy(alpha = 0.5f) else Color(0xFFA7F3D0),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFF10B981), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "OFFLINE NEURAL CORE",
                                color = if (isDark) CyanGlow else Color(0xFF059669),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                            )
                        }
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
                                if (isDark) CyanGlow.copy(alpha = 0.5f) else Color(0xFFBFDBFE),
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
                                } else if (message.isSavedNoteCard) {
                                    SavedNoteInteractiveCard(
                                        message = message,
                                        onOpenNotes = onOpenNotes
                                    )
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
                                        },
                                        onSaveNote = { onSaveNote(message) },
                                        onElaborate = { onElaborate(message) }
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

                    // Ultra-Clean Chat Input Bar Box (Floating Neural Glass Dock)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .imePadding()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(28.dp))
                                .background(inputCardBgColor)
                                .border(
                                    width = 1.dp,
                                    color = if (isDark) CyanGlow.copy(alpha = 0.35f) else Color(0xFFCBD5E1),
                                    shape = RoundedCornerShape(28.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 6.dp)
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
                                            text = if (uiState.isGenerating) "SYNAPSE AI is thinking..." else "Ask anything or say 'save note'...",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 14.sp,
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedTextColor = inputTextColor,
                                        unfocusedTextColor = inputTextColor,
                                    ),
                                    singleLine = true,
                                )

                                // Animated Live Voice Mic Launcher
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(
                                            brush = Brush.radialGradient(
                                                listOf(CyanGlow.copy(alpha = 0.25f), Color(0xFF2563EB).copy(alpha = 0.15f))
                                            )
                                        )
                                        .border(1.dp, CyanGlow.copy(alpha = 0.4f), CircleShape)
                                        .clickable { onMicClick() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🎙️", fontSize = 18.sp)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Send Button (Instant 0ms clear & send)
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(
                                            brush = if (canSend) {
                                                Brush.horizontalGradient(listOf(Color(0xFF00F0FF), Color(0xFF8B5CF6)))
                                            } else {
                                                Brush.horizontalGradient(
                                                    listOf(
                                                        Color(0xFF94A3B8).copy(alpha = 0.35f),
                                                        Color(0xFFCBD5E1).copy(alpha = 0.35f)
                                                    )
                                                )
                                            }
                                        )
                                        .clickable(enabled = canSend) { onSendMessage() },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "↑",
                                        color = Color.White.copy(alpha = if (canSend) 1f else 0.5f),
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
 * Modern User Message Bubble (Vibrant Cyan-to-Violet gradient pill aligned right).
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
                        colors = listOf(Color(0xFF00F0FF), Color(0xFF8B5CF6))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.SemiBold
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
 * Interactive Saved Note Confirmation Card.
 * Tapping navigates the student directly to Notes.
 */
@Composable
private fun SavedNoteInteractiveCard(
    message: ChatMessage,
    onOpenNotes: () -> Unit,
) {
    val isDark = LocalThemeIsDark.current
    val emeraldGlow = Color(0xFF10B981)
    val cardBg = if (isDark) Color(0xFF091F18) else Color(0xFFECFDF5)
    val borderColor = if (isDark) Color(0xFF059669).copy(alpha = 0.6f) else Color(0xFFA7F3D0)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onOpenNotes() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(emeraldGlow.copy(alpha = 0.15f))
                        .border(1.dp, emeraldGlow.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✓", color = emeraldGlow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Saved to Notes",
                        color = emeraldGlow,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = message.savedNoteTitle ?: message.text.ifBlank { "Tap to view in Notes" },
                        color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(emeraldGlow.copy(alpha = 0.2f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Tap to View",
                        color = emeraldGlow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "→",
                        color = emeraldGlow,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * ChatGPT / Gemini Grade Full-Width AI Response Card (Obsidian Glass + Cyan/Violet Tint)
 * with glowing avatar, formatted markdown, and inline action chips (Listen, Copy, Save Note, Elaborate).
 */
@Composable
private fun AiMessageBubble(
    message: ChatMessage,
    isGenerating: Boolean,
    isSpeaking: Boolean,
    onCopyClick: () -> Unit,
    onSpeakClick: () -> Unit,
    onSaveNote: () -> Unit,
    onElaborate: () -> Unit,
) {
    val isDark = LocalThemeIsDark.current
    val cardBg = if (isDark) Color(0xFF0D1322) else Color.White
    val cardBorder = if (isDark) Color(0xFF00F0FF).copy(alpha = 0.25f) else Color(0xFFE2E8F0)
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
                NeuralWaveTypingIndicator()
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Text Content
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
            FormattedAiText(text = message.text, textColor = textColor)

            if (!message.paragraph2.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                FormattedAiText(text = message.paragraph2, textColor = textColor)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Toolbar (Timestamp on left, Action Chips on right)
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Listen / Stop Button
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
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isSpeaking) "Stop" else "Listen",
                            color = if (isSpeaking) Color(0xFF10B981) else Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

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
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Copy",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Save Note Action Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                        .clickable { onSaveNote() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📝", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Save Note",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Elaborate Action Chip (Visible only on concept answers)
                if (message.isConceptAnswer) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        CyanGlow.copy(alpha = 0.15f),
                                        PurpleAccent.copy(alpha = 0.15f)
                                    )
                                )
                            )
                            .border(
                                width = 1.dp,
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        CyanGlow.copy(alpha = 0.5f),
                                        PurpleAccent.copy(alpha = 0.5f)
                                    )
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onElaborate() }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✨", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Elaborate",
                                color = if (isDark) CyanGlow else BlueAccent,
                                fontSize = 11.sp,
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
 * Animated 4-bar neural wave indicator (ChatGPT/Gemini style thinking waveform).
 */
@Composable
fun NeuralWaveTypingIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "neuralWave")

    val height1 by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(420, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave1"
    )
    val height2 by infiniteTransition.animateFloat(
        initialValue = 16f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(480, delayMillis = 80, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave2"
    )
    val height3 by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(390, delayMillis = 160, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave3"
    )
    val height4 by infiniteTransition.animateFloat(
        initialValue = 14f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, delayMillis = 240, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave4"
    )

    Row(
        modifier = modifier.height(20.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(height1.dp)
                .clip(RoundedCornerShape(1.5.dp))
                .background(Color(0xFF00F0FF))
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(height2.dp)
                .clip(RoundedCornerShape(1.5.dp))
                .background(Color(0xFF38BDF8))
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(height3.dp)
                .clip(RoundedCornerShape(1.5.dp))
                .background(Color(0xFF818CF8))
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(height4.dp)
                .clip(RoundedCornerShape(1.5.dp))
                .background(Color(0xFFA855F7))
        )
    }
}

/**
 * Pulsing typing animation indicator for backward compatibility.
 */
@Composable
fun PulsingDotsIndicator(modifier: Modifier = Modifier) {
    NeuralWaveTypingIndicator(modifier = modifier)
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
