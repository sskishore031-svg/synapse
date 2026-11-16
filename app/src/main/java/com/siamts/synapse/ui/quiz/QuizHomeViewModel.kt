package com.siamts.synapse.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.BaseCurriculumData
import com.siamts.synapse.data.local.QuizDao
import com.siamts.synapse.data.local.QuizLocalDatabase
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.model.CatalogSubject
import com.siamts.synapse.data.model.QuizSubjectItemDto
import com.siamts.synapse.data.repository.QuizRepository
import com.siamts.synapse.data.repository.QuizRepositoryImpl
import com.siamts.synapse.data.repository.SubjectCatalogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizHomeViewModel(
    private val quizRepository: QuizRepository = QuizRepositoryImpl(),
    private val quizDao: QuizDao = QuizLocalDatabase.getInstance(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull(),
    private val catalogRepo: SubjectCatalogRepository? = try { SubjectCatalogRepository.getInstance() } catch (_: Throwable) { null }
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizHomeUiState(isLoading = true))
    val uiState: StateFlow<QuizHomeUiState> = _uiState.asStateFlow()

    init {
        observeCatalogSubjects()
    }

    private fun observeCatalogSubjects() {
        if (catalogRepo != null) {
            viewModelScope.launch {
                catalogRepo.catalogSubjects.collect { catalogList ->
                    val addedCatalogSubjects = catalogList.filter { it.isAdded }
                    refreshActiveSubjects(addedCatalogSubjects)
                }
            }
        } else {
            refreshActiveSubjects(emptyList())
        }
    }

    fun loadSubjects() {
        val added = catalogRepo?.getAddedSubjects().orEmpty()
        refreshActiveSubjects(added)
    }

    private fun refreshActiveSubjects(addedCatalogSubjects: List<CatalogSubject>) {
        viewModelScope.launch(Dispatchers.IO) {
            val localRecords = try { quizDao.getAllRecords() } catch (_: Throwable) { emptyList() }

            // 1. Base active subjects (6 core subjects)
            val baseList = BaseCurriculumData.subjects.map { baseSub ->
                val subRecords = localRecords.filter { rec ->
                    rec.subjectId.equals(baseSub.id, ignoreCase = true) ||
                    rec.subjectId.equals(baseSub.name, ignoreCase = true) ||
                    rec.subjectId.equals(baseSub.subjectId.toString(), ignoreCase = true) ||
                    rec.subjectId.equals(baseSub.slug, ignoreCase = true)
                }
                val distinctChapters = subRecords.map { it.chapterId }.distinct().size
                val avgScore = if (subRecords.isNotEmpty()) {
                    (subRecords.map { (it.score * 100) / it.totalQuestions.coerceAtLeast(1) }.average()).toInt()
                } else 0

                QuizSubjectItemDto(
                    id = baseSub.subjectId,
                    name = baseSub.name,
                    slug = baseSub.slug,
                    iconSymbol = baseSub.iconSymbol,
                    colorHex = baseSub.colorHex,
                    availableQuizzes = baseSub.chapters.size.coerceAtLeast(4),
                    attemptedQuizzes = distinctChapters,
                    averageScore = avgScore
                )
            }

            // 2. Dynamically added subjects from catalog (ONLY where isAdded == true)
            val addedList = addedCatalogSubjects.map { catSub ->
                val subRecords = localRecords.filter { rec ->
                    rec.subjectId.equals(catSub.id, ignoreCase = true) ||
                    rec.subjectId.equals(catSub.name, ignoreCase = true) ||
                    rec.subjectId.equals(catSub.subjectId.toString(), ignoreCase = true) ||
                    rec.subjectId.equals(catSub.slug, ignoreCase = true)
                }
                val distinctChapters = subRecords.map { it.chapterId }.distinct().size
                val avgScore = if (subRecords.isNotEmpty()) {
                    (subRecords.map { (it.score * 100) / it.totalQuestions.coerceAtLeast(1) }.average()).toInt()
                } else 0

                QuizSubjectItemDto(
                    id = catSub.subjectId,
                    name = catSub.name,
                    slug = catSub.slug,
                    iconSymbol = catSub.iconSymbol,
                    colorHex = catSub.colorHex,
                    availableQuizzes = catSub.chapters.size.coerceAtLeast(3),
                    attemptedQuizzes = distinctChapters,
                    averageScore = avgScore
                )
            }

            val fullActiveList = (baseList + addedList).distinctBy { it.id }
            val totalAvail = fullActiveList.sumOf { it.availableQuizzes }
            val totalAtt = fullActiveList.sumOf { it.attemptedQuizzes }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = null,
                    subjects = fullActiveList,
                    totalQuizzes = if (totalAvail > 0) totalAvail else 24,
                    totalAttempted = totalAtt
                )
            }
        }
    }
}

