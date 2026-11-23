package com.siamts.synapse.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.siamts.synapse.data.model.AnalyticsData
import com.siamts.synapse.data.model.SubjectBreakdownItem
import com.siamts.synapse.data.model.TimeDistributionItem
import com.siamts.synapse.data.model.WeeklySubjectComparisonItem
import com.siamts.synapse.ui.theme.AmberGold
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.SunsetOrange
import com.siamts.synapse.ui.theme.SurfaceBorder
import com.siamts.synapse.ui.theme.SurfacePanel
import com.siamts.synapse.ui.theme.SurfacePanelElevated
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox
import java.util.Calendar

@Composable
fun ProgressAnalyticsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    initialTab: Int = 0, // 0: Overview, 1: Subject, 2: Time, 3: Streaks
    viewModel: AnalyticsViewModel = viewModel(),
    onTrackClick: ((String) -> Unit)? = null
) {
    val isDark = isSystemInDarkTheme()
    var selectedTab by remember { mutableStateOf(initialTab) }
    val uiState by viewModel.uiState.collectAsState()
    val data = uiState.data

    val pageTitle = when (selectedTab) {
        1 -> "Subject Analytics"
        2 -> "Time Analytics"
        3 -> "Streak Analytics"
        else -> "Progress & Analytics"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isDark) listOf(
                        DarkBackgroundTop,
                        Color(0xFF0C0D14),
                        DarkBackgroundBottom,
                    ) else listOf(
                        Color(0xFFF5F6FA),
                        Color(0xFFF5F6FA),
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
                        Text(
                            text = "←",
                            color = if (isDark) TextPrimary else Color(0xFF11131A),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "PROFILE & SETTINGS",
                            color = SunsetOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = pageTitle,
                            color = if (isDark) TextPrimary else Color(0xFF11131A),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Main Content Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = if (isDark) Color.Transparent else Color(0xFFF5F6FA),
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 28.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Sub-tabs: Overview, Subject, Time, Streaks
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 4.dp) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isDark) SurfacePanel else Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                val tabs = listOf("Overview", "Subject", "Time", "Streaks")
                                tabs.forEachIndexed { index, tabName ->
                                    val isSel = index == selectedTab
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(34.dp)
                                            .background(if (isSel) SunsetOrange else Color.Transparent, RoundedCornerShape(8.dp))
                                            .clickable { selectedTab = index },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = tabName,
                                            color = if (isSel) Color.White else (if (isDark) TextSecondary else Color(0xFF64748B)),
                                            fontSize = 11.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // Tab Contents
                        when (selectedTab) {
                            1 -> SubjectAnalyticsContent(
                                data = data,
                                isDark = isDark,
                                onTrackClick = onTrackClick
                            )
                            2 -> TimeAnalyticsContent(
                                data = data,
                                timeFilter = uiState.timeFilter,
                                onTimeFilterChange = { viewModel.setTimeFilter(it) },
                                isDark = isDark
                            )
                            3 -> StreakAnalyticsContent(
                                data = data,
                                selectedYear = uiState.selectedYear,
                                selectedMonth = uiState.selectedMonth,
                                monthDisplayName = uiState.monthDisplayName,
                                onPreviousMonth = { viewModel.previousMonth() },
                                onNextMonth = { viewModel.nextMonth() },
                                onSelectMonth = { viewModel.selectMonth(it) },
                                isDark = isDark
                            )
                            else -> OverviewAnalyticsContent(
                                data = data,
                                timeFilter = uiState.timeFilter,
                                onTimeFilterChange = { viewModel.setTimeFilter(it) },
                                isDark = isDark,
                                onTrackClick = onTrackClick
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Overview Analytics Tab with interactive time dropdown and real-time activity metrics.
 */
@Composable
private fun OverviewAnalyticsContent(
    data: AnalyticsData,
    timeFilter: String,
    onTimeFilterChange: (String) -> Unit,
    isDark: Boolean = false,
    onTrackClick: ((String) -> Unit)? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Study Time Card with Weekly Bar Chart & Dropdown
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Study Time", color = if (isDark) TextSecondary else Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = data.formattedStudyTime, color = if (isDark) TextPrimary else Color(0xFF0F172A), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }

                        TimeFilterDropdownPill(
                            currentFilter = timeFilter,
                            onFilterSelect = onTimeFilterChange,
                            isDark = isDark
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    DailyStudyBarChart(
                        days = data.chartDays,
                        heights = data.chartHeights,
                        minutes = data.chartMinutes,
                        isDark = isDark
                    )
                }
            }
        }

        // Subjects Breakdown for 4 Placement Tracks
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Subjects Breakdown", color = if (isDark) TextPrimary else Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        data.subjectBreakdown.forEach { item ->
                            SubjectBreakdownRow(item = item, isDark = isDark, onClick = { onTrackClick?.invoke(item.slug) })
                        }
                    }
                }
            }
        }

        // Insights Box
        InsightsCard(insightsText = data.insights, isDark = isDark)
    }
}

