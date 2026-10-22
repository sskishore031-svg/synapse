package com.siamts.synapse.ui.profile

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
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@Composable
fun ProgressAnalyticsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    initialTab: Int = 1, // 0: Overview, 1: Subject, 2: Time, 3: Streaks
) {
    var selectedTab by remember { mutableStateOf(initialTab) }

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
                        Text(text = pageTitle, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                        // Sub-tabs: Overview, Subject, Time, Streaks
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 4.dp) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
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
                                            .background(if (isSel) BlueAccent else Color.Transparent, RoundedCornerShape(8.dp))
                                            .clickable { selectedTab = index },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(text = tabName, color = if (isSel) Color.White else Color(0xFF64748B), fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium)
                                    }
                                }
                            }
                        }

                        when (selectedTab) {
                            1 -> SubjectAnalyticsContent()
                            2 -> TimeAnalyticsContent()
                            3 -> StreakAnalyticsContent()
                            else -> OverviewAnalyticsContent()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectAnalyticsContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 1. Subjects Breakdown Box
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Subjects Breakdown", color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    val subjects = listOf(
                        Triple("Computer Science", 0.45f, Color(0xFF3B82F6)),
                        Triple("AI & DS", 0.25f, Color(0xFF10B981)),
                        Triple("Mathematics", 0.18f, Color(0xFF8B5CF6)),
                        Triple("Others", 0.12f, Color(0xFFF59E0B)),
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        subjects.forEach { (sName, sPct, sColor) ->
                            Column {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(8.dp).background(sColor, CircleShape))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = sName, color = Color(0xFF334155), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    }
                                    Text(text = "${(sPct * 100).toInt()}%", color = Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { sPct },
                                    modifier = Modifier.fillMaxWidth().height(6.dp),
                                    color = sColor,
                                    trackColor = Color(0xFFE2E8F0)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Study Time by Subject Cards Box
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Study Time by Subject", color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SubjectTimeCard("Computer Science", "1h 33m", "💻", Modifier.weight(1f))
                    SubjectTimeCard("AI & DS", "54m", "🧠", Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SubjectTimeCard("Mathematics", "37m", "🧮", Modifier.weight(1f))
                    SubjectTimeCard("Others", "21m", "📋", Modifier.weight(1f))
                }
            }
        }

        // 3. Weekly Subject Comparison Canvas Bar Chart Box
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Weekly Subject Comparison", color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val days = 7
                            val spacing = w / days
                            val barW = 4.dp.toPx()

                            val cCS = Color(0xFF3B82F6)
                            val cAI = Color(0xFF10B981)
                            val cMath = Color(0xFF8B5CF6)
                            val cOth = Color(0xFFF59E0B)

                            val csH = listOf(0.6f, 0.4f, 0.8f, 0.7f, 0.5f, 0.9f, 0.65f)
                            val aiH = listOf(0.4f, 0.3f, 0.5f, 0.6f, 0.4f, 0.7f, 0.5f)
                            val mathH = listOf(0.3f, 0.5f, 0.2f, 0.4f, 0.3f, 0.5f, 0.4f)
                            val othH = listOf(0.2f, 0.1f, 0.3f, 0.2f, 0.2f, 0.3f, 0.2f)

                            for (i in 0 until days) {
                                val groupX = i * spacing + 12f
                                drawRect(color = cCS, topLeft = Offset(groupX, h * (1 - csH[i])), size = Size(barW, h * csH[i]))
                                drawRect(color = cAI, topLeft = Offset(groupX + barW + 2f, h * (1 - aiH[i])), size = Size(barW, h * aiH[i]))
                                drawRect(color = cMath, topLeft = Offset(groupX + (barW + 2f) * 2, h * (1 - mathH[i])), size = Size(barW, h * mathH[i]))
                                drawRect(color = cOth, topLeft = Offset(groupX + (barW + 2f) * 3, h * (1 - othH[i])), size = Size(barW, h * othH[i]))
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                            Text(text = day, color = Color(0xFF94A3B8), fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).background(Color(0xFF3B82F6), CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Computer Science", fontSize = 9.sp, color = Color(0xFF64748B))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).background(Color(0xFF10B981), CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "AI & DS", fontSize = 9.sp, color = Color(0xFF64748B))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).background(Color(0xFF8B5CF6), CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Mathematics", fontSize = 9.sp, color = Color(0xFF64748B))
                        }
                    }
                }
            }
        }

        // 4. Insights Box
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 2.dp) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💡 ", fontSize = 12.sp)
                        Text(text = "Your Insights", color = BlueAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "You spent the most time on Computer Science this week (45% of total study time). Keep it up!", color = Color(0xFF1E293B), fontSize = 11.sp, lineHeight = 15.sp)
                }
            }
        }
    }
}

