package com.siamts.synapse.ui.quiz

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

typealias QuizChapterListUiState = SubjectQuizUiState

/**
 * Screen displaying the chapter quiz list for a selected subject ("Select Chapter Quiz").
 * Clean entry point ensuring full compatibility across navigation graphs.
 */
@Composable
fun QuizChapterListScreen(
    subjectId: Int,
    subjectName: String,
    viewModel: SubjectQuizViewModel,
    onBackClick: () -> Unit,
    onStartQuiz: (chapterId: Int, subjectId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    SubjectQuizSelectionScreen(
        subjectId = subjectId,
        subjectName = subjectName,
        viewModel = viewModel,
        onBackClick = onBackClick,
        onStartQuiz = onStartQuiz,
        modifier = modifier
    )
}

@Composable
fun QuizChapterListScreen(
    subjectId: Int,
    subjectName: String,
    viewModel: SubjectQuizViewModel,
    onBackClick: () -> Unit,
    onStartQuiz: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    SubjectQuizSelectionScreen(
        subjectId = subjectId,
        subjectName = subjectName,
        viewModel = viewModel,
        onBackClick = onBackClick,
        onStartQuiz = onStartQuiz,
        modifier = modifier
    )
}

@Composable
fun QuizChapterListScreen(
    subjectId: String,
    subjectName: String,
    viewModel: SubjectQuizViewModel,
    onBackClick: () -> Unit,
    onStartQuiz: (chapterId: Int, subjectId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    SubjectQuizSelectionScreen(
        subjectId = subjectId,
        subjectName = subjectName,
        viewModel = viewModel,
        onBackClick = onBackClick,
        onStartQuiz = onStartQuiz,
        modifier = modifier
    )
}
