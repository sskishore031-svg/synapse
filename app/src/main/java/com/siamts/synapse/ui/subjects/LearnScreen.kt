package com.siamts.synapse.ui.subjects

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

typealias LearnViewModel = SubjectSelectionViewModel
typealias LearnUiState = SubjectSelectionUiState

/**
 * Top-level Learn Screen facade representing Tab 1 of SYNAPSE AI.
 */
@Composable
fun LearnScreen(
    viewModel: SubjectSelectionViewModel,
    onTabSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onSubjectClick: ((String) -> Unit)? = null,
    onChapterClick: ((Int) -> Unit)? = null,
) {
    SubjectSelectionScreen(
        viewModel = viewModel,
        onTabSelect = onTabSelect,
        modifier = modifier,
        onSubjectClick = onSubjectClick,
        onChapterClick = onChapterClick,
    )
}