@Composable
private fun SubjectTimeCard(title: String, timeText: String, icon: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(28.dp).background(Color(0xFFEFF6FF), CircleShape), contentAlignment = Alignment.Center) {
                    Text(text = icon, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = title, color = Color(0xFF64748B), fontSize = 10.sp)
                    Text(text = timeText, color = Color(0xFF0F172A), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text(text = "›", color = Color(0xFF94A3B8), fontSize = 16.sp)
        }
    }
}

@Composable
private fun TimeAnalyticsContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Study Time Card with Weekly Bar Chart Box
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(text = "Study Time", color = Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "3h 25m", color = Color(0xFF0F172A), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(modifier = Modifier.background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text(text = "This Week ▾", color = Color(0xFF64748B), fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            val heights = listOf(0.3f, 0.6f, 0.45f, 0.8f, 0.5f, 0.9f, 0.7f)
                            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                            val barW = 16.dp.toPx()
                            val spacing = w / days.size

                            for (i in days.indices) {
                                val x = i * spacing + (spacing - barW) / 2f
                                val barH = h * heights[i]
                                drawRect(color = BlueAccent, topLeft = Offset(x, h - barH), size = Size(barW, barH))
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                            Text(text = day, color = Color(0xFF94A3B8), fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Metrics Box
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⏱ ", fontSize = 12.sp)
                            Text(text = "Average Daily Study Time", color = Color(0xFF64748B), fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "29m", color = Color(0xFF0F172A), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = "▲ 20% vs last week", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📊 ", fontSize = 12.sp)
                            Text(text = "Most Productive Day", color = Color(0xFF64748B), fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Saturday", color = Color(0xFF0F172A), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = "56m spent", color = Color(0xFF64748B), fontSize = 10.sp)
                    }
                }
            }
        }

        // Time Distribution Box
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Time Distribution", color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    val dist = listOf(
                        Triple("Focused Study", 0.70f, BlueAccent),
                        Triple("Practice (Quiz)", 0.20f, Color(0xFF10B981)),
                        Triple("Note Making", 0.10f, Color(0xFF8B5CF6)),
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        dist.forEach { (dLabel, dPct, dColor) ->
                            Column {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = dLabel, color = Color(0xFF334155), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Text(text = "${(dPct * 100).toInt()}%", color = Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { dPct },
                                    modifier = Modifier.fillMaxWidth().height(6.dp),
                                    color = dColor,
                                    trackColor = Color(0xFFE2E8F0)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Insights Box
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 2.dp) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💡 ", fontSize = 12.sp)
                        Text(text = "Your Insights", color = BlueAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "You studied 20% more this week compared to last week. Great progress!", color = Color(0xFF1E293B), fontSize = 11.sp, lineHeight = 15.sp)
                }
            }
        }
    }
}

