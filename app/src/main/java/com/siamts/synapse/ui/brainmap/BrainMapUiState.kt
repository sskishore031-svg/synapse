package com.siamts.synapse.ui.brainmap

import androidx.compose.ui.graphics.Color

data class BrainMapNode(
    val id: String,
    val title: String,
    val statusText: String,
    val percentage: Int,
    val color: Color,
)

/**
 * UI State for the Synapse Brain Map Screen following MVVM architecture.
 */
data class BrainMapUiState(
    val headerTag: String = "SEE HOW YOUR KNOWLEDGE CONNECTS",
    val title: String = "Synapse Brain Map",
    val overallKnowledgePercentage: Int = 68,
    val overallKnowledgeText: String = "Every lesson makes a new connection.",
    val centerTitle: String = "Your mind",
    val nodes: List<BrainMapNode> = listOf(
        BrainMapNode("1", "Mathematics", "Strong · 72%", 72, Color(0xFF3B82F6)),
        BrainMapNode("2", "Computer Science", "Strong · 84%", 84, Color(0xFF10B981)),
        BrainMapNode("3", "Data Science", "Learning · 58%", 58, Color(0xFF8B5CF6)),
        BrainMapNode("4", "Artificial Intelligence", "Learning · 60%", 60, Color(0xFF2563EB)),
        BrainMapNode("5", "Physics", "Need Revision · 42%", 42, Color(0xFFD97706)),
        BrainMapNode("6", "Chemistry", "Weak · 35%", 35, Color(0xFFEF4444)),
    ),
    val recommendationTitle: String = "Strengthen your next connection",
    val recommendationText: String = "Revisit Physics this week. A short revision will keep your foundations strong.",
    val selectedTab: Int = 1,
)
