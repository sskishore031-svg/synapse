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
import java.util.concurrent.ConcurrentHashMap

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
    val paragraph2: String = "",
    val matchedSubject: String? = null,
    val matchedChapter: String? = null,
    val matchedTopic: String? = null,
    val confidenceScore: Double = 0.0,
    val isGreeting: Boolean = false,
    val isFollowUp: Boolean = false,
    val isTopicSuggestion: Boolean = false,
    val isFarewell: Boolean = false,
    val isNameCapture: Boolean = false,
    val capturedName: String? = null,
    val isPreferenceUpdate: Boolean = false
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
 * Result of analyzing a pasted Python code snippet or SQL query.
 */
data class CodeSnippetAnalysis(
    val language: String,
    val logicBreakdown: String,
    val timeComplexity: String,
    val spaceComplexity: String,
    val interviewEdgeCases: String
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
        "why", "how", "explain", "describe", "define", "tell", "me", "about", "give", "please",
        "work", "show", "from", "my", "your", "i", "you", "we", "they", "it", "this", "that",
        "and", "or", "by"
    )

    private val genericSubjectWords = setOf(
        "physics", "math", "maths", "mathematics", "chemistry", "computer", "computers",
        "science", "sciences", "biology", "history", "economics", "engineering", "ai",
        "data", "python", "sql", "coding", "programming", "database", "databases", "dbms", "rdbms", "aptitude"
    )

    private val stemCache = ConcurrentHashMap<String, String>()
    private val tokenCache = ConcurrentHashMap<String, List<String>>()

    // Dynamic User Preferences & Conversational Persona Memory
    @Volatile var preferredName: String = ""
    @Volatile var includeFormulas: Boolean = true
    @Volatile var includeExamples: Boolean = true
    @Volatile var useEmojis: Boolean = false // Default false: use clean * bullet points everywhere!
    @Volatile var consecutiveUnclearCount: Int = 0
    @Volatile var isWaitingForName: Boolean = false

    var awaitingPreferredName: Boolean
        get() = isWaitingForName
        set(value) { isWaitingForName = value }

    /**
     * Resolves the active name to address the user by (custom nickname if explicitly set in chat, or empty string).
     * Never auto-injects account username or generic placeholders like "friend".
     */
    fun getEffectiveUserName(): String {
        return preferredName.trim()
    }

    /**
     * Clears cached tokenized queries and stemmed word mappings.
     */
    fun clearCaches() {
        stemCache.clear()
        tokenCache.clear()
    }

    /**
     * Clears rolling context turns from memory.
     */
    fun clearHistory() {
        synchronized(contextHistory) {
            contextHistory.clear()
        }
        clearCaches()
    }

    /**
     * Stems common English plural and inflectional suffixes for robust RAG concept matching:
     * e.g., "waves" -> "wave", "equations" -> "equation", "matrices" -> "matrix", "laws" -> "law".
     * Caches results in [stemCache] for high-throughput sub-5ms semantic retrieval.
     */
    fun stem(word: String): String {
        return stemCache.getOrPut(word) {
            val w = word.lowercase(Locale.ROOT).trim()
            if (w.length <= 2) {
                w
            } else {
                when {
                    w == "matrices" -> "matrix"
                    w == "indices" -> "index"
                    w == "vertices" -> "vertex"
                    w.endsWith("ics") -> w // Preserves physics, mechanics, optics, robotics, mathematics
                    w.endsWith("ies") && w.length > 4 -> w.dropLast(3) + "y"
                    w.endsWith("ves") && w.length > 4 -> w.dropLast(1) // waves -> wave, derivatives -> derivative
                    w.endsWith("shes") || w.endsWith("ches") || w.endsWith("sses") || w.endsWith("xes") || w.endsWith("zes") -> w.dropLast(2)
                    w.endsWith("s") && !w.endsWith("ss") && !w.endsWith("us") && !w.endsWith("is") && w.length > 3 -> w.dropLast(1) // equations -> equation, laws -> law, algorithms -> algorithm
                    else -> w
                }
            }
        }
    }

    private val greetings = setOf(
        "gm", "good morning", "good afternoon", "good evening", "gn", "good night",
        "hi", "hello", "hey", "vanakkam", "namaste", "hola", "greetings", "hii", "heyy", "yo"
    )

    private val capabilityPhrases = setOf(
        "what you can do", "what can you do", "what can u do", "what u can do",
        "wat can u do", "wat you can do", "wat can you do", "how can you help",
        "how can u help", "how do you help", "how does this help", "what do you do",
        "what does it do", "what does this app do", "what does synapse do",
        "what can synapse do", "what are your capabilities", "what are your features",
        "what are the features", "what capabilities", "your capabilities",
        "tell me what you can do", "tell me what u can do", "how to use",
        "how to use this app", "how to use synapse", "how do i use this",
        "how do i use synapse", "how to study", "help me", "help me study",
        "help", "can you help me", "features", "app features", "capabilities",
        "guide me", "what do you know", "what can you teach", "show features", "list features"
    )

    private val outOfScopePhrases = setOf(
        "can you eat", "do you eat", "what do you eat", "will you eat", "are you eating", "eat food",
        "who is rajini", "who is rajinikanth", "who is kamal", "rajini", "rajinikanth", "kamal",
        "kamal haasan", "vijay", "thalapathy", "ajith", "thala", "suriya", "dhanush", "simbu",
        "sivakarthikeyan", "sk", "actor", "actress", "movie", "movies", "cinema", "box office", "song", "songs",
        "cricket", "ipl", "football", "match score", "politics", "politician", "minister",
        "recipe", "food", "cook", "cooking", "restaurant", "hotel", "taste", "delicious",
        "girlfriend", "boyfriend", "marry me", "do you love me", "love you", "joke", "jokes", "weather", "horoscope"
    )

    private val identityPhrases = setOf(
        "who are you", "who r u", "who are u", "who r you", "what are you", "what r u",
        "what is your name", "whats your name", "what s your name", "whats ur name", "what is ur name",
        "tell me your name", "your name", "name please",
        "nee yaru", "neenga yaru", "un peru enna", "unga peru enna", "un per enna", "unga per enna",
        "nee yar", "neenga yar", "yaaru nee", "yaar nee", "yaaru neenga", "yaar neenga",
        "unnala enna panna mudiyum", "epdi work aagum", "epdi work pannuva",
        "what is synapse ai", "what is synapse", "what is this app", "tell me about yourself",
        "tell me about synapse", "tell me about synapse ai", "about yourself", "introduce yourself",
        "are you chatgpt", "are u chatgpt", "are you chat gpt", "are you gpt",
        "are you offline", "are u offline", "is this offline", "is synapse offline",
        "how do you work", "how does this work", "how does synapse work",
        "how does synapse ai work", "how you work", "who made you", "who created you",
        "who built you", "who developed you", "what kind of ai are you"
    )

    private val wellBeingPhrases = setOf(
        "how are you", "how are u", "how r you", "how r u",
        "how are you doing", "how are u doing", "how r u doing", "how do you do",
        "how is it going", "hows it going", "how s it going", "how are things",
        "hope you are well", "hope you re well", "what's up", "whats up",
        "what s up", "wassup", "sup", "are you okay", "are you ok",
        "how have you been"
    )

    private val doubtPhrases = setOf(
        "i have some doubts", "i have a doubt", "i want some doubts", "i have doubts",
        "have some doubts", "have a doubt", "clear my doubt", "clear my doubts",
        "help me with my doubts", "help with doubts", "i want some doubts for my subjects",
        "i have doubts for my subjects", "subject doubts", "doubts in subjects",
        "clear doubts", "doubt clarification", "solve my doubts", "solve my doubt"
    )

    private val subjectCatalogPhrases = setOf(
        "how many subjects have you", "how many subjects do you have", "how many subjects are there",
        "how many subjects", "how many subject", "what subjects are available", "what subjects do you have",
        "what subjects do you know", "what subjects can you teach", "which subjects are available",
        "which subjects do you have", "list all subjects", "list subjects", "show subjects",
        "show all subjects", "all subjects", "available subjects", "subjects catalog",
        "curriculum catalog", "what are the subjects", "tell me all subjects", "tell me the subjects"
    )

    private val howItWorksPhrases = setOf(
        "how should you to work", "how should you work", "how you should work",
        "how should i use you", "how should you operate", "how to work",
        "how should you be working"
    )

    private val gratitudeWords = setOf(
        "thanks", "thank", "thx", "tq", "thankyou", "thanksalot",
        "awesome", "great", "ok", "okay", "nice", "cool",
        "perfect", "understood", "alright", "super", "well done",
        "good job", "k", "kk"
    )

    private val gratitudePhrases = setOf(
        "thank you", "thank u", "thanks a lot", "thank you so much",
        "thank you very much", "thanks so much", "many thanks", "got it",
        "got it thanks", "got it thank you", "ok thanks", "okay thanks",
        "great thanks", "nice thanks", "understood thanks", "thanks a bunch"
    )

    private val greetingWords = setOf(
        "hi", "hello", "hey", "gm", "gn", "ge", "ga", "yo", "sup",
        "howdy", "hiya", "namaste", "vanakkam", "hola", "bonjour", "greetings", "hii", "heyy", "heya"
    )

    private val greetingPhrases = setOf(
        "good morning", "good evening", "good afternoon", "good day", "good night",
        "hey there", "hi there", "hello there"
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

        // 4. Supplement with UniversityCurriculumGenerator chunks if available
        try {
            val genChunks = com.siamts.synapse.data.local.UniversityCurriculumGenerator.getAllRAGChunks()
            if (genChunks.isNotEmpty()) {
                registerChunks(genChunks)
                logD(TAG, "Supplemented UniversityCurriculumGenerator chunks. Total chunks: ${chunks.size}")
            }
        } catch (_: Throwable) {}
    }

    /**
     * Dynamically registers knowledge chunks from memory into the offline RAG index.
     */
    fun registerChunks(newChunks: List<KnowledgeChunk>) {
        if (newChunks.isEmpty()) return
        synchronized(chunks) {
            val existingIds = chunks.map { it.chunkId }.toSet()
            val filtered = newChunks.filter { it.chunkId !in existingIds }
            chunks.addAll(filtered)
        }
    }

    /**
     * Loads knowledge dataset directly from a JSON string (supports both Map and List schemas).
     */
    fun loadFromJson(jsonString: String) {
        try {
            clearCaches()
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
        preferredName = ""
        consecutiveUnclearCount = 0
        isWaitingForName = false
        includeFormulas = true
        includeExamples = true
        useEmojis = false
    }

    /**
     * Returns current rolling context turns.
     */
    fun getContextHistory(): List<ConversationTurn> = synchronized(contextHistory) {
        contextHistory.toList()
    }

    /**
     * Generates a context-aware 100% on-device response.
     * Follows strict 5-stage intent order:
     * 1. Name-setting & Nickname Memory
     * 2. Style/Formatting Preferences
     * 3. Farewells, Greetings & Small Talk
     * 4. Out-of-Scope & Casual Slang Guardrails
     * 5. Weighted Academic RAG Retrieval & Context Memory
     */
    fun generateResponse(userQuery: String): LocalAiResponse {
        val query = userQuery.trim()
        if (query.isBlank()) {
            return buildAcademicGuidanceResponse("")
        }

        val normalized = normalizeQuery(query)
        val lowerQuery = query.lowercase(Locale.ROOT)

        // 1. STAGE 1: Name-Setting & Nickname Memory ("call me Kishore", "enoda name solu kupdu")
        val nameSettingResp = checkNameSettingIntent(query, lowerQuery, normalized)
        if (nameSettingResp != null) {
            consecutiveUnclearCount = 0
            return nameSettingResp
        }

        // 2. STAGE 2: Formatting & Style Preferences ("no formula", "no example", "no emoji", "no need of formula and example")
        val stylePrefResp = checkStylePreferenceIntent(lowerQuery, normalized)
        if (stylePrefResp != null) {
            consecutiveUnclearCount = 0
            return stylePrefResp
        }

        // 3. STAGE 3: Farewells, Greetings & Small Talk (Intercepted BEFORE any topic/RAG search!)
        val farewellResp = checkFarewellIntent(normalized)
        if (farewellResp != null) {
            consecutiveUnclearCount = 0
            return farewellResp
        }

        if (isWellBeingQuery(normalized)) {
            consecutiveUnclearCount = 0
            return buildWellBeingResponse()
        }

        if (isIdentityQuery(normalized)) {
            consecutiveUnclearCount = 0
            return buildIdentityResponse()
        }

        if (isHowItWorksQuery(normalized)) {
            consecutiveUnclearCount = 0
            return buildHowItWorksResponse()
        }

        if (isDoubtAssistanceQuery(normalized)) {
            consecutiveUnclearCount = 0
            return buildDoubtAssistanceResponse()
        }

        if (isSubjectCatalogQuery(normalized)) {
            consecutiveUnclearCount = 0
            return buildSubjectCatalogResponse()
        }

        if (isCapabilitiesQuery(normalized)) {
            consecutiveUnclearCount = 0
            return buildCapabilitiesResponse()
        }

        if (isGratitudeQuery(normalized)) {
            consecutiveUnclearCount = 0
            return buildGratitudeResponse()
        }

        if (isGreetingQuery(normalized)) {
            consecutiveUnclearCount = 0
            return buildGreetingResponse(normalized)
        }

        val smallTalkResp = checkSmallTalkIntent(normalized)
        if (smallTalkResp != null) {
            consecutiveUnclearCount = 0
            return smallTalkResp
        }

        val topicSuggestionResp = checkTopicSuggestionIntent(lowerQuery)
        if (topicSuggestionResp != null) {
            consecutiveUnclearCount = 0
            return topicSuggestionResp
        }

        val appFeatureResp = checkAppFeatureQuery(lowerQuery)
        if (appFeatureResp != null) {
            consecutiveUnclearCount = 0
            return appFeatureResp
        }

        // 4. STAGE 4: Explicit Out-of-Scope & Casual Slang Guardrails (Evaluated before code analysis)
        if (isOutOfScopeQuery(normalized)) {
            consecutiveUnclearCount = 0
            return buildOutOfScopeResponse()
        }

        val casualSlangResp = checkCasualSlangIntent(lowerQuery, normalized)
        if (casualSlangResp != null) {
            return casualSlangResp
        }

        val codeAnalysis = analyzeCodeOrQuerySnippet(query)
        if (codeAnalysis != null) {
            consecutiveUnclearCount = 0
            val p1 = "* **${codeAnalysis.language} Logic Breakdown:**\n${codeAnalysis.logicBreakdown}"
            val p2 = "* **Time & Space Complexity:**\n• Time Complexity: ${codeAnalysis.timeComplexity}\n• Space Complexity: ${codeAnalysis.spaceComplexity}\n\n* **Placement Interview Edge Cases:**\n${codeAnalysis.interviewEdgeCases}"
            val subject = if (codeAnalysis.language.contains("SQL")) "SQL & Database Management" else "Python & Coding Logic"
            val topic = if (codeAnalysis.language.contains("SQL")) "SQL Query Analysis" else "Python Code Analysis"
            recordTurn(
                query,
                KnowledgeChunk(
                    chunkId = "code_${System.currentTimeMillis()}",
                    subject = subject,
                    chapter = "Code Analysis",
                    topic = topic,
                    content = "$p1\n$p2"
                )
            )
            return LocalAiResponse(
                paragraph1 = p1,
                paragraph2 = p2,
                matchedSubject = subject,
                matchedChapter = "Code & Query Helper",
                matchedTopic = topic,
                confidenceScore = 99.0
            )
        }

        // 4B. STAGE 4B: Subject Overview Intent ("what about biology", "uses of math", etc.)
        val subjectOverviewResp = checkSubjectOverviewIntent(lowerQuery, normalized)
        if (subjectOverviewResp != null) {
            consecutiveUnclearCount = 0
            return subjectOverviewResp
        }

        // 4C. STAGE 4C: Direct Question Intent (Origin / Trivia, VS Comparisons, Code Snippets, Core Overviews)
        val directQuestionResp = checkDirectQuestionIntent(query, lowerQuery, normalized)
        if (directQuestionResp != null) {
            consecutiveUnclearCount = 0
            return directQuestionResp
        }

        // Lazy asset load fallback if not loaded yet
        if (chunks.isEmpty()) {
            try {
                val appCtx = SynapseApplication.getAppContext()
                loadFromAssets(appCtx)
            } catch (_: Throwable) {}
        }

        // 5. STAGE 5: Weighted Academic RAG Retrieval & Context Memory
        val lastTopicTurn = synchronized(contextHistory) {
            contextHistory.lastOrNull { it.matchedTopic != null || it.chunkId != null }
        }

        if (lastTopicTurn != null && isFollowUpQuery(lowerQuery)) {
            val activeChunk = synchronized(chunks) {
                chunks.find { it.chunkId == lastTopicTurn.chunkId }
                    ?: chunks.find { it.topic.equals(lastTopicTurn.matchedTopic, ignoreCase = true) }
            }

            if (activeChunk != null) {
                consecutiveUnclearCount = 0
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
                consecutiveUnclearCount = 0
                val followUpResponse = buildContextAwareFollowUpResponse(lowerQuery, syntheticChunk)
                recordTurn(userQuery, syntheticChunk)
                return followUpResponse
            }
        }

        // Tokenize query
        val tokens = tokenize(lowerQuery)
        if (tokens.isEmpty()) {
            return handleUnclearOrEmpty(query)
        }

        // Match against RAG Chunks
        val scoredChunks = synchronized(chunks) {
            chunks.map { chunk ->
                val score = computeRelevanceScore(lowerQuery, tokens, chunk)
                Pair(score, chunk)
            }
        }

        val bestMatch = scoredChunks.maxByOrNull { it.first }
        val bestScore = bestMatch?.first ?: 0.0
        val tokenCount = tokens.size.coerceAtLeast(1)
        val normalizedScore = (bestScore / (tokenCount * 25.0)).coerceIn(0.0, 1.0)

        if (bestMatch != null && bestScore >= 12.0 && normalizedScore >= 0.25) {
            consecutiveUnclearCount = 0
            val response = buildEducationalResponse(bestMatch.second, bestScore, isElaborated = isElaborationQuery(normalized))
            recordTurn(userQuery, bestMatch.second)
            return response
        }

        // Context-assisted match ONLY when query has explicit context pronoun/word
        if (lastTopicTurn != null && hasExplicitContextPronoun(lowerQuery)) {
            val contextQuery = "$lowerQuery ${lastTopicTurn.matchedTopic.orEmpty()} ${lastTopicTurn.matchedChapter.orEmpty()}"
            val contextTokens = tokenize(contextQuery)
            val contextScored = synchronized(chunks) {
                chunks.map { chunk ->
                    Pair(computeRelevanceScore(contextQuery, contextTokens, chunk), chunk)
                }
            }
            val contextBest = contextScored.maxByOrNull { it.first }
            if (contextBest != null && contextBest.first >= 12.0) {
                consecutiveUnclearCount = 0
                val resp = buildEducationalResponse(contextBest.second, contextBest.first, isElaborated = isElaborationQuery(normalized))
                recordTurn(userQuery, contextBest.second)
                return resp
            }
        }

        // Legitimate academic inquiry for curriculum / syllabus / college concepts
        val academicInquiryWords = setOf("syllabus", "curriculum", "study", "exam", "college", "university", "engineering", "degree", "course", "major")
        if (tokens.any { it in academicInquiryWords } || academicInquiryWords.any { normalized.contains(it) }) {
            consecutiveUnclearCount = 0
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
            return buildAcademicGuidanceResponse(query, tokens)
        }

        // Unrecognized query -> handled gracefully by unclear guardrail (NEVER returns Euler's formula!)
        return handleUnclearOrEmpty(query)
    }

    /**
     * Progressive streaming API returning AI response as a Kotlin Flow with word-by-word emission (20-30ms delay).
     */
    fun streamResponse(userQuery: String, delayPerWordMs: Long = 20L): Flow<LocalAiStreamToken> = flow {
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
        if (q.contains("list comprehension") || q.contains("linked list") || q.contains("adjacency list")) {
            return null
        }

        val suggestionTriggers = listOf(
            "give me", "suggest", "list topics", "list chapters", "list of", "topics", "topic", "chapters", "chapter",
            "what can i learn", "what should i learn", "what can you teach", "show me topics",
            "syllabus", "curriculum", "what topics", "some topic", "some topics"
        )

        val isSuggestionQuery = suggestionTriggers.any { q.contains(it) }
        if (!isSuggestionQuery) return null

        // Detect subject keyword
        val detectedSubjectKey = when {
            q.contains("python") || q.contains("coding") || q.contains("programming") || q.contains("logic") -> "python"
            q.contains("sql") || q.contains("database") || q.contains("dbms") || q.contains("rdbms") -> "sql"
            q.contains("artificial intelligence") || q.contains(" ai") || q.startsWith("ai ") || q == "ai" || q.contains("tech") || q.contains("llm") -> "ai_tech"
            q.contains("aptitude") || q.contains("quant") || q.contains("interview") || q.contains("puzzle") -> "aptitude"
            q.contains("math") || q.contains("maths") || q.contains("mathematics") -> "mathematics"
            q.contains("computer science") || q.contains(" cs") || q.startsWith("cs ") || q == "cs" -> "computer_science"
            q.contains("data science") || q.contains(" ds") || q.startsWith("ds ") || q == "ds" || q.contains("analytics") -> "data_science"
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

        // General topic suggestion without specific subject: return all 4 placement tracks catalog
        val p1 = "📚 **SYNAPSE AI Placement & Micro-Learning Tracks**\n\nHere are the 4 high-impact career and technical tracks you can master with SYNAPSE AI:"
        val p2 = "• 🐍 **Python & Coding Logic**: Variables, Data Types, Control Flow, OOP, Exceptions & File I/O\n" +
                "• 🗄️ **SQL & Database Management**: Relational Keys, SELECT Queries, Joins, Normalization, Transactions & ACID\n" +
                "• 🧠 **AI & Modern Tech Concepts**: LLMs, Prompt Engineering, RAG Architecture, REST APIs, Cloud & Cybersecurity\n" +
                "• 🎯 **Aptitude & Interview Prep**: Quantitative Shortcuts, Logical Puzzles, STAR Interview Framework, Technical Resumes\n\n" +
                "💡 **Where to Begin:**\nTell me which track interests you, or ask: *\"Explain Python memory model\"* or *\"What are SQL joins?\"* to get started!"

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
            q.contains("alarm") || q.contains("remind me") || q.contains("reminder") || q.startsWith("remind ") -> {
                LocalAiResponse(
                    paragraph1 = "⏰ **SYNAPSE Study Alarm & Reminders**\n\nI can schedule daily study alarms for you! Simply tell me something like:\n• *'Set a study alarm for 9:44 AM'*\n• *'Remind me to study at 8 PM'*\n\nYour alarm will be scheduled directly on your device with high-priority notifications so you never miss a revision session.",
                    paragraph2 = "Consistent daily study is key to cracking placement interviews!",
                    confidenceScore = 98.0
                )
            }
            q.startsWith("open ") || q.startsWith("go to ") || q.startsWith("navigate to ") || q.contains(" screen") || q.contains("screen open") -> {
                val targetScreen = when {
                    q.contains("home") -> "Home"
                    q.contains("learn") || q.contains("track") -> "Learn"
                    q.contains("quiz") -> "Quiz Hub"
                    q.contains("note") -> "Notes"
                    q.contains("profile") -> "Profile"
                    q.contains("analytics") || q.contains("progress") -> "Progress & Analytics"
                    else -> "requested"
                }
                LocalAiResponse(
                    paragraph1 = "Opening your **$targetScreen** screen now! Navigating...",
                    paragraph2 = "You can also switch tabs anytime using the navigation bar at the bottom.",
                    confidenceScore = 98.0
                )
            }
            q.contains("note") && (q.contains("how") || q.contains("create") || q.contains("save") || q.contains("where") || q.contains("download")) -> {
                LocalAiResponse(
                    paragraph1 = "📝 **SYNAPSE Notes Organizer**\n\nYou can access, create, and organize study notes by tapping the Notes icon in the bottom navigation bar.",
                    paragraph2 = "⚡ **Key Capabilities**\n\n• Tap the **➕** button on the Notes screen to create Common, Subject-specific, or Saved notes.\n• Access **Subject Notes** directly for Python, SQL, AI & Modern Tech, and Aptitude.\n• Save or download any note for instant fast study.",
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

    private fun normalizeQuery(query: String): String {
        return query.lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun isCapabilitiesQuery(normalized: String): Boolean {
        // Exclude technical domain topics (e.g. feature engineering, feature vector)
        val technicalFeatureTerms = listOf(
            "feature vector", "feature extraction", "feature selection",
            "feature scaling", "feature engineering", "feature map"
        )
        if (technicalFeatureTerms.any { normalized.contains(it) }) {
            return false
        }

        // Exclude direct concept questions starting with educational verbs (e.g., "explain quadratic equation")
        val educationalActions = setOf("explain", "solve", "define", "derive", "calculate", "prove")
        val words = normalized.split(" ").filter { it.isNotBlank() }
        if (words.isNotEmpty() && words[0] in educationalActions && words.size > 2) {
            return false
        }
        if (normalized.contains("can you explain") || normalized.contains("can you solve") || normalized.contains("can you tell me about")) {
            return false
        }

        // Exact match against capability phrases
        if (capabilityPhrases.contains(normalized)) {
            return true
        }

        // Substring match for key phrases
        for (phrase in capabilityPhrases) {
            if (phrase.length >= 6 && normalized.contains(phrase)) {
                return true
            }
        }

        // Pattern matching: "what ... you/u do", "wat ... u/you do", "help me study"
        val hasWhatOrWat = words.contains("what") || words.contains("wat")
        val hasYouOrU = words.contains("you") || words.contains("u")
        val hasDo = words.contains("do")
        if (hasWhatOrWat && (words.contains("can") || words.contains("could")) && hasYouOrU && hasDo) {
            return true
        }
        if (hasWhatOrWat && words.contains("you") && words.contains("can") && words.contains("do")) {
            return true
        }
        if (hasWhatOrWat && words.contains("u") && words.contains("can") && words.contains("do")) {
            return true
        }
        if (normalized.startsWith("how can you help") || normalized.startsWith("how can u help") || normalized.startsWith("how to use")) {
            return true
        }
        if (words.contains("capabilities") || words.contains("capability")) {
            return true
        }
        if (normalized == "features" || normalized.startsWith("features of") || normalized.contains("your features") || normalized.contains("app features") || normalized.contains("key features")) {
            return true
        }
        if (words.size <= 3 && words.contains("help")) {
            return true
        }

        return false
    }

    private fun buildCapabilitiesResponse(): LocalAiResponse {
        return LocalAiResponse(
            paragraph1 = "I'm your study friend here to make learning easy! I can give you clear study notes, test you with interactive quizzes, explain any subject concept step-by-step, and clear your exam doubts whenever you need.",
            paragraph2 = "",
            confidenceScore = 100.0,
            isGreeting = false
        )
    }

    private fun isWellBeingQuery(normalized: String): Boolean {
        if (wellBeingPhrases.contains(normalized)) {
            return true
        }

        for (phrase in wellBeingPhrases) {
            if (phrase.length >= 5 && (normalized == phrase || normalized.startsWith("$phrase ") || normalized.endsWith(" $phrase") || normalized.contains(" $phrase "))) {
                return true
            }
        }

        val words = normalized.split(" ").filter { it.isNotBlank() }
        if (words.contains("how") && (words.contains("are") || words.contains("r")) && (words.contains("you") || words.contains("u"))) {
            return true
        }
        if (normalized.startsWith("how are you") || normalized.startsWith("how r u") || normalized.startsWith("hows it going")) {
            return true
        }

        return false
    }

    private fun buildWellBeingResponse(): LocalAiResponse {
        val name = preferredName.trim()
        val text = if (name.isNotBlank()) {
            "Hello, $name! I'm doing great and ready to help you study. What topic shall we explore today?"
        } else {
            "Hello! I'm doing great and ready to help you study. What topic shall we explore today?"
        }
        return LocalAiResponse(
            paragraph1 = text,
            paragraph2 = "",
            confidenceScore = 100.0,
            isGreeting = true
        )
    }

    private fun isIdentityQuery(normalized: String): Boolean {
        if (normalized.contains("capabilit")) return false

        val tanglishPatterns = setOf(
            "nee yaru", "neenga yaru", "un peru enna", "unga peru enna",
            "un per enna", "unga per enna", "nee yar", "neenga yar",
            "yaaru nee", "yaar nee", "yaaru neenga", "yaar neenga",
            "unnala enna panna mudiyum", "epdi work aagum", "epdi work pannuva"
        )
        if (tanglishPatterns.any { normalized.contains(it) }) return true

        val nameQueries = setOf(
            "what is your name", "whats your name", "what s your name", "whats ur name",
            "what is ur name", "tell me your name", "your name", "name please",
            "who are you", "who r u", "who are u", "who r you", "what are you", "what r u"
        )
        if (nameQueries.contains(normalized)) return true

        if (identityPhrases.contains(normalized)) {
            return true
        }

        for (phrase in identityPhrases) {
            if (phrase.length >= 6 && (normalized == phrase || normalized.startsWith("$phrase ") || normalized.endsWith(" $phrase") || normalized.contains(" $phrase "))) {
                return true
            }
        }

        val words = normalized.split(" ").filter { it.isNotBlank() }
        if (words.contains("name") && (words.contains("your") || words.contains("ur") || words.contains("what") || words.contains("whats"))) {
            return true
        }
        if ((words.contains("who") || words.contains("what") || words.contains("wat")) && (words.contains("you") || words.contains("u")) && words.contains("are")) {
            return true
        }
        if (words.contains("who") && (words.contains("are") || words.contains("r")) && (words.contains("you") || words.contains("u"))) {
            return true
        }
        if (normalized.contains("who are you") || normalized.contains("who are u") || normalized.contains("who r u")) {
            return true
        }
        if (normalized.contains("synapse ai") && (words.contains("what") || words.contains("who") || words.contains("about"))) {
            return true
        }
        if (words.contains("chatgpt") || words.contains("gpt")) {
            return true
        }
        if (normalized == "are you offline" || normalized == "are you online" || normalized == "is it offline" || normalized == "are u offline") {
            return true
        }
        if (normalized.contains("how do you work") || normalized.contains("how does it work") || normalized.contains("how you work") || normalized.contains("how synapse works") || normalized.contains("how synapse work")) {
            return true
        }

        return false
    }

    private fun buildIdentityResponse(): LocalAiResponse {
        return LocalAiResponse(
            paragraph1 = "I am SYNAPSE AI, your intelligent career & placement learning companion! I help you master 4 high-impact career tracks: Python & Coding Logic, SQL & Database Management, AI & Modern Tech Concepts, and Aptitude & Interview Prep with clear concept explanations, interactive in-chat quizzes, and smart revision notes. Ask me any study doubt, or tell me to test you with a quiz!",
            paragraph2 = "",
            confidenceScore = 100.0,
            isGreeting = false
        )
    }

    private fun checkSmallTalkIntent(normalized: String): LocalAiResponse? {
        val reply = when {
            normalized.contains("you are awesome") || normalized.contains("you are great") || normalized.contains("youre awesome") || normalized.contains("you are cool") ->
                "Thank you so much! You're awesome for putting in the effort to study. Let's keep it going!"
            normalized.contains("i am bored") || normalized.contains("im bored") || normalized == "bored" ->
                "How about a quick 10-question placement quiz or learning a fascinating coding concept? Pick a track and let's test your skills!"
            normalized.contains("stressed about exams") || normalized.contains("i am stressed") || normalized.contains("im stressed") || normalized.contains("exam stress") || normalized == "stressed" ->
                "Take a deep breath! You've got this. Let's break your preparation down into bite-sized 10-minute micro-lessons so you feel confident and in control."
            normalized.contains("motivate me") || normalized.contains("give me motivation") || normalized.contains("motivation") ->
                "Every senior engineer and tech leader was once a beginner who refused to quit. Focus on mastering just one concept right now—progress adds up fast!"
            normalized.contains("who made you") || normalized.contains("who created you") || normalized.contains("who built you") ->
                "I was built by the SYNAPSE AI team to be your personal tech career & placement study companion!"
            normalized.contains("are you smart") || normalized.contains("how smart are you") ->
                "I know Python, SQL, Modern AI, and Aptitude inside and out, and my goal is helping you excel in technical placement exams and interviews!"
            normalized.contains("hello friend") || normalized.contains("hi friend") || normalized.contains("hey friend") ->
                "Hey friend! Great to see you. Which track shall we dive into today: Python, SQL, AI, or Aptitude?"
            else -> null
        }
        return reply?.let {
            LocalAiResponse(
                paragraph1 = it,
                paragraph2 = "",
                confidenceScore = 100.0,
                isGreeting = false
            )
        }
    }

    private fun checkNameSettingIntent(rawQuery: String, lowerQuery: String, normalized: String): LocalAiResponse? {
        val trimmedRaw = rawQuery.trim()

        // 1. If currently waiting for user to provide their name (e.g. following "call me by my name")
        if (isWaitingForName) {
            val words = trimmedRaw.split(Regex("\\s+"))
            val candidate = words.lastOrNull { it.isNotBlank() && it.first().isLetter() } ?: ""
            val cleanName = candidate.filter { it.isLetter() }.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            if (cleanName.isNotBlank() && cleanName.length >= 2) {
                preferredName = cleanName
                isWaitingForName = false
                consecutiveUnclearCount = 0
                return LocalAiResponse(
                    paragraph1 = "Got it, $preferredName! From now on, I will call you $preferredName.",
                    paragraph2 = "",
                    confidenceScore = 100.0,
                    isGreeting = true,
                    isNameCapture = true,
                    capturedName = preferredName
                )
            }
        }

        // 2. Direct name instruction first: "call me Kishore", "from now on call me Kishore", "call me by my name Kishore", "my name is Kishore"
        val callMeMatch = Regex(
            "^(?:from\\s+now\\s+on\\s+)?(?:call\\s+me\\s+(?:by\\s+my\\s+name\\s+|by\\s+name\\s+|with\\s+my\\s+name\\s+)?|my\\s+name\\s+is\\s+|name\\s+is\\s+|i\\s+am\\s+|i['’]m\\s+)([a-zA-Z]+)",
            RegexOption.IGNORE_CASE
        ).find(trimmedRaw)
        if (callMeMatch != null) {
            val candidate = callMeMatch.groupValues[1]
            if (!setOf("by", "my", "name", "a", "an", "the").contains(candidate.lowercase(Locale.ROOT))) {
                val name = candidate.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                if (name.isNotBlank() && name.length >= 2) {
                    preferredName = name
                    isWaitingForName = false
                    consecutiveUnclearCount = 0
                    return LocalAiResponse(
                        paragraph1 = "Got it, $preferredName! From now on, I will call you $preferredName.",
                        paragraph2 = "",
                        confidenceScore = 100.0,
                        isGreeting = true,
                        isNameCapture = true,
                        capturedName = preferredName
                    )
                }
            }
        }

        val clean = lowerQuery.replace(Regex("[^a-zA-Z0-9\\s]"), " ").trim()

        // 3. Command: User asks AI to call them by name without providing the name yet
        val promptNameRequests = setOf(
            "call me by my name", "call me by name", "call me with my name", "call me my name", "call me",
            "enoda name solu kupdu", "enoda name solli kupdu", "ennoda name solu kupdu", "ennoda name solli koopdu",
            "en peyar solli koopdu", "en peru solli kupdu"
        )
        if (clean in promptNameRequests || clean == "call me") {
            isWaitingForName = true
            return LocalAiResponse(
                paragraph1 = "Sure! What name would you like me to call you?",
                paragraph2 = "",
                confidenceScore = 100.0,
                isGreeting = true
            )
        }

        // Tanglish: "enoda name Kishore", "ennoda name Kishore", "en per Kishore", "en peyar Kishore"
        val tanglishNameMatch = Regex("^(?:enoda|ennoda|en)\\s+(?:name|per|peyar)(?:\\s+is)?\\s+([a-zA-Z]+)", RegexOption.IGNORE_CASE).find(trimmedRaw)
        if (tanglishNameMatch != null) {
            val name = tanglishNameMatch.groupValues[1].replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            if (name.isNotBlank() && name.length >= 2) {
                preferredName = name
                isWaitingForName = false
                consecutiveUnclearCount = 0
                return LocalAiResponse(
                    paragraph1 = "Got it, $preferredName! From now on, I will call you $preferredName.",
                    paragraph2 = "",
                    confidenceScore = 100.0,
                    isGreeting = true,
                    isNameCapture = true,
                    capturedName = preferredName
                )
            }
        }

        // Tanglish: "enakku Kishore nu kupdu", "enna Kishore nu kupdu", "inimel enna Kishore nu kupdu", "inimel Kishore nu kupdu"
        val tanglishKupduMatch = Regex("(?:enakku|enna|inimel\\s+enna|inimel)?\\s*([a-zA-Z]+)\\s*(?:nu|endru)\\s*(?:kupdu|koopdu|kollu)", RegexOption.IGNORE_CASE).find(trimmedRaw)
        if (tanglishKupduMatch != null) {
            val name = tanglishKupduMatch.groupValues[1].replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            if (name.isNotBlank() && !setOf("enna", "enakku", "inimel", "name").contains(name.lowercase(Locale.ROOT))) {
                preferredName = name
                isWaitingForName = false
                consecutiveUnclearCount = 0
                return LocalAiResponse(
                    paragraph1 = "Got it, $preferredName! From now on, I will call you $preferredName.",
                    paragraph2 = "",
                    confidenceScore = 100.0,
                    isGreeting = true,
                    isNameCapture = true,
                    capturedName = preferredName
                )
            }
        }

        return null
    }

    private fun checkStylePreferenceIntent(lowerQuery: String, normalized: String): LocalAiResponse? {
        val clean = lowerQuery.replace(Regex("[^a-zA-Z0-9\\s]"), " ").trim()
        val name = preferredName.trim()
        val gotIt = if (name.isNotBlank()) "Got it, $name!" else "Got it!"

        // 0. Board Exam Format Shortcuts (2M and 5M)
        val isTwoMark = clean.contains("2 mark") || clean.contains("2 marks") || clean == "2m" || clean.contains("two mark")
        if (isTwoMark) {
            val lastChunk = getLastActiveChunk()
            val topic = lastChunk?.topic ?: "Technical Concept"
            val text = buildString {
                appendLine("* **Definition & Core Rule:** $topic states the fundamental syntax, logic, or principle essential for placement technical rounds.")
                appendLine("* **Core Syntax / Formula:** Canonical programming expression or mathematical formulation with clear parameters.")
                append("* **Placement Interview Key:** The critical edge-case or efficiency trade-off interviewers evaluate.")
            }
            return LocalAiResponse(
                paragraph1 = text,
                paragraph2 = "",
                matchedSubject = lastChunk?.subject ?: "Placement Prep",
                matchedChapter = lastChunk?.chapter ?: "Chapter",
                matchedTopic = topic,
                confidenceScore = 95.0
            )
        }

        val isFiveMark = clean.contains("5 mark") || clean.contains("5 marks") || clean == "5m" || clean.contains("five mark")
        if (isFiveMark) {
            val lastChunk = getLastActiveChunk()
            val topic = lastChunk?.topic ?: "Technical Concept"
            val text = buildString {
                appendLine("* **1. Principle & Architecture:**")
                appendLine("$topic is a foundational concept in placement technical rounds, governing computational logic and modern system design.")
                appendLine()
                appendLine("* **2. Mechanics & In-Depth Explanation:**")
                appendLine("Explains internal data structures, state transitions, and how the underlying runtime or database engine optimizes performance.")
                appendLine()
                appendLine("* **3. Syntax / Formula & Time Complexity:**")
                appendLine("Core implementation details, asymptotic complexity (Time: O(log N) or O(N), Space: O(1)), and memory efficiency considerations.")
                appendLine()
                appendLine("* **4. Real-World Applications & Industry Use:**")
                appendLine("Widely applied in production microservices, data processing pipelines, scalable cloud infrastructure, and distributed databases.")
                appendLine()
                appendLine("* **5. Placement Interview Takeaways:**")
                append("Highlight real-world examples, boundary edge cases, and why this design choice outperforms naive alternatives.")
            }
            return LocalAiResponse(
                paragraph1 = text,
                paragraph2 = "",
                matchedSubject = lastChunk?.subject ?: "Placement Prep",
                matchedChapter = lastChunk?.chapter ?: "Chapter",
                matchedTopic = topic,
                confidenceScore = 95.0
            )
        }

        // A. Emoji Preference Check
        val disableEmojiPhrases = setOf(
            "dont use emoji", "dont use emojis", "do not use emoji", "do not use emojis",
            "no emoji", "no emojis", "stop emoji", "stop emojis", "remove emoji",
            "remove emojis", "without emoji", "without emojis", "avoid emoji", "avoid emojis"
        )
        if (disableEmojiPhrases.any { clean.contains(it) }) {
            useEmojis = false
            val lastChunk = getLastActiveChunk()
            val confirmation = "$gotIt I'll keep all responses clean with * bullet points and no emojis."
            val replyText = if (lastChunk != null) {
                "$confirmation\n\nHere is **${lastChunk.topic}** in clean format:\n\n${formatChunkAccordingToPreferences(lastChunk)}"
            } else {
                confirmation
            }
            return LocalAiResponse(
                paragraph1 = replyText,
                paragraph2 = "",
                confidenceScore = 100.0,
                isPreferenceUpdate = true
            )
        }

        val enableEmojiPhrases = setOf("use emoji", "use emojis", "with emoji", "with emojis", "show emoji", "include emoji")
        if (enableEmojiPhrases.any { clean.contains(it) }) {
            useEmojis = true
            return LocalAiResponse(
                paragraph1 = "$gotIt I'll include emojis in responses.",
                paragraph2 = "",
                confidenceScore = 100.0,
                isPreferenceUpdate = true
            )
        }

        // B. Formula & Example Suppression Check
        // 1. Both Suppressed: "no need of formula and example", "no formula and example", "only explain", "just explain", "only concept"
        val suppressBothPhrases = setOf(
            "no need of formula and example", "no need of formula and no example", "no need of formula or example",
            "no formula and example", "no formula and no example", "no formula or example", "no formula no example",
            "without formula and example", "without formula or example", "without formula and without example",
            "no formulas and examples", "without formulas and examples", "no need of formulas and examples",
            "only explain", "just explain", "only definition", "only concept", "explain without formula and example",
            "dont show formula and example", "dont give formula and example"
        )
        if (suppressBothPhrases.any { clean.contains(it) } ||
            (clean.contains("no formula") && clean.contains("no example")) ||
            (clean.contains("without formula") && clean.contains("without example")) ||
            (clean.contains("only") && (clean.contains("explain") || clean.contains("concept") || clean.contains("definition")) && !clean.contains("example") && !clean.contains("formula"))) {
            includeFormulas = false
            includeExamples = false
            val lastChunk = getLastActiveChunk()
            return if (lastChunk != null) {
                val defText = if (lastChunk.definition.isNotBlank()) lastChunk.definition.trim() else lastChunk.content.substringBefore(".")
                LocalAiResponse(
                    paragraph1 = "$gotIt Here is the explanation for **${lastChunk.topic}** showing only the concept and definition:\n\n* **Concept & Definition:**\n$defText",
                    paragraph2 = "",
                    matchedSubject = lastChunk.subject,
                    matchedChapter = lastChunk.chapter,
                    matchedTopic = lastChunk.topic,
                    confidenceScore = 100.0,
                    isPreferenceUpdate = true,
                    isFollowUp = true
                )
            } else {
                LocalAiResponse(
                    paragraph1 = "$gotIt I'll provide only pure concepts and definitions without formulas or examples for upcoming topics. What shall we learn?",
                    paragraph2 = "",
                    confidenceScore = 100.0,
                    isPreferenceUpdate = true
                )
            }
        }

        // 2. Suppress Formula Only: "no formula", "no formulas", "without formula", "no need of formula"
        val suppressFormulaPhrases = setOf(
            "no need of formula", "no need of formulas", "no formula", "no formulas",
            "without formula", "without formulas", "dont show formula", "dont give formula",
            "omit formula", "skip formula"
        )
        if (suppressFormulaPhrases.any { clean.contains(it) }) {
            includeFormulas = false
            val lastChunk = getLastActiveChunk()
            return if (lastChunk != null) {
                LocalAiResponse(
                    paragraph1 = "$gotIt Here is **${lastChunk.topic}** without formulas:\n\n${formatChunkAccordingToPreferences(lastChunk)}",
                    paragraph2 = "",
                    matchedSubject = lastChunk.subject,
                    matchedChapter = lastChunk.chapter,
                    matchedTopic = lastChunk.topic,
                    confidenceScore = 100.0,
                    isPreferenceUpdate = true,
                    isFollowUp = true
                )
            } else {
                LocalAiResponse(
                    paragraph1 = "$gotIt I'll omit formulas and focus on concepts and practical examples.",
                    paragraph2 = "",
                    confidenceScore = 100.0,
                    isPreferenceUpdate = true
                )
            }
        }

        // 3. Suppress Example Only: "no example", "no examples", "without example", "no need of example"
        val suppressExamplePhrases = setOf(
            "no need of example", "no need of examples", "no example", "no examples",
            "without example", "without examples", "dont show example", "dont give example",
            "omit example", "skip example"
        )
        if (suppressExamplePhrases.any { clean.contains(it) }) {
            includeExamples = false
            val lastChunk = getLastActiveChunk()
            return if (lastChunk != null) {
                LocalAiResponse(
                    paragraph1 = "$gotIt Here is **${lastChunk.topic}** without examples:\n\n${formatChunkAccordingToPreferences(lastChunk)}",
                    paragraph2 = "",
                    matchedSubject = lastChunk.subject,
                    matchedChapter = lastChunk.chapter,
                    matchedTopic = lastChunk.topic,
                    confidenceScore = 100.0,
                    isPreferenceUpdate = true,
                    isFollowUp = true
                )
            } else {
                LocalAiResponse(
                    paragraph1 = "$gotIt I'll omit examples and focus on core definitions and formulas.",
                    paragraph2 = "",
                    confidenceScore = 100.0,
                    isPreferenceUpdate = true
                )
            }
        }

        // 4. Restore Formula or Example: "with formula", "include formula", "with example", "include example"
        if (clean.contains("include formula") || clean.contains("with formula") || clean.contains("show formula")) {
            includeFormulas = true
            return LocalAiResponse(
                paragraph1 = "$gotIt Formulas will be included in subsequent explanations.",
                paragraph2 = "",
                confidenceScore = 100.0,
                isPreferenceUpdate = true
            )
        }
        if (clean.contains("include example") || clean.contains("with example") || clean.contains("show example")) {
            includeExamples = true
            return LocalAiResponse(
                paragraph1 = "$gotIt Practical examples will be included in subsequent explanations.",
                paragraph2 = "",
                confidenceScore = 100.0,
                isPreferenceUpdate = true
            )
        }

        return null
    }

    private fun checkFarewellIntent(normalized: String): LocalAiResponse? {
        val farewells = setOf(
            "bye", "byee", "byeee", "goodbye", "good bye", "see you", "see u", "see ya",
            "tata", "take care", "kelamburen", "poyitu varen", "varaen", "varatta"
        )
        val clean = normalized.lowercase(Locale.ROOT).trim()
        val words = clean.split(Regex("\\s+"))

        val isFarewell = farewells.contains(clean) ||
                (words.size <= 3 && words.any { it in setOf("bye", "goodbye", "tata") })

        if (isFarewell) {
            val name = preferredName.trim()
            val replyText = if (name.isNotBlank()) {
                "Okay bye, $name! We'll continue tomorrow."
            } else {
                "Okay bye! We'll continue tomorrow."
            }
            return LocalAiResponse(
                paragraph1 = replyText,
                paragraph2 = "",
                confidenceScore = 100.0,
                isGreeting = true,
                isFarewell = true
            )
        }
        return null
    }

    private fun checkCasualSlangIntent(lowerQuery: String, normalized: String): LocalAiResponse? {
        val trimmed = lowerQuery.replace(Regex("[^a-zA-Z0-9\\s]"), " ").trim()
        val words = trimmed.split(Regex("\\s+")).filter { it.isNotBlank() }

        val casualSlangWords = setOf(
            "bhai", "bro", "brother", "machan", "machi", "thala", "thalaiva", "mapla",
            "nanba", "nanbaa", "dost", "dosti", "hm", "hmm", "hmmm", "da", "di", "pa",
            "ah", "ahh", "oh", "k", "kk", "okey", "lol", "lmao", "wat", "huh", "y",
            "enna", "enna da", "sollu", "sollunga", "pesu", "pesunga", "dei", "ayyo"
        )

        val isPureSlang = words.all { it in casualSlangWords } || (words.size <= 2 && words.any { it in casualSlangWords })
        if (isPureSlang) {
            return handleUnclearOrEmpty(lowerQuery)
        }

        return null
    }

    private fun handleUnclearOrEmpty(query: String): LocalAiResponse {
        consecutiveUnclearCount++
        val name = preferredName.trim()
        val isNamed = name.isNotBlank()
        return if (consecutiveUnclearCount == 1) {
            val text = if (isNamed) {
                "I didn't understand that, $name. Could you please explain clearly which subject or topic you need help with?"
            } else {
                "I didn't understand that. Could you please explain clearly which subject or topic you need help with?"
            }
            LocalAiResponse(
                paragraph1 = text,
                paragraph2 = "",
                confidenceScore = 100.0
            )
        } else {
            val text = if (isNamed) {
                "I don't know about that, $name. Please ask something related to your subjects, study notes, or quizzes."
            } else {
                "I don't know about that. Please ask something related to your subjects, study notes, or quizzes."
            }
            LocalAiResponse(
                paragraph1 = text,
                paragraph2 = "",
                confidenceScore = 100.0
            )
        }
    }

    private data class SubjectOverviewInfo(
        val name: String,
        val whatItIs: String,
        val keyUses: String,
        val advantages: String,
        val challenges: String
    )

    private fun checkSubjectOverviewIntent(lowerQuery: String, normalized: String): LocalAiResponse? {
        if (hasExplicitContextPronoun(lowerQuery) || (contextHistory.isNotEmpty() && isFollowUpQuery(lowerQuery))) {
            return null
        }

        val clean = lowerQuery.replace(Regex("[^a-zA-Z0-9\\s]"), " ").trim()
        val norm = normalized.trim().lowercase(Locale.ROOT)

        val overviewRegex = Regex("^(?:what\\s+about|what\\s+abou|about|tell\\s+me\\s+about|uses\\s+of|use\\s+of|advantages\\s+of|advantage\\s+of|benefits\\s+of|overview\\s+of|explain\\s+about|scope\\s+of|details\\s+of)\\s+([a-zA-Z\\s]+)$")
        val match = overviewRegex.find(clean)

        var candidateSubject = match?.groupValues?.getOrNull(1)?.trim()?.lowercase(Locale.ROOT)

        if (candidateSubject.isNullOrBlank()) {
            val standaloneSubjects = setOf(
                "python", "coding", "programming", "python & coding logic", "coding logic", "python logic",
                "sql", "database", "databases", "dbms", "rdbms", "sql & database management", "database management",
                "ai", "artificial intelligence", "ml", "machine learning", "deep learning", "modern tech", "tech", "llm", "rag", "ai & modern tech concepts",
                "aptitude", "quant", "quantitative aptitude", "logical reasoning", "reasoning", "interview", "interview prep", "puzzles", "placement", "aptitude & interview prep"
            )
            if (norm in standaloneSubjects) {
                candidateSubject = norm
            }
        }

        if (candidateSubject.isNullOrBlank()) return null

        val candWords = candidateSubject.split(Regex("\\s+"))
        if (candWords.any { it in setOf("it", "its", "this", "that", "these", "those", "roots", "solve", "formula") }) {
            return null
        }

        val candClean = candidateSubject.removePrefix("the ").removePrefix("a ").removePrefix("an ").trim()
        val isKnownSub = isKnownSubject(candClean) || isKnownSubject(candidateSubject)

        if (!isKnownSub) {
            // If candidate is a specific topic matching our RAG chunks, defer to RAG
            val bestRagScore = synchronized(chunks) {
                chunks.maxOfOrNull { computeRelevanceScore(lowerQuery, tokenize(lowerQuery), it) } ?: 0.0
            }
            if (bestRagScore >= 4.0) {
                return null
            }
        }

        val info = resolveSubjectOverview(candClean) ?: resolveSubjectOverview(candidateSubject) ?: return null

        val responseText = buildString {
            appendLine("* **What it is:**")
            appendLine(info.whatItIs)
            appendLine()
            appendLine("* **Key Uses & Real-World Work:**")
            appendLine(info.keyUses)
            appendLine()
            appendLine("* **Advantages:**")
            appendLine(info.advantages)
            appendLine()
            appendLine("* **Disadvantages / Challenges:**")
            append(info.challenges)
        }

        return LocalAiResponse(
            paragraph1 = responseText,
            paragraph2 = "",
            matchedSubject = info.name,
            matchedChapter = "Overview",
            matchedTopic = "${info.name} Overview",
            confidenceScore = 98.0
        )
    }

    private fun isKnownSubject(candidate: String): Boolean {
        val known = setOf(
            "python", "coding", "programming", "python & coding logic", "coding logic", "python logic",
            "sql", "database", "databases", "dbms", "rdbms", "sql & database management", "database management",
            "ai", "artificial intelligence", "ml", "machine learning", "deep learning", "modern tech", "tech", "llm", "rag", "ai & modern tech concepts",
            "aptitude", "quant", "quantitative aptitude", "logical reasoning", "reasoning", "interview", "interview prep", "puzzles", "placement", "aptitude & interview prep"
        )
        return candidate.trim().lowercase(Locale.ROOT) in known
    }

    private fun resolveSubjectOverview(candidate: String): SubjectOverviewInfo? {
        val key = candidate.trim().lowercase(Locale.ROOT)
        val overviewMap = mapOf(
            "python & coding logic" to SubjectOverviewInfo(
                "Python & Coding Logic",
                "Python & Coding Logic is the foundational software engineering discipline covering memory architecture, core OOP principles, functional paradigms, and algorithmic problem-solving techniques.",
                "Used in backend web development (Django/FastAPI), machine learning model building, data pipeline automation, enterprise scripting, and technical coding rounds for top tech companies.",
                "High developer velocity, readable syntax, massive standard and third-party library ecosystem, versatile applicability from microservices to AI, and high recruitment demand.",
                "Dynamic typing runtime overhead, GIL (Global Interpreter Lock) concurrency limitations, and memory consumption compared to compiled languages like C++ or Rust."
            ),
            "sql & database management" to SubjectOverviewInfo(
                "SQL & Database Management",
                "SQL & Database Management focuses on relational database modeling, relational algebra, ACID transaction safety, B-Tree index optimization, and querying complex structured datasets.",
                "Powering core enterprise data storage, financial transactions, real-time analytics, user authentication backends, and data warehousing for high-concurrency systems.",
                "Strict data integrity and consistency through ACID compliance, standardized declarative query language across all relational engines (PostgreSQL, MySQL), and predictable query execution plans.",
                "Horizontal scaling complexity (sharding/replication lag) compared to NoSQL, rigid schema migrations on multi-terabyte tables, and database deadlocks under extreme contention."
            ),
            "ai & modern tech concepts" to SubjectOverviewInfo(
                "AI & Modern Tech Concepts",
                "AI & Modern Tech Concepts encompasses the architecture and deployment of machine learning models, neural networks, Large Language Models (LLMs), Retrieval-Augmented Generation (RAG), cloud architectures, and cybersecurity.",
                "Generative AI assistants, autonomous robotics, semantic search engines, enterprise cloud infrastructure, threat intelligence, and predictive decision-support systems.",
                "Automates complex human-cognitive tasks, processes unstructured multimodal data at scale, and unlocks unprecedented software capabilities.",
                "High GPU compute and inference latency costs, model hallucination and alignment risks, data privacy regulations, and complex MLOps deployment pipelines.",
            ),
            "aptitude & interview prep" to SubjectOverviewInfo(
                "Aptitude & Interview Prep",
                "Aptitude & Interview Prep trains quantitative problem-solving speed, logical reasoning, data interpretation, pattern recognition, and professional behavioral interview communication.",
                "Clearing first-round campus placement screening tests, competitive tech assessments, managerial case studies, and engineering HR/technical interviews.",
                "Drastically sharpens mental math speed, structured deduction under strict time limits, cognitive clarity, and articulation during high-pressure placement drives.",
                "Strict time constraints per problem (under 60 seconds), trick questions requiring pattern shortcuts, and fatigue during multi-hour recruitment examinations."
            )
        )

        val aliasMap = mapOf(
            "python" to "python & coding logic", "python3" to "python & coding logic", "py" to "python & coding logic",
            "coding" to "python & coding logic", "programming" to "python & coding logic", "logic" to "python & coding logic",
            "sql" to "sql & database management", "database" to "sql & database management", "databases" to "sql & database management",
            "dbms" to "sql & database management", "rdbms" to "sql & database management",
            "ai" to "ai & modern tech concepts", "artificial intelligence" to "ai & modern tech concepts",
            "machine learning" to "ai & modern tech concepts", "ml" to "ai & modern tech concepts",
            "deep learning" to "ai & modern tech concepts", "modern tech" to "ai & modern tech concepts",
            "tech" to "ai & modern tech concepts", "llm" to "ai & modern tech concepts", "rag" to "ai & modern tech concepts",
            "aptitude" to "aptitude & interview prep", "quant" to "aptitude & interview prep",
            "quantitative aptitude" to "aptitude & interview prep", "logical reasoning" to "aptitude & interview prep",
            "reasoning" to "aptitude & interview prep", "interview" to "aptitude & interview prep",
            "interview prep" to "aptitude & interview prep", "placement" to "aptitude & interview prep",
            "placement prep" to "aptitude & interview prep", "puzzles" to "aptitude & interview prep"
        )

        val resolvedKey = aliasMap[key] ?: key
        if (overviewMap.containsKey(resolvedKey)) {
            return overviewMap[resolvedKey]
        }

        val queryWords = key.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (queryWords.size == 1) {
            for ((k, v) in overviewMap) {
                if (key == k || k.contains(key)) {
                    return v
                }
            }
        }

        val formattedName = candidate.split(" ").filter { it.isNotBlank() }
            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }

        return SubjectOverviewInfo(
            name = formattedName,
            whatItIs = "$formattedName is an academic discipline focused on exploring the foundational principles, analytical methodologies, and structured theories that govern its field of study.",
            keyUses = "Practitioners and scholars apply $formattedName across modern research, industrial problem-solving, high-level analysis, technology integration, and empirical investigation.",
            advantages = "Develops rigorous critical reasoning, analytical problem-solving skills, and deep subject-matter expertise with diverse professional applications.",
            challenges = "Requires substantial dedication, mastery of prerequisite theory, and sustained problem-solving practice to navigate complex abstract concepts."
        )
    }

    private fun checkDirectQuestionIntent(
        rawQuery: String,
        lowerQuery: String,
        normalized: String
    ): LocalAiResponse? {
        val clean = lowerQuery.replace(Regex("[^a-zA-Z0-9+\\-=><\\s]"), " ").trim()
        val norm = normalized.trim().lowercase(Locale.ROOT)

        // 1. Python Snake / Naming Origin
        val isSnakeQuestion = (clean.contains("python") && (clean.contains("snake") || clean.contains("reptile") || clean.contains("animal") || clean.contains("means snake"))) ||
                Regex("(?:is\\s+)?python\\s+(?:means?|named\\s+after|a|the)?\\s*snake").containsMatchIn(clean) ||
                Regex("why\\s+(?:is\\s+it\\s+called|named)\\s+python").containsMatchIn(clean) ||
                clean == "python means snake" || clean == "why python" || clean.contains("python name origin")

        if (isSnakeQuestion) {
            val p1 = "* **No, Python is not named after the snake!**\n\nIn computer science, **Python** is a popular high-level programming language—not the reptile. Guido van Rossum created it in 1991 and named it after the famous BBC comedy series **'Monty Python\\'s Flying Circus'**, because he wanted a short, unique, and slightly mysterious name."
            val p2 = "* **Core Placement Concept:**\nPython is an interpreted, dynamically typed language emphasizing code readability and developer velocity.\n\n* **Key Placement Topics to Explore:**\n1. **Memory Model & Mutability** (`id()`, object references, mutable vs immutable)\n2. **List vs Tuple** (memory layout & performance)\n3. **Global Interpreter Lock (GIL)** (threading vs multiprocessing)\n4. **Generators & Iterators** (lazy evaluation with `yield`)"
            val sub = "Python & Coding Logic"
            val ch = "Python Fundamentals"
            val top = "Python Language Origin & Overview"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 2. Creator of Python
        val isPythonCreator = (clean.contains("who") && (clean.contains("created") || clean.contains("invented") || clean.contains("made") || clean.contains("developed") || clean.contains("designed") || clean.contains("father")) && clean.contains("python")) ||
                clean.contains("father of python") || clean == "who created python" || clean == "who invented python"
        if (isPythonCreator) {
            val p1 = "* **Creator of Python:**\n**Guido van Rossum**, a Dutch computer programmer, created Python in 1989 and officially released it in **February 1991** at Centrum Wiskunde & Informatica (CWI) in the Netherlands. For decades he was recognized as Python's 'Benevolent Dictator For Life' (BDFL)."
            val p2 = "* **Design Philosophy:**\nGuido designed Python around \"The Zen of Python\" (PEP 20): *\"Simple is better than complex. Readability counts.\"* It has become the dominant language for Data Science, AI/ML, and rapid backend service development."
            val sub = "Python & Coding Logic"
            val ch = "Python Fundamentals"
            val top = "Python History & Guido van Rossum"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 3. Creator of SQL
        val isSqlCreator = (clean.contains("who") && (clean.contains("created") || clean.contains("invented") || clean.contains("made") || clean.contains("developed") || clean.contains("designed") || clean.contains("father")) && clean.contains("sql")) ||
                clean.contains("father of sql") || clean == "who created sql" || clean == "who invented sql"
        if (isSqlCreator) {
            val p1 = "* **Creators of SQL:**\n**Donald D. Chamberlin** and **Raymond F. Boyce** developed SQL at IBM's San Jose Research Laboratory in the **early 1970s**. It was originally named **SEQUEL** (*Structured English QUEry Language*) designed to manipulate and retrieve data stored in IBM's original System R relational database."
            val p2 = "* **Placement Importance:**\nStandardized by ANSI in 1986 and ISO in 1987, SQL remains the universal standard declarative language across all major RDBMS engines (PostgreSQL, MySQL, Oracle, Microsoft SQL Server)."
            val sub = "SQL & Database Management"
            val ch = "SQL Fundamentals"
            val top = "SQL History & IBM Origins"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 4. Full Form of SQL
        val isSqlFullForm = (clean.contains("sql") && (clean.contains("full form") || clean.contains("stand for") || clean.contains("meaning of sql") || clean.contains("what does sql stand for") || clean.contains("abbreviation"))) ||
                clean == "sql full form" || clean == "full form of sql"
        if (isSqlFullForm) {
            val p1 = "* **Full Form of SQL:**\n**Structured Query Language** (pronounced either as individual letters *S-Q-L* or as *Sequel*).\n\nOriginally named **SEQUEL** (*Structured English QUEry Language*) at IBM, the name was shortened to **SQL** due to trademark issues."
            val p2 = "* **Core Command Subsets:**\n• **DDL** (Data Definition): `CREATE`, `ALTER`, `DROP`, `TRUNCATE`\n• **DML** (Data Manipulation): `SELECT`, `INSERT`, `UPDATE`, `DELETE`\n• **DCL** (Data Control): `GRANT`, `REVOKE`\n• **TCL** (Transaction Control): `COMMIT`, `ROLLBACK`, `SAVEPOINT`"
            val sub = "SQL & Database Management"
            val ch = "SQL Fundamentals"
            val top = "SQL Full Form & Command Categories"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 5. Full Form of LLM
        val isLlmFullForm = (clean.contains("llm") && (clean.contains("full form") || clean.contains("stand for") || clean.contains("meaning of llm") || clean.contains("abbreviation"))) ||
                clean == "llm full form" || clean == "full form of llm" || clean == "what does llm stand for"
        if (isLlmFullForm) {
            val p1 = "* **Full Form of LLM:**\n**Large Language Model**.\n\nLLMs are foundational deep learning systems trained on hundreds of billions of text and code tokens using Transformer neural networks (multi-head self-attention mechanisms) to understand, generate, and reason across natural language."
            val p2 = "* **Core Production Workflow:**\n• **Pre-training:** Self-supervised next-token prediction across trillions of tokens.\n• **Fine-Tuning (SFT):** Instruction tuning on curated prompt-response datasets.\n• **Alignment (RLHF / DPO):** Optimizing model safety and adherence to human preferences.\n• **RAG (Retrieval-Augmented Generation):** Connecting LLMs to dynamic external vector databases."
            val sub = "AI & Modern Tech Concepts"
            val ch = "Generative AI & LLMs"
            val top = "LLM (Large Language Model) Overview"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 6. What is SQL / SQL Overview
        val isWhatIsSql = norm in setOf("what is sql", "what is sql language", "what is sql database", "explain sql", "what sql", "tell me about sql", "sql overview")
        if (isWhatIsSql) {
            val p1 = "* **What is SQL?**\n**Structured Query Language (SQL)** is the standard declarative programming language used to manage, query, and manipulate data in Relational Database Management Systems (RDBMS).\n\nUnlike procedural languages, SQL is **declarative**: you specify *what* records you require (`SELECT ... FROM ... WHERE ...`), and the database query engine optimizes the underlying physical retrieval execution plan."
            val p2 = "* **Key Placement Topics in SQL:**\n• **Joins:** `INNER JOIN`, `LEFT JOIN`, `FULL OUTER JOIN`, `CROSS JOIN`\n• **Aggregation & Filtering:** `GROUP BY`, `HAVING`, `WHERE` execution order\n• **Indexing & Performance:** B-Tree indexes, clustered vs non-clustered, query cost\n• **ACID Transactions:** Atomicity, Consistency, Isolation, Durability\n• **Normalization:** 1NF, 2NF, 3NF, BCNF"
            val sub = "SQL & Database Management"
            val ch = "SQL Fundamentals"
            val top = "SQL Architecture & Core Concepts"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 7. What is Python / Python Overview
        val isWhatIsPython = norm in setOf("what is python", "what is python language", "what is python programming", "explain python", "what python", "tell me about python", "python overview")
        if (isWhatIsPython) {
            val p1 = "* **What is Python?**\n**Python** is a high-level, general-purpose, interpreted programming language designed with an emphasis on code readability and developer velocity.\n\nIt natively supports multiple programming paradigms including Object-Oriented, Functional, and Procedural styles. It powers modern industry workflows across Data Science, Machine Learning, Backend Web Services (FastAPI, Django), and Cloud Automation."
            val p2 = "* **Key Placement Topics in Python:**\n• **Memory Model & Mutability:** Immutable types (`int`, `str`, `tuple`) vs mutable types (`list`, `dict`, `set`)\n• **Concurrency & GIL:** Global Interpreter Lock implications on multithreading vs multiprocessing\n• **Advanced Idioms:** List/Dict comprehensions, generators (`yield`), decorators (`@`)\n• **Data Structures & Complexity:** Hash map lookup O(1), Timsort algorithm O(N log N)"
            val sub = "Python & Coding Logic"
            val ch = "Python Fundamentals"
            val top = "Python Language Overview & Core Concepts"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 8. What is AI / Artificial Intelligence
        val isWhatIsAi = norm in setOf("what is ai", "what is artificial intelligence", "explain ai", "tell me about ai", "define ai", "what is ai in computer science")
        if (isWhatIsAi) {
            val p1 = "* **What is Artificial Intelligence (AI)?**\n**Artificial Intelligence** is the branch of computer science dedicated to building computational systems capable of performing tasks that historically required human intelligence—such as pattern recognition, visual perception, natural language understanding, and complex decision-making."
            val p2 = "* **The AI Hierarchy (Placement Framework):**\n• **Artificial Intelligence (AI):** The overarching field of intelligent machines and algorithms.\n• **Machine Learning (ML):** A subset of AI where systems learn statistical patterns from data.\n• **Deep Learning (DL):** A subset of ML utilizing multi-layered artificial neural networks.\n• **Generative AI & LLMs:** Modern deep models synthesizing new text, code, or images."
            val sub = "AI & Modern Tech Concepts"
            val ch = "Artificial Intelligence Fundamentals"
            val top = "AI Hierarchy & Overview"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 9. What is LLM
        val isWhatIsLlm = norm in setOf("what is llm", "what is a large language model", "what is large language model", "explain llm", "tell me about llm")
        if (isWhatIsLlm) {
            val p1 = "* **What is a Large Language Model (LLM)?**\nA **Large Language Model** is an advanced artificial intelligence system built upon deep neural network architectures—specifically the **Transformer** self-attention mechanism—and trained on hundreds of billions of words.\n\nLLMs excel at natural language understanding, text synthesis, contextual reasoning, code generation, and conversational problem-solving."
            val p2 = "* **Key LLM Placement Concepts:**\n• **Self-Attention Mechanism:** Computes contextual relevance between every pair of tokens in a sequence (Q, K, V).\n• **Context Window:** The token buffer length the model can process simultaneously (e.g. 8k to 1M tokens).\n• **Hallucination Mitigation:** Using **RAG** (Retrieval-Augmented Generation) to ground generation with verified database records."
            val sub = "AI & Modern Tech Concepts"
            val ch = "Generative AI & LLMs"
            val top = "LLM (Large Language Model) Concepts"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 10. What is Database / DBMS / RDBMS
        val isWhatIsDbms = norm in setOf("what is database", "what is dbms", "what is rdbms", "difference between dbms and rdbms", "what is a database", "what is a dbms", "what is a rdbms") ||
                clean == "what is database" || clean == "what is dbms" || clean == "what is rdbms"
        if (isWhatIsDbms) {
            val p1 = "* **Database, DBMS, and RDBMS Definitions:**\n\n• **Database:** An organized, persistent electronic collection of structured information or data.\n• **DBMS (Database Management System):** Software that interacts with end users, applications, and the database itself to capture, query, and manage data (e.g., file systems, XML databases).\n• **RDBMS (Relational DBMS):** An advanced DBMS based on Edgar Codd's relational model where data is structured into tables (relations) with rows (tuples) and columns (attributes), enforcing integrity constraints and ACID transactions (e.g., PostgreSQL, MySQL, Oracle)."
            val p2 = "* **Core Placement Pillars of RDBMS:**\n• **Primary Key & Foreign Key:** Enforce entity and referential integrity across related tables.\n• **ACID Compliance:** Ensures enterprise transactions remain atomic, consistent, isolated, and durable.\n• **Indexing (B-Tree):** Reduces query search time from O(N) full table scan to O(log N) index seek."
            val sub = "SQL & Database Management"
            val ch = "Database Concepts"
            val top = "Database & RDBMS Fundamentals"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 11. "VS" / Comparison Questions
        val isComparison = clean.contains("vs") || clean.contains("versus") || clean.contains("difference between") || clean.contains("compare") || clean.contains("and")

        // 11a. List vs Tuple
        if (clean.contains("list") && clean.contains("tuple") && (isComparison || clean.contains("difference"))) {
            val p1 = "* **List vs. Tuple Comparison (Python):**\n\n• **Mutability:** Lists are **mutable** (elements can be added, removed, or modified in place); Tuples are **immutable** (once instantiated, elements cannot be modified).\n• **Syntax:** Lists use square brackets `[1, 2, 3]`; Tuples use parentheses `(1, 2, 3)`.\n• **Memory & Speed:** Tuples allocate a single fixed memory block and are faster to construct and iterate over; Lists allocate extra capacity to support dynamic amortized O(1) append operations.\n• **Dictionary Keys:** Tuples can serve as dictionary keys or set elements (if all elements are hashable); Lists can **never** be dictionary keys because mutable types are unhashable."
            val p2 = "* **Placement Interview Tip:**\nUse a tuple for fixed records or heterogeneous data (e.g. `(latitude, longitude)` or database rows). Use a list for collections of homogeneous items that grow or shrink dynamically."
            val sub = "Python & Coding Logic"
            val ch = "Python Data Structures"
            val top = "List vs Tuple"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 11b. SQL vs NoSQL
        if (clean.contains("sql") && clean.contains("nosql")) {
            val p1 = "* **SQL vs. NoSQL Comparison:**\n\n• **Data Model:** SQL databases are **relational** (predefined schemas, tables, rows, columns); NoSQL databases are **non-relational** (Document, Key-Value, Wide-Column, or Graph stores with dynamic JSON-like schemas).\n• **Transactions:** SQL guarantees strict **ACID** properties; NoSQL embraces **BASE** properties (Basically Available, Soft-state, Eventual consistency).\n• **Scaling:** SQL scales primarily **vertically** (increasing CPU, RAM, SSD on a server); NoSQL scales **horizontally** (sharding and distributing data partitions across commodity nodes).\n• **Examples:** SQL: PostgreSQL, MySQL, Oracle; NoSQL: MongoDB, Redis, Apache Cassandra, DynamoDB."
            val p2 = "* **Placement System Design Tip:**\nChoose SQL for relational integrity, financial transactions, and complex analytical JOINs. Choose NoSQL for high-throughput write-heavy logs, caching, rapidly evolving schemas, or massive horizontal scale."
            val sub = "SQL & Database Management"
            val ch = "Database Architecture"
            val top = "SQL vs NoSQL"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 11c. DELETE vs TRUNCATE
        if (clean.contains("delete") && clean.contains("truncate")) {
            val p1 = "* **DELETE vs. TRUNCATE Comparison (SQL):**\n\n• **Command Type:** `DELETE` is a **DML** (Data Manipulation Language) command; `TRUNCATE` is a **DDL** (Data Definition Language) command.\n• **WHERE Clause:** `DELETE` supports a `WHERE` clause to filter specific rows; `TRUNCATE` removes **all rows** unconditionally.\n• **Transaction Logging & Speed:** `DELETE` scans and logs each deleted row individually in the transaction log (slow for large tables); `TRUNCATE` deallocates data pages directly and logs only page deallocations (extremely fast).\n• **Rollback:** `DELETE` operations can be rolled back in an active transaction; `TRUNCATE` commits immediately in some engines (though PostgreSQL and SQL Server support rolling back TRUNCATE within an uncommitted transaction).\n• **Identity Counter:** `TRUNCATE` resets auto-increment / identity seed values back to initial state; `DELETE` retains the current counter."
            val p2 = "* **Placement Interview Takeaway:**\nNever run `DELETE` without a `WHERE` clause in production. Use `TRUNCATE TABLE` when emptying staging or temporary tables."
            val sub = "SQL & Database Management"
            val ch = "SQL DDL & DML Commands"
            val top = "DELETE vs TRUNCATE"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 11d. Primary Key vs Foreign Key
        if (clean.contains("primary key") && clean.contains("foreign key")) {
            val p1 = "* **Primary Key vs. Foreign Key Comparison:**\n\n• **Purpose:** A **Primary Key (PK)** uniquely identifies each row within its own table; a **Foreign Key (FK)** references a Primary Key or Unique Key in another table to establish a relationship.\n• **Uniqueness & Nullability:** Primary Keys must be strictly **UNIQUE and NOT NULL**; Foreign Keys can contain duplicate values and can be `NULL` (unless defined `NOT NULL`).\n• **Quantity:** A table can have only **one** Primary Key (which may be composite); a table can have **multiple** Foreign Keys pointing to different tables.\n• **Clustered Index:** Primary Keys automatically create a clustered index by default; Foreign Keys do not create indexes automatically (they should be indexed manually for JOIN performance)."
            val p2 = "* **Placement Interview Takeaway:**\nForeign Keys enforce **Referential Integrity**. Cascading rules like `ON DELETE CASCADE` or `ON DELETE SET NULL` prevent orphaned child rows when a parent record is deleted."
            val sub = "SQL & Database Management"
            val ch = "Database Constraints & Keys"
            val top = "Primary Key vs Foreign Key"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 11e. AI vs ML
        if ((clean.contains("ai") && clean.contains("ml") && (isComparison || clean.contains("difference"))) ||
                (clean.contains("artificial intelligence") && clean.contains("machine learning"))) {
            val p1 = "* **AI vs. Machine Learning (ML) Comparison:**\n\n• **Scope:** **Artificial Intelligence** is the overarching discipline of building intelligent systems capable of human-like decision making; **Machine Learning** is a specific branch/subset of AI.\n• **Methodology:** AI encompasses symbolic AI, expert rule systems, and search trees (A* algorithm); ML focuses on statistical algorithms that learn patterns directly from training data without being explicitly programmed.\n• **Goal:** AI seeks general intelligent behavior; ML seeks accurate predictive models on specific tasks through data optimization."
            val p2 = "* **Placement Takeaway:**\nAll Machine Learning is Artificial Intelligence, but not all Artificial Intelligence is Machine Learning (e.g. minimax chess algorithms are AI without ML)."
            val sub = "AI & Modern Tech Concepts"
            val ch = "Artificial Intelligence Fundamentals"
            val top = "AI vs ML"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 11f. Deep Learning vs Machine Learning
        if (clean.contains("deep learning") && clean.contains("machine learning")) {
            val p1 = "* **Deep Learning (DL) vs. Traditional Machine Learning (ML):**\n\n• **Feature Engineering:** Traditional ML requires manual feature extraction (PCA, TF-IDF, domain feature engineering); DL automatically learns hierarchical representations directly from raw data (pixels, text tokens, audio signals).\n• **Architecture:** Traditional ML relies on models like Random Forest, SVM, and XGBoost; DL utilizes multi-layer artificial neural networks (CNNs, RNNs, Transformers).\n• **Hardware & Data Requirements:** Traditional ML works well on small-to-medium tabular datasets on standard CPUs; DL requires massive datasets and GPU/TPU matrix accelerators."
            val p2 = "* **Placement Takeaway:**\nFor structured enterprise tabular data, Gradient Boosted Decision Trees (XGBoost/LightGBM) frequently outperform Deep Learning with lower compute costs and higher explainability."
            val sub = "AI & Modern Tech Concepts"
            val ch = "Neural Networks & Deep Learning"
            val top = "Deep Learning vs Machine Learning"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 11g. INNER JOIN vs LEFT JOIN
        if (clean.contains("inner join") && clean.contains("left join")) {
            val p1 = "* **INNER JOIN vs. LEFT JOIN Comparison:**\n\n• **Row Matching:** `INNER JOIN` returns **only rows that have matching values in both tables**; `LEFT JOIN` (or `LEFT OUTER JOIN`) returns **all rows from the left table**, plus matching records from the right table.\n• **Unmatched Left Rows:** In `LEFT JOIN`, if a left row has no match in the right table, all columns from the right table are populated with `NULL`; `INNER JOIN` drops unmatched rows completely.\n• **Result Cardinality:** An `INNER JOIN` row count is ≤ left table row count (assuming unique join keys); a `LEFT JOIN` row count is always ≥ left table row count."
            val p2 = "* **Placement Interview Trick:**\nTo find records in Table A that have no corresponding record in Table B, use a `LEFT JOIN` with a NULL filter:\n```sql\nSELECT a.*\nFROM table_a a\nLEFT JOIN table_b b ON a.id = b.a_id\nWHERE b.a_id IS NULL;\n```"
            val sub = "SQL & Database Management"
            val ch = "SQL Joins & Relational Algebra"
            val top = "SQL Joins (INNER vs LEFT)"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 11h. WHERE vs HAVING
        if (clean.contains("where") && clean.contains("having") && (isComparison || clean.contains("difference"))) {
            val p1 = "* **WHERE vs. HAVING Comparison (SQL):**\n\n• **Evaluation Stage:** `WHERE` filters individual rows **before** any aggregation or `GROUP BY` operation; `HAVING` filters aggregated groups **after** `GROUP BY` calculation.\n• **Aggregate Functions:** `WHERE` **cannot** evaluate aggregate functions (`WHERE AVG(salary) > 50000` is illegal); `HAVING` is specifically designed to filter on aggregate functions (`HAVING AVG(salary) > 50000`).\n• **Indexing:** `WHERE` clauses can utilize B-Tree indexes for fast row seeks; `HAVING` filters aggregated intermediate result partitions in memory."
            val p2 = "* **SQL Query Execution Pipeline (Crucial for Interviews):**\n1. `FROM` & `JOIN`\n2. `WHERE` (row filtering)\n3. `GROUP BY` (grouping)\n4. `HAVING` (group filtering)\n5. `SELECT` (projection)\n6. `DISTINCT`\n7. `ORDER BY` (sorting)\n8. `LIMIT` / `OFFSET`"
            val sub = "SQL & Database Management"
            val ch = "SQL Filtering & Grouping"
            val top = "WHERE vs HAVING"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 11i. is vs == in Python
        if ((lowerQuery.contains("is") && lowerQuery.contains("==")) || clean == "is vs" || clean.contains("is vs ==")) {
            val p1 = "* **`is` vs. `==` Comparison (Python):**\n\n• **Equality (`==`):** Tests for **value equality** — checks whether two objects have equivalent content by invoking the `__eq__()` method.\n• **Identity (`is`):** Compares **memory identity** — checks whether two variables reference the exact same object in heap memory (`id(a) == id(b)`).\n• **Example:**\n```python\na = [1, 2, 3]\nb = [1, 2, 3]\nprint(a == b)  # True (same values)\nprint(a is b)  # False (different memory addresses)\n```"
            val p2 = "* **Placement Interview Trap (Integer Interning & None):**\n• Always compare singletons with `is`: `if x is None:`, never `if x == None:`.\n• Python pre-allocates and interns small integers between -5 and 256. Therefore `x = 250; y = 250; x is y` evaluates to `True`, but integers outside that range allocate separate heap objects."
            val sub = "Python & Coding Logic"
            val ch = "Python Memory & Internals"
            val top = "Object Identity vs Value Equality (`is` vs `==`)"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 11j. Process vs Thread
        if (clean.contains("process") && clean.contains("thread") && (isComparison || clean.contains("difference"))) {
            val p1 = "* **Process vs. Thread Comparison:**\n\n• **Address Space:** A **Process** possesses an independent, isolated virtual memory space; **Threads** within the same process share the same code, data, and heap segments.\n• **Creation & Switching:** Creating and context switching processes involves heavy OS overhead (page table and TLB cache invalidation); Threads are lightweight and switch significantly faster.\n• **Communication:** Processes require Inter-Process Communication (IPC like sockets, pipes, shared memory); Threads communicate directly via shared memory (requiring synchronization primitives like mutexes to prevent race conditions).\n• **Crash Impact:** If one process crashes, other processes remain unaffected; if one thread encounters a fatal exception (like a segmentation fault), the entire parent process can crash."
            val p2 = "* **Placement Takeaway:**\nUse multi-threading for I/O-bound or shared-memory tasks. Use multi-processing for CPU-bound tasks in languages with a Global Interpreter Lock (GIL) or where memory isolation is paramount."
            val sub = "OS & Computer Networks"
            val ch = "Processes vs Threads, Concurrency & Deadlocks"
            val top = "Process vs Thread Comparison"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 11k. TCP vs UDP
        if (clean.contains("tcp") && clean.contains("udp") && (isComparison || clean.contains("difference"))) {
            val p1 = "* **TCP vs. UDP Comparison:**\n\n• **Connection & Reliability:** **TCP** is connection-oriented and guarantees reliable, ordered byte-stream delivery via sequence numbers and ACKs; **UDP** is connectionless, unordered, and provides best-effort datagram delivery with zero delivery guarantees.\n• **Handshake:** TCP establishes connections using a 3-way handshake (SYN, SYN-ACK, ACK); UDP sends datagrams immediately without any connection setup.\n• **Header Overhead:** TCP has a minimum 20-byte header; UDP has a lightweight 8-byte header.\n• **Flow & Congestion Control:** TCP implements sliding window flow control and congestion avoidance (Slow Start, Fast Retransmit); UDP has zero congestion or flow control."
            val p2 = "* **Placement Use Cases:**\n• **TCP:** Web browsing (HTTP/HTTPS), file transfer (FTP), secure shell (SSH), email (SMTP).\n• **UDP:** Real-time video/audio streaming, online multiplayer gaming, DNS queries, VoIP."
            val sub = "OS & Computer Networks"
            val ch = "TCP vs UDP, Three-Way Handshake & Flow Control"
            val top = "TCP vs UDP Comparison"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 11l. Stack vs Queue
        if (clean.contains("stack") && clean.contains("queue") && (isComparison || clean.contains("difference"))) {
            val p1 = "* **Stack vs. Queue Comparison:**\n\n• **Principle:** **Stack** operates on **LIFO** (Last In, First Out) — the newest element inserted is removed first; **Queue** operates on **FIFO** (First In, First Out) — the oldest element inserted is removed first.\n• **Primary Operations:** Stack uses `push()`, `pop()`, and `peek()`; Queue uses `enqueue()`, `dequeue()`, and `peek()`.\n• **Applications:** Stack is used in function call stacks, recursion, backtracking, undo/redo, and syntax parsing; Queue is used in BFS graph traversal, printer spooling, OS task scheduling, and message brokers."
            val p2 = "* **Complexity Note:**\nBoth Stacks and Queues achieve O(1) constant time complexity for insertions and deletions when backed by dynamic arrays or doubly linked lists."
            val sub = "Data Structures & Algorithms"
            val ch = "Stacks, Queues & Monotonic Stack"
            val top = "Stack vs Queue Comparison"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 11m. Array vs Linked List
        if ((clean.contains("array") || clean.contains("arrays")) && clean.contains("linked list") && (isComparison || clean.contains("difference"))) {
            val p1 = "* **Array vs. Linked List Comparison:**\n\n• **Memory Layout:** Arrays store elements in **contiguous** memory blocks; Linked Lists store elements in non-contiguous nodes linked together via memory pointers.\n• **Random Access:** Arrays provide O(1) constant time random access via index `arr[i]`; Linked Lists require O(N) sequential pointer traversal.\n• **Insertion & Deletion:** Inserting or deleting at the beginning of an array is O(N) due to shifting elements; Linked Lists insert or delete at head in O(1) pointer update.\n• **Cache Locality:** Arrays exhibit superior CPU cache locality and spatial prefetching; Linked Lists suffer from cache misses and pointer memory overhead."
            val p2 = "* **Placement Takeaway:**\nChoose arrays for read-heavy or indexed operations. Choose linked lists when dynamic size allocation and frequent head/tail insertion without memory reallocation are required."
            val sub = "Data Structures & Algorithms"
            val ch = "Arrays, Two Pointers & Sliding Window"
            val top = "Array vs Linked List Comparison"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 11n. Overloading vs Overriding
        if (clean.contains("overloading") && clean.contains("overriding") && (isComparison || clean.contains("difference"))) {
            val p1 = "* **Method Overloading vs. Method Overriding (Java/OOP):**\n\n• **Binding Time:** **Overloading** is resolved at **compile-time** (Static Binding); **Overriding** is resolved at **runtime** (Dynamic Method Dispatch).\n• **Class Context:** Overloading occurs within the same class; Overriding requires an inheritance relationship between parent and child classes.\n• **Method Signature:** Overloading requires different parameter types or parameter counts (return type alone cannot differentiate); Overriding requires identical method names and identical parameter signatures (return type can be covariant).\n• **Private & Static Methods:** Private and static methods can be overloaded, but **cannot** be overridden (static methods undergo method hiding)."
            val p2 = "* **Placement Takeaway:**\nOverriding enforces runtime polymorphism. In Java, always decorate overridden methods with `@Override` so the compiler catches accidental signature discrepancies."
            val sub = "Java & OOPs Mastery"
            val ch = "OOPs 4 Pillars: Encapsulation, Inheritance, Polymorphism, Abstraction"
            val top = "Method Overloading vs Method Overriding"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 11o. StateFlow vs SharedFlow
        if (clean.contains("stateflow") && clean.contains("sharedflow") && (isComparison || clean.contains("difference"))) {
            val p1 = "* **StateFlow vs. SharedFlow Comparison (Kotlin Coroutines):**\n\n• **State vs. Event:** **StateFlow** is a state-holder observable designed to represent persistent UI state (e.g. `UiState.Loading`); **SharedFlow** is an event bus designed for one-shot transient events (e.g. navigation, show snackbar).\n• **Initial Value:** StateFlow requires an initial value upon instantiation; SharedFlow does not require an initial value.\n• **Replay Cache:** StateFlow has a fixed replay cache of 1 (new subscribers immediately receive the latest value); SharedFlow has a configurable replay buffer (defaults to 0).\n• **Conflation:** StateFlow conflates consecutive identical values (will not re-emit if state has not changed); SharedFlow emits every single event regardless of value equality."
            val p2 = "* **Placement Android Architecture Tip:**\nExpose `StateFlow<UiState>` from ViewModels for UI screens, and expose `SharedFlow<UiEvent>` for one-off actions that should not re-trigger when the user rotates their screen."
            val sub = "Android & App Architecture"
            val ch = "Kotlin Coroutines & StateFlow / SharedFlow for Async Work"
            val top = "StateFlow vs SharedFlow Comparison"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 12. "How to / Write Code / Example" Questions

        // 12a. Reverse a string in Python
        if (clean.contains("reverse") && (clean.contains("string") || clean.contains("str"))) {
            val p1 = "* **How to Reverse a String in Python:**\n\n**Approach 1: Extended Slicing (Most Pythonic - O(N) Time, O(N) Space):**\n```python\ndef reverse_string(s: str) -> str:\n    return s[::-1]\n\nprint(reverse_string(\"placement\"))  # Output: tnemecalp\n```\n\n**Approach 2: Two-Pointer Technique (Mutable Character Array - O(N) Time, O(1) Auxiliary Space):**\n```python\ndef reverse_two_pointer(s: str) -> str:\n    chars = list(s)\n    left, right = 0, len(chars) - 1\n    while left < right:\n        chars[left], chars[right] = chars[right], chars[left]\n        left += 1\n        right -= 1\n    return \"\".join(chars)\n```"
            val p2 = "* **Placement Interview Tip:**\nBecause Python strings are strictly immutable, slicing `s[::-1]` creates a new string object in memory. If an interviewer asks to reverse a string in-place with O(1) extra memory, convert to a `list` and use two pointers."
            val sub = "Python & Coding Logic"
            val ch = "Python Strings & Algorithms"
            val top = "Reverse a String"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 12b. Second highest salary in SQL
        if (clean.contains("second highest salary") || clean.contains("2nd highest salary") || (clean.contains("highest salary") && clean.contains("second"))) {
            val p1 = "* **Find Second Highest Salary in SQL:**\n\n**Approach 1: Correlated Subquery (Universal ANSI SQL):**\n```sql\nSELECT MAX(salary) AS second_highest_salary\nFROM employees\nWHERE salary < (\n    SELECT MAX(salary)\n    FROM employees\n);\n```\n\n**Approach 2: Window Function (`DENSE_RANK` - Handles ties correctly):**\n```sql\nWITH RankedSalaries AS (\n    SELECT salary,\n           DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk\n    FROM employees\n)\nSELECT salary AS second_highest_salary\nFROM RankedSalaries\nWHERE rnk = 2\nLIMIT 1;\n```"
            val p2 = "* **Placement Interview Trap:**\nNever use `ORDER BY salary DESC LIMIT 1 OFFSET 1` without handling duplicates. If multiple employees share the top salary, `LIMIT 1 OFFSET 1` returns the highest salary again. Always use `DENSE_RANK()` or `MAX() < (SELECT MAX())`."
            val sub = "SQL & Database Management"
            val ch = "SQL Interview Queries"
            val top = "Second Highest Salary Query"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 12c. Palindrome
        if (clean.contains("palindrome")) {
            val p1 = "* **Check Palindrome (Python):**\n\n```python\ndef is_palindrome(s: str) -> bool:\n    # Filter alphanumeric characters and convert to lowercase\n    clean = [c.lower() for c in s if c.isalnum()]\n    \n    left, right = 0, len(clean) - 1\n    while left < right:\n        if clean[left] != clean[right]:\n            return False\n        left += 1\n        right -= 1\n    return True\n\nprint(is_palindrome(\"A man, a plan, a canal: Panama\"))  # True\nprint(is_palindrome(\"race a car\"))  # False\n```"
            val p2 = "* **Complexity & Edge Cases:**\n• **Time Complexity:** O(N) where N is the length of the string.\n• **Space Complexity:** O(1) auxiliary space if checking characters directly with index pointers and `s[left].isalnum()`.\n• **Edge Cases:** Empty strings and single-character strings are valid palindromes."
            val sub = "Python & Coding Logic"
            val ch = "Two-Pointer Algorithms"
            val top = "Palindrome Verification"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 12d. Fibonacci
        if (clean.contains("fibonacci")) {
            val p1 = "* **Fibonacci Series in Python (O(N) Time, O(1) Space):**\n\n```python\ndef fibonacci_sequence(n: int) -> list[int]:\n    if n <= 0:\n        return []\n    if n == 1:\n        return [0]\n    \n    seq = [0, 1]\n    for _ in range(2, n):\n        seq.append(seq[-1] + seq[-2])\n    return seq\n\nprint(fibonacci_sequence(8))  # [0, 1, 1, 2, 3, 5, 8, 13]\n```"
            val p2 = "* **Placement Interview Note:**\nNaive recursion `fib(n-1) + fib(n-2)` takes exponential O(2^N) time due to repeated subproblem calculations. Explain Dynamic Programming (Memoization / Tabulation) to achieve O(N) time, or Matrix Exponentiation for O(log N) time."
            val sub = "Python & Coding Logic"
            val ch = "Algorithmic Problem Solving"
            val top = "Fibonacci Sequence"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        // 12e. Prime Number
        if (clean.contains("prime number") || clean.contains("check prime") || (clean.contains("prime") && (clean.contains("number") || clean.contains("is prime") || clean.contains("check")))) {
            val p1 = "* **Check Prime Number (O(sqrt(N)) Time Complexity):**\n\n```python\ndef is_prime(n: int) -> bool:\n    if n <= 1:\n        return False\n    if n <= 3:\n        return True\n    if n % 2 == 0 or n % 3 == 0:\n        return False\n    \n    # Check factors of 6k +/- 1 up to sqrt(n)\n    i = 5\n    while i * i <= n:\n        if n % i == 0 or n % (i + 2) == 0:\n            return False\n        i += 6\n    return True\n\nprint(is_prime(29))  # True\nprint(is_prime(1))   # False\n```"
            val p2 = "* **Mathematical Proof:**\nEvery composite integer n has at least one prime divisor less than or equal to sqrt(n). If no divisor is found up to floor(sqrt(n)), n is guaranteed to be prime."
            val sub = "Python & Coding Logic"
            val ch = "Mathematical Algorithms"
            val top = "Prime Number Verification"
            recordTurn(rawQuery, KnowledgeChunk(chunkId = "direct_${System.currentTimeMillis()}", subject = sub, chapter = ch, topic = top, content = "$p1\n\n$p2"))
            return LocalAiResponse(p1, p2, matchedSubject = sub, matchedChapter = ch, matchedTopic = top, confidenceScore = 99.0)
        }

        return null
    }

    private fun hasExplicitContextPronoun(lowerQuery: String): Boolean {
        val words = lowerQuery.split(Regex("[^a-zA-Z0-9]+")).filter { it.isNotBlank() }
        val pronouns = setOf("it", "its", "this", "that", "these", "those", "roots", "solve", "formula", "derivation")
        return words.any { it in pronouns } && words.size <= 6
    }

    private fun getLastActiveChunk(): KnowledgeChunk? {
        val lastTopicTurn = synchronized(contextHistory) {
            contextHistory.lastOrNull { it.matchedTopic != null || it.chunkId != null }
        } ?: return null

        return synchronized(chunks) {
            chunks.find { it.chunkId == lastTopicTurn.chunkId }
                ?: chunks.find { it.topic.equals(lastTopicTurn.matchedTopic, ignoreCase = true) }
        } ?: if (!lastTopicTurn.matchedTopic.isNullOrBlank()) {
            KnowledgeChunk(
                chunkId = lastTopicTurn.chunkId ?: "hist",
                subject = lastTopicTurn.matchedSubject.orEmpty(),
                chapter = lastTopicTurn.matchedChapter.orEmpty(),
                topic = lastTopicTurn.matchedTopic.orEmpty(),
                definition = "${lastTopicTurn.matchedTopic} in ${lastTopicTurn.matchedSubject}."
            )
        } else null
    }

    private fun formatChunkAccordingToPreferences(chunk: KnowledgeChunk): String {
        val defText = if (chunk.definition.isNotBlank()) chunk.definition.trim() else chunk.content.substringBefore(".")
        val formulaText = if (chunk.formulaOrCoreRule.isNotBlank()) chunk.formulaOrCoreRule.trim() else ""
        val exampleText = if (chunk.workedExample.isNotBlank()) chunk.workedExample.trim() else generateTopicExample(chunk)

        val bulletPrefix = if (useEmojis) "📘 " else "* "
        val formulaPrefix = if (useEmojis) "⚡ " else "* "
        val examplePrefix = if (useEmojis) "💡 " else "* "

        return buildString {
            appendLine("${bulletPrefix}**Concept & Definition** (${chunk.subject} · ${chunk.topic})")
            appendLine(defText)
            if (includeFormulas && formulaText.isNotBlank()) {
                appendLine()
                appendLine("${formulaPrefix}**Formula / Core Principle:** $formulaText")
            }
            if (includeExamples && exampleText.isNotBlank()) {
                appendLine()
                appendLine("${examplePrefix}**Key Example & Practical Takeaway:** $exampleText")
            }
        }.trim()
    }

    fun isOutOfScopeQuery(normalized: String): Boolean {
        for (phrase in outOfScopePhrases) {
            if (normalized == phrase || normalized.startsWith("$phrase ") || normalized.endsWith(" $phrase") || normalized.contains(" $phrase ") || normalized.contains(phrase)) {
                if (phrase == "food" && (normalized.contains("food chain") || normalized.contains("food web") || normalized.contains("nutrition"))) {
                    continue
                }
                if (phrase == "match" && (normalized.contains("pattern match") || normalized.contains("string match"))) {
                    continue
                }
                return true
            }
        }
        return false
    }

    fun buildOutOfScopeResponse(): LocalAiResponse {
        return LocalAiResponse(
            paragraph1 = OUT_OF_SCOPE_RESPONSE,
            paragraph2 = "",
            confidenceScore = 100.0,
            isGreeting = false
        )
    }

    fun isElaborationQuery(normalized: String): Boolean {
        val clean = normalized.lowercase(Locale.ROOT)
        return clean.contains("elaborate") || clean.contains("in detail") ||
                clean.contains("deep dive") || clean.contains("deepdive") ||
                clean.contains("step by step") || clean.contains("step-by-step") ||
                clean.contains("more detail") || clean.contains("more details") ||
                clean.contains("explain in detail") || clean.contains("explain more") ||
                clean.contains("break it down") || clean.contains("didnt understand") ||
                clean.contains("didn't understand") || clean.contains("dont understand") ||
                clean.contains("don't understand")
    }

    fun isShortenQuery(normalized: String): Boolean {
        val clean = normalized.lowercase(Locale.ROOT)
        return clean.contains("reply shortly") || clean.contains("make it short") ||
                clean.contains("short answer") || clean.contains("briefly") ||
                clean.contains("summarize") || clean.contains("in short") ||
                clean.contains("give me summary") || clean.contains("make it brief") ||
                clean.contains("explain briefly") || clean.contains("quick summary") ||
                clean.contains("crisp summary") || clean.contains("short notes") ||
                clean.contains("give me short") || clean.contains("short ha sollu") ||
                clean.contains("chinadha thaa") || clean == "short" || clean == "brief"
    }

    fun buildShortenedResponse(chunk: KnowledgeChunk): LocalAiResponse {
        val def = if (chunk.definition.isNotBlank()) chunk.definition else chunk.content.substringBefore(".") + "."
        val formula = if (chunk.formulaOrCoreRule.isNotBlank()) chunk.formulaOrCoreRule else "Core principle in ${chunk.subject}."
        val example = if (chunk.workedExample.isNotBlank()) chunk.workedExample.substringBefore(".") + "." else "Applied in ${chunk.chapter}."

        val p1 = "* **${chunk.topic}** (${chunk.subject})\n$def"
        val p2 = buildString {
            if (includeFormulas) {
                appendLine("* **Core Principle & Formula:** $formula")
            }
            if (includeExamples) {
                appendLine("* **Key Example & Takeaway:** $example")
            }
            append("* **Placement Exam Focus:** High-yield concept in ${chunk.chapter}.")
        }.trim()

        return LocalAiResponse(
            paragraph1 = p1,
            paragraph2 = p2,
            matchedSubject = chunk.subject,
            matchedChapter = chunk.chapter,
            matchedTopic = chunk.topic,
            confidenceScore = 98.0,
            isFollowUp = true
        )
    }

    /**
     * Smart Code & Query Helper: Parses raw Python code or SQL queries and produces
     * step-by-step logic breakdown, algorithmic time & space complexity, and placement interview edge cases.
     */
    fun analyzeCodeOrQuerySnippet(rawCode: String): CodeSnippetAnalysis? {
        val trimmed = rawCode.trim()
        if (trimmed.length < 10) return null

        val lower = trimmed.lowercase(Locale.ROOT)

        // Ensure this is not a general theoretical question asking "what is a select query"
        val isQuestion = lower.startsWith("what is") || lower.startsWith("explain") ||
                lower.startsWith("tell me about") || lower.startsWith("define") ||
                lower.startsWith("how does") || lower.startsWith("why is") ||
                lower.startsWith("difference between")
        if (isQuestion) return null

        // 1. Detect SQL Query Snippets
        val sqlPattern = Regex("(?i)\\b(SELECT\\s+[\\s\\S]*?\\s+FROM|INSERT\\s+INTO|UPDATE\\s+[a-zA-Z0-9_]+\\s+SET|DELETE\\s+FROM|CREATE\\s+TABLE)\\b")
        if (sqlPattern.containsMatchIn(trimmed)) {
            val hasJoin = lower.contains("join")
            val hasGroupBy = lower.contains("group by")
            val hasHaving = lower.contains("having")
            val hasOrderBy = lower.contains("order by")
            val hasWhere = lower.contains("where")

            val breakdown = buildString {
                appendLine("1. **Operation & Target Table:** Executes structured relational operation against source tables.")
                if (hasJoin) {
                    appendLine("2. **Join Strategy:** Combines records across multiple tables based on foreign key/relational predicates.")
                }
                if (hasWhere) {
                    appendLine("3. **Row Filtering (WHERE):** Filters row partitions before grouping or projection.")
                }
                if (hasGroupBy) {
                    appendLine("4. **Aggregation (GROUP BY):** Groups matched rows into summary buckets applying aggregate metrics.")
                }
                if (hasHaving) {
                    appendLine("5. **Post-Aggregation Filter (HAVING):** Prunes aggregated groups based on summary thresholds.")
                }
                if (hasOrderBy) {
                    appendLine("6. **Projection & Sorting (ORDER BY):** Emits final selected attributes ordered by designated columns.")
                }
            }.trim()

            val timeComp = if (hasJoin && !hasWhere) {
                "O(M * N) worst-case without indexes (Hash/Merge join); O(M + N) with indexed join keys."
            } else if (hasOrderBy && hasGroupBy) {
                "O(N log N) due to filesort / temporary table sorting."
            } else if (hasWhere) {
                "O(log N) for B-Tree index range scans; O(N) for full table scans."
            } else {
                "O(N) sequential table scan over matching records."
            }

            val spaceComp = if (hasGroupBy || hasOrderBy || hasJoin) {
                "O(K) auxiliary buffer memory for hash aggregation tables and sorting buffers."
            } else {
                "O(1) streaming cursor memory."
            }

            val edgeCases = "• **NULL Semantics:** In SQL, `NULL = NULL` evaluates to `UNKNOWN`, causing unexpected row omission in INNER JOINs.\n" +
                    "• **Cartesian Explosion:** Missing or non-unique JOIN conditions will trigger an accidental CROSS JOIN producing excessive rows.\n" +
                    "• **WHERE vs ON in OUTER JOINs:** Placing filters in the WHERE clause instead of ON turns a LEFT JOIN into an INNER JOIN.\n" +
                    "• **Aggregation on Empty Sets:** `COUNT(*)` returns 0, whereas `SUM()` and `AVG()` return `NULL` when 0 rows match."

            return CodeSnippetAnalysis(
                language = "SQL Query",
                logicBreakdown = breakdown,
                timeComplexity = timeComp,
                spaceComplexity = spaceComp,
                interviewEdgeCases = edgeCases
            )
        }

        // 2. Detect Python Snippets
        val hasPythonMarker = Regex("""(?m)(^\s*def\s+\w+\s*\(|^\s*class\s+\w+|^\s*for\s+\w+\s+in\s+|^\s*while\s+.*?:|^\s*if\s+.*?:|^\s*try\s*:|^\s*except[\s:]|^\s*with\s+open\s*\(|\[\s*\w+\s+for\s+\w+\s+in\s+)""").containsMatchIn(trimmed) ||
                (trimmed.contains(" = [") || trimmed.contains(" = {")) && trimmed.lines().size >= 2 ||
                (trimmed.lines().size >= 2 && trimmed.contains(":") && (lower.contains("return ") || lower.contains("def ") || lower.contains("print(")))

        if (hasPythonMarker && !trimmed.contains("SELECT", ignoreCase = true)) {
            val hasNestedLoops = Regex("(?s)for\\s+.*?:.*for\\s+.*?:").containsMatchIn(trimmed) ||
                    Regex("(?s)while\\s+.*?:.*while\\s+.*?:").containsMatchIn(trimmed)
            val hasSingleLoop = lower.contains("for ") || lower.contains("while ")
            val hasRecursion = lower.contains("def ") && Regex("def\\s+(\\w+)[\\s\\S]*?\\1\\s*\\(").containsMatchIn(trimmed)
            val hasSorting = lower.contains(".sort(") || lower.contains("sorted(")
            val hasDictOrSet = lower.contains("dict(") || lower.contains("set(") || lower.contains("{") || lower.contains(".get(")

            val breakdown = buildString {
                appendLine("1. **Declaration & Input Handling:** Prepares variables and validates function/block arguments.")
                if (hasSingleLoop || hasNestedLoops) {
                    appendLine("2. **Iterative Traversal:** Loops through sequence elements maintaining state counters or pointers.")
                }
                if (hasDictOrSet) {
                    appendLine("3. **Hashing & Fast Lookup:** Utilizes hash table operations for O(1) average lookup/insert.")
                }
                if (hasRecursion) {
                    appendLine("4. **Recursive Call Stack:** Breaks problem down into smaller self-similar subproblems with base cases.")
                }
                if (lower.contains("return ") || lower.contains("yield ")) {
                    appendLine("5. **Result Emission:** Returns calculated result or yields generator stream to caller.")
                }
            }.trim()

            val timeComp = when {
                hasNestedLoops -> "O(N²) quadratic time due to nested loop iteration over data."
                hasSorting -> "O(N log N) using Python's Timsort algorithm."
                hasRecursion -> "O(2ⁿ) if un-memoized tree recursion; O(N) with dynamic programming / memoization."
                hasSingleLoop -> "O(N) linear time passing through the collection once."
                else -> "O(1) constant time execution."
            }

            val spaceComp = when {
                hasRecursion -> "O(D) call stack frame space, where D is maximum recursion depth."
                hasDictOrSet || lower.contains(".append(") || lower.contains("[x for") -> "O(N) auxiliary space storing unique keys/elements."
                else -> "O(1) auxiliary space modifying or evaluating variables in-place."
            }

            val edgeCases = "• **Empty / None Inputs:** Check for `if not data: return ...` before accessing index 0 to avoid `IndexError`.\n" +
                    "• **Default Mutable Argument Anti-Pattern:** Avoid `def func(lst=[])`; default lists persist mutations across calls.\n" +
                    "• **Integer Division & Rounding:** Python 3 `/` returns float; use `//` for integer truncation in placement math.\n" +
                    "• **Off-by-One in Ranges:** Remember `range(start, end)` is exclusive of `end`."

            return CodeSnippetAnalysis(
                language = "Python Code",
                logicBreakdown = breakdown,
                timeComplexity = timeComp,
                spaceComplexity = spaceComp,
                interviewEdgeCases = edgeCases
            )
        }

        return null
    }

    private fun isDoubtAssistanceQuery(normalized: String): Boolean {
        val words = normalized.split(" ").filter { it.isNotBlank() }
        if (words.any { it == "doubt" || it == "doubts" }) return true
        if (normalized.contains("doubt")) return true

        if (doubtPhrases.contains(normalized)) return true
        for (phrase in doubtPhrases) {
            if (phrase.length >= 8 && normalized.contains(phrase)) return true
        }

        if (words.contains("confused") && words.size <= 4) {
            return true
        }

        return false
    }

    private fun buildDoubtAssistanceResponse(): LocalAiResponse {
        return LocalAiResponse(
            paragraph1 = "💡 **SYNAPSE AI Doubt Resolution Tutor**\n\nI am ready to help you resolve any doubt across all your university subjects! Whether you are stuck on a difficult mathematical derivation, an algorithm problem, a physics law, or a complex engineering theory, I will break it down step-by-step.",
            paragraph2 = "⚡ **How We Can Solve Your Doubts:**\n" +
                    "• **Ask Direct Concepts:** Type or speak any concept (e.g. *\"Explain Eigenvalues and Eigenvectors\"*, *\"What is Backpropagation in Neural Networks?\"*, or *\"How does Dijkstra's Algorithm work?\"*)\n" +
                    "• **Step-by-Step Derivations:** Ask *\"How to solve...\"* or *\"Give me a worked example\"* for hands-on problem breakdown.\n" +
                    "• **High-Yield Exam Prep:** Ask *\"Summarize key exam takeaways for Chapter 3\"* to get concise smart notes.\n\n" +
                    "Which subject or chapter are you currently studying? Tell me your question or specific topic, and we'll break it down together!",
            confidenceScore = 98.0,
            isGreeting = false
        )
    }

    private fun isSubjectCatalogQuery(normalized: String): Boolean {
        if (subjectCatalogPhrases.contains(normalized)) return true
        for (phrase in subjectCatalogPhrases) {
            if (phrase.length >= 8 && normalized.contains(phrase)) return true
        }

        val words = normalized.split(" ").filter { it.isNotBlank() }
        if (words.contains("subjects") && (words.contains("how") || words.contains("many") || words.contains("what") || words.contains("which") || words.contains("list") || words.contains("show") || words.contains("all"))) {
            return true
        }

        return false
    }

    private fun buildSubjectCatalogResponse(): LocalAiResponse {
        return LocalAiResponse(
            paragraph1 = "📚 **SYNAPSE AI Placement & Career Track Catalog (4 Core Tracks)**\n\nI feature 4 high-impact career tracks curated for software engineering campus placements, technical assessments, and interview rounds:",
            paragraph2 = "⚡ **Core Tracks Available On-Device:**\n" +
                    "• 🐍 **Python & Coding Logic**: Memory model, OOP, closures, algorithmic problem-solving & data structures\n" +
                    "• 🗄️ **SQL & Database Management**: Schema design, ACID transactions, B-Tree indexing, complex joins & query optimization\n" +
                    "• 🤖 **AI & Modern Tech Concepts**: Neural networks, LLMs, RAG architectures, cloud computing & cybersecurity\n" +
                    "• 🎯 **Aptitude & Interview Prep**: Speed math, logical deduction, data interpretation & placement interview strategies\n\n" +
                    "💡 *Every track includes interactive chapters, in-chat quizzes, and smart revision notes. Tell me which track you want to explore!*",
            confidenceScore = 98.0,
            isTopicSuggestion = true
        )
    }

    private fun isHowItWorksQuery(normalized: String): Boolean {
        if (howItWorksPhrases.contains(normalized)) return true
        for (phrase in howItWorksPhrases) {
            if (phrase.length >= 8 && normalized.contains(phrase)) return true
        }

        val words = normalized.split(" ").filter { it.isNotBlank() }
        if (words.contains("how") && words.contains("should") && (words.contains("work") || words.contains("operate"))) {
            return true
        }

        return false
    }

    private fun buildHowItWorksResponse(): LocalAiResponse {
        return LocalAiResponse(
            paragraph1 = "⚙️ **How SYNAPSE AI Works & How to Study With It**\n\nSYNAPSE AI is built as your complete intelligent academic and placement preparation companion, providing fast on-device responses, interactive in-chat quizzes, and automated smart notes.",
            paragraph2 = "⚡ **Key Capabilities & Workflow:**\n" +
                    "1. 🧠 **Fast SLM + Semantic RAG**: Generates clear, verified concept explanations, formulas, code snippets, and interview breakdowns with zero hallucinations.\n" +
                    "2. 🎙️ **Live Voice Assistant**: Talk naturally to ask questions hands-free; our speech recognizer listens patiently and replies in a natural voice.\n" +
                    "3. 📝 **AI Smart Notes**: Automatically summarizes concepts into key definitions, core principles, and high-yield interview takeaways.\n" +
                    "4. 🎯 **Interactive In-Chat Quizzes**: Test your understanding with chapter-level practice questions, instant answer scoring, and explanations right inside the chat.\n" +
                    "5. 🔄 **Hybrid Backend Sync**: All your learning progress, completed chapters, notes, and quiz scores synchronize seamlessly between local storage and the database.\n\n" +
                    "To begin, select a track from the home screen, or simply type or speak your questions right here!",
            confidenceScore = 100.0,
            isGreeting = false
        )
    }

    private fun isGratitudeQuery(normalized: String): Boolean {
        if (gratitudePhrases.contains(normalized) || gratitudeWords.contains(normalized)) {
            return true
        }

        val words = normalized.split(" ").filter { it.isNotBlank() }
        if (words.isNotEmpty() && words.size <= 4) {
            if (words.any { it in setOf("thanks", "thank", "thx", "tq") }) return true
            if (words.all { it in gratitudeWords }) return true
            if (normalized.startsWith("thank you") || normalized.startsWith("thank u") || normalized.startsWith("thanks")) return true
            if (normalized == "got it" || normalized.startsWith("got it")) return true
        }

        return false
    }

    private fun buildGratitudeResponse(): LocalAiResponse {
        return LocalAiResponse(
            paragraph1 = "You're very welcome! Let me know whenever you want to dive into another topic, formula, or practice question.",
            paragraph2 = "",
            confidenceScore = 100.0,
            isGreeting = false
        )
    }

    private fun isGreetingQuery(normalized: String): Boolean {
        if (greetingWords.contains(normalized) || greetingPhrases.contains(normalized)) {
            return true
        }

        val words = normalized.split(" ").filter { it.isNotBlank() }
        if (words.size in 1..3) {
            if (words.first() in greetingWords) return true
            if (words.any { it in greetingWords } && words.any { it in setOf("there", "synapse", "ai", "friend", "buddy", "tutor") }) {
                return true
            }
            for (gp in greetingPhrases) {
                if (normalized.contains(gp)) return true
            }
        }

        return false
    }

    private fun buildGreetingResponse(normalized: String): LocalAiResponse {
        val name = preferredName.trim()
        val isNamed = name.isNotBlank()
        val replyText = when {
            normalized == "gn" || normalized.contains("good night") ->
                if (isNamed) "Good night, $name! Rest well and keep learning tomorrow." else "Good night! Rest well and keep learning tomorrow."
            normalized.contains("good morning") || normalized == "gm" ->
                if (isNamed) "Good morning, $name! Ready to learn something exciting today? What subject are we tackling?" else "Good morning! Ready to learn something exciting today? What subject are we tackling?"
            normalized.contains("good afternoon") ->
                if (isNamed) "Good afternoon, $name! Hope you're having a productive day. What subject shall we explore?" else "Good afternoon! Hope you're having a productive day. What subject shall we explore?"
            normalized.contains("good evening") ->
                if (isNamed) "Good evening, $name! Let's do some focused study tonight. What can I help you understand?" else "Good evening! Let's do some focused study tonight. What can I help you understand?"
            normalized.contains("vanakkam") ->
                if (isNamed) "Vanakkam, $name! How can I help you with your studies today?" else "Vanakkam! How can I help you with your studies today?"
            normalized.contains("namaste") ->
                if (isNamed) "Namaste, $name! How can I assist you with your learning today?" else "Namaste! How can I assist you with your learning today?"
            normalized.contains("hello friend") || normalized.contains("hi friend") || normalized.contains("hey friend") ->
                if (isNamed) "Hey $name! Great to see you. What subject shall we dive into today?" else "Hey! Great to see you. What subject shall we dive into today?"
            else ->
                if (isNamed) "Hello, $name! I'm doing great and ready to help you study. What topic shall we explore today?" else "Hello! I'm doing great and ready to help you study. What topic shall we explore today?"
        }

        return LocalAiResponse(
            paragraph1 = replyText,
            paragraph2 = "",
            isGreeting = true,
            confidenceScore = 100.0
        )
    }

    private fun isFollowUpQuery(lowerQuery: String): Boolean {
        val cleaned = lowerQuery.replace(Regex("[^a-zA-Z0-9\\s]"), "").trim()
        if (cleaned.contains("no example") || cleaned.contains("without example") ||
            cleaned.contains("no formula") || cleaned.contains("without formula") ||
            cleaned.contains("no need") || cleaned.contains("dont use") || cleaned.contains("no emoji")) {
            return false
        }
        if (isShortenQuery(cleaned)) return true
        if (isElaborationQuery(cleaned)) return true
        if (examplePrompts.any { cleaned.contains(it) }) return true
        if (elaboratePrompts.any { cleaned.contains(it) }) return true
        if (solvePrompts.any { cleaned.contains(it) }) return true
        if (cleaned.contains("interview") || cleaned.contains("interview questions") || cleaned.contains("questions on this") || cleaned.contains("interview q")) return true
        if (cleaned == "why" || cleaned.startsWith("why ") || cleaned.contains("why does") || cleaned.contains("reason")) return true
        if (cleaned.contains("give example") || cleaned.contains("example") || cleaned.contains("code example")) return true

        val words = cleaned.split("\\s+".toRegex())
        if (words.size <= 5 && words.any { it in setOf("it", "its", "this", "that", "these", "those", "roots", "solve", "why", "short", "details", "example") }) {
            return true
        }

        return false
    }

    private fun buildContextAwareFollowUpResponse(
        lowerQuery: String,
        chunk: KnowledgeChunk
    ): LocalAiResponse {
        val cleaned = lowerQuery.replace(Regex("[^a-zA-Z0-9\\s]"), "").trim()

        if (isShortenQuery(cleaned) || cleaned.contains("short") || cleaned.contains("brief")) {
            return buildShortenedResponse(chunk)
        }

        if (isElaborationQuery(cleaned) || elaboratePrompts.any { cleaned.contains(it) } || cleaned == "elaborate" || cleaned.contains("details")) {
            return buildEducationalResponse(chunk, 100.0, isElaborated = true, isFollowUp = true)
        }

        if (cleaned.contains("interview") || cleaned.contains("interview question") || cleaned.contains("questions on this")) {
            val iq = generateInterviewQuestions(chunk)
            val tip = generatePlacementInterviewTip(chunk)
            return LocalAiResponse(
                paragraph1 = "* **Placement Interview Preparation** (${chunk.subject} · ${chunk.topic})\n\n$iq",
                paragraph2 = "* **Interviewer Evaluation Tip & Takeaway:**\n$tip",
                matchedSubject = chunk.subject,
                matchedChapter = chunk.chapter,
                matchedTopic = chunk.topic,
                confidenceScore = 95.0,
                isFollowUp = true
            )
        }

        if (cleaned == "why" || cleaned.startsWith("why ") || cleaned.contains("why does") || cleaned.contains("reason")) {
            val rationale = generateEngineeringRationale(chunk)
            return LocalAiResponse(
                paragraph1 = "* **Engineering Rationale & Design Philosophy** (${chunk.subject} · ${chunk.topic})\n\n$rationale",
                paragraph2 = "* **Core Principle & Takeaway:**\nUnderstanding the architectural reasoning behind ${chunk.topic} differentiates junior coders from senior engineering talent.",
                matchedSubject = chunk.subject,
                matchedChapter = chunk.chapter,
                matchedTopic = chunk.topic,
                confidenceScore = 95.0,
                isFollowUp = true
            )
        }

        val bulletPrefix = if (useEmojis) "📘 " else "* "
        val insightPrefix = if (useEmojis) "💡 " else "* "

        if (examplePrompts.any { cleaned.contains(it) } || (cleaned.contains("example") && !cleaned.contains("no example") && !cleaned.contains("without example"))) {
            val exampleText = if (chunk.workedExample.isNotBlank()) {
                chunk.workedExample
            } else {
                generateTopicExample(chunk)
            }
            return LocalAiResponse(
                paragraph1 = "${bulletPrefix}**Practical Example & Implementation** (${chunk.subject} · ${chunk.topic})\n\nHere is a real-world scenario illustrating **${chunk.topic}**:",
                paragraph2 = "$exampleText\n\n${insightPrefix}*Core Principle & Takeaway:* Derived from ${chunk.chapter} (${chunk.subject}).",
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
                paragraph1 = "${bulletPrefix}**Method & Solving Steps** (${chunk.subject} · ${chunk.topic})\n\nHere is how to solve and evaluate problems for **${chunk.topic}**:",
                paragraph2 = "$stepGuide\n\n${insightPrefix}*Core Principle:* $formulaText.",
                matchedSubject = chunk.subject,
                matchedChapter = chunk.chapter,
                matchedTopic = chunk.topic,
                confidenceScore = 90.0,
                isFollowUp = true
            )
        }

        return LocalAiResponse(
            paragraph1 = "${bulletPrefix}**In-Depth Breakdown** (${chunk.subject} · ${chunk.topic})\n\nExpanding further on **${chunk.topic}**:",
            paragraph2 = "${chunk.content.ifBlank { "${chunk.definition}\n\n${chunk.formulaOrCoreRule}" }}\n\n${insightPrefix}*Core Principle & Takeaway:* Mastering ${chunk.topic} connects directly to advanced applications in ${chunk.chapter}.",
            matchedSubject = chunk.subject,
            matchedChapter = chunk.chapter,
            matchedTopic = chunk.topic,
            confidenceScore = 90.0,
            isFollowUp = true
        )
    }

    fun generateRealLifeAnalogy(chunk: KnowledgeChunk): String {
        val sub = chunk.subject.lowercase(Locale.ROOT)
        val topic = chunk.topic.lowercase(Locale.ROOT)
        val chapter = chunk.chapter.lowercase(Locale.ROOT)

        return when {
            sub.contains("python") || sub.contains("coding") -> {
                when {
                    topic.contains("mutab") || chapter.contains("mutab") ->
                        "Think of an immutable object as an engraved metal plaque: once stamped, its text cannot change—to change it, you must forge an entirely new plaque. A mutable object is like a whiteboard where you can erase and rewrite items in-place without buying a new board."
                    topic.contains("gil") || chapter.contains("gil") || topic.contains("thread") ->
                        "Think of the GIL like a busy restaurant kitchen with four cooks but only one head chef's knife. Even with four cooks standing ready, only one can chop at any single moment, meaning CPU-heavy tasks must create separate standalone kitchens (multiprocessing) to chop simultaneously."
                    topic.contains("generator") || topic.contains("iterator") ->
                        "Think of a generator like a vending machine that dispenses a single snack only when a coin is inserted. A regular list is like hauling an entire grocery store into your living room before eating a single bite."
                    topic.contains("oop") || topic.contains("class") ->
                        "Think of a class as an architectural blueprint for a house, and an object as the physical brick-and-mortar house constructed from that blueprint. Multiple distinct houses can be built from one blueprint."
                    else ->
                        "Think of ${chunk.topic} like a specialized power tool in a software engineer's toolbox. Used in the right scenario, it minimizes boilerplate code and prevents unexpected runtime failures."
                }
            }
            sub.contains("sql") || sub.contains("database") -> {
                when {
                    topic.contains("join") || chapter.contains("join") ->
                        "Think of an INNER JOIN like comparing two class attendance sheets: only students whose names appear on BOTH sheets get marked present. A LEFT JOIN ensures every student on the primary sheet is listed, attaching extra info only if they also signed the second sheet."
                    topic.contains("index") || chapter.contains("index") ->
                        "Think of a database index like the alphabetized index at the back of a thick textbook. Instead of reading every page from 1 to 1000 (full table scan), you look up the keyword and jump directly to the target page."
                    topic.contains("acid") || topic.contains("transaction") ->
                        "Think of an ACID transaction like an ATM withdrawal: either the cash comes out and your account deducts together, or the entire transaction aborts cleanly—your money is never left in an inconsistent half-way state."
                    topic.contains("group") || topic.contains("aggregat") ->
                        "Think of GROUP BY like sorting a box of mixed fruits into separate baskets by fruit type, then weighing each basket individually with an aggregate scale."
                    else ->
                        "Think of ${chunk.topic} like the structural foundation of a high-rise building. It enforces data consistency and prevents catastrophic collapses during high-traffic load."
                }
            }
            sub.contains("ai") || sub.contains("tech") || sub.contains("concept") -> {
                when {
                    topic.contains("rag") || chapter.contains("rag") ->
                        "Think of RAG like giving an intelligent student an open-book library during a difficult final exam. Instead of relying solely on memorized intuition, the AI searches authoritative reference books in real-time before composing its answer."
                    topic.contains("neural") || topic.contains("deep learning") || topic.contains("gradient") ->
                        "Think of training a neural network like tuning a complex sound board with hundreds of knobs. When the music sounds distorted (high loss), you gently nudge each knob in the direction that makes the sound clearer (backpropagation)."
                    topic.contains("transformer") || topic.contains("attention") ->
                        "Think of attention mechanisms like a highlighter pen while reading a dense contract. Rather than treating every single word equally, the reader highlights and cross-references only the key contractual clauses."
                    else ->
                        "Think of ${chunk.topic} like an automated smart assistant that learns statistical patterns from millions of past observations to make accurate real-time predictions."
                }
            }
            sub.contains("aptitude") || sub.contains("interview") -> {
                when {
                    topic.contains("speed") || topic.contains("distance") || topic.contains("train") ->
                        "Think of relative speed like walking on an airport moving walkway. If you walk forward along with the walkway, your speeds add up; if you try walking backwards against it, your net speed is the difference."
                    topic.contains("work") || topic.contains("time") ->
                        "Think of work problems in terms of baking cookies. If Person A bakes 5 cookies an hour and Person B bakes 4 cookies an hour, their combined oven produces 9 cookies every hour."
                    topic.contains("probability") ->
                        "Think of probability like rolling a fair die: having 1 winning face out of 6 possibilities gives you an exact quantifiable 1-in-6 chance of success."
                    else ->
                        "Think of ${chunk.topic} like finding a mathematical shortcut through a maze: applying the fundamental formula allows you to bypass tedious trial-and-error arithmetic."
                }
            }
            else -> {
                "Think of ${chunk.topic} as a foundational building block in ${chunk.subject}. Just as a master blueprint specifies tolerances and constraints before assembly, understanding this principle ensures reliable system performance."
            }
        }
    }

    fun generatePlacementInterviewTip(chunk: KnowledgeChunk): String {
        val sub = chunk.subject.lowercase(Locale.ROOT)
        val topic = chunk.topic.lowercase(Locale.ROOT)
        val chapter = chunk.chapter.lowercase(Locale.ROOT)

        return when {
            sub.contains("python") || sub.contains("coding") -> {
                when {
                    topic.contains("mutab") || chapter.contains("mutab") ->
                        "Placement rounds often test mutating a list while iterating over it (which skips elements) and using mutable default arguments in functions (`def foo(a=[])`). Always explain why dict keys must be immutable and hashable."
                    topic.contains("gil") || chapter.contains("gil") || topic.contains("thread") ->
                        "Interviewers will ask: 'Does multi-threading speed up CPU-bound tasks in Python?' The definitive answer is NO due to the GIL; explain when to switch to `multiprocessing` or asynchronous I/O (`asyncio`)."
                    topic.contains("generator") || topic.contains("iterator") ->
                        "Emphasize the difference between eager evaluation (lists consuming O(N) memory immediately) versus lazy evaluation (generators yielding values one-by-one with O(1) auxiliary memory)."
                    else ->
                        "Always state both Time and Space complexity before writing code. Identify potential boundary conditions like empty inputs, single-element arrays, and negative values."
                }
            }
            sub.contains("sql") || sub.contains("database") -> {
                when {
                    topic.contains("join") || chapter.contains("join") ->
                        "A classic interview trap is asking the difference between `ON` and `WHERE` conditions in `LEFT JOIN`. Remember: filtering the right table in `WHERE` turns the query into an `INNER JOIN` by discarding NULL rows!"
                    topic.contains("index") || chapter.contains("index") ->
                        "Be ready to explain 'Sargable queries'. Applying functions on indexed columns (e.g. `WHERE YEAR(created_at) = 2024`) invalidates B-Tree index lookups and causes slow full-table scans."
                    topic.contains("acid") || topic.contains("transaction") ->
                        "Expect questions on transaction isolation levels (Read Uncommitted, Read Committed, Repeatable Read, Serializable) and how they mitigate dirty reads, non-repeatable reads, and phantom reads."
                    topic.contains("group") || topic.contains("having") ->
                        "Remember: `WHERE` filters rows BEFORE grouping occurs, whereas `HAVING` filters aggregated groups AFTER `GROUP BY` calculation."
                    else ->
                        "Relational schema questions frequently evaluate normal forms (1NF, 2NF, 3NF, BCNF) and when deliberate denormalization is acceptable for read-heavy reporting systems."
                }
            }
            sub.contains("ai") || sub.contains("tech") || sub.contains("concept") -> {
                when {
                    topic.contains("rag") || chapter.contains("rag") ->
                        "Interviewers test chunking strategies (fixed size vs semantic splitting), choice of vector distance metrics (cosine similarity vs dot product vs euclidean), and reranking methods to prevent hallucination."
                    topic.contains("neural") || topic.contains("deep learning") || topic.contains("gradient") ->
                        "Be prepared to explain the vanishing and exploding gradient problem, and how activation functions like ReLU and architectural innovations like residual skip connections solve it."
                    topic.contains("overfitting") || topic.contains("regularization") ->
                        "Highlight techniques to prevent overfitting: Dropout, L1/L2 weight regularization, Early Stopping, and Data Augmentation."
                    else ->
                        "Focus on balancing model accuracy with deployment latency and memory footprint—companies look for engineers who understand production trade-offs."
                }
            }
            sub.contains("aptitude") || sub.contains("interview") -> {
                when {
                    topic.contains("speed") || topic.contains("distance") || topic.contains("train") ->
                        "Unit conversion is the #1 student trap! Always convert km/h to m/s by multiplying by 5/18 (or m/s to km/h by multiplying by 18/5) before solving."
                    topic.contains("work") || topic.contains("time") ->
                        "Use the LCM method to assume a total units of work. It eliminates messy fractions and lets you calculate integer daily efficiencies in seconds."
                    topic.contains("percentage") || topic.contains("profit") ->
                        "Use successive percentage change formula: a + b + (ab/100) to solve successive discounts or double percentage alterations instantly."
                    else ->
                        "Apply answer elimination: inspect unit digits, test divisibility rules, and eliminate absurd options first to solve questions in under 45 seconds."
                }
            }
            else -> {
                "Interviewers look for candidates who explain both the 'how' and the 'why'—always state time complexity, potential failure edge cases, and practical engineering trade-offs."
            }
        }
    }

    fun generateInterviewQuestions(chunk: KnowledgeChunk): String {
        val sub = chunk.subject
        val topic = chunk.topic
        return buildString {
            appendLine("• **Q1 (Core Mechanics & Theory):** How would you explain the internal mechanism of **$topic** to a junior developer, and what core technical problem does it solve in $sub?")
            appendLine()
            appendLine("• **Q2 (Edge Cases & Failure Scenarios):** In high-concurrency production systems, what are the most dangerous edge cases, performance bottlenecks, or failure modes associated with **$topic**, and how do you safeguard against them?")
            appendLine()
            append("• **Q3 (System Design & Tradeoffs):** When evaluating alternative engineering solutions, under what specific architectural conditions would you choose NOT to implement **$topic**?")
        }
    }

    fun generateEngineeringRationale(chunk: KnowledgeChunk): String {
        val sub = chunk.subject
        val topic = chunk.topic
        return buildString {
            appendLine("• **The Engineering Motivation:** Modern software systems demand predictable performance, data integrity, and resource bounds. **$topic** was designed to eliminate non-deterministic race conditions and minimize operational latency in $sub.")
            appendLine()
            appendLine("• **Architectural Trade-Offs:** Every engineering abstraction incurs a cost. While **$topic** provides structural safety and consistency, it requires strict adherence to invariant contracts and consumes auxiliary operational resources.")
            appendLine()
            append("• **Real-World Impact at Scale:** Without **$topic**, distributed production workloads risk catastrophic cascading failures, deadlocks, and silent data corruption under heavy concurrent traffic.")
        }
    }

    private fun generateTopicExample(chunk: KnowledgeChunk): String {
        val sub = chunk.subject.lowercase(Locale.ROOT)
        val topic = chunk.topic.lowercase(Locale.ROOT)
        val chapter = chunk.chapter.lowercase(Locale.ROOT)

        return when {
            sub.contains("python") || sub.contains("coding") -> {
                when {
                    topic.contains("mutab") || chapter.contains("mutab") || topic.contains("memory") -> {
                        "```python\n# Immutable int & tuple vs Mutable list\na = [1, 2, 3]\nb = a\nb.append(4)  # Modifies in-place: id(a) == id(b)\nprint(a)      # Output: [1, 2, 3, 4]\n\nx = (1, 2)\n# x[0] = 9   # Raises TypeError: 'tuple' object does not support item assignment\n```"
                    }
                    topic.contains("gil") || chapter.contains("gil") || topic.contains("thread") -> {
                        "```python\nimport multiprocessing as mp\n\ndef heavy_computation(n):\n    return sum(i * i for i in range(n))\n\nif __name__ == '__main__':\n    with mp.Pool(processes=4) as pool:\n        results = pool.map(heavy_computation, [1000000] * 4)\n    print('Parallel CPU-bound execution completed bypassing GIL!')\n```"
                    }
                    topic.contains("generator") || topic.contains("iterator") -> {
                        "```python\ndef fibonacci_gen(limit):\n    a, b = 0, 1\n    while a < limit:\n        yield a\n        a, b = b, a + b\n\nfor num in fibonacci_gen(50):\n    print(num, end=' ')  # Memory-efficient O(1) auxiliary space streaming\n```"
                    }
                    else -> {
                        "```python\n# Practical Placement Logic Example\ndef solve_two_sum(nums, target):\n    seen = {}\n    for i, num in enumerate(nums):\n        diff = target - num\n        if diff in seen:\n            return [seen[diff], i]\n        seen[num] = i\n    return []\n# O(N) Time and O(N) Space hash map optimization\n```"
                    }
                }
            }
            sub.contains("sql") || sub.contains("database") -> {
                when {
                    topic.contains("join") || chapter.contains("join") -> {
                        "```sql\n-- Retrieve students with their enrolled courses (including students with no courses)\nSELECT s.student_id, s.name, c.course_title\nFROM students s\nLEFT JOIN enrollments e ON s.student_id = e.student_id\nLEFT JOIN courses c ON e.course_id = c.course_id\nWHERE s.active_status = 1;\n```"
                    }
                    topic.contains("group") || topic.contains("aggregat") || topic.contains("having") -> {
                        "```sql\n-- Find departments with average salary exceeding $85,000\nSELECT department_id, COUNT(*) AS employee_count, ROUND(AVG(salary), 2) AS avg_salary\nFROM employees\nGROUP BY department_id\nHAVING AVG(salary) > 85000\nORDER BY avg_salary DESC;\n```"
                    }
                    topic.contains("index") || chapter.contains("index") -> {
                        "```sql\n-- Create composite B-Tree index to avoid full table scans\nCREATE INDEX idx_user_orders ON orders (user_id, order_date DESC);\n-- Explain query execution plan\nEXPLAIN SELECT * FROM orders WHERE user_id = 1042 ORDER BY order_date DESC LIMIT 10;\n```"
                    }
                    else -> {
                        "```sql\n-- High-performance transactional query with ACID safety\nSTART TRANSACTION;\nUPDATE accounts SET balance = balance - 500 WHERE account_id = 101;\nUPDATE accounts SET balance = balance + 500 WHERE account_id = 102;\nCOMMIT;\n```"
                    }
                }
            }
            sub.contains("ai") || sub.contains("tech") || sub.contains("concept") -> {
                when {
                    topic.contains("rag") || chapter.contains("rag") -> {
                        "In an enterprise RAG pipeline: A user queries 'What is the refund policy?'. The vector embedding is computed, top-3 matching chunks from the policy PDF are retrieved via cosine similarity in Pinecone, and injected into the prompt: 'Context: [chunks] Question: [query] Answer based on context only.'"
                    }
                    topic.contains("loss") || topic.contains("gradient") || topic.contains("backprop") -> {
                        "In training a classification model: Cross-entropy loss measures deviation from ground truth. The optimizer calculates partial derivatives ∂L/∂w for each weight layer via the chain rule and updates weights: w = w - η * ∇L."
                    }
                    else -> {
                        "In self-attention mechanisms, input tokens are mapped into Query (Q), Key (K), and Value (V) matrices. Attention weights are computed via Softmax(Q·Kᵀ / √dₖ)·V, allowing the network to weight contextual importance across the entire sequence in parallel."
                    }
                }
            }
            sub.contains("aptitude") || sub.contains("interview") -> {
                when {
                    topic.contains("speed") || topic.contains("distance") || topic.contains("train") -> {
                        "A 150m long train travels at 54 km/h (54 × 5/18 = 15 m/s). To cross a 250m long platform, total distance = 150 + 250 = 400m. Time = Distance / Speed = 400 / 15 = 26.67 seconds."
                    }
                    topic.contains("work") || topic.contains("time") -> {
                        "Person A can finish a project in 12 days; Person B in 15 days. Total work (LCM of 12 & 15) = 60 units. A's efficiency = 5 units/day, B's efficiency = 4 units/day. Working together = 5 + 4 = 9 units/day. Time required = 60 / 9 = 6.67 days."
                    }
                    topic.contains("profit") || topic.contains("loss") || topic.contains("percentage") -> {
                        "If an article is bought at \$400 and marked up by 30% (\$520), then sold with a 10% discount: Selling Price = 520 × 0.90 = \$468. Net Profit = 468 - 400 = \$68 (Net Profit % = 17%)."
                    }
                    else -> {
                        "Probability problem: Selecting 2 cards from a standard 52-card deck without replacement. Probability of getting 2 Aces = (4/52) × (3/51) = 1/221."
                    }
                }
            }
            sub.contains("mathematics") -> {
                if (chunk.chapter.contains("Quadratic", ignoreCase = true)) {
                    "Consider a projectile trajectory: h(t) = -5t² + 20t. Setting height to ground (h(t) = 0) yields -5t(t - 4) = 0. Roots are t = 0 (launch) and t = 4s (impact). The quadratic formula allows calculating the exact flight duration and apex height."
                } else {
                    "In calculus, if a car's displacement is s(t) = 3t² + 4t meters, the instantaneous velocity is the derivative v(t) = s'(t) = 6t + 4 m/s. At t = 2s, velocity equals 16 m/s."
                }
            }
            sub.contains("computer science") -> {
                if (chunk.chapter.contains("Algorithms", ignoreCase = true)) {
                    "Searching for target 42 in sorted array [10, 20, 30, 42, 50, 70]: Binary search inspects midpoint 30 < 42, narrows to right half [42, 50, 70], checks 42 = 42! Located in 2 comparisons instead of scanning 4 elements linearly."
                } else {
                    "A browser's 'Back' and 'Forward' buttons use two Stacks (LIFO). When you click Back, the current URL pushes onto the Forward stack and pops from the Back stack in O(1) constant time."
                }
            }
            sub.contains("data science") -> {
                "When predicting house prices, if a linear regression model predicts \$300,000 for a house actually selling at \$320,000, residual error is \$20,000. Evaluating over test sets using RMSE or R² validates real-world generalization."
            }
            sub.contains("artificial intelligence") -> {
                "In computer vision, an image of handwritten digit '8' is fed into neural layers. Gradient descent computes cross-entropy loss against true label and updates weights backwards via backpropagation so the network learns edge and loop detectors."
            }
            sub.contains("physics") -> {
                "Applying Newton's second law F = ma: Accelerating a 1,200 kg car at 2.5 m/s² requires net horizontal engine force F = 1,200 × 2.5 = 3,000 Newtons."
            }
            sub.contains("chemistry") -> {
                "Neutralizing stomach acid (HCl) with antacid magnesium hydroxide Mg(OH)₂: 2 HCl + Mg(OH)₂ → MgCl₂ + 2 H₂O. The hydronium and hydroxide ions combine into water, raising pH towards neutrality."
            }
            sub.contains("biology") -> {
                "In human respiration, hemoglobin binds four O₂ molecules in pulmonary capillaries and releases them in oxygen-depleted peripheral muscle tissues via allosteric cooperative binding."
            }
            sub.contains("world history") || sub.contains("history") -> {
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
        return tokenCache.getOrPut(text) {
            val cleaned = text.replace(Regex("[^a-zA-Z0-9\\s]"), " ")
            cleaned.split("\\s+".toRegex())
                .map { it.trim() }
                .filter { it.length > 1 && !stopWords.contains(it) }
        }
    }

    private fun computeRelevanceScore(
        cleanQuery: String,
        tokens: List<String>,
        chunk: KnowledgeChunk
    ): Double {
        var score = 0.0
        var matchedConceptTokens = 0

        // Filter and stem query tokens
        val validTokens = tokens
            .map { it.lowercase(Locale.ROOT).trim() }
            .filter { it.isNotBlank() && !stopWords.contains(it) }

        if (validTokens.isEmpty()) return 0.0

        val conceptTokens = validTokens.filter { !genericSubjectWords.contains(it) }
        val subjectTokens = validTokens.filter { genericSubjectWords.contains(it) }

        // Normalization & Stemming of Chunk Fields
        val normTopic = chunk.topic.lowercase(Locale.ROOT)
        val normChapter = chunk.chapter.lowercase(Locale.ROOT)
        val normSubject = chunk.subject.lowercase(Locale.ROOT)
        val normDef = chunk.definition.lowercase(Locale.ROOT)
        val normContent = chunk.content.lowercase(Locale.ROOT)
        val normFormula = chunk.formulaOrCoreRule.lowercase(Locale.ROOT)

        val titleRawTokens = (tokenize(normChapter) + tokenize(normTopic)).map { it.lowercase(Locale.ROOT) }
        val titleStems = titleRawTokens.map { stem(it) }.toSet()
        val titleWordsSet = titleRawTokens.toSet()

        val kwPhrases = chunk.keywords.map { it.lowercase(Locale.ROOT).trim() }.toSet()
        val kwStems = kwPhrases.flatMap { tokenize(it) }.map { stem(it) }.toSet()

        val bodyRawTokens = (tokenize(normDef) + tokenize(normContent) + tokenize(normFormula)).map { it.lowercase(Locale.ROOT) }
        val bodyStems = bodyRawTokens.map { stem(it) }.toSet()

        // 1. Specific Concept & Title Matching + Stemming
        for (token in conceptTokens) {
            val tokenStem = stem(token)
            var tokenMatched = false

            // Chapter Title / Topic Name Match: +15 points per matched token
            val matchedTitle = titleWordsSet.contains(token) || titleStems.contains(tokenStem) ||
                    normTopic.contains(token) || normChapter.contains(token) ||
                    normTopic.contains(tokenStem) || normChapter.contains(tokenStem)
            if (matchedTitle) {
                score += 15.0
                tokenMatched = true
            }

            // Keywords / Tags Match: +10 points per matched token
            val matchedKeyword = kwPhrases.contains(token) || kwPhrases.contains(tokenStem) ||
                    kwStems.contains(tokenStem) || kwPhrases.any { it.contains(token) || it.contains(tokenStem) }
            if (matchedKeyword) {
                score += 10.0
                tokenMatched = true
            }

            // Definition / Content Body Match: +3 points per matched token
            val matchedBody = bodyStems.contains(tokenStem) || normDef.contains(token) ||
                    normContent.contains(token) || normFormula.contains(token) ||
                    normDef.contains(tokenStem) || normContent.contains(tokenStem)
            if (matchedBody) {
                score += 3.0
                tokenMatched = true
            }

            if (tokenMatched) {
                matchedConceptTokens++
            }
        }

        // Multi-word phrase bonus (e.g. "wave mechanics", "newton's laws", "f = ma", "eigenvalues")
        for (kw in kwPhrases) {
            if (kw.length >= 4 && cleanQuery.contains(kw) && !genericSubjectWords.contains(kw)) {
                score += 15.0
                matchedConceptTokens++
            }
        }
        if (normTopic.length >= 4 && cleanQuery.contains(normTopic)) {
            score += 20.0
            matchedConceptTokens++
        }

        // 2. Generic Subject Name Match (+1 point tie-breaker ONLY when combined with a concept match)
        if (matchedConceptTokens > 0) {
            val chunkSubjectStem = stem(normSubject)
            for (subToken in subjectTokens) {
                val subTokenStem = stem(subToken)
                if (normSubject == subToken || chunkSubjectStem == subTokenStem || normSubject.contains(subToken)) {
                    score += 1.0 // Tie-breaker only!
                    break
                }
            }
        } else if (conceptTokens.isEmpty() && subjectTokens.isNotEmpty()) {
            // If user only asked about subject with no concept (e.g. "physics"): modest base score
            for (subToken in subjectTokens) {
                if (normSubject.contains(subToken)) {
                    score += 5.0
                    break
                }
            }
        }

        return score
    }

    /**
     * Builds structured conceptual explanation with clear Definition, Formula / Core Principle, and Key Example.
     * Defaults to concise (3-5 lines max) unless elaboration is explicitly requested.
     */
    fun buildEducationalResponse(
        chunk: KnowledgeChunk,
        score: Double,
        isElaborated: Boolean = false,
        isFollowUp: Boolean = false
    ): LocalAiResponse {
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

        val analogyText = generateRealLifeAnalogy(chunk)
        val interviewTipText = generatePlacementInterviewTip(chunk)

        val bulletPrefix = if (useEmojis) "📘 " else "* "
        val formulaPrefix = if (useEmojis) "⚡ " else "* "
        val examplePrefix = if (useEmojis) "💡 " else "* "

        return if (isElaborated) {
            val p1 = "${bulletPrefix}**In-Depth Breakdown: ${chunk.topic}** (${chunk.subject} · ${chunk.chapter})\n\n" +
                    "**1. Theoretical Foundations & Mechanism:**\n" +
                    "${chunk.content}\n\n" +
                    "**2. Real-Life Analogy:**\n" +
                    "$analogyText" +
                    (if (includeFormulas && formulaText.isNotBlank()) "\n\n**3. Mathematical Formulations & Derivations:**\n$formulaText" else "")
            val p2 = buildString {
                if (includeExamples && exampleText.isNotBlank()) {
                    appendLine("**4. Practical Implementation & Code:**")
                    appendLine(exampleText)
                    appendLine()
                }
                appendLine("**5. Placement Interview Tip & Key Takeaway:**")
                append(interviewTipText)
            }.trim()

            LocalAiResponse(
                paragraph1 = p1.trim(),
                paragraph2 = p2,
                matchedSubject = chunk.subject,
                matchedChapter = chunk.chapter,
                matchedTopic = chunk.topic,
                confidenceScore = score,
                isGreeting = false,
                isFollowUp = isFollowUp
            )
        } else {
            // Concise-by-default (3-5 lines max: 1-sentence definition + analogy + key formula/rule + 1 quick example + interview tip)
            val p1 = "${bulletPrefix}**Concept & Definition** (${chunk.subject} · ${chunk.topic})\n$definitionText\n\n${bulletPrefix}**Real-Life Analogy:**\n$analogyText"
            val p2 = buildString {
                if (includeFormulas && formulaText.isNotBlank()) {
                    appendLine("${formulaPrefix}**Formula / Core Principle:** $formulaText")
                }
                if (includeExamples && exampleText.isNotBlank()) {
                    appendLine("${examplePrefix}**Key Example & Practical Takeaway:** $exampleText")
                }
                append("* **Placement Interview Tip & Takeaway:** $interviewTipText")
            }.trim()

            LocalAiResponse(
                paragraph1 = p1,
                paragraph2 = p2,
                matchedSubject = chunk.subject,
                matchedChapter = chunk.chapter,
                matchedTopic = chunk.topic,
                confidenceScore = score,
                isGreeting = false,
                isFollowUp = isFollowUp
            )
        }
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
        val bPrefix = if (useEmojis) "📘 " else "* "
        val fPrefix = if (useEmojis) "⚡ " else "* "
        val ePrefix = if (useEmojis) "💡 " else "* "

        // 1. PYTHON & CODING LOGIC
        if (q.contains("gil") || (q.contains("global") && q.contains("interpreter") && q.contains("lock"))) {
            val p2 = buildString {
                if (includeFormulas) {
                    appendLine("${fPrefix}**Formula / Core Principle**\n\nCPython Architecture: A mutex mechanism that allows only one native thread to execute Python bytecode at a time, preventing race conditions on reference counting garbage collection.\n")
                }
                if (includeExamples) {
                    appendLine("${ePrefix}**Key Example & Practical Takeaway**\nUse `multiprocessing` instead of `threading` for CPU-bound tasks (e.g. image processing) to utilize multiple CPU cores concurrently.")
                }
            }.trim()
            return LocalAiResponse(
                paragraph1 = "${bPrefix}**Concept & Definition** (Python & Coding Logic · Concurrency & Runtime)\n\nThe Global Interpreter Lock (GIL) is a thread-synchronization mechanism used by the standard CPython interpreter. It ensures that only one native thread executes Python bytecode at any given moment, safeguarding Python's internal memory management against concurrency bugs.",
                paragraph2 = p2,
                matchedSubject = "Python & Coding Logic",
                matchedChapter = "Python Memory Model & Internals",
                matchedTopic = "Global Interpreter Lock (GIL)",
                confidenceScore = 95.0
            )
        }

        if (q.contains("mutable") || q.contains("immutable") || (q.contains("list") && q.contains("tuple"))) {
            val p2 = buildString {
                if (includeFormulas) {
                    appendLine("${fPrefix}**Formula / Core Principle**\n\n• Mutable (In-Place Modifications): list, dict, set, bytearray\n• Immutable (Hashable & Fixed): int, float, str, tuple, frozenset\nIdentity Check: `id(a)` remains identical for mutable in-place mutations.\n")
                }
                if (includeExamples) {
                    appendLine("${ePrefix}**Key Example & Practical Takeaway**\nTuples can be used as dictionary keys because they are hashable, whereas lists will raise `TypeError: unhashable type: 'list'`.")
                }
            }.trim()
            return LocalAiResponse(
                paragraph1 = "${bPrefix}**Concept & Definition** (Python & Coding Logic · Core Types & Memory)\n\nIn Python, mutable objects can have their state or contents modified after instantiation without changing their memory identity (`id()`). Immutable objects cannot be altered once allocated; any modification creates an entirely new object in memory.",
                paragraph2 = p2,
                matchedSubject = "Python & Coding Logic",
                matchedChapter = "Data Structures & Memory Model",
                matchedTopic = "Mutable vs Immutable Types",
                confidenceScore = 95.0
            )
        }

        // 2. SQL & DATABASE MANAGEMENT
        if (q.contains("acid") || (q.contains("atomicity") && q.contains("consistency"))) {
            val p2 = buildString {
                if (includeFormulas) {
                    appendLine("${fPrefix}**Formula / Core Principle**\n\n• **A - Atomicity:** All-or-nothing execution via WAL (Write-Ahead Logging).\n• **C - Consistency:** Preserves database constraints (FK, Unique, Check).\n• **I - Isolation:** Prevents dirty reads, non-repeatable reads, and phantom reads.\n• **D - Durability:** Committed data survives crashes and power loss.\n")
                }
                if (includeExamples) {
                    appendLine("${ePrefix}**Key Example & Practical Takeaway**\nBank Transfer: Debiting Account A and Crediting Account B must either both commit or both roll back together to maintain total account balances.")
                }
            }.trim()
            return LocalAiResponse(
                paragraph1 = "${bPrefix}**Concept & Definition** (SQL & Database Management · Transaction Management)\n\nACID is an acronym representing the four cornerstone properties of reliable relational database transactions: Atomicity, Consistency, Isolation, and Durability. These properties ensure transactional data integrity even during concurrent access and hardware crashes.",
                paragraph2 = p2,
                matchedSubject = "SQL & Database Management",
                matchedChapter = "Transactions & Concurrency",
                matchedTopic = "ACID Properties",
                confidenceScore = 95.0
            )
        }

        if (q.contains("join") || (q.contains("inner") && q.contains("outer"))) {
            val p2 = buildString {
                if (includeFormulas) {
                    appendLine("${fPrefix}**Formula / Core Principle**\n\n• `INNER JOIN`: Returns rows with matches in both tables (intersection).\n• `LEFT JOIN`: Returns all rows from left table + matched right table rows (NULL if no match).\n• `FULL JOIN`: Union of left and right joins with NULL padding for non-matches.\n")
                }
                if (includeExamples) {
                    appendLine("${ePrefix}**Key Example & Practical Takeaway**\n`SELECT u.name, o.id FROM Users u LEFT JOIN Orders o ON u.id = o.user_id` preserves all users, even those who haven't placed an order yet.")
                }
            }.trim()
            return LocalAiResponse(
                paragraph1 = "${bPrefix}**Concept & Definition** (SQL & Database Management · Relational Queries)\n\nAn SQL JOIN clause combines rows from two or more tables based on a related common column between them. Joins form the primary relational algebra operation for querying normalized data.",
                paragraph2 = p2,
                matchedSubject = "SQL & Database Management",
                matchedChapter = "Advanced SQL Queries",
                matchedTopic = "SQL Joins",
                confidenceScore = 95.0
            )
        }

        // 3. AI & MODERN TECH CONCEPTS
        if (q.contains("rag") || (q.contains("retrieval") && q.contains("augmented"))) {
            val p2 = buildString {
                if (includeFormulas) {
                    appendLine("${fPrefix}**Formula / Core Principle**\n\nArchitecture: Query → Embedding Model → Vector Search (Cosine Similarity) → Top-K Chunks Retrieved → Context-Injected Prompt → LLM Synthesis → Accurate Answer.\n")
                }
                if (includeExamples) {
                    appendLine("${ePrefix}**Key Example & Practical Takeaway**\nEnterprise search bots retrieve company PDFs from vector databases to answer employee questions with citations instead of hallucinating outdated training data.")
                }
            }.trim()
            return LocalAiResponse(
                paragraph1 = "${bPrefix}**Concept & Definition** (AI & Modern Tech Concepts · Generative AI & LLMs)\n\nRetrieval-Augmented Generation (RAG) is an AI architectural framework that enhances Large Language Model outputs by dynamically retrieving authoritative knowledge from external databases before generating an answer, drastically eliminating hallucinations.",
                paragraph2 = p2,
                matchedSubject = "AI & Modern Tech Concepts",
                matchedChapter = "Generative AI & LLMs",
                matchedTopic = "Retrieval-Augmented Generation (RAG)",
                confidenceScore = 95.0
            )
        }

        // 4. APTITUDE & INTERVIEW PREP
        if (q.contains("time and work") || (q.contains("efficiency") && q.contains("work"))) {
            val p2 = buildString {
                if (includeFormulas) {
                    appendLine("${fPrefix}**Formula / Core Principle**\n\n• Work Done = Efficiency × Time\n• If A takes x days and B takes y days, combined rate = (1/x) + (1/y) = (x+y)/(x*y)\n• Combined Days = (x*y)/(x+y)\n")
                }
                if (includeExamples) {
                    appendLine("${ePrefix}**Key Example & Practical Takeaway**\nIf A completes a job in 12 days and B in 24 days: Combined Days = (12 * 24) / (12 + 24) = 288 / 36 = 8 days.")
                }
            }.trim()
            return LocalAiResponse(
                paragraph1 = "${bPrefix}**Concept & Definition** (Aptitude & Interview Prep · Quantitative Aptitude)\n\nTime and Work problems evaluate rates of task completion and workforce efficiency. Problems are solved using unit-rate methods or LCM total-work models.",
                paragraph2 = p2,
                matchedSubject = "Aptitude & Interview Prep",
                matchedChapter = "Arithmetic & Word Problems",
                matchedTopic = "Time and Work",
                confidenceScore = 95.0
            )
        }

        // Generic academic fallback with exact word boundary verification
        val wordSet = tokens.toSet()
        fun hasWord(vararg words: String) = words.any { wordSet.contains(it) }

        val academicMarkers = listOf("explain", "define", "theorem", "principle", "formula", "hypothesis", "derivation")
        val isExplicitAcademicQuestion = academicMarkers.any { q.contains(it) }

        val detectedSubject = when {
            hasWord("python", "code", "coding", "oop", "tuple", "list", "dict", "decorator", "generator") -> "Python & Coding Logic"
            hasWord("sql", "query", "database", "dbms", "join", "table", "schema", "acid", "index") -> "SQL & Database Management"
            hasWord("ai", "tech", "model", "neural", "llm", "rag", "transformer", "cloud", "security") -> "AI & Modern Tech Concepts"
            hasWord("aptitude", "speed", "work", "distance", "ratio", "percentage", "probability", "reasoning", "puzzle", "interview") -> "Aptitude & Interview Prep"
            else -> null
        }

        if (detectedSubject != null && isExplicitAcademicQuestion) {
            val cleanTopic = query.replace(Regex("(?i)^(explain|what is|how does|define|tell me about)\\s+"), "").trim()
            val p2 = buildString {
                if (includeFormulas) {
                    appendLine("${fPrefix}**Formula / Core Principle**\n\n1. Master core definition, time/space complexity, and boundary conditions.\n2. Apply the canonical design patterns established in $detectedSubject.\n3. Review placement interview edge cases and trade-offs.\n")
                }
                if (includeExamples) {
                    appendLine("${ePrefix}**Key Example & Practical Takeaway**\nMastering $cleanTopic builds strong conceptual clarity for technical coding assessments and recruitment interviews.")
                }
            }.trim()
            return LocalAiResponse(
                paragraph1 = "${bPrefix}**Concept & Definition** ($detectedSubject · $cleanTopic)\n\n$cleanTopic is an essential concept within $detectedSubject. Understanding its underlying mechanics allows candidates to excel in technical assessments and practical software engineering.",
                paragraph2 = p2,
                matchedSubject = detectedSubject,
                matchedChapter = "Core Concepts",
                matchedTopic = cleanTopic,
                confidenceScore = 80.0
            )
        }

        return null
    }

    /**
     * Builds structured academic guidance for open-ended or unfamiliar questions without ever showing an error.
     */
    private fun buildAcademicGuidanceResponse(query: String, tokens: List<String> = emptyList()): LocalAiResponse {
        val cleanTopic = query
            .replace(Regex("(?i)^(what is|explain|tell me about|how does|what are|define|how do|why is|why do|can you explain)\\s+"), "")
            .replace(Regex("[^a-zA-Z0-9\\s]"), " ")
            .trim()

        if (cleanTopic.length > 2 && tokens.isNotEmpty()) {
            val capitalizedTopic = cleanTopic.split(Regex("\\s+"))
                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }

            val p1 = "📘 **Career & Placement Prep: $capitalizedTopic**\n\n" +
                    "$capitalizedTopic is a high-yield concept in technical campus placements and engineering interview assessments. Mastering its theoretical intuition, memory tradeoffs, and edge cases is essential for technical coding and screening rounds."
            val p2 = "⚡ **Core Study Framework & Key Principles:**\n\n" +
                    "• **Theoretical Foundations:** Understand the core definition, time/space complexity, and syntax rules for $capitalizedTopic.\n" +
                    "• **Code & Syntax Implementation:** Write clean, modular, and optimized code handling edge cases.\n" +
                    "• **Interview Focus:** Be prepared to explain trade-offs and alternative approaches during technical interviews.\n\n" +
                    "💡 *Study Suggestion:* Ask me *'Test me on $capitalizedTopic with a 10-question quiz'* or *'Give me a code example for $capitalizedTopic'*!"

            return LocalAiResponse(
                paragraph1 = p1,
                paragraph2 = p2,
                matchedSubject = "Career & Placement Tracks",
                matchedChapter = capitalizedTopic,
                matchedTopic = capitalizedTopic,
                confidenceScore = 75.0,
                isGreeting = false
            )
        }

        val p1 = "🎓 **SYNAPSE AI Placement & Career Companion**\n\n" +
                "I am your intelligent career companion, equipped with comprehensive study materials spanning 4 high-impact career tracks: Python & Coding Logic, SQL & Database Management, AI & Modern Tech Concepts, and Aptitude & Interview Prep."
        val p2 = "⚡ **Here is How We Can Study Right Now:**\n\n" +
                "• **Master Specific Concepts:** Ask e.g. *\"Explain Python GIL\"*, *\"What is B-Tree Indexing in SQL?\"*, *\"How does RAG work?\"*, or *\"Speed math shortcuts for Time & Work\"*\n" +
                "• **In-Chat 10-Question Quizzes:** Say *\"Test me on Python\"* or *\"Start a 10-question SQL quiz\"* for instant practice and auto-grading.\n" +
                "• **High-Yield Smart Notes:** Ask me to generate revision notes and formula cheat-sheets for any topic.\n\n" +
                "Which track or concept would you like to explore?"

        return LocalAiResponse(
            paragraph1 = p1,
            paragraph2 = p2,
            confidenceScore = 60.0,
            isGreeting = false
        )
    }

    /**
     * Retrieves the most relevant local curriculum knowledge chunks for a given query,
     * formatted as a clean context block for On-Device Small Language Model (SLM) prompting.
     */
    fun retrieveContext(userQuery: String): String? {
        val clean = userQuery.lowercase(Locale.ROOT)
        val tokens = tokenize(clean)
        if (tokens.isEmpty()) return null

        val scored = synchronized(chunks) {
            chunks.map { chunk ->
                Pair(computeRelevanceScore(clean, tokens, chunk), chunk)
            }
        }

        val topChunks = scored.filter { it.first >= 4.0 }.sortedByDescending { it.first }.take(2)
        if (topChunks.isEmpty()) return null

        return topChunks.joinToString("\n\n---\n\n") { (_, c) ->
            "Subject: ${c.subject}\nChapter: ${c.chapter}\nTopic: ${c.topic}\nDefinition: ${c.definition}\nRule/Formula: ${c.formulaOrCoreRule}\nContent: ${c.content}"
        }
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
        val chapterStems = chapterTokens.map { stem(it) }.toSet()

        val matchedChunk = synchronized(chunks) {
            chunks.firstOrNull { c ->
                val normCh = c.chapter.lowercase(Locale.ROOT)
                val normTop = c.topic.lowercase(Locale.ROOT)
                (normCh.isNotBlank() && (lowerChapter.contains(normCh) || normCh.contains(lowerChapter) || stem(normCh) == stem(lowerChapter))) ||
                (normTop.isNotBlank() && (lowerChapter.contains(normTop) || normTop.contains(lowerChapter) || stem(normTop) == stem(lowerChapter))) ||
                c.keywords.any { kw -> chapterTokens.contains(kw.lowercase(Locale.ROOT)) || chapterStems.contains(stem(kw)) }
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
            "Foundational Theory: Master the rigorous definition of $chapterTitle, key axioms, and underlying theoretical constraints.",
            "Quantitative Mastery: Memorize and practice applying the governing equations and operational formulas to multi-step numerical calculations.",
            "Exam & Viva Scoring: State all underlying hypotheses explicitly, review real-world applications, and systematically verify dimensional consistency."
        )

        val md = buildString {
            appendLine("# $chapterTitle - University Smart Notes")
            appendLine("**Subject:** $subjectName\n")
            appendLine("### 📘 Chapter Executive Summary & Key Definition")
            appendLine(definition)
            appendLine()
            appendLine("### ⚡ Key Equations / Rules Box (Core Formulas & Laws)")
            formulas.forEach { appendLine("• $it") }
            appendLine()
            appendLine("### 💡 High-Yield Exam Takeaways & Revision Points")
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
        const val OUT_OF_SCOPE_RESPONSE =
            "That falls outside my placement and technical scope. I'm SYNAPSE AI—built specifically to help you master Python, SQL, Modern AI concepts, and Placement Aptitude!"

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

        fun registerChunks(newChunks: List<KnowledgeChunk>) {
            getInstance().registerChunks(newChunks)
        }
    }
}
