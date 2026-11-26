package com.siamts.synapse.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siamts.synapse.ui.theme.AmberGold
import com.siamts.synapse.ui.theme.AmberGoldEnd
import com.siamts.synapse.ui.theme.LocalThemeIsDark
import com.siamts.synapse.ui.theme.SurfaceBorder
import com.siamts.synapse.ui.theme.SurfacePanel
import com.siamts.synapse.ui.theme.SurfacePanelElevated
import com.siamts.synapse.ui.theme.SunsetOrange
import com.siamts.synapse.ui.theme.SunsetOrangeEnd
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary

/**
 * Interactive Popup Screen for "Topics Done" Stat Card.
 * Displays overall curriculum completion percentage, linear progress bar,
 * and a breakdown of all 8 placement subject tracks with one-tap navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicsDoneBottomSheet(
    onDismissRequest: () -> Unit,
    topicsDone: Int,
    totalTopics: Int = 20,
    overallProgress: Int = 0,
    subjects: List<HomeSubjectItem> = emptyList(),
    onSubjectClick: (String) -> Unit = {},
    onExploreAllClick: () -> Unit = {},
) {
    val isDark = LocalThemeIsDark.current
    val cardBg = if (isDark) SurfacePanel else Color.White
    val cardBorder = if (isDark) SurfaceBorder else Color(0xFFE4E7F0)
    val elevatedBg = if (isDark) SurfacePanelElevated else Color(0xFFF1F5F9)
    val textPrimaryColor = if (isDark) TextPrimary else Color(0xFF11131A)
    val textSecondaryColor = if (isDark) TextSecondary else Color(0xFF64748B)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = cardBg,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .background(cardBorder, RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Emblem
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF3B82F6).copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📚", fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Topics & Lessons Progress",
                color = textPrimaryColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            val displayTotal = totalTopics.coerceAtLeast(20)
            Text(
                text = "$topicsDone of $displayTotal topics completed ($overallProgress%)",
                color = textSecondaryColor,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Overall Progress Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = elevatedBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Curriculum Completion",
                            color = textPrimaryColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$overallProgress%",
                            color = SunsetOrange,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Linear progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(cardBorder)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth((overallProgress / 100f).coerceIn(0f, 1f))
                                .height(8.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(SunsetOrange, AmberGold)
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (topicsDone > 0)
                            "You've completed $topicsDone chapter lessons. Each finished chapter awards +10% towards subject mastery and placement readiness."
                        else
                            "No topics marked completed yet. Open any subject and tap 'Mark as Done' on lessons to track your progress live!",
                        color = textSecondaryColor,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subject Tracks Quick List
            Text(
                text = "Placement Subject Tracks",
                color = textPrimaryColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            val defaultTracks = listOf(
                HomeSubjectItem(1, "Python & Coding Logic", "python", "🐍", "#3B82F6", 5, 0, 0),
                HomeSubjectItem(2, "SQL & Relational DB", "sql", "🐬", "#10B981", 5, 0, 0),
                HomeSubjectItem(3, "AI & Machine Learning", "ai", "🧠", "#8B5CF6", 5, 0, 0),
                HomeSubjectItem(4, "Quantitative Aptitude", "aptitude", "📐", "#F59E0B", 5, 0, 0),
                HomeSubjectItem(5, "Data Structures & Algos", "dsa", "⚡", "#EC4899", 5, 0, 0),
                HomeSubjectItem(6, "Java & OOP Concepts", "java_oops", "☕", "#6366F1", 5, 0, 0),
                HomeSubjectItem(7, "OS & Computer Networks", "os_cn", "🌐", "#06B6D4", 5, 0, 0),
                HomeSubjectItem(8, "Android & Mobile Dev", "android_dev", "📱", "#14B8A6", 5, 0, 0)
            )

            val displaySubjects = if (subjects.isNotEmpty()) subjects else defaultTracks

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 210.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                displaySubjects.forEach { subject ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onDismissRequest()
                                onSubjectClick(subject.slug)
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = elevatedBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = subject.iconSymbol, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = subject.name,
                                        color = textPrimaryColor,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${subject.completedChapters} / ${subject.totalChapters} chapters done",
                                        color = textSecondaryColor,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Text(
                                text = "${subject.progressPercent}%",
                                color = if (subject.progressPercent > 0) SunsetOrange else textSecondaryColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Button
            Button(
                onClick = {
                    onDismissRequest()
                    onExploreAllClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)
            ) {
                Text(
                    text = "Explore All Subjects 🚀",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Interactive Popup Screen for "Quiz Score" Stat Card.
 * Displays live placement readiness tier, score breakdown, and quick action to take quizzes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScoreBottomSheet(
    onDismissRequest: () -> Unit,
    quizScore: Int,
    quizzesAttempted: Int = 0,
    latestQuizScore: Int = 0,
    onTakeQuizClick: () -> Unit = {},
) {
    val isDark = LocalThemeIsDark.current
    val cardBg = if (isDark) SurfacePanel else Color.White
    val cardBorder = if (isDark) SurfaceBorder else Color(0xFFE4E7F0)
    val elevatedBg = if (isDark) SurfacePanelElevated else Color(0xFFF1F5F9)
    val textPrimaryColor = if (isDark) TextPrimary else Color(0xFF11131A)
    val textSecondaryColor = if (isDark) TextSecondary else Color(0xFF64748B)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = cardBg,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .background(cardBorder, RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Emblem
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(AmberGold.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎯", fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Placement Quiz Performance",
                color = textPrimaryColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Average Placement Accuracy: $quizScore%",
                color = AmberGold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Performance Tier Card
            val tierTitle: String
            val tierDesc: String
            val tierIcon: String
            val tierColor: Color
            if (quizScore >= 80) {
                tierTitle = "Placement Ready 🏆"
                tierDesc = "Outstanding accuracy! You are consistently clearing placement test benchmarks."
                tierIcon = "🌟"
                tierColor = Color(0xFF10B981)
            } else if (quizScore >= 60) {
                tierTitle = "On Track 📈"
                tierDesc = "Good progress! Keep completing chapter quizzes to break into the 80%+ top tier."
                tierIcon = "⚡"
                tierColor = AmberGold
            } else if (quizScore >= 40) {
                tierTitle = "Developing Foundation 💡"
                tierDesc = "Solid effort. Review missed questions in your Mistake Vault to sharpen weak areas."
                tierIcon = "🎯"
                tierColor = SunsetOrange
            } else {
                tierTitle = "Getting Started 🚀"
                tierDesc = "Take your first 5-minute chapter quiz to calculate your live accuracy and earn +10% chapter credit!"
                tierIcon = "📚"
                tierColor = Color(0xFF3B82F6)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = elevatedBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, tierColor.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = tierIcon, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tierTitle,
                            color = tierColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = tierDesc,
                        color = textSecondaryColor,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Breakdown Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = elevatedBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$quizScore%",
                            color = AmberGold,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Avg Accuracy",
                            color = textSecondaryColor,
                            fontSize = 11.sp
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = elevatedBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$quizzesAttempted",
                            color = SunsetOrange,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tests Attempted",
                            color = textSecondaryColor,
                            fontSize = 11.sp
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = elevatedBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "+10%",
                            color = Color(0xFF10B981),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Credit / Quiz",
                            color = textSecondaryColor,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Take Quiz Action Button
            Button(
                onClick = {
                    onDismissRequest()
                    onTakeQuizClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
            ) {
                Text(
                    text = "⚡ Take a 5-Min Quiz Now",
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Interactive Popup Screen for "Study Streak" Stat Card.
 * Displays current and best streaks, 7-day visual activity calendar, and motivational tips.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyStreakBottomSheet(
    onDismissRequest: () -> Unit,
    studyStreakDays: Int,
    bestStreakDays: Int = 0,
    weeklyActiveDays: List<Boolean> = emptyList(),
    onViewCalendarClick: () -> Unit = {},
) {
    val isDark = LocalThemeIsDark.current
    val cardBg = if (isDark) SurfacePanel else Color.White
    val cardBorder = if (isDark) SurfaceBorder else Color(0xFFE4E7F0)
    val elevatedBg = if (isDark) SurfacePanelElevated else Color(0xFFF1F5F9)
    val textPrimaryColor = if (isDark) TextPrimary else Color(0xFF11131A)
    val textSecondaryColor = if (isDark) TextSecondary else Color(0xFF64748B)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = cardBg,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .background(cardBorder, RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Flame Emblem
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(SunsetOrange.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🔥", fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Daily Study Streak",
                color = textPrimaryColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            val motivationalText = if (studyStreakDays == 0) {
                "Start your first micro-lesson today to ignite your streak!"
            } else if (studyStreakDays == 1) {
                "Great start! Complete a lesson tomorrow to keep the flame alive."
            } else {
                "You're on fire! $studyStreakDays days in a row — keep the momentum going!"
            }

            Text(
                text = motivationalText,
                color = textSecondaryColor,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Stats Cards: Current Streak & Best Streak
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = elevatedBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp, horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$studyStreakDays days",
                            color = SunsetOrange,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Current Streak",
                            color = textSecondaryColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = elevatedBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp, horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$bestStreakDays days",
                            color = AmberGold,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Best Streak",
                            color = textSecondaryColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // This Week's Activity Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "This Week's Activity",
                    color = textPrimaryColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${weeklyActiveDays.count { it }} / 7 active",
                    color = SunsetOrange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mon - Sun Pills Row
            val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dayNames.forEachIndexed { index, dayName ->
                    val isActive = weeklyActiveDays.getOrElse(index) { false }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isActive) SunsetOrange else elevatedBg
                                )
                                .border(
                                    1.dp,
                                    if (isActive) SunsetOrange else cardBorder,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isActive) {
                                Text(text = "🔥", fontSize = 16.sp)
                            } else {
                                Text(
                                    text = dayName.take(1),
                                    color = textSecondaryColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = dayName,
                            color = if (isActive) SunsetOrange else textSecondaryColor,
                            fontSize = 10.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // View Calendar / Study Action Button
            Button(
                onClick = {
                    onDismissRequest()
                    onViewCalendarClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)
            ) {
                Text(
                    text = "View Full Streak Calendar 📅",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
