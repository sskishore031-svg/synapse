package com.siamts.synapse

import com.siamts.synapse.data.local.BaseCurriculumData
import com.siamts.synapse.data.local.QuizDao
import com.siamts.synapse.data.local.QuizLocalDatabase
import com.siamts.synapse.data.local.QuizRecordEntity
import com.siamts.synapse.data.repository.CurriculumRepository
import com.siamts.synapse.data.repository.SubjectCatalogRepository
import com.siamts.synapse.ui.lesson.ChapterLessonViewModel
import com.siamts.synapse.ui.quiz.QuizNavEvent
import com.siamts.synapse.ui.quiz.QuizViewModel
import com.siamts.synapse.ui.roadmap.RoadmapViewModel
import com.siamts.synapse.ui.subjects.SubjectDetailViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
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
            assertEquals("Subject $name must have 8 core chapters", 8, sub!!.chapters.size)

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
        assertTrue("Must have at least 48 pre-seeded study notes", seededNotes.size >= 48)

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
            assertEquals(8, mathState.chapters.size)
            assertEquals("Foundations of Algebra, Functions & Set Theory", mathState.chapters.first().title)

            // 2. Load Physics
            viewModel.loadSubject("PHY")
            val phyState = viewModel.uiState.value
            assertEquals("Physics", phyState.title)
            assertEquals("Physical Sciences", phyState.category)
            assertEquals("⚛", phyState.iconSymbol)
            assertEquals(8, phyState.chapters.size)
            assertEquals("Units, Vectors & Kinematics", phyState.chapters.first().title)

            // 3. Load Chemistry
            viewModel.loadSubject("CHEM")
            val chemState = viewModel.uiState.value
            assertEquals("Chemistry", chemState.title)
            assertEquals("Physical Sciences", chemState.category)
            assertEquals("🧪", chemState.iconSymbol)
            assertEquals(8, chemState.chapters.size)
            assertEquals("Atomic Structure & Quantum Numbers", chemState.chapters.first().title)

            // 4. Load Computer Science
            viewModel.loadSubject("CS")
            val csState = viewModel.uiState.value
            assertEquals("Computer Science", csState.title)
            assertEquals("Technology", csState.category)
            assertEquals("</>", csState.iconSymbol)
            assertEquals(8, csState.chapters.size)
            assertEquals("Programming Fundamentals, Algorithms & Complexity (O(n))", csState.chapters.first().title)

            // 5. Load Data Science
            viewModel.loadSubject("DS")
            val dsState = viewModel.uiState.value
            assertEquals("Data Science", dsState.title)
            assertEquals("Data & Analytics", dsState.category)
            assertEquals(8, dsState.chapters.size)

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
            assertEquals(8, mathState.milestones.size)
            assertEquals("Foundations of Algebra, Functions & Set Theory", mathState.milestones.first().title)
            assertEquals(1, mathState.activeChapterId)
            assertTrue(mathState.milestoneSummary.contains("8 milestones"))

            // Test Physics Roadmap
            viewModel.loadSubjectRoadmap("PHY")
            val phyState = viewModel.uiState.value
            assertEquals("Physics", phyState.subjectName)
            assertEquals("Physics Roadmap", phyState.title)
            assertEquals("⚛", phyState.subjectIcon)
            assertEquals(8, phyState.milestones.size)
            assertEquals("Units, Vectors & Kinematics", phyState.milestones.first().title)
            assertNotEquals("Artificial Intelligence", phyState.subjectName)

            // Test Computer Science Roadmap
            viewModel.loadSubjectRoadmap("CS")
            val csState = viewModel.uiState.value
            assertEquals("Computer Science", csState.subjectName)
            assertEquals("Computer Science Roadmap", csState.title)
            assertEquals(8, csState.milestones.size)
            assertEquals("Programming Fundamentals, Algorithms & Complexity (O(n))", csState.milestones.first().title)
        }
    }

    @Test
    fun testChapterLessonViewModelLoadsRealFormulasAndZeroPlaceholderText() {
        runBlocking {
            val viewModel = ChapterLessonViewModel(externalScope = CoroutineScope(Dispatchers.Unconfined))

            // Load Foundations of Algebra, Functions & Set Theory (chapterId = 1)
            viewModel.loadChapterLesson(chapterId = 1)
            val state = viewModel.uiState.value

            assertEquals("Foundations of Algebra, Functions & Set Theory", state.chapterTitle)
            assertFalse("Placeholder text must be completely eliminated", state.part1Content.contains("Welcome to this chapter lesson"))
            assertTrue("Part 1 must contain real definitions", state.part1Content.isNotBlank())
            assertTrue("Part 2 must contain worked examples", state.part2Content.isNotBlank())
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

    @Test
    fun testQuizSubmissionFlowScoreCalculationAndDatabasePersistence() {
        runBlocking {
            val db = QuizLocalDatabase.getInstance(null)
            val viewModel = QuizViewModel(
                quizDao = db,
                externalScope = CoroutineScope(Dispatchers.Unconfined)
            )

            // 1. Load Chapter Quiz for chapterId = 4
            viewModel.loadChapterQuiz(4)
            val state = viewModel.uiState.value
            assertEquals(8, state.totalQuestions)
            assertEquals("MATH", viewModel.currentSubjectId)
            assertEquals("4", viewModel.currentChapterId)

            // 2. Select option for question 1 ("A" is correct in BaseCurriculumData)
            viewModel.selectOption("A")
            assertEquals("A", viewModel.uiState.value.selectedOptionId)
            assertEquals(0, viewModel.userSelections.value[0]) // Option index 0

            // Advance to question 2
            viewModel.nextQuestion()
            assertEquals(2, viewModel.uiState.value.currentQuestionIndex)

            // Select option for question 2 ("A" is correct in BaseCurriculumData)
            viewModel.selectOption("A")
            assertEquals("A", viewModel.uiState.value.selectedOptionId)
            assertEquals(0, viewModel.userSelections.value[1]) // Option index 0

            // 3. Submit quiz
            var navEventCaptured: QuizNavEvent? = null
            var callbackResultCaptured: com.siamts.synapse.data.model.QuizResultDataDto? = null

            viewModel.submitQuiz { result ->
                callbackResultCaptured = result
            }

            // Verify score calculation
            assertNotNull(callbackResultCaptured)
            val result = callbackResultCaptured!!
            assertEquals(8, result.totalQuestions)
            assertTrue("Score must be at least 2", result.score >= 2)
            assertEquals(result.score, result.correctAnswers)
            assertEquals(8 - result.score, result.incorrectAnswers)
            assertEquals((result.score * 100) / 8, result.percentage)

            // Verify local database persistence in 'quiz_records'
            val records = db.getAllRecords()
            assertTrue("Database must contain at least 1 record", records.isNotEmpty())
            val latest = records.first()
            assertEquals("MATH", latest.subjectId)
            assertEquals("4", latest.chapterId)
            assertEquals(result.score, latest.score)
            assertEquals(8, latest.totalQuestions)
            assertTrue("Timestamp must be recent", latest.completedAt > 0)
        }
    }

    @Test
    fun testCurriculumRepositoryNormalizesSubjectIdentifiersCorrectly() {
        assertEquals("math", CurriculumRepository.normalizeSubjectId("MATH"))
        assertEquals("mathematics", CurriculumRepository.normalizeSubjectId("Mathematics"))
        assertEquals("cloud computing", CurriculumRepository.normalizeSubjectId("Cloud%20Computing"))
        assertEquals("ext_ai", CurriculumRepository.normalizeSubjectId("  ext_ai  "))
    }

    @Test
    fun testCurriculumRepositoryGuaranteesNonEmptyChaptersAndQuizzes() {
        runBlocking {
            val repo = CurriculumRepository()

            // 1. Check known base curriculum subject (Mathematics)
            val mathChapters = repo.getChaptersForSubject("MATH", "Mathematics")
            assertTrue("Mathematics chapters must not be empty", mathChapters.isNotEmpty())
            assertTrue("Mathematics must have at least 3 chapters", mathChapters.size >= 3)
            assertEquals("Foundations of Algebra, Functions & Set Theory", mathChapters.first().title)

            val mathQuizzes = repo.getSubjectQuizzes("MATH", "Mathematics")
            assertTrue("Mathematics quizzes must not be empty", mathQuizzes.isNotEmpty())
            assertTrue("Quiz question count must be at least 5", mathQuizzes.first().questionCount >= 5)

            // 2. Check completely unknown / arbitrary subject - dynamic generation fallback
            val dynamicChapters = repo.getChaptersForSubject("unknown_subject_99", "Quantum Machine Learning")
            assertTrue("Dynamic chapters must never be empty", dynamicChapters.isNotEmpty())
            assertEquals("Must generate exactly 3 fallback chapters", 3, dynamicChapters.size)
            assertTrue("Generated title must contain subject name", dynamicChapters.first().title.contains("Quantum Machine Learning") || dynamicChapters.first().title.contains("Fundamentals"))

            val dynamicQuizzes = repo.getSubjectQuizzes("unknown_subject_99", "Quantum Machine Learning")
            assertTrue("Dynamic quizzes must never be empty", dynamicQuizzes.isNotEmpty())
            assertEquals(3, dynamicQuizzes.size)
            assertEquals(5, dynamicQuizzes.first().questionCount)
        }
    }

    @Test
    fun testQuizViewModelDynamicallyResolvesAllSubjectNames() {
        runBlocking {
            val db = QuizLocalDatabase.getInstance(null)
            val viewModel = QuizViewModel(
                quizDao = db,
                externalScope = CoroutineScope(Dispatchers.Unconfined)
            )

            // Test loading via subject name
            viewModel.loadSubjectQuiz("Computer Science")
            assertEquals("CS", viewModel.currentSubjectId)
            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertTrue("CS quiz questions must not be empty", state.questions.isNotEmpty())
            assertTrue(state.topicTag.contains("COMPUTER SCIENCE"))
        }
    }
}
