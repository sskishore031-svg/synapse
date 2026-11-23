package com.siamts.synapse.ui.notes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.siamts.synapse.data.model.NoteItemDto
import com.siamts.synapse.ui.notes.components.ThreeDNoteViewerDialog
import com.siamts.synapse.ui.notes.components.threeDTouchEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siamts.synapse.ui.theme.AmberGold
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
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

@Composable
fun SavedNotesScreen(
    viewModel: SavedNotesViewModel,
    onBackClick: () -> Unit,
    onTabSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SavedNotesContent(
        uiState = uiState,
        onSearchChange = { viewModel.onSearchQueryChanged(it) },
        onOpenNote = { viewModel.onOpenNoteClicked(it) },
        onDownloadNote = { viewModel.onDownloadNoteClicked(it) },
        onToggleSave = { viewModel.toggleSave(it) },
        onToggleDownload = { viewModel.toggleDownload(it) },
        onTabSelect = { index ->
            viewModel.onTabSelected(index)
            onTabSelect(index)
        },
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun SavedNotesContent(
    uiState: SavedNotesUiState,
    onSearchChange: (String) -> Unit,
    onOpenNote: (String) -> Unit,
    onDownloadNote: (String) -> Unit,
    onTabSelect: (Int) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onToggleSave: (Int) -> Unit = {},
    onToggleDownload: (Int) -> Unit = {},
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    var selected3DNote by remember { mutableStateOf<NoteItemDto?>(null) }

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
            // Top Dark Header Box (DashedBorderBox)
            DashedBorderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentPadding = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = uiState.headerTag,
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.title,
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            // Body Sheet Container (Dark mode transparent to show gradient)
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
                        // 1. Search Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 4.dp
                        ) {
                            OutlinedTextField(
                                value = uiState.searchQuery,
                                onValueChange = onSearchChange,
                                placeholder = {
                                    Text(
                                        text = "Search by topic or subject",
                                        color = if (isDark) TextSecondary else Color(0xFF94A3B8),
                                        fontSize = 14.sp,
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SunsetOrange,
                                    unfocusedBorderColor = if (isDark) SurfaceBorder else Color(0xFFE2E8F0),
                                    focusedContainerColor = if (isDark) SurfacePanel else Color.White,
                                    unfocusedContainerColor = if (isDark) SurfacePanel else Color.White,
                                    focusedTextColor = if (isDark) TextPrimary else Color(0xFF0F172A),
                                    unfocusedTextColor = if (isDark) TextPrimary else Color(0xFF0F172A),
                                ),
                                singleLine = true,
                            )
                        }

                        // 2. Saved Notes Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "Saved Notes",
                                        color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "${uiState.savedNotes.size} notes",
                                        color = if (isDark) AmberGold else Color(0xFF94A3B8),
                                        fontSize = 12.sp,
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    uiState.savedNotes.forEach { note ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .threeDTouchEffect(maxTiltDegrees = 7f, pressedScale = 0.97f)
                                                .clickable {
                                                    selected3DNote = NoteItemDto(
                                                        id = note.id.toIntOrNull() ?: note.title.hashCode(),
                                                        title = note.title,
                                                        content = "${note.subtitle}\n\nKey Concepts & High-Yield Summary:\n• Core principle and definition: Comprehensive study notes for ${note.title}.\n• Detailed mathematical formulations, worked examples, and formulas.\n• High-yield takeaways and problem-solving steps.",
                                                        category = "SAVED",
                                                        subjectName = note.subtitle.split("·").firstOrNull()?.trim()?.ifBlank { "Saved Notes" } ?: "Saved Notes",
                                                        subjectIcon = "🔖",
                                                        isSaved = true,
                                                        isDownloaded = note.statusText.contains("Downloaded", ignoreCase = true)
                                                    )
                                                },
                                            shape = RoundedCornerShape(14.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isDark) SurfacePanelElevated else Color.White
                                            ),
                                            border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0)),
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = note.title,
                                                        color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = note.subtitle,
                                                        color = if (isDark) TextSecondary else Color(0xFF64748B),
                                                        fontSize = 11.sp,
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = note.statusText,
                                                        color = SunsetOrange,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Medium,
                                                    )
                                                }

                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .background(
                                                                if (isDark) SurfacePanel else Color(0xFFEFF6FF),
                                                                RoundedCornerShape(6.dp)
                                                            )
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(text = "✨ 3D", fontSize = 10.sp, color = SunsetOrange, fontWeight = FontWeight.Bold)
                                                    }

                                                    // Share with Friend Button
                                                    Box(
                                                        modifier = Modifier
                                                            .background(
                                                                if (isDark) SurfacePanel else Color(0xFFE0F2FE),
                                                                RoundedCornerShape(12.dp)
                                                            )
                                                            .border(
                                                                1.dp,
                                                                if (isDark) SurfaceBorder else Color(0xFFBAE6FD),
                                                                RoundedCornerShape(12.dp)
                                                            )
                                                            .clickable {
                                                                val noteContent = "${note.subtitle}\n\nKey Concepts & High-Yield Summary:\n• Core principle and definition: Comprehensive study notes for ${note.title}.\n• Detailed mathematical formulations, worked examples, and formulas.\n• High-yield takeaways and problem-solving steps."
                                                                val subject = note.subtitle.split("·").firstOrNull()?.trim() ?: "Saved Notes"
                                                                com.siamts.synapse.util.ShareUtils.shareNote(
                                                                    context = context,
                                                                    title = note.title,
                                                                    content = noteContent,
                                                                    subject = subject,
                                                                    category = "Saved Notes"
                                                                )
                                                            }
                                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                                        contentAlignment = Alignment.Center,
                                                    ) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Text(text = "📤", fontSize = 11.sp)
                                                            Spacer(modifier = Modifier.width(3.dp))
                                                            Text(
                                                                text = "Share",
                                                                color = SunsetOrange,
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold,
                                                            )
                                                        }
                                                    }

                                                    Box(
                                                        modifier = Modifier
                                                            .background(
                                                                if (isDark) SurfacePanel else Color.White,
                                                                RoundedCornerShape(12.dp)
                                                            )
                                                            .border(
                                                                1.dp,
                                                                if (isDark) SunsetOrange.copy(alpha = 0.6f) else Color(0xFFE2E8F0),
                                                                RoundedCornerShape(12.dp)
                                                            )
                                                            .clickable {
                                                                if (note.buttonAction == "Open") {
                                                                    selected3DNote = NoteItemDto(
                                                                        id = note.id.toIntOrNull() ?: note.title.hashCode(),
                                                                        title = note.title,
                                                                        content = "${note.subtitle}\n\nKey Concepts & High-Yield Summary:\n• Core principle and definition: Comprehensive study notes for ${note.title}.\n• Detailed mathematical formulations, worked examples, and formulas.\n• High-yield takeaways and problem-solving steps.",
                                                                        category = "SAVED",
                                                                        subjectName = note.subtitle.split("·").firstOrNull()?.trim()?.ifBlank { "Saved Notes" } ?: "Saved Notes",
                                                                        subjectIcon = "🔖",
                                                                        isSaved = true,
                                                                        isDownloaded = note.statusText.contains("Downloaded", ignoreCase = true)
                                                                    )
                                                                } else {
                                                                    onDownloadNote(note.id)
                                                                }
                                                            }
                                                            .padding(horizontal = 14.dp, vertical = 6.dp),
                                                        contentAlignment = Alignment.Center,
                                                    ) {
                                                        Text(
                                                            text = note.buttonAction,
                                                            color = SunsetOrange,
                                                            fontSize = 12.sp,
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

                        // 3. Downloaded Notes Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "Downloaded Notes",
                                        color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "${uiState.downloadedFiles.size} files",
                                        color = if (isDark) AmberGold else Color(0xFF94A3B8),
                                        fontSize = 12.sp,
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    uiState.downloadedFiles.forEach { file ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .threeDTouchEffect(maxTiltDegrees = 7f, pressedScale = 0.97f)
                                                .clickable {
                                                    selected3DNote = NoteItemDto(
                                                        id = file.id.toIntOrNull() ?: file.title.hashCode(),
                                                        title = file.title,
                                                        content = "${file.subtitle}\n\nOffline Cached Syllabus Notes:\n• Full offline curriculum study notes for ${file.title}.\n• Comprehensive worked examples, diagrams, formulas, and cheat sheet.\n• High-yield exam revision accessible completely offline.",
                                                        category = "DOWNLOADED",
                                                        subjectName = file.subtitle.split("·").firstOrNull()?.trim()?.ifBlank { "Offline Notes" } ?: "Offline Notes",
                                                        subjectIcon = "💾",
                                                        isSaved = true,
                                                        isDownloaded = true
                                                    )
                                                },
                                            shape = RoundedCornerShape(14.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isDark) SurfacePanelElevated else Color.White
                                            ),
                                            border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0)),
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = file.title,
                                                        color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = file.subtitle,
                                                        color = if (isDark) TextSecondary else Color(0xFF64748B),
                                                        fontSize = 11.sp,
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))

                                                    Box(
                                                        modifier = Modifier
                                                            .background(
                                                                if (isDark) SurfacePanel else Color(0xFFD1FAE5),
                                                                RoundedCornerShape(8.dp)
                                                            )
                                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = "✓ ${file.offlineBadge}",
                                                            color = if (isDark) AmberGold else Color(0xFF059669),
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                        )
                                                    }
                                                }

                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .background(
                                                                if (isDark) SurfacePanel else Color(0xFFEFF6FF),
                                                                RoundedCornerShape(6.dp)
                                                            )
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(text = "✨ 3D", fontSize = 10.sp, color = SunsetOrange, fontWeight = FontWeight.Bold)
                                                    }

                                                    // Share with Friend Button for Downloaded File
                                                    Box(
                                                        modifier = Modifier
                                                            .background(
                                                                if (isDark) SurfacePanel else Color(0xFFE0F2FE),
                                                                RoundedCornerShape(12.dp)
                                                            )
                                                            .border(
                                                                1.dp,
                                                                if (isDark) SurfaceBorder else Color(0xFFBAE6FD),
                                                                RoundedCornerShape(12.dp)
                                                            )
                                                            .clickable {
                                                                val noteContent = "${file.subtitle}\n\nOffline Cached Syllabus Notes:\n• Full offline curriculum study notes for ${file.title}.\n• Comprehensive worked examples, diagrams, formulas, and cheat sheet.\n• High-yield exam revision accessible completely offline."
                                                                val subject = file.subtitle.split("·").firstOrNull()?.trim() ?: "Downloaded Notes"
                                                                com.siamts.synapse.util.ShareUtils.shareNote(
                                                                    context = context,
                                                                    title = file.title,
                                                                    content = noteContent,
                                                                    subject = subject,
                                                                    category = "Downloaded Notes"
                                                                )
                                                            }
                                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                                        contentAlignment = Alignment.Center,
                                                    ) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Text(text = "📤", fontSize = 11.sp)
                                                            Spacer(modifier = Modifier.width(3.dp))
                                                            Text(
                                                                text = "Share",
                                                                color = SunsetOrange,
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold,
                                                            )
                                                        }
                                                    }

                                                    Box(
                                                        modifier = Modifier
                                                            .background(
                                                                if (isDark) SurfacePanel else Color.White,
                                                                RoundedCornerShape(12.dp)
                                                            )
                                                            .border(
                                                                1.dp,
                                                                if (isDark) SunsetOrange.copy(alpha = 0.6f) else Color(0xFFE2E8F0),
                                                                RoundedCornerShape(12.dp)
                                                            )
                                                            .clickable {
                                                                selected3DNote = NoteItemDto(
                                                                    id = file.id.toIntOrNull() ?: file.title.hashCode(),
                                                                    title = file.title,
                                                                    content = "${file.subtitle}\n\nOffline Cached Syllabus Notes:\n• Full offline curriculum study notes for ${file.title}.\n• Comprehensive worked examples, diagrams, formulas, and cheat sheet.\n• High-yield exam revision accessible completely offline.",
                                                                    category = "DOWNLOADED",
                                                                    subjectName = file.subtitle.split("·").firstOrNull()?.trim()?.ifBlank { "Offline Notes" } ?: "Offline Notes",
                                                                    subjectIcon = "💾",
                                                                    isSaved = true,
                                                                    isDownloaded = true
                                                                )
                                                            }
                                                            .padding(horizontal = 14.dp, vertical = 6.dp),
                                                        contentAlignment = Alignment.Center,
                                                    ) {
                                                        Text(
                                                            text = file.buttonAction,
                                                            color = SunsetOrange,
                                                            fontSize = 12.sp,
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

                        // 4. Disclaimer Footer Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 6.dp
                        ) {
                            Text(
                                text = uiState.disclaimer,
                                color = if (isDark) TextSecondary else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }


        }

        // 3D Perspective Note Viewer Dialog
        if (selected3DNote != null) {
            ThreeDNoteViewerDialog(
                note = selected3DNote!!,
                onDismiss = { selected3DNote = null },
                onToggleSave = {
                    onToggleSave(selected3DNote!!.id)
                },
                onToggleDownload = {
                    onToggleDownload(selected3DNote!!.id)
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SavedNotesScreenPreview() {
    SYNAPSETheme {
        SavedNotesContent(
            uiState = SavedNotesUiState(),
            onSearchChange = {},
            onOpenNote = {},
            onDownloadNote = {},
            onTabSelect = {},
            onBackClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
