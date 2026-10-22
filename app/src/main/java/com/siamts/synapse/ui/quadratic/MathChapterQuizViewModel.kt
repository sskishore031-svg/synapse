package com.siamts.synapse.ui.quadratic

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Chapter Quiz state for all subjects.
 */
class MathChapterQuizViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MathChapterQuizUiState())
    val uiState: StateFlow<MathChapterQuizUiState> = _uiState.asStateFlow()

    fun loadSubjectQuiz(subjectName: String) {
        when (subjectName) {
            "Computer Science" -> {
                _uiState.update {
                    it.copy(
                        title = "Computer Science",
                        progressPercent = 100,
                        quizTopics = listOf(
                            "Binary Search algorithm",
                            "Linear Search vs Binary Search",
                            "Data Structures (Arrays, Stacks, Trees)",
                            "Big-O Time Complexity"
                        )
                    )
                }
            }
            "Data Science" -> {
                _uiState.update {
                    it.copy(
                        title = "Data Science",
                        progressPercent = 100,
                        quizTopics = listOf(
                            "Bar & Line Chart selection",
                            "Scatter Plots & Outliers",
                            "Summary Statistics",
                            "Data Interpretation"
                        )
                    )
                }
            }
            "Artificial Intelligence" -> {
                _uiState.update {
                    it.copy(
                        title = "Artificial Intelligence",
                        progressPercent = 100,
                        quizTopics = listOf(
                            "Gradient Descent",
                            "Learning Rate Selection",
                            "Loss Function Optimization",
                            "Backpropagation"
                        )
                    )
                }
            }
            "Physics" -> {
                _uiState.update {
                    it.copy(
                        title = "Physics",
                        progressPercent = 100,
                        quizTopics = listOf(
                            "Wave equation v = f × λ",
                            "Frequency & Wavelength",
                            "Sound & Light Properties",
                            "Interference & Reflection"
                        )
                    )
                }
            }
            "Chemistry" -> {
                _uiState.update {
                    it.copy(
                        title = "Chemistry",
                        progressPercent = 100,
                        quizTopics = listOf(
                            "Ionic & Covalent Bonding",
                            "Lewis Dot Diagrams",
                            "Electronegativity",
                            "Compound Naming & Formulas"
                        )
                    )
                }
            }
            else -> {
                _uiState.update {
                    it.copy(
                        title = "Mathematics",
                        progressPercent = 100,
                        quizTopics = listOf(
                            "Factoring quadratic expressions",
                            "Solving equations by factoring",
                            "Zero product property",
                            "Real-world word problems"
                        )
                    )
                }
            }
        }
    }

    fun onSubTabSelected(index: Int) {
        _uiState.update { it.copy(selectedSubTab = index) }
    }
}
