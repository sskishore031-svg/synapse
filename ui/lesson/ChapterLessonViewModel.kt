package com.siamts.synapse.ui.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.ChaptersRepository
import com.siamts.synapse.data.repository.ChaptersRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChapterLessonViewModel(
    private val chaptersRepository: ChaptersRepository = ChaptersRepositoryImpl(),
    private val sessionManager: SessionManager? = SessionManager.getInstanceOrNull()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChapterLessonUiState())
    val uiState: StateFlow<ChapterLessonUiState> = _uiState.asStateFlow()

    fun loadChapterLesson(chapterId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, chapterId = chapterId) }

            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val result = chaptersRepository.getChapterLesson(
                chapterId = chapterId,
                userId = userId,
                email = userEmail
            )

            result.onSuccess { response ->
                val data = response.data
                val chap = data?.chapter
                val sub = data?.subject

                val p1 = chap?.part1
                val p2 = chap?.part2

                val p1Title = p1?.headline?.ifEmpty { "Part 1: Core Fundamentals" } ?: "Part 1: Core Fundamentals"
                val p1Content = p1?.conceptText?.ifEmpty { "Welcome to this chapter lesson." } ?: "Welcome to this chapter lesson."
                val p2Title = p2?.headline?.ifEmpty { "Part 2: Practical Applications" } ?: "Part 2: Practical Applications"
                val p2Content = p2?.conceptText?.ifEmpty { "Deep dive and practice problems for this topic." } ?: "Deep dive and practice problems for this topic."

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        chapterId = chap?.id ?: chapterId,
                        chapterTitle = chap?.title.orEmpty(),
                        subjectName = sub?.name ?: "Core Subject",
                        subjectSlug = sub?.slug.orEmpty(),
                        chapterOrder = chap?.chapterOrder ?: 1,
                        durationMinutes = chap?.estimatedMinutes ?: 15,
                        isCompleted = chap?.isCompleted ?: false,
                        currentPartIndex = 0,
                        part1Title = p1Title,
                        part1Content = p1Content,
                        part2Title = p2Title,
                        part2Content = p2Content,
                        previousChapterId = chap?.previousChapterId,
                        nextChapterId = chap?.nextChapterId
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = err.localizedMessage ?: "Failed to load chapter lesson"
                    )
                }
            }
        }
    }

    fun selectPart(partIndex: Int) {
        _uiState.update { it.copy(currentPartIndex = partIndex) }
    }

    fun markAsCompleted(onCompleted: (() -> Unit)? = null) {
        val cid = _uiState.value.chapterId
        if (cid <= 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isMarkingComplete = true) }

            val user = sessionManager?.getSessionUser()
            val userId = user?.id ?: 1
            val userEmail = user?.email ?: "student@synapse.ai"

            val result = chaptersRepository.completeChapter(
                chapterId = cid,
                userId = userId,
                email = userEmail
            )

            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isMarkingComplete = false,
                        isCompleted = true,
                        completionSuccess = true
                    )
                }
                onCompleted?.invoke()
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isMarkingComplete = false,
                        errorMessage = err.localizedMessage ?: "Failed to update completion status"
                    )
                }
            }
        }
    }
}
