package com.siamts.synapse.data.model

import com.google.gson.annotations.SerializedName

data class FlashcardsResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: FlashcardsDataDto? = null
)

data class FlashcardsDataDto(
    @SerializedName("total_cards")
    val totalCards: Int = 0,
    @SerializedName("deck_name")
    val deckName: String = "",
    @SerializedName("cards")
    val cards: List<FlashcardItemDto> = emptyList()
)

data class FlashcardItemDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("subject_id")
    val subjectId: Int,
    @SerializedName("chapter_id")
    val chapterId: Int? = null,
    @SerializedName("deck_name")
    val deckName: String,
    @SerializedName("subject_name")
    val subjectName: String = "General",
    @SerializedName("question")
    val question: String,
    @SerializedName("answer")
    val answer: String,
    @SerializedName("difficulty")
    val difficulty: String = "medium",
    @SerializedName("is_difficult")
    val isDifficult: Boolean = false,
    @SerializedName("is_reviewed")
    val isReviewed: Boolean = false
)

data class FlashcardActionResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("card_id")
    val cardId: Int? = null,
    @SerializedName("is_difficult")
    val isDifficult: Boolean? = null,
    @SerializedName("message")
    val message: String? = null
)
