package com.siamts.synapse.ui.welcome.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Custom S-Ribbon Synapse AI Brand Logo matching the official brand identity.
 */
@Composable
fun SynapseBrandLogo(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = true,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // S-Ribbon Synapse Vector Symbol
        Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Blue -> Purple Gradient Brush
                val ribbonGradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF2563EB), // Electric Blue
                        Color(0xFF3B82F6), // Sky Blue
                        Color(0xFF8B5CF6), // Vibrant Purple
                        Color(0xFF7C3AED)  // Deep Violet
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                )

                val ribbonWidth = w * 0.16f

                // Outer S-Ribbon Path
                val sRibbonPath = Path().apply {
                    // Top Node Loop
                    moveTo(w * 0.72f, h * 0.18f)
                    cubicTo(
                        w * 0.82f, h * 0.28f,
                        w * 0.72f, h * 0.45f,
                        w * 0.52f, h * 0.48f
                    )
                    // Middle Loop & > Arrow Bridge
                    cubicTo(
                        w * 0.32f, h * 0.52f,
                        w * 0.78f, h * 0.62f,
                        w * 0.62f, h * 0.82f
                    )
                    // Bottom Node Loop
                    cubicTo(
                        w * 0.48f, h * 0.95f,
                        w * 0.22f, h * 0.88f,
                        w * 0.28f, h * 0.72f
                    )
                }

                // Inner Arrow > Path inside the ribbon
                val innerArrowPath = Path().apply {
                    moveTo(w * 0.48f, h * 0.44f)
                    lineTo(w * 0.62f, h * 0.52f)
                    lineTo(w * 0.48f, h * 0.60f)
                }

                // Draw Thick S-Ribbon
                drawPath(
                    path = sRibbonPath,
                    brush = ribbonGradient,
                    style = Stroke(
                        width = ribbonWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Draw Inner Arrow
                drawPath(
                    path = innerArrowPath,
                    color = Color.White,
                    style = Stroke(
                        width = w * 0.05f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Top Right Synapse Neural Circle Node
                drawCircle(
                    color = Color.White,
                    radius = w * 0.06f,
                    center = Offset(w * 0.72f, h * 0.18f)
                )
                drawCircle(
                    color = Color(0xFF2563EB),
                    radius = w * 0.03f,
                    center = Offset(w * 0.72f, h * 0.18f)
                )

                // Bottom Left Synapse Neural Circle Node
                drawCircle(
                    color = Color.White,
                    radius = w * 0.06f,
                    center = Offset(w * 0.28f, h * 0.72f)
                )
                drawCircle(
                    color = Color(0xFF8B5CF6),
                    radius = w * 0.03f,
                    center = Offset(w * 0.28f, h * 0.72f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Brand Title: Synapse AI
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Synapse ",
                color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp,
            )

            // AI with Gradient Color
            Text(
                text = "AI",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp,
                style = TextStyle(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF3B82F6),
                            Color(0xFF8B5CF6),
                        )
                    )
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Brand Tagline: LEARN · PRACTICE · GROW
        Text(
            text = "LEARN  ·  PRACTICE  ·  GROW",
            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center,
        )
    }
}
