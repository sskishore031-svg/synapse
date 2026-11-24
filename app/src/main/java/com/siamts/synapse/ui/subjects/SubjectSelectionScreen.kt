package com.siamts.synapse.ui.subjects

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siamts.synapse.ui.theme.AmberGold
import com.siamts.synapse.ui.theme.LocalThemeIsDark
import com.siamts.synapse.ui.theme.SunsetOrange
import com.siamts.synapse.ui.theme.SunsetOrangeEnd
import com.siamts.synapse.ui.theme.SurfaceBorder
import com.siamts.synapse.ui.theme.SurfacePanel
import com.siamts.synapse.ui.theme.SurfacePanelElevated
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@Composable
fun SubjectSelectionScreen(
    viewModel: SubjectSelectionViewModel,
    onTabSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onSubjectClick: ((String) -> Unit)? = null,
    onChapterClick: ((Int) -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SubjectSelectionContent(
        uiState = uiState,
        onSearchChange = { viewModel.onSearchQueryChanged(it) },
        onClassSelect = { viewModel.onClassSelected(it) },
        onSubjectClick = { subjectId ->
            viewModel.onSubjectClicked(subjectId)
            onSubjectClick?.invoke(subjectId)
        },
        onChapterClick = { chapterId ->
            onChapterClick?.invoke(chapterId)
        },
        onOpenCatalog = { viewModel.openCatalog() },
        onCloseCatalog = { viewModel.closeCatalog() },
        onToggleCatalogSubject = { viewModel.toggleSubjectInCatalog(it) },
        onTabSelect = { index ->
            viewModel.onTabSelected(index)
            onTabSelect(index)
        },
        onRestartSubject = { viewModel.restartSubject(it) },
        modifier = modifier,
    )
}

@Composable
fun SubjectSelectionContent(
    uiState: SubjectSelectionUiState,
    onSearchChange: (String) -> Unit,
    onClassSelect: (String) -> Unit,
    onSubjectClick: (String) -> Unit,
    onChapterClick: (Int) -> Unit,
    onOpenCatalog: () -> Unit,
    onCloseCatalog: () -> Unit,
    onToggleCatalogSubject: (String) -> Unit,
    onTabSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onRestartSubject: ((String) -> Unit)? = null,
) {
    val isSearching = uiState.searchQuery.isNotBlank()
    val isDark = LocalThemeIsDark.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isDark) listOf(
                        DarkBackgroundTop,
                        Color(0xFF0C0D12),
                        DarkBackgroundBottom,
                    ) else listOf(
                        Color(0xFFF5F6FA),
                        Color(0xFFF5F6FA)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = uiState.headerTag,
                            color = SunsetOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.title,
                            color = if (isDark) TextPrimary else Color(0xFF11131A),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(if (isDark) SurfacePanel else Color.White, CircleShape)
                            .border(1.dp, if (isDark) SurfaceBorder else Color(0xFFE4E7F0), CircleShape)
                            .clickable { onOpenCatalog() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "➕", fontSize = 16.sp)
                    }
                }
            }

            // Sheet Container (Transparent in Dark Mode)
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
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Crisp Track Selector Filter Chips (All Tracks, Python, SQL, AI & Tech, Aptitude)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.availableClasses.forEach { cls ->
                            val isSelected = uiState.selectedClass == cls
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) Brush.horizontalGradient(
                                            listOf(SunsetOrange, SunsetOrangeEnd)
                                        ) else if (isDark) Brush.horizontalGradient(
                                            listOf(SurfacePanelElevated, SurfacePanel)
                                        ) else Brush.horizontalGradient(
                                            listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0))
                                        ),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) SunsetOrange else (if (isDark) SurfaceBorder else Color.Transparent),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { onClassSelect(cls) }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = cls,
                                    color = if (isSelected) Color.White else (if (isDark) TextSecondary else Color(0xFF475569)),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    // 1. Live Search Box
                    DashedBorderBox(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = 4.dp
                    ) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = onSearchChange,
                            placeholder = {
                                Text(
                                    text = "Search tracks, topics (e.g. Python, SQL, DSA, Android)",
                                    color = if (isDark) TextSecondary else Color(0xFF5A6075),
                                    fontSize = 13.sp,
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SunsetOrange,
                                unfocusedBorderColor = if (isDark) SurfaceBorder else Color(0xFFE4E7F0),
                                focusedContainerColor = if (isDark) SurfacePanel else Color.White,
                                unfocusedContainerColor = if (isDark) SurfacePanel else Color.White,
                                focusedTextColor = if (isDark) TextPrimary else Color(0xFF11131A),
                                unfocusedTextColor = if (isDark) TextPrimary else Color(0xFF11131A),
                            ),
                            singleLine = true,
                            leadingIcon = {
                                Text(
                                    text = "🔍",
                                    fontSize = 16.sp,
                                    modifier = Modifier.clickable { onOpenCatalog() }
                                )
                            },
                            trailingIcon = {
                                if (uiState.searchQuery.isNotEmpty()) {
                                    Text(
                                        text = "✕",
                                        color = if (isDark) TextSecondary else Color(0xFF94A3B8),
                                        modifier = Modifier.clickable { onSearchChange("") }
                                    )
                                }
                            }
                        )
                    }

                    // 2. Section Header Box with "+ Add Subject" button
                    DashedBorderBox(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = 6.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(
                                    text = if (isSearching) "Search Results" else uiState.subtitle,
                                    color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = if (isSearching) "${uiState.searchResults.size} topics found" else "${uiState.filteredSubjects.size} subjects available",
                                    color = if (isDark) AmberGold else Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                )
                            }

                            Button(
                                onClick = onOpenCatalog,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDark) SurfacePanel else Color(0xFFEFF6FF)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "➕ Add Subject",
                                    color = SunsetOrange,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // 3. Search Results or Subjects Grid
                    if (isSearching) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (uiState.searchResults.isNotEmpty()) {
                                Text(
                                    text = "Matched Chapters & Topics (Tap to Learn)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )

                                uiState.searchResults.forEach { item ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                if (item.chapterId != null) {
                                                    onChapterClick(item.chapterId)
                                                } else {
                                                    onSubjectClick(item.subjectId)
                                                }
                                            },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isDark) SurfacePanelElevated else Color.White
                                        ),
                                        border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0)),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(if (isDark) SurfacePanel else item.color.copy(alpha = 0.12f), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = item.iconSymbol, fontSize = 16.sp)
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = item.chapterTitle ?: item.subjectName,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isDark) TextPrimary else Color(0xFF0F172A)
                                                )
                                                Text(
                                                    text = "${item.subjectName} · ${item.snippet}",
                                                    fontSize = 11.sp,
                                                    color = if (isDark) TextSecondary else Color(0xFF64748B),
                                                    maxLines = 1
                                                )
                                            }

                                            Text(text = "→", fontSize = 16.sp, color = SunsetOrange)
                                        }
                                    }
                                }
                            }

                            if (uiState.filteredSubjects.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Matched Subjects",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) TextSecondary else Color(0xFF64748B)
                                )

                                uiState.filteredSubjects.forEach { subject ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onSubjectClick(subject.id) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isDark) SurfacePanelElevated else Color.White
                                        ),
                                        border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = subject.iconSymbol, fontSize = 20.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = subject.title,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = if (isDark) TextPrimary else Color(0xFF0F172A)
                                                )
                                                Text(
                                                    text = "${subject.category} • ${subject.chapters.size} chapters",
                                                    fontSize = 11.sp,
                                                    color = if (isDark) TextSecondary else Color(0xFF64748B)
                                                )
                                            }
                                            Text(text = "Open →", color = SunsetOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            if (uiState.searchResults.isEmpty() && uiState.filteredSubjects.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "🔍", fontSize = 28.sp)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "No topics matched \"${uiState.searchQuery}\"",
                                            color = if (isDark) TextSecondary else Color(0xFF64748B),
                                            fontSize = 13.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Try searching in the '+ Add Subject' catalog!",
                                            color = SunsetOrange,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.clickable { onOpenCatalog() }
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Standard 2-Column Grid of Available Subjects
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        ) {
                            items(uiState.filteredSubjects) { subject ->
                                DashedBorderBox(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = 2.dp
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onSubjectClick(subject.id) },
                                        shape = RoundedCornerShape(18.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isDark) SurfacePanelElevated else Color.White
                                        ),
                                        border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0)),
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(38.dp)
                                                        .background(
                                                            if (isDark) SurfacePanel else subject.progressColor.copy(alpha = 0.12f),
                                                            CircleShape
                                                        ),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    Text(
                                                        text = subject.iconSymbol,
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = SunsetOrange,
                                                    )
                                                }

                                                Text(
                                                    text = subject.category,
                                                    color = if (isDark) TextSecondary else Color(0xFF94A3B8),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            Text(
                                                text = subject.title,
                                                color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                            )

                                            if (subject.progressPercentage >= 100) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .background(
                                                            color = Color(0xFF10B981).copy(alpha = 0.15f),
                                                            shape = RoundedCornerShape(6.dp)
                                                        )
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "✓ COMPLETED",
                                                        color = Color(0xFF059669),
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            } else if (subject.isInterested) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .background(
                                                            color = SunsetOrange.copy(alpha = 0.15f),
                                                            shape = RoundedCornerShape(6.dp)
                                                        )
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "★ Interested",
                                                        color = SunsetOrange,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(12.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Text(
                                                    text = "${subject.completedChaptersCount} / ${subject.totalChaptersCount} Chapters Completed",
                                                    color = if (isDark) TextSecondary else Color(0xFF94A3B8),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    text = "${subject.progressPercentage}%",
                                                    color = SunsetOrange,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            LinearProgressIndicator(
                                                progress = { subject.progressPercentage / 100f },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(6.dp),
                                                color = SunsetOrange,
                                                trackColor = if (isDark) SurfaceBorder else Color(0xFFE2E8F0),
                                            )

                                            if (subject.progressPercentage >= 100) {
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    // Review Again button
                                                    Box(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp))
                                                            .clickable { onSubjectClick(subject.id) }
                                                            .padding(vertical = 7.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = "Review Again",
                                                            color = BlueAccent,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }

                                                    // Reset Track button
                                                    Box(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                                                            .clickable { onRestartSubject?.invoke(subject.id) }
                                                            .padding(vertical = 7.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = "Reset Track",
                                                            color = Color(0xFF64748B),
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold
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

                    // 4. Footer Text Box
                    DashedBorderBox(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = 6.dp
                    ) {
                        Text(
                            text = uiState.footerText,
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }


        }
    }

    // Extended Catalog Dialog (+10 Subjects)
    if (uiState.isCatalogSheetOpen) {
        var catalogSearchQuery by remember { mutableStateOf("") }
        var pendingSubjectToToggle by remember { mutableStateOf<Pair<com.siamts.synapse.data.model.CatalogSubject, Boolean>?>(null) }
        val filteredCatalog = remember(catalogSearchQuery, uiState.availableCatalog) {
            if (catalogSearchQuery.isBlank()) {
                uiState.availableCatalog
            } else {
                val q = catalogSearchQuery.trim().lowercase(java.util.Locale.ROOT)
                uiState.availableCatalog.filter { sub ->
                    sub.name.lowercase(java.util.Locale.ROOT).contains(q) ||
                            sub.category.lowercase(java.util.Locale.ROOT).contains(q) ||
                            sub.chapters.any { ch ->
                                ch.title.lowercase(java.util.Locale.ROOT).contains(q) ||
                                        ch.summary.lowercase(java.util.Locale.ROOT).contains(q)
                            }
                }
            }
        }

        AlertDialog(
            onDismissRequest = onCloseCatalog,
            containerColor = if (isDark) SurfacePanelElevated else Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Column {
                    Text(
                        text = "📚 Placement & App Tracks",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) TextPrimary else Color(0xFF11131A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Add comprehensive placement and engineering tracks to your curriculum.",
                        fontSize = 11.sp,
                        color = if (isDark) TextSecondary else Color(0xFF5A6075)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = catalogSearchQuery,
                        onValueChange = { catalogSearchQuery = it },
                        placeholder = {
                            Text(
                                text = "Filter tracks (e.g. DSA, Java, Android, OS)",
                                color = if (isDark) TextSecondary else Color(0xFF5A6075),
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SunsetOrange,
                            unfocusedBorderColor = if (isDark) SurfaceBorder else Color(0xFFE4E7F0),
                            focusedContainerColor = if (isDark) SurfacePanel else Color(0xFFEEF1F7),
                            unfocusedContainerColor = if (isDark) SurfacePanel else Color(0xFFEEF1F7),
                            focusedTextColor = if (isDark) TextPrimary else Color(0xFF11131A),
                            unfocusedTextColor = if (isDark) TextPrimary else Color(0xFF11131A),
                        ),
                        singleLine = true,
                        leadingIcon = { Text(text = "🔍", fontSize = 14.sp) }
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredCatalog) { catalogSub ->
                            val isAdded = catalogSub.isAdded
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isAdded) {
                                        if (isDark) Color(0xFF064E3B).copy(alpha = 0.4f) else Color(0xFFF0FDF4)
                                    } else {
                                        if (isDark) SurfacePanel else Color.White
                                    }
                                ),
                                border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE4E7F0)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(if (isDark) SurfacePanelElevated else Color(0xFFEEF1F7), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = catalogSub.iconSymbol, fontSize = 18.sp)
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = catalogSub.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isDark) TextPrimary else Color(0xFF11131A)
                                        )
                                        Text(
                                            text = "${catalogSub.category} • ${catalogSub.chapters.size} chapters",
                                            fontSize = 11.sp,
                                            color = if (isDark) TextSecondary else Color(0xFF5A6075)
                                        )
                                    }

                                    Button(
                                        onClick = { pendingSubjectToToggle = Pair(catalogSub, !isAdded) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isAdded) Color(0xFF059669) else SunsetOrange
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (isAdded) "✓ Added" else "+ Add",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onCloseCatalog,
                    colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Done", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        )

        pendingSubjectToToggle?.let { (subject, isAdding) ->
            AlertDialog(
                onDismissRequest = { pendingSubjectToToggle = null },
                containerColor = if (isDark) SurfacePanelElevated else Color.White,
                shape = RoundedCornerShape(20.dp),
                title = {
                    Text(
                        text = if (isAdding) "➕ Add Subject" else "Remove Subject",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) TextPrimary else Color(0xFF11131A)
                    )
                },
                text = {
                    Text(
                        text = if (isAdding) {
                            "Add ${subject.name} to your Learning Library?\nIncludes ${subject.chapters.size} micro-lessons & ${subject.chapters.size * 5} MCQs."
                        } else {
                            "Remove ${subject.name} from your Learning Library?"
                        },
                        fontSize = 14.sp,
                        color = if (isDark) TextSecondary else Color(0xFF5A6075)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onToggleCatalogSubject(subject.id)
                            pendingSubjectToToggle = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAdding) SunsetOrange else Color(0xFFEF4444)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (isAdding) "Add Subject" else "Remove",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { pendingSubjectToToggle = null }) {
                        Text(
                            text = "Cancel",
                            color = if (isDark) TextSecondary else Color(0xFF5A6075)
                        )
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SubjectSelectionScreenPreview() {
    SYNAPSETheme {
        SubjectSelectionContent(
            uiState = SubjectSelectionUiState(),
            onSearchChange = {},
            onSubjectClick = {},
            onChapterClick = {},
            onOpenCatalog = {},
            onCloseCatalog = {},
            onToggleCatalogSubject = {},
            onTabSelect = {},
            onClassSelect = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
