package com.siamts.synapse.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siamts.synapse.ui.theme.AmberGold
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.SunsetOrange
import com.siamts.synapse.ui.theme.SunsetOrangeEnd
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

    // Smooth navigation after ~1.8 seconds to HomeScreen (if logged in) or LoginScreen (if logged out)
    LaunchedEffect(Unit) {
        delay(1800L)
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
    val scale = remember { Animatable(if (isAnimated) 0.85f else 1f) }
    val alpha = remember { Animatable(if (isAnimated) 0f else 1f) }

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
                animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "SplashAnimations")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "PulseScale"
    )

    val starAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "StarAlpha"
    )

    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1750, easing = FastOutSlowInEasing),
        )
    }

    // Deep charcoal-slate background (#181A20)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF181A20)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale.value * pulse)
                .alpha(alpha.value)
                .padding(horizontal = 24.dp)
        ) {
            // Glowing Circuit-Brain Canvas with surrounding 4-point sparkle stars
            CircuitBrainEmblem(
                starAlpha = starAlpha,
                modifier = Modifier.size(190.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Brand Typography: Glowing "SYNAPSE" in White + "AI" in Sunset Orange
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "SYNAPSE",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI",
                    color = Color(0xFFFF6B00), // Sunset Orange
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle tag
            Text(
                text = "NEURAL STUDY COMPANION",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )
        }

        // Minimal glowing bottom progress indicator
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 36.dp, start = 64.dp, end = 64.dp)
                .fillMaxWidth()
                .height(2.5.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0x22FFFFFF))
                .alpha(alpha.value),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.value)
                    .height(2.5.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                SunsetOrange,
                                AmberGold,
                                SunsetOrangeEnd
                            )
                        )
                    )
            )
        }
    }
}

/**
 * Custom Canvas drawing the minimal glowing golden-amber brain circuit emblem
 * surrounded by 4-point sparkle stars.
 */
