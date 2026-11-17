package com.siamts.synapse.ui.tutor

import java.util.UUID

/**
 * Data model representing a chat message in the AI Tutor conversation.
 */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val senderName: String = "SYNAPSE AI",
    val text: String,
    val timeAgo: String = "Just now",
    val isUser: Boolean,
    val paragraph2: String? = null,
    val matchedSubject: String? = null,
    val matchedChapter: String? = null,
    val matchedTopic: String? = null,
    val isConceptAnswer: Boolean = false,
    val isSavedNoteCard: Boolean = false,
    val savedNoteTitle: String? = null,
    val actionCommand: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Sealed UI actions dispatched by the Agentic Task Execution Engine.
 */
sealed class AiUiAction {
    data class NavigateToQuiz(val subjectId: String? = null, val subjectName: String? = null) : AiUiAction()
    data class NavigateToSubject(val subjectId: String, val subjectName: String? = null) : AiUiAction()
    data object NavigateToNotes : AiUiAction()
    data object NavigateToProfile : AiUiAction()
    data object NavigateToProgress : AiUiAction()
    data class ShowProgressSummary(val topicsDone: Int, val avgScore: Int, val streak: Int) : AiUiAction()
}

/**
 * UI State for SYNAPSE AI Tutor Chat Screen following MVVM architecture with multi-turn chat support.
 */
data class AiTutorUiState(
    val headerTag: String = "PERSONALIZED FOR STUDENT",
    val tutorTitle: String = "SYNAPSE AI",
    val tutorName: String = "SYNAPSE · On-Device Neural Tutor",
    val isOnline: Boolean = true,
    val timeHeader: String = "TODAY",
    val messages: List<ChatMessage> = emptyList(),
    val aiThoughtStatus: String = "✨ SYNAPSE AI · Ready to help you learn",
    val suggestedPrompts: List<String> = emptyList(),
    val inputQuery: String = "",
    val isGenerating: Boolean = false,
    val currentlySpeakingMessageId: String? = null,
    // Retained for backward compatibility
    val userQuestion: String = "",
    val aiAnswerParagraph1: String = "",
    val aiAnswerParagraph2: String = ""
)