/**
 * Subject Analytics Tab: 4 Placement Tracks, live time cards, and weekly comparison.
 */
@Composable
private fun SubjectAnalyticsContent(
    data: AnalyticsData,
    isDark: Boolean = false,
    onTrackClick: ((String) -> Unit)? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 1. Subjects Breakdown Box
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Subjects Breakdown", color = if (isDark) TextPrimary else Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        data.subjectBreakdown.forEach { item ->
                            SubjectBreakdownRow(item = item, isDark = isDark, onClick = { onTrackClick?.invoke(item.slug) })
                        }
                    }
                }
            }
        }

        // 2. Study Time by Subject Cards Box (Live updated from Room DB / API)
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Study Time by Subject", color = if (isDark) TextPrimary else Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                val items = data.subjectBreakdown
                val item1 = items.getOrNull(0) ?: SubjectBreakdownItem("Python & Coding Logic", "PYTHON", "🐍")
                val item2 = items.getOrNull(1) ?: SubjectBreakdownItem("SQL & Database Management", "SQL", "🗄️")
                val item3 = items.getOrNull(2) ?: SubjectBreakdownItem("AI & Modern Tech Concepts", "AI_TECH", "⚡")
                val item4 = items.getOrNull(3) ?: SubjectBreakdownItem("Aptitude & Interview Prep", "APTITUDE", "🎯")

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SubjectTimeCard(item1, Modifier.weight(1f), isDark) { onTrackClick?.invoke(item1.slug) }
                    SubjectTimeCard(item2, Modifier.weight(1f), isDark) { onTrackClick?.invoke(item2.slug) }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SubjectTimeCard(item3, Modifier.weight(1f), isDark) { onTrackClick?.invoke(item3.slug) }
                    SubjectTimeCard(item4, Modifier.weight(1f), isDark) { onTrackClick?.invoke(item4.slug) }
                }
            }
        }

        // 3. Weekly Subject Comparison Canvas Bar Chart Box
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Weekly Subject Comparison", color = if (isDark) TextPrimary else Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    WeeklySubjectComparisonChart(items = data.weeklySubjectComparison, isDark = isDark)

                    Spacer(modifier = Modifier.height(10.dp))

                    // Legend
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        listOf(
                            Triple("Python", SunsetOrange, "PYTHON"),
                            Triple("SQL", AmberGold, "SQL"),
                            Triple("AI Tech", Color(0xFF8B5CF6), "AI_TECH"),
                            Triple("Aptitude", Color(0xFF10B981), "APTITUDE")
                        ).forEach { (label, color, _) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).background(color, CircleShape))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = label, fontSize = 9.sp, color = if (isDark) TextSecondary else Color(0xFF64748B))
                            }
                        }
                    }
                }
            }
        }

        // 4. Insights Box
        InsightsCard(insightsText = data.insights, isDark = isDark)
    }
}

