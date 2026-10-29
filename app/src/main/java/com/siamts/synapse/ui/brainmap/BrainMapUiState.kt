package com.siamts.synapse.ui.brainmap

import androidx.compose.ui.graphics.Color

data class BrainMapNode(
    val id: String,
    val title: String,
    val statusText: String,
    val percentage: Int,
    val color: Color,
    val iconSymbol: String = "📚",
    val chaptersCompleted: Int = 0,
    val totalChapters: Int = 5,
    val quizzesAttempted: Int = 0,
    val averageQuizScore: Int = 0
)

/**
 * UI State for the Synapse Brain Map Screen following MVVM architecture.
 */
data class BrainMapUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEmpty: Boolean = false,
    val isSuccess: Boolean = false,
    val headerTag: String = "SEE HOW YOUR KNOWLEDGE CONNECTS",
    val title: String = "Synapse Brain Map",
    val overallKnowledgePercentage: Int = 0,
    val overallKnowledgeText: String = "Every lesson makes a new connection.",
    val centerTitle: String = "Your mind",
    val nodes: List<BrainMapNode> = defaultNodes(),
    val recommendationTitle: String = "Strengthen your next connection",
    val recommendationText: String = "Revisit your subjects to build strong foundational connections.",
    val totalSubjectsStudied: Int = 0,
    val totalSubjectsAvailable: Int = 6,
    val totalChaptersCompleted: Int = 0,
    val totalChaptersAvailable: Int = 30,
    val totalQuizzesAttempted: Int = 0,
    val averageQuizScore: Int = 0,
    val latestQuizScore: Int = 0,
    val strongestSubject: String? = null,
    val strongestScore: Int? = null,
    val weakestSubject: String? = null,
    val weakestScore: Int? = null,
    val selectedTab: Int = 1,
)

/**
 * Default fallback nodes matching the 6 core subjects.
 */
fun defaultNodes(): List<BrainMapNode> = listOf(
    BrainMapNode("1", "Mathematics", "Not Started · 0%", 0, Color(0xFF3B82F6), "∑"),
    BrainMapNode("2", "Computer Science", "Not Started · 0%", 0, Color(0xFF10B981), "</>"),
    BrainMapNode("3", "Data Science", "Not Started · 0%", 0, Color(0xFF8B5CF6), "📈"),
    BrainMapNode("4", "Artificial Intelligence", "Not Started · 0%", 0, Color(0xFF2563EB), "🧠"),
    BrainMapNode("5", "Physics", "Not Started · 0%", 0, Color(0xFFD97706), "⚛"),
    BrainMapNode("6", "Chemistry", "Not Started · 0%", 0, Color(0xFFEF4444), "🧪"),
)
