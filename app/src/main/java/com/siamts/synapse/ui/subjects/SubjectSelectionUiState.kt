package com.siamts.synapse.ui.subjects

import androidx.compose.ui.graphics.Color
import com.siamts.synapse.data.model.CatalogSubject

data class SubjectChapterSnippet(
    val chapterId: Int,
    val title: String,
    val summary: String = ""
)

data class Subject(
    val id: String,
    val title: String,
    val iconSymbol: String,
    val progressPercentage: Int,
    val progressColor: Color,
    val category: String = "Core",
    val chapters: List<SubjectChapterSnippet> = emptyList(),
    val isInterested: Boolean = false
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
    val userInterests: List<String> = emptyList()
)
