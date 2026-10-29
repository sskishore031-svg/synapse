package com.siamts.synapse.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.CyanAccent
import com.siamts.synapse.ui.theme.CyanGlow
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.PurpleAccent
import com.siamts.synapse.ui.theme.PurpleGlow
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.theme.TextSecondary
import com.siamts.synapse.ui.welcome.components.BrainGraphic
import com.siamts.synapse.ui.welcome.components.DashedBorderBox
import com.siamts.synapse.ui.welcome.components.SynapseBrandLogo

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isNavigatingToWelcome) {
        if (uiState.isNavigatingToWelcome) {
            onSplashFinished()
        }
    }

    SplashContent(
        uiState = uiState,
        onStartClick = onSplashFinished,
        modifier = modifier,
    )
}

@Composable
fun SplashContent(
    uiState: SplashUiState,
    modifier: Modifier = Modifier,
    onStartClick: () -> Unit = {},
    isAnimated: Boolean = true,
) {
    val scale = remember { Animatable(if (isAnimated) 0.75f else 1f) }
    val alpha = remember { Animatable(if (isAnimated) 0f else 1f) }

    LaunchedEffect(isAnimated) {
        if (isAnimated) {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            )
        }
    }

    LaunchedEffect(isAnimated) {
        if (isAnimated) {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
            )
        }
    }

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
        // Constellation background grid lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val gridColor = Color(0xFF1E293B).copy(alpha = 0.35f)

            for (i in 1..8) {
                val y = h * (i / 9f)
                drawLine(gridColor, Offset(0f, y), Offset(w, y), 1f)
            }
            for (j in 1..5) {
                val x = w * (j / 6f)
                drawLine(gridColor, Offset(x, 0f), Offset(x, h), 1f)
            }

            // Floating glowing particles
            drawCircle(CyanGlow.copy(alpha = 0.6f), radius = 5f, center = Offset(w * 0.22f, h * 0.22f))
            drawCircle(PurpleGlow.copy(alpha = 0.5f), radius = 6f, center = Offset(w * 0.82f, h * 0.55f))
            drawCircle(CyanGlow.copy(alpha = 0.5f), radius = 4f, center = Offset(w * 0.25f, h * 0.68f))
            drawCircle(PurpleGlow.copy(alpha = 0.6f), radius = 5f, center = Offset(w * 0.78f, h * 0.82f))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .alpha(alpha.value)
                .scale(scale.value)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // 1. Top Badge Box
            DashedBorderBox(
                modifier = Modifier.padding(bottom = 12.dp),
                contentPadding = 8.dp,
            ) {
                Text(
                    text = uiState.badgeText,
                    color = CyanGlow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                )
            }

            // 2. Center Original AI Brain Graphic Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .aspectRatio(1f)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                BrainGraphic(modifier = Modifier.fillMaxSize())
            }

            // 3. Main Tagline
            Text(
                text = uiState.tagline,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp),
            )

            // 5. Pagination Indicators & Footer Subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                // Carousel Dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(7.dp)
                            .background(CyanGlow, CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(5.dp)
                            .background(Color(0xFF334155), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(5.dp)
                            .background(Color(0xFF334155), CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = uiState.footerText,
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                )
            }

            // 6. Action Button Section: Let's Start
            DashedBorderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                contentPadding = 4.dp,
            ) {
                Button(
                    onClick = onStartClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        PurpleAccent,
                                        BlueAccent,
                                        CyanAccent,
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = "→",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.buttonText,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    SYNAPSETheme {
        SplashContent(
            uiState = SplashUiState(),
            onStartClick = {},
            modifier = Modifier.fillMaxSize(),
            isAnimated = false,
        )
    }
}
