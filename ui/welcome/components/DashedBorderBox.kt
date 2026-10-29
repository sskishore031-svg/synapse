package com.siamts.synapse.ui.welcome.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Custom container box for screen components without distracting wireframe dashed lines.
 */
@Composable
fun DashedBorderBox(
    modifier: Modifier = Modifier,
    borderColor: Color = Color.Transparent,
    backgroundColor: Color = Color.Transparent,
    strokeWidth: Dp = 0.dp,
    dashLength: Dp = 0.dp,
    gapLength: Dp = 0.dp,
    contentPadding: Dp = 4.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
        content = content
    )
}
