package com.siamts.synapse.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.siamts.synapse.SynapseApplication
import com.siamts.synapse.data.local.BaseCurriculumData
import com.siamts.synapse.data.model.CatalogChapter
import com.siamts.synapse.data.model.CatalogSubject
import com.siamts.synapse.data.model.ChapterDetailDto
import com.siamts.synapse.data.model.ChapterDetailResponse
import com.siamts.synapse.data.model.ChapterLessonData
import com.siamts.synapse.data.model.ExtendedCurriculumCatalog
import com.siamts.synapse.data.model.HomeSubjectDto
import com.siamts.synapse.data.model.LessonPartDto
import com.siamts.synapse.data.model.NoteItemDto
import com.siamts.synapse.data.model.QuizQuestionDto
import com.siamts.synapse.data.model.QuizResultDataDto
import com.siamts.synapse.data.model.SubjectChapterDto
import com.siamts.synapse.data.model.SubjectChaptersData
import com.siamts.synapse.data.model.SubjectChaptersResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.util.Locale

class SubjectCatalogRepository private constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _catalogSubjects = MutableStateFlow<List<CatalogSubject>>(emptyList())
    val catalogSubjects: StateFlow<List<CatalogSubject>> = _catalogSubjects.asStateFlow()

    init {
        loadCatalogFromAssets(context)
    }

    private fun loadCatalogFromAssets(context: Context) {
        scope.launch {
            try {
                context.assets.open("extended_curriculum_catalog.json").use { inputStream ->
                    InputStreamReader(inputStream, Charsets.UTF_8).use { reader ->
                        val catalog = gson.fromJson(reader, ExtendedCurriculumCatalog::class.java)
                        val addedSet = getSavedAddedSubjectIds()
                        val updated = catalog.subjects.map { subj ->
                            subj.copy(isAdded = addedSet.contains(subj.id) || addedSet.contains(subj.subjectId.toString()))
                        }
                        _catalogSubjects.value = updated
                    }
                }
            } catch (e: Throwable) {
                Log.e("SubjectCatalogRepo", "Failed to load extended_curriculum_catalog.json: ${e.message}")
            }
        }
    }

    private fun getSavedAddedSubjectIds(): Set<String> {
        return prefs.getStringSet(KEY_ADDED_SUBJECTS, emptySet()) ?: emptySet()
    }

    /**
     * Returns only the extended catalog subjects (10 subjects).
     */
    fun getAllCatalogSubjects(): List<CatalogSubject> = _catalogSubjects.value

    /**
     * Returns all subjects across the entire curriculum (6 base + 10 extended).
     */
    fun getAllSubjects(): List<CatalogSubject> {
        return BaseCurriculumData.subjects + _catalogSubjects.value
    }

    fun getAddedSubjects(): List<CatalogSubject> {
        return _catalogSubjects.value.filter { it.isAdded }
    }

    fun addSubject(subjectId: String) {
        val currentAdded = getSavedAddedSubjectIds().toMutableSet()
        currentAdded.add(subjectId)
        prefs.edit().putStringSet(KEY_ADDED_SUBJECTS, currentAdded).apply()

        _catalogSubjects.value = _catalogSubjects.value.map {
            if (it.id == subjectId || it.subjectId.toString() == subjectId) {
                it.copy(isAdded = true)
            } else it
        }
    }

    fun removeSubject(subjectId: String) {
        val currentAdded = getSavedAddedSubjectIds().toMutableSet()
        currentAdded.remove(subjectId)
        prefs.edit().putStringSet(KEY_ADDED_SUBJECTS, currentAdded).apply()

        _catalogSubjects.value = _catalogSubjects.value.map {
            if (it.id == subjectId || it.subjectId.toString() == subjectId) {
                it.copy(isAdded = false)
            } else it
        }
    }

    fun isSubjectAdded(subjectId: String): Boolean {
        return _catalogSubjects.value.any { (it.id == subjectId || it.subjectId.toString() == subjectId) && it.isAdded }
    }

    fun resolveSubjectNumericId(subjectId: String): Int? {
        val trimmed = subjectId.trim().lowercase(Locale.ROOT)
        return when (trimmed) {
            "math", "maths", "mathematics", "algebra", "calculus", "1", "math_001" -> 1
            "cs", "computer science", "algorithms", "data structures", "2" -> 2
            "ds", "data science", "data analytics", "3" -> 3
            "ai", "artificial intelligence", "ml", "deep learning", "4" -> 4
            "phy", "physics", "waves", "5" -> 5
            "chem", "chemistry", "bonding", "6" -> 6
            "bio", "biology", "ext_bio", "101" -> 101
            "history", "world history", "ext_history", "102" -> 102
            "econ", "economics", "finance", "economics & finance", "ext_econ", "103" -> 103
            "env", "environmental science", "ecology", "ext_env", "104" -> 104
            "net", "networks", "computer networks", "ext_net", "105" -> 105
            "sec", "security", "cyber security", "ext_sec", "106" -> 106
            "psych", "psychology", "ext_psych", "107" -> 107
            "biz", "business", "business management", "management", "ext_biz", "108" -> 108
            "ee", "electrical engineering", "circuits", "ext_ee", "109" -> 109
            "astro", "astronomy", "astronomy & astrophysics", "astrophysics", "ext_astro", "110" -> 110
            else -> trimmed.toIntOrNull()
        }
    }

    fun getSubjectById(subjectId: String): CatalogSubject? {
        val trimmed = subjectId.trim()
        val numId = resolveSubjectNumericId(trimmed)
        if (numId != null) {
            val byNum = getSubjectByNumericId(numId)
            if (byNum != null) return byNum
        }

        return getAllSubjects().firstOrNull {
            it.id.equals(trimmed, ignoreCase = true) ||
            it.name.equals(trimmed, ignoreCase = true) ||
            it.slug.equals(trimmed, ignoreCase = true) ||
            it.subjectId.toString() == trimmed
        }
    }

    fun getSubjectByNumericId(numericId: Int): CatalogSubject? {
        return getAllSubjects().firstOrNull { it.subjectId == numericId }
    }

    fun getChapterById(chapterId: Int): CatalogChapter? {
        for (subj in getAllSubjects()) {
            val ch = subj.chapters.firstOrNull { it.chapterId == chapterId }
            if (ch != null) return ch
        }
        return null
    }

    fun getChapterQuiz(chapterId: Int): List<QuizQuestionDto>? {
        return getChapterById(chapterId)?.mcqs
    }

    fun getSubjectChapters(subjectId: Int): SubjectChaptersResponse? {
        val subject = getSubjectByNumericId(subjectId) ?: return null
        val homeSub = HomeSubjectDto(
            id = subject.subjectId,
            name = subject.name,
            slug = subject.slug,
            iconSymbol = subject.iconSymbol,
            colorHex = subject.colorHex,
            totalChapters = subject.chapters.size,
            completedChapters = 0,
            progressPercent = 0
        )
        val chapterDtos = subject.chapters.mapIndexed { idx, ch ->
            SubjectChapterDto(
                id = ch.chapterId.toString(),
                chapterId = ch.chapterId,
                title = ch.title,
                order = ch.chapterOrder,
                chapterOrder = ch.chapterOrder,
                estimatedMinutes = ch.estimatedMinutes,
                isCompleted = false,
                isUnlocked = true,
                status = if (idx == 0) "ACTIVE" else "UNLOCKED",
                subtext = ch.summary
            )
        }
        return SubjectChaptersResponse(
            status = "success",
            data = SubjectChaptersData(
                subject = homeSub,
                chapters = chapterDtos
            )
        )
    }

    fun getChapterLesson(chapterId: Int): ChapterDetailResponse? {
        val chapter = getChapterById(chapterId) ?: return null
        val parentSubject = getAllSubjects().firstOrNull { subj ->
            subj.chapters.any { it.chapterId == chapterId }
        } ?: return null

        val homeSub = HomeSubjectDto(
            id = parentSubject.subjectId,
            name = parentSubject.name,
            slug = parentSubject.slug,
            iconSymbol = parentSubject.iconSymbol,
            colorHex = parentSubject.colorHex
        )

        val chList = parentSubject.chapters
        val curIdx = chList.indexOfFirst { it.chapterId == chapterId }
        val prevCh = if (curIdx > 0) chList[curIdx - 1] else null
        val nextCh = if (curIdx >= 0 && curIdx < chList.size - 1) chList[curIdx + 1] else null

        val detail = ChapterDetailDto(
            id = chapter.chapterId,
            title = chapter.title,
            chapterOrder = chapter.chapterOrder,
            estimatedMinutes = chapter.estimatedMinutes,
            isCompleted = false,
            previousChapterId = prevCh?.chapterId,
            previousChapterTitle = prevCh?.title,
            nextChapterId = nextCh?.chapterId,
            nextChapterTitle = nextCh?.title,
            part1 = LessonPartDto(
                partNumber = 1,
                subtitleTag = "Part 1: Core Fundamentals",
                headline = chapter.part1Title.ifBlank { "Core Concept & Foundations" },
                conceptText = chapter.part1Content,
                keyPoints = chapter.syllabus
            ),
            part2 = LessonPartDto(
                partNumber = 2,
                subtitleTag = "Part 2: Practical Applications",
                headline = chapter.part2Title.ifBlank { "Deep Dive & Analysis" },
                conceptText = chapter.part2Content,
                keyPoints = chapter.coreFormulas
            )
        )

        return ChapterDetailResponse(
            status = "success",
            data = ChapterLessonData(
                subject = homeSub,
                chapter = detail
            )
        )
    }

    fun submitLocalQuiz(chapterId: Int, answers: Map<String, String>): QuizResultDataDto {
        val chapter = getChapterById(chapterId)
        val questions = chapter?.mcqs.orEmpty()
        val total = questions.size.coerceAtLeast(1)

        var correctCount = 0
        questions.forEach { q ->
            val ans = answers[q.id.toString()] ?: answers[q.questionNumber.toString()]
            if (ans != null && ans.equals(q.correctOption, ignoreCase = true)) {
                correctCount++
            }
        }

        val percentage = (correctCount * 100) / total
        val parentSubject = getAllSubjects().firstOrNull { subj ->
            subj.chapters.any { it.chapterId == chapterId }
        }

        return QuizResultDataDto(
            studentName = "SYNAPSE Scholar",
            subjectName = parentSubject?.name ?: "Curriculum Quiz",
            quizTitle = "${chapter?.title ?: "Chapter"} Quiz",
            chapterId = chapterId,
            score = correctCount,
            totalMarks = total * 10,
            percentage = percentage,
            correctAnswers = correctCount,
            incorrectAnswers = total - correctCount,
            totalQuestions = total,
            attemptDate = "Today",
            passed = percentage >= 60
        )
    }

    /**
     * Returns pre-seeded and catalog study notes ready for on-device display.
     */
    fun getAllStudyNotes(): List<NoteItemDto> {
        val allNotes = BaseCurriculumData.seededNotes.toMutableList()
        var noteCounter = 5000

        for (sub in _catalogSubjects.value) {
            for (ch in sub.chapters) {
                noteCounter++
                allNotes.add(
                    NoteItemDto(
                        id = noteCounter,
                        userId = 1,
                        subjectId = sub.subjectId,
                        chapterId = ch.chapterId,
                        title = "${ch.title} Notes",
                        content = buildString {
                            appendLine("## ${ch.title}")
                            appendLine("**Subject:** ${sub.name} | **Category:** ${sub.category}")
                            appendLine()
                            appendLine("### Chapter Summary")
                            appendLine(ch.summary)
                            appendLine()
                            if (ch.coreFormulas.isNotEmpty()) {
                                appendLine("### Core Formulas & Principles")
                                ch.coreFormulas.forEach { f -> appendLine("- `$f`") }
                                appendLine()
                            }
                            if (ch.syllabus.isNotEmpty()) {
                                appendLine("### Syllabus Topics")
                                ch.syllabus.forEach { s -> appendLine("- $s") }
                                appendLine()
                            }
                            appendLine("### Exam Takeaways")
                            appendLine("1. Review core definitions and key formulas before attempting quizzes.")
                            appendLine("2. Pay attention to distinction between foundational theory and practical applications.")
                        },
                        category = "SUBJECT",
                        isSaved = true,
                        isDownloaded = true,
                        subjectName = sub.name,
                        subjectSlug = sub.slug,
                        subjectIcon = sub.iconSymbol,
                        subjectColor = sub.colorHex,
                        chapterTitle = ch.title,
                        createdAt = "2026-11-04 10:00:00",
                        updatedAt = "2026-11-04 10:00:00"
                    )
                )
            }
        }
        return allNotes
    }

    companion object {
        private const val PREFS_NAME = "synapse_catalog_prefs"
        private const val KEY_ADDED_SUBJECTS = "added_subjects"

        @Volatile
        private var INSTANCE: SubjectCatalogRepository? = null

        fun getInstance(context: Context = SynapseApplication.getAppContext()): SubjectCatalogRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SubjectCatalogRepository(context).also { INSTANCE = it }
            }
        }
    }
}
