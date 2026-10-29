package com.siamts.synapse.ui.welcome.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.PurpleGlow

/**
 * Custom Canvas composable that renders a glowing futuristic AI brain with neural network pathways and pulsing synapse nodes.
 */
@Composable
fun BrainGraphic(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "brainPulse")
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val centerY = height / 2f
        val scale = width.coerceAtMost(height) * 0.38f

        // Center Glow Radial Gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    CyanGlow.copy(alpha = 0.25f * pulseAnim),
                    PurpleGlow.copy(alpha = 0.15f * pulseAnim),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = scale * 1.5f
            ),
            radius = scale * 1.5f,
            center = Offset(centerX, centerY)
        )

        // Brain Hemisphere Lobe Paths
        val leftBrain = Path().apply {
            moveTo(centerX - scale * 0.05f, centerY - scale * 0.85f)
            cubicTo(
                centerX - scale * 0.5f, centerY - scale * 0.95f,
                centerX - scale * 0.95f, centerY - scale * 0.5f,
                centerX - scale * 0.9f, centerY - scale * 0.05f
            )
            cubicTo(
                centerX - scale * 1.0f, centerY + scale * 0.35f,
                centerX - scale * 0.7f, centerY + scale * 0.8f,
                centerX - scale * 0.3f, centerY + scale * 0.85f
            )
            cubicTo(
                centerX - scale * 0.15f, centerY + scale * 0.88f,
                centerX - scale * 0.05f, centerY + scale * 0.7f,
                centerX - scale * 0.05f, centerY + scale * 0.5f
            )
            close()
        }

        val rightBrain = Path().apply {
            moveTo(centerX + scale * 0.05f, centerY - scale * 0.85f)
            cubicTo(
                centerX + scale * 0.5f, centerY - scale * 0.95f,
                centerX + scale * 0.95f, centerY - scale * 0.5f,
                centerX + scale * 0.9f, centerY - scale * 0.05f
            )
            cubicTo(
                centerX + scale * 1.0f, centerY + scale * 0.35f,
                centerX + scale * 0.7f, centerY + scale * 0.8f,
                centerX + scale * 0.3f, centerY + scale * 0.85f
            )
            cubicTo(
                centerX + scale * 0.15f, centerY + scale * 0.88f,
                centerX + scale * 0.05f, centerY + scale * 0.7f,
                centerX + scale * 0.05f, centerY + scale * 0.5f
            )
            close()
        }

        // Draw Lobe Outlines
        drawPath(
            path = leftBrain,
            color = CyanGlow.copy(alpha = 0.5f),
            style = Stroke(width = 3f)
        )
        drawPath(
            path = rightBrain,
            color = PurpleGlow.copy(alpha = 0.5f),
            style = Stroke(width = 3f)
        )

        // Internal Synapse Neural Network Nodes & Connecting Fibers
        val nodes = listOf(
            // Left Hemisphere
            Offset(centerX - scale * 0.35f, centerY - scale * 0.6f),
            Offset(centerX - scale * 0.65f, centerY - scale * 0.35f),
            Offset(centerX - scale * 0.45f, centerY - scale * 0.1f),
            Offset(centerX - scale * 0.7f, centerY + scale * 0.15f),
            Offset(centerX - scale * 0.4f, centerY + scale * 0.35f),
            Offset(centerX - scale * 0.25f, centerY + scale * 0.65f),

            // Right Hemisphere
            Offset(centerX + scale * 0.35f, centerY - scale * 0.6f),
            Offset(centerX + scale * 0.65f, centerY - scale * 0.35f),
            Offset(centerX + scale * 0.45f, centerY - scale * 0.1f),
            Offset(centerX + scale * 0.7f, centerY + scale * 0.15f),
            Offset(centerX + scale * 0.4f, centerY + scale * 0.35f),
            Offset(centerX + scale * 0.25f, centerY + scale * 0.65f),

            // Center Stem
            Offset(centerX, centerY - scale * 0.4f),
            Offset(centerX, centerY),
            Offset(centerX, centerY + scale * 0.4f)
        )

        // Draw Synaptic Connecting Lines
        for (i in nodes.indices) {
            for (j in i + 1 until nodes.size) {
                val dist = (nodes[i] - nodes[j]).getDistance()
                if (dist < scale * 0.75f) {
                    val alpha = (1f - dist / (scale * 0.75f)) * 0.6f * pulseAnim
                    drawLine(
                        color = if (i % 2 == 0) CyanGlow.copy(alpha = alpha) else PurpleGlow.copy(alpha = alpha),
                        start = nodes[i],
                        end = nodes[j],
                        strokeWidth = 2f
                    )
                }
            }
        }

        // Draw Synapse Glowing Node Circles
        nodes.forEachIndexed { index, node ->
            val color = if (index % 2 == 0) CyanGlow else PurpleGlow
            val nodeRadius = if (index % 3 == 0) 6f else 4f

            // Outer Glow
            drawCircle(
                color = color.copy(alpha = 0.4f * pulseAnim),
                radius = nodeRadius * 2.2f,
                center = node
            )
            // Core
            drawCircle(
                color = Color.White,
                radius = nodeRadius,
                center = node
            )
        }
    }
}
