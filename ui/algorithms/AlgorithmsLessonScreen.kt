package com.siamts.synapse.ui.algorithms

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.PurpleAccent
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@Composable
fun AlgorithmsLessonScreen(
    viewModel: AlgorithmsLessonViewModel,
    onBackClick: () -> Unit,
    onAskAiClick: () -> Unit,
    modifier: Modifier = Modifier,
    onNotesClick: (() -> Unit)? = null,
    onQuizClick: (() -> Unit)? = null,
    onNextClick: (() -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AlgorithmsLessonContent(
        uiState = uiState,
        onSubTabSelect = { tabIndex ->
            viewModel.onSubTabSelected(tabIndex)
            if (tabIndex == 1) { // Notes Tab
                onNotesClick?.invoke()
            } else if (tabIndex == 2) { // Quiz Tab
                onQuizClick?.invoke()
            }
        },
        onNextClick = {
            viewModel.onNextClicked()
            onNextClick?.invoke()
        },
        onPreviousClick = { viewModel.onPreviousClicked() },
        onAskAiClick = onAskAiClick,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun AlgorithmsLessonContent(
    uiState: AlgorithmsLessonUiState,
    onSubTabSelect: (Int) -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onAskAiClick: () -> Unit,
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

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF161F42), CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "•••",
                            color = TextSecondary,
                            fontSize = 14.sp,
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
                    // Scrollable Content
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. Progress Bar Box
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
                                    LinearProgressIndicator(
                                        progress = { uiState.progressPercent / 100f },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(6.dp),
                                        color = BlueAccent,
                                        trackColor = Color(0xFFE2E8F0),
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "${uiState.progressPercent}%",
                                        color = BlueAccent,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = uiState.progressSubtext,
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp,
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
                                    .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                val tabs = listOf("Learn", "Notes", "Quiz")
                                tabs.forEachIndexed { index, tabName ->
                                    val isSelected = index == uiState.selectedSubTab
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(36.dp)
                                            .background(
                                                color = if (isSelected) Color.White else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { onSubTabSelect(index) },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = tabName,
                                            color = if (isSelected) BlueAccent else Color(0xFF64748B),
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        )
                                    }
                                }
                            }
                        }

                        // 3. Lesson Concept Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = uiState.headline,
                                    color = Color(0xFF0F172A),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = uiState.conceptText,
                                    color = Color(0xFF475569),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                )
                            }
                        }

                        // 4. Algorithm Flowchart Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Start
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFE9D5FF), RoundedCornerShape(20.dp))
                                            .padding(horizontal = 24.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(text = "Start", color = PurpleAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Text(text = "↓", color = Color(0xFF94A3B8), fontSize = 14.sp)

                                    // Read input
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp))
                                            .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 20.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(text = "Read input", color = BlueAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }

                                    Text(text = "↓", color = Color(0xFF94A3B8), fontSize = 14.sp)

                                    // Process data
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp))
                                            .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 20.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(text = "Process data", color = BlueAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }

                                    Text(text = "↓", color = Color(0xFF94A3B8), fontSize = 14.sp)

                                    // Condition? Branch Row
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                    ) {
                                        // Left Branch: No -> Do something else
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = "No", color = Color(0xFF64748B), fontSize = 10.sp)
                                            Text(text = "←", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFFEFF6FF), RoundedCornerShape(6.dp))
                                                    .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(text = "Do something\nelse", color = BlueAccent, fontSize = 10.sp, textAlign = TextAlign.Center)
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Center Diamond Decision
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFFDBEAFE), RoundedCornerShape(12.dp))
                                                .border(1.dp, BlueAccent, RoundedCornerShape(12.dp))
                                                .padding(horizontal = 14.dp, vertical = 10.dp),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(text = "Condition?", color = BlueAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Right Branch: Yes -> Do something
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = "Yes", color = Color(0xFF64748B), fontSize = 10.sp)
                                            Text(text = "→", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFFEFF6FF), RoundedCornerShape(6.dp))
                                                    .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(text = "Do something", color = BlueAccent, fontSize = 10.sp)
                                            }
                                        }
                                    }

                                    Text(text = "↓", color = Color(0xFF94A3B8), fontSize = 14.sp)

                                    // End
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFE9D5FF), RoundedCornerShape(20.dp))
                                            .padding(horizontal = 24.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(text = "End", color = PurpleAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // 5. Step-by-Step Example Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = uiState.exampleHeader,
                                    color = Color(0xFF0F172A),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                uiState.steps.forEachIndexed { idx, stepText ->
                                    Row(
                                        modifier = Modifier.padding(bottom = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .background(BlueAccent, CircleShape),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                text = "${idx + 1}",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Text(
                                            text = stepText,
                                            color = Color(0xFF475569),
                                            fontSize = 12.sp,
                                        )
                                    }
                                }
                            }
                        }

                        // 6. Quick Example Card Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .background(Color(0xFFEFF6FF), CircleShape),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(text = "🧮", fontSize = 12.sp)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = uiState.exampleTitle,
                                            color = Color(0xFF0F172A),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = uiState.exampleTarget,
                                        color = Color(0xFF1E293B),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                    )
                                    Text(
                                        text = uiState.exampleArray,
                                        color = BlueAccent,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }

                        // 7. Ask AI Action Button Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 4.dp
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .background(Color(0xFFF3E8FF), RoundedCornerShape(12.dp))
                                    .clickable { onAskAiClick() },
                                contentAlignment = Alignment.Center,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "✨", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Ask AI to explain",
                                        color = PurpleAccent,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }

                        // 8. Navigation Buttons Box (Previous & Next)
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
                                        .height(46.dp)
                                        .background(Color.White, RoundedCornerShape(14.dp))
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                                        .clickable { onPreviousClick() },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "←", color = BlueAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Previous",
                                            color = BlueAccent,
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
                                        .height(46.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    contentPadding = PaddingValues(0.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    colors = listOf(
                                                        PurpleAccent,
                                                        BlueAccent,
                                                        CyanAccent,
                                                    )
                                                ),
                                                shape = RoundedCornerShape(14.dp),
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "Next",
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = "→", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AlgorithmsLessonScreenPreview() {
    SYNAPSETheme {
        AlgorithmsLessonContent(
            uiState = AlgorithmsLessonUiState(),
            onSubTabSelect = {},
            onNextClick = {},
            onPreviousClick = {},
            onAskAiClick = {},
            onBackClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
