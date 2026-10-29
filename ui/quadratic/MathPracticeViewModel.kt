package com.siamts.synapse.ui.quadratic

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Practice Questions state for all subjects.
 */
class MathPracticeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MathPracticeUiState())
    val uiState: StateFlow<MathPracticeUiState> = _uiState.asStateFlow()

    fun loadSubjectPractice(subjectName: String) {
        when (subjectName) {
            "Computer Science" -> {
                _uiState.update {
                    it.copy(
                        title = "Computer Science",
                        progressPercent = 50,
                        conceptText = "Analyze algorithm efficiency and perform binary search.",
                        questions = listOf(
                            PracticeQuestionItem("1", "Target = 6 in [2, 4, 6, 8, 10, 12, 14]", "Compare with middle element 8, search left half [2, 4, 6]"),
                            PracticeQuestionItem("2", "Time complexity of Binary Search", "Halves search space at each step: O(log n)"),
                            PracticeQuestionItem("3", "Data Structure for LIFO Principle", "Last-In-First-Out: Stack"),
                            PracticeQuestionItem("4", "Worst-case for Linear Search", "Element at last index or missing: O(n)")
                        ),
                        quickHintText = "Divide the search space in half at each step for binary search."
                    )
                }
            }
            "Data Science" -> {
                _uiState.update {
                    it.copy(
                        title = "Data Science",
                        progressPercent = 48,
                        conceptText = "Choose appropriate visualizations and interpret datasets.",
                        questions = listOf(
                            PracticeQuestionItem("1", "Chart type for monthly sales trend", "Continuous trend over time: Line Chart"),
                            PracticeQuestionItem("2", "Plot for correlation between 2 variables", "Shows continuous pairs: Scatter Plot"),
                            PracticeQuestionItem("3", "Metric least affected by outliers", "Middle rank value: Median"),
                            PracticeQuestionItem("4", "Best chart for category comparison", "Discrete bars: Bar Chart")
                        ),
                        quickHintText = "Scatter plots reveal correlations between two numerical variables."
                    )
                }
            }
            "Artificial Intelligence" -> {
                _uiState.update {
                    it.copy(
                        title = "Artificial Intelligence",
                        progressPercent = 60,
                        conceptText = "Calculate weight updates and evaluate neural network parameters.",
                        questions = listOf(
                            PracticeQuestionItem("1", "Update w=2.0, grad=0.5, learning_rate=0.1", "w = 2.0 - 0.1*(0.5) = 1.95"),
                            PracticeQuestionItem("2", "Effect of learning rate α being too large", "Overshoots the minimum and diverges"),
                            PracticeQuestionItem("3", "Algorithm computing gradients via chain rule", "Propagates errors backwards: Backpropagation"),
                            PracticeQuestionItem("4", "Primary goal of training a neural net", "Minimize the loss function")
                        ),
                        quickHintText = "Subtract the product of learning rate and gradient from current weight."
                    )
                }
            }
            "Physics" -> {
                _uiState.update {
                    it.copy(
                        title = "Physics",
                        progressPercent = 68,
                        conceptText = "Solve wave motion problems using v = f × λ.",
                        questions = listOf(
                            PracticeQuestionItem("1", "f = 440 Hz, λ = 0.8 m → Find Speed v", "v = 440 × 0.8 = 352 m/s"),
                            PracticeQuestionItem("2", "v = 300 m/s, f = 100 Hz → Find Wavelength λ", "λ = v / f = 300 / 100 = 3 m"),
                            PracticeQuestionItem("3", "Frequency = 5 Hz, Wavelength = 2 m", "v = 5 × 2 = 10 m/s"),
                            PracticeQuestionItem("4", "Property measuring peak height", "Maximum displacement from rest: Amplitude")
                        ),
                        quickHintText = "Multiply frequency in Hz by wavelength in meters to get wave speed in m/s."
                    )
                }
            }
            "Chemistry" -> {
                _uiState.update {
                    it.copy(
                        title = "Chemistry",
                        progressPercent = 64,
                        conceptText = "Determine bonding types and chemical compound formulas.",
                        questions = listOf(
                            PracticeQuestionItem("1", "Bonding between Na⁺ and Cl⁻", "Complete transfer of electron: Ionic Bond"),
                            PracticeQuestionItem("2", "Bonding in H₂O molecule", "Sharing electron pair: Covalent Bond"),
                            PracticeQuestionItem("3", "Subatomic particle in chemical bonding", "Outermost valence electrons"),
                            PracticeQuestionItem("4", "Ion formed when an atom loses electrons", "Positively charged ion: Cation")
                        ),
                        quickHintText = "Transferred electrons form ionic bonds; shared electron pairs form covalent bonds."
                    )
                }
            }
            else -> {
                _uiState.update {
                    it.copy(
                        title = "Mathematics",
                        progressPercent = 88,
                        conceptText = "Solve the following quadratic equations by factoring.",
                        questions = listOf(
                            PracticeQuestionItem("1", "x² - 7x + 12 = 0", "Numbers that multiply to 12 and add to -7: -3 and -4"),
                            PracticeQuestionItem("2", "x² + 6x + 9 = 0", "Numbers that multiply to 9 and add to 6: 3 and 3"),
                            PracticeQuestionItem("3", "2x² - 5x - 3 = 0", "Factor by grouping: (2x + 1)(x - 3) = 0"),
                            PracticeQuestionItem("4", "x² - 4x - 12 = 0", "Numbers that multiply to -12 and add to -4: -6 and 2")
                        ),
                        quickHintText = "Look for two numbers that multiply to c and add to b."
                    )
                }
            }
        }
    }

    fun onSubTabSelected(index: Int) {
        _uiState.update { it.copy(selectedSubTab = index) }
    }
}
