package com.siamts.synapse

import com.siamts.synapse.ai.LocalAIEngine
import com.siamts.synapse.ai.LocalAiStreamToken
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Unit tests verifying on-device [LocalAIEngine] behavior, greetings detection,
 * educational dataset retrieval across all subjects, topic suggestion intent resolution,
 * conceptual RAG explanations, and progressive Kotlin Flow streaming.
 */
class LocalAIEngineTest {

    private lateinit var engine: LocalAIEngine

    @Before
    fun setUp() {
        engine = LocalAIEngine.getInstance()
        val possiblePaths = listOf(
            File("src/main/assets/educational_knowledge_base.json"),
            File("app/src/main/assets/educational_knowledge_base.json"),
            File("../app/src/main/assets/educational_knowledge_base.json"),
            File("C:/Users/SCAD LAB/AndroidStudioProjects/SYNAPSE/app/src/main/assets/educational_knowledge_base.json"),
            File("src/main/assets/all_subjects_expanded.json"),
            File("app/src/main/assets/all_subjects_expanded.json")
        )

        val jsonContent = possiblePaths.firstOrNull { it.exists() }?.readText()

        assertNotNull("Knowledge dataset json file must exist for tests", jsonContent)
        engine.loadFromJson(jsonContent!!)
        engine.resetContext()
    }

    @Test
    fun testDatasetLoaded_containsAllSubjectsAndChunks() {
        val chunks = engine.getAllChunks()
        assertTrue("Dataset should contain at least 25 chunks", chunks.size >= 25)

        val subjects = chunks.map { it.subject }.distinct()
        assertTrue("Contains 10th Mathematics", subjects.contains("10th Mathematics"))
        assertTrue("Contains 10th Science", subjects.contains("10th Science"))
        assertTrue("Contains 10th Social Science", subjects.contains("10th Social Science"))
        assertTrue("Contains 11th Mathematics", subjects.contains("11th Mathematics"))
        assertTrue("Contains 11th Physics", subjects.contains("11th Physics"))
        assertTrue("Contains 11th Chemistry", subjects.contains("11th Chemistry"))
        assertTrue("Contains 11th Biology", subjects.any { it.contains("Biology") })
        assertTrue("Contains 12th Computer Science", subjects.contains("12th Computer Science"))
        assertTrue("Contains 12th Commerce", subjects.any { it.contains("Commerce") })
        assertTrue("Contains 12th Economics", subjects.contains("12th Economics"))
    }

    @Test
    fun testGreetings_returnChatGPTStyleConciseGreeting() {
        val casualGreetings = listOf("hi", "hello", "hey")
        for (query in casualGreetings) {
            val response = engine.generateResponse(query)
            assertTrue("Query '$query' should be detected as greeting", response.isGreeting)
            assertEquals("Hello! I'm doing great and ready to help you study. What topic shall we explore today?", response.paragraph1)
            assertTrue("Greeting should not dump long paragraphs", response.paragraph2.isBlank())
        }

        val gmResponse = engine.generateResponse("good morning")
        assertTrue(gmResponse.isGreeting)
        assertEquals("Good morning! Ready to learn something exciting today? What subject are we tackling?", gmResponse.paragraph1)

        val gnResponse = engine.generateResponse("good night")
        assertTrue(gnResponse.isGreeting)
        assertEquals("Good night! Rest well and keep learning tomorrow.", gnResponse.paragraph1)

        val vanakkamResp = engine.generateResponse("vanakkam")
        assertTrue(vanakkamResp.isGreeting)
        assertTrue(vanakkamResp.paragraph1.contains("Vanakkam"))
    }

    @Test
    fun testConversationalIntents_CapabilitiesAndHelp() {
        val capabilityQueries = listOf(
            "what you can do",
            "what can you do?",
            "wat can u do",
            "help",
            "help me",
            "help me study",
            "features",
            "how to use",
            "how can you help",
            "what are your capabilities",
            "tell me what you can do"
        )
        for (query in capabilityQueries) {
            val response = engine.generateResponse(query)
            assertFalse("Query '$query' is capability, not simple greeting", response.isGreeting)
            assertEquals(100.0, response.confidenceScore, 0.001)
            assertTrue(
                "Query '$query' should return capabilities intro",
                response.paragraph1.contains("study friend here to make learning easy")
            )
            assertFalse("Must not trigger fallback", response.paragraph1.contains("couldn't find a direct match"))
        }
    }

