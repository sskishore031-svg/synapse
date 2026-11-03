package com.siamts.synapse.data.model

import com.google.gson.annotations.SerializedName
import com.siamts.synapse.ai.KnowledgeChunk

data class ExtendedCurriculumCatalog(
    @SerializedName("version") val version: String = "1.0",
    @SerializedName("description") val description: String = "",
    @SerializedName("subjects") val subjects: List<CatalogSubject> = emptyList()
)

data class CatalogSubject(
    @SerializedName("id") val id: String = "",
    @SerializedName("subject_id") val subjectId: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("slug") val slug: String = "",
    @SerializedName("icon_symbol") val iconSymbol: String = "📚",
    @SerializedName("color_hex") val colorHex: String = "#3B82F6",
    @SerializedName("category") val category: String = "General",
    @SerializedName("description") val description: String = "",
    @SerializedName("is_added") val isAdded: Boolean = false,
    @SerializedName("chapters") val chapters: List<CatalogChapter> = emptyList()
)

data class CatalogChapter(
    @SerializedName("chapter_id") val chapterId: Int = 0,
    @SerializedName("chapter_order") val chapterOrder: Int = 1,
    @SerializedName("title") val title: String = "",
    @SerializedName("estimated_minutes") val estimatedMinutes: Int = 15,
    @SerializedName("summary") val summary: String = "",
    @SerializedName("syllabus") val syllabus: List<String> = emptyList(),
    @SerializedName("core_formulas") val coreFormulas: List<String> = emptyList(),
    @SerializedName("part1_title") val part1Title: String = "",
    @SerializedName("part1_content") val part1Content: String = "",
    @SerializedName("part2_title") val part2Title: String = "",
    @SerializedName("part2_content") val part2Content: String = "",
    @SerializedName("mcqs") val mcqs: List<QuizQuestionDto> = emptyList(),
    @SerializedName("rag_chunks") val ragChunks: List<KnowledgeChunk> = emptyList()
)

