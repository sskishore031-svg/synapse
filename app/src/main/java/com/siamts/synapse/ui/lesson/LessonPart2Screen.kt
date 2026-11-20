package com.siamts.synapse.ui.lesson

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.isSystemInDarkTheme
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
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@Composable
fun LessonPart2Screen(
    viewModel: LessonPart2ViewModel,
    onBackClick: () -> Unit,
    onAskAiClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    modifier: Modifier = Modifier,
    onNotesClick: (() -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LessonPart2Content(
        uiState = uiState,
        onBookmarkClick = { viewModel.toggleBookmark() },
        onSubTabSelect = { tabIndex ->
            viewModel.onSubTabSelected(tabIndex)
            if (tabIndex == 1) { // Notes Tab
                onNotesClick?.invoke()
            } else if (tabIndex == 2) { // Quiz Tab
                viewModel.markCompleted { onNextClick() }
            }
        },
        onNextClick = {
            viewModel.markCompleted {
                onNextClick()
            }
        },
        onPreviousClick = {
            viewModel.onPreviousClicked()
            onPreviousClick()
        },
        onAskAiClick = onAskAiClick,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun LessonPart2Content(
    uiState: LessonPart2UiState,
    onSubTabSelect: (Int) -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onAskAiClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onBookmarkClick: () -> Unit = {},
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
            // Top Dark Header Section
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
                            text = uiState.subjectTag,
                            color = AmberGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.lessonTitle,
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (uiState.isBookmarked) (if (isDark) AmberGold.copy(alpha = 0.25f) else Color(0xFFFEF3C7)) else (if (isDark) SurfacePanel else Color(0xFF161F42)),
                                CircleShape
                            )
                            .clickable { onBookmarkClick() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (uiState.isBookmarked) "⭐" else "🔖",
                            fontSize = 14.sp,
                        )
                    }
                }
            }

            // Sheet Container
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
                        // 1. Lesson Progress Bar Box
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
                                        text = uiState.lessonInfo,
                                        color = if (isDark) TextSecondary else Color(0xFF64748B),
                                        fontSize = 12.sp,
                                    )
                                    Text(
                                        text = "${uiState.completionPercentage}% complete",
                                        color = SunsetOrange,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                LinearProgressIndicator(
                                    progress = { uiState.completionPercentage / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp),
                                    color = SunsetOrange,
                                    trackColor = if (isDark) SurfaceBorder else Color(0xFFE2E8F0),
                                )
                            }
                        }

                        // 2. Sub-navigation Segmented Control Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 4.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isDark) SurfacePanel else Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                val subTabs = listOf("Learn", "Notes", "Quiz")
                                subTabs.forEachIndexed { index, title ->
                                    val isSelected = index == uiState.selectedSubTab
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(
                                                color = if (isSelected) (if (isDark) SurfacePanelElevated else Color.White) else Color.Transparent,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { onSubTabSelect(index) }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = title,
                                            color = if (isSelected) SunsetOrange else (if (isDark) TextSecondary else Color(0xFF64748B)),
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        )
                                    }
                                }
                            }
                        }

                        // 3. Part Header & Headline Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = uiState.partTag,
                                    color = AmberGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = uiState.headline,
                                    color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = uiState.description,
                                    color = if (isDark) TextSecondary else Color(0xFF475569),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                )
                            }
                        }

                        // 4. Mathematical Step Calculation Display Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 2.dp
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color(0xFF0F172A)),
                                border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color.Transparent),
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = uiState.formulaHeader,
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = uiState.formulaCalculation,
                                        color = AmberGold,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Step Process Row: 4.00 ➔ 3.80 ➔ 3.62
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        uiState.steps.forEachIndexed { idx, step ->
                                            Column {
                                                Text(
                                                    text = step.weight,
                                                    color = if (step.isCurrent) AmberGold else TextPrimary,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                                Text(
                                                    text = step.loss,
                                                    color = if (step.isCurrent) AmberGold.copy(alpha = 0.8f) else TextSecondary,
                                                    fontSize = 11.sp,
                                                )
                                            }

                                            if (idx < uiState.steps.size - 1) {
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    text = "➔",
                                                    color = SunsetOrange,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = uiState.formulaFooter,
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                        }

                        // 5. Key Takeaway Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isDark) SurfacePanelElevated else Color(0xFFEFF6FF), RoundedCornerShape(12.dp))
                                    .border(1.dp, if (isDark) SurfaceBorder else Color(0xFFBFDBFE), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = uiState.takeawayTitle,
                                        color = SunsetOrange,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = uiState.takeawayContent,
                                        color = if (isDark) TextPrimary else Color(0xFF1E293B),
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                    )
                                }
                            }
                        }

                        // 6. Ask AI Button Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 4.dp
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .background(if (isDark) SurfacePanelElevated else Color.White, RoundedCornerShape(12.dp))
                                    .border(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .clickable { onAskAiClick() },
                                contentAlignment = Alignment.Center,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "✦", color = AmberGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Ask AI to explain",
                                        color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 7. Navigation Row: Previous & Next Buttons Box
                    DashedBorderBox(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            // Previous Button
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .background(if (isDark) SurfacePanelElevated else Color.White, RoundedCornerShape(16.dp))
                                    .border(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                                    .clickable { onPreviousClick() },
                                contentAlignment = Alignment.Center,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "←", color = if (isDark) TextPrimary else Color(0xFF0F172A), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Previous",
                                        color = if (isDark) TextPrimary else Color(0xFF0F172A),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }

                            // Next Button
                            Button(
                                onClick = onNextClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
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
                                        Text(text = "→", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Next",
                                            color = Color.White,
                                            fontSize = 14.sp,
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
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LessonPart2ScreenPreview() {
    SYNAPSETheme {
        LessonPart2Content(
            uiState = LessonPart2UiState(),
            onSubTabSelect = {},
            onNextClick = {},
            onPreviousClick = {},
            onAskAiClick = {},
            onBackClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
