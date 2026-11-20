package com.siamts.synapse.data.local

/**
 * Entity representing a real user study session recorded in local SQLite / Room.
 * Starts with 0 records for brand new accounts.
 */
data class StudySessionEntity(
    val id: Long = 0,
    val userId: Int = 1,
    val trackName: String, // "Python & Coding Logic", "SQL & Database Management", "AI & Modern Tech Concepts", "Aptitude & Interview Prep"
    val category: String,  // "Focused Study", "Practice (Quiz)", "Note Making"
    val durationMinutes: Int = 15,
    val studyDate: String, // "YYYY-MM-DD" e.g., "2026-11-20"
    val timestamp: Long = System.currentTimeMillis()
)
