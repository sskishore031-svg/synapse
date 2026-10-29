package com.siamts.synapse.ui.welcome

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.welcome.components.BrainGraphic
import com.siamts.synapse.ui.welcome.components.DashedBorderBox
import com.siamts.synapse.ui.welcome.components.SynapseBrandLogo

/**
 * Main Welcome Screen implementing MVVM pattern.
 * Consumes state from [WelcomeViewModel].
 */
@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel,
    modifier: Modifier = Modifier,
    onStartClick: (() -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WelcomeContent(
        uiState = uiState,
        onStartClick = {
            viewModel.onStartClicked()
            onStartClick?.invoke()
        },
        onPageClick = { page -> viewModel.onPageSelected(page) },
        modifier = modifier,
    )
}

@Composable
fun WelcomeContent(
    uiState: WelcomeUiState,
    onStartClick: () -> Unit,
    onPageClick: (Int) -> Unit,
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
        // Futuristic background constellation line grid
        BackgroundConstellationGrid()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 1. Top Badge Box: YOUR MIND. SUPERCHARGED.
            DashedBorderBox(
                modifier = Modifier.padding(bottom = 16.dp),
                contentPadding = 8.dp,
            ) {
                Text(
                    text = uiState.badgeText,
                    color = CyanGlow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                )
            }

            // 2. Center Graphic Frame: Original AI Brain Graphic
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .aspectRatio(1f)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                BrainGraphic(modifier = Modifier.fillMaxSize())
            }

            // 3. Secondary Tagline Box: Learn Smarter. Grow Faster.
            DashedBorderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = 12.dp,
            ) {
                Text(
                    text = uiState.secondaryTagline,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // 4. Pagination Dots & Footer Subtitle Box
            DashedBorderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = 12.dp,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Page Indicator Dots
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        repeat(uiState.totalPages) { pageIndex ->
                            val isSelected = pageIndex == uiState.currentPage
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(if (isSelected) 8.dp else 6.dp)
                                    .background(
                                        color = if (isSelected) CyanGlow else Color(0xFF1E293B),
                                        shape = CircleShape,
                                    )
                                    .clickable { onPageClick(pageIndex) },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = uiState.footerText,
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            // 5. Action Button Section: Let's Start
            DashedBorderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 16.dp),
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
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Background star/particle constellation lines drawing.
 */
@Composable
private fun BackgroundConstellationGrid() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val gridColor = Color(0xFF1E293B).copy(alpha = 0.35f)

        // Draw faint horizontal grid lines
        for (i in 1..8) {
            val y = h * (i / 9f)
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1f,
            )
        }

        // Draw faint vertical grid lines
        for (j in 1..5) {
            val x = w * (j / 6f)
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, h),
                strokeWidth = 1f,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    SYNAPSETheme {
        WelcomeContent(
            uiState = WelcomeUiState(),
            onStartClick = {},
            onPageClick = {},
        )
    }
}
