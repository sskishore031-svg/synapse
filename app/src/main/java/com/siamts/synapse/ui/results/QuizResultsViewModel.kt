package com.siamts.synapse.ui.results

import androidx.lifecycle.ViewModel
import com.siamts.synapse.data.model.QuizResultDataDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Quiz Results state and dynamic backend attempt data.
 */
class QuizResultsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QuizResultsUiState())
    val uiState: StateFlow<QuizResultsUiState> = _uiState.asStateFlow()

    fun setQuizResult(result: QuizResultDataDto) {
        _uiState.update {
            it.copy(
                headerTag = "${result.subjectName.uppercase()} · CHAPTER ${result.chapterId}",
                title = "Learning, leveled up",
                headline = if (result.passed) "Quiz Passed! 🎉" else "Quiz Completed!",
                subHeadline = "Great effort, ${result.studentName.ifEmpty { "Student" }}!",
                scorePercentage = result.percentage,
                resultTitle = if (result.passed) "Nicely done!" else "Keep Practicing!",
                resultMessage = if (result.passed) "Passed with ${result.score} / ${result.totalMarks} marks." else "Scored ${result.score} / ${result.totalMarks} marks. Try again to boost your score!",
                xpEarned = result.correctAnswers * 20,
                correctAnswers = result.correctAnswers,
                incorrectAnswers = result.incorrectAnswers,
                accuracy = "${result.percentage}%",
                feedbackTitle = if (result.passed) "Solid Mastery" else "Review Recommended",
                feedbackText = if (result.passed) "You have demonstrated strong understanding of this chapter's topics." else "Review the chapter lesson to reinforce key concepts before retaking."
            )
        }
    }

    fun onReviewAnswersClicked() {
        // Review quiz answers logic
    }

    fun onShareClicked() {
        // Share result logic
    }
}
