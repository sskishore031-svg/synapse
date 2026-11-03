package com.siamts.synapse

import com.google.gson.Gson
import com.siamts.synapse.ai.LocalAIEngine
import com.siamts.synapse.data.model.ExtendedCurriculumCatalog
import com.siamts.synapse.ui.subjects.SubjectSelectionViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class ExtendedFeaturesTest {

    private val gson = Gson()
    private val assetsCatalogFile = File("src/main/assets/extended_curriculum_catalog.json")

    @Test
    fun testExtendedCurriculumCatalogHas10ProductionGradeSubjects() {
        val fileToRead = if (assetsCatalogFile.exists()) assetsCatalogFile else File("app/src/main/assets/extended_curriculum_catalog.json")
        assertTrue("Catalog file must exist", fileToRead.exists())

        val catalog = gson.fromJson(fileToRead.readText(), ExtendedCurriculumCatalog::class.java)
        assertNotNull(catalog)
        assertEquals(10, catalog.subjects.size)

        val expectedSubjectNames = listOf(
            "Biology",
            "World History",
            "Economics & Finance",
            "Environmental Science",
            "Computer Networks",
            "Cyber Security",
            "Psychology",
            "Business Management",
            "Electrical Engineering",
            "Astronomy & Astrophysics"
        )

        for (name in expectedSubjectNames) {
            val sub = catalog.subjects.firstOrNull { it.name.equals(name, ignoreCase = true) }
            assertNotNull("Subject $name must exist in catalog", sub)
            assertTrue("Subject $name must have chapters", sub!!.chapters.isNotEmpty())

            for (ch in sub.chapters) {
                assertTrue("Chapter title must not be blank", ch.title.isNotBlank())
                assertTrue("Chapter must have at least 5 MCQs", ch.mcqs.size >= 5)

                for (mcq in ch.mcqs) {
                    assertTrue("Question must not be blank", mcq.question.isNotBlank())
                    assertEquals("Must have 4 options A, B, C, D", 4, mcq.options.size)
                    assertTrue("Correct option must be valid", listOf("A", "B", "C", "D").contains(mcq.correctOption))
                    assertNotNull("Must have explanation", mcq.explanation)
                    assertTrue("Explanation must not be blank", mcq.explanation!!.isNotBlank())
                }

                assertTrue("Chapter must have RAG chunks", ch.ragChunks.isNotEmpty())
                for (rag in ch.ragChunks) {
                    assertTrue("RAG chunk must have definition", rag.definition.isNotBlank())
                    assertTrue("RAG chunk must have keywords", rag.keywords.isNotEmpty())
                    assertTrue("RAG chunk must have content", rag.content.isNotBlank())
                }
            }
        }
    }

    @Test
    fun testSmartNotesGenerationOnDevice() {
        val engine = LocalAIEngine.getInstance(null)
        val fileToRead = if (assetsCatalogFile.exists()) assetsCatalogFile else File("app/src/main/assets/extended_curriculum_catalog.json")
        if (fileToRead.exists()) {
            engine.loadFromJson(fileToRead.readText())
        }

        val smartNotes = engine.generateSmartNotes(
            chapterTitle = "Cell Biology & Structure",
            subjectName = "Biology",
            lessonText = "The cell is the basic structural and functional unit of all living organisms. All living cells arise from pre-existing cells."
        )

        assertNotNull(smartNotes)
        assertEquals("Cell Biology & Structure", smartNotes.title)
        assertEquals("Biology", smartNotes.subject)
        assertTrue("Definition should not be blank", smartNotes.keyDefinition.isNotBlank())
        assertTrue("Formulas/rules should be extracted", smartNotes.coreFormulasOrLaws.isNotEmpty())
        assertEquals("Must provide exactly 3 high-yield exam takeaways", 3, smartNotes.examTakeaways.size)
        assertTrue("Markdown must contain Key Definition header", smartNotes.fullMarkdown.contains("Key Definition"))
        assertTrue("Markdown must contain Core Formulas header", smartNotes.fullMarkdown.contains("Core Formulas & Laws"))
        assertTrue("Markdown must contain Exam Takeaways header", smartNotes.fullMarkdown.contains("High-Yield Exam Takeaways"))
    }

    @Test
    fun testLearnScreenLiveSearchFiltersChaptersAndSubjects() {
        val viewModel = SubjectSelectionViewModel()

        // 1. Initial State
        assertTrue("Should have initial base subjects", viewModel.uiState.value.subjects.isNotEmpty())
        assertEquals(viewModel.uiState.value.subjects.size, viewModel.uiState.value.filteredSubjects.size)

        // 2. Search for "differentiation" (calculus topic)
        viewModel.onSearchQueryChanged("differentiation")
        val diffState = viewModel.uiState.value
        assertTrue("Should match calculus in topics", diffState.searchResults.any {
            it.chapterTitle?.contains("Derivatives", ignoreCase = true) == true ||
            it.subjectName.contains("Mathematics", ignoreCase = true)
        })

        // 3. Search for "wave" (physics)
        viewModel.onSearchQueryChanged("wave")
        val waveState = viewModel.uiState.value
        assertTrue("Should match Physics waves", waveState.filteredSubjects.any { it.title.equals("Physics", ignoreCase = true) })
        assertTrue("Should match Waves chapter", waveState.searchResults.any { it.chapterTitle?.contains("Waves", ignoreCase = true) == true })

        // 4. Search for "algorithms"
        viewModel.onSearchQueryChanged("algorithms")
        val csState = viewModel.uiState.value
        assertTrue("Should match Computer Science", csState.filteredSubjects.any { it.title.contains("Computer Science", ignoreCase = true) })

        // 5. Clear search query
        viewModel.onSearchQueryChanged("")
        val clearedState = viewModel.uiState.value
        assertEquals("", clearedState.searchQuery)
        assertTrue("Search results should be cleared", clearedState.searchResults.isEmpty())
        assertEquals(clearedState.subjects.size, clearedState.filteredSubjects.size)
    }
}

