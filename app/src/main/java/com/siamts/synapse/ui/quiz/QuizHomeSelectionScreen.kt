package com.siamts.synapse.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.LazyRow
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.LightBackground
import com.siamts.synapse.ui.theme.LightBorder
import com.siamts.synapse.ui.theme.LightTextPrimary
import com.siamts.synapse.ui.theme.LightTextSecondary
import com.siamts.synapse.ui.theme.LocalThemeIsDark
import com.siamts.synapse.ui.theme.SunsetOrange
import com.siamts.synapse.ui.theme.SunsetOrangeEnd
import com.siamts.synapse.ui.theme.AmberGold
import com.siamts.synapse.ui.theme.AmberGoldEnd
import com.siamts.synapse.ui.theme.SurfacePanel
import com.siamts.synapse.ui.theme.SurfacePanelElevated
import com.siamts.synapse.ui.theme.SurfaceBorder
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@Composable
fun QuizHomeSelectionScreen(
    viewModel: QuizHomeViewModel,
    onBackClick: () -> Unit,
    onSelectSubject: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
    onOpenMistakeVault: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = LocalThemeIsDark.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isDark) listOf(
                        DarkBackgroundTop,
                        Color(0xFF0C0D12),
                        DarkBackgroundBottom
                    ) else listOf(
                        Color(0xFFF5F6FA),
                        Color(0xFFF5F6FA)
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
                            color = if (isDark) TextPrimary else LightTextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "QUIZ ASSESSMENT HUB",
                            color = if (isDark) CyanAccent else SunsetOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Select a Subject",
                            color = if (isDark) TextPrimary else LightTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(modifier = Modifier.size(40.dp))
                }
            }

            // Content Container (Design 4 Seamless Dark Theme)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(color = Color.Transparent)
            ) {
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SunsetOrange)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            // High-Demand Track Filter Chips in LazyRow (resolves Aptitude chip clipping)
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(uiState.availableClasses) { cls ->
                                    val isSelected = uiState.selectedClass == cls
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (isSelected) Brush.horizontalGradient(
                                                    listOf(SunsetOrange, SunsetOrangeEnd)
                                                ) else if (isDark) Brush.linearGradient(
                                                    listOf(SurfacePanelElevated, SurfacePanelElevated)
                                                ) else Brush.linearGradient(
                                                    listOf(Color.White, Color.White)
                                                ),
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) SunsetOrange else (if (isDark) SurfaceBorder else LightBorder),
                                                RoundedCornerShape(20.dp)
                                            )
                                            .clickable { viewModel.onClassSelected(cls) }
                                            .padding(horizontal = 14.dp, vertical = 7.dp)
                                    ) {
                                        Text(
                                            text = cls,
                                            color = if (isSelected) Color.White else (if (isDark) TextSecondary else LightTextSecondary),
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // 1. 4 High-demand tracker cards show immediately at top
                        val displayedSubjects = if (uiState.filteredSubjects.isNotEmpty()) uiState.filteredSubjects else uiState.subjects
                        items(displayedSubjects) { subject ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectSubject(subject.id, subject.name) },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanel else Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) SurfaceBorder else LightBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .background(
                                                if (isDark) SurfacePanelElevated else Color(0xFFF1F4F9),
                                                CircleShape
                                            )
                                            .border(1.dp, if (isDark) SurfaceBorder else LightBorder, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = subject.iconSymbol, fontSize = 24.sp)
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = subject.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = if (isDark) TextPrimary else LightTextPrimary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${subject.availableQuizzes} Micro-Lessons · 25 MCQs",
                                            fontSize = 12.sp,
                                            color = if (isDark) TextSecondary else LightTextSecondary
                                        )
                                    }

                                    if (subject.attemptedQuizzes > 0) {
                                        Box(
                                            modifier = Modifier
                                                .background(AmberGold.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                                .border(1.dp, AmberGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "Avg ${subject.averageScore}%",
                                                color = AmberGold,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .background(SunsetOrange.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                                .border(1.dp, SunsetOrange.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "Start →",
                                                color = SunsetOrange,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Mistake Vault (0 to Master) moved to bottom
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = uiState.mistakeCount > 0) {
                                        onOpenMistakeVault()
                                    },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (uiState.mistakeCount > 0) SunsetOrange.copy(alpha = 0.6f) else (if (isDark) SurfaceBorder else LightBorder)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "🎯",
                                            fontSize = 24.sp
                                        )
                                        Column {
                                            Text(
                                                text = if (uiState.mistakeCount > 0) "Mistake Vault (${uiState.mistakeCount} to Master)" else "Mistake Vault (0 to Master)",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = if (uiState.mistakeCount > 0) SunsetOrange else (if (isDark) TextPrimary else LightTextPrimary)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = if (uiState.mistakeCount > 0) "Tap to practice your incorrect questions and master them!" else "Zero missed questions! Great job.",
                                                fontSize = 12.sp,
                                                color = if (isDark) TextSecondary else LightTextSecondary
                                            )
                                        }
                                    }
                                    if (uiState.mistakeCount > 0) {
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    Brush.horizontalGradient(listOf(SunsetOrange, SunsetOrangeEnd)),
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "Re-Test",
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Compact 'Subject-Wise Quizzes' banner (without extra space/vertical gap)
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanel else Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) SurfaceBorder else LightBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "Subject-Wise Quizzes",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = if (isDark) TextPrimary else LightTextPrimary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Placement interview practice & revision tests",
                                            fontSize = 11.sp,
                                            color = if (isDark) TextSecondary else LightTextSecondary
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(SunsetOrange, RoundedCornerShape(12.dp))
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = "${uiState.totalAttempted}/${uiState.totalQuizzes} done",
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
            }
        }
    }
}

