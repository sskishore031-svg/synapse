package com.siamts.synapse

import com.siamts.synapse.data.local.BaseCurriculumData
import com.siamts.synapse.data.repository.SubjectCatalogRepository
import com.siamts.synapse.ui.lesson.ChapterLessonViewModel
import com.siamts.synapse.ui.roadmap.RoadmapViewModel
import com.siamts.synapse.ui.subjects.SubjectDetailViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
            val viewModel = SubjectDetailViewModel(externalScope = CoroutineScope(Dispatchers.Unconfined))

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

    @Test
    fun testRoadmapViewModelLoadsDynamicSubjectMilestonesWithoutArtificialIntelligenceHardcoding() {
        runBlocking {
            val viewModel = RoadmapViewModel(externalScope = CoroutineScope(Dispatchers.Unconfined))

            // Test Mathematics Roadmap
            viewModel.loadSubjectRoadmap("MATH")
            val mathState = viewModel.uiState.value
            assertEquals("Mathematics", mathState.subjectName)
            assertEquals("Mathematics Roadmap", mathState.title)
            assertEquals("∑", mathState.subjectIcon)
            assertEquals(4, mathState.milestones.size)
            assertEquals("Quadratic Equations", mathState.milestones.first().title)
            assertEquals(4, mathState.activeChapterId)
            assertTrue(mathState.milestoneSummary.contains("4 milestones"))

            // Test Physics Roadmap
            viewModel.loadSubjectRoadmap("PHY")
            val phyState = viewModel.uiState.value
            assertEquals("Physics", phyState.subjectName)
            assertEquals("Physics Roadmap", phyState.title)
            assertEquals("⚛", phyState.subjectIcon)
            assertEquals(4, phyState.milestones.size)
            assertEquals("Waves & Sound", phyState.milestones.first().title)
            assertNotEquals("Artificial Intelligence", phyState.subjectName)

            // Test Computer Science Roadmap
            viewModel.loadSubjectRoadmap("CS")
            val csState = viewModel.uiState.value
            assertEquals("Computer Science", csState.subjectName)
            assertEquals("Computer Science Roadmap", csState.title)
            assertEquals(4, csState.milestones.size)
            assertEquals("Algorithms & Programming", csState.milestones.first().title)
        }
    }

    @Test
    fun testChapterLessonViewModelLoadsRealFormulasAndZeroPlaceholderText() {
        runBlocking {
            val viewModel = ChapterLessonViewModel(externalScope = CoroutineScope(Dispatchers.Unconfined))

            // Load Quadratic Equations (chapterId = 4)
            viewModel.loadChapterLesson(chapterId = 4)
            val state = viewModel.uiState.value

            assertEquals("Quadratic Equations", state.chapterTitle)
            assertFalse("Placeholder text must be completely eliminated", state.part1Content.contains("Welcome to this chapter lesson"))
            assertTrue("Part 1 must contain real polynomial definitions", state.part1Content.contains("ax² + bx + c = 0"))
            assertTrue("Part 2 must contain quadratic formula / discriminant", state.part2Content.contains("b² - 4ac"))
            assertTrue("Syllabus topics must be populated", state.syllabus.isNotEmpty())
            assertTrue("Core formulas must be populated", state.coreFormulas.isNotEmpty())
        }
    }

    @Test
    fun testBaseCurriculumResolvesAllSubjectNumericIdsCorrectly() {
        assertEquals(1, BaseCurriculumData.resolveSubjectNumericId("MATH"))
        assertEquals(1, BaseCurriculumData.resolveSubjectNumericId("mathematics"))
        assertEquals(2, BaseCurriculumData.resolveSubjectNumericId("CS"))
        assertEquals(2, BaseCurriculumData.resolveSubjectNumericId("computer science"))
        assertEquals(3, BaseCurriculumData.resolveSubjectNumericId("DS"))
        assertEquals(4, BaseCurriculumData.resolveSubjectNumericId("AI"))
        assertEquals(5, BaseCurriculumData.resolveSubjectNumericId("PHY"))
        assertEquals(6, BaseCurriculumData.resolveSubjectNumericId("CHEM"))
        assertEquals(101, BaseCurriculumData.resolveSubjectNumericId("ext_bio"))
        assertEquals(102, BaseCurriculumData.resolveSubjectNumericId("history"))
        assertEquals(1, BaseCurriculumData.resolveSubjectNumericId("unknown_xyz"))
    }
}
