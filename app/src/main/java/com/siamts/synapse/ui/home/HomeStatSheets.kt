package com.siamts.synapse.ui.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.siamts.synapse.data.local.LearningTaskEntity
import com.siamts.synapse.ui.theme.AmberGold
import com.siamts.synapse.ui.theme.LocalThemeIsDark
import com.siamts.synapse.ui.theme.SunsetOrange

/**
 * Compact, Centered Popup Dialog for "TODAY'S OVERALL PROGRESS".
 * Shows:
 *  - Header: "Overall Learning Progress" + circular X close button
 *  - Center Hero Ring: 96.dp circular ring with percentage and "Completed"
 *  - 3 Compact Stat Boxes: Lessons (X/40 done), Quizzes (Y/40 done), Tracks (Z/8 active)
 *  - Top Active Subjects Mini-Bars (3 rows, compact, no scrolling)
 *  - Full-width Sunset Orange "Continue Learning" button
 */
@Composable
fun TodayOverallProgressDialog(
    onDismissRequest: () -> Unit,
    overallProgress: Int,
    topicsDone: Int,
    totalTopics: Int = 40,
    quizScore: Int = 0,
    quizzesAttempted: Int = 0,
    studyStreakDays: Int = 0,
    bestStreakDays: Int = 0,
    mindPower: Int = 0,
    dailyTasks: List<LearningTaskEntity> = emptyList(),
    subjects: List<HomeSubjectItem> = emptyList(),
    onContinueLearningClick: () -> Unit = {},
    onTakeQuizClick: () -> Unit = {},
    onSubjectClick: (String) -> Unit = {},
) {
    val isDark = LocalThemeIsDark.current
    val dialogBg = if (isDark) Color(0xFF181B22) else Color.White
    val dialogBorder = if (isDark) Color(0xFF282D3A) else Color(0xFFE2E8F0)
    val pillBg = if (isDark) Color(0xFF222630) else Color(0xFFF8FAFC)
    val textPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = dialogBg),
            border = BorderStroke(1.2.dp, dialogBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header: "Overall Learning Progress" + Circular X close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Overall Learning Progress",
                        color = textPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(pillBg)
                            .border(1.dp, dialogBorder, CircleShape)
                            .clickable { onDismissRequest() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✕",
                            color = textSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Center Hero Ring: 96.dp circular progress ring with percentage & "Completed"
                Box(
                    modifier = Modifier.size(96.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { (overallProgress / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFFFF6B00),
                        trackColor = if (isDark) Color(0xFF282D3A) else Color(0xFFE2E8F0),
                        strokeWidth = 8.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$overallProgress%",
                            color = textPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Completed",
                            color = textSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 3 Compact Stat Boxes in a Row (8.dp gap)
                val activeTracks = subjects.count { it.progressPercent > 0 }.coerceAtLeast(1)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Box 1: Lessons Done
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(pillBg, RoundedCornerShape(12.dp))
                            .border(1.dp, dialogBorder, RoundedCornerShape(12.dp))
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$topicsDone / 40",
                                color = textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Lessons",
                                color = textSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "done",
                                color = textSecondary.copy(alpha = 0.8f),
                                fontSize = 9.sp
                            )
                        }
                    }

                    // Box 2: Quizzes Done
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(pillBg, RoundedCornerShape(12.dp))
                            .border(1.dp, dialogBorder, RoundedCornerShape(12.dp))
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$quizzesAttempted / 40",
                                color = AmberGold,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Quizzes",
                                color = textSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "done",
                                color = textSecondary.copy(alpha = 0.8f),
                                fontSize = 9.sp
                            )
                        }
                    }

                    // Box 3: Tracks Active
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(pillBg, RoundedCornerShape(12.dp))
                            .border(1.dp, dialogBorder, RoundedCornerShape(12.dp))
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$activeTracks / 8",
                                color = Color(0xFF3B82F6),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Tracks",
                                color = textSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "active",
                                color = textSecondary.copy(alpha = 0.8f),
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Top Active Subjects Mini-Bars (Max 3 rows, compact, no scrolling needed)
                val defaultTracks = listOf(
                    HomeSubjectItem(1, "Python & Coding Logic", "python", "🐍", "#3B82F6", 5, 0, 0),
                    HomeSubjectItem(2, "SQL & Relational DB", "sql", "🐬", "#10B981", 5, 0, 0),
                    HomeSubjectItem(3, "AI & Machine Learning", "ai", "🧠", "#8B5CF6", 5, 0, 0)
                )
                val topSubjects = (if (subjects.isNotEmpty()) subjects else defaultTracks)
                    .sortedByDescending { it.progressPercent }
                    .take(3)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(pillBg, RoundedCornerShape(14.dp))
                        .border(1.dp, dialogBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    topSubjects.forEach { subj ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = subj.iconSymbol, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = subj.name,
                                        color = textPrimary,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = "${subj.progressPercent}%",
                                    color = if (subj.progressPercent > 0) Color(0xFFFF6B00) else textSecondary,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            // Slim orange progress bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (isDark) Color(0xFF282D3A) else Color(0xFFE2E8F0))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth((subj.progressPercent / 100f).coerceIn(0f, 1f))
                                        .height(4.dp)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(Color(0xFFFFB03A), Color(0xFFFF6B00))
                                            )
                                        )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bottom Action Button: Full-width Sunset Orange (#FF6B00) "Continue Learning" button
                Button(
                    onClick = {
                        onDismissRequest()
                        onContinueLearningClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B00))
                ) {
                    Text(
                        text = "Continue Learning 🚀",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Backward compatibility alias for TodayOverallProgressBottomSheet -> TodayOverallProgressDialog
 */
@Composable
fun TodayOverallProgressBottomSheet(
    onDismissRequest: () -> Unit,
    overallProgress: Int,
    topicsDone: Int,
    totalTopics: Int = 40,
    quizScore: Int = 0,
    quizzesAttempted: Int = 0,
    studyStreakDays: Int = 0,
    bestStreakDays: Int = 0,
    mindPower: Int = 0,
    dailyTasks: List<LearningTaskEntity> = emptyList(),
    subjects: List<HomeSubjectItem> = emptyList(),
    onContinueLearningClick: () -> Unit = {},
    onTakeQuizClick: () -> Unit = {},
    onSubjectClick: (String) -> Unit = {},
) {
    TodayOverallProgressDialog(
        onDismissRequest = onDismissRequest,
        overallProgress = overallProgress,
        topicsDone = topicsDone,
        totalTopics = totalTopics,
        quizScore = quizScore,
        quizzesAttempted = quizzesAttempted,
        studyStreakDays = studyStreakDays,
        bestStreakDays = bestStreakDays,
        mindPower = mindPower,
        dailyTasks = dailyTasks,
        subjects = subjects,
        onContinueLearningClick = onContinueLearningClick,
        onTakeQuizClick = onTakeQuizClick,
        onSubjectClick = onSubjectClick
    )
}

/**
 * Compact, Centered Popup Dialog for "Topics Done" Stat Card.
 * Displays overall curriculum completion percentage, linear progress bar,
 * compact stat breakdown, and a clean "Got it" button (Zero scrolling needed).
 */
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
    val dialogBg = if (isDark) Color(0xFF1C1F26) else Color.White
    val dialogBorder = if (isDark) Color(0xFF2E3342) else Color(0xFFE2E8F0)
    val pillBg = if (isDark) Color(0xFF262A34) else Color(0xFFF1F5F9)
    val textPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = dialogBg),
            border = BorderStroke(1.2.dp, dialogBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Emblem & Title
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF3B82F6).copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📚", fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Topics & Lessons Progress",
                    color = textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(3.dp))

                val displayTotal = totalTopics.coerceAtLeast(20)
                Text(
                    text = "$topicsDone of $displayTotal topics completed ($overallProgress%)",
                    color = textSecondary,
                    fontSize = 12.5.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Linear Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isDark) Color(0xFF262A34) else Color(0xFFE2E8F0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((overallProgress / 100f).coerceIn(0f, 1f))
                            .height(8.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF3B82F6), Color(0xFF60A5FA))
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2 Stat Boxes Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(pillBg, RoundedCornerShape(12.dp))
                            .border(1.dp, dialogBorder, RoundedCornerShape(12.dp))
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$topicsDone Done",
                                color = Color(0xFF3B82F6),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "+10% Credit / Topic",
                                color = textSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    val remaining = (displayTotal - topicsDone).coerceAtLeast(0)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(pillBg, RoundedCornerShape(12.dp))
                            .border(1.dp, dialogBorder, RoundedCornerShape(12.dp))
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$remaining Left",
                                color = textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Curriculum Remaining",
                                color = textSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Each finished chapter lesson awards +10% towards subject mastery and placement readiness.",
                    color = textSecondary,
                    fontSize = 11.5.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Clean "Got it" button
                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                ) {
                    Text(
                        text = "Got it ✓",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Compact, Centered Popup Dialog for "Quiz Score" Stat Card.
 * Displays live placement readiness tier, score breakdown, and a clean "Got it" button (Zero scrolling needed).
 */
@Composable
fun QuizScoreBottomSheet(
    onDismissRequest: () -> Unit,
    quizScore: Int,
    quizzesAttempted: Int = 0,
    latestQuizScore: Int = 0,
    onTakeQuizClick: () -> Unit = {},
) {
    val isDark = LocalThemeIsDark.current
    val dialogBg = if (isDark) Color(0xFF1C1F26) else Color.White
    val dialogBorder = if (isDark) Color(0xFF2E3342) else Color(0xFFE2E8F0)
    val pillBg = if (isDark) Color(0xFF262A34) else Color(0xFFF1F5F9)
    val textPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    val tierTitle: String
    val tierDesc: String
    val tierColor: Color
    if (quizScore >= 80) {
        tierTitle = "Placement Ready 🏆"
        tierDesc = "Outstanding accuracy! Consistently clearing placement benchmarks."
        tierColor = Color(0xFF10B981)
    } else if (quizScore >= 60) {
        tierTitle = "On Track 📈"
        tierDesc = "Good progress! Keep completing chapter quizzes to reach 80%+."
        tierColor = AmberGold
    } else if (quizScore >= 40) {
        tierTitle = "Developing Foundation 💡"
        tierDesc = "Solid effort. Review missed questions in Mistake Vault to sharpen skills."
        tierColor = SunsetOrange
    } else {
        tierTitle = "Getting Started 🚀"
        tierDesc = "Take a 5-minute chapter quiz to calculate live placement accuracy!"
        tierColor = Color(0xFF3B82F6)
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = dialogBg),
            border = BorderStroke(1.2.dp, dialogBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Emblem & Title
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(AmberGold.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎯", fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Placement Quiz Performance",
                    color = textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Average Placement Accuracy: $quizScore%",
                    color = AmberGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Tier Status Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(pillBg, RoundedCornerShape(12.dp))
                        .border(1.dp, tierColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = tierTitle,
                            color = tierColor,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tierDesc,
                            color = textSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2 Stat Boxes Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(pillBg, RoundedCornerShape(12.dp))
                            .border(1.dp, dialogBorder, RoundedCornerShape(12.dp))
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$quizzesAttempted",
                                color = SunsetOrange,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Quizzes Taken",
                                color = textSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(pillBg, RoundedCornerShape(12.dp))
                            .border(1.dp, dialogBorder, RoundedCornerShape(12.dp))
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "+10%",
                                color = Color(0xFF10B981),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Credit / Quiz",
                                color = textSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Clean "Got it" button
                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
                ) {
                    Text(
                        text = "Got it ✓",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Compact, Centered Popup Dialog for "Study Streak" Stat Card.
 * Displays current and best streaks, 7-day mini activity indicators, and a clean "Got it" button (Zero scrolling needed).
 */
@Composable
fun StudyStreakBottomSheet(
    onDismissRequest: () -> Unit,
    studyStreakDays: Int,
    bestStreakDays: Int = 0,
    weeklyActiveDays: List<Boolean> = emptyList(),
    onViewCalendarClick: () -> Unit = {},
) {
    val isDark = LocalThemeIsDark.current
    val dialogBg = if (isDark) Color(0xFF1C1F26) else Color.White
    val dialogBorder = if (isDark) Color(0xFF2E3342) else Color(0xFFE2E8F0)
    val pillBg = if (isDark) Color(0xFF262A34) else Color(0xFFF1F5F9)
    val textPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = dialogBg),
            border = BorderStroke(1.2.dp, dialogBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Emblem & Title
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(SunsetOrange.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔥", fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Daily Study Streak",
                    color = textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = if (studyStreakDays == 0)
                        "Complete a lesson today to ignite your streak!"
                    else
                        "$studyStreakDays days in a row • Keep the momentum!",
                    color = textSecondary,
                    fontSize = 12.5.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Current Streak vs Best Streak Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(pillBg, RoundedCornerShape(12.dp))
                            .border(1.dp, SunsetOrange.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$studyStreakDays Days",
                                color = SunsetOrange,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Current Streak 🔥",
                                color = textSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(pillBg, RoundedCornerShape(12.dp))
                            .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$bestStreakDays Days",
                                color = AmberGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Personal Best 🏆",
                                color = textSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 7-Day Mini Dots (Mon - Sun)
                val dayNames = listOf("M", "T", "W", "T", "F", "S", "S")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    dayNames.forEachIndexed { index, dayName ->
                        val isActive = weeklyActiveDays.getOrElse(index) { false }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isActive) SunsetOrange else pillBg
                                    )
                                    .border(
                                        1.dp,
                                        if (isActive) SunsetOrange else dialogBorder,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isActive) {
                                    Text(text = "🔥", fontSize = 13.sp)
                                } else {
                                    Text(
                                        text = dayName,
                                        color = textSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Clean "Got it" button
                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)
                ) {
                    Text(
                        text = "Got it ✓",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
