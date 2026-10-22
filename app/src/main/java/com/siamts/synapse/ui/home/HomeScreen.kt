package com.siamts.synapse.ui.home

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        uiState = uiState,
        onSearchChange = { query -> viewModel.onSearchQueryChanged(query) },
        onTabSelect = { index -> viewModel.onTabSelected(index) },
        onActionClick = { id -> viewModel.onActionClicked(id) },
        onSessionClick = { id -> viewModel.onSessionClicked(id) },
        modifier = modifier,
    )
}

@Composable
fun HomeContent(
    uiState: HomeUiState,
    onSearchChange: (String) -> Unit,
    onTabSelect: (Int) -> Unit,
    onActionClick: (String) -> Unit,
    onSessionClick: (String) -> Unit,
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
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = uiState.greeting,
                            color = TextSecondary,
                            fontSize = 13.sp,
                        )
                        Text(
                            text = "${uiState.userName} 👋",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    DashedBorderBox(contentPadding = 8.dp) {
                        Text(
                            text = "⚡ Mind Power: ${uiState.mindPower}%",
                            color = CyanGlow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Input
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text(
                            text = "Ask Synapse AI or search topics...",
                            color = TextMuted,
                            fontSize = 14.sp,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanGlow,
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedContainerColor = Color(0xFF0D1430),
                        unfocusedContainerColor = Color(0xFF0B0F24),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                    ),
                    singleLine = true,
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                // If Profile Tab selected (index 3), show Student Profile Card with Continue button
                if (uiState.selectedTab == 3) {
                    item {
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 16.dp,
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .padding(end = 12.dp)
                                            .background(Color(0xFFE0F2FE), CircleShape)
                                            .border(2.dp, Color(0xFFBAE6FD), CircleShape)
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = "AM",
                                            color = Color(0xFF0284C7),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = uiState.userName,
                                            color = TextPrimary,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = "B.Tech Computer Science · Year 3",
                                            color = TextSecondary,
                                            fontSize = 13.sp,
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Interests: Computer Science, AI, Math",
                                    color = CyanAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Dashed Continue Button Box
                                DashedBorderBox(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = 4.dp,
                                ) {
                                    Button(
                                        onClick = { },
                                        modifier = Modifier
                                            .fillMaxWidth()
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
                                                            PurpleAccent,
                                                            BlueAccent,
                                                            CyanAccent,
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
                                                    text = "Continue",
                                                    color = Color.White,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Quick AI Modules
                    item {
                        Column {
                            Text(
                                text = "AI Learning Modules",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                items(uiState.quickActions) { action ->
                                    Card(
                                        modifier = Modifier
                                            .width(140.dp)
                                            .clickable { onActionClick(action.id) },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (action.isHighlighted) Color(0xFF161F42) else Color(0xFF0D1430)
                                        ),
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                        ) {
                                            Text(
                                                text = action.iconEmoji,
                                                fontSize = 28.sp,
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = action.title,
                                                color = TextPrimary,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Text(
                                                text = action.subtitle,
                                                color = TextSecondary,
                                                fontSize = 12.sp,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Recent Sessions
                    item {
                        Column {
                            Text(
                                text = "Recent Sessions",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    items(uiState.recentSessions) { session ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSessionClick(session.id) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF0D1430)
                            ),
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = session.title,
                                        color = TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = session.timeAgo,
                                        color = TextMuted,
                                        fontSize = 12.sp,
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = session.category,
                                    color = CyanAccent,
                                    fontSize = 12.sp,
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                LinearProgressIndicator(
                                    progress = { session.progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp),
                                    color = CyanGlow,
                                    trackColor = Color(0xFF1E293B),
                                )

                                // Add Continue button to first active session card
                                if (session.id == "1") {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    DashedBorderBox(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentPadding = 4.dp,
                                    ) {
                                        Button(
                                            onClick = { onSessionClick(session.id) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(44.dp),
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
                                                        text = "→",
                                                        color = Color.White,
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold,
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "Continue",
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

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Bottom Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF070B1B))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val tabs = listOf("Home 🏠", "Learn 📚", "AI Chat 🤖", "Profile 👤")
                tabs.forEachIndexed { index, tabName ->
                    val isSelected = index == uiState.selectedTab
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) Color(0xFF161F42) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onTabSelect(index) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = tabName,
                            color = if (isSelected) CyanGlow else TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    SYNAPSETheme {
        HomeContent(
            uiState = HomeUiState(),
            onSearchChange = {},
            onTabSelect = {},
            onActionClick = {},
            onSessionClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudentProfileScreenPreview() {
    SYNAPSETheme {
        HomeContent(
            uiState = HomeUiState(selectedTab = 3),
            onSearchChange = {},
            onTabSelect = {},
            onActionClick = {},
            onSessionClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
