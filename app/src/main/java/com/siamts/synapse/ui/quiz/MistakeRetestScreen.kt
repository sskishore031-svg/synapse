package com.siamts.synapse.ui.quiz

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Re-Test Screen for practicing unmastered questions in the Mistake Vault.
 */
@Composable
fun MistakeRetestScreen(
    viewModel: QuizViewModel,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    QuizScreen(
        viewModel = viewModel,
        onBackClick = onNavigateBack,
        modifier = modifier
    )
}