/**
 * Time Analytics Tab with working interactive "This Week ▾" / "Last Week" / "This Month" filter.
 */
@Composable
private fun TimeAnalyticsContent(
    data: AnalyticsData,
    timeFilter: String,
    onTimeFilterChange: (String) -> Unit,
    isDark: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Study Time Card with Weekly Bar Chart Box
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Study Time", color = if (isDark) TextSecondary else Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = data.formattedStudyTime, color = if (isDark) TextPrimary else Color(0xFF0F172A), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }

                        TimeFilterDropdownPill(
                            currentFilter = timeFilter,
                            onFilterSelect = onTimeFilterChange,
                            isDark = isDark
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    DailyStudyBarChart(
                        days = data.chartDays,
                        heights = data.chartHeights,
                        minutes = data.chartMinutes,
                        isDark = isDark
                    )
                }
            }
        }

        // Metrics Box: Average Daily Study Time & Most Productive Day
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                    border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⏱ ", fontSize = 12.sp)
                            Text(text = "Average Daily Study Time", color = if (isDark) TextSecondary else Color(0xFF64748B), fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = data.averageDailyTime, color = if (isDark) TextPrimary else Color(0xFF0F172A), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = if (data.totalMinutes > 0) "Live average" else "0m daily", color = AmberGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                    border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📊 ", fontSize = 12.sp)
                            Text(text = "Most Productive Day", color = if (isDark) TextSecondary else Color(0xFF64748B), fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = data.mostProductiveDay, color = if (isDark) TextPrimary else Color(0xFF0F172A), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = data.mostProductiveDayText, color = if (isDark) TextSecondary else Color(0xFF64748B), fontSize = 10.sp)
                    }
                }
            }
        }

        // Time Distribution Box
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Time Distribution", color = if (isDark) TextPrimary else Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        data.timeDistribution.forEach { item ->
                            TimeDistributionRow(item = item, isDark = isDark)
                        }
                    }
                }
            }
        }

        // Insights Box
        InsightsCard(insightsText = data.insights, isDark = isDark)
    }
}

/**
 * Streak Analytics Tab with interactive Month Navigation (Arrows + Dropdown) & dynamic Calendar.
 */