@Composable
private fun StreakAnalyticsContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Streak Summary Cards Row Box
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).background(Color(0xFFFFF7ED), CircleShape), contentAlignment = Alignment.Center) {
                            Text(text = "🔥", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Current Streak", color = Color(0xFF64748B), fontSize = 10.sp)
                            Text(text = "5 days", color = Color(0xFF0F172A), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Keep it going!", color = Color(0xFF94A3B8), fontSize = 9.sp)
                        }
                    }
                }

                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).background(Color(0xFFFEF3C7), CircleShape), contentAlignment = Alignment.Center) {
                            Text(text = "🏆", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Best Streak", color = Color(0xFF64748B), fontSize = 10.sp)
                            Text(text = "12 days", color = Color(0xFF0F172A), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Your highest streak", color = Color(0xFF94A3B8), fontSize = 9.sp)
                        }
                    }
                }
            }
        }

        // Study Calendar Grid Box
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Study Calendar", color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Box(modifier = Modifier.background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text(text = "October 2026 ▾", color = Color(0xFF64748B), fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        daysOfWeek.forEach { day ->
                            Text(text = day, color = Color(0xFF94A3B8), fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val activeDays = setOf(5, 6, 8, 10, 14, 15, 17, 20, 24, 28, 29, 31)
                    val dates = (28..30).map { Pair(it, false) } + (1..31).map { Pair(it, activeDays.contains(it)) }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (i in dates.indices step 7) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                                for (j in i until (i + 7).coerceAtMost(dates.size)) {
                                    val (dNum, isActive) = dates[j]
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(28.dp)
                                            .padding(2.dp)
                                            .background(if (isActive) BlueAccent else Color.Transparent, CircleShape),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = "$dNum",
                                            color = if (isActive) Color.White else Color(0xFF64748B),
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
                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🎯 ", fontSize = 12.sp)
                            Text(text = "Weekly Consistency", color = Color(0xFF64748B), fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "71%", color = Color(0xFF0F172A), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = "▲ 15% vs last week", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✅ ", fontSize = 12.sp)
                            Text(text = "Completed Daily Goals", color = Color(0xFF64748B), fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "5 / 7", color = Color(0xFF0F172A), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = "this week", color = Color(0xFF64748B), fontSize = 10.sp)
                    }
                }
            }
        }

        // Insights Box
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 2.dp) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💡 ", fontSize = 12.sp)
                        Text(text = "Your Insights", color = BlueAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "You have a 5-day study streak! Stay consistent to beat your best streak of 12 days.", color = Color(0xFF1E293B), fontSize = 11.sp, lineHeight = 15.sp)
                }
            }
        }
    }
}

@Composable
private fun OverviewAnalyticsContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Study Time Card
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(text = "Study Time", color = Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "3h 25m", color = Color(0xFF0F172A), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(modifier = Modifier.background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text(text = "This Week ▾", color = Color(0xFF64748B), fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            val heights = listOf(0.3f, 0.6f, 0.45f, 0.8f, 0.5f, 0.9f, 0.7f)
                            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                            val barW = 16.dp.toPx()
                            val spacing = w / days.size

                            for (i in days.indices) {
                                val x = i * spacing + (spacing - barW) / 2f
                                val barH = h * heights[i]
                                drawRect(color = BlueAccent, topLeft = Offset(x, h - barH), size = Size(barW, barH))
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                            Text(text = day, color = Color(0xFF94A3B8), fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Subjects Breakdown Donut Chart Card Box
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Subjects Breakdown", color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(10.dp))

                    val subjects = listOf(
                        Triple("Computer Science", "45%", Color(0xFF3B82F6)),
                        Triple("AI & DS", "25%", Color(0xFF10B981)),
                        Triple("Mathematics", "18%", Color(0xFF8B5CF6)),
                        Triple("Others", "12%", Color(0xFFF59E0B)),
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        subjects.forEach { (sName, sPct, sColor) ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(10.dp).background(sColor, CircleShape))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = sName, color = Color(0xFF334155), fontSize = 12.sp)
                                }
                                Text(text = sPct, color = Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Insights Card Box
        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 2.dp) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💡 ", fontSize = 12.sp)
                        Text(text = "Your Insights", color = BlueAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "You studied 20% more this week compared to last week. Great progress!", color = Color(0xFF1E293B), fontSize = 11.sp, lineHeight = 15.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProgressAnalyticsScreenPreview() {
    SYNAPSETheme {
        ProgressAnalyticsScreen(onBackClick = {}, initialTab = 1, modifier = Modifier.fillMaxSize())
    }
}
