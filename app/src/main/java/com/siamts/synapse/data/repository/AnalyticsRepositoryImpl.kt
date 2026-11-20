package com.siamts.synapse.data.repository

import com.siamts.synapse.data.local.AuthSessionState
import com.siamts.synapse.data.local.NotesDao
import com.siamts.synapse.data.local.QuizDao
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.local.StudySessionDao
import com.siamts.synapse.data.local.StudySessionEntity
import com.siamts.synapse.data.local.SynapseDatabase
import com.siamts.synapse.data.model.AnalyticsData
import com.siamts.synapse.data.model.LogSessionRequest
import com.siamts.synapse.data.model.SubjectBreakdownItem
import com.siamts.synapse.data.model.TimeDistributionItem
import com.siamts.synapse.data.model.WeeklySubjectComparisonItem
import com.siamts.synapse.data.remote.AnalyticsApi
import com.siamts.synapse.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AnalyticsRepositoryImpl(
    private val analyticsApi: AnalyticsApi = RetrofitClient.analyticsApi,
    private val studySessionDao: StudySessionDao = SynapseDatabase.getStudySessionDao(),
    private val quizDao: QuizDao = SynapseDatabase.getInstance(),
    private val notesDao: NotesDao = SynapseDatabase.getNotesDao(),
    private val sessionManager: SessionManager? = runCatching { SessionManager.getInstance() }.getOrNull()
) : AnalyticsRepository {

    override suspend fun getAnalytics(
        userId: Int?,
        email: String?,
        timeFilter: String,
        month: String?
    ): Result<AnalyticsData> = withContext(Dispatchers.IO) {
        var targetUserId = userId
        var targetEmail = email

        if ((targetUserId == null || targetUserId <= 0) && targetEmail.isNullOrBlank()) {
            sessionManager?.let { sm ->
                val session = sm.sessionStateFlow.first()
                if (session is AuthSessionState.Authenticated) {
                    targetUserId = session.user.id
                    targetEmail = session.user.email
                }
            }
        }

        val targetMonth = month ?: SimpleDateFormat("yyyy-MM", Locale.US).format(Date())

        // 1. Try remote backend endpoint (/auth/analytics.php -> /analytics.php)
        try {
            val response = try {
                analyticsApi.getAnalytics(
                    userId = targetUserId,
                    email = targetEmail,
                    timeFilter = timeFilter,
                    month = targetMonth
                )
            } catch (_: Exception) {
                analyticsApi.getAnalyticsRoot(
                    userId = targetUserId,
                    email = targetEmail,
                    timeFilter = timeFilter,
                    month = targetMonth
                )
            }

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.status.equals("success", ignoreCase = true) && body.data != null) {
                    return@withContext Result.success(body.data)
                }
            }
        } catch (_: Exception) {}

        // 2. Offline / Local fallback computed directly from local SQLite / Room
        try {
            val localData = computeLocalAnalytics(timeFilter, targetMonth)
            return@withContext Result.success(localData)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    override suspend fun logSession(
        trackName: String,
        category: String,
        durationMinutes: Int,
        studyDate: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val sDate = studyDate ?: sdf.format(Date())
        var targetUserId = 1
        var targetEmail = ""

        sessionManager?.let { sm ->
            val session = sm.sessionStateFlow.first()
            if (session is AuthSessionState.Authenticated) {
                targetUserId = session.user.id
                targetEmail = session.user.email
            }
        }

        val normalizedTrack = normalizeTrack(trackName)

        // 1. Save locally in SQLite/Room
        try {
            val entity = StudySessionEntity(
                userId = targetUserId,
                trackName = normalizedTrack,
                category = category,
                durationMinutes = durationMinutes.coerceAtLeast(1),
                studyDate = sDate,
                timestamp = System.currentTimeMillis()
            )
            studySessionDao.insertSession(entity)
        } catch (_: Throwable) {}

        // 2. Sync to remote backend asynchronously
        try {
            val req = LogSessionRequest(
                userId = targetUserId,
                trackName = normalizedTrack,
                category = category,
                durationMinutes = durationMinutes,
                studyDate = sDate
            )
            try {
                analyticsApi.logSession(request = req)
            } catch (_: Exception) {
                analyticsApi.logSessionRoot(request = req)
            }
        } catch (_: Throwable) {}

        Result.success(Unit)
    }

    /**
     * Computes fully reactive real-time analytics data locally from Room DB
     * starting at 0 for clean accounts.
     */
    private suspend fun computeLocalAnalytics(timeFilter: String, selectedMonth: String): AnalyticsData {
        val allSessions = studySessionDao.getAllSessions()
        val allActiveDates = studySessionDao.getAllActiveDates()

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val todayStr = sdf.format(Date())

        // Calculate current & best streak
        val sortedDates = allActiveDates.distinct().sorted()
        var currentStreak = 0
        var bestStreak = 0

        if (sortedDates.isNotEmpty()) {
            val descDates = sortedDates.reversed()
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = sdf.format(cal.time)

            var cursor: String? = when {
                descDates.contains(todayStr) -> todayStr
                descDates.contains(yesterdayStr) -> yesterdayStr
                else -> null
            }

            if (cursor != null) {
                val tempCal = Calendar.getInstance()
                tempCal.time = sdf.parse(cursor) ?: Date()
                while (descDates.contains(sdf.format(tempCal.time))) {
                    currentStreak++
                    tempCal.add(Calendar.DAY_OF_YEAR, -1)
                }
            }

            var streakCount = 0
            var prevTime: Long? = null
            for (dStr in sortedDates) {
                val t = sdf.parse(dStr)?.time ?: continue
                if (prevTime == null) {
                    streakCount = 1
                } else {
                    val diff = Math.round((t - prevTime).toDouble() / 86400000.0).toInt()
                    if (diff == 1) {
                        streakCount++
                    } else if (diff > 1) {
                        streakCount = 1
                    }
                }
                prevTime = t
                if (streakCount > bestStreak) bestStreak = streakCount
            }
        }

        // Determine date range for timeFilter
        val (startDate, endDate) = getFilterDateRange(timeFilter)

        val filteredSessions = allSessions.filter { it.studyDate in startDate..endDate }
        val totalMinutes = filteredSessions.sumOf { it.durationMinutes }

        // Track breakdown
        val trackMap = mutableMapOf(
            "Python & Coding Logic" to 0,
            "SQL & Database Management" to 0,
            "AI & Modern Tech Concepts" to 0,
            "Aptitude & Interview Prep" to 0
        )
        val categoryMap = mutableMapOf(
            "Focused Study" to 0,
            "Practice (Quiz)" to 0,
            "Note Making" to 0
        )

        filteredSessions.forEach { s ->
            val norm = normalizeTrack(s.trackName)
            trackMap[norm] = (trackMap[norm] ?: 0) + s.durationMinutes
            categoryMap[s.category] = (categoryMap[s.category] ?: 0) + s.durationMinutes
        }

        val subjectBreakdown = listOf(
            Triple("Python & Coding Logic", "PYTHON", "🐍"),
            Triple("SQL & Database Management", "SQL", "🗄️"),
            Triple("AI & Modern Tech Concepts", "AI_TECH", "⚡"),
            Triple("Aptitude & Interview Prep", "APTITUDE", "🎯")
        ).map { (name, slug, icon) ->
            val mins = trackMap[name] ?: 0
            val pct = if (totalMinutes > 0) Math.round((mins.toFloat() / totalMinutes) * 100) else 0
            SubjectBreakdownItem(
                name = name,
                slug = slug,
                icon = icon,
                colorHex = when (slug) {
                    "PYTHON" -> "#FF6B00"
                    "SQL" -> "#FFAA00"
                    "AI_TECH" -> "#8B5CF6"
                    else -> "#10B981"
                },
                minutes = mins,
                formattedTime = formatMinutes(mins),
                percentage = pct,
                fraction = if (totalMinutes > 0) mins.toFloat() / totalMinutes else 0f
            )
        }

        // Time distribution
        val timeDistribution = listOf(
            Triple("Focused Study", "#FF6B00", categoryMap["Focused Study"] ?: 0),
            Triple("Practice (Quiz)", "#FFAA00", categoryMap["Practice (Quiz)"] ?: 0),
            Triple("Note Making", "#8B5CF6", categoryMap["Note Making"] ?: 0)
        ).map { (label, color, mins) ->
            val pct = if (totalMinutes > 0) Math.round((mins.toFloat() / totalMinutes) * 100) else 0
            TimeDistributionItem(
                label = label,
                minutes = mins,
                formattedTime = formatMinutes(mins),
                percentage = pct,
                fraction = if (totalMinutes > 0) mins.toFloat() / totalMinutes else 0f,
                colorHex = color
            )
        }

        // Mon-Sun Daily Bar Heights
        val (dailyMinutes, chartHeights) = computeWeeklyHeights(filteredSessions, timeFilter)

        // Most productive day
        val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val maxIdx = dailyMinutes.indices.maxByOrNull { dailyMinutes[it] } ?: 0
        val mostProductiveDay = if (totalMinutes > 0) dayNames[maxIdx] else "None"
        val mostProductiveDayText = if (totalMinutes > 0) "${formatMinutes(dailyMinutes[maxIdx])} spent" else "0m spent"

        // Average daily time
        val avgMins = if (timeFilter == "this_month") {
            val dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).coerceAtLeast(1)
            totalMinutes / dayOfMonth
        } else {
            totalMinutes / 7
        }

        // Consistency & Daily Goals in current week
        val activeThisWeek = countActiveDaysThisWeek(allActiveDates)
        val consistencyPct = Math.round((activeThisWeek / 7f) * 100)

        // Active days for selected month
        val monthActiveDays = allActiveDates
            .filter { it.startsWith(selectedMonth) }
            .mapNotNull { it.substringAfterLast("-").toIntOrNull() }
            .distinct()

        // Dynamic insights
        val topTrack = subjectBreakdown.maxByOrNull { it.minutes }
        val insights = if (totalMinutes > 0 && topTrack != null && topTrack.minutes > 0) {
            "You spent the most time on ${topTrack.name} this period (${topTrack.percentage}% of your ${formatMinutes(avgMins)}/day study time). Keep up the great pace!"
        } else {
            "Start your first micro-lesson, quiz, or smart note to unlock personalized study insights!"
        }

        return AnalyticsData(
            userId = 1,
            timeFilter = timeFilter,
            selectedMonth = selectedMonth,
            totalMinutes = totalMinutes,
            formattedStudyTime = formatMinutes(totalMinutes),
            averageDailyTime = formatMinutes(avgMins),
            mostProductiveDay = mostProductiveDay,
            mostProductiveDayText = mostProductiveDayText,
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            weeklyConsistency = "$consistencyPct%",
            weeklyConsistencyPercentage = consistencyPct,
            completedDailyGoals = "$activeThisWeek / 7",
            completedDailyGoalsCount = activeThisWeek,
            activeStudyDates = allActiveDates,
            monthActiveDays = monthActiveDays,
            chartDays = dayNames,
            chartHeights = chartHeights,
            chartMinutes = dailyMinutes,
            timeDistribution = timeDistribution,
            subjectBreakdown = subjectBreakdown,
            weeklySubjectComparison = computeWeeklySubjectComparison(filteredSessions),
            insights = insights
        )
    }

    private fun computeWeeklyHeights(sessions: List<StudySessionEntity>, timeFilter: String): Pair<List<Int>, List<Float>> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY

        if (timeFilter == "last_week") {
            cal.add(Calendar.WEEK_OF_YEAR, -1)
        }
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val dailyMinutes = MutableList(7) { 0 }
        val dateToDayIndex = mutableMapOf<String, Int>()

        for (i in 0 until 7) {
            dateToDayIndex[sdf.format(cal.time)] = i
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }

        sessions.forEach { s ->
            val idx = dateToDayIndex[s.studyDate]
            if (idx != null) {
                dailyMinutes[idx] = dailyMinutes[idx] + s.durationMinutes
            }
        }

        val maxMins = dailyMinutes.maxOrNull() ?: 0
        val heights = dailyMinutes.map { mins ->
            if (maxMins <= 0 || mins <= 0) 0f
            else (mins.toFloat() / maxMins).coerceIn(0.12f, 1f)
        }

        return Pair(dailyMinutes, heights)
    }

    private fun computeWeeklySubjectComparison(sessions: List<StudySessionEntity>): List<WeeklySubjectComparisonItem> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val dateToDayIndex = mutableMapOf<String, Int>()
        for (i in 0 until 7) {
            dateToDayIndex[sdf.format(cal.time)] = i
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }

        val tracks = listOf(
            Triple("Python & Coding Logic", "PYTHON", "#FF6B00"),
            Triple("SQL & Database Management", "SQL", "#FFAA00"),
            Triple("AI & Modern Tech Concepts", "AI_TECH", "#8B5CF6"),
            Triple("Aptitude & Interview Prep", "APTITUDE", "#10B981")
        )

        val trackDaily = mutableMapOf<String, MutableList<Int>>()
        tracks.forEach { (name, _, _) -> trackDaily[name] = MutableList(7) { 0 } }

        sessions.forEach { s ->
            val norm = normalizeTrack(s.trackName)
            val dayIdx = dateToDayIndex[s.studyDate]
            if (dayIdx != null && trackDaily.containsKey(norm)) {
                trackDaily[norm]!![dayIdx] = trackDaily[norm]!![dayIdx] + s.durationMinutes
            }
        }

        val maxMins = trackDaily.values.flatMap { it }.maxOrNull() ?: 1
        return tracks.map { (name, slug, color) ->
            val minsList = trackDaily[name] ?: List(7) { 0 }
            val heights = minsList.map { m ->
                if (maxMins <= 0 || m <= 0) 0f else (m.toFloat() / maxMins).coerceIn(0.1f, 1f)
            }
            WeeklySubjectComparisonItem(
                name = name,
                slug = slug,
                colorHex = color,
                heights = heights
            )
        }
    }

    private fun countActiveDaysThisWeek(dates: List<String>): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        var count = 0
        for (i in 0 until 7) {
            val dStr = sdf.format(cal.time)
            if (dates.contains(dStr)) count++
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return count
    }

    private fun getFilterDateRange(timeFilter: String): Pair<String, String> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY

        return when (timeFilter) {
            "last_week" -> {
                cal.add(Calendar.WEEK_OF_YEAR, -1)
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                val s = sdf.format(cal.time)
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                val e = sdf.format(cal.time)
                Pair(s, e)
            }
            "this_month" -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                val s = sdf.format(cal.time)
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                val e = sdf.format(cal.time)
                Pair(s, e)
            }
            else -> {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                val s = sdf.format(cal.time)
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                val e = sdf.format(cal.time)
                Pair(s, e)
            }
        }
    }

    private fun normalizeTrack(raw: String): String {
        val r = raw.trim().lowercase(Locale.ROOT)
        return when {
            r.contains("python") || r.contains("coding") -> "Python & Coding Logic"
            r.contains("sql") || r.contains("database") || r.contains("dbms") -> "SQL & Database Management"
            r.contains("ai") || r.contains("tech") || r.contains("intelligence") -> "AI & Modern Tech Concepts"
            r.contains("aptitude") || r.contains("interview") || r.contains("math") -> "Aptitude & Interview Prep"
            else -> "Python & Coding Logic"
        }
    }

    private fun formatMinutes(minutes: Int): String {
        if (minutes <= 0) return "0m"
        val h = minutes / 60
        val m = minutes % 60
        return when {
            h > 0 && m > 0 -> "${h}h ${m}m"
            h > 0 -> "${h}h"
            else -> "${m}m"
        }
    }
}
