package com.siamts.synapse.ui.quiz

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
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.PurpleAccent
import com.siamts.synapse.ui.theme.SYNAPSETheme
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
                        Text(text = "QUIZ PREPARATION", color = CyanGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
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
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Box(
                                        modifier = Modifier.size(60.dp).background(Color(0xFFEFF6FF), CircleShape),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(text = "🏆", fontSize = 28.sp)
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(text = "Chapter Knowledge Test", color = Color(0xFF0F172A), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Evaluate your understanding of key concepts", color = Color(0xFF64748B), fontSize = 12.sp, textAlign = TextAlign.Center)

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Stats Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = "⏱ Time", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                            Text(text = "10 Mins", color = BlueAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFE2E8F0)))

                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = "❓ Questions", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                            Text(text = "10 Qs", color = BlueAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFE2E8F0)))

                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = "🏆 Bonus", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                            Text(text = "+120 XP", color = PurpleAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Instructions Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                                Text(text = "Quiz Guidelines", color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))

                                val guidelines = listOf(
                                    "1. Read each question carefully before choosing your answer.",
                                    "2. You can select and change your chosen option anytime before proceeding.",
                                    "3. Complete all questions within the time limit to earn full bonus XP."
                                )

                                guidelines.forEach { rule ->
                                    Text(
                                        text = rule,
                                        color = Color(0xFF475569),
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
                                text = "✨ FRIDAY AI · Tailored to your learning speed and mastery",
                                color = BlueAccent,
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
                                    .background(Color.White, RoundedCornerShape(14.dp))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                                    .clickable { onBackClick() },
                                contentAlignment = Alignment.Center,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "←", color = BlueAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Back",
                                        color = BlueAccent,
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
                    val isSelected = index == 2
                    Column(
                        modifier = Modifier
                            .clickable { onTabSelect?.invoke(index) }
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(text = icon, fontSize = 18.sp)
                        Text(
                            text = label,
                            color = if (isSelected) BlueAccent else Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        )
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
