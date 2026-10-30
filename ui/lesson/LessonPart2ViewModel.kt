package com.siamts.synapse.ui.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.LessonRepository
import com.siamts.synapse.data.repository.LessonRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing Lesson Part 2 state, real progress persistence, and backend actions.
 */
class LessonPart2ViewModel(
    private val lessonRepository: LessonRepository = LessonRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonPart2UiState())
    val uiState: StateFlow<LessonPart2UiState> = _uiState.asStateFlow()

    init {
        loadLesson()
    }

    fun loadLesson(
        chapterId: Int? = null,
        subjectId: Int? = null,
        subjectSlug: String? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val result = lessonRepository.getLessonDetails(
                chapterId = chapterId,
                subjectId = subjectId,
                subjectSlug = subjectSlug,
                part = 2,
                userId = userId,
                email = userEmail
            )

            result.onSuccess { response ->
                val data = response.data
                val p2 = data?.part2

                val mappedSteps = p2?.steps?.map { s ->
                    StepValue(
                        weight = s.weight,
                        loss = s.loss,
                        isCurrent = s.isCurrent
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        subjectTag = data?.subjectTag ?: it.subjectTag,
                        lessonTitle = data?.lessonTitle ?: it.lessonTitle,
                        lessonInfo = data?.lessonInfo ?: it.lessonInfo,
                        completionPercentage = data?.completionPercentage ?: it.completionPercentage,
                        chapterId = data?.chapterId ?: (chapterId ?: 18),
                        subjectId = data?.subjectId ?: (subjectId ?: 4),
                        isCompleted = data?.isCompleted ?: false,
                        isBookmarked = data?.isBookmarked ?: false,
                        previousChapterId = data?.previousChapterId,
                        nextChapterId = data?.nextChapterId,
                        partTag = p2?.partTag ?: it.partTag,
                        headline = p2?.headline ?: it.headline,
                        description = p2?.description ?: it.description,
                        formulaHeader = p2?.formulaHeader ?: it.formulaHeader,
                        formulaCalculation = p2?.formulaCalculation ?: it.formulaCalculation,
                        steps = if (!mappedSteps.isNullOrEmpty()) mappedSteps else it.steps,
                        formulaFooter = p2?.formulaFooter ?: it.formulaFooter,
                        takeawayTitle = p2?.takeawayTitle ?: it.takeawayTitle,
                        takeawayContent = p2?.takeawayContent ?: it.takeawayContent
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = err.localizedMessage ?: "Failed to load lesson part 2"
                    )
                }
            }
        }
    }

    fun toggleBookmark() {
        val cid = _uiState.value.chapterId
        viewModelScope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val result = lessonRepository.toggleBookmark(
                chapterId = cid,
                part = 2,
                userId = userId,
                email = userEmail
            )

            result.onSuccess { res ->
                val isBkmk = res.data?.isBookmarked ?: !_uiState.value.isBookmarked
                _uiState.update { it.copy(isBookmarked = isBkmk) }
            }
        }
    }

    fun markCompleted(onSuccess: (() -> Unit)? = null) {
        val cid = _uiState.value.chapterId
        viewModelScope.launch {
            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val result = lessonRepository.completeLesson(
                chapterId = cid,
                userId = userId,
                email = userEmail
            )

            result.onSuccess { res ->
                val newPct = res.data?.progressPercent ?: 100
                _uiState.update {
                    it.copy(
                        isCompleted = true,
                        completionPercentage = newPct
                    )
                }
                onSuccess?.invoke()
            }
        }
    }

    fun onSubTabSelected(index: Int) {
        _uiState.update { it.copy(selectedSubTab = index) }
    }

    fun onNextClicked() {
        // Complete and take quiz or next
    }

    fun onPreviousClicked() {
        // Return to Part 1
    }
}
