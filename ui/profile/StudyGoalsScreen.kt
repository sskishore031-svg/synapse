package com.siamts.synapse.ui.profile

import androidx.compose.foundation.background
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@Composable
fun StudyGoalsScreen(
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
            // Header Box
            DashedBorderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentPadding = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBackClick) {
                        Text(text = "←", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(text = "PROFILE & SETTINGS", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Study Goals", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Daily Goal Progress Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(modifier = Modifier.size(44.dp).background(Color(0xFFEFF6FF), CircleShape), contentAlignment = Alignment.Center) {
                                        Text(text = "🎯", fontSize = 22.sp)
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(text = "Daily Goal", color = Color(0xFF0F172A), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                            Text(text = "42%", color = BlueAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Text(text = "25 / 60 minutes", color = Color(0xFF64748B), fontSize = 12.sp)

                                        Spacer(modifier = Modifier.height(8.dp))

                                        LinearProgressIndicator(
                                            progress = { 25f / 60f },
                                            modifier = Modifier.fillMaxWidth().height(6.dp),
                                            color = BlueAccent,
                                            trackColor = Color(0xFFE2E8F0),
                                        )
                                    }
                                }
                            }
                        }

                        // Goal Settings Options Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                    GoalOptionRow("Daily Study Time", "60 minutes", "⏱")
                                    GoalOptionRow("Subjects to Focus", "3 selected", "📚")
                                    GoalOptionRow("Study Reminders", "2 reminders", "🔔")
                                }
                            }
                        }

                        // Current Streak Card Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(modifier = Modifier.size(44.dp).background(Color(0xFFFFF7ED), CircleShape), contentAlignment = Alignment.Center) {
                                        Text(text = "🔥", fontSize = 22.sp)
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(text = "Current Streak", color = Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "5 days", color = Color(0xFF0F172A), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Keep going!", color = Color(0xFF94A3B8), fontSize = 11.sp)
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

@Composable
private fun GoalOptionRow(title: String, valueText: String, icon: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { }.padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = valueText, color = Color(0xFF94A3B8), fontSize = 12.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "›", color = Color(0xFF94A3B8), fontSize = 18.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudyGoalsScreenPreview() {
    SYNAPSETheme {
        StudyGoalsScreen(onBackClick = {}, modifier = Modifier.fillMaxSize())
    }
}
