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
}
