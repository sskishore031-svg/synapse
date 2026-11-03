package com.siamts.synapse.ai

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.siamts.synapse.SynapseApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
    @SerializedName("definition")
    val definition: String = "",
    @SerializedName(value = "formula_or_core_rule", alternate = ["formula"])
    val formulaOrCoreRule: String = "",
    @SerializedName("worked_example")
    val workedExample: String = "",
    @SerializedName("content")
    val content: String = ""
)

/**
 * Subject domain knowledge containing overview, topic catalog, and RAG chunks.
 */
data class SubjectKnowledge(
    @SerializedName("subject")
    val subject: String = "",
    @SerializedName("overview")
    val overview: String = "",
    @SerializedName("topics")
    val topics: List<String> = emptyList(),
    @SerializedName("rag_chunks")
    val ragChunks: List<KnowledgeChunk> = emptyList()
)

/**
 * Root container for educational knowledge base.
 */
data class EducationalKnowledgeBase(
    @SerializedName("subjects")
    val subjects: Map<String, SubjectKnowledge> = emptyMap()
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
    val isFollowUp: Boolean = false,
    val isTopicSuggestion: Boolean = false
)

/**
 * Structured summary produced by the on-device AI Smart Notes Generator.
 */
data class SmartNotesResult(
    val title: String,
    val subject: String,
    val keyDefinition: String,
    val coreFormulasOrLaws: List<String>,
    val examTakeaways: List<String>,
    val fullMarkdown: String
)

/**
 * Progressive streaming token update for ChatGPT-style live typing effect.
 */
sealed class LocalAiStreamToken {
    data class Thinking(val status: String = "✨ SYNAPSE AI is thinking...") : LocalAiStreamToken()
    data class Token(
        val paragraph1: String,
        val paragraph2: String,
        val isComplete: Boolean,
        val response: LocalAiResponse
    ) : LocalAiStreamToken()
    data class Completed(val response: LocalAiResponse) : LocalAiStreamToken()
}

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
 * Grounded on local educational datasets, RAG retrieval, and on-device knowledge synthesis without cloud APIs.
 */
class LocalAIEngine private constructor(context: Context?) {

    private val chunks = mutableListOf<KnowledgeChunk>()
    private val subjectMap = mutableMapOf<String, SubjectKnowledge>()
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
     * Loads knowledge datasets asynchronously in Dispatchers.IO.
     * Prioritizes 'educational_knowledge_base.json' and supplements with 'all_subjects_expanded.json'.
     */
    fun loadFromAssets(context: Context) {
        // 1. Load primary educational_knowledge_base.json
        try {
            context.assets.open("educational_knowledge_base.json").use { inputStream ->
                InputStreamReader(inputStream, Charsets.UTF_8).use { reader ->
                    parseKnowledgeBase(reader.readText())
                }
            }
        } catch (e: Throwable) {
            logW(TAG, "educational_knowledge_base.json not loaded: ${e.message}")
        }

        // 2. Supplement with all_subjects_expanded.json if present
        try {
            context.assets.open("all_subjects_expanded.json").use { inputStream ->
                InputStreamReader(inputStream, Charsets.UTF_8).use { reader ->
                    val type = object : TypeToken<List<KnowledgeChunk>>() {}.type
                    val list: List<KnowledgeChunk> = gson.fromJson(reader, type)
                    synchronized(chunks) {
                        val existingIds = chunks.map { it.chunkId }.toSet()
                        val newChunks = list.filter { it.chunkId !in existingIds }
                        chunks.addAll(newChunks)
                    }
                    logD(TAG, "Supplemented all_subjects_expanded.json. Total chunks: ${chunks.size}")
                }
            }
        } catch (e: Throwable) {
            logW(TAG, "all_subjects_expanded.json not loaded: ${e.message}")
        }

        // 3. Supplement with extended_curriculum_catalog.json if present
        try {
            context.assets.open("extended_curriculum_catalog.json").use { inputStream ->
                InputStreamReader(inputStream, Charsets.UTF_8).use { reader ->
                    val catalog = gson.fromJson(reader, com.siamts.synapse.data.model.ExtendedCurriculumCatalog::class.java)
                    val newChunks = mutableListOf<KnowledgeChunk>()
                    if (catalog?.subjects != null) {
                        for (sub in catalog.subjects) {
                            for (ch in sub.chapters) {
                                for (rc in ch.ragChunks) {
                                    val fixedChunk = rc.copy(
                                        subject = if (rc.subject.isBlank()) sub.name else rc.subject,
                                        chapter = if (rc.chapter.isBlank()) ch.title else rc.chapter
                                    )
                                    newChunks.add(fixedChunk)
                                }
                            }
                        }
                    }
                    synchronized(chunks) {
                        val existingIds = chunks.map { it.chunkId }.toSet()
                        val filtered = newChunks.filter { it.chunkId !in existingIds }
                        chunks.addAll(filtered)
                    }
                    logD(TAG, "Supplemented extended_curriculum_catalog.json. Total chunks: ${chunks.size}")
                }
            }
        } catch (e: Throwable) {
            logW(TAG, "extended_curriculum_catalog.json not loaded: ${e.message}")
        }
    }

