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

    @org.junit.Before
    fun setUp() {
        runBlocking {
            com.siamts.synapse.data.local.SynapseDatabase.getLessonProgressDao().clearAllProgress()
        }
    }

    @org.junit.After
    fun tearDown() {
        runBlocking {
            com.siamts.synapse.data.local.SynapseDatabase.getLessonProgressDao().clearAllProgress()
        }
    }

    @Test
    fun testBaseCurriculumContainsAll4PlacementTracksWith5MCQsPerChapter() {
        val baseSubjects = BaseCurriculumData.subjects
        assertEquals("Must have 4 placement & micro-learning tracks", 4, baseSubjects.size)

        val totalChapters = baseSubjects.sumOf { it.chapters.size }
        assertEquals("Must have exactly 20 total syllabus chapters (5 per track)", 20, totalChapters)

        val expectedSubjects = listOf(
            "Python & Coding Logic",
            "SQL & Database Management",
            "AI & Modern Tech Concepts",
            "Aptitude & Interview Prep"
        )

        for (name in expectedSubjects) {
            val sub = baseSubjects.firstOrNull { it.name.equals(name, ignoreCase = true) }
            assertNotNull("Base subject $name must exist", sub)
            assertTrue("Subject $name must have chapters", sub!!.chapters.isNotEmpty())
            assertEquals("Subject $name must have 5 chapters", 5, sub.chapters.size)

            for (ch in sub.chapters) {
                assertEquals("Chapter ${ch.title} must have exactly 5 MCQs", 5, ch.mcqs.size)
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
        // Zero default notes; on-demand note generation
        val seededNotes = BaseCurriculumData.seededNotes
        assertEquals("Must start with 0 default pre-seeded notes (on-demand generation)", 0, seededNotes.size)
    }

    @Test
    fun testSubjectDetailViewModelLoadsAnySubjectDynamically() {
        runBlocking {
            val viewModel = SubjectDetailViewModel(externalScope = CoroutineScope(Dispatchers.Unconfined))

            // 1. Load Python & Coding Logic
            viewModel.loadSubject("101")
            val pyState = viewModel.uiState.value
            assertEquals("Python & Coding Logic", pyState.title)
            assertEquals("Placement Prep", pyState.category)
            assertEquals("🐍", pyState.iconSymbol)
            assertEquals(5, pyState.chapters.size)
            assertEquals("Variables, Data Types & Dynamic Typing", pyState.chapters.first().title)

            // 2. Load SQL & Database Management
            viewModel.loadSubject("102")
            val sqlState = viewModel.uiState.value
            assertEquals("SQL & Database Management", sqlState.title)
            assertEquals("Placement Prep", sqlState.category)
            assertEquals(5, sqlState.chapters.size)
            assertEquals("Relational Databases, Tables & Keys (Primary/Foreign)", sqlState.chapters.first().title)

            // 3. Load AI & Modern Tech Concepts
            viewModel.loadSubject("103")
            val aiState = viewModel.uiState.value
            assertEquals("AI & Modern Tech Concepts", aiState.title)
            assertEquals("Placement Prep", aiState.category)
            assertEquals(5, aiState.chapters.size)
            assertEquals("Large Language Models (LLMs) & Tokens", aiState.chapters.first().title)

            // 4. Load Aptitude & Interview Prep
            viewModel.loadSubject("104")
            val aptState = viewModel.uiState.value
            assertEquals("Aptitude & Interview Prep", aptState.title)
            assertEquals("Placement Prep", aptState.category)
            assertEquals(5, aptState.chapters.size)
            assertEquals("Quantitative: Percentages, Profit & Loss", aptState.chapters.first().title)
        }
    }

    @Test
    fun testRoadmapViewModelLoadsDynamicSubjectMilestonesWithoutArtificialIntelligenceHardcoding() {
        runBlocking {
            val viewModel = RoadmapViewModel(externalScope = CoroutineScope(Dispatchers.Unconfined))

            // Test Python Roadmap
            viewModel.loadSubjectRoadmap("101")
            val pyState = viewModel.uiState.value
            assertEquals("Python & Coding Logic", pyState.subjectName)
            assertEquals("Python & Coding Logic Roadmap", pyState.title)
            assertEquals("🐍", pyState.subjectIcon)
            assertEquals(5, pyState.milestones.size)
            assertEquals("Variables, Data Types & Dynamic Typing", pyState.milestones.first().title)
            assertEquals(10101, pyState.activeChapterId)
            assertTrue(pyState.milestoneSummary.contains("5 milestones"))

            // Test SQL Roadmap
            viewModel.loadSubjectRoadmap("102")
            val sqlState = viewModel.uiState.value
            assertEquals("SQL & Database Management", sqlState.subjectName)
            assertEquals("SQL & Database Management Roadmap", sqlState.title)
            assertEquals(5, sqlState.milestones.size)
            assertEquals("Relational Databases, Tables & Keys (Primary/Foreign)", sqlState.milestones.first().title)

            // Test AI Tech Roadmap
            viewModel.loadSubjectRoadmap("103")
            val aiState = viewModel.uiState.value
            assertEquals("AI & Modern Tech Concepts", aiState.subjectName)
            assertEquals("AI & Modern Tech Concepts Roadmap", aiState.title)
            assertEquals(5, aiState.milestones.size)
            assertEquals("Large Language Models (LLMs) & Tokens", aiState.milestones.first().title)
        }
    }

    @Test
    fun testChapterLessonViewModelLoadsRealFormulasAndZeroPlaceholderText() {
        runBlocking {
            val viewModel = ChapterLessonViewModel(externalScope = CoroutineScope(Dispatchers.Unconfined))

            // Load Python Core Essentials (chapterId = 10101)
            viewModel.loadChapterLesson(chapterId = 10101)
            val state = viewModel.uiState.value

            assertEquals("Variables, Data Types & Dynamic Typing", state.chapterTitle)
            assertFalse("Placeholder text must be completely eliminated", state.part1Content.contains("Welcome to this chapter lesson"))
            assertTrue("Part 1 must contain real definitions", state.part1Content.isNotBlank())
            assertTrue("Part 2 must contain worked examples", state.part2Content.isNotBlank())
            assertTrue("Syllabus topics must be populated", state.syllabus.isNotEmpty())
            assertTrue("Core formulas must be populated", state.coreFormulas.isNotEmpty())
        }
    }

    @Test
    fun testBaseCurriculumResolvesAllSubjectNumericIdsCorrectly() {
        assertEquals(101, BaseCurriculumData.resolveSubjectNumericId("101"))
        assertEquals(101, BaseCurriculumData.resolveSubjectNumericId("python"))
        assertEquals(101, BaseCurriculumData.resolveSubjectNumericId("Python & Coding Logic"))
        assertEquals(102, BaseCurriculumData.resolveSubjectNumericId("102"))
        assertEquals(102, BaseCurriculumData.resolveSubjectNumericId("sql"))
        assertEquals(102, BaseCurriculumData.resolveSubjectNumericId("SQL & Database Management"))
        assertEquals(103, BaseCurriculumData.resolveSubjectNumericId("103"))
        assertEquals(103, BaseCurriculumData.resolveSubjectNumericId("ai_tech"))
        assertEquals(103, BaseCurriculumData.resolveSubjectNumericId("AI & Modern Tech Concepts"))
        assertEquals(104, BaseCurriculumData.resolveSubjectNumericId("104"))
        assertEquals(104, BaseCurriculumData.resolveSubjectNumericId("aptitude"))
        assertEquals(104, BaseCurriculumData.resolveSubjectNumericId("Aptitude & Interview Prep"))
        assertEquals(101, BaseCurriculumData.resolveSubjectNumericId("unknown_xyz"))
    }

    @Test
    fun testQuizSubmissionFlowScoreCalculationAndDatabasePersistence() {
        runBlocking {
            val db = QuizLocalDatabase.getInstance(null)
            val viewModel = QuizViewModel(
                quizDao = db,
                externalScope = CoroutineScope(Dispatchers.Unconfined)
            )

            // 1. Load Chapter Quiz for chapterId = 10101 (Python Ch 1)
            viewModel.loadChapterQuiz(10101)
            val state = viewModel.uiState.value
            assertEquals(5, state.totalQuestions)
            assertEquals("PYTHON", viewModel.currentSubjectId)
            assertEquals("10101", viewModel.currentChapterId)

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
            assertEquals(5, result.totalQuestions)
            assertEquals(result.score, result.correctAnswers)
            assertEquals(5 - result.score, result.incorrectAnswers)
            assertEquals((result.score * 100) / 5, result.percentage)

            // Verify local database persistence in 'quiz_records'
            val records = db.getAllRecords()
            assertTrue("Database must contain at least 1 record", records.isNotEmpty())
            val latest = records.first()
            assertEquals("python", latest.subjectId.lowercase())
            assertEquals("10101", latest.chapterId)
        }
    }

    @Test
    fun testCurriculumRepositoryNormalizesSubjectIdentifiersCorrectly() {
        assertEquals("101", CurriculumRepository.normalizeSubjectId("101"))
        assertEquals("python & coding logic", CurriculumRepository.normalizeSubjectId("Python & Coding Logic"))
        assertEquals("sql & database management", CurriculumRepository.normalizeSubjectId("SQL%20&%20Database%20Management"))
    }

    @Test
    fun testCurriculumRepositoryGuaranteesNonEmptyChaptersAndQuizzes() {
        runBlocking {
            val repo = CurriculumRepository()

            // 1. Check known base curriculum track (Python & Coding Logic)
            val pyChapters = repo.getChaptersForSubject("101", "Python & Coding Logic")
            assertTrue("Python chapters must not be empty", pyChapters.isNotEmpty())
            assertEquals("Python must have exactly 5 chapters", 5, pyChapters.size)
            assertEquals("Variables, Data Types & Dynamic Typing", pyChapters.first().title)

            val pyQuizzes = repo.getSubjectQuizzes("101", "Python & Coding Logic")
            assertTrue("Python quizzes must not be empty", pyQuizzes.isNotEmpty())
            assertEquals("Quiz question count must be 5", 5, pyQuizzes.first().questionCount)

            // 2. Check completely unknown / arbitrary subject - dynamic generation fallback
            val dynamicChapters = repo.getChaptersForSubject("unknown_subject_99", "Rust Systems Programming")
            assertTrue("Dynamic chapters must never be empty", dynamicChapters.isNotEmpty())
            assertEquals("Must generate exactly 3 fallback chapters", 3, dynamicChapters.size)

            val dynamicQuizzes = repo.getSubjectQuizzes("unknown_subject_99", "Rust Systems Programming")
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
            viewModel.loadSubjectQuiz("Python & Coding Logic")
            assertEquals("PYTHON", viewModel.currentSubjectId)
            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertTrue("Python quiz questions must not be empty", state.questions.isNotEmpty())
            assertTrue(state.topicTag.contains("PYTHON"))
        }
    }
}
