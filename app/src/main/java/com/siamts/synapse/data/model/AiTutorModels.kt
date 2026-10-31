package com.siamts.synapse.data.model

import com.google.gson.annotations.SerializedName

data class AiTutorRequest(
    @SerializedName("question")
    val question: String
)

data class AiTutorResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: AiTutorDataDto? = null
)

data class AiTutorDataDto(
    @SerializedName("question")
    val question: String,
    @SerializedName("paragraph1")
    val paragraph1: String,
    @SerializedName("paragraph2")
    val paragraph2: String,
    @SerializedName("answer")
    val answer: String,
    @SerializedName("tutor_name")
    val tutorName: String = "FRIDAY AI",
    @SerializedName("created_at")
    val createdAt: String = ""
)

data class AiTutorHistoryResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: AiTutorHistoryDataDto? = null
)

data class AiTutorHistoryDataDto(
    @SerializedName("total_messages")
    val totalMessages: Int = 0,
    @SerializedName("messages")
    val messages: List<AiTutorChatMessageDto> = emptyList()
)

data class AiTutorChatMessageDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("sender_type")
    val senderType: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("created_at")
    val createdAt: String
)
