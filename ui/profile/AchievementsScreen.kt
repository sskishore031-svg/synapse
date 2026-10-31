package com.siamts.synapse.ui.profile

import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@Composable
fun AchievementsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AchievementsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                        Text(text = "Achievements", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BlueAccent)
                    }
                } else {
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
                            // Badges Count Banner
                            DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(modifier = Modifier.size(40.dp).background(Color(0xFFFEF3C7), CircleShape), contentAlignment = Alignment.Center) {
                                        Text(text = "🏆", fontSize = 20.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = uiState.totalBadgesText, color = Color(0xFF0F172A), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Your milestones in Synapse AI", color = Color(0xFF64748B), fontSize = 11.sp)
                                    }
                                }
                            }

                            // Badges Grid List Box
                            DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(text = "Your Badges", color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)

                                    val badges = uiState.badges

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        for (i in badges.indices step 2) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                for (j in i..i + 1) {
                                                    if (j < badges.size) {
                                                        val badge = badges[j]
                                                        Card(
                                                            modifier = Modifier.weight(1f),
                                                            shape = RoundedCornerShape(14.dp),
                                                            colors = CardDefaults.cardColors(containerColor = Color.White),
                                                        ) {
                                                            Column(
                                                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                            ) {
                                                                Box(modifier = Modifier.size(36.dp).background(Color(0xFFEFF6FF), CircleShape), contentAlignment = Alignment.Center) {
                                                                    Text(text = badge.icon.ifBlank { "🏆" }, fontSize = 16.sp)
                                                                }
                                                                Spacer(modifier = Modifier.height(6.dp))
                                                                Text(text = badge.title, color = Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                                                Spacer(modifier = Modifier.height(2.dp))
                                                                Text(text = badge.description, color = Color(0xFF64748B), fontSize = 10.sp, textAlign = TextAlign.Center)
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
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AchievementsScreenPreview() {
    SYNAPSETheme {
        AchievementsScreen(onBackClick = {}, modifier = Modifier.fillMaxSize())
    }
}
