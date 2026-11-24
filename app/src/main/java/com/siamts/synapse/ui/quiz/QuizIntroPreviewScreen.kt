package com.siamts.synapse.ui.quiz

import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.siamts.synapse.ui.theme.AmberGoldEnd
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.LocalThemeIsDark
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

@Composable
fun QuizIntroPreviewScreen(
    onBackClick: () -> Unit,
    onStartQuizClick: () -> Unit,
    onTabSelect: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val isDark = LocalThemeIsDark.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isDark) listOf(
                        DarkBackgroundTop,
                        Color(0xFF090D22),
                        DarkBackgroundBottom,
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
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBackClick) {
                        Text(text = "←", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "QUIZ PREPARATION", color = AmberGold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Ready for your Quiz?", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF161F42), CircleShape)
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "•••", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            }

            // Dark Content Body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(color = if (isDark) Color.Transparent else Color(0xFFF8FAFC), shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    // Scrollable Area
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 1. Quiz Header Info Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                                border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .background(if (isDark) SunsetOrange.copy(alpha = 0.2f) else Color(0xFFEFF6FF), CircleShape)
                                            .border(1.dp, if (isDark) SunsetOrange.copy(alpha = 0.4f) else Color.Transparent, CircleShape),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(text = "🏆", fontSize = 28.sp)
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(text = "Chapter Knowledge Test", color = if (isDark) TextPrimary else Color(0xFF0F172A), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Evaluate your understanding of key concepts", color = if (isDark) TextSecondary else Color(0xFF64748B), fontSize = 12.sp, textAlign = TextAlign.Center)

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Stats Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = "⏱ Time", color = if (isDark) TextSecondary else Color(0xFF94A3B8), fontSize = 10.sp)
                                            Text(text = "10 Mins", color = SunsetOrange, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(if (isDark) SurfaceBorder else Color(0xFFE2E8F0)))

                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = "❓ Questions", color = if (isDark) TextSecondary else Color(0xFF94A3B8), fontSize = 10.sp)
                                            Text(text = "10 Qs", color = SunsetOrange, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(if (isDark) SurfaceBorder else Color(0xFFE2E8F0)))

                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = "🏆 Bonus", color = if (isDark) TextSecondary else Color(0xFF94A3B8), fontSize = 10.sp)
                                            Text(text = "+120 XP", color = AmberGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Instructions Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                                Text(text = "Quiz Guidelines", color = if (isDark) TextPrimary else Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))

                                val guidelines = listOf(
                                    "1. Read each question carefully before choosing your answer.",
                                    "2. You can select and change your chosen option anytime before proceeding.",
                                    "3. Complete all questions within the time limit to earn full bonus XP."
                                )

                                guidelines.forEach { rule ->
                                    Text(
                                        text = rule,
                                        color = if (isDark) TextSecondary else Color(0xFF475569),
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }
                            }
                        }

                        // 3. AI Powered Tag Banner Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 6.dp) {
                            Text(
                                text = "✨ SYNAPSE AI · Tailored to your learning speed and mastery",
                                color = AmberGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4. Bottom Buttons Row Box: Back & Start Quiz
                    DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 4.dp) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            // Back Button
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .background(if (isDark) SurfacePanelElevated else Color.White, RoundedCornerShape(14.dp))
                                    .border(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                                    .clickable { onBackClick() },
                                contentAlignment = Alignment.Center,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "←", color = if (isDark) SunsetOrange else BlueAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Back",
                                        color = if (isDark) SunsetOrange else BlueAccent,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }

                            // Start Quiz Button
                            Button(
                                onClick = onStartQuizClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
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
                                                    SunsetOrange,
                                                    SunsetOrangeEnd,
                                                    AmberGold,
                                                )
                                            ),
                                            shape = RoundedCornerShape(14.dp),
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Start Quiz",
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuizIntroPreviewScreenPreview() {
    SYNAPSETheme {
        QuizIntroPreviewScreen(
            onBackClick = {},
            onStartQuizClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
