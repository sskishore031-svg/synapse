package com.siamts.synapse

import com.siamts.synapse.data.local.BaseCurriculumData
import com.siamts.synapse.data.repository.SubjectCatalogRepository
import com.siamts.synapse.ui.subjects.SubjectDetailViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SubjectRoutingAndSeedingTest {

    @Test
    fun testBaseCurriculumContainsAll6SubjectsWith5MCQsPerChapter() {
        val baseSubjects = BaseCurriculumData.subjects
        assertEquals("Must have 6 base curriculum subjects", 6, baseSubjects.size)

        val expectedSubjects = listOf(
            "Mathematics",
            "Computer Science",
            "Data Science",
            "Artificial Intelligence",
            "Physics",
            "Chemistry"
        )

        for (name in expectedSubjects) {
            val sub = baseSubjects.firstOrNull { it.name.equals(name, ignoreCase = true) }
            assertNotNull("Base subject $name must exist", sub)
            assertEquals("Subject $name must have 4 core chapters", 4, sub!!.chapters.size)

            for (ch in sub.chapters) {
                assertTrue("Chapter ${ch.title} must have at least 5 MCQs", ch.mcqs.size >= 5)
                for (mcq in ch.mcqs) {
                    assertTrue("MCQ question text must not be blank", mcq.question.isNotBlank())
                    assertEquals("MCQ must have 4 options", 4, mcq.options.size)
                    assertTrue("Correct option must be A, B, C, or D", listOf("A", "B", "C", "D").contains(mcq.correctOption))
                    assertNotNull("MCQ must have explanation", mcq.explanation)
                    assertTrue("MCQ explanation must not be blank", mcq.explanation!!.isNotBlank())
                }
            }
        }
    }

    @Test
    fun testPreSeededStudyNotesAreGeneratedForAllChapters() {
        val seededNotes = BaseCurriculumData.seededNotes
        assertTrue("Must have at least 24 pre-seeded study notes", seededNotes.size >= 24)

        for (note in seededNotes) {
            assertTrue("Note title must not be blank", note.title.isNotBlank())
            assertTrue("Note content must contain markdown", note.content.contains("##"))
            assertTrue("Note must be marked as saved", note.isSaved)
            assertTrue("Note must be marked as downloaded for offline access", note.isDownloaded)
            assertNotNull("Note must belong to a subject", note.subjectId)
            assertNotNull("Note must belong to a chapter", note.chapterId)
        }
    }

    @Test
    fun testSubjectDetailViewModelLoadsAnySubjectDynamically() {
        runBlocking {
            val viewModel = SubjectDetailViewModel()

            // 1. Load Mathematics
            viewModel.loadSubject("MATH")
            val mathState = viewModel.uiState.value
            assertEquals("Mathematics", mathState.title)
            assertEquals("Formal Sciences", mathState.category)
            assertEquals("∑", mathState.iconSymbol)
            assertEquals(4, mathState.chapters.size)
            assertEquals("Quadratic Equations", mathState.chapters.first().title)

            // 2. Load Physics
            viewModel.loadSubject("PHY")
            val phyState = viewModel.uiState.value
            assertEquals("Physics", phyState.title)
            assertEquals("Physical Sciences", phyState.category)
            assertEquals("⚛", phyState.iconSymbol)
            assertEquals(4, phyState.chapters.size)
            assertEquals("Waves & Sound", phyState.chapters.first().title)

            // 3. Load Chemistry
            viewModel.loadSubject("CHEM")
            val chemState = viewModel.uiState.value
            assertEquals("Chemistry", chemState.title)
            assertEquals("Physical Sciences", chemState.category)
            assertEquals("🧪", chemState.iconSymbol)
            assertEquals(4, chemState.chapters.size)
            assertEquals("Chemical Bonding", chemState.chapters.first().title)

            // 4. Load Computer Science
            viewModel.loadSubject("CS")
            val csState = viewModel.uiState.value
            assertEquals("Computer Science", csState.title)
            assertEquals("Technology", csState.category)
            assertEquals("</>", csState.iconSymbol)
            assertEquals(4, csState.chapters.size)
            assertEquals("Algorithms & Programming", csState.chapters.first().title)

            // 5. Load Data Science
            viewModel.loadSubject("DS")
            val dsState = viewModel.uiState.value
            assertEquals("Data Science", dsState.title)
            assertEquals("Data & Analytics", dsState.category)
            assertEquals(4, dsState.chapters.size)

            // 6. Ensure Physics does NOT fall back to Artificial Intelligence
            assertNotEquals("Artificial Intelligence", phyState.title)
            assertNotEquals("Artificial Intelligence", mathState.title)
            assertNotEquals("Artificial Intelligence", chemState.title)
        }
    }
}