    @Test
    fun testConversationalIntents_IdentityAndPersona() {
        val identityQueries = listOf(
            "who are you",
            "what is synapse ai",
            "tell me about yourself",
            "are you chatgpt",
            "are you offline",
            "how do you work"
        )
        for (query in identityQueries) {
            val response = engine.generateResponse(query)
            assertFalse("Query '$query' should not be greeting", response.isGreeting)
            assertEquals(100.0, response.confidenceScore, 0.001)
            assertTrue(
                "Query '$query' should return identity description",
                response.paragraph1.contains("SYNAPSE AI")
            )
            assertFalse("Must not trigger fallback", response.paragraph1.contains("couldn't find a direct match"))
        }
    }

    @Test
    fun testConversationalIntents_GratitudeAndClosing() {
        val gratitudeQueries = listOf(
            "thanks",
            "thank you",
            "thx",
            "awesome",
            "great",
            "ok",
            "okay",
            "got it",
            "nice"
        )
        for (query in gratitudeQueries) {
            val response = engine.generateResponse(query)
            assertFalse("Query '$query' should not be greeting", response.isGreeting)
            assertEquals(100.0, response.confidenceScore, 0.001)
            assertEquals(
                "You're very welcome! Let me know whenever you want to dive into another topic, formula, or practice question.",
                response.paragraph1
            )
            assertFalse("Must not trigger fallback", response.paragraph1.contains("couldn't find a direct match"))
        }
    }

    @Test
    fun testEducationalQuery_QuadraticEquation_intact() {
        val response = engine.generateResponse("explain quadratic equation in mathematics")
        assertFalse(response.isGreeting)
        assertTrue(response.matchedSubject?.contains("Mathematics") == true)
        assertTrue(response.paragraph1.contains("Quadratic") || response.paragraph1.contains("Algebra") || response.paragraph1.contains("Concept"))
        assertFalse("Must not trigger fallback", response.paragraph1.contains("couldn't find a direct match"))
    }

    @Test
    fun testTopicSuggestionIntent_mathsTopics_returnsStructuredListAndStartingPoint() {
        val mathResponse = engine.generateResponse("give me some maths topic")
        assertFalse(mathResponse.isGreeting)
        assertTrue("Should be flagged as topic suggestion", mathResponse.isTopicSuggestion)
        assertTrue(mathResponse.matchedSubject?.contains("Mathematics") == true)
        assertTrue(mathResponse.paragraph1.contains("Mathematics Topics & Study Roadmap"))
        assertTrue(mathResponse.paragraph2.contains("Where to Begin:"))

        val chemResponse = engine.generateResponse("suggest chemistry chapters")
        assertFalse(chemResponse.isGreeting)
        assertTrue("Should be flagged as topic suggestion", chemResponse.isTopicSuggestion)
        assertTrue(chemResponse.matchedSubject?.contains("Chemistry") == true)
        assertTrue(chemResponse.paragraph2.contains("Where to Begin:"))

        val physicsResponse = engine.generateResponse("list physics topics")
        assertFalse(physicsResponse.isGreeting)
        assertTrue("Should be flagged as topic suggestion", physicsResponse.isTopicSuggestion)
        assertTrue(physicsResponse.matchedSubject?.contains("Physics") == true)
    }

    @Test
    fun testTopicSuggestionIntent_general_returnsAllSubjectsCatalog() {
        val response = engine.generateResponse("what can i learn")
        assertFalse(response.isGreeting)
        assertTrue("Should be flagged as topic suggestion", response.isTopicSuggestion)
        assertTrue(response.paragraph1.contains("SYNAPSE AI Curriculum & Topic Catalog"))
        assertTrue(response.paragraph2.contains("Mathematics"))
        assertTrue(response.paragraph2.contains("Science"))
        assertTrue(response.paragraph2.contains("Physics"))
        assertTrue(response.paragraph2.contains("Chemistry"))
    }

    @Test
    fun testConceptualExplanation_quadraticEquation_structuredSections() {
        val response = engine.generateResponse("explain quadratic equation and formula")
        assertFalse(response.isGreeting)
        assertTrue(response.matchedSubject?.contains("Mathematics") == true)
        assertTrue(response.paragraph1.contains("Concept & Definition"))
        assertTrue(response.paragraph2.contains("Formula") || response.paragraph2.contains("Rule") || response.paragraph2.contains("Takeaway"))
    }

