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

private data class PrivacyPillar(
    val title: String,
    val badge: String,
    val icon: String,
    val description: String,
    val color: Color
)

@Composable
fun PrivacyPolicyScreen(
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

    val pillars = listOf(
        PrivacyPillar(
            title = "Zero Data Collection & Cloud Streaming",
            badge = "100% Offline",
            icon = "🚫",
            description = "SYNAPSE AI does not collect, log, track, or upload your academic chat prompts, voice recordings, quiz answers, or identity to remote servers. All neural model inference and retrieval operations run strictly inside your device sandbox.",
            color = Color(0xFF10B981)
        ),
        PrivacyPillar(
            title = "Encrypted Local On-Device Storage",
            badge = "Room & DataStore",
            icon = "💾",
            description = "Your notes, study categories, chapter progress, and theme settings are maintained exclusively in local Android Room Databases and Jetpack DataStore. No external database replication occurs without your explicit manual export.",
            color = CyanAccent
        ),
        PrivacyPillar(
            title = "Strict Microphone Permission Transparency",
            badge = "On-Device STT",
            icon = "🎙️",
            description = "The RECORD_AUDIO permission is requested solely to power live speech-to-text when you tap the voice button. Audio buffers are processed locally by the Android SpeechRecognizer and are instantly discarded without ever touching the network or disk.",
            color = BlueAccent
        ),
        PrivacyPillar(
            title = "Zero Third-Party Trackers & Ads",
            badge = "Ad-Free & Tracker-Free",
            icon = "🛡️",
            description = "There are no marketing SDKs, analytics tracking pixels, social media beacons, or telemetry libraries embedded within SYNAPSE AI. Your learning routines, strengths, and study habits belong only to you.",
            color = Color(0xFF8B5CF6)
        ),
        PrivacyPillar(
            title = "Full Student Data Sovereignty",
            badge = "Complete Control",
            icon = "🎛️",
            description = "You can wipe your ongoing chat history with one tap on the trash icon, delete custom created notes anytime, or completely clear cached assets through Settings > App Preferences > Clear Cache.",
            color = Color(0xFFF59E0B)
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
                            text = "Privacy Policy",
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
                        // Privacy Guarantee Banner
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) Color(0xFF06281E) else Color(0xFFECFDF5)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                Color(0xFF10B981).copy(alpha = 0.5f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = "🔒", fontSize = 20.sp)
                                    Text(
                                        text = "Our Privacy Guarantee",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (isDark) Color(0xFF34D399) else Color(0xFF065F46)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "SYNAPSE AI operates with strict offline-first integrity. We do not sell, rent, monetize, or harvest student queries. What you learn stays on your device.",
                                    fontSize = 12.sp,
                                    color = if (isDark) Color(0xFFA7F3D0) else Color(0xFF047857),
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        // Section Header
                        Text(
                            text = "CORE PRIVACY PRINCIPLES",
                            color = if (isDark) CyanAccent else BlueAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        // Privacy Pillars
                        pillars.forEach { pillar ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(
                                                        pillar.color.copy(alpha = if (isDark) 0.18f else 0.12f),
                                                        CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = pillar.icon, fontSize = 18.sp)
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Text(
                                                text = pillar.title,
                                                color = primaryText,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    pillar.color.copy(alpha = 0.15f),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = pillar.badge,
                                                color = pillar.color,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = pillar.description,
                                        color = secondaryText,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }

                        // Summary Note
                        Text(
                            text = "Effective: October 2026 • Synapse AI Privacy Engineering Team",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

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
                                text = "Back to About Synapse",
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
fun PrivacyPolicyScreenPreview() {
    SYNAPSETheme {
        PrivacyPolicyScreen(onBackClick = {}, modifier = Modifier.fillMaxSize())
    }
}

