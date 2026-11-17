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
    onNavigateToQuiz: ((subjectId: String?, subjectName: String?) -> Unit)? = null,
    onNavigateToSubject: ((subjectId: String) -> Unit)? = null,
    onNavigateToNotes: (() -> Unit)? = null,
    onNavigateToProfile: (() -> Unit)? = null,
    onNavigateToProgress: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    AiTutorScreen(
        viewModel = viewModel,
        onBackClick = onBackClick,
        onNavigateToQuiz = onNavigateToQuiz,
        onNavigateToSubject = onNavigateToSubject,
        onNavigateToNotes = onNavigateToNotes,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToProgress = onNavigateToProgress,
        modifier = modifier,
    )
}
