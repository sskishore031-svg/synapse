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
        SamacheerCurriculumData.subjects
    }

    // Requirement 4: Start with 0 default notes (only user-created/saved notes exist)
    val seededNotes: List<NoteItemDto> by lazy {
        emptyList()
    }

    fun resolveSubjectNumericId(identifier: String): Int {
        val trimmed = identifier.trim().lowercase(Locale.ROOT)
        return when {
            // Class 10
            trimmed in setOf("10_math", "1001", "10th math", "10th maths", "10th mathematics", "math 10", "maths 10", "math", "maths", "mathematics", "algebra", "sub_math") -> 1001
            trimmed in setOf("10_sci", "1002", "10th sci", "10th science", "science 10", "science", "physics & chemistry", "sub_science") -> 1002
            trimmed in setOf("10_soc", "1003", "10th soc", "10th social", "10th social science", "social science", "social", "history & geography", "sub_social") -> 1003

            // Class 11
            trimmed in setOf("11_math", "1101", "11th math", "11th maths", "11th mathematics", "math 11", "maths 11") -> 1101
            trimmed in setOf("11_phy", "1102", "11th phy", "11th physics", "physics 11") -> 1102
            trimmed in setOf("11_chem", "1103", "11th chem", "11th chemistry", "chemistry 11") -> 1103
            trimmed in setOf("11_bio", "1104", "11th bio", "11th biology", "biology 11", "botany 11", "zoology 11") -> 1104
            trimmed in setOf("11_cs", "1105", "11th cs", "11th computer science", "cs 11", "computer science 11") -> 1105
            trimmed in setOf("11_comm", "1106", "11th comm", "11th commerce", "11th accountancy", "commerce 11") -> 1106
            trimmed in setOf("11_econ", "1107", "11th econ", "11th economics", "economics 11") -> 1107

            // Class 12
            trimmed in setOf("12_math", "1201", "12th math", "12th maths", "12th mathematics", "math 12", "maths 12") -> 1201
            trimmed in setOf("12_phy", "1202", "12th phy", "12th physics", "physics 12") -> 1202
            trimmed in setOf("12_chem", "1203", "12th chem", "12th chemistry", "chemistry 12") -> 1203
            trimmed in setOf("12_bio", "1204", "12th bio", "12th biology", "biology 12", "botany 12", "zoology 12") -> 1204
            trimmed in setOf("12_cs", "1205", "12th cs", "12th computer science", "cs 12", "computer science 12") -> 1205
            trimmed in setOf("12_comm", "1206", "12th comm", "12th commerce", "12th accountancy", "commerce 12") -> 1206
            trimmed in setOf("12_econ", "1207", "12th econ", "12th economics", "economics 12") -> 1207

            // Fallback numeric parsing
            trimmed == "1" -> 1001
            trimmed == "2" -> 1002
            trimmed == "3" -> 1003
            trimmed == "4" -> 1101
            trimmed == "5" -> 1102
            trimmed == "6" -> 1103
            trimmed == "7" -> 1104
            trimmed == "8" -> 1105
            trimmed == "9" -> 1106
            trimmed == "10" -> 1107
            trimmed == "11" -> 1201
            trimmed == "12" -> 1202
            trimmed == "13" -> 1203
            trimmed == "14" -> 1204
            trimmed == "15" -> 1205
            trimmed == "16" -> 1206
            trimmed == "17" -> 1207
            else -> trimmed.toIntOrNull() ?: 1001
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
    }

    fun getChapterById(chapterId: Int): CatalogChapter? {
        for (subj in subjects) {
            val ch = subj.chapters.firstOrNull { it.chapterId == chapterId }
            if (ch != null) return ch
        }
        return com.siamts.synapse.data.repository.SubjectCatalogRepository.getInstanceOrNull()?.getChapterById(chapterId)
    }

    fun getSubjectChapters(subjectId: Int): SubjectChaptersResponse? {
        val subject = subjects.firstOrNull { it.subjectId == subjectId }
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
