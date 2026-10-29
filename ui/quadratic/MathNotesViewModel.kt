package com.siamts.synapse.ui.quadratic

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Chapter Notes state for all subjects.
 */
class MathNotesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MathNotesUiState())
    val uiState: StateFlow<MathNotesUiState> = _uiState.asStateFlow()

    fun loadSubjectNotes(subjectName: String) {
        when (subjectName) {
            "Computer Science" -> {
                _uiState.update {
                    it.copy(
                        title = "Computer Science",
                        progressPercent = 50,
                        progressSubtext = "3 of 5 lessons completed · Beginner path",
                        noteItems = listOf(
                            NoteSummaryItem("1", "Algorithm Design", "Pseudocode and flowcharts"),
                            NoteSummaryItem("2", "Time Complexity", "Big O notation: O(1), O(log n), O(n)"),
                            NoteSummaryItem("3", "Binary Search Tree", "Properties and search operations"),
                            NoteSummaryItem("4", "Optimization Rules", "Avoiding redundant iterations")
                        ),
                        rememberBannerText = "Binary search requires a sorted array. Always sort before searching."
                    )
                }
            }
            "Data Science" -> {
                _uiState.update {
                    it.copy(
                        title = "Data Science",
                        progressPercent = 48,
                        progressSubtext = "2 of 5 lessons completed · Beginner path",
                        noteItems = listOf(
                            NoteSummaryItem("1", "Chart Selection Guide", "When to use Bar, Line, or Scatter plots"),
                            NoteSummaryItem("2", "Summary Statistics", "Mean, Median, Mode, Variance"),
                            NoteSummaryItem("3", "Data Cleaning Rules", "Handling missing values and outliers"),
                            NoteSummaryItem("4", "Python Pandas Cheat Sheet", "Dataframe filtering and aggregation")
                        ),
                        rememberBannerText = "Choose line charts for continuous time-series data and bar charts for discrete categories."
                    )
                }
            }
            "Artificial Intelligence" -> {
                _uiState.update {
                    it.copy(
                        title = "Artificial Intelligence",
                        progressPercent = 60,
                        progressSubtext = "3 of 5 milestones unlocked · Beginner path",
                        noteItems = listOf(
                            NoteSummaryItem("1", "Gradient Descent Rule", "Weight update equation w = w - α * ∂L/∂w"),
                            NoteSummaryItem("2", "Learning Rate Effects", "Overshooting vs slow convergence"),
                            NoteSummaryItem("3", "Loss Functions", "Mean Squared Error & Cross-Entropy"),
                            NoteSummaryItem("4", "Backpropagation Steps", "Chain rule for gradient propagation")
                        ),
                        rememberBannerText = "A learning rate that is too high can cause oscillation and fail to converge."
                    )
                }
            }
            "Physics" -> {
                _uiState.update {
                    it.copy(
                        title = "Physics",
                        progressPercent = 68,
                        progressSubtext = "3 of 5 lessons completed · Beginner path",
                        noteItems = listOf(
                            NoteSummaryItem("1", "Wave Velocity Formula", "v = f × λ"),
                            NoteSummaryItem("2", "Wave Characteristics", "Wavelength, Frequency, Amplitude, Period"),
                            NoteSummaryItem("3", "Sound vs Light Waves", "Mechanical vs Electromagnetic"),
                            NoteSummaryItem("4", "Interference Principles", "Constructive and destructive interference")
                        ),
                        rememberBannerText = "Wave speed depends on the medium. Frequency remains constant across media transitions."
                    )
                }
            }
            "Chemistry" -> {
                _uiState.update {
                    it.copy(
                        title = "Chemistry",
                        progressPercent = 64,
                        progressSubtext = "3 of 5 lessons completed · Beginner path",
                        noteItems = listOf(
                            NoteSummaryItem("1", "Ionic vs Covalent Bonding", "Transfer vs Sharing of electrons"),
                            NoteSummaryItem("2", "Lewis Dot Structures", "Valence electron representation"),
                            NoteSummaryItem("3", "Electronegativity Trends", "Polarity and dipole moments"),
                            NoteSummaryItem("4", "Compound Formulas", "Balancing ionic charges: Na⁺ + Cl⁻ → NaCl")
                        ),
                        rememberBannerText = "Ionic bonds form between metals and non-metals; covalent bonds form between non-metals."
                    )
                }
            }
            else -> {
                _uiState.update {
                    it.copy(
                        title = "Mathematics",
                        progressPercent = 88,
                        progressSubtext = "4 of 5 lessons completed · Beginner path",
                        noteItems = listOf(
                            NoteSummaryItem("1", "Key Formulas", "Quadratic formula, factoring rules"),
                            NoteSummaryItem("2", "Steps to Factor", "Step-by-step method"),
                            NoteSummaryItem("3", "Common Mistakes", "Things to avoid while solving"),
                            NoteSummaryItem("4", "Practice Tips", "Quick tips for better understanding")
                        ),
                        rememberBannerText = "Factoring works only when the quadratic expression can be written as a product of two binomials with integer coefficients."
                    )
                }
            }
        }
    }

    fun onSubTabSelected(index: Int) {
        _uiState.update { it.copy(selectedSubTab = index) }
    }
}
