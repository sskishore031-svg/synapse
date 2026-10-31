package com.siamts.synapse.ui.flashcards

import com.siamts.synapse.data.model.FlashcardItemDto

/**
 * UI State for the Flashcards Screen ("A little daily recall").
 */
data class FlashcardsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val headerTag: String = "DAILY RECALL · FLASHCARDS",
    val title: String = "A little daily recall",
    val deckName: String = "Revision Deck",
    val currentCardIndex: Int = 1,
    val totalCards: Int = 1,
    val questionLabel: String = "FRONT · QUESTION",
    val questionText: String = "Loading question...",
    val flipInstruction: String = "Tap to flip",
    val answerLabel: String = "BACK · ANSWER PREVIEW",
    val answerText: String = "Loading answer...",
    val swipeHelperText: String = "↔ Use Next / Prev to navigate through cards",
    val isFlipped: Boolean = false,
    val isMarkedDifficult: Boolean = false,
    val aiNoteText: String = "✨ Synapse AI · Personalized revision deck",
    val cards: List<FlashcardItemDto> = emptyList()
) {
    val isFinalCard: Boolean get() = currentCardIndex >= totalCards
    val nextButtonText: String get() = if (isFinalCard) "Restart" else "Next"
}
