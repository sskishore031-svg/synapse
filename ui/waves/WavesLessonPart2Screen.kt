package com.siamts.synapse.ui.waves

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.PurpleAccent
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox
import kotlin.math.sin

@Composable
fun WavesLessonPart2Screen(
    viewModel: WavesLessonPart2ViewModel,
    onBackClick: () -> Unit,
    onAskAiClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    modifier: Modifier = Modifier,
    onNotesClick: (() -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WavesLessonPart2Content(
        uiState = uiState,
        onSubTabSelect = { tabIndex ->
            viewModel.onSubTabSelected(tabIndex)
            if (tabIndex == 1) { // Notes Tab
                onNotesClick?.invoke()
            } else if (tabIndex == 2) { // Quiz Tab
                onNextClick()
            }
        },
        onNextClick = onNextClick,
        onPreviousClick = onPreviousClick,
        onAskAiClick = onAskAiClick,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun WavesLessonPart2Content(
    uiState: WavesLessonPart2UiState,
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
            // Top Header Box
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
                        Text(text = uiState.title, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(text = uiState.subtitleTag, color = CyanGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF161F42), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "•••", color = TextSecondary, fontSize = 14.sp)
                    }
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
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. Progress Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    LinearProgressIndicator(
                                        progress = { uiState.progressPercent / 100f },
                                        modifier = Modifier.weight(1f).height(6.dp),
                                        color = BlueAccent,
                                        trackColor = Color(0xFFE2E8F0),
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = "${uiState.progressPercent}%", color = BlueAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = uiState.progressSubtext, color = Color(0xFF64748B), fontSize = 12.sp)
                            }
                        }

                        // 2. Sub-tab Segmented Control
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 4.dp) {
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
                                            .background(if (isSelected) Color.White else Color.Transparent, RoundedCornerShape(8.dp))
                                            .clickable { onSubTabSelect(index) },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(text = tabName, color = if (isSelected) BlueAccent else Color(0xFF64748B), fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
                                    }
                                }
                            }
                        }

                        // 3. Concept Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(text = uiState.headline, color = Color(0xFF0F172A), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = uiState.conceptText, color = Color(0xFF475569), fontSize = 13.sp, lineHeight = 18.sp)
                            }
                        }

                        // 4. Sine Wave Canvas Plot Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                            ) {
                                Box(modifier = Modifier.fillMaxWidth().height(150.dp).padding(12.dp)) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val w = size.width
                                        val h = size.height
                                        val midY = h * 0.5f

                                        drawLine(Color(0xFFE2E8F0), Offset(0f, midY), Offset(w, midY), 2f)

                                        val path = Path()
                                        val waveAmplitude = h * 0.35f
                                        val frequency = 2f * Math.PI.toFloat() * 2f / w

                                        for (i in 0..200) {
                                            val x = i * (w / 200f)
                                            val y = midY - waveAmplitude * sin(x * frequency)
                                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                        }

                                        drawPath(path, BlueAccent, style = Stroke(width = 4f))
                                    }

                                    Text(text = "Wavelength (λ)", color = BlueAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.TopCenter))
                                    Text(text = "Amplitude", color = BlueAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterEnd).padding(bottom = 16.dp))
                                    Text(text = "Time →", color = Color(0xFF64748B), fontSize = 10.sp, modifier = Modifier.align(Alignment.BottomEnd))
                                }
                            }
                        }

                        // 5. Properties Breakdown
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                uiState.properties.forEachIndexed { idx, propText ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(20.dp).background(BlueAccent, CircleShape), contentAlignment = Alignment.Center) {
                                            Text(text = "${idx + 1}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = propText, color = Color(0xFF334155), fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        // 6. Example Calculation Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(24.dp).background(Color(0xFFEFF6FF), CircleShape), contentAlignment = Alignment.Center) {
                                            Text(text = "🧮", fontSize = 12.sp)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "Example", color = Color(0xFF0F172A), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = uiState.exampleCalculation, color = BlueAccent, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }

                        // 7. Key Points Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(text = "Key Points", color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                uiState.keyPoints.forEach { point ->
                                    Row(modifier = Modifier.padding(bottom = 4.dp)) {
                                        Text(text = "• ", color = BlueAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text(text = point, color = Color(0xFF475569), fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        // 8. AI Recommendation Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 2.dp) {
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "✨ ", fontSize = 12.sp)
                                        Text(text = "AI RECOMMENDED NEXT", color = CyanGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = uiState.aiRecommendation, color = TextSecondary, fontSize = 11.sp, lineHeight = 15.sp)
                                }
                            }
                        }

                        // 9. Navigation Buttons Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 4.dp) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .background(Color.White, RoundedCornerShape(12.dp))
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                        .clickable { onPreviousClick() },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(text = "← Previous", color = BlueAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = onNextClick,
                                    modifier = Modifier.weight(1f).height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    contentPadding = PaddingValues(0.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Brush.horizontalGradient(listOf(PurpleAccent, BlueAccent, CyanAccent)), RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(text = "Next Lesson →", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
fun WavesLessonPart2ScreenPreview() {
    SYNAPSETheme {
        WavesLessonPart2Content(
            uiState = WavesLessonPart2UiState(),
            onSubTabSelect = {},
            onNextClick = {},
            onPreviousClick = {},
            onAskAiClick = {},
            onBackClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
