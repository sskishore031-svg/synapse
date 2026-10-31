package com.siamts.synapse.ui.flashcards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.FlashcardsRepository
import com.siamts.synapse.data.repository.FlashcardsRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing Flashcard flips, live backend loading, progress, and difficulty markings.
 */
class FlashcardsViewModel(
    private val repository: FlashcardsRepository = FlashcardsRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlashcardsUiState(isLoading = true))
    val uiState: StateFlow<FlashcardsUiState> = _uiState.asStateFlow()

    init {
        loadFlashcards()
    }

    fun loadFlashcards(deckName: String? = null, subjectId: Int? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val email = user?.email ?: "student@synapse.ai"

            val result = repository.getFlashcards(userId = userId, email = email, deck = deckName, subjectId = subjectId)
            result.onSuccess { response ->
                val cardsList = response.data?.cards.orEmpty()
                if (cardsList.isNotEmpty()) {
                    val first = cardsList[0]
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            cards = cardsList,
                            totalCards = cardsList.size,
                            currentCardIndex = 1,
                            deckName = first.deckName,
                            headerTag = "${first.deckName.uppercase()} · FLASHCARDS",
                            questionText = first.question,
                            answerText = first.answer,
                            isMarkedDifficult = first.isDifficult,
                            isFlipped = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "No flashcards found."
                        )
                    }
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = err.localizedMessage ?: "Failed to load flashcards."
                    )
                }
            }
        }
    }

    fun onFlipCard() {
        _uiState.update { it.copy(isFlipped = !it.isFlipped) }
    }

    fun onNextCard() {
        _uiState.update { currentState ->
            val cards = currentState.cards
            if (cards.isEmpty()) return@update currentState

            val nextIndex = if (currentState.currentCardIndex < cards.size) {
                currentState.currentCardIndex + 1
            } else {
                1
            }
            val card = cards[nextIndex - 1]
            currentState.copy(
                currentCardIndex = nextIndex,
                deckName = card.deckName,
                headerTag = "${card.deckName.uppercase()} · FLASHCARDS",
                questionText = card.question,
                answerText = card.answer,
                isMarkedDifficult = card.isDifficult,
                isFlipped = false
            )
        }
    }

    fun onPreviousCard() {
        _uiState.update { currentState ->
            val cards = currentState.cards
            if (cards.isEmpty()) return@update currentState

            val prevIndex = if (currentState.currentCardIndex > 1) {
                currentState.currentCardIndex - 1
            } else {
                cards.size
            }
            val card = cards[prevIndex - 1]
            currentState.copy(
                currentCardIndex = prevIndex,
                deckName = card.deckName,
                headerTag = "${card.deckName.uppercase()} · FLASHCARDS",
                questionText = card.question,
                answerText = card.answer,
                isMarkedDifficult = card.isDifficult,
                isFlipped = false
            )
        }
    }

    fun onToggleMarkDifficult() {
        val currentCard = _uiState.value.cards.getOrNull(_uiState.value.currentCardIndex - 1) ?: return
        val newStatus = !_uiState.value.isMarkedDifficult

        _uiState.update { it.copy(isMarkedDifficult = newStatus) }

        viewModelScope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val email = user?.email ?: "student@synapse.ai"
            repository.markDifficult(cardId = currentCard.id, userId = userId, email = email)
        }
    }
}
