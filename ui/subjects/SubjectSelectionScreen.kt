package com.siamts.synapse.ui.subjects

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@Composable
fun SubjectSelectionScreen(
    viewModel: SubjectSelectionViewModel,
    onTabSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onSubjectClick: ((String) -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SubjectSelectionContent(
        uiState = uiState,
        onSearchChange = { viewModel.onSearchQueryChanged(it) },
        onSubjectClick = { subjectId ->
            viewModel.onSubjectClicked(subjectId)
            onSubjectClick?.invoke(subjectId)
        },
        onTabSelect = { index ->
            viewModel.onTabSelected(index)
            onTabSelect(index)
        },
        modifier = modifier,
    )
}

@Composable
fun SubjectSelectionContent(
    uiState: SubjectSelectionUiState,
    onSearchChange: (String) -> Unit,
    onSubjectClick: (String) -> Unit,
    onTabSelect: (Int) -> Unit,
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
            // Top Dark Header
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
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF161F42), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "🔍", fontSize = 16.sp)
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
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. Search Box
                    DashedBorderBox(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = 4.dp
                    ) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = onSearchChange,
                            placeholder = {
                                Text(
                                    text = "Search subjects or topics",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 14.sp,
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BlueAccent,
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedTextColor = Color(0xFF0F172A),
                                unfocusedTextColor = Color(0xFF0F172A),
                            ),
                            singleLine = true,
                            leadingIcon = {
                                Text(text = "🔍", fontSize = 16.sp)
                            }
                        )
                    }

                    // 2. Section Header Box
                    DashedBorderBox(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = 8.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = uiState.subtitle,
                                color = Color(0xFF0F172A),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "${uiState.subjects.size} subjects",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                            )
                        }
                    }

                    // 3. 2-Column Grid of 6 Subject Cards
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    ) {
                        items(uiState.subjects) { subject ->
                            DashedBorderBox(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = 2.dp
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSubjectClick(subject.id) },
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .background(subject.progressColor.copy(alpha = 0.12f), CircleShape),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                text = subject.iconSymbol,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = subject.progressColor,
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = subject.title,
                                            color = Color(0xFF0F172A),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(
                                                text = "Your progress",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 11.sp,
                                            )
                                            Text(
                                                text = "${subject.progressPercentage}%",
                                                color = subject.progressColor,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        LinearProgressIndicator(
                                            progress = { subject.progressPercentage / 100f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(5.dp),
                                            color = subject.progressColor,
                                            trackColor = Color(0xFFF1F5F9),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 4. Footer Text Box
                    DashedBorderBox(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = 6.dp
                    ) {
                        Text(
                            text = uiState.footerText,
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
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
                    val isSelected = index == uiState.selectedTab
                    Column(
                        modifier = Modifier
                            .clickable { onTabSelect(index) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = icon,
                            fontSize = 18.sp,
                            color = if (isSelected) BlueAccent else Color(0xFF94A3B8)
                        )
                        Text(
                            text = label,
                            color = if (isSelected) BlueAccent else Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SubjectSelectionScreenPreview() {
    SYNAPSETheme {
        SubjectSelectionContent(
            uiState = SubjectSelectionUiState(),
            onSearchChange = {},
            onSubjectClick = {},
            onTabSelect = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
