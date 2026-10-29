package com.siamts.synapse.ui.tutor

data class ChatMessage(
    val id: String,
    val senderName: String,
    val text: String,
    val timeAgo: String,
    val isUser: Boolean,
)

/**
 * UI State for FRIDAY AI Tutor Chat Screen following MVVM architecture.
 */
data class AiTutorUiState(
    val headerTag: String = "PERSONALIZED FOR KISHORE",
    val tutorTitle: String = "FRIDAY AI Tutor",
    val tutorName: String = "FRIDAY · Your personal AI companion",
    val isOnline: Boolean = true,
    val timeHeader: String = "TODAY, 9:41 AM",
    val userQuestion: String = "How does gradient descent help a neural network learn?",
    val aiAnswerParagraph1: String = "Think of it like walking downhill in the fog. You take small steps in the direction that slopes down the most.",
    val aiAnswerParagraph2: String = "A neural network does the same: it adjusts its weights to reduce prediction errors. The learning rate controls how big each step is.",
    val aiThoughtStatus: String = "✨ FRIDAY is finding a helpful visual example...",
    val suggestedPrompts: List<String> = listOf(
        "Explain this topic simply",
        "Give me an example",
        "Create a quiz"
    ),
    val inputQuery: String = ""
)
