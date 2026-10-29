package com.siamts.synapse.ui.home

data class QuickAction(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val isHighlighted: Boolean = false,
)

data class RecentSession(
    val id: String,
    val title: String,
    val category: String,
    val timeAgo: String,
    val progress: Float,
)

/**
 * UI State for the Home / Dashboard Screen following MVVM architecture.
 */
data class HomeUiState(
    val userName: String = "Alex",
    val greeting: String = "Welcome Back,",
    val mindPower: Int = 94,
    val searchQuery: String = "",
    val quickActions: List<QuickAction> = listOf(
        QuickAction("1", "Neural Chat", "AI Companion", "🧠", true),
        QuickAction("2", "Summarizer", "Instant Insights", "⚡"),
        QuickAction("3", "Flashcards", "Memory Boost", "🎴"),
        QuickAction("4", "Quiz Gen", "Test Knowledge", "🎯"),
    ),
    val recentSessions: List<RecentSession> = listOf(
        RecentSession("1", "Quantum Computing Basics", "Physics & AI", "2h ago", 0.75f),
        RecentSession("2", "Deep Learning Architectures", "Computer Science", "Yesterday", 0.90f),
        RecentSession("3", "Cognitive Neuroscience", "Biology", "3 days ago", 0.40f),
    ),
    val selectedTab: Int = 0,
)
