package com.siamts.synapse.ui.tutor

import com.siamts.synapse.data.local.ChatSessionEntity
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
    val isSaved: Boolean = false,
    val isQuizQuestion: Boolean = false,
    val quizOptions: Map<String, String>? = null,
    val quizQuestionIndex: Int = 0,
    val quizTotalQuestions: Int = 10,
    val isQuizScorecard: Boolean = false,
    val quizScore: Int = 0,
    val quizTotal: Int = 10,
    val quizSubject: String? = null,
    val quizMistakes: List<String> = emptyList(),
    val isQuizScoreSaved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class InChatQuizAnswer(
    val questionNumber: Int,
    val question: String,
    val selectedOption: String,
    val correctOption: String,
    val isCorrect: Boolean,
    val explanation: String
)

data class InChatQuizSession(
    val sessionId: String = UUID.randomUUID().toString(),
    val subjectId: String,
    val subjectName: String,
    val questions: List<com.siamts.synapse.data.model.QuizQuestionDto>,
    val currentIndex: Int = 0,
    val score: Int = 0,
    val userAnswers: List<InChatQuizAnswer> = emptyList(),
    val isCompleted: Boolean = false
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
    val activeQuizSession: InChatQuizSession? = null,
    val isHistorySheetVisible: Boolean = false,
    val chatSessions: List<ChatSessionEntity> = emptyList(),
    val currentSessionId: String = "",
    val isHoldRecording: Boolean = false,
    val isRecordingLocked: Boolean = false,
    val recordingDurationSeconds: Int = 0,
    val recordedSpokenText: String = "",
    // Retained for backward compatibility
    val userQuestion: String = "",
    val aiAnswerParagraph1: String = "",
    val aiAnswerParagraph2: String = ""
)
