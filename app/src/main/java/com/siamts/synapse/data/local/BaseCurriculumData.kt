package com.siamts.synapse.data.local

import com.siamts.synapse.data.model.CatalogChapter
import com.siamts.synapse.data.model.CatalogSubject
import com.siamts.synapse.data.model.ChapterDetailDto
import com.siamts.synapse.data.model.ChapterDetailResponse
import com.siamts.synapse.data.model.ChapterLessonData
import com.siamts.synapse.data.model.HomeSubjectDto
import com.siamts.synapse.data.model.LessonPartDto
import com.siamts.synapse.data.model.NoteItemDto
import com.siamts.synapse.data.model.SubjectChapterDto
import com.siamts.synapse.data.model.SubjectChaptersData
import com.siamts.synapse.data.model.SubjectChaptersResponse
import java.util.Locale

/**
 * Embedded on-device curriculum bank containing all base university subjects
 * (Mathematics, Computer Science, Data Science, Artificial Intelligence, Physics, Chemistry)
 * with 8 progressive college chapters per subject, 8-10 MCQs per chapter, complete 2-part lessons,
 * and pre-seeded study notes.
 */
object BaseCurriculumData {

    val subjects: List<CatalogSubject> by lazy {
        SamacheerCurriculumData.subjects.filter { it.isAdded || it.subjectId in 101..104 }
    }

    // Requirement 4: Start with 0 default notes (only user-created/saved notes exist)
    val seededNotes: List<NoteItemDto> by lazy {
        emptyList()
    }

    fun resolveSubjectNumericId(identifier: String): Int {
        val trimmed = identifier.trim().lowercase(Locale.ROOT)
        return when {
            // Track 1: Python & Coding Logic
            trimmed in setOf("python", "101", "py", "coding", "logic", "python & coding logic", "python-coding-logic", "sub_python", "sub_py") -> 101
            // Track 2: SQL & Database Management
            trimmed in setOf("sql", "102", "db", "database", "rdbms", "sql & database management", "sql-database-management", "sub_sql", "sub_db") -> 102
            // Track 3: AI & Modern Tech Concepts
            trimmed in setOf("ai_tech", "103", "ai", "tech", "genai", "llm", "modern tech", "ai & modern tech concepts", "ai-modern-tech-concepts", "sub_ai") -> 103
            // Track 4: Aptitude & Interview Prep
            trimmed in setOf("aptitude", "104", "quant", "logical", "interview", "prep", "aptitude & interview prep", "aptitude-interview-prep", "sub_aptitude") -> 104
            // Track 5: Data Structures & Algorithms
            trimmed in setOf("dsa", "105", "data structures", "algorithms", "dsa & algorithms", "data structures & algorithms", "data-structures-algorithms", "sub_dsa") -> 105
            // Track 6: Java & OOPs Mastery
            trimmed in setOf("java_oops", "106", "java", "oops", "java & oops", "java & oops mastery", "java-oops-mastery", "sub_java") -> 106
            // Track 7: OS & Computer Networks
            trimmed in setOf("os_cn", "107", "os", "cn", "networks", "operating systems", "os & computer networks", "os-computer-networks", "sub_os") -> 107
            // Track 8: Android & App Architecture
            trimmed in setOf("android_dev", "108", "android", "compose", "app architecture", "android & app architecture", "android-app-architecture", "sub_android") -> 108

            // Fallback numeric parsing
            trimmed == "1" -> 101
            trimmed == "2" -> 102
            trimmed == "3" -> 103
            trimmed == "4" -> 104
            trimmed == "5" -> 105
            trimmed == "6" -> 106
            trimmed == "7" -> 107
            trimmed == "8" -> 108
            else -> trimmed.toIntOrNull() ?: 101
        }
    }

    fun getSubjectById(subjectId: String): CatalogSubject? {
        val numId = resolveSubjectNumericId(subjectId)
        val byNum = subjects.firstOrNull { it.subjectId == numId }
        if (byNum != null) return byNum
        val byBase = subjects.firstOrNull {
            it.id.equals(subjectId, ignoreCase = true) ||
            it.name.equals(subjectId, ignoreCase = true) ||
            it.slug.equals(subjectId, ignoreCase = true)
        }
        if (byBase != null) return byBase

        return com.siamts.synapse.data.repository.SubjectCatalogRepository.getInstanceOrNull()?.getSubjectById(subjectId)
            ?: SamacheerCurriculumData.subjects.firstOrNull { it.subjectId == numId || it.id.equals(subjectId, ignoreCase = true) || it.name.equals(subjectId, ignoreCase = true) || it.slug.equals(subjectId, ignoreCase = true) }
    }

    fun getChapterById(chapterId: Int): CatalogChapter? {
        for (subj in subjects) {
            val ch = subj.chapters.firstOrNull { it.chapterId == chapterId }
            if (ch != null) return ch
        }
        for (subj in SamacheerCurriculumData.subjects) {
            val ch = subj.chapters.firstOrNull { it.chapterId == chapterId }
            if (ch != null) return ch
        }
        return com.siamts.synapse.data.repository.SubjectCatalogRepository.getInstanceOrNull()?.getChapterById(chapterId)
    }

    fun getSubjectChapters(subjectId: Int): SubjectChaptersResponse? {
        val subject = subjects.firstOrNull { it.subjectId == subjectId }
            ?: SamacheerCurriculumData.subjects.firstOrNull { it.subjectId == subjectId }
            ?: return com.siamts.synapse.data.repository.SubjectCatalogRepository.getInstanceOrNull()?.getSubjectChapters(subjectId)
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
        val parentSubject = subjects.firstOrNull { subj ->
            subj.chapters.any { it.chapterId == chapterId }
        } ?: SamacheerCurriculumData.subjects.firstOrNull { subj ->
            subj.chapters.any { it.chapterId == chapterId }
        } ?: return com.siamts.synapse.data.repository.SubjectCatalogRepository.getInstanceOrNull()?.getChapterLesson(chapterId)
        val chapter = parentSubject.chapters.firstOrNull { it.chapterId == chapterId } ?: return null

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
                headline = chapter.part1Title.ifBlank { "${chapter.title} Fundamentals" },
                conceptText = chapter.part1Content,
                keyPoints = chapter.syllabus
            ),
            part2 = LessonPartDto(
                partNumber = 2,
                subtitleTag = "Part 2: Practical Applications",
                headline = chapter.part2Title.ifBlank { "${chapter.title} Deep Dive & Formulas" },
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
}
