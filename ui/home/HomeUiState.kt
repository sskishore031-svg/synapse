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
    val chapterId: Int = 0,
    val title: String,
    val category: String,
    val subjectSlug: String? = null,
    val timeAgo: String,
    val progress: Float,
)

data class HomeSubjectItem(
    val id: Int,
    val name: String,
    val slug: String,
    val iconSymbol: String,
    val colorHex: String,
    val totalChapters: Int,
    val completedChapters: Int,
    val progressPercent: Int
)

/**
 * UI State for the Home / Dashboard Screen following Clean MVVM architecture.
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userName: String = "Student",
    val greeting: String = "Welcome Back,",
    val mindPower: Int = 60,
    val profilePhotoUri: String? = null,
    val course: String = "B.Tech Computer Science · Year 3",
    val interests: String = "Computer Science, AI, Math",
    val overallProgress: Int = 50,
    val progressMessage: String = "",
    val topicsDone: Int = 0,
    val totalTopics: Int = 30,
    val quizScore: Int = 0,
    val quizzesAttempted: Int = 0,
    val latestQuizScore: Int = 0,
    val studyStreakDays: Int = 7,
    val continueChapterTitle: String? = null,
    val continueSubjectSlug: String? = null,
    val continueChapterId: Int = 0,
    val searchQuery: String = "",
    val quickActions: List<QuickAction> = listOf(
        QuickAction("1", "Neural Chat", "AI Companion", "🧠", true),
        QuickAction("2", "Summarizer", "Instant Insights", "⚡"),
        QuickAction("3", "Flashcards", "Memory Boost", "🎴"),
        QuickAction("4", "Quiz Gen", "Test Knowledge", "🎯"),
    ),
    val recentSessions: List<RecentSession> = emptyList(),
    val subjects: List<HomeSubjectItem> = emptyList(),
    val selectedTab: Int = 0,
)
