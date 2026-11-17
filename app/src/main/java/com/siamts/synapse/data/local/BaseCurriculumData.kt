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
        listOf(
            CoreMathCurriculum.getMathSubject(),
            CoreCsCurriculum.getCsSubject(),
            CoreDsCurriculum.getDsSubject(),
            CoreAiCurriculum.getAiSubject(),
            CorePhysicsCurriculum.getPhysicsSubject(),
            CoreChemistryCurriculum.getChemistrySubject()
        )
    }

    val seededNotes: List<NoteItemDto> by lazy {
        val notes = mutableListOf<NoteItemDto>()
        var noteCounter = 2000

        for (subject in subjects) {
            for (chapter in subject.chapters) {
                noteCounter++
                notes.add(
                    NoteItemDto(
                        id = noteCounter,
                        userId = 1,
                        subjectId = subject.subjectId,
                        chapterId = chapter.chapterId,
                        title = "${chapter.title} Notes",
                        content = buildString {
                            appendLine("## ${chapter.title}")
                            appendLine("**Subject:** ${subject.name} | **Category:** ${subject.category}")
                            appendLine()
                            appendLine("### Key Concepts")
                            appendLine(chapter.summary)
                            appendLine()
                            if (chapter.coreFormulas.isNotEmpty()) {
                                appendLine("### Core Formulas & Rules")
                                chapter.coreFormulas.forEach { f ->
                                    appendLine("- `$f`")
                                }
                                appendLine()
                            }
                            if (chapter.syllabus.isNotEmpty()) {
                                appendLine("### Syllabus & Learning Objectives")
                                chapter.syllabus.forEach { s ->
                                    appendLine("- $s")
                                }
                                appendLine()
                            }
                            appendLine("### Exam Takeaways")
                            appendLine("1. Master core definitions and apply them to standard practice problems.")
                            appendLine("2. Double-check calculation signs and variable domains before finalizing answers.")
                            appendLine("3. Review real-world engineering and computational applications.")
                            if (chapter.examTip.isNotBlank()) {
                                appendLine()
                                appendLine("### University Exam & Viva Tip")
                                appendLine(chapter.examTip)
                            }
                        },
                        category = "SUBJECT",
                        isSaved = true,
                        isDownloaded = true,
                        subjectName = subject.name,
                        subjectSlug = subject.slug,
                        subjectIcon = subject.iconSymbol,
                        subjectColor = subject.colorHex,
                        chapterTitle = chapter.title,
                        createdAt = "2026-11-04 10:00:00",
                        updatedAt = "2026-11-04 10:00:00"
                    )
                )
            }
        }
        notes
    }

    fun resolveSubjectNumericId(identifier: String): Int {
        val trimmed = identifier.trim().lowercase(Locale.ROOT)
        return when (trimmed) {
            "math", "maths", "mathematics", "algebra", "calculus", "1", "math_001", "sub_math" -> 1
            "cs", "computer science", "algorithms", "data structures", "2", "sub_cs" -> 2
            "ds", "data science", "data analytics", "3", "sub_ds" -> 3
            "ai", "artificial intelligence", "ml", "deep learning", "4", "sub_ai" -> 4
            "phy", "physics", "waves", "5", "sub_physics" -> 5
            "chem", "chemistry", "bonding", "6", "sub_chemistry" -> 6
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
            else -> trimmed.toIntOrNull() ?: 1
        }
    }

    fun getSubjectById(subjectId: String): CatalogSubject? {
        val numId = resolveSubjectNumericId(subjectId)
        val byNum = subjects.firstOrNull { it.subjectId == numId }
        if (byNum != null) return byNum
        return subjects.firstOrNull {
            it.id.equals(subjectId, ignoreCase = true) ||
            it.name.equals(subjectId, ignoreCase = true) ||
            it.slug.equals(subjectId, ignoreCase = true)
        }
    }

    fun getChapterById(chapterId: Int): CatalogChapter? {
        for (subj in subjects) {
            val ch = subj.chapters.firstOrNull { it.chapterId == chapterId }
            if (ch != null) return ch
        }
        return null
    }

    fun getSubjectChapters(subjectId: Int): SubjectChaptersResponse? {
        val subject = subjects.firstOrNull { it.subjectId == subjectId } ?: return null
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
        } ?: return null
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
