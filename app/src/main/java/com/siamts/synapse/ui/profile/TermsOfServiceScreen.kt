package com.siamts.synapse.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.siamts.synapse.ui.theme.DarkBackground
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.LightBackground
import com.siamts.synapse.ui.theme.LightBorder
import com.siamts.synapse.ui.theme.LightTextPrimary
import com.siamts.synapse.ui.theme.LightTextSecondary
import com.siamts.synapse.ui.theme.LocalThemeIsDark
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

private data class TermsSection(
    val title: String,
    val icon: String,
    val content: String
)

@Composable
fun TermsOfServiceScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = LocalThemeIsDark.current

    val pageBgGradient = if (isDark) {
        listOf(DarkBackgroundTop, Color(0xFF090D22), DarkBackgroundBottom)
    } else {
        listOf(Color(0xFFE0E7FF), Color(0xFFEEF2F6), LightBackground)
    }

    val bodyBg = if (isDark) DarkBackground else Color(0xFFF8FAFC)
    val cardBg = if (isDark) Color(0xFF131C38) else Color.White
    val cardBorder = if (isDark) Color(0xFF1E2B58) else LightBorder
    val primaryText = if (isDark) TextPrimary else LightTextPrimary
    val secondaryText = if (isDark) Color(0xFF94A3B8) else LightTextSecondary

    val sections = listOf(
        TermsSection(
            title = "1. Acceptance of Terms",
            icon = "📜",
            content = "By downloading, installing, or accessing SYNAPSE AI ('the Application'), you agree to be bound by these Terms of Service. This platform is engineered specifically for students, educators, and lifelong learners for personal, academic, and non-commercial educational purposes. If you do not agree to these terms, please discontinue use of the application."
        ),
        TermsSection(
            title = "2. 100% Local Data Ownership",
            icon = "🔒",
            content = "You retain complete, exclusive ownership of all study notes, generated materials, bookmarked chapters, and conversation logs created using SYNAPSE AI. All user assets are stored strictly in local on-device SQLite / Room databases and encrypted Jetpack DataStore preferences on your Android device. SYNAPSE AI does not transmit, monetize, upload, or claim intellectual property rights over any notes or queries you produce."
        ),
        TermsSection(
            title = "3. Academic & Educational Disclaimer",
            icon = "🎓",
            content = "SYNAPSE AI functions as an offline neural study companion and pedagogical assistant. While our curriculum datasets and generative responses are curated with rigorous academic standards, the content is intended as a supplementary learning aid. Students are encouraged to cross-reference solutions, formulas, and historical dates with their accredited textbooks, professors, and institutional syllabus guidelines."
        ),
        TermsSection(
            title = "4. Responsible AI Usage & Prohibited Conduct",
            icon = "⚖️",
            content = "Users agree to utilize SYNAPSE AI in an ethical, honorable manner. You agree not to: (a) attempt reverse engineering, binary tampering, or unauthorized decompilation of on-device neural model weights; (b) leverage the application for automated academic dishonesty or exam cheating; or (c) inject or prompt harmful, malicious, or abusive materials into the conversation engine."
        ),
        TermsSection(
            title = "5. Offline Availability & Software Updates",
            icon = "📶",
            content = "SYNAPSE AI is built to provide uninterrupted study sessions without requiring an active internet connection. Periodic software updates may introduce new subject catalogs, performance improvements, and diagnostic tools. We commit to maintaining transparent update release notes without retroactively altering your offline rights or local ownership."
        ),
        TermsSection(
            title = "6. Limitation of Liability",
            icon = "🛡️",
            content = "SYNAPSE AI is provided on an 'as-is' and 'as-available' basis without warranties of any kind, whether express or implied. Under no circumstances shall the developers be liable for direct, indirect, or incidental educational outcomes, test scores, or hardware malfunctions arising from the use of this software."
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = pageBgGradient))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar Box
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
                            color = primaryText,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "LEGAL & COMPLIANCE",
                            color = if (isDark) CyanAccent else BlueAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Terms of Service",
                            color = primaryText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Main Content Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = bodyBg,
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
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
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Overview Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) Color(0xFF0F1A3A) else Color(0xFFEFF6FF)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                BlueAccent.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = "📜", fontSize = 20.sp)
                                    Text(
                                        text = "SYNAPSE AI Terms of Use",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = primaryText
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Last revised: October 2026 • Version 1.0.0\nPlease read these terms carefully before engaging with the SYNAPSE educational ecosystem.",
                                    fontSize = 12.sp,
                                    color = secondaryText,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        // Structured Term Sections
                        sections.forEach { section ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(
                                                    BlueAccent.copy(alpha = if (isDark) 0.18f else 0.12f),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = section.icon, fontSize = 18.sp)
                                        }

                                        Text(
                                            text = section.title,
                                            color = primaryText,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = section.content,
                                        color = secondaryText,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }

                        // Support Contact Banner
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) Color(0xFF131C38) else Color(0xFFF1F5F9)
                            )
                        ) {
                            Text(
                                text = "Have questions regarding our terms or academic policies? Reach out to support via Profile > Help & Support.",
                                color = secondaryText,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            )
                        }

                        // Bottom Back Button
                        Button(
                            onClick = onBackClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BlueAccent)
                        ) {
                            Text(
                                text = "I Understand & Accept",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TermsOfServiceScreenPreview() {
    SYNAPSETheme {
        TermsOfServiceScreen(onBackClick = {}, modifier = Modifier.fillMaxSize())
    }
}

