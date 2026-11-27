package com.siamts.synapse

import com.siamts.synapse.data.local.LessonProgressDao
import com.siamts.synapse.data.local.QuizLocalDatabase
import com.siamts.synapse.data.local.TrackMapping
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TrackAndChapterProgressSyncTest {

    private lateinit var progressDao: LessonProgressDao

    @Before
    fun setUp() = runBlocking {
        // QuizLocalDatabase with null context operates purely in-memory
        progressDao = QuizLocalDatabase.getLessonProgressDao(null)
        progressDao.clearAllProgress()
    }

    @Test
    fun testTrackMapping_normalization() {
        assertEquals(TrackMapping.TRACK_PYTHON, TrackMapping.normalizeTrackId("python"))
        assertEquals(TrackMapping.TRACK_PYTHON, TrackMapping.normalizeTrackId("101"))
        assertEquals(TrackMapping.TRACK_PYTHON, TrackMapping.normalizeTrackId("PYTHON & CODING"))
        assertEquals(TrackMapping.TRACK_PYTHON, TrackMapping.normalizeTrackId("py"))

        assertEquals(TrackMapping.TRACK_SQL, TrackMapping.normalizeTrackId("sql"))
        assertEquals(TrackMapping.TRACK_SQL, TrackMapping.normalizeTrackId("102"))
        assertEquals(TrackMapping.TRACK_SQL, TrackMapping.normalizeTrackId("database"))
        assertEquals(TrackMapping.TRACK_SQL, TrackMapping.normalizeTrackId("db"))

        assertEquals(TrackMapping.TRACK_AI, TrackMapping.normalizeTrackId("ai"))
        assertEquals(TrackMapping.TRACK_AI, TrackMapping.normalizeTrackId("103"))
        assertEquals(TrackMapping.TRACK_AI, TrackMapping.normalizeTrackId("AI & Modern Tech"))

        assertEquals(TrackMapping.TRACK_APTITUDE, TrackMapping.normalizeTrackId("aptitude"))
        assertEquals(TrackMapping.TRACK_APTITUDE, TrackMapping.normalizeTrackId("104"))
        assertEquals(TrackMapping.TRACK_APTITUDE, TrackMapping.normalizeTrackId("interview prep"))
        assertEquals(TrackMapping.TRACK_APTITUDE, TrackMapping.normalizeTrackId("quant"))
    }

    @Test
    fun testTrackMapping_canonicalLessonId() {
        // Chapter indices 1..5 mapped with track
        assertEquals("10101", TrackMapping.canonicalLessonId(1, "python"))
        assertEquals("10105", TrackMapping.canonicalLessonId(5, "python"))
        assertEquals("10202", TrackMapping.canonicalLessonId(2, "sql"))
        assertEquals("10303", TrackMapping.canonicalLessonId(3, "ai"))
        assertEquals("10404", TrackMapping.canonicalLessonId(4, "aptitude"))

        // Already canonical 5-digit IDs pass through directly
        assertEquals("10103", TrackMapping.canonicalLessonId("10103", "python"))
        assertEquals("10205", TrackMapping.canonicalLessonId(10205, "sql"))
        assertEquals("10401", TrackMapping.canonicalLessonId("10401", ""))
    }

    @Test
    fun testTrackMapping_getTrackForChapter() {
        assertEquals(TrackMapping.TRACK_PYTHON, TrackMapping.getTrackForChapter("10101"))
        assertEquals(TrackMapping.TRACK_PYTHON, TrackMapping.getTrackForChapter("10105"))
        assertEquals(TrackMapping.TRACK_SQL, TrackMapping.getTrackForChapter("10203"))
        assertEquals(TrackMapping.TRACK_AI, TrackMapping.getTrackForChapter("10304"))
        assertEquals(TrackMapping.TRACK_APTITUDE, TrackMapping.getTrackForChapter("10402"))
    }

    @Test
    fun testLessonProgress_cleanStartIsZero() = runBlocking {
        val userProgress = progressDao.getProgressForUser("test_user_1")
        assertTrue("New user should start with 0 progress records", userProgress.isEmpty())

        for (trackId in TrackMapping.ALL_TRACK_IDS) {
            val trackProgress = progressDao.getCompletedLessonsForTrack(trackId, "test_user_1")
            assertTrue("Track $trackId should start with 0 completed chapters", trackProgress.isEmpty())
        }
    }

    @Test
    fun testLessonCompletion_marksLessonAndComputesTrackPercentage() = runBlocking {
        val userId = "user_42"

        // Mark Chapter 1 completed in Python
        val progress1 = progressDao.markLessonCompleted("10101", "python", userId)
        assertTrue(progress1.isCompleted)
        assertTrue(progress1.lessonCompleted)
        assertFalse(progress1.quizCompleted)
        assertEquals("10101", progress1.lessonId)
        assertEquals(TrackMapping.TRACK_PYTHON, progress1.trackId)

        // Verify retrieval
        val retrieved = progressDao.getProgress("10101", userId)
        assertNotNull(retrieved)
        assertTrue(retrieved!!.isCompleted)

        // Check track completion count (1 of 5 chapters = 20%)
        val pythonCompleted = progressDao.getCompletedLessonsForTrack("python", userId)
        assertEquals(1, pythonCompleted.size)
        val percentage1 = (pythonCompleted.size * 100) / 5
        assertEquals(20, percentage1)

        // Mark Chapter 2 completed in Python
        val progress2 = progressDao.markLessonCompleted("10102", "python", userId)
        assertTrue(progress2.isCompleted)

        val pythonCompleted2 = progressDao.getCompletedLessonsForTrack("python", userId)
        assertEquals(2, pythonCompleted2.size)
        val percentage2 = (pythonCompleted2.size * 100) / 5
        assertEquals(40, percentage2)
    }

    @Test
    fun testQuizCompletion_marksQuizAndUpdatesStatus() = runBlocking {
        val userId = "user_quiz"

        // Completing the quiz for a chapter marks it complete even if lesson wasn't explicitly marked
        val progress = progressDao.markQuizCompleted("10201", "sql", userId)
        assertTrue(progress.isCompleted)
        assertTrue(progress.quizCompleted)
        assertEquals("10201", progress.lessonId)
        assertEquals(TrackMapping.TRACK_SQL, progress.trackId)

        val sqlCompleted = progressDao.getCompletedLessonsForTrack("sql", userId)
        assertEquals(1, sqlCompleted.size)
        assertEquals(20, (sqlCompleted.size * 100) / 5)
    }

    @Test
    fun testMultiTrackIsolation_andOverallProgressCalculation() = runBlocking {
        val userId = "user_multi"

        // Complete 2 chapters in Python (10101, 10102)
        progressDao.markLessonCompleted("10101", "python", userId)
        progressDao.markLessonCompleted("10102", "python", userId)

        // Complete 1 chapter in SQL (10201)
        progressDao.markLessonCompleted("10201", "sql", userId)

        // Complete 3 chapters in AI (10301, 10302, 10303)
        progressDao.markLessonCompleted("10301", "ai", userId)
        progressDao.markLessonCompleted("10302", "ai", userId)
        progressDao.markLessonCompleted("10303", "ai", userId)

        // Verify each track's isolated counts
        val pyCount = progressDao.getCompletedLessonsForTrack("python", userId).size
        val sqlCount = progressDao.getCompletedLessonsForTrack("sql", userId).size
        val aiCount = progressDao.getCompletedLessonsForTrack("ai", userId).size
        val aptCount = progressDao.getCompletedLessonsForTrack("aptitude", userId).size

        assertEquals(2, pyCount)
        assertEquals(1, sqlCount)
        assertEquals(3, aiCount)
        assertEquals(0, aptCount)

        // Verify percentages per track (out of 5)
        assertEquals(40, (pyCount * 100) / 5) // 2/5 = 40%
        assertEquals(20, (sqlCount * 100) / 5) // 1/5 = 20%
        assertEquals(60, (aiCount * 100) / 5) // 3/5 = 60%
        assertEquals(0, (aptCount * 100) / 5)  // 0/5 = 0%

        // Verify overall completion across all 20 chapters
        val totalCompleted = progressDao.getProgressForUser(userId).size
        assertEquals(6, totalCompleted)
        val overallProgressPercentage = (totalCompleted * 100) / 20 // 6/20 = 30%
        assertEquals(30, overallProgressPercentage)
    }

    @Test
    fun testResetTrackProgress_resetsOnlyTargetTrack() = runBlocking {
        val userId = "user_reset"

        // Add progress to Python and SQL
        progressDao.markLessonCompleted("10101", "python", userId)
        progressDao.markLessonCompleted("10201", "sql", userId)

        assertEquals(1, progressDao.getCompletedLessonsForTrack("python", userId).size)
        assertEquals(1, progressDao.getCompletedLessonsForTrack("sql", userId).size)

        // Reset only Python
        progressDao.resetTrackProgress("python", userId)

        assertEquals(0, progressDao.getCompletedLessonsForTrack("python", userId).size)
        assertEquals(1, progressDao.getCompletedLessonsForTrack("sql", userId).size)
        assertNull(progressDao.getProgress("10101", userId))
        assertNotNull(progressDao.getProgress("10201", userId))
    }

    @Test
    fun testTwoStageChapterProgressAndStartAgainReset() = runBlocking {
        val userId = "user_twostage"

        // 1. Mark Concept Done for Chapter 1 in Python (+10%, 0.5 chapter credit)
        val p1 = progressDao.markLessonCompleted("10101", "python", userId)
        assertTrue(p1.conceptDone)
        assertFalse(p1.quizDone)
        assertTrue(p1.isCompleted)
        assertEquals(1, p1.chapterIndex)

        // Verify retrieval by chapter index
        val chProg1 = progressDao.getChapterProgress("python", 1, userId)
        assertNotNull(chProg1)
        assertTrue(chProg1!!.conceptDone)
        assertFalse(chProg1.quizDone)

        // 2-stage percentage: 0.5 credits / 5 chapters = 10%
        var userProgress = progressDao.getProgressForUser(userId)
        var totalCredits = userProgress.sumOf { (if (it.conceptDone) 0.5 else 0.0) + (if (it.quizDone) 0.5 else 0.0) }
        var percentage = ((totalCredits / 5.0) * 100.0).toInt()
        assertEquals(10, percentage)

        // 2. Mark Quiz Done for Chapter 1 (+10%, another 0.5 chapter credit -> total 1.0 credit = 20%)
        val q1 = progressDao.markQuizCompleted("10101", "python", userId)
        assertTrue(q1.conceptDone)
        assertTrue(q1.quizDone)
        assertTrue(q1.isCompleted)

        userProgress = progressDao.getProgressForUser(userId)
        totalCredits = userProgress.sumOf { (if (it.conceptDone) 0.5 else 0.0) + (if (it.quizDone) 0.5 else 0.0) }
        percentage = ((totalCredits / 5.0) * 100.0).toInt()
        assertEquals(20, percentage)

        // 3. Navigation test: Navigating to Chapter 2 and back to Chapter 1 preserves state
        val p2 = progressDao.markLessonCompleted("10102", "python", userId)
        assertEquals(2, p2.chapterIndex)

        // Back to Chapter 1: must be completed with conceptDone & quizDone intact
        val backToCh1 = progressDao.getChapterProgress("python", 1, userId)
        assertNotNull(backToCh1)
        assertTrue(backToCh1!!.conceptDone)
        assertTrue(backToCh1.quizDone)
        assertTrue(backToCh1.isCompleted)

        // 4. Test "Start Again" (resetChapterProgress): resets only Chapter 1
        progressDao.resetChapterProgress("python", 1, userId)

        val resetCh1 = progressDao.getChapterProgress("python", 1, userId)
        assertNull(resetCh1)

        // Chapter 2 must still remain intact!
        val ch2Retained = progressDao.getChapterProgress("python", 2, userId)
        assertNotNull(ch2Retained)
        assertTrue(ch2Retained!!.conceptDone)

        // Recalculated percentage: Chapter 2 has 0.5 credit = 10%
        userProgress = progressDao.getProgressForUser(userId)
        totalCredits = userProgress.sumOf { (if (it.conceptDone) 0.5 else 0.0) + (if (it.quizDone) 0.5 else 0.0) }
        percentage = ((totalCredits / 5.0) * 100.0).toInt()
        assertEquals(10, percentage)
    }

    @Test
    fun testAll8SubjectsSupported() = runBlocking {
        val all8 = listOf(
            "python", "sql", "ai", "aptitude",
            "dsa", "java_oops", "os_cn", "android_dev"
        )
        val userId = "user_all8"

        for (track in all8) {
            val prog = progressDao.markLessonCompleted("1", track, userId)
            assertEquals(track, prog.trackId)
            assertEquals(1, prog.chapterIndex)
            assertTrue(prog.conceptDone)

            val fetched = progressDao.getChapterProgress(track, 1, userId)
            assertNotNull("Track $track should be retrievable", fetched)
            assertTrue(fetched!!.conceptDone)
        }
    }

    @Test
    fun testQuizHubGeneralQuizAdvancesNextUncompletedChapter() = runBlocking {
        val userId = "user_hub_quiz"

        // 1. First general quiz finished for Python: should mark chapter 1 quizDone = true (+10%)
        var targetIdx = 1
        for (idx in 1..5) {
            val prog = progressDao.getChapterProgress("python", idx, userId)
            if (prog?.quizDone != true) {
                targetIdx = idx
                break
            }
        }
        assertEquals(1, targetIdx)
        progressDao.markQuizDone(userId, "python", targetIdx)

        val ch1 = progressDao.getChapterProgress("python", 1, userId)
        assertNotNull(ch1)
        assertTrue(ch1!!.quizDone)

        // 2. Second general quiz finished for Python: should mark chapter 2 quizDone = true (+10% -> 20%)
        for (idx in 1..5) {
            val prog = progressDao.getChapterProgress("python", idx, userId)
            if (prog?.quizDone != true) {
                targetIdx = idx
                break
            }
        }
        assertEquals(2, targetIdx)
        progressDao.markQuizDone(userId, "python", targetIdx)

        val ch2 = progressDao.getChapterProgress("python", 2, userId)
        assertNotNull(ch2)
        assertTrue(ch2!!.quizDone)

        // 3. User finishes 3 more general quizzes: chapters 3, 4, 5
        for (q in 3..5) {
            for (idx in 1..5) {
                val prog = progressDao.getChapterProgress("python", idx, userId)
                if (prog?.quizDone != true) {
                    targetIdx = idx
                    break
                }
            }
            progressDao.markQuizDone(userId, "python", targetIdx)
        }

        // Verify all 5 chapters have quizDone = true
        for (idx in 1..5) {
            val p = progressDao.getChapterProgress("python", idx, userId)
            assertNotNull(p)
            assertTrue("Chapter $idx should have quizDone = true", p!!.quizDone)
        }

        val allProg = progressDao.getProgressForUser(userId)
        val totalCredits = allProg.sumOf { (if (it.conceptDone) 0.5 else 0.0) + (if (it.quizDone) 0.5 else 0.0) }
        val pct = ((totalCredits / 5.0) * 100.0).toInt()
        assertEquals(50, pct) // 5 quizzes done = 50%
    }

    @Test
    fun testBackfillQuizRecordsFromDatabase() = runBlocking {
        val userId = "user_backfill"
        val quizDao = QuizLocalDatabase.getInstance(null)

        // Simulate 6 completed quizzes for Python in QuizHub / quiz_records
        for (i in 1..6) {
            quizDao.insertQuizRecord(
                com.siamts.synapse.data.local.QuizRecordEntity(
                    subjectId = "Python & Coding Logic", // Un-normalized title from old QuizHub
                    chapterId = "0", // General track quiz
                    score = 4,
                    totalQuestions = 5,
                    completedAt = System.currentTimeMillis()
                )
            )
        }

        // Run backfill
        com.siamts.synapse.data.local.SynapseDatabase.backfillQuizProgressFromQuizRecords(userId, null)

        // Verify that chapters 1..5 now have quizDone = true
        for (idx in 1..5) {
            val prog = progressDao.getChapterProgress("python", idx, userId)
            assertNotNull("Chapter $idx should be backfilled", prog)
            assertTrue("Chapter $idx must have quizDone = true", prog!!.quizDone)
        }
    }
}
