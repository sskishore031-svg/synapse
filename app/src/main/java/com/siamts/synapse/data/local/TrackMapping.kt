package com.siamts.synapse.data.local

import java.util.Locale

/**
 * Unified mapping and normalizer for the 8 Placement Tracks and their 40 chapters.
 */
object TrackMapping {
    const val TRACK_PYTHON = "python"
    const val TRACK_SQL = "sql"
    const val TRACK_AI = "ai"
    const val TRACK_APTITUDE = "aptitude"
    const val TRACK_DSA = "dsa"
    const val TRACK_JAVA_OOPS = "java_oops"
    const val TRACK_OS_CN = "os_cn"
    const val TRACK_ANDROID_DEV = "android_dev"

    val ALL_TRACK_IDS = listOf(
        TRACK_PYTHON, TRACK_SQL, TRACK_AI, TRACK_APTITUDE,
        TRACK_DSA, TRACK_JAVA_OOPS, TRACK_OS_CN, TRACK_ANDROID_DEV
    )

    val TRACK_CHAPTER_IDS = mapOf(
        TRACK_PYTHON to listOf("10101", "10102", "10103", "10104", "10105"),
        TRACK_SQL to listOf("10201", "10202", "10203", "10204", "10205"),
        TRACK_AI to listOf("10301", "10302", "10303", "10304", "10305"),
        TRACK_APTITUDE to listOf("10401", "10402", "10403", "10404", "10405"),
        TRACK_DSA to listOf("10501", "10502", "10503", "10504", "10505"),
        TRACK_JAVA_OOPS to listOf("10601", "10602", "10603", "10604", "10605"),
        TRACK_OS_CN to listOf("10701", "10702", "10703", "10704", "10705"),
        TRACK_ANDROID_DEV to listOf("10801", "10802", "10803", "10804", "10805")
    )

    fun normalizeTrackId(raw: String): String {
        val r = raw.trim().lowercase(Locale.ROOT)
        val num = r.toIntOrNull()
        return when {
            num != null && num in 10101..10105 -> TRACK_PYTHON
            num != null && num in 10201..10205 -> TRACK_SQL
            num != null && num in 10301..10305 -> TRACK_AI
            num != null && num in 10401..10405 -> TRACK_APTITUDE
            num != null && num in 10501..10505 -> TRACK_DSA
            num != null && num in 10601..10605 -> TRACK_JAVA_OOPS
            num != null && num in 10701..10705 -> TRACK_OS_CN
            num != null && num in 10801..10805 -> TRACK_ANDROID_DEV
            r.contains("python") || r == "101" || r == "py" || r.startsWith("101") -> TRACK_PYTHON
            r.contains("sql") || r == "102" || r == "db" || r.contains("database") || r.startsWith("102") -> TRACK_SQL
            r.contains("ai") || r == "103" || r.contains("tech") || r.startsWith("103") -> TRACK_AI
            r.contains("aptitude") || r == "104" || r.contains("interview") || r.contains("math") || r.contains("quant") || r.startsWith("104") -> TRACK_APTITUDE
            r.contains("dsa") || r.contains("data structure") || r.contains("algorithm") || r == "105" || r.startsWith("105") -> TRACK_DSA
            r.contains("java") || r.contains("oops") || r == "106" || r.startsWith("106") -> TRACK_JAVA_OOPS
            r.contains("os") || r.contains("network") || r == "107" || r.startsWith("107") -> TRACK_OS_CN
            r.contains("android") || r.contains("compose") || r == "108" || r.startsWith("108") -> TRACK_ANDROID_DEV
            else -> TRACK_PYTHON
        }
    }

    fun getTrackTitle(trackId: String): String {
        return when (normalizeTrackId(trackId)) {
            TRACK_PYTHON -> "Python & Coding Logic"
            TRACK_SQL -> "SQL & Database Management"
            TRACK_AI -> "AI & Modern Tech Concepts"
            TRACK_APTITUDE -> "Aptitude & Interview Prep"
            TRACK_DSA -> "Data Structures & Algorithms"
            TRACK_JAVA_OOPS -> "Java & OOPs Mastery"
            TRACK_OS_CN -> "OS & Computer Networks"
            TRACK_ANDROID_DEV -> "Android & App Architecture"
            else -> "Python & Coding Logic"
        }
    }

    fun getTrackForChapter(chapterId: String): String {
        val cid = chapterId.trim()
        val num = cid.toIntOrNull() ?: 10101
        return when {
            num in 10101..10105 -> TRACK_PYTHON
            num in 10201..10205 -> TRACK_SQL
            num in 10301..10305 -> TRACK_AI
            num in 10401..10405 -> TRACK_APTITUDE
            num in 10501..10505 -> TRACK_DSA
            num in 10601..10605 -> TRACK_JAVA_OOPS
            num in 10701..10705 -> TRACK_OS_CN
            num in 10801..10805 -> TRACK_ANDROID_DEV
            else -> TRACK_PYTHON
        }
    }

    fun chapterIndex(chapterId: Any, trackId: String = ""): Int {
        val str = chapterId.toString().trim()
        val num = str.toIntOrNull()
        if (num != null) {
            if (num in 10101..10805) return ((num % 100) - 1).coerceIn(0, 4) + 1
            if (num in 1..5) return num
        }
        val match = Regex("""ch(?:apter)?_?(\d+)""", RegexOption.IGNORE_CASE).find(str)
        if (match != null) {
            val idx = match.groupValues[1].toIntOrNull()
            if (idx != null && idx in 1..5) return idx
        }
        return 1
    }

