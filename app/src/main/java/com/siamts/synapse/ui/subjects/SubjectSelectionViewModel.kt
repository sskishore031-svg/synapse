package com.siamts.synapse.ui.subjects

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.AuthSessionState
import com.siamts.synapse.data.local.BaseCurriculumData
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.CurriculumRepository
import com.siamts.synapse.data.repository.SubjectCatalogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * ViewModel managing Subject Selection, Live Search, Dynamic Interests Mapping,
 * and Extended Catalog dynamic addition.
 */
class SubjectSelectionViewModel(
    private val catalogRepo: SubjectCatalogRepository? = try {
        SubjectCatalogRepository.getInstance()
    } catch (_: Throwable) {
        null
    },
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull(),
    private val curriculumRepo: CurriculumRepository = CurriculumRepository.getInstance()
) : ViewModel() {

    private val baseSubjects = BaseCurriculumData.subjects.map { catalogSub ->
        val parsedColor = try {
            Color(android.graphics.Color.parseColor(catalogSub.colorHex))
        } catch (_: Throwable) {
            when (catalogSub.id) {
                "MATH" -> Color(0xFF3B82F6)
                "CS" -> Color(0xFF10B981)
                "DS" -> Color(0xFF8B5CF6)
                "AI" -> Color(0xFF2563EB)
                "PHY" -> Color(0xFFD97706)
                "CHEM" -> Color(0xFFEF4444)
                else -> Color(0xFF3B82F6)
            }
        }
        Subject(
            id = catalogSub.id,
            title = catalogSub.name,
            iconSymbol = catalogSub.iconSymbol,
            progressPercentage = 0,
            progressColor = parsedColor,
            category = catalogSub.category,
            chapters = catalogSub.chapters.map { ch ->
                SubjectChapterSnippet(
                    chapterId = ch.chapterId,
                    title = ch.title,
                    summary = ch.summary
                )
            }
        )
    }

    private val _uiState = MutableStateFlow(
        SubjectSelectionUiState(
            subjects = baseSubjects,
            filteredSubjects = baseSubjects
        )
    )
    val uiState: StateFlow<SubjectSelectionUiState> = _uiState.asStateFlow()

    init {
        observeUserSession()
        if (catalogRepo != null) {
            observeCatalog()
        }
    }

    private fun applyInterests(subjects: List<Subject>, interests: List<String>): List<Subject> {
        if (interests.isEmpty()) return subjects
        val mapped = subjects.map { sub ->
            val match = interests.any { userInt ->
                sub.title.contains(userInt, ignoreCase = true) ||
                userInt.contains(sub.title, ignoreCase = true) ||
                sub.category.contains(userInt, ignoreCase = true)
            }
            sub.copy(isInterested = match)
        }
        return mapped.sortedByDescending { it.isInterested }
    }

    private fun observeUserSession() {
        val manager = sessionManager ?: SessionManager.getInstanceOrNull() ?: return
        viewModelScope.launch {
            manager.sessionStateFlow.collect { sessionState ->
                if (sessionState is AuthSessionState.Authenticated) {
                    val interests = (sessionState.user.selectedInterests ?: sessionState.user.interests).orEmpty()
                    _uiState.update { current ->
                        val updatedSubjects = applyInterests(current.subjects, interests)
                        val (filtered, results) = performSearch(updatedSubjects, current.searchQuery)
                        current.copy(
                            userInterests = interests,
                            subjects = updatedSubjects,
                            filteredSubjects = filtered,
                            searchResults = results
                        )
                    }
                }
            }
        }
    }

    private fun observeCatalog() {
        if (catalogRepo == null) return
        viewModelScope.launch {
            catalogRepo.catalogSubjects.collect { catalogList ->
                val addedSubjects = catalogList.filter { it.isAdded }.map { catSub ->
                    Subject(
                        id = catSub.subjectId.toString(),
                        title = catSub.name,
                        iconSymbol = catSub.iconSymbol,
                        progressPercentage = 0,
                        progressColor = try {
                            Color(android.graphics.Color.parseColor(catSub.colorHex))
                        } catch (_: Throwable) {
                            Color(0xFF2563EB)
                        },
                        category = catSub.category,
                        chapters = catSub.chapters.map { ch ->
                            SubjectChapterSnippet(
                                chapterId = ch.chapterId,
                                title = ch.title,
                                summary = ch.summary
                            )
                        }
                    )
                }

                val allCombined = applyInterests(baseSubjects + addedSubjects, _uiState.value.userInterests)
                val currentQuery = _uiState.value.searchQuery

                _uiState.update { current ->
                    val (filtered, results) = performSearch(allCombined, currentQuery)
                    current.copy(
                        subjects = allCombined,
                        filteredSubjects = filtered,
                        searchResults = results,
                        availableCatalog = catalogList
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        val allSubs = _uiState.value.subjects
        val (filtered, results) = performSearch(allSubs, query)
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredSubjects = filtered,
                searchResults = results
            )
        }
    }

    private fun performSearch(
        subjects: List<Subject>,
        query: String
    ): Pair<List<Subject>, List<SearchResultItem>> {
        val trimmed = query.trim().lowercase(Locale.ROOT)
        if (trimmed.isEmpty()) {
            return Pair(subjects, emptyList())
        }

        val matchingSubjects = subjects.filter { sub ->
            sub.title.lowercase(Locale.ROOT).contains(trimmed) ||
            sub.category.lowercase(Locale.ROOT).contains(trimmed) ||
            sub.chapters.any {
                it.title.lowercase(Locale.ROOT).contains(trimmed) ||
                it.summary.lowercase(Locale.ROOT).contains(trimmed)
            }
        }

        val chapterResults = mutableListOf<SearchResultItem>()
        for (sub in subjects) {
            for (ch in sub.chapters) {
                if (ch.title.lowercase(Locale.ROOT).contains(trimmed) ||
                    ch.summary.lowercase(Locale.ROOT).contains(trimmed) ||
                    sub.title.lowercase(Locale.ROOT).contains(trimmed)
                ) {
                    chapterResults.add(
                        SearchResultItem(
                            subjectId = sub.id,
                            subjectName = sub.title,
                            iconSymbol = sub.iconSymbol,
                            color = sub.progressColor,
                            chapterId = ch.chapterId,
                            chapterTitle = ch.title,
                            snippet = ch.summary
                        )
                    )
                }
            }
        }

        return Pair(matchingSubjects, chapterResults)
    }

    fun openCatalog() {
        _uiState.update { it.copy(isCatalogSheetOpen = true) }
    }

    fun closeCatalog() {
        _uiState.update { it.copy(isCatalogSheetOpen = false) }
    }

    fun toggleSubjectInCatalog(subjectId: String) {
        val repo = catalogRepo ?: return
        val willAdd = !repo.isSubjectAdded(subjectId)
        if (willAdd) {
            repo.addSubject(subjectId)
        } else {
            repo.removeSubject(subjectId)
        }
        viewModelScope.launch {
            val user = sessionManager?.getSessionUser()
            if (willAdd) {
                curriculumRepo.addSubjectToUser(subjectId, user?.id, user?.email)
            }
        }
    }

    fun addSubjectFromCatalog(subjectId: String) {
        catalogRepo?.addSubject(subjectId)
        viewModelScope.launch {
            val user = sessionManager?.getSessionUser()
            curriculumRepo.addSubjectToUser(subjectId, user?.id, user?.email)
        }
    }

    fun onSubjectClicked(subjectId: String) {
        // Handled via callback
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }
}
