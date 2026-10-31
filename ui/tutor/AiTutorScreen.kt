package com.siamts.synapse.ui.tutor

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.PurpleAccent
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
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

    // Smoothly scroll down whenever new messages arrive
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(listState.layoutInfo.totalItemsCount.coerceAtLeast(1) - 1)
        }
    }

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
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.headerTag,
                            color = CyanGlow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.tutorTitle,
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    // Clear Chat / New Chat action button in TopAppBar
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF161F42), RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFF2A3660), RoundedCornerShape(16.dp))
                            .clickable { onClearChat() }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✨", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "New",
                                color = CyanGlow,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            // Light Rounded Sheet Container
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
                                                text = "Online · Multi-Turn Conversation Memory",
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
                                                color = Color.White,
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                                            .padding(14.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = message.text,
                                                color = Color(0xFF1E293B),
                                                fontSize = 13.sp,
                                                lineHeight = 18.sp,
                                            )
                                            if (!message.paragraph2.isNullOrBlank()) {
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Text(
                                                    text = message.paragraph2,
                                                    color = Color(0xFF1E293B),
                                                    fontSize = 13.sp,
                                                    lineHeight = 18.sp,
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Synapse AI · ${message.timeAgo}",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 11.sp,
                                            )
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
                                Text(
                                    text = uiState.aiThoughtStatus,
                                    color = Color(0xFF0284C7),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                )
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
                                                .clickable { onPromptClick(prompt) }
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
                                                        color = BlueAccent,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                    )
                                                }
                                                Text(
                                                    text = "↗",
                                                    color = BlueAccent,
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

                    // Chat Input Bar Box
                    DashedBorderBox(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = 6.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            OutlinedTextField(
                                value = uiState.inputQuery,
                                onValueChange = onQueryChange,
                                placeholder = {
                                    Text(
                                        text = "Ask a question or follow up...",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 14.sp,
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(20.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BlueAccent,
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A),
                                ),
                                singleLine = true,
                                trailingIcon = {
                                    Text(
                                        text = "🎙️",
                                        fontSize = 18.sp,
                                        modifier = Modifier.clickable { }
                                    )
                                }
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Send Button Circle
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                PurpleAccent,
                                                BlueAccent,
                                                CyanAccent,
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .clickable { onSendMessage() },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "↑",
                                    color = Color.White,
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
