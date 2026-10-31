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
    val isGreeting: Boolean = false,
    val isFollowUp: Boolean = false
)

/**
 * Represents a single turn in the multi-turn conversation memory.
 */
data class ConversationTurn(
    val userQuery: String,
    val matchedSubject: String? = null,
    val matchedChapter: String? = null,
    val matchedTopic: String? = null,
    val chunkId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 100% on-device AI Tutor Engine with multi-turn rolling context memory.
 * Grounded on local educational datasets without external cloud API calls.
 */
class LocalAIEngine private constructor(context: Context?) {

    private val chunks = mutableListOf<KnowledgeChunk>()
    private val contextHistory = mutableListOf<ConversationTurn>()
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

    private val examplePrompts = setOf(
        "give an example", "give me an example", "show an example", "can you give an example",
        "example please", "another example", "practical example", "real life example"
    )

    private val elaboratePrompts = setOf(
        "explain more", "tell me more", "more details", "elaborate", "can you explain more",
        "explain further", "details please", "deep dive"
    )

    private val solvePrompts = setOf(
        "what about its roots", "what about its roots?", "how to solve it", "how to solve it?",
        "how does it work", "how does it work?", "how to calculate it", "how to calculate it?",
        "what is the formula", "what is the formula?", "why does it happen", "why does it happen?"
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

    /**
     * Resets conversation context memory (e.g. when starting a new chat session).
     */
    fun resetContext() {
        synchronized(contextHistory) {
            contextHistory.clear()
        }
    }

    /**
     * Returns current rolling context turns.
     */
    fun getContextHistory(): List<ConversationTurn> = synchronized(contextHistory) {
        contextHistory.toList()
    }

    /**
     * Generates a context-aware 100% on-device response.
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

        // 2. Check for Context-Aware Follow-up Queries
        val lastTopicTurn = synchronized(contextHistory) {
            contextHistory.lastOrNull { it.matchedTopic != null || it.chunkId != null }
        }

        if (lastTopicTurn != null && isFollowUpQuery(lowerQuery)) {
            val activeChunk = synchronized(chunks) {
                chunks.find { it.chunkId == lastTopicTurn.chunkId }
                    ?: chunks.find { it.topic.equals(lastTopicTurn.matchedTopic, ignoreCase = true) }
            }

            if (activeChunk != null) {
                val followUpResponse = buildContextAwareFollowUpResponse(lowerQuery, activeChunk)
                recordTurn(userQuery, activeChunk)
                return followUpResponse
            }
        }

        // 3. Tokenize query
        val tokens = tokenize(lowerQuery)
        if (tokens.isEmpty()) {
            return buildFallbackResponse(query)
        }

        // 4. Token/Keyword scoring
        val scoredChunks = synchronized(chunks) {
            chunks.map { chunk ->
                val score = computeRelevanceScore(lowerQuery, tokens, chunk)
                Pair(score, chunk)
            }
        }

        val bestMatch = scoredChunks.maxByOrNull { it.first }
        val bestScore = bestMatch?.first ?: 0.0

        // 5. Educational response or fallback
        return if (bestMatch != null && bestScore >= 5.0) {
            val response = buildEducationalResponse(bestMatch.second, bestScore)
            recordTurn(userQuery, bestMatch.second)
            response
        } else {
            // Attempt context-assisted fallback if prior topic exists
            if (lastTopicTurn != null) {
                val contextQuery = "$lowerQuery ${lastTopicTurn.matchedTopic.orEmpty()} ${lastTopicTurn.matchedChapter.orEmpty()}"
                val contextTokens = tokenize(contextQuery)
                val contextScored = synchronized(chunks) {
                    chunks.map { chunk ->
                        Pair(computeRelevanceScore(contextQuery, contextTokens, chunk), chunk)
                    }
                }
                val contextBest = contextScored.maxByOrNull { it.first }
                if (contextBest != null && contextBest.first >= 5.0) {
                    val resp = buildEducationalResponse(contextBest.second, contextBest.first)
                    recordTurn(userQuery, contextBest.second)
                    return resp
                }
            }
            buildFallbackResponse(query)
        }
    }

    private fun recordTurn(query: String, chunk: KnowledgeChunk) {
        synchronized(contextHistory) {
            if (contextHistory.size >= MAX_CONTEXT_TURNS) {
                contextHistory.removeAt(0)
            }
            contextHistory.add(
                ConversationTurn(
                    userQuery = query,
                    matchedSubject = chunk.subject,
                    matchedChapter = chunk.chapter,
                    matchedTopic = chunk.topic,
                    chunkId = chunk.chunkId
                )
            )
        }
    }

    private fun isGreetingQuery(lowerQuery: String): Boolean {
        val cleaned = lowerQuery.replace(Regex("[^a-zA-Z0-9\\s]"), "").trim()
        if (greetings.contains(cleaned)) return true

        for (g in greetings) {
            if (cleaned == g || cleaned.startsWith("$g ") || cleaned.endsWith(" $g")) {
                return true
            }
        }
        return false
    }

    private fun isFollowUpQuery(lowerQuery: String): Boolean {
        val cleaned = lowerQuery.replace(Regex("[^a-zA-Z0-9\\s]"), "").trim()
        if (examplePrompts.any { cleaned.contains(it) }) return true
        if (elaboratePrompts.any { cleaned.contains(it) }) return true
        if (solvePrompts.any { cleaned.contains(it) }) return true

        // Short queries with referential pronouns ("it", "its", "this", "that")
        val words = cleaned.split("\\s+".toRegex())
        if (words.size <= 5 && words.any { it in setOf("it", "its", "this", "that", "these", "those", "roots", "solve") }) {
            return true
        }

        return false
    }

    private fun buildContextAwareFollowUpResponse(
        lowerQuery: String,
        chunk: KnowledgeChunk
    ): LocalAiResponse {
        val cleaned = lowerQuery.replace(Regex("[^a-zA-Z0-9\\s]"), "").trim()

        // 1. Example request
        if (examplePrompts.any { cleaned.contains(it) } || cleaned.contains("example")) {
            val exampleText = generateTopicExample(chunk)
            return LocalAiResponse(
                paragraph1 = "📘 **Practical Example** (${chunk.subject} · ${chunk.topic})\n\nHere is a real-world scenario illustrating **${chunk.topic}**:",
                paragraph2 = "$exampleText\n\n💡 *Context Grounded:* Derived from ${chunk.chapter} (${chunk.subject}).",
                matchedSubject = chunk.subject,
                matchedChapter = chunk.chapter,
                matchedTopic = chunk.topic,
                confidenceScore = 95.0,
                isFollowUp = true
            )
        }

        // 2. Solve / Roots request
        if (solvePrompts.any { cleaned.contains(it) } || cleaned.contains("root") || cleaned.contains("solve")) {
            val stepGuide = generateSolveGuide(chunk)
            return LocalAiResponse(
                paragraph1 = "📘 **Method & Solving Steps** (${chunk.subject} · ${chunk.topic})\n\nHere is how to solve and evaluate problems for **${chunk.topic}**:",
                paragraph2 = "$stepGuide\n\n💡 *Formula/Insight:* ${chunk.content.substringBefore(".")}.",
                matchedSubject = chunk.subject,
                matchedChapter = chunk.chapter,
                matchedTopic = chunk.topic,
                confidenceScore = 90.0,
                isFollowUp = true
            )
        }

        // 3. Elaborate / Deep dive request
        return LocalAiResponse(
            paragraph1 = "📘 **In-Depth Breakdown** (${chunk.subject} · ${chunk.topic})\n\nExpanding further on **${chunk.topic}**:",
            paragraph2 = "${chunk.content}\n\n💡 *Takeaway:* Mastering ${chunk.topic} connects directly to advanced applications in ${chunk.chapter}.",
            matchedSubject = chunk.subject,
            matchedChapter = chunk.chapter,
            matchedTopic = chunk.topic,
            confidenceScore = 90.0,
            isFollowUp = true
        )
    }

    private fun generateTopicExample(chunk: KnowledgeChunk): String {
        return when (chunk.subject.lowercase(Locale.ROOT)) {
            "mathematics" -> {
                if (chunk.chapter.contains("Quadratic", ignoreCase = true)) {
                    "Consider a projectile trajectory: h(t) = -5t² + 20t. Setting height to ground (h(t) = 0) yields -5t(t - 4) = 0. Roots are t = 0 (launch) and t = 4s (impact). The quadratic formula allows calculating the exact flight duration and apex height."
                } else {
                    "In calculus, if a car's displacement is s(t) = 3t² + 4t meters, the instantaneous velocity is the derivative v(t) = s'(t) = 6t + 4 m/s. At t = 2s, velocity equals 16 m/s."
                }
            }
            "computer science" -> {
                if (chunk.chapter.contains("Algorithms", ignoreCase = true)) {
                    "Searching for target 42 in sorted array [10, 20, 30, 42, 50, 70]: Binary search inspects midpoint 30 < 42, narrows to right half [42, 50, 70], checks 42 = 42! Located in 2 comparisons instead of scanning 4 elements linearly."
                } else {
                    "A browser's 'Back' and 'Forward' buttons use two Stacks (LIFO). When you click Back, the current URL pushes onto the Forward stack and pops from the Back stack in O(1) constant time."
                }
            }
            "data science" -> {
                "When predicting house prices, if a linear regression model predicts \$300,000 for a house actually selling at \$320,000, residual error is \$20,000. Evaluating over test sets using RMSE or R² validates real-world generalization."
            }
            "artificial intelligence" -> {
                "In computer vision, an image of handwritten digit '8' is fed into neural layers. Gradient descent computes cross-entropy loss against true label and updates weights backwards via backpropagation so the network learns edge and loop detectors."
            }
            "physics" -> {
                "Applying Newton's second law F = ma: Accelerating a 1,200 kg car at 2.5 m/s² requires net horizontal engine force F = 1,200 × 2.5 = 3,000 Newtons."
            }
            "chemistry" -> {
                "Neutralizing stomach acid (HCl) with antacid magnesium hydroxide Mg(OH)₂: 2 HCl + Mg(OH)₂ → MgCl₂ + 2 H₂O. The hydronium and hydroxide ions combine into water, raising pH towards neutrality."
            }
            else -> {
                "In practice, ${chunk.topic} is applied in ${chunk.subject} systems to model real-world phenomena accurately."
            }
        }
    }

    private fun generateSolveGuide(chunk: KnowledgeChunk): String {
        return "1. Identify the given variables and establish standard algebraic or scientific representations.\n" +
                "2. Apply the fundamental formula or mechanism for ${chunk.topic}.\n" +
                "3. Compute the target parameters and verify units and boundary conditions.\n" +
                "4. Interpret the physical or algorithmic meaning of the calculated result."
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

        if (cleanQuery.contains(normTopic)) score += 30.0
        if (cleanQuery.contains(normChapter)) score += 20.0
        if (cleanQuery.contains(normSubject)) score += 15.0

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

        val topicTokens = tokenize(normTopic)
        for (token in tokens) {
            if (topicTokens.contains(token)) {
                score += 6.0
            }
        }

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

    companion object {
        private const val TAG = "LocalAIEngine"
        private const val MAX_CONTEXT_TURNS = 5

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