    @Test
    fun testConceptualExplanation_wavesAndSound() {
        val response = engine.generateResponse("what are waves in physics?")
        assertFalse(response.isGreeting)
        assertTrue(response.matchedSubject?.contains("Physics") == true)
        assertTrue(response.paragraph1.contains("Concept & Definition"))
        assertTrue(response.confidenceScore >= 5.0)
    }

    @Test
    fun testConceptualExplanation_covalentBond() {
        val response = engine.generateResponse("define chemical bonding in chemistry")
        assertFalse(response.isGreeting)
        assertTrue(response.matchedSubject?.contains("Chemistry") == true)
        assertTrue(response.paragraph1.contains("Concept & Definition"))
    }

    @Test
    fun testComputerScienceQuery_binarySearchAndSorting() {
        val response = engine.generateResponse("explain function and algorithms in computer science")
        assertFalse(response.isGreeting)
        assertTrue(response.matchedSubject?.contains("Computer Science") == true)
        assertTrue(response.paragraph1.contains("Concept & Definition"))
        assertTrue(response.confidenceScore >= 5.0)
    }

    @Test
    fun testDataScienceQuery_dataVisualizationAndModelMetrics() {
        val response = engine.generateResponse("explain accounting and commerce fundamentals")
        assertFalse(response.isGreeting)
        assertTrue(response.matchedSubject?.contains("Commerce") == true)
        assertTrue(response.paragraph1.contains("Concept & Definition"))
    }

    @Test
    fun testAIQuery_gradientDescentAndNeuralNetworks() {
        val response = engine.generateResponse("explain fiscal economics and national income")
        assertFalse(response.isGreeting)
        assertTrue(response.matchedSubject?.contains("Economics") == true)
        assertTrue(response.paragraph1.contains("Concept & Definition"))
    }

    @Test
    fun testBiologySynthesis_photosynthesisAndDNA() {
        val photoResp = engine.generateResponse("explain plant biology and organ systems")
        assertTrue(photoResp.matchedSubject?.contains("Biology") == true || photoResp.matchedSubject?.contains("Science") == true)
        assertTrue(photoResp.paragraph1.contains("Concept & Definition"))

        val dnaResp = engine.generateResponse("explain genetics and cell biology")
        assertTrue(dnaResp.matchedSubject?.contains("Biology") == true || dnaResp.matchedSubject?.contains("Science") == true)
    }

    @Test
    fun testHistorySynthesis_worldWarAndCivilizations() {
        val ww2Resp = engine.generateResponse("explain the outbreak of world war in social science")
        assertTrue(ww2Resp.matchedSubject?.contains("Social Science") == true)
        assertTrue(ww2Resp.paragraph1.contains("Concept & Definition") || ww2Resp.paragraph1.contains("World War") || ww2Resp.paragraph1.contains("Social"))

        val civResp = engine.generateResponse("tell me about social and religious reform movements in social science")
        assertTrue(civResp.matchedSubject?.contains("Social Science") == true)
        assertTrue(civResp.paragraph1.contains("Reform") || civResp.paragraph1.contains("Social") || civResp.paragraph1.contains("Concept"))
    }

    @Test
    fun testMultiTurnFollowUp_resolvesTopicFromPreviousTurn() {
        // Turn 1: Primary educational question
        val turn1 = engine.generateResponse("How do I solve quadratic equations in mathematics?")
        assertTrue(turn1.matchedSubject?.contains("Mathematics") == true)

        // Turn 2: Follow-up requesting an example
        val turn2 = engine.generateResponse("give an example")
        assertTrue("Follow-up flag should be set", turn2.isFollowUp)
        assertTrue(turn2.matchedSubject?.contains("Mathematics") == true)
        assertTrue("Response should contain practical example", turn2.paragraph1.contains("Practical Example"))

        // Turn 3: Follow-up requesting method / roots
        val turn3 = engine.generateResponse("what about its roots?")
        assertTrue("Follow-up flag should be set", turn3.isFollowUp)
        assertTrue(turn3.matchedSubject?.contains("Mathematics") == true)
        assertTrue(turn3.paragraph1.contains("Method & Solving Steps"))

        // Turn 4: Follow-up requesting more details
        val turn4 = engine.generateResponse("explain more")
        assertTrue("Follow-up flag should be set", turn4.isFollowUp)
        assertTrue(turn4.matchedSubject?.contains("Mathematics") == true)
        assertTrue(turn4.paragraph1.contains("In-Depth Breakdown"))
    }

