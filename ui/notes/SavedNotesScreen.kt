package com.siamts.synapse.ui.notes

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
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.SYNAPSETheme
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
) {
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

            // Light Sheet Container
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
                                        color = Color(0xFF94A3B8),
                                        fontSize = 14.sp,
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BlueAccent,
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A),
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
                                        color = Color(0xFF0F172A),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "${uiState.savedNotes.size} notes",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp,
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    uiState.savedNotes.forEach { note ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(14.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                                        color = Color(0xFF0F172A),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = note.subtitle,
                                                        color = Color(0xFF64748B),
                                                        fontSize = 11.sp,
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = note.statusText,
                                                        color = BlueAccent,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Medium,
                                                    )
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .background(Color.White, RoundedCornerShape(12.dp))
                                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                                        .clickable {
                                                            if (note.buttonAction == "Open") {
                                                                onOpenNote(note.id)
                                                            } else {
                                                                onDownloadNote(note.id)
                                                            }
                                                        }
                                                        .padding(horizontal = 14.dp, vertical = 6.dp),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    Text(
                                                        text = note.buttonAction,
                                                        color = BlueAccent,
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
                                        color = Color(0xFF0F172A),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "${uiState.downloadedFiles.size} files",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp,
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    uiState.downloadedFiles.forEach { file ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(14.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                                        color = Color(0xFF0F172A),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = file.subtitle,
                                                        color = Color(0xFF64748B),
                                                        fontSize = 11.sp,
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))

                                                    Box(
                                                        modifier = Modifier
                                                            .background(Color(0xFFD1FAE5), RoundedCornerShape(8.dp))
                                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = "✓ ${file.offlineBadge}",
                                                            color = Color(0xFF059669),
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                        )
                                                    }
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .background(Color.White, RoundedCornerShape(12.dp))
                                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                                        .clickable { onOpenNote(file.id) }
                                                        .padding(horizontal = 14.dp, vertical = 6.dp),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    Text(
                                                        text = file.buttonAction,
                                                        color = BlueAccent,
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

                        // 4. Disclaimer Footer Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 6.dp
                        ) {
                            Text(
                                text = uiState.disclaimer,
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }

            // Bottom Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val navItems = listOf(
                    Triple("Home", "🏠", 0),
                    Triple("Learn", "📖", 1),
                    Triple("Quiz", "❓", 2),
                    Triple("Notes", "📝", 3),
                    Triple("Profile", "👤", 4)
                )

                navItems.forEach { (label, icon, index) ->
                    val isSelected = index == uiState.selectedTab
                    Column(
                        modifier = Modifier
                            .clickable { onTabSelect(index) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = icon,
                            fontSize = 18.sp,
                            color = if (isSelected) BlueAccent else Color(0xFF94A3B8)
                        )
                        Text(
                            text = label,
                            color = if (isSelected) BlueAccent else Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
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