@Composable
fun CircuitBrainEmblem(
    starAlpha: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val r = minOf(w, h) * 0.44f

        // 1. Subtle radial warm-amber glow behind the emblem
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x55FFAA00),
                    Color(0x22FF5500),
                    Color(0x05FF5500),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = r * 1.55f
            )
        )

        // 2. 4-Point Sparkle Stars surrounding the emblem
        val goldStar = Color(0xFFFFB03A).copy(alpha = starAlpha)
        val brightStar = Color(0xFFFFE082).copy(alpha = starAlpha)

        drawSparkleStar(Offset(w * 0.14f, h * 0.20f), 12f, goldStar)
        drawSparkleStar(Offset(w * 0.86f, h * 0.18f), 14f, brightStar)
        drawSparkleStar(Offset(w * 0.16f, h * 0.82f), 11f, brightStar)
        drawSparkleStar(Offset(w * 0.84f, h * 0.80f), 13f, goldStar)
        drawSparkleStar(Offset(w * 0.50f, h * 0.08f), 9f, goldStar)

        // 3. Subtle Outer Segmented Orbital Ring
        val strokeRing = Stroke(width = 1.5.dp.toPx())
        drawArc(
            color = Color(0x33FFB03A),
            startAngle = 25f,
            sweepAngle = 130f,
            useCenter = false,
            topLeft = Offset(cx - r * 1.15f, cy - r * 1.15f),
            size = androidx.compose.ui.geometry.Size(r * 2.3f, r * 2.3f),
            style = strokeRing
        )
        drawArc(
            color = Color(0x33FFB03A),
            startAngle = 205f,
            sweepAngle = 130f,
            useCenter = false,
            topLeft = Offset(cx - r * 1.15f, cy - r * 1.15f),
            size = androidx.compose.ui.geometry.Size(r * 2.3f, r * 2.3f),
            style = strokeRing
        )

        // 4. Brain Circuit Traces Gradient: Golden-Amber (#FFB03A to #FF5500)
        val circuitBrush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFB03A),
                Color(0xFFFF7A00),
                Color(0xFFFF5500)
            ),
            start = Offset(cx, cy - r),
            end = Offset(cx, cy + r)
        )
        val traceStroke = Stroke(
            width = 2.4.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        // Left Hemisphere Outer Contour
        val leftHemisphere = Path().apply {
            moveTo(cx - 3.dp.toPx(), cy - r * 0.82f)
            cubicTo(
                cx - r * 0.55f, cy - r * 0.95f,
                cx - r * 0.90f, cy - r * 0.60f,
                cx - r * 0.85f, cy - r * 0.20f
            )
            cubicTo(
                cx - r * 0.95f, cy + r * 0.15f,
                cx - r * 0.70f, cy + r * 0.65f,
                cx - r * 0.35f, cy + r * 0.82f
            )
            lineTo(cx - 3.dp.toPx(), cy + r * 0.82f)
        }
        drawPath(leftHemisphere, circuitBrush, style = traceStroke)

        // Right Hemisphere Outer Contour (Mirrored)
        val rightHemisphere = Path().apply {
            moveTo(cx + 3.dp.toPx(), cy - r * 0.82f)
            cubicTo(
                cx + r * 0.55f, cy - r * 0.95f,
                cx + r * 0.90f, cy - r * 0.60f,
                cx + r * 0.85f, cy - r * 0.20f
            )
            cubicTo(
                cx + r * 0.95f, cy + r * 0.15f,
                cx + r * 0.70f, cy + r * 0.65f,
                cx + r * 0.35f, cy + r * 0.82f
            )
            lineTo(cx + 3.dp.toPx(), cy + r * 0.82f)
        }
        drawPath(rightHemisphere, circuitBrush, style = traceStroke)

        // Center Neural Fissure (Dividing Line)
        drawLine(
            brush = circuitBrush,
            start = Offset(cx, cy - r * 0.75f),
            end = Offset(cx, cy + r * 0.75f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Internal Circuit Lines — Left Hemisphere
        val leftInternal = Path().apply {
            // Upper frontal circuit
            moveTo(cx, cy - r * 0.50f)
            lineTo(cx - r * 0.35f, cy - r * 0.50f)
            lineTo(cx - r * 0.55f, cy - r * 0.35f)

            // Middle sensory circuit
            moveTo(cx, cy - r * 0.10f)
            lineTo(cx - r * 0.40f, cy - r * 0.10f)
            lineTo(cx - r * 0.65f, cy + r * 0.05f)

            // Lower temporal/occipital circuit
            moveTo(cx, cy + r * 0.35f)
            lineTo(cx - r * 0.35f, cy + r * 0.35f)
            lineTo(cx - r * 0.50f, cy + r * 0.55f)
        }
        drawPath(leftInternal, circuitBrush, style = traceStroke)

        // Internal Circuit Lines — Right Hemisphere
        val rightInternal = Path().apply {
            // Upper frontal circuit
            moveTo(cx, cy - r * 0.50f)
            lineTo(cx + r * 0.35f, cy - r * 0.50f)
            lineTo(cx + r * 0.55f, cy - r * 0.35f)

            // Middle sensory circuit
            moveTo(cx, cy - r * 0.10f)
            lineTo(cx + r * 0.40f, cy - r * 0.10f)
            lineTo(cx + r * 0.65f, cy + r * 0.05f)

            // Lower temporal/occipital circuit
            moveTo(cx, cy + r * 0.35f)
            lineTo(cx + r * 0.35f, cy + r * 0.35f)
            lineTo(cx + r * 0.50f, cy + r * 0.55f)
        }
        drawPath(rightInternal, circuitBrush, style = traceStroke)

        // 5. Glowing Circuit Nodes (Filled circles with luminous halos)
        val nodes = listOf(
            Offset(cx - r * 0.55f, cy - r * 0.35f),
            Offset(cx - r * 0.65f, cy + r * 0.05f),
            Offset(cx - r * 0.50f, cy + r * 0.55f),
            Offset(cx + r * 0.55f, cy - r * 0.35f),
            Offset(cx + r * 0.65f, cy + r * 0.05f),
            Offset(cx + r * 0.50f, cy + r * 0.55f),
            Offset(cx - r * 0.35f, cy + r * 0.82f),
            Offset(cx + r * 0.35f, cy + r * 0.82f),
            Offset(cx, cy - r * 0.75f),
            Offset(cx, cy)
        )

        for (node in nodes) {
            // Luminous halo
            drawCircle(
                color = Color(0x66FFAA00),
                radius = 5.5.dp.toPx(),
                center = node
            )
            // Core node
            drawCircle(
                color = Color(0xFFFFE082),
                radius = 2.8.dp.toPx(),
                center = node
            )
        }
    }
}

/**
 * Draws a sharp 4-point sparkle star centered at the specified offset.
 */
private fun DrawScope.drawSparkleStar(center: Offset, size: Float, color: Color) {
    val path = Path().apply {
        moveTo(center.x, center.y - size)
        cubicTo(center.x, center.y, center.x, center.y, center.x + size, center.y)
        cubicTo(center.x, center.y, center.x, center.y, center.x, center.y + size)
        cubicTo(center.x, center.y, center.x, center.y, center.x - size, center.y)
        cubicTo(center.x, center.y, center.x, center.y, center.x, center.y - size)
        close()
    }
    drawPath(path, color)
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
