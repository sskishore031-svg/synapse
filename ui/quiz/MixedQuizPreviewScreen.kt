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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
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

data class MixedQuizItem(
    val id: String,
    val subjectTag: String,
    val subjectIcon: String,
    val questionText: String,
    val options: List<QuizOption>,
)

@Composable
fun MixedQuizPreviewScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSubmitQuizClick: (() -> Unit)? = null,
) {
    val selectedAnswers = remember { mutableStateMapOf<String, String>() }

    val mixedQuestions = listOf(
        MixedQuizItem(
            id = "1",
            subjectTag = "ARTIFICIAL INTELLIGENCE",
            subjectIcon = "🧠",
            questionText = "What is the main goal of gradient descent?",
            options = listOf(
                QuizOption("A", "A", "Increase model complexity"),
                QuizOption("B", "B", "Reduce prediction error / loss", true),
                QuizOption("C", "C", "Remove all training data"),
                QuizOption("D", "D", "Make every weight equal"),
            )
        ),
        MixedQuizItem(
            id = "2",
            subjectTag = "MATHEMATICS",
            subjectIcon = "🧮",
            questionText = "What are the roots of x² - 5x + 6 = 0?",
            options = listOf(
                QuizOption("A", "A", "x = 1, 6"),
                QuizOption("B", "B", "x = 2, 3", true),
                QuizOption("C", "C", "x = -2, -3"),
                QuizOption("D", "D", "x = 0, 5"),
            )
        ),
        MixedQuizItem(
            id = "3",
            subjectTag = "COMPUTER SCIENCE",
            subjectIcon = "</>",
            questionText = "What is the worst-case time complexity of Binary Search?",
            options = listOf(
                QuizOption("A", "A", "O(1)"),
                QuizOption("B", "B", "O(log n)", true),
                QuizOption("C", "C", "O(n)"),
                QuizOption("D", "D", "O(n²)"),
            )
        ),
        MixedQuizItem(
            id = "4",
            subjectTag = "DATA SCIENCE",
            subjectIcon = "📊",
            questionText = "Which chart type is best for showing sales trends over time?",
            options = listOf(
                QuizOption("A", "A", "Pie Chart"),
                QuizOption("B", "B", "Line Chart", true),
                QuizOption("C", "C", "Bar Chart"),
                QuizOption("D", "D", "Scatter Plot"),
            )
        ),
        MixedQuizItem(
            id = "5",
            subjectTag = "PHYSICS",
            subjectIcon = "⚛",
            questionText = "If a sound wave has f = 440 Hz and λ = 0.8 m, what is its speed v?",
            options = listOf(
                QuizOption("A", "A", "300 m/s"),
                QuizOption("B", "B", "352 m/s", true),
                QuizOption("C", "C", "400 m/s"),
                QuizOption("D", "D", "550 m/s"),
            )
        ),
        MixedQuizItem(
            id = "6",
            subjectTag = "CHEMISTRY",
            subjectIcon = "🧪",
            questionText = "What type of chemical bond forms between Na⁺ and Cl⁻ ions?",
            options = listOf(
                QuizOption("A", "A", "Covalent Bond"),
                QuizOption("B", "B", "Ionic Bond", true),
                QuizOption("C", "C", "Metallic Bond"),
                QuizOption("D", "D", "Hydrogen Bond"),
            )
        )
    )

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
            // Top Header Box
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
                        Text(text = "ALL SUBJECTS · COMPREHENSIVE QUIZ", color = CyanGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Mixed Knowledge Challenge", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF161F42), CircleShape)
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "✕", color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
                    // Scrollable Questions Area
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Overview Progress Box
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "6 Mixed Questions · 6 Subjects",
                                        color = Color(0xFF0F172A),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "⏱ ", fontSize = 12.sp)
                                        Text(
                                            text = "05:00",
                                            color = BlueAccent,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                LinearProgressIndicator(
                                    progress = { 0.5f },
                                    modifier = Modifier.fillMaxWidth().height(6.dp),
                                    color = BlueAccent,
                                    trackColor = Color(0xFFE2E8F0),
                                )
                            }
                        }

                        // Mixed Question Cards List
                        mixedQuestions.forEachIndexed { qIdx, qItem ->
                            DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Subject Header Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFFEFF6FF), RoundedCornerShape(10.dp))
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = "${qItem.subjectIcon} ", fontSize = 11.sp)
                                                Text(
                                                    text = qItem.subjectTag,
                                                    color = BlueAccent,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 0.8.sp,
                                                )
                                            }
                                        }

                                        Text(
                                            text = "Q${qIdx + 1}",
                                            color = Color(0xFF64748B),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = qItem.questionText,
                                        color = Color(0xFF0F172A),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 20.sp,
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Options
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        qItem.options.forEach { opt ->
                                            val isSelected = selectedAnswers[qItem.id] == opt.id
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(if (isSelected) Color(0xFFEFF6FF) else Color.White, RoundedCornerShape(10.dp))
                                                    .border(width = if (isSelected) 1.5.dp else 1.dp, color = if (isSelected) BlueAccent else Color(0xFFE2E8F0), shape = RoundedCornerShape(10.dp))
                                                    .clickable { selectedAnswers[qItem.id] = opt.id }
                                                    .padding(10.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(26.dp)
                                                            .background(if (isSelected) BlueAccent else Color(0xFFF1F5F9), CircleShape),
                                                        contentAlignment = Alignment.Center,
                                                    ) {
                                                        Text(
                                                            text = opt.optionLabel,
                                                            color = if (isSelected) Color.White else Color(0xFF64748B),
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                        )
                                                    }

                                                    Spacer(modifier = Modifier.width(10.dp))

                                                    Text(
                                                        text = opt.text,
                                                        color = if (isSelected) BlueAccent else Color(0xFF1E293B),
                                                        fontSize = 12.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        modifier = Modifier.weight(1f),
                                                    )

                                                    if (isSelected) {
                                                        Text(text = "✓", color = BlueAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Bottom AI Note
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 6.dp) {
                            Text(
                                text = "✨ AI-generated mixed quiz · 6 Subjects Covered",
                                color = BlueAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Submit Button Box
                    DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 4.dp) {
                        Button(
                            onClick = { onSubmitQuizClick?.invoke() ?: onBackClick() },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Brush.horizontalGradient(listOf(PurpleAccent, BlueAccent, CyanAccent)), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(text = "Submit Mixed Quiz →", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
fun MixedQuizPreviewScreenPreview() {
    SYNAPSETheme {
        MixedQuizPreviewScreen(onBackClick = {}, modifier = Modifier.fillMaxSize())
    }
}
