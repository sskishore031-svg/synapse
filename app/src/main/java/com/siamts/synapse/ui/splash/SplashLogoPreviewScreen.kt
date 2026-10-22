package com.siamts.synapse.ui.splash

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.PurpleAccent
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.BrainGraphic
import com.siamts.synapse.ui.welcome.components.DashedBorderBox
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashLogoPreviewScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
            // Header Box
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
                        Text(text = "←", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(text = "SPLASH SCREEN DESIGN CONCEPTS", color = CyanGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Logo Concept Showcase", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Scrollable Logo Showcase List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Logo Concept 1: Neural Synapse Orb
                DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            CyanGlow,
                                            BlueAccent,
                                            PurpleAccent,
                                            Color(0xFF0F172A)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .border(2.dp, CyanGlow, CircleShape)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            BrainGraphic(modifier = Modifier.fillMaxSize())
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(text = "Concept 1: Neural Synapse Orb", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Pulsing radial aura with glowing interconnected neural pathways.", color = TextSecondary, fontSize = 11.sp, textAlign = TextAlign.Center)
                    }
                }

                // Logo Concept 2: Quantum Brain Ring
                DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .background(Color(0xFF0D1430), CircleShape)
                                .border(2.dp, Brush.sweepGradient(listOf(PurpleAccent, BlueAccent, CyanGlow, PurpleAccent)), CircleShape)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height
                                val center = Offset(w / 2f, h / 2f)

                                drawCircle(color = CyanGlow, radius = w * 0.4f, style = Stroke(width = 3f))

                                // Ring particles
                                for (i in 0 until 8) {
                                    val angle = (i * Math.PI * 2 / 8).toFloat()
                                    val px = center.x + cos(angle) * (w * 0.4f)
                                    val py = center.y + sin(angle) * (w * 0.4f)
                                    drawCircle(color = Color.White, radius = 5f, center = Offset(px, py))
                                }
                            }
                            Text(text = "🧠", fontSize = 28.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(text = "Concept 2: Quantum Brain Ring", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Orbiting quantum data particles around a central AI brain ring.", color = TextSecondary, fontSize = 11.sp, textAlign = TextAlign.Center)
                    }
                }

                // Logo Concept 3: AI Synaptic Pulse
                DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .background(Brush.linearGradient(listOf(Color(0xFF06B6D4), Color(0xFF8B5CF6))), CircleShape)
                                .padding(18.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height
                                val midY = h / 2f

                                val path = Path()
                                for (i in 0..100) {
                                    val x = i * (w / 100f)
                                    val y = midY - (h * 0.3f) * sin(i * 0.2f)
                                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                }
                                drawPath(path, color = Color.White, style = Stroke(width = 4f))
                            }
                            Text(text = "✨", fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(text = "Concept 3: AI Synaptic Pulse", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Sinusoidal frequency spark with radiating AI intelligence pulse.", color = TextSecondary, fontSize = 11.sp, textAlign = TextAlign.Center)
                    }
                }

                // Logo Concept 4: Cyber Hex Lattice
                DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .background(Color(0xFF1E1B4B), RoundedCornerShape(20.dp))
                                .border(2.dp, CyanGlow, RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "S",
                                color = CyanGlow,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(text = "Concept 4: Cyber Hex Lattice ('S')", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Futuristic S-monogram enclosed in a glowing cybernetic tile.", color = TextSecondary, fontSize = 11.sp, textAlign = TextAlign.Center)
                    }
                }

                // Info Footer
                DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 6.dp) {
                    Text(
                        text = "✨ Select your preferred logo concept and I will apply it to the main Splash Screen!",
                        color = CyanGlow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashLogoPreviewScreenPreview() {
    SYNAPSETheme {
        SplashLogoPreviewScreen(onBackClick = {}, modifier = Modifier.fillMaxSize())
    }
}