    fun canonicalLessonNumericId(trackId: String, chapterIndex: Int): Int {
        val norm = normalizeTrackId(trackId)
        val idx = chapterIndex.coerceIn(1, 5)
        val base = when (norm) {
            TRACK_PYTHON -> 10100
            TRACK_SQL -> 10200
            TRACK_AI -> 10300
            TRACK_APTITUDE -> 10400
            TRACK_DSA -> 10500
            TRACK_JAVA_OOPS -> 10600
            TRACK_OS_CN -> 10700
            TRACK_ANDROID_DEV -> 10800
            else -> 10100
        }
        return base + idx
    }

    fun canonicalLessonKey(trackId: String, chapterIndex: Int): String {
        val norm = normalizeTrackId(trackId)
        val idx = chapterIndex.coerceIn(1, 5)
        return "${norm}_ch$idx"
    }

    fun canonicalLessonId(chapterId: Any, trackId: String = ""): String {
        val str = chapterId.toString().trim()
        val num = str.toIntOrNull()
        if (num != null) {
            if (num in 10101..10805) return num.toString()
            if (num in 1..5) {
                return canonicalLessonNumericId(trackId, num).toString()
            }
        }
        return str
    }

    fun formatSubjectDisplayName(raw: String?): String {
        if (raw.isNullOrBlank()) return "General Notes"
        val r = raw.trim().lowercase(Locale.ROOT)
        return when {
            r == "1001" || r == "101" || r == "10101" || r.contains("python") -> "Python & Coding Logic"
            r == "1002" || r == "102" || r == "10201" || r.contains("sql") || r.contains("database") -> "SQL & Database Management"
            r == "1003" || r == "103" || r == "10301" || r.contains("ai") || r.contains("tech") -> "AI & Modern Tech Concepts"
            r == "1004" || r == "104" || r == "10401" || r.contains("aptitude") || r.contains("interview") || r.contains("quant") -> "Aptitude & Interview Prep"
            r == "1005" || r == "105" || r == "10501" || r.contains("dsa") || r.contains("data structure") || r.contains("algorithm") -> "Data Structures & Algorithms"
            r == "1006" || r == "106" || r == "10601" || r.contains("java") || r.contains("oops") -> "Java & OOPs Mastery"
            r == "1007" || r == "107" || r == "10701" || r.contains("os") || r.contains("network") -> "OS & Computer Networks"
            r == "1008" || r == "108" || r == "10801" || r.contains("android") || r.contains("compose") -> "Android & App Architecture"
            r == "1" || r.contains("math") -> "Mathematics"
            r == "2" || r.contains("computer science") -> "Computer Science"
            r == "3" || r.contains("data science") -> "Data Science"
            r == "4" -> "Artificial Intelligence"
            r == "5" || r.contains("physics") -> "Physics"
            r == "6" || r.contains("chemistry") -> "Chemistry"
            r.contains("general") || r == "common" -> "General Notes"
            !raw.all { it.isDigit() } && !raw.contains("-") && !raw.contains("_") -> raw
            else -> raw.replace("-", " ").replace("_", " ").split(" ").filter { it.isNotBlank() }
                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        }
    }

    fun formatSubjectIcon(raw: String?): String {
        if (raw.isNullOrBlank()) return "📝"
        val r = raw.trim().lowercase(Locale.ROOT)
        return when {
            r == "1001" || r == "101" || r == "10101" || r.contains("python") -> "🐍"
            r == "1002" || r == "102" || r == "10201" || r.contains("sql") || r.contains("database") -> "🗄️"
            r == "1003" || r == "103" || r == "10301" || r.contains("ai") || r.contains("tech") -> "🤖"
            r == "1004" || r == "104" || r == "10401" || r.contains("aptitude") || r.contains("quant") -> "📈"
            r == "1005" || r == "105" || r == "10501" || r.contains("dsa") || r.contains("algorithm") -> "⚡"
            r == "1006" || r == "106" || r == "10601" || r.contains("java") || r.contains("oops") -> "☕"
            r == "1007" || r == "107" || r == "10701" || r.contains("os") || r.contains("network") -> "💻"
            r == "1008" || r == "108" || r == "10801" || r.contains("android") -> "📱"
            r == "1" || r.contains("math") -> "📐"
            r == "2" || r.contains("computer") -> "💻"
            r == "3" || r.contains("data science") -> "📊"
            r == "4" -> "🤖"
            r == "5" || r.contains("physics") -> "⚛️"
            r == "6" || r.contains("chemistry") -> "🧪"
            else -> "📝"
        }
    }

    fun stripMarkdown(raw: String): String {
        if (raw.isBlank()) return ""
        return raw
            .replace(Regex("```[a-zA-Z]*\\n?"), "")
            .replace(Regex("(?m)^\\s*#{1,6}\\s*"), "")
            .replace(Regex("\\*\\*([^*]+)\\*\\*"), "$1")
            .replace(Regex("\\*([^*]+)\\*"), "$1")
            .replace(Regex("__([^_]+)__"), "$1")
            .replace(Regex("(?<!\\w)_([^_]+)_(?!\\w)"), "$1")
            .replace(Regex("`([^`]+)`"), "$1")
            .replace(Regex("(?m)^\\s*[-*•]\\s*"), "• ")
            .replace(Regex("\\[([^\\]]+)\\]\\([^)]+\\)"), "$1")
            .replace(Regex("\\n{3,}"), "\n\n")
            .trim()
    }
}