    /**
     * Loads knowledge dataset directly from a JSON string (supports both Map and List schemas).
     */
    fun loadFromJson(jsonString: String) {
        try {
            parseKnowledgeBase(jsonString)
        } catch (e: Throwable) {
            logE(TAG, "Error loading dataset from JSON string: ${e.message}", e)
        }
    }

    private fun parseKnowledgeBase(jsonString: String) {
        val jsonElement = JsonParser.parseString(jsonString.trim())
        if (jsonElement.isJsonObject) {
            val rootObj = jsonElement.asJsonObject
            if (rootObj.has("subjects")) {
                val subjectsElement = rootObj.get("subjects")
                val subjectList = mutableListOf<SubjectKnowledge>()

                if (subjectsElement.isJsonArray) {
                    val type = object : TypeToken<List<SubjectKnowledge>>() {}.type
                    val parsed: List<SubjectKnowledge> = gson.fromJson(subjectsElement, type)
                    subjectList.addAll(parsed)
                } else if (subjectsElement.isJsonObject) {
                    for ((_, subObj) in subjectsElement.asJsonObject.entrySet()) {
                        val sub: SubjectKnowledge = gson.fromJson(subObj, SubjectKnowledge::class.java)
                        subjectList.add(sub)
                    }
                }

                synchronized(subjectMap) {
                    subjectMap.clear()
                    for (sub in subjectList) {
                        val rawName = sub.subject.lowercase(Locale.ROOT)
                        subjectMap[rawName] = sub
                        val normName = rawName.replace(Regex("[^a-z0-9]+"), "_")
                        subjectMap[normName] = sub
                    }
                }

                synchronized(chunks) {
                    chunks.clear()
                    for (sub in subjectList) {
                        for (chunk in sub.ragChunks) {
                            val fixedChunk = if (chunk.subject.isBlank()) chunk.copy(subject = sub.subject) else chunk
                            chunks.add(fixedChunk)
                        }
                    }
                }
                logD(TAG, "Successfully loaded knowledge base: ${subjectList.size} subjects, ${chunks.size} chunks.")
                return
            }
        } else if (jsonElement.isJsonArray) {
            val type = object : TypeToken<List<KnowledgeChunk>>() {}.type
            val list: List<KnowledgeChunk> = gson.fromJson(jsonElement, type)
            synchronized(chunks) {
                chunks.clear()
                chunks.addAll(list)
            }
            synchronized(subjectMap) {
                val grouped = list.groupBy { it.subject }
                for ((subName, subChunks) in grouped) {
                    if (subName.isNotBlank()) {
                        val topics = subChunks.map { it.chapter }.distinct()
                        val rawName = subName.lowercase(Locale.ROOT)
                        val sub = SubjectKnowledge(
                            subject = subName,
                            overview = "$subName educational curriculum modules.",
                            topics = topics,
                            ragChunks = subChunks
                        )
                        subjectMap[rawName] = sub
                        subjectMap[rawName.replace(Regex("[^a-z0-9]+"), "_")] = sub
                    }
                }
            }
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

        // 1. Intent Handler: Casual Greetings (ChatGPT-style short, natural greeting)
        if (isGreetingQuery(lowerQuery)) {
            return buildGreetingResponse(lowerQuery)
        }

        // 2. Intent Handler: Capabilities / Help queries
        if (isCapabilitiesQuery(lowerQuery)) {
            return buildCapabilitiesResponse()
        }

        // 3. Intent Handler: Topic Suggestion / Chapter Listing ("give me some maths topic", "suggest chemistry chapters")
        val topicSuggestionResp = checkTopicSuggestionIntent(lowerQuery)
        if (topicSuggestionResp != null) {
            return topicSuggestionResp
        }

        // 4. Intent Handler: App Features & Navigation Guidance
        val appFeatureResp = checkAppFeatureQuery(lowerQuery)
        if (appFeatureResp != null) {
            return appFeatureResp
        }

        // Lazy asset load fallback if not loaded yet
        if (chunks.isEmpty()) {
            try {
                val appCtx = SynapseApplication.getAppContext()
                loadFromAssets(appCtx)
            } catch (_: Throwable) {}
        }

        // 5. Intent Handler: Context-Aware Multi-Turn Follow-up Queries
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

        // 6. Tokenize query
        val tokens = tokenize(lowerQuery)
        if (tokens.isEmpty()) {
            return buildFallbackResponse(query)
        }

        // 7. Intent Handler: Conceptual Explanation (Match against RAG Chunks)
        val scoredChunks = synchronized(chunks) {
            chunks.map { chunk ->
                val score = computeRelevanceScore(lowerQuery, tokens, chunk)
                Pair(score, chunk)
            }
        }

        val bestMatch = scoredChunks.maxByOrNull { it.first }
        val bestScore = bestMatch?.first ?: 0.0

        if (bestMatch != null && bestScore >= 5.0) {
            val response = buildEducationalResponse(bestMatch.second, bestScore)
            recordTurn(userQuery, bestMatch.second)
            return response
        }

        // 8. Attempt context-assisted match if prior turn exists
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
        val generativeResp = synthesizeGenerativeEducationalResponse(query, lowerQuery, tokens)
        if (generativeResp != null) {
            recordTurn(
                query,
                KnowledgeChunk(
                    chunkId = "gen_${System.currentTimeMillis()}",
                    subject = generativeResp.matchedSubject.orEmpty(),
                    chapter = generativeResp.matchedChapter.orEmpty(),
                    topic = generativeResp.matchedTopic.orEmpty(),
                    definition = generativeResp.paragraph1,
                    formulaOrCoreRule = generativeResp.paragraph2,
                    content = "${generativeResp.paragraph1}\n\n${generativeResp.paragraph2}"
                )
            )
            return generativeResp
        }

        // 10. Fallback guidance for unrelated inquiries
        return buildFallbackResponse(query)
    }

    /**
     * Progressive streaming API returning AI response as a Kotlin Flow with word-by-word emission (20-30ms delay).
     */
    fun streamResponse(userQuery: String, delayPerWordMs: Long = 24L): Flow<LocalAiStreamToken> = flow {
        emit(LocalAiStreamToken.Thinking())
        val response = generateResponse(userQuery)
        delay(160L) // Initial retrieval thinking pause

        val p1Words = response.paragraph1.split(Regex("\\s+")).filter { it.isNotEmpty() }
        val p1Builder = StringBuilder()
        for (i in p1Words.indices) {
            if (i > 0) p1Builder.append(" ")
            p1Builder.append(p1Words[i])
            emit(
                LocalAiStreamToken.Token(
                    paragraph1 = p1Builder.toString(),
                    paragraph2 = "",
                    isComplete = false,
                    response = response
                )
            )
            delay(delayPerWordMs)
        }

        val p2Text = response.paragraph2.trim()
        if (p2Text.isNotEmpty()) {
            delay(40L)
            val p2Words = p2Text.split(Regex("\\s+")).filter { it.isNotEmpty() }
            val p2Builder = StringBuilder()
            for (i in p2Words.indices) {
                if (i > 0) p2Builder.append(" ")
                p2Builder.append(p2Words[i])
                emit(
                    LocalAiStreamToken.Token(
                        paragraph1 = response.paragraph1,
                        paragraph2 = p2Builder.toString(),
                        isComplete = false,
                        response = response
                    )
                )
                delay(delayPerWordMs)
            }
        }

        emit(LocalAiStreamToken.Completed(response))
    }

    /**
     * Handles topic suggestion / chapter listing intent:
     * e.g., "give me some maths topic", "suggest chemistry chapters", "what can i learn in computer science".
     */
    private fun checkTopicSuggestionIntent(lowerQuery: String): LocalAiResponse? {
        val q = lowerQuery.replace(Regex("[^a-zA-Z0-9\\s]"), " ").trim()

        val suggestionTriggers = listOf(
            "give me", "suggest", "list", "topics", "topic", "chapters", "chapter",
            "what can i learn", "what should i learn", "what can you teach", "show me topics",
            "syllabus", "curriculum", "what topics", "some topic", "some topics"
        )

        val isSuggestionQuery = suggestionTriggers.any { q.contains(it) }
        if (!isSuggestionQuery) return null

        // Detect subject keyword
        val detectedSubjectKey = when {
            q.contains("math") || q.contains("maths") || q.contains("mathematics") -> "mathematics"
            q.contains("computer science") || q.contains(" cs") || q.startsWith("cs ") || q == "cs" || q.contains("coding") || q.contains("programming") -> "computer_science"
            q.contains("data science") || q.contains(" ds") || q.startsWith("ds ") || q == "ds" || q.contains("analytics") -> "data_science"
            q.contains("artificial intelligence") || q.contains(" ai") || q.startsWith("ai ") || q == "ai" || q.contains("machine learning") || q.contains("deep learning") -> "artificial_intelligence"
            q.contains("physics") -> "physics"
            q.contains("chemistry") -> "chemistry"
            q.contains("biology") || q.contains("bio") -> "biology"
            q.contains("history") || q.contains("world history") -> "world_history"
            else -> null
        }

        if (detectedSubjectKey != null) {
            val subjectKnowledge = synchronized(subjectMap) {
                subjectMap[detectedSubjectKey]
                    ?: subjectMap.values.find { it.subject.lowercase(Locale.ROOT).contains(detectedSubjectKey) }
            }

            val subjectName = subjectKnowledge?.subject ?: detectedSubjectKey.replace("_", " ").replaceFirstChar { it.uppercase() }
            val overview = subjectKnowledge?.overview ?: "$subjectName is a foundational academic discipline."
            val topics = subjectKnowledge?.topics ?: listOf(
                "Foundational Principles",
                "Core Mathematical/Scientific Laws",
                "Advanced Problem Solving",
                "Real-World Practical Applications"
            )

            val bulletedTopics = topics.joinToString("\n") { "• $it" }
            val startingPoint = topics.firstOrNull() ?: "the fundamentals"

            val p1 = "📚 **$subjectName Topics & Study Roadmap**\n\n$overview\n\nHere are the core topics and chapters you can learn in **$subjectName**:"
            val p2 = "$bulletedTopics\n\n💡 **Where to Begin:**\nStart with **$startingPoint** to build a strong foundation. Simply ask me: *\"Explain $startingPoint\"* or *\"Give me an example of $startingPoint\"* whenever you're ready!"

            return LocalAiResponse(
                paragraph1 = p1,
                paragraph2 = p2,
                matchedSubject = subjectName,
                matchedChapter = "Topic Suggestions",
                matchedTopic = startingPoint,
                confidenceScore = 98.0,
                isTopicSuggestion = true
            )
        }

        // General topic suggestion without specific subject: return all subjects catalog
        val p1 = "📚 **SYNAPSE AI Curriculum & Topic Catalog**\n\nHere are the core academic disciplines and chapters you can learn on-device with SYNAPSE AI:"
        val p2 = "• 📐 **Mathematics**: Quadratic Equations, Calculus (Derivatives & Integrals), Linear Algebra, Probability\n" +
                "• 💻 **Computer Science**: Algorithms (Big-O), Data Structures (Trees, Stacks, Queues), Operating Systems, Networks\n" +
                "• 📊 **Data Science**: Data Visualization, Exploratory Data Analysis, Supervised & Unsupervised Learning\n" +
                "• 🤖 **Artificial Intelligence**: Neural Networks, Backpropagation, Transformers, RAG\n" +
                "• ⚡ **Physics**: Waves & Sound, Newton's Laws of Motion, Thermodynamics, Electromagnetism\n" +
                "• 🧪 **Chemistry**: Chemical Bonding, Acids and Bases, Periodic Table, Stoichiometry\n" +
                "• 🧬 **Biology**: Cell Structure, Photosynthesis, Molecular Genetics & DNA\n" +
                "• 🏛️ **World History**: Ancient Civilizations, Industrial Revolution, World Wars, Indian Freedom Movement\n\n" +
                "💡 **Where to Begin:**\nTell me which subject interests you, or ask: *\"Suggest Mathematics topics\"* to explore a detailed learning roadmap!"

        return LocalAiResponse(
            paragraph1 = p1,
            paragraph2 = p2,
            confidenceScore = 95.0,
            isTopicSuggestion = true
        )
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

        if (examplePrompts.any { cleaned.contains(it) } || cleaned.contains("example")) {
            val exampleText = if (chunk.workedExample.isNotBlank()) {
                chunk.workedExample
            } else {
                generateTopicExample(chunk)
            }
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

        if (solvePrompts.any { cleaned.contains(it) } || cleaned.contains("root") || cleaned.contains("solve")) {
            val stepGuide = generateSolveGuide(chunk)
            val formulaText = if (chunk.formulaOrCoreRule.isNotBlank()) chunk.formulaOrCoreRule else chunk.content.substringBefore(".")
            return LocalAiResponse(
                paragraph1 = "📘 **Method & Solving Steps** (${chunk.subject} · ${chunk.topic})\n\nHere is how to solve and evaluate problems for **${chunk.topic}**:",
                paragraph2 = "$stepGuide\n\n💡 *Formula/Insight:* $formulaText.",
                matchedSubject = chunk.subject,
                matchedChapter = chunk.chapter,
                matchedTopic = chunk.topic,
                confidenceScore = 90.0,
                isFollowUp = true
            )
        }

        return LocalAiResponse(
            paragraph1 = "📘 **In-Depth Breakdown** (${chunk.subject} · ${chunk.topic})\n\nExpanding further on **${chunk.topic}**:",
            paragraph2 = "${chunk.content.ifBlank { "${chunk.definition}\n\n${chunk.formulaOrCoreRule}" }}\n\n💡 *Takeaway:* Mastering ${chunk.topic} connects directly to advanced applications in ${chunk.chapter}.",
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
            "world history", "history" -> {
                "The Industrial Revolution led to steam locomotives, enabling transport of raw coal from Welsh mines to Manchester manufacturing factories within hours instead of days."
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
        val queryWords = cleanQuery.split(Regex("[^a-zA-Z0-9]+")).filter { it.isNotBlank() }.toSet()

        if (cleanQuery.contains(normTopic)) score += 30.0
        if (cleanQuery.contains(normChapter)) score += 20.0
        if (cleanQuery.contains(normSubject)) score += 15.0

        for (kw in chunk.keywords) {
            val normKw = kw.lowercase(Locale.ROOT).trim()
            if (normKw.contains(" ")) {
                if (cleanQuery.contains(normKw)) {
                    score += 15.0
                }
            } else if (queryWords.contains(normKw)) {
                score += if (normKw.length >= 3) 8.0 else 5.0
            }

            for (token in tokens) {
                if (token == normKw) {
                    score += 5.0
                } else if (normKw.length >= 4 && token.length >= 4 && (normKw.startsWith(token) || token.startsWith(normKw))) {
                    score += 2.0
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
     * Builds structured conceptual explanation with clear Definition, Formula / Core Principle, and Key Example.
     */
    private fun buildEducationalResponse(chunk: KnowledgeChunk, score: Double): LocalAiResponse {
        val definitionText = if (chunk.definition.isNotBlank()) {
            chunk.definition.trim()
        } else {
            val sentences = chunk.content.split(Regex("(?<=\\.)\\s+"))
            if (sentences.size > 2) sentences.take(2).joinToString(" ").trim() else sentences.take(1).joinToString(" ").trim()
        }

        val formulaText = if (chunk.formulaOrCoreRule.isNotBlank()) {
            chunk.formulaOrCoreRule.trim()
        } else {
            val sentences = chunk.content.split(Regex("(?<=\\.)\\s+"))
            if (sentences.size > 2) sentences.drop(2).joinToString(" ").trim() else "Applying ${chunk.topic} is fundamental to mastering ${chunk.subject}."
        }

        val exampleText = if (chunk.workedExample.isNotBlank()) {
            chunk.workedExample.trim()
        } else {
            generateTopicExample(chunk)
        }

        val paragraph1 = "📘 **Concept & Definition** (${chunk.subject} · ${chunk.chapter})\n\n$definitionText"
        val paragraph2 = "⚡ **Formula / Core Principle**\n\n$formulaText\n\n💡 **Key Example & Practical Takeaway**\n\n$exampleText\n\n💡 *Topic: ${chunk.topic}*"

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

    /**
     * Synthesizes rich, structured educational responses across academic domains.
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
                paragraph2 = "⚡ **Formula / Core Principle**\n\n6CO₂ + 6H₂O + Photons → C₆H₁₂O₆ + 6O₂\n\nOperates in two stages: Light-dependent reactions (in thylakoids) generate ATP and NADPH, followed by the Calvin cycle (in stroma) synthesizing glucose carbohydrates.\n\n💡 **Key Example & Practical Takeaway**\nGreen leaves absorb atmospheric CO₂ through stomata and pull water from soil roots; oxygen is released as a vital byproduct sustaining aerobic life.",
                matchedSubject = "Biology",
                matchedChapter = "Plant Physiology",
                matchedTopic = "Photosynthesis",
                confidenceScore = 90.0
            )
        }

        if (q.contains("dna") || q.contains("rna") || q.contains("genetic") || q.contains("heredity") || q.contains("chromosome")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (Biology · Genetics & Molecular Biology)\n\nDNA (Deoxyribonucleic Acid) is the double-helical macromolecule containing genetic blueprints for biological organism development and functioning. RNA (Ribonucleic Acid) carries messages from DNA to ribosomes for protein synthesis.",
                paragraph2 = "⚡ **Formula / Core Principle**\n\nBase Pairing Rule: Adenine pairs with Thymine (A=T via 2 hydrogen bonds), and Guanine pairs with Cytosine (G≡C via 3 hydrogen bonds). In RNA, Uracil (U) replaces Thymine.\nCentral Dogma: DNA → (Transcription) → mRNA → (Translation) → Functional Proteins.\n\n💡 **Key Example & Practical Takeaway**\nA single nucleotide substitution in the hemoglobin gene causes sickle cell anemia, illustrating how molecular base sequences govern macroscopic human health.",
                matchedSubject = "Biology",
                matchedChapter = "Genetics & Molecular Biology",
                matchedTopic = "DNA and Genetics",
                confidenceScore = 90.0
            )
        }

        // 2. HISTORY
        if (q.contains("world war") || q.contains("ww1") || q.contains("ww2") || q.contains("world war 1") || q.contains("world war 2") || q.contains("world war i") || q.contains("world war ii")) {
            val isWW2 = q.contains("2") || q.contains("ii") || q.contains("hitler") || q.contains("nazi") || q.contains("pearl harbor") || q.contains("hiroshima")
            return if (isWW2) {
                LocalAiResponse(
                    paragraph1 = "📘 **Concept & Definition** (History · Modern World History)\n\nWorld War II (1939–1945) was a global conflict fought between the Allied Powers (USA, Great Britain, Soviet Union, France, China) and the Axis Powers (Nazi Germany, Imperial Japan, Fascist Italy), sparked by Germany's invasion of Poland on September 1, 1939.",
                    paragraph2 = "⚡ **Formula / Core Principle**\n\nKey Milestones: Battle of Stalingrad (1942–1943), D-Day Normandy Landings (1944), conclusion with German surrender and atomic bombings of Hiroshima & Nagasaki (August 1945).\n\n💡 **Key Example & Practical Takeaway**\nThe post-war Bretton Woods conference established modern international economic architecture including the World Bank and IMF.",
                    matchedSubject = "History",
                    matchedChapter = "Modern World History",
                    matchedTopic = "World War II",
                    confidenceScore = 90.0
                )
            } else {
                LocalAiResponse(
                    paragraph1 = "📘 **Concept & Definition** (History · Modern World History)\n\nWorld War I (1914–1918), known as the Great War, was a global European-centered conflict between the Allied Powers (Britain, France, Russia, USA, Italy) and the Central Powers (Germany, Austria-Hungary, Ottoman Empire, Bulgaria).",
                    paragraph2 = "⚡ **Formula / Core Principle**\n\nThe M-A-I-N Causes: Militarism, Alliances, Imperialism, and Nationalism. Sparked by the assassination of Archduke Franz Ferdinand in Sarajevo (June 28, 1914).\n\n💡 **Key Example & Practical Takeaway**\nTrench warfare on the Western Front reshaped 20th-century warfare, leading to the collapse of four empires and the Treaty of Versailles (1919).",
                    matchedSubject = "History",
                    matchedChapter = "Modern World History",
                    matchedTopic = "World War I",
                    confidenceScore = 90.0
                )
            }
        }

        // 3. GEOGRAPHY
        if (q.contains("plate tectonics") || q.contains("earthquake") || q.contains("volcano") || q.contains("continental drift") || q.contains("tectonic")) {
            return LocalAiResponse(
                paragraph1 = "📘 **Concept & Definition** (Geography · Geomorphology & Geology)\n\nPlate Tectonics is the unifying geological theory stating that Earth's outer lithosphere is broken into rigid plates that float and move atop the ductile asthenosphere mantle beneath them.",
                paragraph2 = "⚡ **Formula / Core Principle**\n\n• Convergent: Plates collide, causing subduction trenches and mountain ranges.\n• Divergent: Plates pull apart, creating mid-ocean ridges and rift valleys.\n• Transform: Plates slide horizontally past each other, releasing seismic stress as earthquakes.\n\n💡 **Key Example & Practical Takeaway**\nThe ongoing collision between the Indian Plate and Eurasian Plate elevates Mount Everest and the Himalayan range by ~5 mm annually.",
                matchedSubject = "Geography",
                matchedChapter = "Geomorphology",
                matchedTopic = "Plate Tectonics and Seismic Activity",
                confidenceScore = 90.0
            )
        }

        // Generic academic fallback with exact word boundary verification
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
                paragraph2 = "⚡ **Formula / Core Principle**\n\n1. Define primary variables, boundary conditions, and governing hypotheses.\n2. Apply the canonical laws and theorems established in $detectedSubject.\n3. Verify results and examine real-world trade-offs.\n\n💡 **Key Example & Practical Takeaway**\nMastering $cleanTopic develops rigorous problem-solving skills and forms a solid foundation for advanced studies in $detectedSubject.",
                matchedSubject = detectedSubject,
                matchedChapter = "Core Concepts",
                matchedTopic = cleanTopic,
                confidenceScore = 75.0
            )
        }

        return null
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
                "Try asking: 'Give me some maths topic', 'Suggest chemistry chapters', 'Explain photosynthesis', or 'What is Newton's second law?'"

        return LocalAiResponse(
            paragraph1 = paragraph1,
            paragraph2 = paragraph2,
            confidenceScore = 0.0,
            isGreeting = false
        )
    }

    /**
     * Synthesizes short, sweet, high-yield Smart Notes on-device from RAG knowledge chunks.
     * Extracts:
     * 1. Key Definition (1-2 sentences)
     * 2. Core Formulas / Laws (clean bullet points)
     * 3. 3 High-Yield Exam Takeaways
     */
    fun generateSmartNotes(chapterTitle: String, subjectName: String, lessonText: String? = null): SmartNotesResult {
        val lowerChapter = chapterTitle.lowercase(Locale.ROOT)
        val chapterTokens = tokenize(lowerChapter)

        val matchedChunk = synchronized(chunks) {
            chunks.firstOrNull { c ->
                (c.chapter.isNotBlank() && (lowerChapter.contains(c.chapter.lowercase(Locale.ROOT)) || c.chapter.lowercase(Locale.ROOT).contains(lowerChapter))) ||
                (c.topic.isNotBlank() && (lowerChapter.contains(c.topic.lowercase(Locale.ROOT)) || c.topic.lowercase(Locale.ROOT).contains(lowerChapter))) ||
                c.keywords.any { kw -> chapterTokens.contains(kw.lowercase(Locale.ROOT)) }
            }
        }

        val definition = if (matchedChunk != null && matchedChunk.definition.isNotBlank()) {
            matchedChunk.definition
        } else if (!lessonText.isNullOrBlank()) {
            val firstSentence = lessonText.split(Regex("[.!?]\\s+")).firstOrNull()?.trim()
            if (!firstSentence.isNullOrBlank()) "$firstSentence."
            else "$chapterTitle is a fundamental core concept in $subjectName essential for academic mastery."
        } else {
            "$chapterTitle is an essential core concept in $subjectName exploring foundational principles and applied real-world mechanisms."
        }

        val formulas = mutableListOf<String>()
        if (matchedChunk != null && matchedChunk.formulaOrCoreRule.isNotBlank()) {
            formulas.add(matchedChunk.formulaOrCoreRule)
        }
        if (matchedChunk != null && matchedChunk.workedExample.isNotBlank()) {
            formulas.add("Application: ${matchedChunk.workedExample}")
        }
        if (formulas.isEmpty()) {
            formulas.add("Core Principle: Analyze primary parameters and maintain structural invariants for $chapterTitle.")
            formulas.add("Standard Equation / Rule: Ground theoretical derivations on foundational $subjectName laws.")
        }

        val takeaways = listOf(
            "Exam Focus: Master the core definition of $chapterTitle and clearly articulate key steps or boundary conditions.",
            "High-Yield Mechanism: Understand mathematical relationships and governing formulas to solve multi-step problems accurately.",
            "Scoring Tip: State fundamental assumptions, sketch accompanying diagrams or workflows, and verify units or edge cases."
        )

        val md = buildString {
            appendLine("# $chapterTitle - AI Smart Summary")
            appendLine("**Subject:** $subjectName\n")
            appendLine("### 📘 Key Definition")
            appendLine(definition)
            appendLine()
            appendLine("### ⚡ Core Formulas & Laws")
            formulas.forEach { appendLine("• $it") }
            appendLine()
            appendLine("### 💡 High-Yield Exam Takeaways")
            takeaways.forEachIndexed { i, t -> appendLine("${i + 1}. $t") }
        }

        return SmartNotesResult(
            title = chapterTitle,
            subject = subjectName,
            keyDefinition = definition,
            coreFormulasOrLaws = formulas,
            examTakeaways = takeaways,
            fullMarkdown = md
        )
    }

    fun getAllChunks(): List<KnowledgeChunk> = synchronized(chunks) { chunks.toList() }

    fun getSubjectKnowledge(subjectKey: String): SubjectKnowledge? = synchronized(subjectMap) {
        subjectMap[subjectKey.lowercase(Locale.ROOT)]
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
