package com.siamts.synapse.ui.tutor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siamts.synapse.ai.VoiceState
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.PurpleAccent
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Full-screen, GPU-accelerated Live Voice Chat Mode inspired by Gemini Live and ChatGPT Voice.
 * Features an animated multi-layered neural plasma orb reacting to live microphone RMS dB levels,
 * real-time user speech transcription, and live spoken AI responses.
 */
@Composable
fun LiveVoiceChatOverlay(
    isVisible: Boolean,
    voiceState: VoiceState,
    audioLevel: Float,
    userSpokenText: String,
    aiSpokenText: String,
    onTogglePauseResume: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(300)) + slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(350)),
        exit = fadeOut(tween(250)) + slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300))
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF070B14))
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Subtle ambient neon gradient backdrop
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF1E1B4B).copy(alpha = 0.5f),
                                Color(0xFF0F172A).copy(alpha = 0.7f),
                                Color(0xFF070B14)
                            ),
                            center = Offset(500f, 600f),
                            radius = 900f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Navigation & Live Mode Indicator Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xFF111827).copy(alpha = 0.85f), RoundedCornerShape(20.dp))
                            .border(1.dp, Color(0xFF1F2937), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        val dotColor = when (voiceState) {
                            VoiceState.LISTENING -> Color(0xFF06B6D4) // Cyan
                            VoiceState.THINKING -> Color(0xFFA855F7) // Violet
                            VoiceState.SPEAKING -> Color(0xFF10B981) // Emerald
                            VoiceState.PAUSED -> Color(0xFFF59E0B) // Amber
                            VoiceState.ERROR -> Color(0xFFEF4444) // Red
                            VoiceState.IDLE -> Color(0xFF94A3B8)
                        }

                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(dotColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (voiceState) {
                                VoiceState.LISTENING -> "LIVE · LISTENING"
                                VoiceState.THINKING -> "LIVE · THINKING"
                                VoiceState.SPEAKING -> "LIVE · SPEAKING"
                                VoiceState.PAUSED -> "PAUSED"
                                VoiceState.ERROR -> "MIC ERROR"
                                VoiceState.IDLE -> "READY"
                            },
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    // Model Badge
                    Text(
                        text = "SYNAPSE NEURAL LIVE",
                        color = CyanGlow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }

                // Center Dynamic Animated Neural Voice Orb (Audio-Reactive)
                Box(
                    modifier = Modifier
                        .size(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    NeuralVoiceOrb(
                        voiceState = voiceState,
                        audioLevel = audioLevel,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Live Real-Time Captions Container
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .height(180.dp)
                        .background(Color(0xFF0F172A).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (userSpokenText.isNotBlank()) {
                        Text(
                            text = "“$userSpokenText”",
                            color = Color(0xFFE2E8F0),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }

                    if (aiSpokenText.isNotBlank() && aiSpokenText != "Tap or speak to ask anything...") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = aiSpokenText,
                            color = CyanAccent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    } else if (userSpokenText.isBlank()) {
                        Text(
                            text = "Speak naturally. SYNAPSE AI is listening...",
                            color = Color(0xFF94A3B8),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Action Control Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Mode info or spacer
                    Text(
                        text = "100% On-Device",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    // Center: Glowing Mic / Pause Toggle Button
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = if (voiceState == VoiceState.PAUSED) {
                                        listOf(Color(0xFF475569), Color(0xFF334155))
                                    } else {
                                        listOf(CyanAccent, PurpleAccent)
                                    }
                                )
                            )
                            .clickable { onTogglePauseResume() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (voiceState == VoiceState.PAUSED) "▶" else "❚❚",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Right: Close / End Voice session button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E293B).copy(alpha = 0.8f))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                            .clickable { onClose() }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✕", color = Color(0xFFEF4444), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "End Voice",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * High-performance, GPU-accelerated animated Canvas drawing a multi-layered breathing
 * neural orb that organically pulses with audio volume and shifts colors based on AI state.
 */
@Composable
fun NeuralVoiceOrb(
    voiceState: VoiceState,
    audioLevel: Float,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "orb_animation")

    // Slow organic continuous rotation
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Breathing pulse
    val breathingPulse by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    // Wave ring ripple 1
    val ripple1 by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple1"
    )

    // Wave ring ripple 2
    val ripple2 by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, delayMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple2"
    )

    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val baseRadius = size.minDimension * 0.28f

        // Audio reactivity multiplier (0.0 to 1.0 audio level pushes the orb outwards)
        val dynamicScale = when (voiceState) {
            VoiceState.LISTENING -> breathingPulse + (audioLevel * 0.45f)
            VoiceState.SPEAKING -> breathingPulse + (audioLevel.coerceAtLeast(0.35f) * 0.35f)
            VoiceState.THINKING -> breathingPulse * 0.92f
            VoiceState.PAUSED -> 0.90f
            else -> 1f
        }

        val primaryColor = when (voiceState) {
            VoiceState.LISTENING -> Color(0xFF00F5FF) // Electric Cyan
            VoiceState.THINKING -> Color(0xFFA855F7) // Mystic Violet
            VoiceState.SPEAKING -> Color(0xFF10B981) // Emerald Turquoise
            VoiceState.PAUSED -> Color(0xFFF59E0B) // Amber
            VoiceState.ERROR -> Color(0xFFEF4444)
            VoiceState.IDLE -> Color(0xFF38BDF8)
        }

        val secondaryColor = when (voiceState) {
            VoiceState.LISTENING -> Color(0xFF3B82F6) // Deep Royal Blue
            VoiceState.THINKING -> Color(0xFF6366F1) // Indigo
            VoiceState.SPEAKING -> Color(0xFF06B6D4) // Cyan
            VoiceState.PAUSED -> Color(0xFFB45309)
            VoiceState.ERROR -> Color(0xFF991B1B)
            VoiceState.IDLE -> Color(0xFF6366F1)
        }

        // 1. Ambient outer diffuse glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.35f * (dynamicScale / 1.2f).coerceIn(0.2f, 0.7f)),
                    secondaryColor.copy(alpha = 0.12f),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = baseRadius * 2.3f * dynamicScale
            ),
            radius = baseRadius * 2.3f * dynamicScale,
            center = Offset(centerX, centerY)
        )

        // 2. Animated ripple sound waves (active when speaking/listening)
        if (voiceState == VoiceState.LISTENING || voiceState == VoiceState.SPEAKING) {
            val rippleAlpha1 = ((1.4f - ripple1) / 0.6f).coerceIn(0f, 1f) * 0.45f
            drawCircle(
                color = primaryColor.copy(alpha = rippleAlpha1),
                radius = baseRadius * ripple1 * dynamicScale,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2.5f)
            )

            val rippleAlpha2 = ((1.4f - ripple2) / 0.6f).coerceIn(0f, 1f) * 0.45f
            drawCircle(
                color = secondaryColor.copy(alpha = rippleAlpha2),
                radius = baseRadius * ripple2 * dynamicScale,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2.0f)
            )
        }

        // 3. Rotating orbital particle ring
        val ringCount = 12
        for (i in 0 until ringCount) {
            val angleRad = ((i * (360f / ringCount) + rotation) * (PI / 180f)).toFloat()
            val ringRadius = baseRadius * 1.35f * dynamicScale
            val dotX = centerX + ringRadius * cos(angleRad)
            val dotY = centerY + ringRadius * sin(angleRad)
            drawCircle(
                color = if (i % 2 == 0) primaryColor.copy(alpha = 0.7f) else secondaryColor.copy(alpha = 0.7f),
                radius = 3.5f,
                center = Offset(dotX, dotY)
            )
        }

        // 4. Middle luminous plasma orb
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.85f),
                    primaryColor.copy(alpha = 0.80f),
                    secondaryColor.copy(alpha = 0.60f),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = baseRadius * 1.15f * dynamicScale
            ),
            radius = baseRadius * 1.15f * dynamicScale,
            center = Offset(centerX, centerY)
        )

        // 5. Vibrant inner core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White,
                    primaryColor,
                    secondaryColor
                ),
                center = Offset(centerX - (baseRadius * 0.15f), centerY - (baseRadius * 0.15f)),
                radius = baseRadius * 0.82f * dynamicScale
            ),
            radius = baseRadius * 0.82f * dynamicScale,
            center = Offset(centerX, centerY)
        )
    }
}

