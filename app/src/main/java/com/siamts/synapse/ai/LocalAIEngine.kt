package com.siamts.synapse.ai

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.siamts.synapse.SynapseApplication
import java.io.InputStreamReader
import java.util.Locale

/**
 * Knowledge chunk representing an educational concept across subjects.
 */
data class KnowledgeChunk(
    @SerializedName("chunk_id")
    val chunkId: String = "",
    @SerializedName("subject")
    val subject: String = "",
    @SerializedName("chapter")
    val chapter: String = "",
    @SerializedName("topic")
    val topic: String = "",
    @SerializedName("keywords")
    val keywords: List<String> = emptyList(),
    @SerializedName("content")
    val content: String = ""
)

/**
 * Structured response produced by [LocalAIEngine].
 */
data class LocalAiResponse(
    val paragraph1: String,
    val paragraph2: String,
    val matchedSubject: String? = null,
    val matchedChapter: String? = null,
    val matchedTopic: String? = null,
    val confidenceScore: Double = 0.0,
    val isGreeting: Boolean = false
)

/**
 * 100% on-device AI Tutor Engine that loads educational knowledge from assets
 * and generates immediate responses via keyword scoring and token retrieval.
 */
class LocalAIEngine private constructor(context: Context?) {

    private val chunks = mutableListOf<KnowledgeChunk>()
    private val gson = Gson()

    private val stopWords = setOf(
        "a", "an", "the", "in", "on", "at", "to", "for", "of", "with", "is", "are",
        "was", "were", "be", "been", "being", "do", "does", "did", "have", "has", "had",
        "can", "could", "should", "would", "what", "which", "who", "whom", "where", "when",
        "why", "how", "explain", "describe", "tell", "me", "about", "give", "please",
        "my", "your", "i", "you", "we", "they", "it", "this", "that", "and", "or", "by"
    )

    private val greetings = setOf(
        "gm", "good morning", "good afternoon", "good evening",
        "hi", "hello", "hey", "vanakkam", "namaste", "hola", "greetings"
    )

    init {
        if (context != null) {
            loadFromAssets(context)
        } else {
            try {
                val appContext = SynapseApplication.getAppContext()
                loadFromAssets(appContext)
            } catch (e: Throwable) {
                logW(TAG, "Application context not yet ready for LocalAIEngine auto-load: ${e.message}")
            }
        }
    }

    /**
     * Loads knowledge dataset from Android assets folder.
     */
    fun loadFromAssets(context: Context, filename: String = "all_subjects_expanded.json") {
        try {
            context.assets.open(filename).use { inputStream ->
                InputStreamReader(inputStream, Charsets.UTF_8).use { reader ->
                    val type = object : TypeToken<List<KnowledgeChunk>>() {}.type
                    val list: List<KnowledgeChunk> = gson.fromJson(reader, type)
                    synchronized(chunks) {
                        chunks.clear()
                        chunks.addAll(list)
                    }
                    logD(TAG, "Successfully loaded ${chunks.size} knowledge chunks from assets.")
                }
            }
        } catch (e: Throwable) {
            logE(TAG, "Error loading knowledge dataset from assets: ${e.message}", e)
        }
    }

    /**
     * Loads knowledge dataset directly from a JSON string (useful for testing or updates).
     */
    fun loadFromJson(jsonString: String) {
        try {
            val type = object : TypeToken<List<KnowledgeChunk>>() {}.type
            val list: List<KnowledgeChunk> = gson.fromJson(jsonString, type)
            synchronized(chunks) {
                chunks.clear()
                chunks.addAll(list)
            }
        } catch (e: Throwable) {
            logE(TAG, "Error loading dataset from JSON string: ${e.message}", e)
        }
    }

    private fun logD(tag: String, message: String) {
        try {
            Log.d(tag, message)
        } catch (_: Throwable) {
            println("[$tag] $message")
        }
    }

