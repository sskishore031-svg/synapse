package com.siamts.synapse.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.siamts.synapse.SynapseApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Local SQLite & In-Memory fallback implementation of QuizDao for 'quiz_records' table.
 */
class QuizLocalDatabase private constructor(context: Context?) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
), QuizDao {

    // Thread-safe in-memory cache and JVM unit-test fallback
    private val inMemoryRecords = CopyOnWriteArrayList<QuizRecordEntity>()
    private val hasAndroidContext = context != null

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS quiz_records (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                subject_id TEXT NOT NULL,
                chapter_id TEXT NOT NULL,
                score INTEGER NOT NULL,
                total_questions INTEGER NOT NULL,
                completed_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS quiz_records")
        onCreate(db)
    }

    override suspend fun insertQuizRecord(record: QuizRecordEntity): Long = withContext(Dispatchers.IO) {
        val assignedId = System.currentTimeMillis()
        val recordWithId = record.copy(id = if (record.id > 0) record.id else assignedId)
        inMemoryRecords.add(recordWithId)

        if (hasAndroidContext) {
            try {
                val db = writableDatabase
                val values = ContentValues().apply {
                    put("subject_id", record.subjectId)
                    put("chapter_id", record.chapterId)
                    put("score", record.score)
                    put("total_questions", record.totalQuestions)
                    put("completed_at", record.completedAt)
                }
                return@withContext db.insert("quiz_records", null, values)
            } catch (_: Throwable) {
                // Return assigned in-memory ID if SQLite fails
                return@withContext assignedId
            }
        }
        assignedId
    }

    override suspend fun getAllRecords(): List<QuizRecordEntity> = withContext(Dispatchers.IO) {
        if (!hasAndroidContext) {
            return@withContext inMemoryRecords.toList()
        }

        try {
            val db = readableDatabase
            val cursor = db.query(
                "quiz_records",
                null,
                null,
                null,
                null,
                null,
                "completed_at DESC"
            )
            val list = mutableListOf<QuizRecordEntity>()
            cursor.use { c ->
                val idCol = c.getColumnIndex("id")
                val subCol = c.getColumnIndex("subject_id")
                val chapCol = c.getColumnIndex("chapter_id")
                val scoreCol = c.getColumnIndex("score")
                val totalCol = c.getColumnIndex("total_questions")
                val dateCol = c.getColumnIndex("completed_at")

                while (c.moveToNext()) {
                    list.add(
                        QuizRecordEntity(
                            id = if (idCol >= 0) c.getLong(idCol) else 0L,
                            subjectId = if (subCol >= 0) c.getString(subCol) else "",
                            chapterId = if (chapCol >= 0) c.getString(chapCol) else "",
                            score = if (scoreCol >= 0) c.getInt(scoreCol) else 0,
                            totalQuestions = if (totalCol >= 0) c.getInt(totalCol) else 0,
                            completedAt = if (dateCol >= 0) c.getLong(dateCol) else 0L
                        )
                    )
                }
            }
            if (list.isNotEmpty()) list else inMemoryRecords.toList()
        } catch (_: Throwable) {
            inMemoryRecords.toList()
        }
    }

    override suspend fun getRecordsByChapter(chapterId: String): List<QuizRecordEntity> = withContext(Dispatchers.IO) {
        getAllRecords().filter { it.chapterId == chapterId }
    }

    override suspend fun getRecordsBySubject(subjectId: String): List<QuizRecordEntity> = withContext(Dispatchers.IO) {
        getAllRecords().filter { it.subjectId == subjectId }
    }

    companion object {
        private const val DATABASE_NAME = "synapse_quiz.db"
        private const val DATABASE_VERSION = 1

        @Volatile
        private var INSTANCE: QuizLocalDatabase? = null

        fun getInstance(context: Context? = try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }): QuizDao {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: QuizLocalDatabase(context).also { INSTANCE = it }
            }
        }
    }
}
