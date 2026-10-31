package com.siamts.synapse.ui.tutor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Type alias and composable wrapper for [AiTutorScreen].
 */
typealias AIChatScreenViewModel = AiTutorViewModel

@Composable
fun AIChatScreen(
    viewModel: AiTutorViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AiTutorScreen(
        viewModel = viewModel,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

