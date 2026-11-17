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
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.BookmarkAdded
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
import com.siamts.synapse.data.local.ChatSessionEntity
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
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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

    // Hold-to-Talk & Slide-to-Lock State
    var isHoldingMic by remember { mutableStateOf(false) }
    var isLockedMic by remember { mutableStateOf(false) }
    var holdTimerSeconds by remember { mutableIntStateOf(0) }
    var transcribedSpeech by remember { mutableStateOf("") }

    // Timer loop for hold-to-talk recording
    LaunchedEffect(isHoldingMic, isLockedMic) {
        if (isHoldingMic || isLockedMic) {
            holdTimerSeconds = 0
            while (isHoldingMic || isLockedMic) {
                delay(1000L)
                holdTimerSeconds++
            }
        }
    }

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
            voiceAssistant.cancelDictation()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AiTutorContent(
            uiState = uiState,
            isHoldingMic = isHoldingMic,
            isLockedMic = isLockedMic,
            holdTimerSeconds = holdTimerSeconds,
            transcribedSpeech = transcribedSpeech,
            onQueryChange = { viewModel.onInputQueryChanged(it) },
            onSendMessage = { viewModel.onSendMessage() },
            onNewChat = { viewModel.onNewChat() },
            onOpenHistory = { viewModel.toggleHistorySheet(true) },
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
            onBackClick = onBackClick,
            onMicTouchDown = {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED

                if (!hasPermission) {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    false
                } else {
                    isHoldingMic = true
                    transcribedSpeech = ""
                    voiceAssistant.startDictation(
                        onPartial = { transcribedSpeech = it },
                        onComplete = {
                            transcribedSpeech = it
                            if (!isLockedMic && it.isNotBlank()) {
                                viewModel.onSendMessage(it)
                            }
                        }
                    )
                    true
                }
            },
            onMicSlideLocked = {
                isLockedMic = true
                isHoldingMic = false
            },
            onMicTouchUp = { durationMs ->
                if (!isLockedMic) {
                    isHoldingMic = false
                    if (durationMs < 350L && transcribedSpeech.isBlank()) {
                        // Short tap -> Open full-screen Live Voice Assistant
                        voiceAssistant.cancelDictation()
                        showLiveVoiceOverlay = true
                        voiceAssistant.startLiveSession()
                    } else {
                        val text = voiceAssistant.stopDictationAndGet()
                        val toSend = text.ifBlank { transcribedSpeech }.trim()
                        if (toSend.isNotBlank()) {
                            viewModel.onSendMessage(toSend)
                        }
                        transcribedSpeech = ""
                    }
                }
            },
            onCancelLockedRecording = {
                isLockedMic = false
                isHoldingMic = false
                voiceAssistant.cancelDictation()
                transcribedSpeech = ""
            },
            onSendLockedRecording = {
                isLockedMic = false
                isHoldingMic = false
                val text = voiceAssistant.stopDictationAndGet()
                val toSend = text.ifBlank { transcribedSpeech }.trim()
                if (toSend.isNotBlank()) {
                    viewModel.onSendMessage(toSend)
                }
                transcribedSpeech = ""
            },
            modifier = Modifier.fillMaxSize(),
        )

        // Persistent Chat History Bottom Sheet Modal
        if (uiState.isHistorySheetVisible) {
            ChatHistoryBottomSheet(
                sessions = uiState.chatSessions,
                currentSessionId = uiState.currentSessionId,
                onSelectSession = { sessionId ->
                    viewModel.onSelectSession(sessionId)
                },
                onDeleteSession = { sessionId ->
                    viewModel.onDeleteSession(sessionId)
                },
                onNewChat = {
                    viewModel.onNewChat()
                },
                onDismiss = {
                    viewModel.toggleHistorySheet(false)
                }
            )
        }

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
    isHoldingMic: Boolean = false,
    isLockedMic: Boolean = false,
    holdTimerSeconds: Int = 0,
    transcribedSpeech: String = "",
    onQueryChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onNewChat: () -> Unit = {},
    onOpenHistory: () -> Unit = {},
    onPromptClick: (String) -> Unit,
    onSpeakMessage: (messageId: String, text: String) -> Unit = { _, _ -> },
    onSaveNote: (ChatMessage) -> Unit = {},
    onElaborate: (ChatMessage) -> Unit = {},
    onOpenNotes: () -> Unit = {},
    onBackClick: () -> Unit,
    onMicTouchDown: () -> Boolean = { true },
    onMicSlideLocked: () -> Unit = {},
    onMicTouchUp: (Long) -> Unit = {},
    onCancelLockedRecording: () -> Unit = {},
    onSendLockedRecording: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val isDark = LocalThemeIsDark.current
    val context = LocalContext.current

    val pageBgGradient = if (isDark) {
        listOf(DarkBackgroundTop, Color(0xFF090D22), DarkBackgroundBottom)
    } else {
        listOf(Color(0xFFE0E7FF), Color(0xFFEEF2F6), LightBackground)
    }
    val bodyBgColor = if (isDark) DarkBackground else Color(0xFFF8FAFC)
    val headerTextColor = if (isDark) TextPrimary else LightTextPrimary
    val inputCardBgColor = if (isDark) Color(0xFF0D1322) else Color.White
    val inputTextColor = if (isDark) TextPrimary else LightTextPrimary

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
            // Top Header Bar (Clean Minimalist: Back, Title, History & New Chat Pill - ZERO "Offline" Badge)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = headerTextColor
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "SYNAPSE AI",
                            color = headerTextColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                        )
                        Text(
                            text = "Neural Study Companion",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Chat History Drawer / Bottom Sheet trigger
                    IconButton(
                        onClick = onOpenHistory,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = if (isDark) Color(0xFF131C38) else Color(0xFFEFF6FF),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.History,
                            contentDescription = "Chat History",
                            tint = if (isDark) CyanGlow else BlueAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // + New Chat Pill (Starts fresh session without deleting past ones)
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isDark) Color(0xFF161F42) else Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                1.dp,
                                if (isDark) CyanGlow.copy(alpha = 0.4f) else Color(0xFFBFDBFE),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { onNewChat() }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "New Chat",
                                tint = if (isDark) CyanGlow else BlueAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
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
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    if (uiState.messages.isEmpty()) {
                        // Empty Chat State: Centered Fresh Welcome Screen (Requirement 11)
                        EmptyChatWelcomeEmblem(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            onSubjectClick = { onPromptClick("Tell me about $it") }
                        )
                    } else {
                        // Multi-Turn Conversation Messages List (ChatGPT-Grade Minimal Canvas)
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
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

                            item(key = "bottom_spacer") {
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Hands-Free Locked Recording Bar OR Regular Floating Input Bar
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .imePadding()
                    ) {
                        if (isLockedMic) {
                            // Locked Hands-Free Recording Dock (Emerald lock, wave pulse, Cancel & Send)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(28.dp))
                                    .background(if (isDark) Color(0xFF061A14) else Color(0xFFECFDF5))
                                    .border(
                                        width = 1.5.dp,
                                        color = Color(0xFF10B981).copy(alpha = 0.6f),
                                        shape = RoundedCornerShape(28.dp)
                                    )
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Cancel Button
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clickable { onCancelLockedRecording() }
                                            .padding(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = "Cancel Recording",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Cancel",
                                            color = Color(0xFFEF4444),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    // Lock Indicator & Recording Timer
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Rounded.Lock,
                                            contentDescription = "Locked Hands-free",
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        NeuralWaveTypingIndicator()
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = formatTimer(holdTimerSeconds),
                                            color = Color(0xFF10B981),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Send Spoken Query Button
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                            .clickable { onSendLockedRecording() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Rounded.Send,
                                            contentDescription = "Send Voice Query",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            // Ultra-Clean Chat Input Bar (minHeight = 48.dp, multi-line 4 max, 0 cursor clipping)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(28.dp))
                                    .background(inputCardBgColor)
                                    .border(
                                        width = 1.dp,
                                        color = if (isHoldingMic) Color(0xFF00F0FF) else if (isDark) CyanGlow.copy(alpha = 0.35f) else Color(0xFFCBD5E1),
                                        shape = RoundedCornerShape(28.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    val canSend = uiState.inputQuery.isNotBlank() && !uiState.isGenerating

                                    if (isHoldingMic) {
                                        // Live Pulsing Hold-to-Talk Status Banner
                                        Row(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(horizontal = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .background(Color(0xFFEF4444), CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = formatTimer(holdTimerSeconds),
                                                color = if (isDark) CyanGlow else BlueAccent,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Listening... Slide up to lock",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    } else {
                                        OutlinedTextField(
                                            value = uiState.inputQuery,
                                            onValueChange = onQueryChange,
                                            placeholder = {
                                                Text(
                                                    text = if (uiState.isGenerating) "SYNAPSE AI is thinking..." else "Ask SYNAPSE AI anything...",
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 14.sp,
                                                )
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .defaultMinSize(minHeight = 48.dp),
                                            shape = RoundedCornerShape(24.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color.Transparent,
                                                unfocusedBorderColor = Color.Transparent,
                                                focusedContainerColor = Color.Transparent,
                                                unfocusedContainerColor = Color.Transparent,
                                                focusedTextColor = inputTextColor,
                                                unfocusedTextColor = inputTextColor,
                                            ),
                                            singleLine = false,
                                            maxLines = 4,
                                        )
                                    }

                                    // WhatsApp-Style Hold-to-Talk Mic with Slide-to-Lock Gesture
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                brush = if (isHoldingMic) {
                                                    Brush.radialGradient(listOf(Color(0xFF00F0FF), Color(0xFF2563EB)))
                                                } else {
                                                    Brush.radialGradient(
                                                        listOf(CyanGlow.copy(alpha = 0.25f), Color(0xFF2563EB).copy(alpha = 0.15f))
                                                    )
                                                }
                                            )
                                            .border(
                                                1.dp,
                                                if (isHoldingMic) Color(0xFF00F0FF) else CyanGlow.copy(alpha = 0.4f),
                                                CircleShape
                                            )
                                            .pointerInput(Unit) {
                                                awaitPointerEventScope {
                                                    while (true) {
                                                        val down = awaitFirstDown(requireUnconsumed = false)
                                                        val startTime = System.currentTimeMillis()
                                                        var isLocked = false
                                                        var totalDragY = 0f

                                                        val success = onMicTouchDown()
                                                        if (!success) continue

                                                        while (true) {
                                                            val event = awaitPointerEvent()
                                                            val change = event.changes.firstOrNull() ?: break
                                                            if (!change.pressed) {
                                                                if (!isLocked) {
                                                                    val duration = System.currentTimeMillis() - startTime
                                                                    onMicTouchUp(duration)
                                                                }
                                                                break
                                                            }
                                                            val dragY = change.position.y - down.position.y
                                                            totalDragY = dragY
                                                            if (totalDragY < -100f && !isLocked) {
                                                                isLocked = true
                                                                onMicSlideLocked()
                                                            }
                                                        }
                                                    }
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Mic,
                                            contentDescription = "Hold to talk or tap for Live Voice",
                                            tint = if (isHoldingMic) Color.White else if (isDark) CyanGlow else BlueAccent,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Send Button (Instant 0ms clear & send)
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
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
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Rounded.Send,
                                            contentDescription = "Send",
                                            tint = Color.White.copy(alpha = if (canSend) 1f else 0.5f),
                                            modifier = Modifier.size(18.dp)
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

/**
 * Centered minimal welcome screen shown when the chat is empty (Requirement 11).
 * Displays Title "Welcome to SYNAPSE AI", Subtitle, and clickable subject chips.
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

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Welcome to SYNAPSE AI",
            color = if (isDark) TextPrimary else Color(0xFF0F172A),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your smart study friend for clear concepts, instant quizzes, and revision notes. What shall we learn today?",
            color = Color(0xFF94A3B8),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Clickable capability chips
        val subjects = listOf("Mathematics", "Computer Science", "Artificial Intelligence", "Physics", "Chemistry", "Data Science")
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
                .clip(RoundedCornerShape(20.dp))
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
                lineHeight = 22.sp,
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
                        text = "View Notes",
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
 * Minimalist ChatGPT-Grade AI Response Bubble (Direct Dark Canvas - Zero Cramped Box Borders).
 * Renders AI replies with generous 16dp horizontal padding, 22sp comfortable line height,
 * and a sleek minimalist icon row (TTS Listen, Copy, emerald Save Note bookmark, Elaborate).
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
    val textColor = if (isDark) TextPrimary else Color(0xFF0F172A)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp)
    ) {
        // AI Header row: Avatar + Title + Subject Badge
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
            FormattedAiText(text = message.text, textColor = textColor)

            if (!message.paragraph2.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                FormattedAiText(text = message.paragraph2, textColor = textColor)
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
 * Persistent Chat History Bottom Sheet Modal (Requirement 10).
 * Displays past chat sessions stored in SQLite with selection & deletion.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatHistoryBottomSheet(
    sessions: List<ChatSessionEntity>,
    currentSessionId: String,
    onSelectSession: (String) -> Unit,
    onDeleteSession: (String) -> Unit,
    onNewChat: () -> Unit,
    onDismiss: () -> Unit,
) {
    val isDark = LocalThemeIsDark.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isDark) Color(0xFF0C132C) else Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chat History",
                    color = if (isDark) TextPrimary else Color(0xFF0F172A),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0xFF1E293B) else Color(0xFFEFF6FF))
                        .clickable {
                            onNewChat()
                            onDismiss()
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "New",
                            tint = if (isDark) CyanGlow else BlueAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "New Chat",
                            color = if (isDark) CyanGlow else BlueAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (sessions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No saved chat sessions yet.\nAsk SYNAPSE AI a study question to get started!",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sessions, key = { it.sessionId }) { session ->
                        val isCurrent = session.sessionId == currentSessionId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isCurrent) {
                                        if (isDark) Color(0xFF162544) else Color(0xFFDBEAFE)
                                    } else {
                                        if (isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9)
                                    }
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isCurrent) CyanGlow.copy(alpha = 0.5f) else Color.Transparent,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    onSelectSession(session.sessionId)
                                    onDismiss()
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ChatBubbleOutline,
                                    contentDescription = null,
                                    tint = if (isCurrent) CyanGlow else Color(0xFF94A3B8),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = session.title,
                                        color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                        fontSize = 14.sp,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = formatTimeAgo(session.updatedAt),
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onDeleteSession(session.sessionId) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.DeleteOutline,
                                    contentDescription = "Delete Session",
                                    tint = Color(0xFFEF4444).copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Formats Markdown bold headers, bullet items, and regular text cleanly with comfortable 22.sp line height.
 */
@Composable
private fun FormattedAiText(text: String, textColor: Color) {
    val lines = text.split("\n")
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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

private fun formatTimeAgo(timestamp: Long): String {
    val diff = (System.currentTimeMillis() - timestamp).coerceAtLeast(0L)
    val minutes = diff / (1000 * 60)
    val hours = minutes / 60
    val days = hours / 24
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes min ago"
        hours < 24 -> "$hours hr ago"
        days == 1L -> "Yesterday"
        else -> "$days days ago"
    }
}

private fun formatTimer(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format(Locale.ROOT, "%02d:%02d", m, s)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AiTutorScreenPreview() {
    SYNAPSETheme {
        AiTutorContent(
            uiState = AiTutorUiState(),
            onQueryChange = {},
            onSendMessage = {},
            onNewChat = {},
            onOpenHistory = {},
            onPromptClick = {},
            onBackClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