@Composable
private fun StreakAnalyticsContent(
    data: AnalyticsData,
    selectedYear: Int,
    selectedMonth: Int,
    monthDisplayName: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectMonth: (Int) -> Unit,
    isDark: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Streak Summary Cards Row Box
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                    border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(36.dp).background(if (isDark) SunsetOrange.copy(alpha = 0.2f) else Color(0xFFFFF7ED), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🔥", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Current Streak", color = if (isDark) TextSecondary else Color(0xFF64748B), fontSize = 10.sp)
                            Text(text = "${data.currentStreak} days", color = if (isDark) TextPrimary else Color(0xFF0F172A), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(text = if (data.currentStreak > 0) "Keep it going!" else "Study today to start!", color = SunsetOrange, fontSize = 9.sp)
                        }
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                    border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(36.dp).background(if (isDark) AmberGold.copy(alpha = 0.2f) else Color(0xFFFEF3C7), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🏆", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Best Streak", color = if (isDark) TextSecondary else Color(0xFF64748B), fontSize = 10.sp)
                            Text(text = "${data.bestStreak} days", color = if (isDark) TextPrimary else Color(0xFF0F172A), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Your highest streak", color = if (isDark) TextSecondary else Color(0xFF94A3B8), fontSize = 9.sp)
                        }
                    }
                }
            }
        }

        // Study Calendar Grid Box with working Month Navigator (Arrows + Dropdown)
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Study Calendar", color = if (isDark) TextPrimary else Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)

                        // Interactive Month Navigation Bar with Arrows + Dropdown
                        MonthNavigatorPill(
                            displayName = monthDisplayName,
                            selectedMonth = selectedMonth,
                            onPrevious = onPreviousMonth,
                            onNext = onNextMonth,
                            onSelectMonth = onSelectMonth,
                            isDark = isDark
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Day Header Row (Mon..Sun)
                    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        daysOfWeek.forEach { day ->
                            Text(
                                text = day,
                                color = if (isDark) TextSecondary else Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dynamic Calendar Grid
                    val activeDaysSet = remember(data.monthActiveDays) { data.monthActiveDays.toSet() }
                    val calendarGrid = remember(selectedYear, selectedMonth, activeDaysSet) {
                        buildCalendarGridDays(selectedYear, selectedMonth, activeDaysSet)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (i in calendarGrid.indices step 7) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                                for (j in i until (i + 7).coerceAtMost(calendarGrid.size)) {
                                    val dayItem = calendarGrid[j]
                                    val isCurrentMonth = dayItem.isCurrentMonth
                                    val isActive = dayItem.isActive

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(28.dp)
                                            .padding(2.dp)
                                            .background(
                                                color = when {
                                                    isActive -> SunsetOrange
                                                    else -> Color.Transparent
                                                },
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = "${dayItem.dayNumber}",
                                            color = when {
                                                isActive -> Color.White
                                                !isCurrentMonth -> (if (isDark) TextMuted.copy(alpha = 0.4f) else Color(0xFFCBD5E1))
                                                else -> (if (isDark) TextSecondary else Color(0xFF64748B))
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Consistency Metrics Box
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                    border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🎯 ", fontSize = 12.sp)
                            Text(text = "Weekly Consistency", color = if (isDark) TextSecondary else Color(0xFF64748B), fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = data.weeklyConsistency, color = if (isDark) TextPrimary else Color(0xFF0F172A), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = if (data.weeklyConsistencyPercentage > 0) "Active this week" else "0% this week", color = AmberGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
                    border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✅ ", fontSize = 12.sp)
                            Text(text = "Completed Daily Goals", color = if (isDark) TextSecondary else Color(0xFF64748B), fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = data.completedDailyGoals, color = if (isDark) TextPrimary else Color(0xFF0F172A), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = "this week", color = if (isDark) TextSecondary else Color(0xFF64748B), fontSize = 10.sp)
                    }
                }
            }
        }

        // Insights Box
        val streakInsights = if (data.currentStreak > 0) {
            "You have a ${data.currentStreak}-day study streak! Stay consistent to beat your personal best streak of ${data.bestStreak} days."
        } else {
            "Start a micro-lesson or take a quick quiz today to kickstart your study streak!"
        }
        InsightsCard(insightsText = streakInsights, isDark = isDark)
    }
}

// ==========================================
// Reusable UI Components
// ==========================================

@Composable
private fun TimeFilterDropdownPill(
    currentFilter: String,
    onFilterSelect: (String) -> Unit,
    isDark: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    val filterLabels = mapOf(
        "this_week" to "This Week",
        "last_week" to "Last Week",
        "this_month" to "This Month"
    )
    val displayLabel = filterLabels[currentFilter] ?: "This Week"

    Box {
        Row(
            modifier = Modifier
                .background(if (isDark) SurfacePanel else Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$displayLabel ▾",
                color = if (isDark) TextSecondary else Color(0xFF64748B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(if (isDark) SurfacePanelElevated else Color.White)
        ) {
            listOf("this_week" to "This Week", "last_week" to "Last Week", "this_month" to "This Month").forEach { (key, label) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                            color = if (key == currentFilter) SunsetOrange else (if (isDark) TextPrimary else Color(0xFF0F172A)),
                            fontWeight = if (key == currentFilter) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    },
                    onClick = {
                        onFilterSelect(key)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors()
                )
            }
        }
    }
}

@Composable
private fun MonthNavigatorPill(
    displayName: String,
    selectedMonth: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSelectMonth: (Int) -> Unit,
    isDark: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    Row(
        modifier = Modifier
            .background(if (isDark) SurfacePanel else Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Arrow
        Text(
            text = "‹",
            color = if (isDark) TextSecondary else Color(0xFF64748B),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { onPrevious() }
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )

        // Clickable Month Dropdown
        Box {
            Text(
                text = "$displayName ▾",
                color = if (isDark) TextSecondary else Color(0xFF64748B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable { expanded = true }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(if (isDark) SurfacePanelElevated else Color.White)
            ) {
                monthNames.forEachIndexed { index, mName ->
                    val mNum = index + 1
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = mName,
                                color = if (mNum == selectedMonth) SunsetOrange else (if (isDark) TextPrimary else Color(0xFF0F172A)),
                                fontWeight = if (mNum == selectedMonth) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        onClick = {
                            onSelectMonth(mNum)
                            expanded = false
                        }
                    )
                }
            }
        }

        // Right Arrow
        Text(
            text = "›",
            color = if (isDark) TextSecondary else Color(0xFF64748B),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { onNext() }
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun DailyStudyBarChart(
    days: List<String>,
    heights: List<Float>,
    minutes: List<Int>,
    isDark: Boolean = false
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                val barW = 16.dp.toPx()
                val count = if (days.isNotEmpty()) days.size else 7
                val spacing = w / count

                for (i in 0 until count) {
                    val rawH = heights.getOrNull(i) ?: 0f
                    val x = i * spacing + (spacing - barW) / 2f
                    val barH = (h * rawH).coerceIn(0f, h)

                    if (barH > 0f) {
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(SunsetOrange, AmberGold)
                            ),
                            topLeft = Offset(x, h - barH),
                            size = Size(barW, barH)
                        )
                    } else {
                        // Subtle base bar placeholder
                        drawRect(
                            color = if (isDark) Color(0xFF1F202B) else Color(0xFFE2E8F0),
                            topLeft = Offset(x, h - 3.dp.toPx()),
                            size = Size(barW, 3.dp.toPx())
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            days.forEach { day ->
                Text(
                    text = day,
                    color = if (isDark) TextSecondary else Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun SubjectBreakdownRow(
    item: SubjectBreakdownItem,
    isDark: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val barColor = Color(android.graphics.Color.parseColor(item.colorHex))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(barColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${item.icon} ${item.name}",
                    color = if (isDark) TextSecondary else Color(0xFF334155),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "${item.percentage}% (${item.formattedTime})",
                color = if (isDark) TextPrimary else Color(0xFF0F172A),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { item.fraction.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = barColor,
            trackColor = if (isDark) Color(0xFF25221F) else Color(0xFFE2E8F0)
        )
    }
}

@Composable
private fun SubjectTimeCard(
    item: SubjectBreakdownItem,
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.clickable(enabled = onClick != null) { onClick?.invoke() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) SurfacePanelElevated else Color.White),
        border = BorderStroke(1.dp, if (isDark) SurfaceBorder else Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(if (isDark) SunsetOrange.copy(alpha = 0.2f) else Color(0xFFEFF6FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.icon, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = item.name, color = if (isDark) TextSecondary else Color(0xFF64748B), fontSize = 10.sp, maxLines = 1)
                    Text(text = item.formattedTime, color = if (isDark) TextPrimary else Color(0xFF0F172A), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text(text = "›", color = if (isDark) TextSecondary else Color(0xFF94A3B8), fontSize = 16.sp)
        }
    }
}

@Composable
private fun TimeDistributionRow(item: TimeDistributionItem, isDark: Boolean = false) {
    val barColor = Color(android.graphics.Color.parseColor(item.colorHex))
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = item.label, color = if (isDark) TextSecondary else Color(0xFF334155), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(text = "${item.percentage}% (${item.formattedTime})", color = if (isDark) TextPrimary else Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { item.fraction.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = barColor,
            trackColor = if (isDark) Color(0xFF25221F) else Color(0xFFE2E8F0)
        )
    }
}

@Composable
private fun WeeklySubjectComparisonChart(items: List<WeeklySubjectComparisonItem>, isDark: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val days = 7
            val spacing = w / days
            val barW = 3.5.dp.toPx()

            val colors = listOf(
                SunsetOrange,
                AmberGold,
                Color(0xFF8B5CF6),
                Color(0xFF10B981)
            )

            for (d in 0 until days) {
                val groupX = d * spacing + 8f
                items.take(4).forEachIndexed { trackIdx, trackItem ->
                    val rawH = trackItem.heights.getOrNull(d) ?: 0f
                    val bHeight = (h * rawH).coerceIn(0f, h)
                    val bx = groupX + trackIdx * (barW + 1.5f)
                    val bColor = colors.getOrElse(trackIdx) { SunsetOrange }

                    if (bHeight > 0f) {
                        drawRect(
                            color = bColor,
                            topLeft = Offset(bx, h - bHeight),
                            size = Size(barW, bHeight)
                        )
                    } else {
                        drawRect(
                            color = if (isDark) Color(0xFF1F202B) else Color(0xFFE2E8F0),
                            topLeft = Offset(bx, h - 2.dp.toPx()),
                            size = Size(barW, 2.dp.toPx())
                        )
                    }
                }
            }
        }
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
            Text(text = day, color = if (isDark) TextSecondary else Color(0xFF94A3B8), fontSize = 10.sp)
        }
    }
}

@Composable
private fun InsightsCard(insightsText: String, isDark: Boolean = false) {
    DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 2.dp) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) SunsetOrange.copy(alpha = 0.12f) else Color(0xFFEFF6FF)),
            border = BorderStroke(1.dp, if (isDark) SunsetOrange.copy(alpha = 0.3f) else Color(0xFFBFDBFE))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💡 ", fontSize = 12.sp)
                    Text(text = "Your Insights", color = SunsetOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = insightsText,
                    color = if (isDark) TextPrimary else Color(0xFF1E293B),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

data class CalendarDayItem(
    val dayNumber: Int,
    val isCurrentMonth: Boolean,
    val isActive: Boolean
)

private fun buildCalendarGridDays(year: Int, month: Int, activeDays: Set<Int>): List<CalendarDayItem> {
    val cal = Calendar.getInstance()
    cal.clear()
    cal.set(Calendar.YEAR, year)
    cal.set(Calendar.MONTH, month - 1)
    cal.set(Calendar.DAY_OF_MONTH, 1)

    val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon...
    val leadingEmpty = when (firstDayOfWeek) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5
        Calendar.SUNDAY -> 6
        else -> 0
    }

    cal.add(Calendar.MONTH, -1)
    val prevMax = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val list = mutableListOf<CalendarDayItem>()
    for (i in (prevMax - leadingEmpty + 1)..prevMax) {
        list.add(CalendarDayItem(dayNumber = i, isCurrentMonth = false, isActive = false))
    }

    for (d in 1..maxDays) {
        list.add(CalendarDayItem(dayNumber = d, isCurrentMonth = true, isActive = activeDays.contains(d)))
    }

    var nextMonthDay = 1
    while (list.size % 7 != 0) {
        list.add(CalendarDayItem(dayNumber = nextMonthDay++, isCurrentMonth = false, isActive = false))
    }

    return list
}

@Composable
fun AnalyticsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    initialTab: Int = 0,
    viewModel: AnalyticsViewModel = viewModel(),
    onTrackClick: ((String) -> Unit)? = null
) {
    ProgressAnalyticsScreen(
        onBackClick = onBackClick,
        modifier = modifier,
        initialTab = initialTab,
        viewModel = viewModel,
        onTrackClick = onTrackClick
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProgressAnalyticsScreenPreview() {
    SYNAPSETheme {
        ProgressAnalyticsScreen(onBackClick = {}, initialTab = 0, modifier = Modifier.fillMaxSize())
    }
}
