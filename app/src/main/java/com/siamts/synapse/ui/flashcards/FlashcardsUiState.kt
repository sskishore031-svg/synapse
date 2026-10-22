package com.siamts.synapse.ui.flashcards

/**
 * UI State for the Flashcards Screen ("A little daily recall").
 */
data class FlashcardsUiState(
    val headerTag: String = "GRADIENT DESCENT · FLASHCARDS",
    val title: String = "A little daily recall",
    val deckName: String = "Gradient descent",
    val currentCardIndex: Int = 10,
    val totalCards: Int = 10,
    val questionLabel: String = "FRONT · QUESTION",
    val questionText: String = "Why move against the gradient?",
    val flipInstruction: String = "Tap to flip",
    val answerLabel: String = "BACK · ANSWER PREVIEW",
    val answerText: String = "The gradient points toward the steepest increase in loss. Moving in the opposite direction reduces loss for a small enough step.",
    val swipeHelperText: String = "↔ Swipe to move through your deck",
    val isFlipped: Boolean = false,
    val isMarkedDifficult: Boolean = false,
    val aiNoteText: String = "✨ AI-generated · Personalized revision"
) {
    val isFinalCard: Boolean get() = currentCardIndex >= totalCards
    val nextButtonText: String get() = if (isFinalCard) "End" else "Next"
}
