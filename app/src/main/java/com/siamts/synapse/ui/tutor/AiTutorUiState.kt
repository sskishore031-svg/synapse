package com.siamts.synapse.ui.tutor

import java.util.UUID

/**
 * Data model representing a chat message in the AI Tutor conversation.
 */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val senderName: String,
    val text: String,
    val timeAgo: String = "Just now",
    val isUser: Boolean,
    val paragraph2: String? = null,
    val matchedSubject: String? = null,
    val matchedChapter: String? = null,
    val matchedTopic: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * UI State for FRIDAY AI Tutor Chat Screen following MVVM architecture with multi-turn chat support.
 */
data class AiTutorUiState(
    val headerTag: String = "PERSONALIZED FOR STUDENT",
    val tutorTitle: String = "FRIDAY AI Tutor",
    val tutorName: String = "FRIDAY · Your personal AI companion",
    val isOnline: Boolean = true,
    val timeHeader: String = "TODAY",
    val messages: List<ChatMessage> = emptyList(),
    val aiThoughtStatus: String = "✨ FRIDAY AI · Ready to help you learn",
    val suggestedPrompts: List<String> = listOf(
        "Explain this topic simply",
        "Give me an example",
        "What about its roots?",
        "Create a quiz"
    ),
    val inputQuery: String = "",
    // Retained for backward compatibility
    val userQuestion: String = "",
    val aiAnswerParagraph1: String = "",
    val aiAnswerParagraph2: String = ""
)
