package com.siamts.synapse.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siamts.synapse.data.model.NoteItemDto
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@Composable
fun NotesHomeScreen(
    viewModel: NotesHomeViewModel,
    onBackClick: () -> Unit,
    onOpenNotesList: (type: String, subjectId: Int?, title: String) -> Unit,
    modifier: Modifier = Modifier,
    initialFilter: String = "ALL"
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("COMMON") } // "COMMON", "SUBJECT", "SAVED"
    var selectedSubjectId by remember { mutableStateOf(1) } // 1: Math, 2: CS, 3: DS, 4: AI, 5: Physics, 6: Chemistry

    LaunchedEffect(initialFilter) {
        if (initialFilter.isNotBlank() && initialFilter.uppercase() != "ALL") {
            viewModel.setFilter(initialFilter)
        }
    }

    val predefinedSubjects = listOf(
        Pair(1, "Mathematics"),
        Pair(2, "Computer Science"),
        Pair(3, "Data Science"),
        Pair(4, "Artificial Intelligence"),
        Pair(5, "Physics"),
        Pair(6, "Chemistry")
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkBackgroundTop,
                        Color(0xFF090D22),
                        DarkBackgroundBottom
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Box
            DashedBorderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentPadding = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Text(
                            text = "←",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NOTES & STUDY GUIDES",
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Notes Organizer",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = { showCreateDialog = true }) {
                        Text(text = "➕", fontSize = 18.sp)
                    }
                }
            }

            // Light Body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
            ) {
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BlueAccent)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 1. Stats Overview Bar
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    StatItem("Subject", uiState.summary.subjectNotesCount.toString(), "📚")
                                    StatItem("Common", uiState.summary.commonNotesCount.toString(), "📝")
                                    StatItem("Saved", uiState.summary.savedNotesCount.toString(), "🔖")
                                    StatItem("Offline", uiState.summary.downloadedNotesCount.toString(), "💾")
                                }
                            }
                        }

                        // 2. Filter Tabs Row (All, Subject, Common, Saved, Downloaded)
                        item {
                            val filterOptions = listOf(
                                Triple("ALL", "All", "📋"),
                                Triple("SUBJECT", "Subject", "📚"),
                                Triple("COMMON", "Common", "📝"),
                                Triple("SAVED", "Saved", "🔖"),
                                Triple("DOWNLOADED", "Downloaded", "💾")
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                filterOptions.forEach { (filterKey, label, icon) ->
                                    val isSelected = uiState.activeFilter == filterKey
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = if (isSelected) BlueAccent else Color.White,
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) BlueAccent else Color(0xFFE2E8F0),
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                            .clickable { viewModel.setFilter(filterKey) }
                                            .padding(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = icon, fontSize = 13.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = label,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color.White else Color(0xFF1E293B)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Render either Overview Cards (ALL) or Filtered Notes List
                        if (uiState.activeFilter == "ALL") {
                            // Option A: Subject Notes
                            item {
                                NoteCategoryCard(
                                    title = "Option A: Subject Notes",
                                    subtitle = "Notes organized strictly by academic subjects and chapters.",
                                    icon = "📚",
                                    countBadge = "${uiState.summary.subjectNotesCount} Notes",
                                    iconBgColor = Color(0xFFEFF6FF),
                                    onClick = { onOpenNotesList("subject", null, "Subject Notes") }
                                )
                            }

                            // Option B: Common Notes
                            item {
                                NoteCategoryCard(
                                    title = "Option B: Common Notes",
                                    subtitle = "General notes, study strategies, and shared concepts across topics.",
                                    icon = "📝",
                                    countBadge = "${uiState.summary.commonNotesCount} Notes",
                                    iconBgColor = Color(0xFFF5F3FF),
                                    onClick = { onOpenNotesList("common", null, "Common Notes") }
                                )
                            }

                            // Option C: Saved Notes
                            item {
                                NoteCategoryCard(
                                    title = "Option C: Saved Notes",
                                    subtitle = "Your bookmarked and starred notes for rapid review.",
                                    icon = "🔖",
                                    countBadge = "${uiState.summary.savedNotesCount} Saved",
                                    iconBgColor = Color(0xFFFEF3C7),
                                    onClick = { onOpenNotesList("saved", null, "Saved Notes") }
                                )
                            }

                            // Option D: Downloaded Notes
                            item {
                                NoteCategoryCard(
                                    title = "Option D: Downloaded Notes",
                                    subtitle = "Offline-cached notes available anytime without internet.",
                                    icon = "💾",
                                    countBadge = "${uiState.summary.downloadedNotesCount} Offline",
                                    iconBgColor = Color(0xFFD1FAE5),
                                    onClick = { onOpenNotesList("downloaded", null, "Downloaded Notes") }
                                )
                            }

                            // Subject Direct Quick Access
                            item {
                                Text(
                                    text = "Browse Notes by Subject",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                )
                            }

                            items(uiState.subjects) { sub ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onOpenNotesList("subject", sub.id, "${sub.name} Notes") },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = sub.iconSymbol, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = sub.name,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF1E293B),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = "${sub.notesCount} notes →",
                                            fontSize = 12.sp,
                                            color = BlueAccent,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        } else {
                            // Filtered List View (Subject, Common, Saved, or Downloaded Notes)
                            if (uiState.isFilteredLoading) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = BlueAccent)
                                    }
                                }
                            } else if (uiState.filteredNotes.isEmpty()) {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(32.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = if (uiState.activeFilter == "DOWNLOADED") "💾" else "📝",
                                                fontSize = 40.sp
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = if (uiState.activeFilter == "DOWNLOADED")
                                                    "No downloaded notes found yet"
                                                else "No ${uiState.activeFilter.lowercase()} notes found",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = Color(0xFF0F172A)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Tap the '➕' button above to create and save new notes.",
                                                fontSize = 13.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }
                                }
                            } else {
                                items(uiState.filteredNotes) { note ->
                                    NoteRowItemCard(
                                        note = note,
                                        onToggleSave = { viewModel.toggleSave(note.id) },
                                        onToggleDownload = { viewModel.toggleDownload(note.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Create Note Dialog with Full Categorization
        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text(text = "Create New Note", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = noteTitle,
                            onValueChange = { noteTitle = it },
                            label = { Text("Note Title") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Category Selector: Common, Subject Notes, Saved Notes
                        Text(
                            text = "Select Category:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                Pair("COMMON", "Common"),
                                Pair("SUBJECT", "Subject"),
                                Pair("SAVED", "Saved")
                            ).forEach { (catKey, label) ->
                                val isCatSelected = selectedCategory == catKey
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            color = if (isCatSelected) BlueAccent else Color(0xFFF1F5F9),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { selectedCategory = catKey }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isCatSelected) Color.White else Color(0xFF1E293B)
                                    )
                                }
                            }
                        }

                        // If Subject Notes is selected, show Subject Picker
                        if (selectedCategory == "SUBJECT") {
                            Text(
                                text = "Select Subject:",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                predefinedSubjects.forEach { (subId, subName) ->
                                    val isSubSelected = selectedSubjectId == subId
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = if (isSubSelected) CyanAccent else Color(0xFFF1F5F9),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { selectedSubjectId = subId }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = subName,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSubSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSubSelected) Color(0xFF0F172A) else Color(0xFF334155)
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = noteContent,
                            onValueChange = { noteContent = it },
                            label = { Text("Content / Notes") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            maxLines = 6
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (noteTitle.isNotBlank()) {
                                viewModel.createQuickNote(
                                    title = noteTitle,
                                    content = noteContent,
                                    category = selectedCategory,
                                    subjectId = if (selectedCategory == "SUBJECT") selectedSubjectId else null
                                )
                                noteTitle = ""
                                noteContent = ""
                                showCreateDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BlueAccent)
                    ) {
                        Text("Save Note")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun StatItem(label: String, count: String, icon: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = count, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
        Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B))
    }
}

@Composable
private fun NoteCategoryCard(
    title: String,
    subtitle: String,
    icon: String,
    countBadge: String,
    iconBgColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(iconBgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = countBadge,
                    color = BlueAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun NoteRowItemCard(
    note: NoteItemDto,
    onToggleSave: () -> Unit,
    onToggleDownload: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = note.subjectIcon, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = note.subjectName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0284C7)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Save icon toggle
                    Text(
                        text = if (note.isSaved) "🔖" else "🏷️",
                        fontSize = 15.sp,
                        modifier = Modifier.clickable { onToggleSave() }
                    )
                    // Download icon toggle
                    Text(
                        text = if (note.isDownloaded) "💾 Offline" else "⬇️ Download",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (note.isDownloaded) Color(0xFF16A34A) else BlueAccent,
                        modifier = Modifier.clickable { onToggleDownload() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF0F172A)
            )

            if (note.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.content,
                    fontSize = 13.sp,
                    color = Color(0xFF475569),
                    maxLines = 3,
                    lineHeight = 17.sp
                )
            }
        }
    }
}
