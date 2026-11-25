package com.siamts.synapse.ui.subjects

import androidx.compose.ui.graphics.Color
import com.siamts.synapse.data.model.CatalogSubject

data class SubjectChapterSnippet(
    val chapterId: Int,
    val title: String,
    val summary: String = "",
    val isCompleted: Boolean = false,
    val conceptDone: Boolean = false,
    val quizDone: Boolean = false
)

data class Subject(
    val id: String,
    val title: String,
    val iconSymbol: String,
    val progressPercentage: Int,
    val progressColor: Color,
    val category: String = "Core",
    val chapters: List<SubjectChapterSnippet> = emptyList(),
    val isInterested: Boolean = false,
    val completedChaptersCount: Int = 0,
    val totalChaptersCount: Int = 5
)

data class SearchResultItem(
    val subjectId: String,
    val subjectName: String,
    val iconSymbol: String,
    val color: Color,
    val chapterId: Int? = null,
    val chapterTitle: String? = null,
    val snippet: String = ""
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
    val subjects: List<Subject> = emptyList(),
    val filteredSubjects: List<Subject> = emptyList(),
    val searchResults: List<SearchResultItem> = emptyList(),
    val availableCatalog: List<CatalogSubject> = emptyList(),
    val isCatalogSheetOpen: Boolean = false,
    val selectedTab: Int = 1,
    val userInterests: List<String> = emptyList(),
    val selectedClass: String = "All Tracks",
    val availableClasses: List<String> = listOf("All Tracks", "Python", "SQL", "AI & Tech", "Aptitude")
)
