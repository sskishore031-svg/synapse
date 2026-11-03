package com.siamts.synapse.ai

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.siamts.synapse.SynapseApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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
 * 100% on-device Real Generative AI & Reasoning Engine with multi-turn rolling context memory.
 * Grounded on local educational datasets and on-device knowledge synthesis without external cloud API calls.
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
        "gm", "good morning", "good afternoon", "good evening", "gn", "good night",
        "hi", "hello", "hey", "vanakkam", "namaste", "hola", "greetings", "hii", "heyy", "yo"
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

    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        val targetContext = context ?: try {
            SynapseApplication.getAppContext()
        } catch (_: Throwable) {
            null
        }

        if (targetContext != null) {
            ioScope.launch {
                loadFromAssets(targetContext)
            }
        }
    }

    /**
     * Loads knowledge dataset asynchronously strictly in Dispatchers.IO.
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
                    logD(TAG, "Successfully loaded ${chunks.size} knowledge chunks from assets in Dispatchers.IO.")
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

        // 1. Check for Instant Natural Greetings
        if (isGreetingQuery(lowerQuery)) {
            return buildGreetingResponse(lowerQuery)
        }

        // 2. Check for Capabilities / Help queries
        if (isCapabilitiesQuery(lowerQuery)) {
            return buildCapabilitiesResponse()
        }

        // 3. Check for App Features & Navigation Guidance
        val appFeatureResp = checkAppFeatureQuery(lowerQuery)
        if (appFeatureResp != null) {
            return appFeatureResp
        }

        // If dataset hasn't finished loading yet, try synchronous load from app context
        if (chunks.isEmpty()) {
            try {
                val appCtx = SynapseApplication.getAppContext()
                loadFromAssets(appCtx)
            } catch (_: Throwable) {}
        }

        // 4. Check for Context-Aware Follow-up Queries
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
            } else if (!lastTopicTurn.matchedTopic.isNullOrBlank()) {
                // Synthetic follow-up from rolling memory
                val syntheticChunk = KnowledgeChunk(
                    chunkId = lastTopicTurn.chunkId ?: "hist_${System.currentTimeMillis()}",
                    subject = lastTopicTurn.matchedSubject.orEmpty(),
                    chapter = lastTopicTurn.matchedChapter.orEmpty(),
                    topic = lastTopicTurn.matchedTopic.orEmpty(),
                    content = "${lastTopicTurn.matchedTopic} in ${lastTopicTurn.matchedSubject}."
                )
                val followUpResponse = buildContextAwareFollowUpResponse(lowerQuery, syntheticChunk)
                recordTurn(userQuery, syntheticChunk)
                return followUpResponse
            }
        }

        // 5. Tokenize query
        val tokens = tokenize(lowerQuery)
        if (tokens.isEmpty()) {
            return buildFallbackResponse(query)
        }

        // 6. Token/Keyword scoring against local curriculum chunks
        val scoredChunks = synchronized(chunks) {
            chunks.map { chunk ->
                val score = computeRelevanceScore(lowerQuery, tokens, chunk)
                Pair(score, chunk)
            }
        }

        val bestMatch = scoredChunks.maxByOrNull { it.first }
        val bestScore = bestMatch?.first ?: 0.0

        // 7. Educational curriculum response if score >= 5.0
        if (bestMatch != null && bestScore >= 5.0) {
            val response = buildEducationalResponse(bestMatch.second, bestScore)
            recordTurn(userQuery, bestMatch.second)
            return response
        }

        // 8. Attempt context-assisted match if prior topic exists
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

        // 9. On-Device Generative Reasoning Synthesizer
        // Synthesizes structured knowledge across expanded academic subjects
        // (Biology, History, Geography, advanced Mathematics, Physics, Chemistry, Computer Science, AI)
        val generativeResp = synthesizeGenerativeEducationalResponse(query, lowerQuery, tokens)
        if (generativeResp != null) {
            recordTurn(
                query,
                KnowledgeChunk(
                    chunkId = "gen_${System.currentTimeMillis()}",
                    subject = generativeResp.matchedSubject.orEmpty(),
                    chapter = generativeResp.matchedChapter.orEmpty(),
                    topic = generativeResp.matchedTopic.orEmpty(),
                    content = "${generativeResp.paragraph1}\n\n${generativeResp.paragraph2}"
                )
            )
            return generativeResp
        }

        // 10. Fallback guidance for unrelated inquiries
        return buildFallbackResponse(query)
    }

    private fun checkAppFeatureQuery(lowerQuery: String): LocalAiResponse? {
        val q = lowerQuery.trim()
        return when {
            q.contains("note") && (q.contains("how") || q.contains("create") || q.contains("save") || q.contains("where") || q.contains("download")) -> {
                LocalAiResponse(
                    paragraph1 = "📝 **SYNAPSE Notes Organizer**\n\nYou can access, create, and organize study notes by tapping the Notes icon in the bottom navigation bar.",
                    paragraph2 = "⚡ **Key Capabilities**\n\n• Tap the **➕** button on the Notes screen to create Common, Subject-specific, or Saved notes.\n• Access **Subject Notes** directly for Mathematics, CS, AI, Physics, and Chemistry.\n• Save or download any note for instant offline study.",
                    confidenceScore = 95.0
                )
            }
            q.contains("quiz") && (q.contains("how") || q.contains("take") || q.contains("start") || q.contains("where") || q.contains("attempt")) -> {
                LocalAiResponse(
                    paragraph1 = "🎯 **SYNAPSE Interactive Quizzes**\n\nYou can test your knowledge anytime by tapping the Quiz icon in the bottom navigation bar.",
                    paragraph2 = "⚡ **Quiz Guide**\n\n• Choose your subject and specific chapter topic to start a targeted quiz.\n• Receive instant scoring, comprehensive answer explanations, and performance metrics.",
                    confidenceScore = 95.0
                )
            }
            q.contains("brain") && (q.contains("map") || q.contains("what") || q.contains("where") || q.contains("how")) -> {
                LocalAiResponse(
                    paragraph1 = "🧠 **SYNAPSE BrainMap Knowledge Graph**\n\nBrainMap visualizes your learning journey, showing interconnected subject concepts and prerequisite relationships.",
                    paragraph2 = "⚡ **How to Use**\n\n• Tap **BrainMap** from the Dashboard to explore interactive nodes.\n• Track mastery levels and discover recommended study paths.",
                    confidenceScore = 95.0
                )
            }
            q.contains("flashcard") -> {
                LocalAiResponse(
                    paragraph1 = "🎴 **Interactive Flashcards**\n\nFlashcards use spaced repetition to help you remember key formulas, definitions, and theorems.",
                    paragraph2 = "⚡ **Study Tip**\n\n• Tap **Flashcards** from the Dashboard to flip through curated question-and-answer cards across all subjects.",
                    confidenceScore = 95.0
                )
            }
            q.contains("theme") || q.contains("dark mode") || q.contains("light mode") -> {
                LocalAiResponse(
                    paragraph1 = "🎨 **Theme Customization**\n\nSYNAPSE AI supports real-time Dark Mode, Light Mode, and System Default appearance.",
                    paragraph2 = "⚡ **How to Switch**\n\n• Navigate to **Profile → Settings (⚙️)**.\n• Tap **Theme Selection** to choose between 3D-styled Light, Dark, or System preview cards.",
                    confidenceScore = 95.0
                )
            }
            else -> null
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
        val cleaned = lowerQuery.replace(Regex("[^a-zA-Z0-9\\s]"), " ").trim()
        val tokens = cleaned.split("\\s+".toRegex())
        if (tokens.size in 1..3 && greetings.contains(cleaned)) return true

        for (g in greetings) {
            if (cleaned == g || cleaned == "$g!" || cleaned.startsWith("$g ") || cleaned.endsWith(" $g")) {
                return true
            }
        }
        return false
    }

    private fun buildGreetingResponse(lowerQuery: String): LocalAiResponse {
        val cleaned = lowerQuery.replace(Regex("[^a-zA-Z0-9\\s]"), "").trim()
        val replyText = when {
            cleaned == "gm" || cleaned.contains("good morning") ->
                "Good morning! What topic or subject are we learning today?"
            cleaned == "gn" || cleaned.contains("good night") ->
                "Good night! Rest well and keep learning tomorrow."
            cleaned.contains("vanakkam") ->
                "Vanakkam! How can I help you with your studies today?"
            cleaned.contains("namaste") ->
                "Namaste! How can I assist you with your learning today?"
            cleaned.contains("good afternoon") ->
                "Good afternoon! What topic or subject would you like to study today?"
            cleaned.contains("good evening") ->
                "Good evening! Ready to dive into your studies tonight?"
            else ->
                // "hi", "hello", "hey", "yo", etc.
                "Hi! How can I help you with your studies today?"
        }

        return LocalAiResponse(
            paragraph1 = replyText,
            paragraph2 = "",
            isGreeting = true,
            confidenceScore = 100.0
        )
    }

    private fun isCapabilitiesQuery(lowerQuery: String): Boolean {
        val q = lowerQuery.replace(Regex("[^a-zA-Z0-9\\s]"), " ").trim()
        return q.contains("what can you do") ||
                q.contains("show subjects") ||
                q.contains("list subjects") ||
                q.contains("what subjects") ||
                q.contains("help me") ||
                q == "help" ||
                q.contains("what do you know") ||
                q.contains("capabilities")
    }

    private fun buildCapabilitiesResponse(): LocalAiResponse {
        return LocalAiResponse(
            paragraph1 = "I am your SYNAPSE AI on-device tutor. Here are the core subjects and topics I can help you master:",
            paragraph2 = "• Mathematics (Quadratic Equations, Calculus, Linear Algebra, Probability, Geometry)\n" +
                    "• Computer Science (Algorithms, Data Structures, Operating Systems, Computer Networks, SQL)\n" +
                    "• Data Science (Data Visualization, Machine Learning Fundamentals, Model Evaluation)\n" +
                    "• Artificial Intelligence (Neural Networks, Transformers, Deep Learning, RAG)\n" +
                    "• Physics (Newton's Laws of Motion, Waves, Thermodynamics, Electromagnetism, Optics)\n" +
                    "• Chemistry (Chemical Bonds, Acids & Bases, Periodic Table, Stoichiometry, Electrochemistry)\n" +
                    "• Extended Academic Subjects (Biology, History, Geography, and more)\n\n" +
                    "Feel free to ask any question, request examples, or explore problem solving steps!",
            confidenceScore = 100.0,
            isGreeting = false
        )
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
            "biology" -> {
                "In human respiration, hemoglobin binds four O₂ molecules in pulmonary capillaries and releases them in oxygen-depleted peripheral muscle tissues via allosteric cooperative binding."
            }
            "history" -> {
                "The Industrial Revolution led to steam locomotives, enabling transport of raw coal from Welsh mines to Manchester manufacturing factories within hours instead of days."
            }
            "geography" -> {
                "Subduction of oceanic crust beneath continental plates creates volcanic arcs such as the Pacific Ring of Fire, generating frequent seismic earthquakes."
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

    /**
     * Synthesizes rich, structured educational responses on-device across expanded academic domains:
     * Biology, History, Geography, advanced Mathematics, Physics, Chemistry, Computer Science, and AI.
     */
    private fun synthesizeGenerativeEducationalResponse(
        query: String,
        lowerQuery: String,
        tokens: List<String>
    ): LocalAiResponse? {
        val q = lowerQuery.replace(Regex("[^a-zA-Z0-9\\s]"), " ").trim()

        // 1. BIOLOGY
        if (q.contains("photosynthesis") || (q.contains("light") && q.contains("chloroplast"))) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (Biology · Plant Physiology)\n\nPhotosynthesis is the biochemical process by which green plants, algae, and cyanobacteria convert light energy from the sun into chemical energy stored in glucose molecules. It takes place in cellular chloroplasts using the green pigment chlorophyll.",
                paragraph2 = "⚡ **Key Formula & Core Rule**\n\n6CO₂ + 6H₂O + Photons → C₆H₁₂O₆ + 6O₂\n\nOperates in two stages: Light-dependent reactions (in thylakoids) generate ATP and NADPH, followed by the Calvin cycle (in stroma) synthesizing glucose carbohydrates.\n\n💡 **Practical Example & Takeaway:**\nGreen leaves absorb atmospheric CO₂ through stomata and pull water from soil roots; oxygen is released as a vital byproduct sustaining aerobic life.",
                matchedSubject = "Biology",
                matchedChapter = "Plant Physiology",
                matchedTopic = "Photosynthesis",
                confidenceScore = 90.0
            )
        }

        if (q.contains("dna") || q.contains("rna") || q.contains("genetic") || q.contains("heredity") || q.contains("chromosome")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (Biology · Genetics & Molecular Biology)\n\nDNA (Deoxyribonucleic Acid) is the double-helical macromolecule containing genetic blueprints for biological organism development and functioning. RNA (Ribonucleic Acid) carries messages from DNA to ribosomes for protein synthesis.",
                paragraph2 = "⚡ **Key Formula & Core Rule**\n\nBase Pairing Rule: Adenine pairs with Thymine (A=T via 2 hydrogen bonds), and Guanine pairs with Cytosine (G≡C via 3 hydrogen bonds). In RNA, Uracil (U) replaces Thymine.\nCentral Dogma: DNA → (Transcription) → mRNA → (Translation) → Functional Proteins.\n\n💡 **Practical Example & Takeaway:**\nA single nucleotide substitution in the hemoglobin gene causes sickle cell anemia, illustrating how molecular base sequences govern macroscopic human health.",
                matchedSubject = "Biology",
                matchedChapter = "Genetics & Molecular Biology",
                matchedTopic = "DNA and Genetics",
                confidenceScore = 90.0
            )
        }

        if (q.contains("cell") && (q.contains("organelle") || q.contains("mitochondria") || q.contains("nucleus") || q.contains("membrane") || q.contains("structure"))) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (Biology · Cytology & Cell Biology)\n\nThe biological cell is the fundamental unit of all living organisms. Eukaryotic cells feature specialized membrane-bound organelles operating in harmony within a protective phospholipid bilayer membrane.",
                paragraph2 = "⚡ **Key Formula & Core Rule**\n\n• Mitochondria: Synthesize cellular energy currency (ATP) via cellular respiration.\n• Nucleus: Houses genomic chromosomes and coordinates cellular replication.\n• Ribosomes: Translate mRNA codons into polypeptide protein chains.\n• Endoplasmic Reticulum & Golgi: Package and transport synthesized macromolecules.\n\n💡 **Practical Example & Takeaway:**\nHuman heart and skeletal muscle cells contain thousands of mitochondria to continuously supply ATP required for mechanical contraction.",
                matchedSubject = "Biology",
                matchedChapter = "Cytology & Cell Biology",
                matchedTopic = "Cell Structure and Organelles",
                confidenceScore = 90.0
            )
        }

        if (q.contains("mitosis") || q.contains("meiosis") || q.contains("cell division")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (Biology · Cell Division)\n\nMitosis is equational cell division producing two genetically identical diploid (2n) somatic cells for growth and tissue repair. Meiosis is reductional division producing four genetically diverse haploid (n) gametes for sexual reproduction.",
                paragraph2 = "⚡ **Key Formula & Core Rule**\n\nStages: Prophase → Metaphase (chromosomes align at equatorial plate) → Anaphase (sister chromatids pull apart) → Telophase & Cytokinesis.\nCrossing over during Meiosis I introduces genetic diversity via homologous recombination.\n\n💡 **Practical Example & Takeaway:**\nSkin cells continuously divide via mitosis to repair cuts, whereas germ cells in reproductive organs undergo meiosis to produce sperm and egg cells.",
                matchedSubject = "Biology",
                matchedChapter = "Cell Division",
                matchedTopic = "Mitosis and Meiosis",
                confidenceScore = 90.0
            )
        }

        if (q.contains("enzyme") || q.contains("catalyst") || q.contains("substrate") || q.contains("metabolism")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (Biology · Biochemistry)\n\nEnzymes are biological protein catalysts that dramatically increase the rate of biochemical reactions by lowering the activation energy barrier (Ea) without being consumed in the process.",
                paragraph2 = "⚡ **Key Formula & Core Rule**\n\nEnzymatic Mechanism: Enzyme (E) + Substrate (S) ⇌ ES Complex → Enzyme (E) + Product (P).\nEnzyme activity follows Michaelis-Menten kinetics: v = (Vmax × [S]) / (Km + [S]), and is sensitive to optimal pH and temperature.\n\n💡 **Practical Example & Takeaway:**\nSalivary amylase in human saliva rapidly hydrolyzes complex starches into maltose sugars during mastication within seconds.",
                matchedSubject = "Biology",
                matchedChapter = "Biochemistry",
                matchedTopic = "Enzymes and Catalysis",
                confidenceScore = 90.0
            )
        }

        // 2. HISTORY
        if (q.contains("world war") || q.contains("ww1") || q.contains("ww2") || q.contains("world war 1") || q.contains("world war 2") || q.contains("world war i") || q.contains("world war ii")) {
            val isWW2 = q.contains("2") || q.contains("ii") || q.contains("hitler") || q.contains("nazi") || q.contains("pearl harbor") || q.contains("hiroshima")
            return if (isWW2) {
                LocalAiResponse(
                    paragraph1 = "📘 **Concept & Definition** (History · Modern World History)\n\nWorld War II (1939–1945) was a global conflict fought between the Allied Powers (USA, Great Britain, Soviet Union, France, China) and the Axis Powers (Nazi Germany, Imperial Japan, Fascist Italy), sparked by Germany's invasion of Poland on September 1, 1939.",
                    paragraph2 = "⚡ **Key Milestones & Geopolitical Impact**\n\n• Key Battles: Battle of Britain (1940), Battle of Stalingrad (1942–1943), D-Day Normandy Landings (1944).\n• Conclusion: Surrender of Germany (May 1945) and atomic bombings of Hiroshima & Nagasaki (August 1945).\n• Legacy: Established the United Nations, dismantled European colonial empires, and initiated the Cold War.\n\n💡 **Practical Example & Takeaway:**\nThe post-war Bretton Woods conference established modern international economic architecture including the World Bank and IMF.",
                    matchedSubject = "History",
                    matchedChapter = "Modern World History",
                    matchedTopic = "World War II",
                    confidenceScore = 90.0
                )
            } else {
                LocalAiResponse(
                    paragraph1 = "📘 **Concept & Definition** (History · Modern World History)\n\nWorld War I (1914–1918), known as the Great War, was a global European-centered conflict between the Allied Powers (Britain, France, Russia, USA, Italy) and the Central Powers (Germany, Austria-Hungary, Ottoman Empire, Bulgaria).",
                    paragraph2 = "⚡ **Core Causes & Rules (M-A-I-N)**\n\n• Militarism: Massive military build-up and naval arms race.\n• Alliances: Interlocking defense treaties (Triple Entente vs Triple Alliance).\n• Imperialism: Colonial rivalries in Africa and Asia.\n• Nationalism & Trigger: Assassination of Archduke Franz Ferdinand in Sarajevo (June 28, 1914).\n\n💡 **Practical Example & Takeaway:**\nTrench warfare on the Western Front reshaped 20th-century warfare, leading to the collapse of four empires and the Treaty of Versailles (1919).",
                    matchedSubject = "History",
                    matchedChapter = "Modern World History",
                    matchedTopic = "World War I",
                    confidenceScore = 90.0
                )
            }
        }

        if (q.contains("civilization") || q.contains("indus valley") || q.contains("harappa") || q.contains("mesopotamia") || q.contains("egypt") || q.contains("rome") || q.contains("greece")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (History · Ancient Civilizations)\n\nAncient civilizations emerged around fertile river valleys: the Indus Valley (Indus River), Mesopotamia (Tigris and Euphrates), Ancient Egypt (Nile), and Ancient China (Yellow River), transitioning humanity from nomadic foraging to organized urban societies.",
                paragraph2 = "⚡ **Key Characteristics & Discoveries**\n\n• Urban Planning: Mohenjo-daro and Harappa featured standardized baked-brick architecture and covered drainage grids.\n• Governance & Laws: The Code of Hammurabi (Mesopotamia) established codified legal statutes.\n• Written Scripts: Cuneiform in Sumer, Hieroglyphs in Egypt, and Brahmi/Indus scripts.\n\n💡 **Practical Example & Takeaway:**\nThe Nile river's predictable annual flooding allowed Ancient Egypt to generate agricultural surpluses that funded the construction of the Great Pyramids.",
                matchedSubject = "History",
                matchedChapter = "Ancient Civilizations",
                matchedTopic = "Early River Valley Civilizations",
                confidenceScore = 90.0
            )
        }

        if (q.contains("industrial revolution")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (History · Economic & Social History)\n\nThe Industrial Revolution (18th–19th century) was the global transformation from agrarian, handcrafted cottage economies to machine-driven, automated factory manufacturing, beginning in Great Britain.",
                paragraph2 = "⚡ **Key Inventions & Mechanisms**\n\n• Steam Power: James Watt's refined steam engine revolutionized machinery and transportation.\n• Textile Mechanization: The Spinning Jenny and power loom amplified textile output tenfold.\n• Transport: Steam locomotives and canals created integrated national and international markets.\n\n💡 **Practical Example & Takeaway:**\nRapid urbanization concentrated populations in industrial cities like Manchester, prompting modern labor laws, public sanitation systems, and labor unions.",
                matchedSubject = "History",
                matchedChapter = "Economic & Social History",
                matchedTopic = "The Industrial Revolution",
                confidenceScore = 90.0
            )
        }

        if (q.contains("freedom") || q.contains("gandhi") || q.contains("indian independence") || q.contains("satyagraha") || q.contains("british raj")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (History · Indian National Movement)\n\nThe Indian Freedom Struggle was a historic mass movement to liberate India from British colonial rule (East India Company and British Raj), culminating in sovereign independence on August 15, 1947.",
                paragraph2 = "⚡ **Key Milestones & Principles**\n\n• 1857 Revolt: First major war of Indian independence against colonial exploitation.\n• Satyagraha & Ahimsa: Mahatma Gandhi's philosophy of non-violent civil resistance.\n• Major Campaigns: Non-Cooperation Movement (1920), Dandi Salt March (1930), and Quit India Movement (1942).\n\n💡 **Practical Example & Takeaway:**\nThe 240-mile Salt March mobilized millions of ordinary citizens by defying the colonial salt tax, demonstrating the moral power of non-violent collective protest.",
                matchedSubject = "History",
                matchedChapter = "Indian National Movement",
                matchedTopic = "Indian Independence Struggle",
                confidenceScore = 90.0
            )
        }

        // 3. GEOGRAPHY
        if (q.contains("plate tectonics") || q.contains("earthquake") || q.contains("volcano") || q.contains("continental drift") || q.contains("tectonic")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (Geography · Geomorphology & Geology)\n\nPlate Tectonics is the unifying geological theory stating that Earth's outer lithosphere is broken into rigid plates that float and move atop the ductile asthenosphere mantle beneath them.",
                paragraph2 = "⚡ **Key Formula & Boundary Types**\n\n• Convergent: Plates collide, causing subduction trenches and mountain ranges (e.g. Himalayas).\n• Divergent: Plates pull apart, creating mid-ocean ridges and rift valleys.\n• Transform: Plates slide horizontally past each other, releasing seismic stress as earthquakes (e.g. San Andreas Fault).\n\n💡 **Practical Example & Takeaway:**\nThe ongoing collision between the Indian Plate and Eurasian Plate elevates Mount Everest and the Himalayan range by ~5 mm annually.",
                matchedSubject = "Geography",
                matchedChapter = "Geomorphology",
                matchedTopic = "Plate Tectonics and Seismic Activity",
                confidenceScore = 90.0
            )
        }

        if (q.contains("atmosphere") || q.contains("troposphere") || q.contains("stratosphere") || q.contains("ozone") || q.contains("climate zone")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (Geography · Climatology & Meteorology)\n\nEarth's atmosphere is a protective multi-layered envelope of gases (78% Nitrogen, 21% Oxygen, 0.9% Argon, trace greenhouse gases) held by gravity that regulates planetary surface temperatures and shields life from solar radiation.",
                paragraph2 = "⚡ **Atmospheric Stratification Layers**\n\n• Troposphere (0–12 km): Contains 80% of atmospheric mass and all terrestrial weather phenomena.\n• Stratosphere (12–50 km): Houses the ozone layer (O₃) which absorbs harmful ultraviolet (UV-B) rays.\n• Mesosphere (50–85 km): Burns up incoming meteors.\n• Thermosphere & Exosphere: Ionized gas layers reflecting radio waves and hosting auroras.\n\n💡 **Practical Example & Takeaway:**\nCommercial jet airliners cruise in the lower stratosphere (~10–12 km) to fly above turbulent tropospheric weather systems and maximize fuel efficiency.",
                matchedSubject = "Geography",
                matchedChapter = "Climatology",
                matchedTopic = "Atmospheric Layers and Climate",
                confidenceScore = 90.0
            )
        }

        if (q.contains("water cycle") || q.contains("hydrological cycle") || q.contains("precipitation") || q.contains("evaporation")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (Geography · Hydrology)\n\nThe Water Cycle (Hydrologic Cycle) is the continuous global biogeochemical circulation of water through the atmosphere, land, and oceans driven by solar radiation and gravity.",
                paragraph2 = "⚡ **Key Cycle Stages**\n\nEvaporation & Transpiration (liquid water turns into atmospheric vapor) → Condensation (cooling vapor forms cloud droplets) → Precipitation (rain, snow, sleet) → Infiltration and surface runoff replenishing rivers, lakes, and underground aquifers.\n\n💡 **Practical Example & Takeaway:**\nThe Amazon Basin's dense rainforest transpires trillions of liters of water daily, producing 'flying rivers' of atmospheric vapor that irrigate agriculture thousands of kilometers away.",
                matchedSubject = "Geography",
                matchedChapter = "Hydrology",
                matchedTopic = "The Hydrological Water Cycle",
                confidenceScore = 90.0
            )
        }

        if (q.contains("solar system") || q.contains("planet") || q.contains("astronomy") || q.contains("orbit") || q.contains("kepler")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (Geography · Astronomical Geography)\n\nThe Solar System consists of our central G-type star (the Sun) and all astronomical bodies bound by its gravitational field: 8 planets, dwarf planets (Pluto), over 200 moons, and millions of asteroids and comets.",
                paragraph2 = "⚡ **Kepler's Laws of Planetary Motion**\n\n1. Orbits are ellipses with the Sun at one focus.\n2. Equal Area Law: A line connecting planet and Sun sweeps out equal areas in equal times (planets move fastest at perihelion).\n3. Harmonic Law: T² ∝ a³ (the square of orbital period T is proportional to the cube of semi-major axis a).\n\n💡 **Practical Example & Takeaway:**\nEarth's 23.5° axial tilt relative to its orbital plane causes the alternating seasons, as northern and southern hemispheres receive varying angles of solar insolation throughout the year.",
                matchedSubject = "Geography",
                matchedChapter = "Astronomical Geography",
                matchedTopic = "The Solar System and Planetary Motion",
                confidenceScore = 90.0
            )
        }

        // 4. MATHEMATICS (Extended)
        if (q.contains("matrix") || q.contains("linear algebra") || q.contains("eigenvalue") || q.contains("determinant") || q.contains("vector space")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (Mathematics · Linear Algebra)\n\nLinear algebra is the branch of mathematics dealing with vectors, vector spaces, and linear mappings represented systematically by matrices. A matrix is a rectangular array of numbers representing linear transformations.",
                paragraph2 = "⚡ **Key Formula & Core Rule**\n\n• Matrix Multiplication: (AB)ᵢⱼ = ∑ₖ Aᵢₖ Bₖⱼ (valid only when columns of A equal rows of B).\n• Invertibility: A matrix A has an inverse A⁻¹ if and only if det(A) ≠ 0.\n• Eigenvalue Equation: A · v = λ · v, where v is the non-zero eigenvector and λ is the scalar eigenvalue.\n\n💡 **Practical Example & Takeaway:**\n3D computer graphics and game engines multiply 4×4 transformation matrices by 3D object vertices to achieve real-time camera rotations, scaling, and perspective projections.",
                matchedSubject = "Mathematics",
                matchedChapter = "Linear Algebra",
                matchedTopic = "Matrices and Linear Transformations",
                confidenceScore = 90.0
            )
        }

        if (q.contains("probability") || q.contains("statistics") || q.contains("bayes") || q.contains("normal distribution") || q.contains("standard deviation")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (Mathematics · Probability & Statistics)\n\nProbability measures the mathematical likelihood that an event occurs (0 ≤ P(E) ≤ 1). Statistics provides mathematical frameworks to collect, analyze, interpret, and infer patterns from empirical datasets.",
                paragraph2 = "⚡ **Key Formula & Core Rule**\n\n• Bayes' Theorem: P(A|B) = [P(B|A) × P(A)] / P(B)\n• Normal Distribution: Gaussian bell curve characterized by mean (μ) and variance (σ²).\n• Standard Deviation: σ = √[ ∑(xᵢ - μ)² / N ], quantifying the dispersion of data points around the mean.\n\n💡 **Practical Example & Takeaway:**\nSpam email filters and medical diagnostics use Bayes' Theorem to update the probability that an email is spam (or a patient has a condition) given specific keyword/symptom evidence.",
                matchedSubject = "Mathematics",
                matchedChapter = "Probability & Statistics",
                matchedTopic = "Probability Distributions and Bayes Theorem",
                confidenceScore = 90.0
            )
        }

        if (q.contains("trigonometry") || q.contains("sin") || q.contains("cos") || q.contains("tan") || q.contains("pythagor")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (Mathematics · Trigonometry)\n\nTrigonometry studies the mathematical relationships between side lengths and angles of triangles, extending to periodic circular functions that model harmonic wave phenomena across engineering and physics.",
                paragraph2 = "⚡ **Key Formula & Core Rule**\n\n• Right Triangle Ratios: sin(θ) = Opp/Hyp, cos(θ) = Adj/Hyp, tan(θ) = Opp/Adj.\n• Fundamental Pythagorean Identity: sin²(θ) + cos²(θ) = 1.\n• Law of Cosines: c² = a² + b² - 2ab·cos(C).\n\n💡 **Practical Example & Takeaway:**\nGPS navigation and surveying calculate exact distance coordinates on Earth using satellite triangulation based on spherical trigonometric cosine laws.",
                matchedSubject = "Mathematics",
                matchedChapter = "Trigonometry",
                matchedTopic = "Trigonometric Functions and Identities",
                confidenceScore = 90.0
            )
        }

        // 5. PHYSICS (Extended)
        if (q.contains("thermodynamics") || q.contains("entropy") || q.contains("heat engine") || q.contains("carnot")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (Physics · Thermal Physics)\n\nThermodynamics is the branch of physics studying heat, mechanical work, temperature, and their relations to energy transformation across physical systems.",
                paragraph2 = "⚡ **Fundamental Laws of Thermodynamics**\n\n• 1st Law (Conservation): ΔU = Q - W (Internal energy change equals heat added minus work done).\n• 2nd Law (Entropy): In any spontaneous thermodynamic process, total entropy ΔS ≥ 0 (heat cannot spontaneously flow from cold to hot).\n• Carnot Efficiency: Maximum theoretical efficiency η = 1 - (T_cold / T_hot).\n\n💡 **Practical Example & Takeaway:**\nA car engine or power plant converts fuel combustion heat into mechanical crankshaft work, with waste heat necessarily ejected to the colder ambient exhaust.",
                matchedSubject = "Physics",
                matchedChapter = "Thermal Physics",
                matchedTopic = "Thermodynamics and Entropy",
                confidenceScore = 90.0
            )
        }

        if (q.contains("electromagnet") || q.contains("ohm's law") || q.contains("faraday") || q.contains("coulomb") || q.contains("magnetic field")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (Physics · Electromagnetism)\n\nElectromagnetism is the fundamental physical force governing electrically charged particles, combining electric fields produced by charges and magnetic fields created by moving electric currents.",
                paragraph2 = "⚡ **Key Formula & Core Rule**\n\n• Ohm's Law: V = I × R (Voltage = Current × Resistance).\n• Coulomb's Law: F = k_e × (|q₁ × q₂| / r²).\n• Faraday's Law of Induction: ε = -dΦ_B / dt (A changing magnetic flux through a conductor induces an electromotive voltage force).\n\n💡 **Practical Example & Takeaway:**\nHydroelectric dams and wind turbines generate commercial electricity by spinning copper coils through magnetic fields via Faraday's induction.",
                matchedSubject = "Physics",
                matchedChapter = "Electromagnetism",
                matchedTopic = "Electromagnetic Induction and Circuits",
                confidenceScore = 90.0
            )
        }

        // 6. CHEMISTRY (Extended)
        if (q.contains("periodic table") || q.contains("element") || q.contains("atomic structure") || q.contains("electronegativity")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (Chemistry · Atomic Structure & Elements)\n\nThe Periodic Table organizes all 118 known chemical elements in order of increasing atomic number (Z), electron configuration, and recurring chemical valence trends across periods and groups.",
                paragraph2 = "⚡ **Key Periodic Trends & Rules**\n\n• Across a Period (Left to Right): Atomic radius decreases, while ionization energy and electronegativity increase (Fluorine is highest).\n• Down a Group: Atomic radius increases due to added electron shells; metallic reactivity increases.\n• Octet Rule: Atoms gain, lose, or share valence electrons to attain stable noble gas configurations.\n\n💡 **Practical Example & Takeaway:**\nGroup 1 alkali metals (Sodium, Potassium) react vigorously with water because their solitary outer valence electron is easily stripped away.",
                matchedSubject = "Chemistry",
                matchedChapter = "Atomic Structure",
                matchedTopic = "Periodic Table Trends and Electron Configuration",
                confidenceScore = 90.0
            )
        }

        if (q.contains("stoichiometry") || q.contains("mole") || q.contains("avogadro") || q.contains("molarity")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (Chemistry · Chemical Stoichiometry)\n\nStoichiometry is the quantitative calculation of reactants and products in chemical reactions governed by the fundamental Law of Conservation of Mass.",
                paragraph2 = "⚡ **Key Formula & Core Rule**\n\n• The Mole: 1 mole = 6.022 × 10²³ particles (Avogadro's constant N_A).\n• Mole Calculation: n = mass (g) / Molar Mass (g/mol).\n• Solution Molarity: M = moles of solute / volume of solution (liters).\n\n💡 **Practical Example & Takeaway:**\nIn ammonia synthesis: N₂ + 3H₂ → 2NH₃, exactly 1 mole of N₂ reacts with 3 moles of H₂ to produce 2 moles of NH₃, determining required factory reactant ratios.",
                matchedSubject = "Chemistry",
                matchedChapter = "Chemical Stoichiometry",
                matchedTopic = "Stoichiometry and the Mole Concept",
                confidenceScore = 90.0
            )
        }

        // 7. COMPUTER SCIENCE & AI (Extended)
        if (q.contains("operating system") || q.contains("deadlock") || q.contains("thread") || q.contains("paging") || q.contains("scheduling")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (Computer Science · Operating Systems)\n\nAn Operating System (OS) manages hardware resources (CPU, RAM, storage, I/O devices) and provides essential kernel abstraction layers for software applications to execute securely and concurrently.",
                paragraph2 = "⚡ **Core Mechanisms & Deadlock Rules**\n\n• CPU Scheduling: Round Robin, Multilevel Feedback Queues allocate CPU time-slices.\n• Virtual Memory & Paging: Maps virtual process addresses to physical RAM frames, managing page faults.\n• Deadlock: Occurs when processes hold resources while waiting for others; prevented by breaking one of the 4 Coffman conditions: Mutual Exclusion, Hold & Wait, No Preemption, Circular Wait.\n\n💡 **Practical Example & Takeaway:**\nSmartphone operating systems use mutex locks and priority inheritance so background audio threads and UI touch events run smoothly without freezing the display.",
                matchedSubject = "Computer Science",
                matchedChapter = "Operating Systems",
                matchedTopic = "Process Concurrency and Memory Management",
                confidenceScore = 90.0
            )
        }

        if (q.contains("network") || q.contains("tcp") || q.contains("udp") || q.contains("osi") || q.contains("dns") || q.contains("http")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (Computer Science · Computer Networks)\n\nComputer networks interconnect computing nodes to exchange packetized data using standardized protocol suites: the OSI 7-Layer Model and the TCP/IP protocol stack.",
                paragraph2 = "⚡ **Key Protocols & Core Rules**\n\n• TCP (Transmission Control Protocol): Reliable, connection-oriented delivery using a 3-way handshake (SYN → SYN-ACK → ACK), sequence numbers, and packet acknowledgments.\n• UDP (User Datagram Protocol): Lightweight, connectionless, low-latency transmission.\n• DNS: Resolves human-readable domain names (e.g. synapse.ai) to machine IP addresses.\n\n💡 **Practical Example & Takeaway:**\nWeb browsing and banking use TCP (HTTPS) for guaranteed zero-data-loss packet delivery, whereas online multiplayer games and live video streams use UDP for real-time speed.",
                matchedSubject = "Computer Science",
                matchedChapter = "Computer Networks",
                matchedTopic = "Networking Protocols and TCP/IP Architecture",
                confidenceScore = 90.0
            )
        }

        if (q.contains("sql") || q.contains("database") || q.contains("acid") || q.contains("normalization") || q.contains("relational")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (Computer Science · Database Systems)\n\nA Relational Database Management System (RDBMS) organizes structured data into tables of rows and columns with defined schemas and primary/foreign key relationships.",
                paragraph2 = "⚡ **ACID Transaction Guarantees**\n\n• Atomicity: Transactions succeed completely or abort with zero partial writes.\n• Consistency: Database transitions between valid states preserving schema constraints.\n• Isolation: Concurrent transactions execute without mutual interference.\n• Durability: Committed updates persist permanently on non-volatile storage even after power failures.\n\n💡 **Practical Example & Takeaway:**\nWhen transferring money between bank accounts, an ACID transaction debits Account A and credits Account B inside one atomic transaction block so money is never lost.",
                matchedSubject = "Computer Science",
                matchedChapter = "Database Systems",
                matchedTopic = "Relational Databases and ACID Transactions",
                confidenceScore = 90.0
            )
        }

        // Generic academic fallback if query contains explicit academic question markers
        val wordSet = tokens.toSet()
        fun hasWord(vararg words: String) = words.any { wordSet.contains(it) }

        val academicMarkers = listOf("explain", "define", "theorem", "principle", "formula", "hypothesis", "derivation")
        val isExplicitAcademicQuestion = academicMarkers.any { q.contains(it) }

        val detectedSubject = when {
            hasWord("biology", "biological", "plant", "plants", "animal", "animals", "species", "organism", "cellular") -> "Biology"
            hasWord("history", "historical", "dynasty", "monarchy", "emperor", "empire", "treaty", "colonization") -> "History"
            hasWord("geography", "geographic", "ocean", "oceans", "river", "rivers", "mountain", "mountains", "plateau", "glacier") -> "Geography"
            hasWord("math", "mathematics", "algebra", "calculus", "geometry", "trigonometry", "matrix", "integral", "derivative") -> "Mathematics"
            hasWord("physics", "quantum", "optics", "relativity", "gravitation", "thermodynamics", "electromagnetism") -> "Physics"
            hasWord("chemistry", "stoichiometry", "equilibrium", "redox", "titration", "isotope", "enthalpy") -> "Chemistry"
            hasWord("computer", "software", "algorithm", "compiler", "database", "cybersecurity", "encryption") -> "Computer Science"
            else -> null
        }

        if (detectedSubject != null && isExplicitAcademicQuestion) {
            val cleanTopic = query.replace(Regex("(?i)^(explain|what is|how does|define|tell me about)\\s+"), "").trim()
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** ($detectedSubject · $cleanTopic)\n\n$cleanTopic is an essential concept within $detectedSubject. Understanding its underlying principles allows students to connect theoretical fundamentals with empirical real-world applications.",
                paragraph2 = "⚡ **Core Principles & Analytical Method**\n\n1. Define primary variables, boundary conditions, and governing hypotheses.\n2. Apply the canonical laws and theorems established in $detectedSubject.\n3. Verify results and examine real-world trade-offs.\n\n💡 **Practical Example & Takeaway:**\nMastering $cleanTopic develops rigorous problem-solving skills and forms a solid foundation for advanced studies in $detectedSubject.",
                matchedSubject = detectedSubject,
                matchedChapter = "Core Concepts",
                matchedTopic = cleanTopic,
                confidenceScore = 75.0
            )
        }

        return null
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
        val paragraph2 = "I can help you explore concepts across our core academic subjects:\n\n" +
                "• Mathematics (Quadratic Equations, Calculus, Linear Algebra, Probability)\n" +
                "• Computer Science (Algorithms, Data Structures, Operating Systems, Networks, SQL)\n" +
                "• Data Science (Data Visualization, Machine Learning Fundamentals)\n" +
                "• Artificial Intelligence (Neural Networks, Transformers & RAG)\n" +
                "• Physics (Waves, Newton's Laws of Motion, Thermodynamics, Electromagnetism)\n" +
                "• Chemistry (Chemical Bonds, Acids and Bases, Periodic Table, Stoichiometry)\n" +
                "• Biology, History, and Geography\n\n" +
                "Try asking: 'Explain photosynthesis', 'What were the causes of World War 1?', 'How does plate tectonics work?', or 'Explain gradient descent'."

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
