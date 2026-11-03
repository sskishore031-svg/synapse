package com.siamts.synapse.ui.results

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.siamts.synapse.data.model.QuizQuestionDto
import com.siamts.synapse.data.model.QuizResultDataDto

/**
 * Dynamic Quiz Review Screen showing user's selected answers,
 * correct answers, highlighted explanations, score, and action buttons.
 */
@Composable
fun QuizReviewScreen(
    resultData: QuizResultDataDto?,
    questions: List<QuizQuestionDto>,
    userAnswers: Map<Int, String>,
    onRetakeQuizClick: () -> Unit,
    onBackToChaptersClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reviewItems = remember(questions, userAnswers) {
        if (questions.isEmpty()) {
            emptyList()
        } else {
            questions.mapIndexed { idx, q ->
                val qNumber = if (q.questionNumber > 0) q.questionNumber else (idx + 1)
                val selectedKey = userAnswers[q.id] ?: userAnswers[qNumber]
                val correctKey = q.correctOption.trim()
                val isCorrect = selectedKey != null && selectedKey.equals(correctKey, ignoreCase = true)

                val userOptionText = if (selectedKey != null) {
                    val label = q.options[selectedKey] ?: selectedKey
                    "$selectedKey. $label"
                } else {
                    "Not Answered"
                }

                val correctOptionText = "$correctKey. ${q.options[correctKey] ?: correctKey}"

                ReviewQuestionItem(
                    id = q.id.toString(),
                    questionNumber = qNumber,
                    questionText = q.question,
                    userAnswer = userOptionText,
                    correctAnswer = correctOptionText,
                    isCorrect = isCorrect,
                    explanation = q.explanation ?: "The correct answer is option $correctKey."
                )
            }
        }
    }

    val totalQ = if (questions.isNotEmpty()) questions.size else (resultData?.totalQuestions ?: 1).coerceAtLeast(1)
    val correctQ = if (reviewItems.isNotEmpty()) reviewItems.count { it.isCorrect } else (resultData?.correctAnswers ?: 0)
    val percentage = (correctQ * 100) / totalQ

    val scoreHeading = "Your Score: $correctQ / $totalQ ($percentage%)"
    val subtext = if (percentage >= 80) "Great job! $correctQ correct answers, ${totalQ - correctQ} incorrect."
        else if (percentage >= 50) "Good effort! $correctQ correct answers, ${totalQ - correctQ} incorrect."
        else "Keep practicing! $correctQ correct answers, ${totalQ - correctQ} incorrect."

    QuizReviewAnswersPreviewScreen(
        onBackClick = onBackClick,
        reviewQuestions = reviewItems.ifEmpty { null },
        scoreText = scoreHeading,
        summarySubtext = subtext,
        accuracyPercent = correctQ.toFloat() / totalQ.toFloat(),
        xpEarned = "+${correctQ * 25} XP",
        onRetryQuizClick = onRetakeQuizClick,
        onBackToChaptersClick = onBackToChaptersClick,
        onDashboardClick = onBackToChaptersClick,
        modifier = modifier
    )
}

