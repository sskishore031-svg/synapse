package com.siamts.synapse.data.local

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.siamts.synapse.SynapseApplication
import com.siamts.synapse.ai.KnowledgeChunk
import com.siamts.synapse.ai.LocalAIEngine
import com.siamts.synapse.data.model.CatalogChapter
import com.siamts.synapse.data.model.CatalogSubject
import com.siamts.synapse.data.model.ExtendedCurriculumCatalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.util.Locale

/**
 * University Curriculum Engine for SYNAPSE AI.
 * Seeds and delivers comprehensive university-grade curricula across all core and catalog subjects,
 * with 8 progressive chapters per subject, rich 2-part lessons, MCQs, and offline RAG knowledge chunks.
 */
object UniversityCurriculumGenerator {
    private const val TAG = "UniversityCurriculum"
    private val gson = Gson()

    val coreSubjects: List<CatalogSubject> by lazy {
        listOf(
            CoreMathCurriculum.getMathSubject(),
            CoreCsCurriculum.getCsSubject(),
            CoreDsCurriculum.getDsSubject(),
            CoreAiCurriculum.getAiSubject(),
            CorePhysicsCurriculum.getPhysicsSubject(),
            CoreChemistryCurriculum.getChemistrySubject()
        )
    }

    @Volatile
    private var allRAGChunksCache: List<KnowledgeChunk>? = null

    /**
     * Returns all RAG knowledge chunks across core subjects for LocalAIEngine indexing.
     */
    fun getAllRAGChunks(): List<KnowledgeChunk> {
        allRAGChunksCache?.let { return it }
        val chunks = mutableListOf<KnowledgeChunk>()
        for (sub in coreSubjects) {
            for (ch in sub.chapters) {
                chunks.addAll(ch.ragChunks)
            }
        }
        allRAGChunksCache = chunks
        return chunks
    }

    /**
     * Seeds all university curricula (48 core chapters + extended catalog) into SQLite database on Dispatchers.IO.
     */
    suspend fun seedAllCurriculum(context: Context? = null) = withContext(Dispatchers.IO) {
        val appContext = context ?: try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }
        val chapterDao = QuizLocalDatabase.getChapterDao(appContext)
        val quizDao = QuizLocalDatabase.getInstance(appContext)

        Log.i(TAG, "Starting University Curriculum Seeding...")

        // 1. Seed the 6 Core Subjects (48 chapters, 384 MCQs)
        for (sub in coreSubjects) {
            seedCatalogSubject(sub, chapterDao, quizDao)
        }

        // 2. Seed the Extended Catalog Subjects from assets if context is available
        if (appContext != null) {
            try {
                appContext.assets.open("extended_curriculum_catalog.json").use { inputStream ->
                    InputStreamReader(inputStream, Charsets.UTF_8).use { reader ->
                        val catalog = gson.fromJson(reader, ExtendedCurriculumCatalog::class.java)
                        val coreIds = setOf("math", "sub_math", "cs", "sub_cs", "ds", "sub_ds", "ai", "sub_ai", "phy", "sub_physics", "chem", "sub_chemistry")
                        for (sub in catalog.subjects) {
                            val normId = sub.id.lowercase(Locale.ROOT)
                            if (normId !in coreIds) {
                                seedCatalogSubject(sub, chapterDao, quizDao)
                            }
                        }
                    }
                }
            } catch (e: Throwable) {
                Log.w(TAG, "Could not seed extended_curriculum_catalog.json: ${e.message}")
            }
        }

        // 3. Register RAG chunks into LocalAIEngine
        val ragChunks = getAllRAGChunks()
        if (ragChunks.isNotEmpty()) {
            LocalAIEngine.registerChunks(ragChunks)
        }

        Log.i(TAG, "University Curriculum Seeding completed successfully.")
    }

    private suspend fun seedCatalogSubject(
        sub: CatalogSubject,
        chapterDao: ChapterDao,
        quizDao: QuizDao
    ) {
        val entities = mutableListOf<ChapterEntity>()
        val normId = sub.id.lowercase(Locale.ROOT)

        for (ch in sub.chapters) {
            val chIdStr = ch.chapterId.toString()
            val entity = ChapterEntity(
                id = chIdStr,
                subjectId = normId,
                chapterOrder = ch.chapterOrder,
                title = ch.title,
                summary = ch.summary,
                questionCount = ch.mcqs.size.coerceAtLeast(8),
                estimatedMinutes = ch.estimatedMinutes.coerceAtLeast(20),
                totalMarks = ch.mcqs.size.coerceAtLeast(8) * 10
            )
            entities.add(entity)

            // Insert MCQs into quiz_questions and chapter_questions tables
            if (ch.mcqs.isNotEmpty()) {
                try {
                    quizDao.insertQuestionsForChapter(normId, chIdStr, ch.mcqs)
                    // Also insert with numeric subjectId
                    quizDao.insertQuestionsForChapter(sub.subjectId.toString(), chIdStr, ch.mcqs)
                } catch (_: Throwable) {}
            }

            // Insert lesson content
            try {
                chapterDao.insertChapterLesson(
                    ch.chapterId,
                    sub.subjectId,
                    ch.title,
                    ch.part1Title,
                    ch.part1Content,
                    ch.part2Title,
                    ch.part2Content,
                    ch.examTip
                )
            } catch (_: Throwable) {}
        }

        chapterDao.insertChapters(entities)
    }

    /**
     * Resolves chapters for a subject ID directly from in-memory core subjects.
     */
    fun getChaptersForSubject(subjectId: String): List<ChapterEntity> {
        val norm = subjectId.trim().lowercase(Locale.ROOT)
        val matchedSub = coreSubjects.firstOrNull {
            it.id.equals(norm, ignoreCase = true) ||
            it.name.equals(norm, ignoreCase = true) ||
            it.slug.equals(norm, ignoreCase = true) ||
            it.subjectId.toString() == norm ||
            (norm.contains("math") && it.id == "MATH") ||
            (norm.contains("comp") && it.id == "CS") ||
            (norm.contains("data") && it.id == "DS") ||
            (norm.contains("artif") && it.id == "AI") ||
            (norm.contains("phys") && it.id == "PHY") ||
            (norm.contains("chem") && it.id == "CHEM")
        } ?: return emptyList()

        return matchedSub.chapters.map { ch ->
            ChapterEntity(
                id = ch.chapterId.toString(),
                subjectId = matchedSub.id.lowercase(Locale.ROOT),
                chapterOrder = ch.chapterOrder,
                title = ch.title,
                summary = ch.summary,
                questionCount = ch.mcqs.size,
                estimatedMinutes = ch.estimatedMinutes,
                totalMarks = ch.mcqs.size * 10
            )
        }
    }
}
