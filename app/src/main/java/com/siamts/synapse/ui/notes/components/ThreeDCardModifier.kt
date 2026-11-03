package com.siamts.synapse.ui.notes.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Adds tactile 3D perspective tilt, depth scaling, and dynamic elevation to any card when touched.
 */
fun Modifier.threeDTouchEffect(
    maxTiltDegrees: Float = 6f,
    pressedScale: Float = 0.97f
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = spring(stiffness = 400f, dampingRatio = 0.65f),
        label = "3D Scale"
    )

    val tiltX by animateFloatAsState(
        targetValue = if (isPressed) maxTiltDegrees else 0f,
        animationSpec = spring(stiffness = 400f, dampingRatio = 0.65f),
        label = "3D Tilt"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            rotationX = tiltX
            cameraDistance = 14f * density
            shadowElevation = if (isPressed) 16f else 4f
        }
        .pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown()
                isPressed = true
                waitForUpOrCancellation()
                isPressed = false
            }
        }
}