    private fun logW(tag: String, message: String) {
        try {
            Log.w(tag, message)
        } catch (_: Throwable) {
            System.err.println("[$tag] $message")
        }
    }

    private fun logE(tag: String, message: String, throwable: Throwable? = null) {
        try {
            Log.e(tag, message, throwable)
        } catch (_: Throwable) {
            System.err.println("[$tag] $message ${throwable?.message.orEmpty()}")
        }
    }

    /**
     * Generates a 100% on-device response for the given user query.
     */
    fun generateResponse(userQuery: String): LocalAiResponse {
        val query = userQuery.trim()
        if (query.isBlank()) {
            return buildFallbackResponse("")
        }

        val lowerQuery = query.lowercase(Locale.ROOT)

        // 1. Check for Greetings
        if (isGreetingQuery(lowerQuery)) {
            return buildGreetingResponse()
        }

        // 2. Tokenize and clean query
        val tokens = tokenize(lowerQuery)
        if (tokens.isEmpty()) {
            return buildFallbackResponse(query)
        }

        // 3. Score against knowledge chunks
        val scoredChunks = synchronized(chunks) {
            chunks.map { chunk ->
                val score = computeRelevanceScore(lowerQuery, tokens, chunk)
                Pair(score, chunk)
            }
        }

        val bestMatch = scoredChunks.maxByOrNull { it.first }
        val bestScore = bestMatch?.first ?: 0.0

        // 4. Return structured educational explanation if threshold met (>= 5.0)
        return if (bestMatch != null && bestScore >= 5.0) {
            buildEducationalResponse(bestMatch.second, bestScore)
        } else {
            buildFallbackResponse(query)
        }
    }

    private fun isGreetingQuery(lowerQuery: String): Boolean {
        // Strip trailing punctuation
        val cleaned = lowerQuery.replace(Regex("[^a-zA-Z0-9\\s]"), "").trim()
        if (greetings.contains(cleaned)) return true

        for (g in greetings) {
            if (cleaned == g || cleaned.startsWith("$g ") || cleaned.endsWith(" $g")) {
                return true
            }
        }
        return false
    }

    private fun tokenize(text: String): List<String> {
        val cleaned = text.replace(Regex("[^a-zA-Z0-9\\s]"), " ")
        return cleaned.split("\\s+".toRegex())
            .map { it.trim() }
            .filter { it.length > 1 && !stopWords.contains(it) }
    }

    private fun computeRelevanceScore(
        cleanQuery: String,
        tokens: List<String>,
        chunk: KnowledgeChunk
    ): Double {
        var score = 0.0

        val normTopic = chunk.topic.lowercase(Locale.ROOT)
        val normChapter = chunk.chapter.lowercase(Locale.ROOT)
        val normSubject = chunk.subject.lowercase(Locale.ROOT)

        // Exact phrase in topic, chapter, or subject
        if (cleanQuery.contains(normTopic)) score += 30.0
        if (cleanQuery.contains(normChapter)) score += 20.0
        if (cleanQuery.contains(normSubject)) score += 15.0

        // Keywords matching
        for (kw in chunk.keywords) {
            val normKw = kw.lowercase(Locale.ROOT)
            if (cleanQuery.contains(normKw)) {
                score += if (normKw.contains(" ")) 15.0 else 8.0
            }
            for (token in tokens) {
                if (token == normKw) {
                    score += 5.0
                } else if (normKw.contains(token) || token.contains(normKw)) {
                    score += 2.5
                }
            }
        }

        // Topic token matches
        val topicTokens = tokenize(normTopic)
        for (token in tokens) {
            if (topicTokens.contains(token)) {
                score += 6.0
            }
        }

        // Content token matches
        val normContent = chunk.content.lowercase(Locale.ROOT)
        for (token in tokens) {
            if (normContent.contains(token)) {
                score += 1.0
            }
        }

        return score
    }

