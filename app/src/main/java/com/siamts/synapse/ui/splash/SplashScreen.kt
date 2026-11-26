package com.siamts.synapse.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siamts.synapse.ui.theme.SYNAPSETheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onSplashFinished: (isAuthenticated: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isNavigating) {
        if (uiState.isNavigating) {
            onSplashFinished(uiState.isAuthenticated)
        }
    }

    // Clean navigation after 1.6 seconds with zero flash
    LaunchedEffect(Unit) {
        delay(1600L)
        if (!uiState.isNavigating) {
            onSplashFinished(viewModel.uiState.value.isAuthenticated)
        }
    }

    SplashContent(
        uiState = uiState,
        modifier = modifier,
    )
}

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SplashScreen(
        viewModel = viewModel,
        onSplashFinished = { _ -> onSplashFinished() },
        modifier = modifier,
    )
}

@Composable
fun SplashContent(
    uiState: SplashUiState,
    modifier: Modifier = Modifier,
    isAnimated: Boolean = true,
) {
    val scale = remember { Animatable(if (isAnimated) 0.92f else 1f) }
    val alpha = remember { Animatable(if (isAnimated) 0f else 1f) }
    val progressAnim = remember { Animatable(if (isAnimated) 0f else 1f) }

    LaunchedEffect(isAnimated) {
        if (isAnimated) {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
            )
        }
    }

    LaunchedEffect(isAnimated) {
        if (isAnimated) {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            )
        }
    }

    LaunchedEffect(isAnimated) {
        if (isAnimated) {
            progressAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1500, easing = LinearOutSlowInEasing),
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "SplashAnimations")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "PulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0E1015)),
        contentAlignment = Alignment.Center,
    ) {
        // Warm radial background glow centered behind emblem
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f - 40.dp.toPx()
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF7A00).copy(alpha = 0.16f * pulse),
                        Color(0xFFFF7A00).copy(alpha = 0.05f * pulse),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = 220.dp.toPx()
                ),
                radius = 220.dp.toPx(),
                center = Offset(cx, cy)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
                .padding(horizontal = 24.dp)
        ) {
            // Glowing Circuit Brain with Center </> Code Bracket
            CircuitBrainCanvas(
                modifier = Modifier.size(180.dp),
                pulse = pulse
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Brand Typography: "SYNAPSE AI"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "SYNAPSE",
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI",
                    color = Color(0xFFFF7A00),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle: "Neural Study Companion"
            Text(
                text = "Neural Study Companion",
                color = Color(0xFFB0B5C3),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Loading state & Slim animated progress bar
            Text(
                text = "Loading...",
                color = Color(0xFF8A90A2),
                fontSize = 13.sp,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF222630))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressAnim.value)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFFB03A), Color(0xFFFF6B00))
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun CircuitBrainCanvas(
    modifier: Modifier = Modifier,
    pulse: Float = 1f
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        val strokeGrad = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFB03A), // Amber Gold
                Color(0xFFFF7A00), // Sunset Orange
                Color(0xFFFF5500)  // Deep Orange
            ),
            startY = 0f,
            endY = h
        )
        val glowColor = Color(0xFFFF7A00).copy(alpha = (0.28f * pulse).coerceIn(0.15f, 0.45f))
        val dotGlowColor = Color(0xFFFF9D2E).copy(alpha = (0.35f * pulse).coerceIn(0.20f, 0.55f))

        val strokeWidth = 2.6.dp.toPx()
        val glowWidth = 6.2.dp.toPx()
        val nodeRadius = 3.2.dp.toPx()
        val nodeGlowRadius = 5.8.dp.toPx()

        // 1. Brain Hemispheres Paths (Left and Right with Anatomical Sulcal Folds)
        val leftBrain = Path().apply {
            moveTo(cx - w * 0.04f, cy - h * 0.42f)
            // Frontal top lobe curve
            cubicTo(
                cx - w * 0.20f, cy - h * 0.47f,
                cx - w * 0.38f, cy - h * 0.40f,
                cx - w * 0.45f, cy - h * 0.26f
            )
            // Parietal lobe bump
            cubicTo(
                cx - w * 0.50f, cy - h * 0.18f,
                cx - w * 0.48f, cy - h * 0.06f,
                cx - w * 0.46f, cy + h * 0.02f
            )
            // Temporal lobe curve
            cubicTo(
                cx - w * 0.50f, cy + h * 0.12f,
                cx - w * 0.45f, cy + h * 0.24f,
                cx - w * 0.36f, cy + h * 0.34f
            )
            // Cerebellum & brainstem base
            cubicTo(
                cx - w * 0.30f, cy + h * 0.43f,
                cx - w * 0.18f, cy + h * 0.45f,
                cx - w * 0.10f, cy + h * 0.40f
            )
            // Return to midline stem
            cubicTo(
                cx - w * 0.06f, cy + h * 0.36f,
                cx - w * 0.04f, cy + h * 0.28f,
                cx - w * 0.04f, cy + h * 0.20f
            )
        }

        val rightBrain = Path().apply {
            moveTo(cx + w * 0.04f, cy - h * 0.42f)
            // Frontal top lobe curve
            cubicTo(
                cx + w * 0.20f, cy - h * 0.47f,
                cx + w * 0.38f, cy - h * 0.40f,
                cx + w * 0.45f, cy - h * 0.26f
            )
            // Parietal lobe bump
            cubicTo(
                cx + w * 0.50f, cy - h * 0.18f,
                cx + w * 0.48f, cy - h * 0.06f,
                cx + w * 0.46f, cy + h * 0.02f
            )
            // Temporal lobe curve
            cubicTo(
                cx + w * 0.50f, cy + h * 0.12f,
                cx + w * 0.45f, cy + h * 0.24f,
                cx + w * 0.36f, cy + h * 0.34f
            )
            // Cerebellum & brainstem base
            cubicTo(
                cx + w * 0.30f, cy + h * 0.43f,
                cx + w * 0.18f, cy + h * 0.45f,
                cx + w * 0.10f, cy + h * 0.40f
            )
            // Return to midline stem
            cubicTo(
                cx + w * 0.06f, cy + h * 0.36f,
                cx + w * 0.04f, cy + h * 0.28f,
                cx + w * 0.04f, cy + h * 0.20f
            )
        }

        // Draw Brain Outer Glow & Crisp Stroke
        drawPath(leftBrain, color = glowColor, style = Stroke(width = glowWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(leftBrain, brush = strokeGrad, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(rightBrain, color = glowColor, style = Stroke(width = glowWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(rightBrain, brush = strokeGrad, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // 2. Center Code Symbol: </>
        val codeSymbolPath = Path().apply {
            // Left Bracket: <
            moveTo(cx - w * 0.10f, cy - h * 0.14f)
            lineTo(cx - w * 0.20f, cy)
            lineTo(cx - w * 0.10f, cy + h * 0.14f)

            // Center Slash: /
            moveTo(cx + w * 0.035f, cy - h * 0.17f)
            lineTo(cx - w * 0.035f, cy + h * 0.17f)

            // Right Bracket: >
            moveTo(cx + w * 0.10f, cy - h * 0.14f)
            lineTo(cx + w * 0.20f, cy)
            lineTo(cx + w * 0.10f, cy + h * 0.14f)
        }

        drawPath(codeSymbolPath, color = glowColor, style = Stroke(width = glowWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(codeSymbolPath, brush = strokeGrad, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // 3. Internal PCB Circuit Traces
        val circuitTraces = Path().apply {
            // Left Hemisphere Traces
            // Upper frontal trace
            moveTo(cx - w * 0.09f, cy - h * 0.26f)
            lineTo(cx - w * 0.22f, cy - h * 0.26f)
            lineTo(cx - w * 0.32f, cy - h * 0.32f)

            // Upper sulcus trace
            moveTo(cx - w * 0.22f, cy - h * 0.26f)
            lineTo(cx - w * 0.22f, cy - h * 0.16f)
            lineTo(cx - w * 0.34f, cy - h * 0.16f)

            // Mid horizontal branch
            moveTo(cx - w * 0.22f, cy - h * 0.02f)
            lineTo(cx - w * 0.34f, cy - h * 0.02f)
            lineTo(cx - w * 0.39f, cy + h * 0.06f)

            // Lower temporal trace
            moveTo(cx - w * 0.12f, cy + h * 0.18f)
            lineTo(cx - w * 0.24f, cy + h * 0.18f)
            lineTo(cx - w * 0.32f, cy + h * 0.26f)

            // Lower occipital stem trace
            moveTo(cx - w * 0.08f, cy + h * 0.28f)
            lineTo(cx - w * 0.16f, cy + h * 0.34f)
            lineTo(cx - w * 0.22f, cy + h * 0.34f)

            // Right Hemisphere Traces
            // Upper frontal trace
            moveTo(cx + w * 0.09f, cy - h * 0.26f)
            lineTo(cx + w * 0.22f, cy - h * 0.26f)
            lineTo(cx + w * 0.32f, cy - h * 0.32f)

            // Upper sulcus trace
            moveTo(cx + w * 0.22f, cy - h * 0.26f)
            lineTo(cx + w * 0.22f, cy - h * 0.16f)
            lineTo(cx + w * 0.34f, cy - h * 0.16f)

            // Mid horizontal branch
            moveTo(cx + w * 0.22f, cy - h * 0.02f)
            lineTo(cx + w * 0.34f, cy - h * 0.02f)
            lineTo(cx + w * 0.39f, cy + h * 0.06f)

            // Lower temporal trace
            moveTo(cx + w * 0.12f, cy + h * 0.18f)
            lineTo(cx + w * 0.24f, cy + h * 0.18f)
            lineTo(cx + w * 0.32f, cy + h * 0.26f)

            // Lower occipital stem trace
            moveTo(cx + w * 0.08f, cy + h * 0.28f)
            lineTo(cx + w * 0.16f, cy + h * 0.34f)
            lineTo(cx + w * 0.22f, cy + h * 0.34f)
        }

        drawPath(circuitTraces, color = glowColor, style = Stroke(width = glowWidth * 0.8f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(circuitTraces, brush = strokeGrad, style = Stroke(width = strokeWidth * 0.9f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // 4. Circular Node Endpoints (Solder / Synapse Nodes)
        val nodePoints = listOf(
            // Left nodes
            Offset(cx - w * 0.32f, cy - h * 0.32f),
            Offset(cx - w * 0.34f, cy - h * 0.16f),
            Offset(cx - w * 0.39f, cy + h * 0.06f),
            Offset(cx - w * 0.32f, cy + h * 0.26f),
            Offset(cx - w * 0.22f, cy + h * 0.34f),
            // Right nodes
            Offset(cx + w * 0.32f, cy - h * 0.32f),
            Offset(cx + w * 0.34f, cy - h * 0.16f),
            Offset(cx + w * 0.39f, cy + h * 0.06f),
            Offset(cx + w * 0.32f, cy + h * 0.26f),
            Offset(cx + w * 0.22f, cy + h * 0.34f)
        )

        for (pt in nodePoints) {
            drawCircle(color = dotGlowColor, radius = nodeGlowRadius, center = pt)
            drawCircle(brush = strokeGrad, radius = nodeRadius, center = pt)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    SYNAPSETheme {
        SplashContent(
            uiState = SplashUiState(),
            modifier = Modifier.fillMaxSize(),
            isAnimated = false,
        )
    }
}
