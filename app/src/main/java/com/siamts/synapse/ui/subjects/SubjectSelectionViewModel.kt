package com.siamts.synapse.ui.subjects

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.AuthSessionState
import com.siamts.synapse.data.local.BaseCurriculumData
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.CurriculumRepository
import com.siamts.synapse.data.repository.SubjectCatalogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private val curriculumRepo: CurriculumRepository = CurriculumRepository.getInstance(),
    externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope: CoroutineScope = externalScope ?: try {
        val d = Dispatchers.Main
        d.isDispatchNeeded(kotlin.coroutines.EmptyCoroutineContext)
        viewModelScope
    } catch (_: Throwable) {
        CoroutineScope(Dispatchers.Unconfined)
    }

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
        observeLessonProgress()
        if (catalogRepo != null) {
            observeCatalog()
        }
    }

    private fun observeLessonProgress() {
        val progressDao = try {
            com.siamts.synapse.data.local.SynapseDatabase.getLessonProgressDao()
        } catch (_: Throwable) {
            null
        } ?: return
        scope.launch {
            progressDao.observeProgressForUser("1").collect { progressList ->
                val completedMap = progressList.associateBy { it.lessonId }
                _uiState.update { current ->
                    val updatedSubjects = current.subjects.map { sub ->
                        val normTrack = com.siamts.synapse.data.local.TrackMapping.normalizeTrackId(sub.id.ifBlank { sub.title })
                        val totalInTrack = sub.chapters.size.coerceAtLeast(1)
                        val updatedChapters = sub.chapters.map { ch ->
                            val canonId = com.siamts.synapse.data.local.TrackMapping.canonicalLessonId(ch.chapterId, normTrack)
                            val isDone = completedMap[canonId]?.isCompleted == true || completedMap[ch.chapterId.toString()]?.isCompleted == true
                            ch.copy(isCompleted = isDone)
                        }
                        val doneCount = updatedChapters.count { it.isCompleted }
                        val pct = ((doneCount.toFloat() / totalInTrack.toFloat()) * 100).toInt().coerceIn(0, 100)
                        sub.copy(
                            completedChaptersCount = doneCount,
                            totalChaptersCount = totalInTrack,
                            progressPercentage = pct,
                            chapters = updatedChapters
                        )
                    }
                    val (filtered, results) = performSearch(updatedSubjects, current.searchQuery, current.selectedClass)
                    current.copy(
                        subjects = updatedSubjects,
                        filteredSubjects = filtered,
                        searchResults = results
                    )
                }
            }
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
        scope.launch {
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
        val progressDao = try {
            com.siamts.synapse.data.local.SynapseDatabase.getLessonProgressDao()
        } catch (_: Throwable) {
            null
        }
        scope.launch {
            if (progressDao != null) {
                kotlinx.coroutines.flow.combine(
                    catalogRepo.catalogSubjects,
                    progressDao.observeProgressForUser("1")
                ) { catalogList, progressList ->
                    val completedMap = progressList.associateBy { it.lessonId }
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

                    val mergedList = (baseSubjects + addedSubjects).distinctBy { it.id }.map { sub ->
                        val normTrack = com.siamts.synapse.data.local.TrackMapping.normalizeTrackId(sub.id.ifBlank { sub.title })
                        val totalInTrack = sub.chapters.size.coerceAtLeast(1)
                        val updatedChapters = sub.chapters.map { ch ->
                            val canonId = com.siamts.synapse.data.local.TrackMapping.canonicalLessonId(ch.chapterId, normTrack)
                            val isDone = completedMap[canonId]?.isCompleted == true || completedMap[ch.chapterId.toString()]?.isCompleted == true
                            ch.copy(isCompleted = isDone)
                        }
                        val doneCount = updatedChapters.count { it.isCompleted }
                        val pct = ((doneCount.toFloat() / totalInTrack.toFloat()) * 100).toInt().coerceIn(0, 100)
                        sub.copy(
                            completedChaptersCount = doneCount,
                            totalChaptersCount = totalInTrack,
                            progressPercentage = pct,
                            chapters = updatedChapters
                        )
                    }

                    val allCombined = applyInterests(mergedList, _uiState.value.userInterests)
                    val currentQuery = _uiState.value.searchQuery

                    _uiState.update { current ->
                        val (filtered, results) = performSearch(allCombined, currentQuery, current.selectedClass)
                        current.copy(
                            subjects = allCombined,
                            filteredSubjects = filtered,
                            searchResults = results,
                            availableCatalog = catalogList
                        )
                    }
                }.collect {}
            } else {
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

                    val mergedList = (baseSubjects + addedSubjects).distinctBy { it.id }
                    val allCombined = applyInterests(mergedList, _uiState.value.userInterests)
                    val currentQuery = _uiState.value.searchQuery

                    _uiState.update { current ->
                        val (filtered, results) = performSearch(allCombined, currentQuery, current.selectedClass)
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
    }

    fun onClassSelected(className: String) {
        _uiState.update { current ->
            val (filtered, results) = performSearch(current.subjects, current.searchQuery, className)
            current.copy(
                selectedClass = className,
                filteredSubjects = filtered,
                searchResults = results
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        val allSubs = _uiState.value.subjects
        val (filtered, results) = performSearch(allSubs, query, _uiState.value.selectedClass)
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
        query: String,
        selectedClass: String = "All Tracks"
    ): Pair<List<Subject>, List<SearchResultItem>> {
        val classFiltered = if (selectedClass == "All Classes" || selectedClass == "All Tracks") {
            subjects
        } else {
            subjects.filter {
                it.category.contains(selectedClass, ignoreCase = true) ||
                it.title.contains(selectedClass, ignoreCase = true) ||
                (selectedClass == "Python" && (it.id.equals("PYTHON", ignoreCase = true) || it.id == "101" || it.title.contains("Python", ignoreCase = true))) ||
                (selectedClass == "SQL" && (it.id.equals("SQL", ignoreCase = true) || it.id == "102" || it.title.contains("SQL", ignoreCase = true))) ||
                (selectedClass.contains("AI", ignoreCase = true) && (it.id.equals("AI_TECH", ignoreCase = true) || it.id == "103" || it.title.contains("AI", ignoreCase = true))) ||
                (selectedClass.contains("Aptitude", ignoreCase = true) && (it.id.equals("APTITUDE", ignoreCase = true) || it.id == "104" || it.title.contains("Aptitude", ignoreCase = true)))
            }
        }

        val trimmed = query.trim().lowercase(Locale.ROOT)
        if (trimmed.isEmpty()) {
            return Pair(classFiltered, emptyList())
        }

        val matchingSubjects = classFiltered.filter { sub ->
            sub.title.lowercase(Locale.ROOT).contains(trimmed) ||
            sub.category.lowercase(Locale.ROOT).contains(trimmed) ||
            sub.chapters.any {
                it.title.lowercase(Locale.ROOT).contains(trimmed) ||
                it.summary.lowercase(Locale.ROOT).contains(trimmed)
            }
        }

        val chapterResults = mutableListOf<SearchResultItem>()
        for (sub in classFiltered) {
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
        scope.launch {
            val user = sessionManager?.getSessionUser()
            if (willAdd) {
                curriculumRepo.addSubjectToUser(subjectId, user?.id, user?.email)
            }
        }
    }

    fun addSubjectFromCatalog(subjectId: String) {
        catalogRepo?.addSubject(subjectId)
        scope.launch {
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

    fun restartSubject(subjectId: String) {
        val normTrack = com.siamts.synapse.data.local.TrackMapping.normalizeTrackId(subjectId)
        scope.launch {
            try {
                com.siamts.synapse.data.local.SynapseDatabase.getLessonProgressDao().resetTrackProgress(normTrack, "1")
            } catch (_: Throwable) {}
        }
        _uiState.update { current ->
            val updatedSubs = current.subjects.map { sub ->
                if (sub.id.equals(subjectId, ignoreCase = true) || sub.title.equals(subjectId, ignoreCase = true)) {
                    sub.copy(progressPercentage = 0, completedChaptersCount = 0)
                } else sub
            }
            val (filtered, results) = performSearch(updatedSubs, current.searchQuery, current.selectedClass)
            current.copy(
                subjects = updatedSubs,
                filteredSubjects = filtered,
                searchResults = results
            )
        }
    }
}