    @Test
    fun testResetContext_clearsRollingMemory() {
        engine.generateResponse("Explain laws of motion in physics")
        assertTrue(engine.getContextHistory().isNotEmpty())

        engine.resetContext()
        assertEquals(0, engine.getContextHistory().size)

        val response = engine.generateResponse("what is the recipe for baking a chocolate cake?")
        assertTrue(response.confidenceScore > 0.0)
        assertFalse("Must never contain fallback error", response.paragraph1.contains("couldn't find a direct match"))
        assertTrue(response.paragraph1.contains("not relatable to our 10th, 11th & 12th studies") || response.paragraph1 == LocalAIEngine.OUT_OF_SCOPE_RESPONSE)
    }

    @Test
    fun testFallbackQuery_unrelatedQuestion_returnsHelpfulGuidance() {
        val response = engine.generateResponse("What is the recipe for baking a chocolate cake?")
        assertFalse(response.isGreeting)
        assertTrue(response.confidenceScore > 0.0)
        assertFalse(
            "Fallback response should NEVER state it could not find a match",
            response.paragraph1.contains("couldn't find a direct match")
        )
        assertTrue(
            "Food/recipe query must hit the strict out-of-scope guardrail boundary",
            response.paragraph1.contains("not relatable to our 10th, 11th & 12th studies") || response.paragraph1 == LocalAIEngine.OUT_OF_SCOPE_RESPONSE
        )
    }

    @Test
    fun testOutOfScopeGuardrail_canYouEat_returnsBoundaryResponse() {
        val response = engine.generateResponse("can you eat??")
        assertEquals(LocalAIEngine.OUT_OF_SCOPE_RESPONSE, response.paragraph1)
    }

    @Test
    fun testOutOfScopeGuardrail_whoIsRajini_returnsBoundaryResponse() {
        val response = engine.generateResponse("who is rajini")
        assertEquals(LocalAIEngine.OUT_OF_SCOPE_RESPONSE, response.paragraph1)
    }

    @Test
    fun testTanglishIdentity_neeYaru_returnsConciseIntro() {
        val response = engine.generateResponse("nee yaru?")
        assertTrue(response.paragraph1.contains("I am SYNAPSE AI"))
        assertTrue(response.paragraph1.contains("50+ university subjects"))
    }

    @Test
    fun testTanglishIdentity_unPeruEnna_returnsConciseIntro() {
        val response = engine.generateResponse("un peru enna")
        assertTrue(response.paragraph1.contains("I am SYNAPSE AI"))
    }

    @Test
    fun testWellBeingIntent_howAreYou() {
        val testQueries = listOf("how are you?", "how are you", "how r u", "how are you doing", "what's up", "whats up")
        for (q in testQueries) {
            val response = engine.generateResponse(q)
            assertFalse("Query '$q' must NEVER contain fallback error", response.paragraph1.contains("couldn't find a direct match"))
            assertTrue("Query '$q' should return positive well-being reply", response.paragraph1.contains("doing great"))
            assertEquals(100.0, response.confidenceScore, 0.001)
        }
    }

    @Test
    fun testIdentityAndName_whatsYourName() {
        val nameQueries = listOf("whats your name?", "what is your name?", "what's your name", "tell me your name", "who are you")
        for (q in nameQueries) {
            val response = engine.generateResponse(q)
            assertFalse("Query '$q' must NEVER contain fallback error", response.paragraph1.contains("couldn't find a direct match"))
            assertTrue("Query '$q' should state its name SYNAPSE AI", response.paragraph1.contains("SYNAPSE AI"))
            assertEquals(100.0, response.confidenceScore, 0.001)
        }
    }

    @Test
    fun testSubjectCatalog_howManySubjectsHaveYou() {
        val subjectQueries = listOf(
            "how many subjects have you?",
            "how many subjects do you have?",
            "what subjects are available?",
            "what subjects do you have",
            "list all subjects"
        )
        for (q in subjectQueries) {
            val response = engine.generateResponse(q)
            assertFalse("Query '$q' must NEVER contain fallback error", response.paragraph1.contains("couldn't find a direct match"))
            assertTrue("Query '$q' should mention 50+ subjects", response.paragraph1.contains("50+"))
            assertTrue("Query '$q' should mention core subjects", response.paragraph2.contains("Mathematics") && response.paragraph2.contains("Computer Science"))
            assertTrue(response.confidenceScore >= 95.0)
        }
    }

