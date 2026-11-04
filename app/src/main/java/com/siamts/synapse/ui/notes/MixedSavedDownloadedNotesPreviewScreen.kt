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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.siamts.synapse.data.model.NoteItemDto
import com.siamts.synapse.ui.notes.components.ThreeDNoteViewerDialog
import com.siamts.synapse.ui.notes.components.threeDTouchEffect
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

data class MixedNoteItem(
    val id: String,
    val title: String,
    val subjectTag: String,
    val subjectIcon: String,
    val dateText: String,
    val description: String,
    val isDownloaded: Boolean,
    val fileSizeText: String,
)

@Composable
fun MixedSavedDownloadedNotesPreviewScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onReadNoteClick: ((String) -> Unit)? = null,
    onGenerateNewNotesClick: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) } // 0: Saved Notes, 1: Downloaded Notes
    var searchQuery by remember { mutableStateOf("") }
    var selected3DNote by remember { mutableStateOf<NoteItemDto?>(null) }

    val allNotes = listOf(
        MixedNoteItem(
            id = "1",
            title = "Quadratic Equations & Factoring Rules",
            subjectTag = "Mathematics",
            subjectIcon = "🧮",
            dateText = "14 Oct 2026",
            description = "Factoring methods, quadratic formula derivation, and root properties.",
            isDownloaded = false,
            fileSizeText = "1.2 MB"
        ),
        MixedNoteItem(
            id = "2",
            title = "Binary Search & Big-O Complexity",
            subjectTag = "Computer Science",
            subjectIcon = "</>",
            dateText = "12 Oct 2026",
            description = "Divide-and-conquer algorithm analysis and O(log n) runtime proof.",
            isDownloaded = true,
            fileSizeText = "2.1 MB"
        ),
        MixedNoteItem(
            id = "3",
            title = "Data Visualization & Chart Guide",
            subjectTag = "Data Science",
            subjectIcon = "📊",
            dateText = "10 Oct 2026",
            description = "Comparing Bar Charts, Line Graphs, and Scatter Plots with Seaborn.",
            isDownloaded = false,
            fileSizeText = "2.4 MB"
        ),
        MixedNoteItem(
            id = "4",
            title = "Gradient Descent & Backpropagation",
            subjectTag = "Artificial Intelligence",
            subjectIcon = "🧠",
            dateText = "08 Oct 2026",
            description = "Weight update rule, learning rates, and partial derivative calculations.",
            isDownloaded = true,
            fileSizeText = "3.0 MB"
        ),
        MixedNoteItem(
            id = "5",
            title = "Wave Speed Equation & Harmonic Motion",
            subjectTag = "Physics",
            subjectIcon = "⚛",
            dateText = "05 Oct 2026",
            description = "v = f × λ calculations, transverse vs longitudinal wave properties.",
            isDownloaded = false,
            fileSizeText = "1.8 MB"
        ),
        MixedNoteItem(
            id = "6",
            title = "Ionic vs Covalent Bonding & Lewis Dots",
            subjectTag = "Chemistry",
            subjectIcon = "🧪",
            dateText = "02 Oct 2026",
            description = "Electron transfer in NaCl vs electron pair sharing in H₂O molecules.",
            isDownloaded = true,
            fileSizeText = "1.5 MB"
        )
    )

    val displayedNotes = if (selectedTab == 1) {
        allNotes.filter { it.isDownloaded }
    } else {
        allNotes
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
            // Top Dark Header Box
            DashedBorderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentPadding = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBackClick) {
                        Text(text = "←", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "ALL SUBJECTS · KNOWLEDGE REPOSITORY", color = CyanGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Saved & Downloaded Notes", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.size(36.dp))
                }
            }

            // Light Sheet Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    // Scrollable Notes Area
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. Tab Segmented Control Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 4.dp) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                // Saved Notes Tab
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .background(if (selectedTab == 0) BlueAccent else Color.Transparent, RoundedCornerShape(8.dp))
                                        .clickable { selectedTab = 0 },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "Saved Notes (${allNotes.size})",
                                        color = if (selectedTab == 0) Color.White else Color(0xFF64748B),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Downloaded Notes Tab
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .background(if (selectedTab == 1) BlueAccent else Color.Transparent, RoundedCornerShape(8.dp))
                                        .clickable { selectedTab = 1 },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "Downloaded PDFs (${allNotes.count { it.isDownloaded }})",
                                        color = if (selectedTab == 1) Color.White else Color(0xFF64748B),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // 2. Search Field Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 4.dp) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text(text = "🔍 Search all subject notes...", color = Color(0xFF94A3B8), fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = BlueAccent,
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                )
                            )
                        }

                        // 3. Mixed Notes List Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = if (selectedTab == 1) "Offline Downloaded PDFs" else "All Subject Smart Notes",
                                    color = Color(0xFF0F172A),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                displayedNotes.forEach { note ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .threeDTouchEffect(maxTiltDegrees = 7f, pressedScale = 0.97f)
                                            .clickable {
                                                selected3DNote = NoteItemDto(
                                                    id = note.id.toIntOrNull() ?: note.title.hashCode(),
                                                    title = note.title,
                                                    content = "${note.description}\n\nHigh-Yield Study Note & Syllabus Summary:\n• Core Subject: ${note.subjectTag}\n• Key takeaways, formulas, worked derivations, and diagrams.\n• High-yield questions and exam revision takeaways.",
                                                    category = if (note.isDownloaded) "DOWNLOADED" else "SAVED",
                                                    subjectName = note.subjectTag,
                                                    subjectIcon = note.subjectIcon,
                                                    isSaved = true,
                                                    isDownloaded = note.isDownloaded
                                                )
                                            },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp))
                                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                                    ) {
                                                        Text(
                                                            text = "${note.subjectIcon} ${note.subjectTag}",
                                                            color = BlueAccent,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }

                                                    Box(
                                                        modifier = Modifier
                                                            .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(text = "✨ 3D", fontSize = 10.sp, color = BlueAccent, fontWeight = FontWeight.Bold)
                                                    }
                                                }

                                                Text(text = note.dateText, color = Color(0xFF94A3B8), fontSize = 10.sp)
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))

                                            Text(
                                                text = note.title,
                                                color = Color(0xFF0F172A),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Text(
                                                text = note.description,
                                                color = Color(0xFF475569),
                                                fontSize = 11.sp,
                                                lineHeight = 15.sp
                                            )

                                            Spacer(modifier = Modifier.height(10.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    // Share with Friend
                                                    Box(
                                                        modifier = Modifier
                                                            .background(Color(0xFFE0F2FE), RoundedCornerShape(8.dp))
                                                            .clickable {
                                                                val noteContent = "${note.description}\n\nHigh-Yield Study Note & Syllabus Summary:\n• Core Subject: ${note.subjectTag}\n• Key takeaways, formulas, worked derivations, and diagrams.\n• High-yield questions and exam revision takeaways."
                                                                com.siamts.synapse.util.ShareUtils.shareNote(
                                                                    context = context,
                                                                    title = note.title,
                                                                    content = noteContent,
                                                                    subject = note.subjectTag,
                                                                    category = if (note.isDownloaded) "Downloaded Notes" else "Saved Notes"
                                                                )
                                                            }
                                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                                    ) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Text(text = "📤", fontSize = 10.sp)
                                                            Spacer(modifier = Modifier.width(3.dp))
                                                            Text(text = "Share", color = Color(0xFF0284C7), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }

                                                    // Read in 3D
                                                    Box(
                                                        modifier = Modifier
                                                            .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                                                            .clickable {
                                                                selected3DNote = NoteItemDto(
                                                                    id = note.id.toIntOrNull() ?: note.title.hashCode(),
                                                                    title = note.title,
                                                                    content = "${note.description}\n\nHigh-Yield Study Note & Syllabus Summary:\n• Core Subject: ${note.subjectTag}\n• Key takeaways, formulas, worked derivations, and diagrams.\n• High-yield questions and exam revision takeaways.",
                                                                    category = if (note.isDownloaded) "DOWNLOADED" else "SAVED",
                                                                    subjectName = note.subjectTag,
                                                                    subjectIcon = note.subjectIcon,
                                                                    isSaved = true,
                                                                    isDownloaded = note.isDownloaded
                                                                )
                                                            }
                                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                                    ) {
                                                        Text(text = "📖 Read in 3D", color = BlueAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }

                                                if (note.isDownloaded) {
                                                    Box(
                                                        modifier = Modifier
                                                            .background(Color(0xFFD1FAE5), RoundedCornerShape(8.dp))
                                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                                    ) {
                                                        Text(text = "✅ Offline Available (${note.fileSizeText})", color = Color(0xFF059669), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                } else {
                                                    Box(
                                                        modifier = Modifier
                                                            .background(Color(0xFFF3E8FF), RoundedCornerShape(8.dp))
                                                            .clickable { }
                                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                                    ) {
                                                        Text(text = "📥 Download PDF (${note.fileSizeText})", color = Color(0xFF7C3AED), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 4. Summary Note Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 6.dp) {
                            Text(
                                text = "✨ 6 Subject Notes Saved · 3 Available Offline",
                                color = BlueAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
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
                onDismiss = { selected3DNote = null }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MixedSavedDownloadedNotesPreviewScreenPreview() {
    SYNAPSETheme {
        MixedSavedDownloadedNotesPreviewScreen(onBackClick = {}, modifier = Modifier.fillMaxSize())
    }
}
