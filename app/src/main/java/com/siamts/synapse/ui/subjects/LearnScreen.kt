package com.siamts.synapse.ui.subjects

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

typealias LearnViewModel = SubjectSelectionViewModel
typealias LearnUiState = SubjectSelectionUiState

/**
 * Top-level Learn Screen facade representing Tab 1 of SYNAPSE AI.
 * Routes subject card clicks dynamically through subject ID (e.g. "MATH", "CS", "DS", "AI", "PHY", "CHEM").
 */
@Composable
fun LearnScreen(
    viewModel: SubjectSelectionViewModel,
    onTabSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onSubjectClick: ((String) -> Unit)? = null,
    onChapterClick: ((Int) -> Unit)? = null,
    onNavigateToRoute: ((String) -> Unit)? = null,
) {
    SubjectSelectionScreen(
        viewModel = viewModel,
        onTabSelect = onTabSelect,
        modifier = modifier,
        onSubjectClick = { subjectId ->
            onNavigateToRoute?.invoke("roadmap_screen/$subjectId")
            onSubjectClick?.invoke(subjectId)
        },
        onChapterClick = { chapterId ->
            onNavigateToRoute?.invoke("lesson_screen/$chapterId")
            onChapterClick?.invoke(chapterId)
        },
    )
}