    @Test
    fun testHowShouldYouToWork() {
        val workQueries = listOf(
            "how should you to work?",
            "how should you work?",
            "how should you operate?",
            "how should you to work"
        )
        for (q in workQueries) {
            val response = engine.generateResponse(q)
            assertFalse("Query '$q' must NEVER contain fallback error", response.paragraph1.contains("couldn't find a direct match"))
            assertTrue("Query '$q' should explain how SYNAPSE AI works", response.paragraph1.contains("How SYNAPSE AI Works") || response.paragraph1.contains("How SYNAPSE AI Operates"))
            assertEquals(100.0, response.confidenceScore, 0.001)
        }
    }

    @Test
    fun testDoubtAssistance_iHaveSomeDoubts() {
        val doubtQueries = listOf(
            "I have some doubts",
            "i have some doubts",
            "i have a doubt",
            "i want some doubts for my subjects",
            "clear my doubts",
            "help me with my doubts"
        )
        for (q in doubtQueries) {
            val response = engine.generateResponse(q)
            assertFalse("Query '$q' must NEVER contain fallback error", response.paragraph1.contains("couldn't find a direct match"))
            assertTrue("Query '$q' should trigger Doubt Resolution Tutor", response.paragraph1.contains("Doubt Resolution Tutor"))
            assertTrue("Query '$q' should offer guidance on how to solve doubts", response.paragraph2.contains("How We Can Solve Your Doubts"))
            assertTrue(response.confidenceScore >= 95.0)
        }
    }

    @Test
    fun testConceptQuery_whatIsWaveInPhysics_matchesWavesNotNewton() {
        val response = engine.generateResponse("what is wave in physics?")
        assertFalse("Must never contain fallback error", response.paragraph1.contains("couldn't find a direct match"))
        assertFalse("Must NOT return Newton's laws", response.paragraph1.contains("Newton") || response.paragraph2.contains("F = ma"))
        assertTrue("Must match Waves topic or chapter", response.matchedChapter?.contains("Wave", ignoreCase = true) == true || response.matchedTopic?.contains("Wave", ignoreCase = true) == true)
    }

    @Test
    fun testConceptQuery_whatIsNewtonLaw_matchesNewtonLaws() {
        val response = engine.generateResponse("what are laws of motion in physics?")
        assertFalse("Must never contain fallback error", response.paragraph1.contains("couldn't find a direct match"))
        assertTrue("Must match Laws of Motion", response.matchedChapter?.contains("Motion", ignoreCase = true) == true || response.matchedTopic?.contains("Motion", ignoreCase = true) == true || response.paragraph1.contains("Motion") || response.paragraph1.contains("Laws"))
    }

    @Test
    fun testConceptQuery_explainEigenvalues_matchesEigenvalues() {
        val response = engine.generateResponse("explain matrices in mathematics")
        assertFalse("Must never contain fallback error", response.paragraph1.contains("couldn't find a direct match"))
        assertTrue("Must match Matrices", response.matchedChapter?.contains("Matrices", ignoreCase = true) == true || response.matchedTopic?.contains("Matrices", ignoreCase = true) == true || response.paragraph1.contains("Matrices", ignoreCase = true))
    }

    @Test
    fun testConceptQuery_whatIsBackpropagation_matchesBackpropagation() {
        val response = engine.generateResponse("what are electromagnetic waves in physics?")
        assertFalse("Must never contain fallback error", response.paragraph1.contains("couldn't find a direct match"))
        assertTrue("Must match Electromagnetic Waves", response.matchedChapter?.contains("Electromagnetic", ignoreCase = true) == true || response.matchedTopic?.contains("Electromagnetic", ignoreCase = true) == true || response.paragraph1.contains("Electromagnetic", ignoreCase = true))
    }

    @Test
    fun testStreamingResponse_emitsThinkingAndTokensProgressively() = runBlocking {
        val events = engine.streamResponse("hi", delayPerWordMs = 1L).toList()
        assertTrue("Must emit at least 3 events", events.size >= 3)
        assertTrue("First event must be Thinking", events.first() is LocalAiStreamToken.Thinking)
        assertTrue("Last event must be Completed", events.last() is LocalAiStreamToken.Completed)
        val tokenEvents = events.filterIsInstance<LocalAiStreamToken.Token>()
        assertTrue("Must emit token events", tokenEvents.isNotEmpty())
        assertEquals("Hello! I'm doing great and ready to help you study. What topic shall we explore today?", (events.last() as LocalAiStreamToken.Completed).response.paragraph1)
    }
}
