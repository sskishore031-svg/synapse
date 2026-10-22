package com.siamts.synapse.ui.welcome.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.siamts.synapse.ui.theme.DashedBorderBoxBg
import com.siamts.synapse.ui.theme.DashedBorderColor

/**
 * Custom container box that draws a dashed tech border around content,
 * matching the blueprint/wireframe dotted bounding boxes seen in the UI design.
 */
@Composable
fun DashedBorderBox(
    modifier: Modifier = Modifier,
    borderColor: Color = DashedBorderColor,
    backgroundColor: Color = DashedBorderBoxBg,
    strokeWidth: Dp = 1.dp,
    dashLength: Dp = 3.dp,
    gapLength: Dp = 3.dp,
    contentPadding: Dp = 8.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .drawWithContent {
                drawContent()
                val strokePx = strokeWidth.toPx()
                val dashPx = dashLength.toPx()
                val gapPx = gapLength.toPx()
                
                drawRoundRect(
                    color = borderColor,
                    style = Stroke(
                        width = strokePx,
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(dashPx, gapPx),
                            0f
                        )
                    )
                )
            }
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
        content = content
    )
}
