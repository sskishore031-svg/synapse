package com.siamts.synapse.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siamts.synapse.R
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

    // Safety fallback to guarantee transition within 1.8 seconds
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
    onStartClick: () -> Unit = {},
    isAnimated: Boolean = true,
) {
    val scale = remember { Animatable(if (isAnimated) 0.94f else 1f) }
    val alpha = remember { Animatable(if (isAnimated) 0f else 1f) }

    LaunchedEffect(isAnimated) {
        if (isAnimated) {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            )
        }
    }

    LaunchedEffect(isAnimated) {
        if (isAnimated) {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            )
        }
    }

    // Subtle pulsing ambient glow at the bottom
    val infiniteTransition = rememberInfiniteTransition(label = "SplashPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "PulseAlpha",
    )

    // Progress bar fill animation over 1500ms
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070407)), // Deep cosmic tone matching the portal background
        contentAlignment = Alignment.Center,
    ) {
        // Fullscreen Neural Portal Artwork
        Image(
            painter = painterResource(id = R.drawable.splash_neural_portal),
            contentDescription = "SYNAPSE Neural Portal",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .scale(scale.value)
                .alpha(alpha.value),
        )

        // Sleek glowing progress indicator docked seamlessly at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp, start = 48.dp, end = 48.dp)
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0x22FFFFFF))
                .alpha(alpha.value),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.value)
                    .height(3.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                SunsetOrange.copy(alpha = pulseAlpha),
                                AmberGold,
                                SunsetOrangeEnd.copy(alpha = pulseAlpha),
                            )
                        )
                    )
            )
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
