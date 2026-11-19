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
    fun testBaseCurriculumContainsAll17SamacheerSubjectsWith10MCQsPerChapter() {
        val baseSubjects = BaseCurriculumData.subjects
        assertEquals("Must have 17 Samacheer Kalvi core curriculum subjects", 17, baseSubjects.size)

        val totalChapters = baseSubjects.sumOf { it.chapters.size }
        assertEquals("Must have exactly 248 total syllabus chapters across Class 10, 11 & 12", 248, totalChapters)

        val expectedSubjects = listOf(
            "10th Mathematics",
            "10th Science",
            "10th Social Science",
            "11th Mathematics",
            "11th Physics",
            "11th Chemistry",
            "11th Biology (Botany & Zoology)",
            "11th Computer Science",
            "11th Commerce & Accountancy",
            "11th Economics",
            "12th Mathematics",
            "12th Physics",
            "12th Chemistry",
            "12th Biology (Botany & Zoology)",
            "12th Computer Science",
            "12th Commerce & Accountancy",
            "12th Economics"
        )

        for (name in expectedSubjects) {
            val sub = baseSubjects.firstOrNull { it.name.equals(name, ignoreCase = true) }
            assertNotNull("Base subject $name must exist", sub)
            assertTrue("Subject $name must have chapters", sub!!.chapters.isNotEmpty())

            for (ch in sub.chapters) {
                assertTrue("Chapter ${ch.title} must have 10 MCQs", ch.mcqs.size >= 10)
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
        // Requirement 6: Start with 0 default notes; on-demand note generation
        val seededNotes = BaseCurriculumData.seededNotes
        assertEquals("Must start with 0 default pre-seeded notes (on-demand generation)", 0, seededNotes.size)
    }

    @Test
    fun testSubjectDetailViewModelLoadsAnySubjectDynamically() {
        runBlocking {
            val viewModel = SubjectDetailViewModel(externalScope = CoroutineScope(Dispatchers.Unconfined))

            // 1. Load 10th Mathematics
            viewModel.loadSubject("1001")
            val mathState = viewModel.uiState.value
            assertEquals("10th Mathematics", mathState.title)
            assertEquals("10th Std", mathState.category)
            assertEquals("📐", mathState.iconSymbol)
            assertEquals(8, mathState.chapters.size)
            assertEquals("Relations & Functions", mathState.chapters.first().title)

            // 2. Load 11th Physics
            viewModel.loadSubject("1102")
            val phyState = viewModel.uiState.value
            assertEquals("11th Physics", phyState.title)
            assertEquals("11th Std", phyState.category)
            assertEquals(11, phyState.chapters.size)
            assertEquals("Nature of Physical World & Measurement", phyState.chapters.first().title)

            // 3. Load 12th Chemistry
            viewModel.loadSubject("1203")
            val chemState = viewModel.uiState.value
            assertEquals("12th Chemistry", chemState.title)
            assertEquals("12th Std", chemState.category)
            assertEquals(14, chemState.chapters.size)
            assertEquals("Metallurgy", chemState.chapters.first().title)

            // 4. Load 12th Computer Science
            viewModel.loadSubject("1205")
            val csState = viewModel.uiState.value
            assertEquals("12th Computer Science", csState.title)
            assertEquals("12th Std", csState.category)
            assertEquals(16, csState.chapters.size)
            assertEquals("Function", csState.chapters.first().title)
        }
    }

    @Test
    fun testRoadmapViewModelLoadsDynamicSubjectMilestonesWithoutArtificialIntelligenceHardcoding() {
        runBlocking {
            val viewModel = RoadmapViewModel(externalScope = CoroutineScope(Dispatchers.Unconfined))

            // Test 10th Mathematics Roadmap
            viewModel.loadSubjectRoadmap("1001")
            val mathState = viewModel.uiState.value
            assertEquals("10th Mathematics", mathState.subjectName)
            assertEquals("10th Mathematics Roadmap", mathState.title)
            assertEquals("📐", mathState.subjectIcon)
            assertEquals(8, mathState.milestones.size)
            assertEquals("Relations & Functions", mathState.milestones.first().title)
            assertEquals(100101, mathState.activeChapterId)
            assertTrue(mathState.milestoneSummary.contains("8 milestones"))

            // Test 11th Physics Roadmap
            viewModel.loadSubjectRoadmap("1102")
            val phyState = viewModel.uiState.value
            assertEquals("11th Physics", phyState.subjectName)
            assertEquals("11th Physics Roadmap", phyState.title)
            assertEquals(11, phyState.milestones.size)
            assertEquals("Nature of Physical World & Measurement", phyState.milestones.first().title)

            // Test 12th Computer Science Roadmap
            viewModel.loadSubjectRoadmap("1205")
            val csState = viewModel.uiState.value
            assertEquals("12th Computer Science", csState.subjectName)
            assertEquals("12th Computer Science Roadmap", csState.title)
            assertEquals(16, csState.milestones.size)
            assertEquals("Function", csState.milestones.first().title)
        }
    }

    @Test
    fun testChapterLessonViewModelLoadsRealFormulasAndZeroPlaceholderText() {
        runBlocking {
            val viewModel = ChapterLessonViewModel(externalScope = CoroutineScope(Dispatchers.Unconfined))

            // Load Relations and Functions (chapterId = 100101)
            viewModel.loadChapterLesson(chapterId = 100101)
            val state = viewModel.uiState.value

            assertEquals("Relations & Functions", state.chapterTitle)
            assertFalse("Placeholder text must be completely eliminated", state.part1Content.contains("Welcome to this chapter lesson"))
            assertTrue("Part 1 must contain real definitions", state.part1Content.isNotBlank())
            assertTrue("Part 2 must contain worked examples", state.part2Content.isNotBlank())
            assertTrue("Syllabus topics must be populated", state.syllabus.isNotEmpty())
            assertTrue("Core formulas must be populated", state.coreFormulas.isNotEmpty())
        }
    }

    @Test
    fun testBaseCurriculumResolvesAllSubjectNumericIdsCorrectly() {
        assertEquals(1001, BaseCurriculumData.resolveSubjectNumericId("1001"))
        assertEquals(1001, BaseCurriculumData.resolveSubjectNumericId("10th Mathematics"))
        assertEquals(1002, BaseCurriculumData.resolveSubjectNumericId("1002"))
        assertEquals(1002, BaseCurriculumData.resolveSubjectNumericId("10th Science"))
        assertEquals(1003, BaseCurriculumData.resolveSubjectNumericId("1003"))
        assertEquals(1003, BaseCurriculumData.resolveSubjectNumericId("10th Social Science"))
        assertEquals(1101, BaseCurriculumData.resolveSubjectNumericId("1101"))
        assertEquals(1102, BaseCurriculumData.resolveSubjectNumericId("1102"))
        assertEquals(1103, BaseCurriculumData.resolveSubjectNumericId("1103"))
        assertEquals(1201, BaseCurriculumData.resolveSubjectNumericId("1201"))
        assertEquals(1202, BaseCurriculumData.resolveSubjectNumericId("1202"))
        assertEquals(1205, BaseCurriculumData.resolveSubjectNumericId("1205"))
        assertEquals(1001, BaseCurriculumData.resolveSubjectNumericId("unknown_xyz"))
    }

    @Test
    fun testQuizSubmissionFlowScoreCalculationAndDatabasePersistence() {
        runBlocking {
            val db = QuizLocalDatabase.getInstance(null)
            val viewModel = QuizViewModel(
                quizDao = db,
                externalScope = CoroutineScope(Dispatchers.Unconfined)
            )

            // 1. Load Chapter Quiz for chapterId = 100101 (10th Math Ch 1)
            viewModel.loadChapterQuiz(100101)
            val state = viewModel.uiState.value
            assertEquals(10, state.totalQuestions)
            assertEquals("10_MATH", viewModel.currentSubjectId)
            assertEquals("100101", viewModel.currentChapterId)

            // 2. Select option for question 1
            viewModel.selectOption("A")
            assertEquals("A", viewModel.uiState.value.selectedOptionId)
            assertEquals(0, viewModel.userSelections.value[0]) // Option index 0

            // Advance to question 2
            viewModel.nextQuestion()
            assertEquals(2, viewModel.uiState.value.currentQuestionIndex)

            // Select option for question 2
            viewModel.selectOption("B")
            assertEquals("B", viewModel.uiState.value.selectedOptionId)
            assertEquals(1, viewModel.userSelections.value[1])

            // 3. Submit quiz
            var callbackResultCaptured: com.siamts.synapse.data.model.QuizResultDataDto? = null

            viewModel.submitQuiz { result ->
                callbackResultCaptured = result
            }

            // Verify score calculation
            assertNotNull(callbackResultCaptured)
            val result = callbackResultCaptured!!
            assertEquals(10, result.totalQuestions)
            assertEquals(result.score, result.correctAnswers)
            assertEquals(10 - result.score, result.incorrectAnswers)
            assertEquals((result.score * 100) / 10, result.percentage)

            // Verify local database persistence in 'quiz_records'
            val records = db.getAllRecords()
            assertTrue("Database must contain at least 1 record", records.isNotEmpty())
            val latest = records.first()
            assertEquals("10_MATH", latest.subjectId)
            assertEquals("100101", latest.chapterId)
        }
    }

    @Test
    fun testCurriculumRepositoryNormalizesSubjectIdentifiersCorrectly() {
        assertEquals("1001", CurriculumRepository.normalizeSubjectId("1001"))
        assertEquals("10th mathematics", CurriculumRepository.normalizeSubjectId("10th Mathematics"))
        assertEquals("12th physics", CurriculumRepository.normalizeSubjectId("12th%20Physics"))
    }

    @Test
    fun testCurriculumRepositoryGuaranteesNonEmptyChaptersAndQuizzes() {
        runBlocking {
            val repo = CurriculumRepository()

            // 1. Check known base curriculum subject (10th Mathematics)
            val mathChapters = repo.getChaptersForSubject("1001", "10th Mathematics")
            assertTrue("Mathematics chapters must not be empty", mathChapters.isNotEmpty())
            assertTrue("Mathematics must have at least 8 chapters", mathChapters.size >= 8)
            assertEquals("Relations & Functions", mathChapters.first().title)

            val mathQuizzes = repo.getSubjectQuizzes("1001", "10th Mathematics")
            assertTrue("Mathematics quizzes must not be empty", mathQuizzes.isNotEmpty())
            assertTrue("Quiz question count must be 10", mathQuizzes.first().questionCount >= 10)

            // 2. Check completely unknown / arbitrary subject - dynamic generation fallback
            val dynamicChapters = repo.getChaptersForSubject("unknown_subject_99", "Environmental Engineering")
            assertTrue("Dynamic chapters must never be empty", dynamicChapters.isNotEmpty())
            assertEquals("Must generate exactly 3 fallback chapters", 3, dynamicChapters.size)

            val dynamicQuizzes = repo.getSubjectQuizzes("unknown_subject_99", "Environmental Engineering")
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
            viewModel.loadSubjectQuiz("10th Mathematics")
            assertEquals("10_MATH", viewModel.currentSubjectId)
            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertTrue("10th Mathematics quiz questions must not be empty", state.questions.isNotEmpty())
            assertTrue(state.topicTag.contains("10TH MATHEMATICS"))
        }
    }
}
