package com.siamts.synapse.ui.subjects

import androidx.compose.ui.graphics.Color

data class Subject(
    val id: String,
    val title: String,
    val iconSymbol: String,
    val progressPercentage: Int,
    val progressColor: Color,
)

/**
 * UI State for the Choose Your Subjects / Personal Learning Library Screen.
 */
data class SubjectSelectionUiState(
    val headerTag: String = "YOUR PERSONAL LEARNING LIBRARY",
    val title: String = "Choose Your Subjects",
    val searchQuery: String = "",
    val subtitle: String = "Keep your curiosity growing",
    val footerText: String = "AI adapts each path to your pace and goals.",
    val subjects: List<Subject> = listOf(
        Subject("1", "Mathematics", "∑", 72, Color(0xFF3B82F6)),
        Subject("2", "Computer Science", "</>", 84, Color(0xFF10B981)),
        Subject("3", "Data Science", "📈", 58, Color(0xFF8B5CF6)),
        Subject("4", "Artificial Intelligence", "🧠", 80, Color(0xFF2563EB)),
        Subject("5", "Physics", "⚛", 42, Color(0xFFD97706)),
        Subject("6", "Chemistry", "🧪", 35, Color(0xFFEF4444)),
    ),
    val selectedTab: Int = 1,
)
