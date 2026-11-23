package com.siamts.synapse.ui.roadmap

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siamts.synapse.ui.theme.AmberGold
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
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
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@Composable
fun RoadmapScreen(
    viewModel: RoadmapViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onContinueClick: (() -> Unit)? = null,
    onChapterClick: ((Int) -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RoadmapContent(
        uiState = uiState,
        onMilestoneClick = { milestoneId ->
            viewModel.onMilestoneClicked(milestoneId)
            val milestone = uiState.milestones.find { it.id == milestoneId }
            if (milestone != null && milestone.status != MilestoneStatus.LOCKED) {
                if (onChapterClick != null && milestone.chapterId > 0) {
                    onChapterClick(milestone.chapterId)
                } else {
                    onContinueClick?.invoke()
                }
            } else if (milestone == null) {
                onContinueClick?.invoke()
            }
        },
        onContinueClick = {
            viewModel.onContinueLessonClicked()
            if (onChapterClick != null && uiState.activeChapterId > 0) {
                onChapterClick(uiState.activeChapterId)
            } else {
                onContinueClick?.invoke()
            }
        },
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun RoadmapContent(
    uiState: RoadmapUiState,
    onMilestoneClick: (String) -> Unit,
    onContinueClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = isSystemInDarkTheme()

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
                            color = SunsetOrange,
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

                    Spacer(modifier = Modifier.size(48.dp))
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
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    // Scrollable Area
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. Subject Progress Header Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDark) SurfacePanelElevated else Color.White
                                ),
                                border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0)),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(
                                                if (isDark) SurfacePanel else Color(0xFFEFF6FF),
                                                RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(text = uiState.subjectIcon, fontSize = 22.sp)
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(
                                                text = uiState.subjectName,
                                                color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Text(
                                                text = "${uiState.progressPercentage}%",
                                                color = SunsetOrange,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        LinearProgressIndicator(
                                            progress = { uiState.progressPercentage / 100f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp),
                                            color = SunsetOrange,
                                            trackColor = if (isDark) SurfaceBorder else Color(0xFFE2E8F0),
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = uiState.milestoneSummary,
                                            color = if (isDark) TextSecondary else Color(0xFF64748B),
                                            fontSize = 11.sp,
                                        )
                                    }
                                }
                            }
                        }

                        // 2. Milestone Timeline Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                uiState.milestones.forEachIndexed { index, milestone ->
                                    val isLast = index == uiState.milestones.size - 1

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(IntrinsicSize.Min)
                                            .clickable { onMilestoneClick(milestone.id) },
                                        verticalAlignment = Alignment.Top,
                                    ) {
                                        // Left Connector Line & Icon
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.fillMaxHeight(),
                                        ) {
                                            when (milestone.status) {
                                                MilestoneStatus.COMPLETED -> {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(32.dp)
                                                            .background(
                                                                if (isDark) SurfacePanel else Color(0xFFD1FAE5),
                                                                CircleShape
                                                            ),
                                                        contentAlignment = Alignment.Center,
                                                    ) {
                                                        Text(text = "✓", color = if (isDark) AmberGold else Color(0xFF059669), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                                MilestoneStatus.ACTIVE -> {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(36.dp)
                                                            .background(SunsetOrange, CircleShape),
                                                        contentAlignment = Alignment.Center,
                                                    ) {
                                                        Text(text = "▶", color = Color.White, fontSize = 12.sp)
                                                    }
                                                }
                                                MilestoneStatus.LOCKED -> {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(32.dp)
                                                            .background(
                                                                if (isDark) SurfacePanel else Color(0xFFF1F5F9),
                                                                CircleShape
                                                            )
                                                            .border(
                                                                1.dp,
                                                                if (isDark) SurfaceBorder else Color(0xFFCBD5E1),
                                                                CircleShape
                                                            ),
                                                        contentAlignment = Alignment.Center,
                                                    ) {
                                                        Text(text = "🔒", fontSize = 12.sp)
                                                    }
                                                }
                                            }

                                            if (!isLast) {
                                                Box(
                                                    modifier = Modifier
                                                        .width(2.dp)
                                                        .weight(1f)
                                                        .background(if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        // Milestone Card Details
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(bottom = if (isLast) 0.dp else 12.dp)
                                                .background(
                                                    color = if (milestone.status == MilestoneStatus.ACTIVE) {
                                                        if (isDark) SunsetOrange.copy(alpha = 0.15f) else Color(0xFFEFF6FF)
                                                    } else {
                                                        if (isDark) SurfacePanelElevated else Color.White
                                                    },
                                                    shape = RoundedCornerShape(14.dp)
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = if (milestone.status == MilestoneStatus.ACTIVE) SunsetOrange else (if (isDark) SurfaceBorder else Color(0xFFE2E8F0)),
                                                    shape = RoundedCornerShape(14.dp)
                                                )
                                                .padding(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = milestone.title,
                                                        color = if (milestone.status == MilestoneStatus.ACTIVE) {
                                                            if (isDark) AmberGold else SunsetOrange
                                                        } else if (milestone.status == MilestoneStatus.LOCKED) {
                                                            if (isDark) TextSecondary else Color(0xFF94A3B8)
                                                        } else {
                                                            if (isDark) TextPrimary else Color(0xFF0F172A)
                                                        },
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = milestone.subtitle,
                                                        color = if (milestone.status == MilestoneStatus.ACTIVE) SunsetOrange else (if (isDark) TextSecondary else Color(0xFF64748B)),
                                                        fontSize = 11.sp,
                                                    )
                                                }

                                                if (milestone.status == MilestoneStatus.ACTIVE) {
                                                    Text(text = "›", color = SunsetOrange, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Action Button Box: Continue Button
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 4.dp
                        ) {
                            Button(
                                onClick = onContinueClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                contentPadding = PaddingValues(0.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    SunsetOrange,
                                                    SunsetOrangeEnd,
                                                    AmberGold,
                                                )
                                            ),
                                            shape = RoundedCornerShape(16.dp),
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "→",
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Continue: ${uiState.activeLessonTitle}",
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                            }
                        }

                        // 4. AI Recommended Next Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 2.dp
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDark) SurfacePanelElevated else Color(0xFF0F172A)
                                ),
                                border = BorderStroke(1.dp, if (isDark) SunsetOrange.copy(alpha = 0.5f) else Color.Transparent),
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "✨ ", fontSize = 14.sp)
                                        Text(
                                            text = "AI RECOMMENDED NEXT",
                                            color = SunsetOrange,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = uiState.aiRecommendation,
                                        color = if (isDark) TextSecondary else TextSecondary,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
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
fun RoadmapScreenPreview() {
    SYNAPSETheme {
        RoadmapContent(
            uiState = RoadmapUiState(),
            onMilestoneClick = {},
            onContinueClick = {},
            onBackClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
