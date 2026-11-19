package com.siamts.synapse.ui.quiz

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

typealias QuizHubViewModel = QuizHomeViewModel
typealias QuizHubUiState = QuizHomeUiState

/**
 * Clean facade / alias entry point for Quiz Assessment Hub ("quiz_hub_screen").
 * Strictly routes to QuizHomeSelectionScreen with dynamic enrolled subjects, chapter counts, and scores.
 */
@Composable
fun QuizHubScreen(
    viewModel: QuizHomeViewModel,
    onBackClick: () -> Unit,
    onSelectSubject: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
    onOpenMistakeVault: () -> Unit = {}
) {
    QuizHomeSelectionScreen(
        viewModel = viewModel,
        onBackClick = onBackClick,
        onSelectSubject = onSelectSubject,
        modifier = modifier,
        onOpenMistakeVault = onOpenMistakeVault
    )
}

