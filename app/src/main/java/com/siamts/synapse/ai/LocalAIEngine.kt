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
        "data"
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

        // 4. STAGE 4: Explicit Out-of-Scope & Casual Slang Guardrails
        if (isOutOfScopeQuery(normalized)) {
            consecutiveUnclearCount = 0
            return buildOutOfScopeResponse()
        }

        val casualSlangResp = checkCasualSlangIntent(lowerQuery, normalized)
        if (casualSlangResp != null) {
            return casualSlangResp
        }

        // 4B. STAGE 4B: Subject Overview Intent ("what about biology", "uses of math", etc.)
        val subjectOverviewResp = checkSubjectOverviewIntent(lowerQuery, normalized)
        if (subjectOverviewResp != null) {
            consecutiveUnclearCount = 0
            return subjectOverviewResp
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
            paragraph1 = "I am SYNAPSE AI, your friendly study companion! I help you master 50+ university subjects across Engineering, Science, and Humanities with clear concept explanations, interactive quizzes, and smart revision notes. Ask me any study doubt, or tell me to launch a quiz or save notes!",
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
                "How about a quick 3-minute quiz or learning a fascinating fact in science or tech? Pick a subject and let's make it fun!"
            normalized.contains("stressed about exams") || normalized.contains("i am stressed") || normalized.contains("im stressed") || normalized.contains("exam stress") || normalized == "stressed" ->
                "Take a deep breath! You've got this. Let's break your revision down into bite-sized 10-minute topics so you feel confident and in control."
            normalized.contains("motivate me") || normalized.contains("give me motivation") || normalized.contains("motivation") ->
                "Every expert was once a beginner who refused to quit. Focus on mastering just one concept right now—progress adds up fast!"
            normalized.contains("who made you") || normalized.contains("who created you") || normalized.contains("who built you") ->
                "I was built by the SYNAPSE AI team to be your personal university study companion and learning buddy!"
            normalized.contains("are you smart") || normalized.contains("how smart are you") ->
                "I know 50+ college subjects inside and out, but my favorite thing is helping you become the smartest person in the room!"
            normalized.contains("hello friend") || normalized.contains("hi friend") || normalized.contains("hey friend") ->
                "Hey friend! Great to see you. What subject shall we dive into today?"
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
                "Okay, bye, $name! We'll continue our studies tomorrow. Take care!"
            } else {
                "Okay, bye! We'll continue our studies tomorrow. Take care!"
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
        val clean = lowerQuery.replace(Regex("[^a-zA-Z0-9\\s]"), " ").trim()
        val norm = normalized.trim().lowercase(Locale.ROOT)

        val overviewRegex = Regex("^(?:what\\s+about|what\\s+abou|about|tell\\s+me\\s+about|uses\\s+of|use\\s+of|advantages\\s+of|advantage\\s+of|benefits\\s+of|overview\\s+of|explain\\s+about|scope\\s+of|details\\s+of)\\s+([a-zA-Z\\s]+)$")
        val match = overviewRegex.find(clean)

        var candidateSubject = match?.groupValues?.getOrNull(1)?.trim()?.lowercase(Locale.ROOT)

        if (candidateSubject.isNullOrBlank()) {
            val standaloneSubjects = setOf(
                "math", "maths", "mathematics", "computer science", "cs", "data science", "ds",
                "artificial intelligence", "ai", "machine learning", "ml", "deep learning",
                "physics", "chemistry", "biology", "bio", "history", "world history",
                "economics", "finance", "environmental science", "ecology", "computer networks",
                "networking", "cyber security", "cybersecurity", "security", "psychology",
                "business", "business management", "electrical engineering", "astronomy",
                "astrophysics", "statistics", "software engineering", "cloud computing",
                "operating systems", "dbms", "database", "web development", "mobile development",
                "blockchain", "robotics", "mechanical engineering", "civil engineering",
                "biotechnology", "genetics", "neuroscience", "organic chemistry", "quantum physics",
                "calculus", "linear algebra", "discrete mathematics", "philosophy", "sociology",
                "political science", "literature", "accounting", "marketing", "cryptography",
                "information technology", "architecture", "aerospace engineering",
                "chemical engineering", "biomedical engineering", "nanotechnology",
                "supply chain", "supply chain management", "law"
            )
            if (norm in standaloneSubjects) {
                candidateSubject = norm
            }
        }

        if (candidateSubject.isNullOrBlank()) return null

        val info = resolveSubjectOverview(candidateSubject) ?: return null

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

    private fun resolveSubjectOverview(candidate: String): SubjectOverviewInfo? {
        val key = candidate.trim().lowercase(Locale.ROOT)
        val overviewMap = mapOf(
            "mathematics" to SubjectOverviewInfo(
                "Mathematics",
                "Mathematics is the foundational science of structure, quantity, space, and change, studying patterns through rigorous logical deduction and formal proofs.",
                "Used across machine learning algorithms, quantitative finance, structural and aerospace engineering, cryptography, data science, and physics simulations.",
                "Builds unparalleled analytical rigor, sharp abstract problem-solving, and universally transferable reasoning applied across all STEM fields.",
                "High abstraction levels, complex prerequisite chains, and unforgiving precision requirements where minor computational errors alter entire proofs."
            ),
            "computer science" to SubjectOverviewInfo(
                "Computer Science",
                "Computer Science is the systematic study of computation, algorithmic problem-solving, data structures, software architecture, and the interaction between hardware and software.",
                "Powering modern software engineering, operating systems, cloud infrastructures, distributed web systems, cybersecurity, and artificial intelligence platforms.",
                "Massive industry demand, high scalability of digital solutions, rapid feedback cycles, and endless creative engineering possibilities.",
                "Fast-evolving technological landscape requiring constant continuous learning, intricate software debugging, and complex system concurrency issues."
            ),
            "data science" to SubjectOverviewInfo(
                "Data Science",
                "Data Science is an interdisciplinary field combining statistics, machine learning, and domain knowledge to extract actionable insights and predictive intelligence from structured and unstructured data.",
                "Business intelligence, algorithmic trading, customer behavioral modeling, recommendation engines (Netflix/Spotify), healthcare diagnostics, and supply chain forecasting.",
                "Data-driven decision making, high industry valuation, and powerful synergy across business strategy and machine intelligence.",
                "Noisy and messy real-world data, risks of statistical bias and over-fitting, and complex data pipeline maintenance."
            ),
            "artificial intelligence" to SubjectOverviewInfo(
                "Artificial Intelligence",
                "Artificial Intelligence is the branch of computer science dedicated to engineering intelligent agents that simulate cognitive capabilities like learning, pattern recognition, decision-making, and natural language understanding.",
                "Large language models, autonomous self-driving vehicles, medical imaging diagnosis, automated robotics, real-time speech translation, and intelligent game theory.",
                "Automates complex cognitive workflows, enables superhuman processing of massive data, and drives groundbreaking innovations across every scientific domain.",
                "High computational and GPU power costs, 'black-box' explainability challenges, hallucination risks, and complex ethical and alignment considerations."
            ),
            "physics" to SubjectOverviewInfo(
                "Physics",
                "Physics is the fundamental natural science that investigates matter, energy, space, time, and the core physical laws that govern the universe from subatomic particles to cosmic scales.",
                "Semiconductor hardware design, aerospace and satellite navigation, quantum computing, renewable energy systems, laser optics, and medical radiation technology.",
                "Provides the deepest mechanistic understanding of physical reality, first-principles thinking, and universal mathematical modeling skills.",
                "Demanding mathematical machinery (multivariable calculus, tensor algebra, differential equations) and unintuitive counter-intuitive quantum concepts."
            ),
            "chemistry" to SubjectOverviewInfo(
                "Chemistry",
                "Chemistry is the central science studying the composition, molecular structure, properties, and reactive transformations of matter at the atomic scale.",
                "Pharmaceutical drug discovery, battery technology and energy storage, materials engineering, polymers, petrochemicals, and agricultural fertilizers.",
                "Direct tangible impact on medicine and green energy, bridging physics and biology, and high industrial manufacturing relevance.",
                "Extensive memorization of reaction pathways, complex spatial 3D stereochemistry, and hazardous laboratory handling protocols."
            ),
            "biology" to SubjectOverviewInfo(
                "Biology",
                "Biology is the natural science dedicated to investigating living organisms, covering cellular processes, physiological systems, genetic inheritance, evolutionary history, and ecological interactions.",
                "Biomedical research, genomic medicine and CRISPR gene editing, vaccine engineering, ecological conservation, agricultural crop improvement, and forensic pathology.",
                "Directly advances human health and disease prevention, explains the living natural world, and drives multi-billion dollar biotechnology innovations.",
                "High conceptual complexity with endless biological exceptions, massive terminology memorization, and long experimental wet-lab incubation periods."
            ),
            "history" to SubjectOverviewInfo(
                "World History",
                "History is the academic study of past human societies, civilizations, cultural revolutions, governance systems, and geopolitical transformations across time.",
                "Foreign policy and diplomatic strategy, legal jurisprudence, historical document archiving, geopolitical risk assessment, journalism, and public governance.",
                "Contextualizes modern global politics, refines critical source evaluation, and teaches pattern recognition across human socio-economic cycles.",
                "Subjective source interpretations, historiographical biases, and reliance on qualitative archival evidence rather than repeatable experiments."
            ),
            "economics" to SubjectOverviewInfo(
                "Economics & Finance",
                "Economics and Finance analyze the production, distribution, and consumption of scarce resources, examining individual decision-making, market mechanisms, and monetary policy.",
                "Central banking, corporate financial strategy, investment portfolio management, public policy design, market pricing models, and international trade treaties.",
                "High-earning career paths in investment banking and consulting, quantitative financial literacy, and deep understanding of global market dynamics.",
                "Economic models depend heavily on simplifying behavioral assumptions, market volatility, and unpredictable geopolitical shocks."
            ),
            "environmental science" to SubjectOverviewInfo(
                "Environmental Science",
                "Environmental Science is an integrative discipline examining ecological systems, climate dynamics, human environmental impact, and sustainable natural resource management.",
                "Carbon emissions auditing, renewable energy transition, wildlife conservation planning, environmental impact assessments (EIA), and climate policy advocacy.",
                "Addresses the world's most critical sustainability challenges, clean energy innovations, and growing environmental regulatory compliance demand.",
                "Complex multi-variable climate feedback loops, political polarization surrounding climate policy, and high friction in global treaty enforcement."
            ),
            "computer networks" to SubjectOverviewInfo(
                "Computer Networks",
                "Computer Networks explores the physical and logical communication architectures that interconnect computing devices, transmitting data packets through structured protocol layers.",
                "Internet routing, cloud data center architectures, 5G wireless telecommunications, Content Delivery Networks (CDNs), and enterprise VPNs.",
                "Essential foundation for all connected devices and web systems, stable enterprise infrastructure roles, and vital knowledge for cloud scale.",
                "Complex packet loss and latency troubleshooting, distributed failure modes, and constant threat of network-level security attacks."
            ),
            "cyber security" to SubjectOverviewInfo(
                "Cyber Security",
                "Cyber Security is the practice of safeguarding digital architectures, servers, endpoints, networks, and confidential data from malicious intrusions, ransomware, and vulnerabilities.",
                "Penetration testing and ethical hacking, security operations centers (SOC), secure cryptographic protocols, vulnerability patching, and compliance auditing.",
                "Massive global talent shortage, high compensation, mission-critical organizational impact, and engaging adversarial problem solving.",
                "Asymmetric battlefield (defenders must block all attacks, attackers need one gap), high burnout stress during active breaches, and relentless evolving threats."
            ),
            "psychology" to SubjectOverviewInfo(
                "Psychology",
                "Psychology is the scientific study of the mind and human behavior, examining cognitive perception, emotional regulation, neurobiology, and interpersonal social dynamics.",
                "Clinical psychotherapy and mental health, User Experience (UX) product design, behavioral economics, organizational leadership, and marketing psychology.",
                "Improves human understanding and emotional intelligence, enhances user-centered software design, and vital for mental health therapy.",
                "High individual behavioral variance, replication crises in experimental psychological studies, and emotional fatigue in clinical therapy roles."
            ),
            "business" to SubjectOverviewInfo(
                "Business Management",
                "Business Management focuses on coordinating organizational resources, strategic planning, human capital, and operational execution to achieve sustainable commercial success.",
                "Corporate strategy, startup scaling and venture capital, operational supply chains, product management, human resources, and marketing execution.",
                "Broad executive career mobility, foundational leadership skills, and universal applicability across private enterprises and non-profits.",
                "High dependency on market competition, interpersonal corporate friction, and high organizational accountability during revenue downturns."
            ),
            "electrical engineering" to SubjectOverviewInfo(
                "Electrical Engineering",
                "Electrical Engineering investigates the physics and practical engineering of electricity, electromagnetism, analog/digital electronics, power systems, and signal processing.",
                "Microchip (VLSI) design, electric vehicle powertrains, power grid transmission, wireless telecommunications, and IoT embedded microcontrollers.",
                "Powers the hardware backbone of modern civilization, strong cross-discipline hardware-software synergy, and protected engineering licensing.",
                "Steep physical mathematics and electromagnetic field equations, expensive hardware prototyping cycles, and physical electrical hazards."
            ),
            "astronomy" to SubjectOverviewInfo(
                "Astronomy & Astrophysics",
                "Astronomy and Astrophysics explore celestial bodies, stellar life cycles, black holes, galactic structures, dark energy, and the cosmological origins of the universe.",
                "Deep-space satellite exploration, cosmic radiation monitoring, orbital trajectory planning for space agencies, and advanced spectroscopy imaging.",
                "Expands human knowledge of cosmic origins, pushes the limits of optical and radio sensors, and inspires visionary scientific exploration.",
                "Inability to conduct hands-on laboratory experiments on celestial targets, scarce academic research positions, and reliance on massive space telescopes."
            ),
            "statistics" to SubjectOverviewInfo(
                "Statistics",
                "Statistics is the mathematical science of collecting, exploring, organizing, interpreting, and presenting empirical numerical data under uncertainty.",
                "Clinical medical trials, A/B testing in tech products, actuarial insurance modeling, econometric forecasting, and quality assurance engineering.",
                "Critical backbone for all empirical research and data science, high analytical demand, and objectivity in scientific conclusions.",
                "Vulnerable to misinterpretation ('p-hacking', false correlations), abstract probability theory, and sensitive to non-representative sample sizes."
            ),
            "software engineering" to SubjectOverviewInfo(
                "Software Engineering",
                "Software Engineering is the disciplined engineering approach to the design, implementation, testing, maintenance, and lifecycle management of reliable software systems.",
                "Enterprise application suites, automated CI/CD pipelines, distributed microservices, operating system kernels, and high-frequency trading platforms.",
                "Instant global distribution of software products, high salaries, agile workflow feedback, and massive economic leverage.",
                "Accumulation of technical debt, complex legacy codebases, and frequent production outages requiring high-stakes triage."
            ),
            "cloud computing" to SubjectOverviewInfo(
                "Cloud Computing & DevOps",
                "Cloud Computing provides on-demand computing power, storage, and networking over the internet, while DevOps unifies software development and IT infrastructure operations.",
                "Containerized deployments (Kubernetes/Docker), serverless backends, auto-scaling web architectures, infrastructure as code (Terraform), and disaster recovery.",
                "Eliminates physical data centers, scales apps effortlessly to millions of users, and reduces operational maintenance overhead.",
                "Complex multi-cloud pricing and runaway billing costs, shared-responsibility security models, and distributed microservice network outages."
            ),
            "operating systems" to SubjectOverviewInfo(
                "Operating Systems",
                "Operating Systems is the study of core system software that manages hardware resources, processes, memory allocation, storage filesystems, and security abstractions.",
                "Linux and Unix kernel development, real-time operating systems (RTOS) in aviation/robotics, hypervisors (KVM), and mobile platforms (Android/iOS).",
                "Reveals how computers truly execute code at the lowest level, optimizes hardware efficiency, and foundational for system programmers.",
                "Hard-to-reproduce race conditions and deadlocks, memory corruption bugs in low-level languages (C/Rust), and unforgiving kernel panics."
            ),
            "database" to SubjectOverviewInfo(
                "Database Management Systems (DBMS)",
                "DBMS focuses on software systems that store, retrieve, index, and organize structured and unstructured data while ensuring ACID transaction guarantees.",
                "Relational SQL banking ledgers, NoSQL distributed key-value stores (Redis/Cassandra), vector databases for LLMs, and analytical data warehouses.",
                "Guarantees mission-critical data integrity, enables sub-millisecond query execution over petabytes, and essential for every application.",
                "Distributed consensus (CAP theorem trade-offs), complex query execution plan tuning, and catastrophic database migration risks."
            ),
            "web development" to SubjectOverviewInfo(
                "Web Development",
                "Web Development involves building interactive websites and web applications across client-side frontend interfaces, server-side backends, and APIs.",
                "SaaS software products, modern e-commerce storefronts, interactive web portals, responsive progressive web apps (PWAs), and REST/GraphQL APIs.",
                "Lowest barrier to entry for building working apps, instant cross-platform browser accessibility, and huge freelance/job market.",
                "Rapidly changing frontend framework churn, cross-browser compatibility quirks, and frontend state management complexity."
            ),
            "mobile development" to SubjectOverviewInfo(
                "Mobile App Development",
                "Mobile App Development focuses on engineering applications optimized for mobile operating systems, touch interfaces, and portable embedded sensors.",
                "Consumer smartphone apps, on-demand ride/food delivery logistics, mobile banking apps, wearable health sensors, and mobile games.",
                "Direct access to billions of active smartphone users, rich device sensors (GPS/Camera/Biometrics), and high engagement.",
                "Strict app store review delays, device fragmentation (varying screen sizes and Android OS versions), and battery consumption constraints."
            ),
            "blockchain" to SubjectOverviewInfo(
                "Blockchain & Cryptography",
                "Blockchain is a decentralized, cryptographically secure distributed ledger system that records transactions across peer-to-peer nodes without central authorities.",
                "Decentralized finance (DeFi), immutable audit trails, smart contracts (Ethereum), zero-knowledge identity verification, and supply chain provenance.",
                "Trustless peer-to-peer verification, resistance to centralized censorship and tampering, and automated smart contract execution.",
                "High transaction latency and throughput bottlenecks, irreversible code vulnerability exploits in smart contracts, and regulatory uncertainty."
            ),
            "robotics" to SubjectOverviewInfo(
                "Robotics",
                "Robotics combines mechanical engineering, electronics, computer science, and control theory to design autonomous physical machines that sense and interact with the physical world.",
                "Automated factory assembly lines, robotic surgery assistants, warehouse automation (Amazon Kiva), interplanetary rovers, and humanoid robots.",
                "Replaces dangerous human labor in hazardous environments, increases manufacturing precision and speed, and embodies physical AI.",
                "High hardware prototyping costs, actuator mechanical wear and tear, and complex real-time sensory perception under physical noise."
            ),
            "mechanical engineering" to SubjectOverviewInfo(
                "Mechanical Engineering",
                "Mechanical Engineering applies physics, materials science, and mathematics to design, analyze, manufacture, and maintain mechanical systems and thermal machinery.",
                "Automotive engine and chassis design, aircraft turbines, HVAC thermal regulation, industrial tooling, and renewable wind turbines.",
                "One of the broadest and most durable engineering disciplines, tangible physical creation, and foundational to manufacturing industries.",
                "Long manufacturing lead times, strict safety certifications where failures cause physical catastrophe, and material fatigue issues."
            ),
            "civil engineering" to SubjectOverviewInfo(
                "Civil Engineering",
                "Civil Engineering is the branch of engineering that designs, constructs, and maintains physical infrastructure including bridges, highways, dams, tunnels, and skyscrapers.",
                "Urban transportation networks, earthquake-resilient architectural design, municipal water supply and sewage, and mega-structural bridges.",
                "Direct contribution to community infrastructure and civilization building, stable public and private employment, and enduring physical legacies.",
                "Enormous capital budgets with heavy bureaucratic and environmental oversight, high liability risk for structural failures, and slow adoption of new construction tech."
            ),
            "biotechnology" to SubjectOverviewInfo(
                "Biotechnology",
                "Biotechnology utilizes biological systems, cellular machinery, and living organisms to create innovative medical treatments, industrial enzymes, and agricultural products.",
                "Monoclonal antibody synthesis, synthetic insulin production, biodegradable bioplastics, bio-fuels, and drought-resistant genetically modified crops.",
                "Revolutionary potential to treat previously incurable diseases, creates sustainable green bio-alternatives, and high financial investment.",
                "Extremely long FDA clinical trial timelines (10+ years), massive research capital costs, and complex bioethical regulations."
            ),
            "genetics" to SubjectOverviewInfo(
                "Genetics & Genomics",
                "Genetics is the scientific study of heredity, genes, genomic sequencing, and genetic variation in living organisms.",
                "Personalized medicine, prenatal genetic screening, CRISPR base editing, agricultural selective breeding, and hereditary cancer risk profiling.",
                "Decodes the fundamental blueprint of life, unlocks targeted therapies, and revolutionizes forensic investigations.",
                "Ethical debates surrounding germline gene modifications, gene patenting disputes, and complex polygenic interactions."
            ),
            "neuroscience" to SubjectOverviewInfo(
                "Neuroscience",
                "Neuroscience is the multidisciplinary science studying the nervous system and brain architecture, from cellular synaptic firing to cognitive brain networks.",
                "Brain-Computer Interfaces (Neuralink), Alzheimer's and Parkinson's therapeutics, neuro-imaging (fMRI), and biologically inspired AI algorithms.",
                "Tackles the ultimate mystery of human consciousness and memory, with profound applications in mental health and cognitive restoration.",
                "Incredible biological complexity with billions of interconnected neurons, limited non-invasive measurement precision, and elusive neural decoding."
            ),
            "organic chemistry" to SubjectOverviewInfo(
                "Organic Chemistry",
                "Organic Chemistry is the study of the structure, properties, composition, reactions, and synthesis of carbon-based compounds.",
                "Small-molecule pharmaceutical synthesis, petrochemical refining, specialty polymers, agrochemicals, and natural product extraction.",
                "Indispensable molecular foundation for all drug development, paints, plastics, and biochemistry.",
                "Challenging reaction mechanism pathways, stereochemical chirality puzzles, and hazardous laboratory reagents."
            ),
            "quantum physics" to SubjectOverviewInfo(
                "Quantum Physics",
                "Quantum Physics investigates physical behavior at atomic and subatomic scales where energy, momentum, and angular momentum are restricted to quantized values.",
                "Quantum computing (qubits), quantum key distribution (QKD cryptography), atomic clocks for GPS, and laser diode spectroscopy.",
                "Unlocks the deepest physical insights of our universe and drives exponential advances in quantum information processing.",
                "Paradoxical phenomena (superposition, entanglement) that defy classical intuition, and extreme quantum decoherence challenges in hardware."
            ),
            "calculus" to SubjectOverviewInfo(
                "Calculus",
                "Calculus is the mathematical branch that studies continuous change, split into differential calculus (rates of change) and integral calculus (accumulation of quantities).",
                "Gradient descent in machine learning, physical trajectory computation, optimization of economic profit, and signal processing transforms.",
                "The foundational language of all physical and computational optimization, enabling modeling of dynamic continuous systems.",
                "High barrier to entry with limit definitions, integration technique memorization, and rigorous multivariable coordinate conversions."
            ),
            "linear algebra" to SubjectOverviewInfo(
                "Linear Algebra",
                "Linear Algebra is the branch of mathematics focusing on vector spaces, linear transformations, matrices, and systems of linear equations.",
                "3D computer graphics transformations, neural network weight tensors, Google PageRank, quantum state representations, and PCA dimensionality reduction.",
                "Compact representation of high-dimensional data, computationally efficient on modern GPUs, and the mathematical heart of AI.",
                "Abstract geometric transformations in n-dimensional spaces, unintuitive eigenvalue/eigenvector concepts, and matrix condition number instabilities."
            ),
            "discrete mathematics" to SubjectOverviewInfo(
                "Discrete Mathematics",
                "Discrete Mathematics is the study of mathematical structures that are fundamentally countable and distinct rather than continuous.",
                "Graph theory for social network modeling, relational database query optimization, formal logic in compiler verification, and cryptographic modular arithmetic.",
                "Essential theoretical pillar of computer science algorithms, proof techniques (induction, contradiction), and computational complexity theory.",
                "Requires rigorous combinatorial reasoning, formal symbolic logic manipulation, and absence of smooth calculus approximations."
            ),
            "philosophy" to SubjectOverviewInfo(
                "Philosophy & Ethics",
                "Philosophy explores fundamental questions regarding existence, knowledge, ethics, logic, mind, and the principles of rational discourse.",
                "AI alignment and safety ethics, legal reasoning, political constitutional theory, bioethics review boards, and logic formalization.",
                "Sharpens flawless critical thinking, clarifies core assumptions, and navigates moral dilemmas in rapidly advancing technological eras.",
                "Absence of empirical consensus, highly abstract metaphysical debates, and frequently misunderstood as having little direct commercial output."
            ),
            "sociology" to SubjectOverviewInfo(
                "Sociology",
                "Sociology is the scientific study of human society, social relationships, collective behavior, social stratification, and institutional dynamics.",
                "Public policy design, urban community planning, demographic census analysis, market research, and corporate social responsibility (CSR).",
                "Exposes systemic social patterns and inequalities, fosters empathy, and informs progressive governance and community initiatives.",
                "Difficulty isolating confounding variables in complex populations, ideological debates, and qualitative research methodology disputes."
            ),
            "political science" to SubjectOverviewInfo(
                "Political Science",
                "Political Science investigates government systems, political behavior, public policies, electoral mechanisms, and international relations.",
                "Electoral campaign strategy, legislative drafting, diplomatic negotiations, geopolitical intelligence, and public administration.",
                "Demystifies governmental power structures, prepares for legal and diplomatic leadership, and protects democratic processes.",
                "High polarization in contemporary politics, volatile voter sentiment, and gap between theoretical policy design and real-world implementation."
            ),
            "literature" to SubjectOverviewInfo(
                "English Literature",
                "English Literature studies written works of artistic value across cultural eras, analyzing narrative technique, thematic depth, rhetoric, and cultural commentary.",
                "Professional copywriting, narrative writing in gaming/film, literary publishing, communications management, and content strategy.",
                "Cultivates exceptional written expression, emotional empathy, and powerful rhetorical persuasion.",
                "Subjective grading and critical reception, shifting publishing markets, and intense competition in creative writing industries."
            ),
            "accounting" to SubjectOverviewInfo(
                "Accounting",
                "Accounting is the measurement, processing, and communication of financial and non-financial information about economic entities.",
                "Statutory corporate tax preparation, external financial auditing (Big 4), forensic fraud investigation, and managerial budgeting.",
                "Rock-solid job stability, protected professional designations (CPA/CA), and foundational transparency for capital markets.",
                "Strict regulatory compliance rules, repetitive manual ledger entries, and intense overtime during quarterly tax seasons."
            ),
            "marketing" to SubjectOverviewInfo(
                "Marketing",
                "Marketing is the business process of creating, communicating, delivering, and exchanging offerings that have value for customers, clients, and society.",
                "Digital SEO/SEM customer acquisition, brand storytelling, consumer conversion funnels, market segmentation, and viral social media campaigns.",
                "Direct driver of corporate revenue, creative and data-driven duality, and fast feedback from customer purchasing habits.",
                "High platform ad cost volatility (Google/Meta), consumer ad fatigue, and difficulty proving long-term brand equity ROI."
            ),
            "information technology" to SubjectOverviewInfo(
                "Information Technology",
                "Information Technology focuses on the deployment, administration, security, and maintenance of computer systems, networks, and business software suites.",
                "Enterprise IT support, identity management (Active Directory), corporate network security, systems administration, and hardware provisioning.",
                "Universal demand across every modern business, clear certification career pathways (CompTIA, Cisco), and reliable career progression.",
                "Repetitive ticket troubleshooting, sudden emergency outages outside business hours, and managing non-technical end-user expectations."
            ),
            "architecture" to SubjectOverviewInfo(
                "Architecture",
                "Architecture is the art and science of designing and erecting buildings, balancing aesthetic appeal, structural integrity, and human functionality.",
                "Sustainable green building design, urban residential housing, commercial skyscraper blueprints, and historical preservation.",
                "Leaves enduring aesthetic and physical monuments, combines creative artistry with engineering physics, and transforms city skylines.",
                "Lengthy multi-year licensure requirements, demanding client revision cycles, and strict zoning code constraints."
            ),
            "aerospace engineering" to SubjectOverviewInfo(
                "Aerospace Engineering",
                "Aerospace Engineering is the primary field of engineering concerned with the development of aircraft, spacecraft, satellites, and propulsion missiles.",
                "Commercial passenger aircraft design, orbital space launch vehicles (SpaceX), aerodynamic simulations, and satellite constellation tracking.",
                "Pushes the physical frontier of human exploration, high prestige, and cutting-edge aerodynamics and materials science.",
                "Zero tolerance for structural or computational failure, multi-decade design cycles, and strict national defense security clearances."
            ),
            "chemical engineering" to SubjectOverviewInfo(
                "Chemical Engineering",
                "Chemical Engineering applies physical sciences and life sciences together with mathematics and economics to convert chemicals and raw materials into valuable products at industrial scale.",
                "Large-scale petroleum refining, pharmaceutical batch production, water desalination plants, and industrial food processing.",
                "High starting salaries, mastery of industrial scaling from test-tube to factory, and versatile manufacturing careers.",
                "Extreme industrial plant hazards (high pressure, toxic chemicals), remote manufacturing plant locations, and heavy emissions regulations."
            ),
            "biomedical engineering" to SubjectOverviewInfo(
                "Biomedical Engineering",
                "Biomedical Engineering combines engineering design and problem-solving principles with medical and biological sciences to advance healthcare treatment.",
                "Prosthetic limb engineering, artificial organ development, MRI/CT scanner design, pacemaker electronics, and surgical robotics.",
                "Directly improves human lifespan and patient quality of life, bridging clinical medicine and advanced hardware engineering.",
                "Extremely stringent FDA medical device approvals, high risk of litigation, and complex biological-hardware biocompatibility issues."
            ),
            "nanotechnology" to SubjectOverviewInfo(
                "Nanotechnology",
                "Nanotechnology is the manipulation of matter on an atomic, molecular, and supramolecular scale (1 to 100 nanometers).",
                "Targeted cancer drug nanocarriers, carbon nanotube materials, semiconductor EUV lithography, and self-cleaning hydrophobic coatings.",
                "Unlocks revolutionary material properties invisible at macro scales, enabling hyper-efficient microchips and revolutionary medicines.",
                "Expensive clean-room fabrication facilities, difficulty controlling molecular self-assembly, and unknown environmental nanotoxicology risks."
            ),
            "supply chain" to SubjectOverviewInfo(
                "Supply Chain & Logistics",
                "Supply Chain Management oversees the entire flow of goods, data, and finances related to a product or service, from raw material procurement to final customer delivery.",
                "Global shipping fleet optimization, Amazon fulfillment warehousing, just-in-time inventory manufacturing, and demand forecasting.",
                "Crucial backbone of global commerce, high organizational visibility, and heavy adoption of algorithmic and AI automation.",
                "High vulnerability to geopolitical conflicts and pandemic disruptions, relentless margin pressure, and round-the-clock operational stress."
            ),
            "law" to SubjectOverviewInfo(
                "Law & Legal Studies",
                "Law is the system of rules created and enforced through social or governmental institutions to regulate behavior, settle disputes, and ensure justice.",
                "Corporate contract negotiations, intellectual property litigation, criminal defense and prosecution, and constitutional advocacy.",
                "Immense intellectual prestige, high-level advocacy power, and lucrative legal partnership earnings.",
                "Exhaustive legal case law reading and citation memorization, adversarial courtroom environments, and high-stakes client accountability."
            )
        )

        val aliasMap = mapOf(
            "math" to "mathematics", "maths" to "mathematics", "algebra" to "mathematics", "calculus" to "calculus",
            "cs" to "computer science", "programming" to "computer science", "coding" to "computer science",
            "ds" to "data science", "data analytics" to "data science",
            "ai" to "artificial intelligence", "ml" to "artificial intelligence", "machine learning" to "artificial intelligence", "deep learning" to "artificial intelligence",
            "phy" to "physics", "waves" to "physics",
            "chem" to "chemistry", "bonding" to "chemistry", "chemical bonding" to "chemistry",
            "bio" to "biology", "life science" to "biology", "biological sciences" to "biology",
            "world history" to "history", "ancient history" to "history",
            "finance" to "economics", "econ" to "economics", "macroeconomics" to "economics", "microeconomics" to "economics",
            "ecology" to "environmental science", "environment" to "environmental science", "environmental" to "environmental science",
            "networks" to "computer networks", "networking" to "computer networks", "net" to "computer networks",
            "security" to "cyber security", "cybersecurity" to "cyber security", "infosec" to "cyber security", "sec" to "cyber security",
            "psych" to "psychology",
            "management" to "business", "business management" to "business", "mba" to "business", "biz" to "business",
            "electronics" to "electrical engineering", "circuits" to "electrical engineering", "ee" to "electrical engineering",
            "astrophysics" to "astronomy", "astro" to "astronomy",
            "swe" to "software engineering", "software dev" to "software engineering",
            "devops" to "cloud computing", "cloud" to "cloud computing", "aws" to "cloud computing",
            "os" to "operating systems", "linux" to "operating systems",
            "dbms" to "database", "databases" to "database", "sql" to "database",
            "web dev" to "web development", "frontend" to "web development", "backend" to "web development",
            "mobile" to "mobile development", "android" to "mobile development", "ios" to "mobile development",
            "crypto" to "blockchain", "cryptocurrency" to "blockchain", "cryptography" to "blockchain",
            "automation" to "robotics",
            "mechanics" to "mechanical engineering",
            "structural engineering" to "civil engineering",
            "biotech" to "biotechnology",
            "genomics" to "genetics",
            "brain" to "neuroscience",
            "quantum" to "quantum physics", "quantum mechanics" to "quantum physics",
            "matrices" to "linear algebra",
            "discrete math" to "discrete mathematics",
            "ethics" to "philosophy",
            "pol sci" to "political science", "politics" to "political science", "govt" to "political science",
            "english" to "literature", "english literature" to "literature",
            "audit" to "accounting",
            "it" to "information technology",
            "aeronautics" to "aerospace engineering",
            "bioengineering" to "biomedical engineering", "bme" to "biomedical engineering",
            "logistics" to "supply chain", "supply chain management" to "supply chain",
            "legal" to "law", "jurisprudence" to "law"
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
                clean == "short" || clean == "brief"
    }

    fun buildShortenedResponse(chunk: KnowledgeChunk): LocalAiResponse {
        val def = if (chunk.definition.isNotBlank()) chunk.definition else chunk.content.substringBefore(".") + "."
        val formula = if (chunk.formulaOrCoreRule.isNotBlank()) chunk.formulaOrCoreRule else "Core principle in ${chunk.subject}."
        val example = if (chunk.workedExample.isNotBlank()) chunk.workedExample.substringBefore(".") + "." else "Applied in ${chunk.chapter}."

        val bulletPrefix = if (useEmojis) "📘 " else "* "
        val p1 = "${bulletPrefix}**${chunk.topic}** (${chunk.subject})\n$def"
        val p2 = buildString {
            if (includeFormulas) {
                appendLine("1. **Core Principle & Formula:** $formula")
            }
            if (includeExamples) {
                appendLine("2. **Quick Takeaway:** $example")
            }
            append("3. **Exam Focus:** Key concept in ${chunk.chapter}.")
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
            paragraph1 = "📚 **SYNAPSE AI University Curriculum Catalog (50+ Subjects)**\n\nI currently feature a comprehensive, university-grade curriculum of **50+ academic subjects**, each organized into 8 to 10 progressive college chapters ranging from foundational basics to advanced mastery.",
            paragraph2 = "⚡ **Core Disciplines Available On-Device:**\n" +
                    "• 📐 **Mathematics & Statistics**: Foundations of Algebra, Complex Numbers, Differential Calculus, Integral Calculus, Linear Algebra, ODE/PDE, Probability & Graph Theory\n" +
                    "• 💻 **Computer Science**: Programming Fundamentals, Data Structures (Trees, Stacks, Queues), Graphs & DP, Operating Systems, DBMS & Compilers\n" +
                    "• 🤖 **Artificial Intelligence & Data Science**: Search Algorithms, Supervised ML, Deep Learning & CNNs, Transformers, RAG & AI Ethics\n" +
                    "• ⚡ **Physical Sciences & Engineering**: Waves, Newton's Laws, Thermodynamics, Electromagnetism, Digital Electronics, VLSI & Robotics\n" +
                    "• 🧪 **Chemical & Life Sciences**: Chemical Bonding, Stoichiometry, Molecular Biology, Genetics & Cellular Physiology\n" +
                    "• 🏛️ **Humanities & Management**: World History, Micro/Macro Economics, and Professional Communication\n\n" +
                    "💡 *Every chapter includes structured lesson content, auto-graded quizzes, and AI smart notes. Just type or speak any subject to get started!*",
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
            paragraph1 = "⚙️ **How SYNAPSE AI Works & How to Study With It**\n\nSYNAPSE AI is built as a complete on-device academic companion designed to run 100% offline directly on your mobile device without needing internet access or sending private data to cloud servers.",
            paragraph2 = "⚡ **Key Capabilities & Workflow:**\n" +
                    "1. 🧠 **Offline SLM + Semantic RAG**: Generates clear, verified concept explanations, formulas, and step-by-step worked examples directly on your phone with zero hallucinations.\n" +
                    "2. 🎙️ **Live Voice Assistant**: Talk naturally to ask questions hands-free; our speech recognizer listens patiently without cutting you off, and replies in a natural male voice.\n" +
                    "3. 📝 **AI Smart Notes**: Automatically summarizes chapters into key definitions, core formulas, and high-yield exam takeaways.\n" +
                    "4. 🎯 **Auto-Graded Quizzes**: Test your understanding with chapter-level practice questions and instant answer explanations.\n" +
                    "5. 🔄 **Hybrid Backend Sync**: All your learning progress, completed chapters, notes, and quiz scores synchronize seamlessly between local storage and the database.\n\n" +
                    "To begin, select a subject from the home screen, or simply type or speak your questions right here!",
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

        if (isShortenQuery(cleaned)) {
            return buildShortenedResponse(chunk)
        }

        if (isElaborationQuery(cleaned) || elaboratePrompts.any { cleaned.contains(it) } || cleaned == "elaborate") {
            return buildEducationalResponse(chunk, 100.0, isElaborated = true, isFollowUp = true)
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
                paragraph1 = "${bulletPrefix}**Practical Example** (${chunk.subject} · ${chunk.topic})\n\nHere is a real-world scenario illustrating **${chunk.topic}**:",
                paragraph2 = "$exampleText\n\n${insightPrefix}*Context Grounded:* Derived from ${chunk.chapter} (${chunk.subject}).",
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
                paragraph2 = "$stepGuide\n\n${insightPrefix}*Formula/Insight:* $formulaText.",
                matchedSubject = chunk.subject,
                matchedChapter = chunk.chapter,
                matchedTopic = chunk.topic,
                confidenceScore = 90.0,
                isFollowUp = true
            )
        }

        return LocalAiResponse(
            paragraph1 = "${bulletPrefix}**In-Depth Breakdown** (${chunk.subject} · ${chunk.topic})\n\nExpanding further on **${chunk.topic}**:",
            paragraph2 = "${chunk.content.ifBlank { "${chunk.definition}\n\n${chunk.formulaOrCoreRule}" }}\n\n${insightPrefix}*Takeaway:* Mastering ${chunk.topic} connects directly to advanced applications in ${chunk.chapter}.",
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

        val bulletPrefix = if (useEmojis) "📘 " else "* "
        val formulaPrefix = if (useEmojis) "⚡ " else "* "
        val examplePrefix = if (useEmojis) "💡 " else "* "

        return if (isElaborated) {
            val p1 = "${bulletPrefix}**In-Depth Breakdown: ${chunk.topic}** (${chunk.subject} · ${chunk.chapter})\n\n" +
                    "**1. Theoretical Foundations & Mechanism:**\n" +
                    "${chunk.content}" +
                    (if (includeFormulas && formulaText.isNotBlank()) "\n\n**2. Mathematical Formulations & Derivations:**\n$formulaText" else "")
            val p2 = buildString {
                if (includeExamples && exampleText.isNotBlank()) {
                    appendLine("**3. Comprehensive Practical Application:**")
                    appendLine(exampleText)
                    appendLine()
                }
                appendLine("**4. High-Yield Exam Takeaway:**")
                append("Mastering ${chunk.topic} requires remembering key governing assumptions, units, and boundary conditions.")
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
            // Concise-by-default (3-5 lines max: 1-sentence definition + key formula/rule + 1 quick example)
            val p1 = "${bulletPrefix}**Concept & Definition** (${chunk.subject} · ${chunk.topic})\n$definitionText"
            val p2 = buildString {
                if (includeFormulas && formulaText.isNotBlank()) {
                    appendLine("${formulaPrefix}**Formula / Core Principle:** $formulaText")
                }
                if (includeExamples && exampleText.isNotBlank()) {
                    append("${examplePrefix}**Key Example & Practical Takeaway:** $exampleText")
                }
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

        // 1. BIOLOGY
        if (q.contains("photosynthesis") || (q.contains("light") && q.contains("chloroplast"))) {
            val p2 = buildString {
                if (includeFormulas) {
                    appendLine("${fPrefix}**Formula / Core Principle**\n\n6CO₂ + 6H₂O + Photons → C₆H₁₂O₆ + 6O₂\n\nOperates in two stages: Light-dependent reactions (in thylakoids) generate ATP and NADPH, followed by the Calvin cycle (in stroma) synthesizing glucose carbohydrates.\n")
                }
                if (includeExamples) {
                    appendLine("${ePrefix}**Key Example & Practical Takeaway**\nGreen leaves absorb atmospheric CO₂ through stomata and pull water from soil roots; oxygen is released as a vital byproduct sustaining aerobic life.")
                }
            }.trim()
            return LocalAiResponse(
                paragraph1 = "${bPrefix}**Concept & Definition** (Biology · Plant Physiology)\n\nPhotosynthesis is the biochemical process by which green plants, algae, and cyanobacteria convert light energy from the sun into chemical energy stored in glucose molecules. It takes place in cellular chloroplasts using the green pigment chlorophyll.",
                paragraph2 = p2,
                matchedSubject = "Biology",
                matchedChapter = "Plant Physiology",
                matchedTopic = "Photosynthesis",
                confidenceScore = 90.0
            )
        }

        if (q.contains("dna") || q.contains("rna") || q.contains("genetic") || q.contains("heredity") || q.contains("chromosome")) {
            val p2 = buildString {
                if (includeFormulas) {
                    appendLine("${fPrefix}**Formula / Core Principle**\n\nBase Pairing Rule: Adenine pairs with Thymine (A=T via 2 hydrogen bonds), and Guanine pairs with Cytosine (G≡C via 3 hydrogen bonds). In RNA, Uracil (U) replaces Thymine.\nCentral Dogma: DNA → (Transcription) → mRNA → (Translation) → Functional Proteins.\n")
                }
                if (includeExamples) {
                    appendLine("${ePrefix}**Key Example & Practical Takeaway**\nA single nucleotide substitution in the hemoglobin gene causes sickle cell anemia, illustrating how molecular base sequences govern macroscopic human health.")
                }
            }.trim()
            return LocalAiResponse(
                paragraph1 = "${bPrefix}**Concept & Definition** (Biology · Genetics & Molecular Biology)\n\nDNA (Deoxyribonucleic Acid) is the double-helical macromolecule containing genetic blueprints for biological organism development and functioning. RNA (Ribonucleic Acid) carries messages from DNA to ribosomes for protein synthesis.",
                paragraph2 = p2,
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
                val p2 = buildString {
                    if (includeFormulas) {
                        appendLine("${fPrefix}**Formula / Core Principle**\n\nKey Milestones: Battle of Stalingrad (1942–1943), D-Day Normandy Landings (1944), conclusion with German surrender and atomic bombings of Hiroshima & Nagasaki (August 1945).\n")
                    }
                    if (includeExamples) {
                        appendLine("${ePrefix}**Key Example & Practical Takeaway**\nThe post-war Bretton Woods conference established modern international economic architecture including the World Bank and IMF.")
                    }
                }.trim()
                LocalAiResponse(
                    paragraph1 = "${bPrefix}**Concept & Definition** (History · Modern World History)\n\nWorld War II (1939–1945) was a global conflict fought between the Allied Powers (USA, Great Britain, Soviet Union, France, China) and the Axis Powers (Nazi Germany, Imperial Japan, Fascist Italy), sparked by Germany's invasion of Poland on September 1, 1939.",
                    paragraph2 = p2,
                    matchedSubject = "History",
                    matchedChapter = "Modern World History",
                    matchedTopic = "World War II",
                    confidenceScore = 90.0
                )
            } else {
                val p2 = buildString {
                    if (includeFormulas) {
                        appendLine("${fPrefix}**Formula / Core Principle**\n\nThe M-A-I-N Causes: Militarism, Alliances, Imperialism, and Nationalism. Sparked by the assassination of Archduke Franz Ferdinand in Sarajevo (June 28, 1914).\n")
                    }
                    if (includeExamples) {
                        appendLine("${ePrefix}**Key Example & Practical Takeaway**\nTrench warfare on the Western Front reshaped 20th-century warfare, leading to the collapse of four empires and the Treaty of Versailles (1919).")
                    }
                }.trim()
                LocalAiResponse(
                    paragraph1 = "${bPrefix}**Concept & Definition** (History · Modern World History)\n\nWorld War I (1914–1918), known as the Great War, was a global European-centered conflict between the Allied Powers (Britain, France, Russia, USA, Italy) and the Central Powers (Germany, Austria-Hungary, Ottoman Empire, Bulgaria).",
                    paragraph2 = p2,
                    matchedSubject = "History",
                    matchedChapter = "Modern World History",
                    matchedTopic = "World War I",
                    confidenceScore = 90.0
                )
            }
        }

        // 3. GEOGRAPHY
        if (q.contains("plate tectonics") || q.contains("earthquake") || q.contains("volcano") || q.contains("continental drift") || q.contains("tectonic")) {
            val p2 = buildString {
                if (includeFormulas) {
                    appendLine("${fPrefix}**Formula / Core Principle**\n\n• Convergent: Plates collide, causing subduction trenches and mountain ranges.\n• Divergent: Plates pull apart, creating mid-ocean ridges and rift valleys.\n• Transform: Plates slide horizontally past each other, releasing seismic stress as earthquakes.\n")
                }
                if (includeExamples) {
                    appendLine("${ePrefix}**Key Example & Practical Takeaway**\nThe ongoing collision between the Indian Plate and Eurasian Plate elevates Mount Everest and the Himalayan range by ~5 mm annually.")
                }
            }.trim()
            return LocalAiResponse(
                paragraph1 = "${bPrefix}**Concept & Definition** (Geography · Geomorphology & Geology)\n\nPlate Tectonics is the unifying geological theory stating that Earth's outer lithosphere is broken into rigid plates that float and move atop the ductile asthenosphere mantle beneath them.",
                paragraph2 = p2,
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
            val p2 = buildString {
                if (includeFormulas) {
                    appendLine("${fPrefix}**Formula / Core Principle**\n\n1. Define primary variables, boundary conditions, and governing hypotheses.\n2. Apply the canonical laws and theorems established in $detectedSubject.\n3. Verify results and examine real-world trade-offs.\n")
                }
                if (includeExamples) {
                    appendLine("${ePrefix}**Key Example & Practical Takeaway**\nMastering $cleanTopic develops rigorous problem-solving skills and forms a solid foundation for advanced studies in $detectedSubject.")
                }
            }.trim()
            return LocalAiResponse(
                paragraph1 = "${bPrefix}**Concept & Definition** ($detectedSubject · $cleanTopic)\n\n$cleanTopic is an essential concept within $detectedSubject. Understanding its underlying principles allows students to connect theoretical fundamentals with empirical real-world applications.",
                paragraph2 = p2,
                matchedSubject = detectedSubject,
                matchedChapter = "Core Concepts",
                matchedTopic = cleanTopic,
                confidenceScore = 75.0
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

            val p1 = "📘 **Academic Exploration: $capitalizedTopic**\n\n" +
                    "$capitalizedTopic is an important concept in university-level studies. In higher education curricula, mastering this concept equips students with rigorous conceptual grounding, mathematical logic, and analytical problem-solving methodologies."
            val p2 = "⚡ **Core Study Framework & Key Principles:**\n\n" +
                    "• **Theoretical Foundations:** Review the primary definitions, governing laws, and underlying assumptions for $capitalizedTopic.\n" +
                    "• **Analytical Derivations:** Break down complex multi-step problems, verify boundary conditions, and analyze tradeoffs.\n" +
                    "• **Real-World Application:** Relate theoretical equations to modern scientific and engineering implementations.\n\n" +
                    "💡 *Study Suggestion:* Ask me *\"Give me a worked example of $capitalizedTopic\"*, *\"Explain $capitalizedTopic step-by-step\"*, or *\"Generate smart notes for $capitalizedTopic\"*!"

            return LocalAiResponse(
                paragraph1 = p1,
                paragraph2 = p2,
                matchedSubject = "University Curriculum",
                matchedChapter = capitalizedTopic,
                matchedTopic = capitalizedTopic,
                confidenceScore = 75.0,
                isGreeting = false
            )
        }

        val p1 = "🎓 **SYNAPSE AI University Study Companion**\n\n" +
                "I am your 100% on-device academic tutor, equipped with an extensive university curriculum spanning over 50 academic subjects—including Mathematics, Computer Science, AI, Physics, Chemistry, and Engineering."
        val p2 = "⚡ **Here is How We Can Study Right Now:**\n\n" +
                "• **Master Specific Concepts:** Ask e.g. *\"Explain Eigenvalues\"*, *\"What is a B-Tree?\"*, *\"Derive Quadratic Formula\"*, or *\"How does Backpropagation work?\"*\n" +
                "• **Resolve Study Doubts:** Ask me any problem or question you are stuck on for an immediate step-by-step breakdown.\n" +
                "• **Explore Course Syllabi:** Ask *\"Suggest Mathematics topics\"* or *\"What can I learn in Computer Science?\"*\n" +
                "• **High-Yield Smart Notes:** Ask me to generate revision notes and formula cheat-sheets for any chapter.\n\n" +
                "Which subject or concept would you like to explore?"

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
            "Sorry, I don't know about that—it's outside my study scope. Ask me any subject concept, quiz, or revision note instead."

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
