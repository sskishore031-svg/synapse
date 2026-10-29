package com.siamts.synapse.ui.quiz

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Quiz question selection, manual option toggling, and subject questions.
 */
class QuizViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun loadSubjectQuiz(subjectName: String) {
        when (subjectName) {
            "Mathematics" -> {
                _uiState.update {
                    it.copy(
                        topicTag = "QUADRATIC EQUATIONS",
                        questionText = "What are the roots of x² - 5x + 6 = 0?",
                        options = listOf(
                            QuizOption("A", "A", "x = 1, 6"),
                            QuizOption("B", "B", "x = 2, 3", true),
                            QuizOption("C", "C", "x = -2, -3"),
                            QuizOption("D", "D", "x = 0, 5"),
                        ),
                        selectedOptionId = null
                    )
                }
            }
            "Computer Science" -> {
                _uiState.update {
                    it.copy(
                        topicTag = "ALGORITHMS & DS",
                        questionText = "What is the worst-case time complexity of Binary Search?",
                        options = listOf(
                            QuizOption("A", "A", "O(1)"),
                            QuizOption("B", "B", "O(log n)", true),
                            QuizOption("C", "C", "O(n)"),
                            QuizOption("D", "D", "O(n²)"),
                        ),
                        selectedOptionId = null
                    )
                }
            }
            "Data Science" -> {
                _uiState.update {
                    it.copy(
                        topicTag = "DATA VISUALIZATION",
                        questionText = "Which chart type is best for showing sales trends over time?",
                        options = listOf(
                            QuizOption("A", "A", "Pie Chart"),
                            QuizOption("B", "B", "Line Chart", true),
                            QuizOption("C", "C", "Bar Chart"),
                            QuizOption("D", "D", "Scatter Plot"),
                        ),
                        selectedOptionId = null
                    )
                }
            }
            "Physics" -> {
                _uiState.update {
                    it.copy(
                        topicTag = "WAVES & MOTION",
                        questionText = "If a sound wave has f = 440 Hz and λ = 0.8 m, what is its speed v?",
                        options = listOf(
                            QuizOption("A", "A", "300 m/s"),
                            QuizOption("B", "B", "352 m/s", true),
                            QuizOption("C", "C", "400 m/s"),
                            QuizOption("D", "D", "550 m/s"),
                        ),
                        selectedOptionId = null
                    )
                }
            }
            "Chemistry" -> {
                _uiState.update {
                    it.copy(
                        topicTag = "CHEMICAL BONDING",
                        questionText = "What type of chemical bond forms between Na⁺ and Cl⁻?",
                        options = listOf(
                            QuizOption("A", "A", "Covalent Bond"),
                            QuizOption("B", "B", "Ionic Bond", true),
                            QuizOption("C", "C", "Metallic Bond"),
                            QuizOption("D", "D", "Hydrogen Bond"),
                        ),
                        selectedOptionId = null
                    )
                }
            }
            else -> {
                _uiState.update {
                    it.copy(
                        topicTag = "GRADIENT DESCENT",
                        questionText = "What is the main goal of gradient descent?",
                        options = listOf(
                            QuizOption("A", "A", "Increase model complexity"),
                            QuizOption("B", "B", "Reduce prediction error / loss", true),
                            QuizOption("C", "C", "Remove all training data"),
                            QuizOption("D", "D", "Make every weight equal"),
                        ),
                        selectedOptionId = null
                    )
                }
            }
        }
    }

    fun onOptionSelected(optionId: String) {
        _uiState.update { currentState ->
            currentState.copy(selectedOptionId = optionId)
        }
    }

    fun onNextQuestionClicked() {
        _uiState.update { currentState ->
            val nextIndex = if (currentState.currentQuestionIndex < currentState.totalQuestions) {
                currentState.currentQuestionIndex + 1
            } else {
                1
            }
            currentState.copy(
                currentQuestionIndex = nextIndex,
                selectedOptionId = null,
            )
        }
    }
}
