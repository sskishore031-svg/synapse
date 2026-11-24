package com.siamts.synapse.ui.notes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siamts.synapse.ui.theme.AmberGold
import com.siamts.synapse.ui.theme.AmberGoldEnd
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.LocalThemeIsDark
import com.siamts.synapse.ui.theme.PurpleAccent
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.SunsetOrange
import com.siamts.synapse.ui.theme.SunsetOrangeEnd
import com.siamts.synapse.ui.theme.SurfaceBorder
import com.siamts.synapse.ui.theme.SurfacePanel
import com.siamts.synapse.ui.theme.SurfacePanelElevated
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NotesGeneratorScreen(
    viewModel: NotesGeneratorViewModel,
    onTabSelect: (Int) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenSavedNotes: (() -> Unit)? = null,
    onDownloadNotes: (() -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var isSavedTemporarily by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        NotesGeneratorContent(
            uiState = uiState,
            onTopicChange = { viewModel.onTopicInputChanged(it) },
            onGenerateNotes = { viewModel.onGenerateNotesClicked() },
            onSaveNotes = {
                viewModel.onSaveClicked()
                isSavedTemporarily = true
                try {
                    com.siamts.synapse.notification.SynapseNotificationHelper.showNoteSavedNotification(
                        context = context,
                        noteTitle = uiState.topicInput.ifBlank { "Smart Study Notes" }
                    )
                } catch (_: Throwable) {}
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "✓ Notes saved to My Notes!",
                        duration = SnackbarDuration.Short
                    )
                }
                coroutineScope.launch {
                    delay(2000)
                    isSavedTemporarily = false
                }
            },
            onDownloadNotes = {
                onDownloadNotes?.invoke()
            },
            onTabSelect = { index ->
                viewModel.onTabSelected(index)
                onTabSelect(index)
            },
            onOpenSavedNotes = { onOpenSavedNotes?.invoke() },
            onBackClick = onBackClick,
            modifier = Modifier.fillMaxSize(),
            isSavedTemporarily = isSavedTemporarily
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 70.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NotesGeneratorContent(
    uiState: NotesGeneratorUiState,
    onTopicChange: (String) -> Unit,
    onGenerateNotes: () -> Unit,
    onSaveNotes: () -> Unit,
    onTabSelect: (Int) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenSavedNotes: (() -> Unit)? = null,
    onDownloadNotes: (() -> Unit)? = null,
    isSavedTemporarily: Boolean = false,
) {
    val context = LocalContext.current
    val isDark = LocalThemeIsDark.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isDark) listOf(
                        DarkBackgroundTop,
                        Color(0xFF090D22),
                        DarkBackgroundBottom,
                    ) else listOf(
                        Color(0xFFF5F6FA),
                        Color(0xFFF5F6FA)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Dark Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = uiState.headerTag,
                            color = AmberGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.title,
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF161F42), CircleShape)
                            .clickable { onOpenSavedNotes?.invoke() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "🕒", fontSize = 16.sp)
                    }
                }
            }

            // Dark Content Body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = if (isDark) Color.Transparent else Color(0xFFF8FAFC),
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
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. Enter Topic Input Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Enter topic or subject",
                                    color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    OutlinedTextField(
                                        value = uiState.topicInput,
                                        onValueChange = onTopicChange,
                                        placeholder = {
                                            Text(
                                                text = "e.g. Python, Photosynthesis, Calculus",
                                                color = if (isDark) TextSecondary else Color(0xFF94A3B8),
                                                fontSize = 11.sp
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = SunsetOrange,
                                            unfocusedBorderColor = if (isDark) SurfaceBorder else Color(0xFFE2E8F0),
                                            focusedContainerColor = if (isDark) SurfacePanelElevated else Color.White,
                                            unfocusedContainerColor = if (isDark) SurfacePanel else Color.White,
                                            focusedTextColor = if (isDark) TextPrimary else Color(0xFF0F172A),
                                            unfocusedTextColor = if (isDark) TextPrimary else Color(0xFF0F172A),
                                        ),
                                        singleLine = true,
                                    )

                                    Button(
                                        onClick = onGenerateNotes,
                                        enabled = !uiState.isGenerating,
                                        modifier = Modifier.height(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                        contentPadding = PaddingValues(0.dp),
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    brush = Brush.horizontalGradient(
                                                        colors = listOf(SunsetOrange, SunsetOrangeEnd, AmberGold)
                                                    ),
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .padding(horizontal = 12.dp),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            if (uiState.isGenerating) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(18.dp),
                                                    color = Color.White,
                                                    strokeWidth = 2.dp
                                                )
                                            } else {
                                                Text(
                                                    text = "Generate Notes",
                                                    color = Color.White,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Upload Document Card Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                                border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(if (isDark) SunsetOrange.copy(alpha = 0.2f) else Color(0xFFEFF6FF), CircleShape),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(text = "☁️", fontSize = 16.sp)
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = "Upload document",
                                                color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Text(
                                                text = uiState.documentText,
                                                color = if (isDark) TextSecondary else Color(0xFF94A3B8),
                                                fontSize = 11.sp,
                                            )
                                        }
                                    }

                                    Text(
                                        text = "+",
                                        color = SunsetOrange,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }

                        // 3. Your Study Notes Card Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                                border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = "Your study notes",
                                            color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                        )

                                        Box(
                                            modifier = Modifier
                                                .background(if (isDark) SunsetOrange.copy(alpha = 0.2f) else Color(0xFFEFF6FF), RoundedCornerShape(10.dp))
                                                .border(1.dp, if (isDark) SunsetOrange.copy(alpha = 0.4f) else Color.Transparent, RoundedCornerShape(10.dp))
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "✨ AI-generated",
                                                color = if (isDark) SunsetOrange else BlueAccent,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = uiState.noteTitle,
                                        color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = uiState.noteSummary,
                                        color = if (isDark) TextSecondary else Color(0xFF475569),
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "Key points",
                                        color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    uiState.keyPoints.forEach { point ->
                                        Text(
                                            text = "• $point",
                                            color = if (isDark) TextSecondary else Color(0xFF475569),
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(bottom = 2.dp),
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "IMPORTANT TERMS",
                                        color = if (isDark) TextSecondary else Color(0xFF64748B),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        uiState.importantTerms.forEach { term ->
                                            Box(
                                                modifier = Modifier
                                                    .background(if (isDark) SurfacePanel else Color(0xFFEFF6FF), RoundedCornerShape(8.dp))
                                                    .border(1.dp, if (isDark) SurfaceBorder else Color.Transparent, RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = term,
                                                    color = if (isDark) AmberGold else BlueAccent,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 4. Download & Save Action Row Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 4.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                // Download Button
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .background(if (isDark) SurfacePanelElevated else Color.White, RoundedCornerShape(12.dp))
                                        .border(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                        .clickable { onDownloadNotes?.invoke() },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "📥", fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Download",
                                            color = if (isDark) SunsetOrange else BlueAccent,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }

                                // Share Button
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .background(if (isDark) SurfacePanelElevated else Color(0xFFE0F2FE), RoundedCornerShape(12.dp))
                                        .border(1.dp, if (isDark) SurfaceBorder else Color(0xFFBAE6FD), RoundedCornerShape(12.dp))
                                        .clickable {
                                            val content = buildString {
                                                append(uiState.noteSummary).append("\n\n")
                                                append("Key Points:\n")
                                                uiState.keyPoints.forEach { append("• ").append(it).append("\n") }
                                                append("\nImportant Terms:\n")
                                                uiState.importantTerms.forEach { append("• ").append(it).append("\n") }
                                            }
                                            com.siamts.synapse.util.ShareUtils.shareNote(
                                                context = context,
                                                title = uiState.noteTitle.ifBlank { "AI Study Notes" },
                                                content = content,
                                                subject = "AI Generated Notes",
                                                category = "Study Guide"
                                            )
                                        },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "📤", fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Share",
                                            color = if (isDark) AmberGold else Color(0xFF0284C7),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }

                                // Save Button
                                Button(
                                    onClick = onSaveNotes,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    contentPadding = PaddingValues(0.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    colors = listOf(SunsetOrange, SunsetOrangeEnd, AmberGold)
                                                ),
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = if (isSavedTemporarily) "✓" else "🔖", fontSize = 14.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isSavedTemporarily) "✓ Saved" else "Save",
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 5. Save Status Banner Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 4.dp
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isDark) Color(0xFF059669).copy(alpha = 0.25f) else Color(0xFFD1FAE5), RoundedCornerShape(12.dp))
                                    .border(1.dp, if (isDark) Color(0xFF059669).copy(alpha = 0.5f) else Color.Transparent, RoundedCornerShape(12.dp))
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "✓ ${uiState.saveStatusText}",
                                    color = if (isDark) Color(0xFF34D399) else Color(0xFF059669),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        // 6. Back Button Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 4.dp
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(40.dp)
                                    .background(if (isDark) SurfacePanelElevated else Color.White, RoundedCornerShape(12.dp))
                                    .border(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .clickable { onBackClick() },
                                contentAlignment = Alignment.Center,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "←", color = if (isDark) SunsetOrange else BlueAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Back",
                                        color = if (isDark) SunsetOrange else BlueAccent,
                                        fontSize = 13.sp,
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotesGeneratorScreenPreview() {
    SYNAPSETheme {
        NotesGeneratorContent(
            uiState = NotesGeneratorUiState(),
            onTopicChange = {},
            onGenerateNotes = {},
            onSaveNotes = {},
            onTabSelect = {},
            onBackClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
