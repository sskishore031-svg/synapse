package com.siamts.synapse.ui.tutor

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun AiTutorScreen(
    viewModel: AiTutorViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AiTutorContent(
        uiState = uiState,
        onQueryChange = { viewModel.onInputQueryChanged(it) },
        onSendMessage = { viewModel.onSendMessage() },
        onClearChat = { viewModel.onClearChat() },
        onPromptClick = { viewModel.onPromptClicked(it) },
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun AiTutorContent(
    uiState: AiTutorUiState,
    onQueryChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onClearChat: () -> Unit = {},
    onPromptClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val isDark = LocalThemeIsDark.current
    val context = LocalContext.current

    var isListening by remember { mutableStateOf(false) }
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startSpeechRecognition(
                context = context,
                setRecognizer = { speechRecognizer = it },
                setListening = { isListening = it },
                onQueryChange = onQueryChange,
                currentQuery = uiState.inputQuery
            )
        } else {
            Toast.makeText(context, "Microphone permission is required for voice input", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer?.destroy()
            speechRecognizer = null
        }
    }

    val onMicClick = {
        if (isListening) {
            speechRecognizer?.stopListening()
            isListening = false
        } else {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                startSpeechRecognition(
                    context = context,
                    setRecognizer = { speechRecognizer = it },
                    setListening = { isListening = it },
                    onQueryChange = onQueryChange,
                    currentQuery = uiState.inputQuery
                )
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    val pageBgGradient = if (isDark) {
        listOf(DarkBackgroundTop, Color(0xFF090D22), DarkBackgroundBottom)
    } else {
        listOf(Color(0xFFE0E7FF), Color(0xFFEEF2F6), LightBackground)
    }
    val bodyBgColor = if (isDark) DarkBackground else Color(0xFFF8FAFC)
    val headerTextColor = if (isDark) TextPrimary else LightTextPrimary
    val aiCardBgColor = if (isDark) Color(0xFF131C38) else Color.White
    val aiCardBorderColor = if (isDark) Color(0xFF1E2B58) else Color(0xFFE2E8F0)
    val inputCardBgColor = if (isDark) Color(0xFF131C38) else Color.White
    val inputTextColor = if (isDark) TextPrimary else LightTextPrimary

    // Smoothly scroll down whenever new messages arrive or streaming tokens update
    val lastMessageTextLength = uiState.messages.lastOrNull()?.let {
        it.text.length + (it.paragraph2?.length ?: 0)
    } ?: 0

    LaunchedEffect(uiState.messages.size, lastMessageTextLength, uiState.isGenerating) {
        if (uiState.messages.isNotEmpty()) {
            listState.scrollToItem(listState.layoutInfo.totalItemsCount.coerceAtLeast(1) - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = pageBgGradient))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Header Section
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

                    // Clear Chat / New Chat action button in TopAppBar
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
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    // Continuous Multi-Turn Chat List (LazyColumn with auto-scroll)
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. AI Assistant Status Bar
                        item(key = "status_bar") {
                            DashedBorderBox(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = 8.dp
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                brush = Brush.linearGradient(listOf(PurpleAccent, CyanGlow)),
                                                shape = CircleShape
                                            )
                                            .padding(4.dp)
                                    ) {
                                        BrainGraphic(modifier = Modifier.fillMaxSize())
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = uiState.tutorName,
                                            color = Color(0xFF0F172A),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = "🟢 ", fontSize = 10.sp)
                                            Text(
                                                text = "Online · 100% On-Device Neural Inference",
                                                color = Color(0xFF0284C7),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Time Header Box
                        item(key = "time_header") {
                            DashedBorderBox(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = 6.dp
                            ) {
                                Text(
                                    text = uiState.timeHeader,
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }

                        // 3. Multi-Turn Conversation Messages
                        items(items = uiState.messages, key = { it.id }) { message ->
                            if (message.isUser) {
                                // User Chat Message Bubble Box
                                DashedBorderBox(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = 8.dp
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.88f)
                                            .align(Alignment.CenterEnd)
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    colors = listOf(
                                                        BlueAccent,
                                                        PurpleAccent,
                                                    )
                                                ),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .padding(14.dp)
                                    ) {
                                        Text(
                                            text = message.text,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                        )
                                    }
                                }
                            } else {
                                // AI Tutor Response Bubble Box
                                DashedBorderBox(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = 8.dp
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.92f)
                                            .align(Alignment.CenterStart)
                                            .background(
                                                color = aiCardBgColor,
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .border(1.dp, aiCardBorderColor, RoundedCornerShape(16.dp))
                                            .padding(14.dp)
                                    ) {
                                        Column {
                                            if (message.text.isEmpty()) {
                                                // Streaming thinking state
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
                                                Text(
                                                    text = message.text,
                                                    color = inputTextColor,
                                                    fontSize = 13.sp,
                                                    lineHeight = 18.sp,
                                                )
                                                if (!message.paragraph2.isNullOrBlank()) {
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                    Text(
                                                        text = message.paragraph2,
                                                        color = inputTextColor,
                                                        fontSize = 13.sp,
                                                        lineHeight = 18.sp,
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = "SYNAPSE AI · ${message.timeAgo}",
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 11.sp,
                                                )
                                                if (uiState.isGenerating && message == uiState.messages.lastOrNull { !it.isUser }) {
                                                    PulsingDotsIndicator()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 4. AI Thought Status Box
                        item(key = "thought_status") {
                            DashedBorderBox(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = 6.dp
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = uiState.aiThoughtStatus,
                                        color = Color(0xFF0284C7),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (uiState.isGenerating) {
                                        PulsingDotsIndicator()
                                    }
                                }
                            }
                        }

                        // 5. Quick Follow-up Action Chips Box
                        item(key = "prompt_chips") {
                            DashedBorderBox(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = 8.dp
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    uiState.suggestedPrompts.forEach { prompt ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.White, RoundedCornerShape(12.dp))
                                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                                .clickable(enabled = !uiState.isGenerating) { onPromptClick(prompt) }
                                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(text = "🧩", fontSize = 14.sp)
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = prompt,
                                                        color = if (uiState.isGenerating) Color(0xFF94A3B8) else BlueAccent,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                    )
                                                }
                                                Text(
                                                    text = "↗",
                                                    color = if (uiState.isGenerating) Color(0xFF94A3B8) else BlueAccent,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 6. Bottom Spacer Box
                        item(key = "bottom_spacer") {
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Chat Input Bar Box with automatic IME Keyboard Elevation
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .imePadding()
                    ) {
                        AnimatedVisibility(visible = isListening) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(listOf(Color(0xFF2563EB), Color(0xFF7C3AED))),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(Color(0xFFEF4444), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "🎙️ Listening... Speak your academic question",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Tap to Stop",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {
                                        speechRecognizer?.stopListening()
                                        isListening = false
                                    }
                                )
                            }
                        }

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
                                            text = if (isListening) "Listening to your voice..." else if (uiState.isGenerating) "SYNAPSE AI is thinking..." else "Ask a question or follow up...",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 14.sp,
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BlueAccent,
                                        unfocusedBorderColor = aiCardBorderColor,
                                        focusedContainerColor = inputCardBgColor,
                                        unfocusedContainerColor = inputCardBgColor,
                                        focusedTextColor = inputTextColor,
                                        unfocusedTextColor = inputTextColor,
                                    ),
                                    singleLine = true,
                                    trailingIcon = {
                                        Box(
                                            modifier = Modifier
                                                .padding(end = 4.dp)
                                                .size(34.dp)
                                                .background(
                                                    color = if (isListening) Color(0xFFEF4444).copy(alpha = 0.15f) else Color.Transparent,
                                                    shape = CircleShape
                                                )
                                                .clickable { onMicClick() },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (isListening) "🔴" else "🎙️",
                                                fontSize = 18.sp
                                            )
                                        }
                                    }
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Send Button Circle (disabled while streaming)
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(
                                            brush = if (canSend) {
                                                Brush.horizontalGradient(
                                                    colors = listOf(
                                                        PurpleAccent,
                                                        BlueAccent,
                                                        CyanAccent,
                                                    )
                                                )
                                            } else {
                                                Brush.horizontalGradient(
                                                    colors = listOf(
                                                        Color(0xFF94A3B8).copy(alpha = 0.5f),
                                                        Color(0xFFCBD5E1).copy(alpha = 0.5f)
                                                    )
                                                )
                                            },
                                            shape = CircleShape
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

/**
 * Connects the microphone to Android SpeechRecognizer with real-time partial results
 * and robust error / lifecycle handling.
 */
private fun startSpeechRecognition(
    context: Context,
    setRecognizer: (SpeechRecognizer?) -> Unit,
    setListening: (Boolean) -> Unit,
    onQueryChange: (String) -> Unit,
    currentQuery: String,
) {
    if (!SpeechRecognizer.isRecognitionAvailable(context)) {
        Toast.makeText(context, "Speech recognition is not available on this device", Toast.LENGTH_SHORT).show()
        setListening(false)
        return
    }

    try {
        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        setRecognizer(recognizer)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        val baseQuery = currentQuery.trim()

        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                setListening(true)
            }

            override fun onBeginningOfSpeech() {
                setListening(true)
            }

            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                setListening(false)
            }

            override fun onError(error: Int) {
                setListening(false)
                val msg = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Listening timed out"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Audio permission missing"
                    else -> null
                }
                if (msg != null && error != SpeechRecognizer.ERROR_NO_MATCH && error != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResults(results: Bundle?) {
                setListening(false)
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull()?.trim().orEmpty()
                if (text.isNotBlank()) {
                    val fullText = if (baseQuery.isEmpty()) text else "$baseQuery $text"
                    onQueryChange(fullText)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partialMatches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val partialText = partialMatches?.firstOrNull()?.trim().orEmpty()
                if (partialText.isNotBlank()) {
                    val liveText = if (baseQuery.isEmpty()) partialText else "$baseQuery $partialText"
                    onQueryChange(liveText)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        recognizer.startListening(intent)
        setListening(true)
    } catch (e: Exception) {
        setListening(false)
        Toast.makeText(context, "Voice recognition error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
