package com.siamts.synapse.ui.brainmap

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.welcome.components.BrainGraphic
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@Composable
fun BrainMapScreen(
    viewModel: BrainMapViewModel,
    onBackClick: () -> Unit,
    onTabSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BrainMapContent(
        uiState = uiState,
        onNodeClick = { viewModel.onNodeClicked(it) },
        onTabSelect = { index ->
            viewModel.onTabSelected(index)
            onTabSelect(index)
        },
        onBackClick = onBackClick,
        onRetryClick = { viewModel.retry() },
        modifier = modifier,
    )
}

@Composable
fun BrainMapContent(
    uiState: BrainMapUiState,
    onNodeClick: (String) -> Unit,
    onTabSelect: (Int) -> Unit,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val fallbackNodes = defaultNodes()
    val node1 = uiState.nodes.getOrNull(0) ?: fallbackNodes[0]
    val node2 = uiState.nodes.getOrNull(1) ?: fallbackNodes[1]
    val node3 = uiState.nodes.getOrNull(2) ?: fallbackNodes[2]
    val node4 = uiState.nodes.getOrNull(3) ?: fallbackNodes[3]
    val node5 = uiState.nodes.getOrNull(4) ?: fallbackNodes[4]
    val node6 = uiState.nodes.getOrNull(5) ?: fallbackNodes[5]

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
                            color = CyanGlow,
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
                            .clickable { onRetryClick() },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = CyanGlow
                            )
                        } else {
                            Text(
                                text = "🗺️",
                                fontSize = 14.sp,
                            )
                        }
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
                    // Scrollable Area
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Error Notice Banner if failure occurs
                        AnimatedVisibility(visible = uiState.errorMessage != null) {
                            DashedBorderBox(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = 8.dp
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = uiState.errorMessage ?: "",
                                        color = Color(0xFFEF4444),
                                        fontSize = 12.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Retry",
                                        color = BlueAccent,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable { onRetryClick() }
                                    )
                                }
                            }
                        }

                        // 1. Overall Knowledge Progress Box
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
                                        text = "Overall knowledge",
                                        color = Color(0xFF0F172A),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "${uiState.overallKnowledgePercentage}%",
                                        color = BlueAccent,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                LinearProgressIndicator(
                                    progress = { uiState.overallKnowledgePercentage / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp),
                                    color = BlueAccent,
                                    trackColor = Color(0xFFE2E8F0),
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = uiState.overallKnowledgeText,
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp,
                                )
                            }
                        }

                        // 2. Central Mind Diagram Card Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 2.dp
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1430)),
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    // Connecting Constellation Lines Canvas
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val w = size.width
                                        val h = size.height
                                        val center = Offset(w / 2f, h / 2f)

                                        val nodeOffsets = listOf(
                                            Offset(w * 0.22f, h * 0.22f), // Node 1 (Math)
                                            Offset(w * 0.78f, h * 0.22f), // Node 2 (CS)
                                            Offset(w * 0.22f, h * 0.50f), // Node 3 (Data Science)
                                            Offset(w * 0.78f, h * 0.50f), // Node 4 (AI)
                                            Offset(w * 0.22f, h * 0.78f), // Node 5 (Physics)
                                            Offset(w * 0.78f, h * 0.78f)  // Node 6 (Chemistry)
                                        )

                                        nodeOffsets.forEach { nodePos ->
                                            drawLine(
                                                color = Color(0xFF38BDF8).copy(alpha = 0.5f),
                                                start = center,
                                                end = nodePos,
                                                strokeWidth = 2f
                                            )
                                        }
                                    }

                                    // Center "Your mind" Circle
                                    Box(
                                        modifier = Modifier
                                            .size(76.dp)
                                            .background(Color(0xFF0F172A), CircleShape)
                                            .border(2.dp, CyanGlow, CircleShape)
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Box(modifier = Modifier.size(24.dp)) {
                                                BrainGraphic(modifier = Modifier.fillMaxSize())
                                            }
                                            Text(
                                                text = uiState.centerTitle,
                                                color = TextPrimary,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }

                                    // 6 Subject Node Cards dynamically mapped from real backend analytics
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp),
                                        verticalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        // Row 1 (Top)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            NodeBox(node1.title, node1.statusText, node1.color) { onNodeClick(node1.id) }
                                            NodeBox(node2.title, node2.statusText, node2.color) { onNodeClick(node2.id) }
                                        }

                                        // Row 2 (Middle)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            NodeBox(node3.title, node3.statusText, node3.color) { onNodeClick(node3.id) }
                                            NodeBox(node4.title, node4.statusText, node4.color) { onNodeClick(node4.id) }
                                        }

                                        // Row 3 (Bottom)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            NodeBox(node5.title, node5.statusText, node5.color) { onNodeClick(node5.id) }
                                            NodeBox(node6.title, node6.statusText, node6.color) { onNodeClick(node6.id) }
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Legend / Status Indicators Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).background(Color(0xFF38BDF8), CircleShape))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Strong", color = Color(0xFF64748B), fontSize = 11.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).background(Color(0xFF8B5CF6), CircleShape))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Learning", color = Color(0xFF64748B), fontSize = 11.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).background(Color(0xFFEF4444), CircleShape))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Weak", color = Color(0xFF64748B), fontSize = 11.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).background(Color(0xFFD97706), CircleShape))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Need Revision", color = Color(0xFF64748B), fontSize = 11.sp)
                                }
                            }
                        }

                        // 4. Recommendation Card Box
                        DashedBorderBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 8.dp
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "✨ ", fontSize = 14.sp)
                                    Text(
                                        text = uiState.recommendationTitle,
                                        color = Color(0xFF0F172A),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = uiState.recommendationText,
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                )
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

@Composable
private fun NodeBox(
    title: String,
    statusText: String,
    borderColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .width(110.dp)
            .background(Color(0xFF0F172A).copy(alpha = 0.9f), RoundedCornerShape(12.dp))
            .border(1.dp, borderColor.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = statusText,
                color = borderColor,
                fontSize = 9.sp,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BrainMapScreenPreview() {
    SYNAPSETheme {
        BrainMapContent(
            uiState = BrainMapUiState(),
            onNodeClick = {},
            onTabSelect = {},
            onBackClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