    private fun buildGreetingResponse(): LocalAiResponse {
        return LocalAiResponse(
            paragraph1 = "Good morning! I am your SYNAPSE AI tutor. Which subject or chapter would you like to explore today? (Mathematics, Computer Science, Data Science, AI, Physics, Chemistry)",
            paragraph2 = "I can help you master Quadratic Equations, Calculus, Algorithms, Data Structures, Data Visualization, Machine Learning, Neural Networks, Transformers, Waves, Newton's Laws, Chemical Bonds, or Acids and Bases. Feel free to ask any question!",
            isGreeting = true,
            confidenceScore = 100.0
        )
    }

    private fun buildEducationalResponse(chunk: KnowledgeChunk, score: Double): LocalAiResponse {
        // Split content into concept/definition and formula/takeaway
        val sentences = chunk.content.split(Regex("(?<=\\.)\\s+"))
        val conceptSentences = if (sentences.size > 2) sentences.take(2) else sentences.take(1)
        val remainingSentences = if (sentences.size > 2) sentences.drop(2) else sentences.drop(1)

        val conceptText = conceptSentences.joinToString(" ").trim()
        val formulaAndTakeawayText = if (remainingSentences.isNotEmpty()) {
            remainingSentences.joinToString(" ").trim()
        } else {
            "Applying ${chunk.topic} is fundamental to mastering ${chunk.subject} and enables efficient problem-solving in real-world scenarios."
        }

        val paragraph1 = "📘 **Concept & Definition** (${chunk.subject} · ${chunk.chapter})\n\n$conceptText"
        val paragraph2 = "⚡ **Key Formula, Rule & Takeaway**\n\n$formulaAndTakeawayText\n\n💡 *Topic: ${chunk.topic}*"

        return LocalAiResponse(
            paragraph1 = paragraph1,
            paragraph2 = paragraph2,
            matchedSubject = chunk.subject,
            matchedChapter = chunk.chapter,
            matchedTopic = chunk.topic,
            confidenceScore = score,
            isGreeting = false
        )
    }

    private fun buildFallbackResponse(query: String): LocalAiResponse {
        val querySnippet = if (query.isNotBlank()) " for \"$query\"" else ""
        val paragraph1 = "I couldn't find a direct match$querySnippet in my on-device knowledge base."
        val paragraph2 = "I can help you explore concepts across our 6 core subjects:\n\n" +
                "• Mathematics (Quadratic Equations, Calculus)\n" +
                "• Computer Science (Algorithms, Data Structures)\n" +
                "• Data Science (Data Visualization, Machine Learning Fundamentals)\n" +
                "• Artificial Intelligence (Neural Networks, Transformers & RAG)\n" +
                "• Physics (Waves, Newton's Laws of Motion)\n" +
                "• Chemistry (Chemical Bonds, Acids and Bases)\n\n" +
                "Try asking: 'Explain gradient descent', 'What is Newton's second law?', or 'How does binary search work?'"

        return LocalAiResponse(
            paragraph1 = paragraph1,
            paragraph2 = paragraph2,
            confidenceScore = 0.0,
            isGreeting = false
        )
    }

    fun getAllChunks(): List<KnowledgeChunk> = synchronized(chunks) { chunks.toList() }

    companion object {
        private const val TAG = "LocalAIEngine"

        @Volatile
        private var INSTANCE: LocalAIEngine? = null

        fun init(context: Context): LocalAIEngine {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocalAIEngine(context.applicationContext).also { INSTANCE = it }
            }
        }

        fun getInstance(context: Context? = null): LocalAIEngine {
            return INSTANCE ?: synchronized(this) {
                val appContext = context?.applicationContext ?: try {
                    SynapseApplication.getAppContext()
                } catch (e: Exception) {
                    null
                }
                INSTANCE ?: LocalAIEngine(appContext).also { INSTANCE = it }
            }
        }
    }
}
