package com.siamts.synapse.ui.notes

/**
 * UI State for the AI Notes Generator Screen following MVVM architecture.
 */
data class NotesGeneratorUiState(
    val headerTag: String = "LESS COPYING. MORE UNDERSTANDING.",
    val title: String = "AI Notes Generator",
    val topicInput: String = "Gradient descent",
    val documentText: String = "No file selected · PDF, DOCX · Up to 10 MB",
    val noteTitle: String = "Gradient descent",
    val noteSummary: String = "An optimization method that reduces a model's loss by updating its weights one small step at a time.",
    val keyPoints: List<String> = listOf(
        "Follow the negative gradient.",
        "Learning rate controls step size.",
        "Repeat until loss stabilizes."
    ),
    val importantTerms: List<String> = listOf(
        "Gradient",
        "Loss",
        "Learning rate"
    ),
    val isSaved: Boolean = true,
    val saveStatusText: String = "Saved to My Notes · Just now",
    val selectedTab: Int = 3,
)
