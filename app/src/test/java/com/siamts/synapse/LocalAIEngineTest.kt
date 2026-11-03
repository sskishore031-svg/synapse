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
        assertTrue("Contains Mathematics", subjects.contains("Mathematics"))
        assertTrue("Contains Computer Science", subjects.contains("Computer Science"))
        assertTrue("Contains Data Science", subjects.contains("Data Science"))
        assertTrue("Contains Artificial Intelligence", subjects.contains("Artificial Intelligence"))
        assertTrue("Contains Physics", subjects.contains("Physics"))
        assertTrue("Contains Chemistry", subjects.contains("Chemistry"))
        assertTrue("Contains Biology", subjects.contains("Biology"))
        assertTrue("Contains World History", subjects.contains("World History") || subjects.contains("History"))
    }

    @Test
    fun testGreetings_returnChatGPTStyleConciseGreeting() {
        val casualGreetings = listOf("hi", "hello", "hey")
        for (query in casualGreetings) {
            val response = engine.generateResponse(query)
            assertTrue("Query '$query' should be detected as greeting", response.isGreeting)
            assertEquals("Hi! How can I help you with your studies today?", response.paragraph1)
            assertTrue("Greeting should not dump long paragraphs", response.paragraph2.isBlank())
        }

        val gmResponse = engine.generateResponse("good morning")
        assertTrue(gmResponse.isGreeting)
        assertEquals("Good morning! What topic or subject are we learning today?", gmResponse.paragraph1)

        val gnResponse = engine.generateResponse("good night")
        assertTrue(gnResponse.isGreeting)
        assertEquals("Good night! Rest well and keep learning tomorrow.", gnResponse.paragraph1)

        val vanakkamResp = engine.generateResponse("vanakkam")
        assertTrue(vanakkamResp.isGreeting)
        assertTrue(vanakkamResp.paragraph1.contains("Vanakkam"))
    }

    @Test
    fun testTopicSuggestionIntent_mathsTopics_returnsStructuredListAndStartingPoint() {
        val mathResponse = engine.generateResponse("give me some maths topic")
        assertFalse(mathResponse.isGreeting)
        assertTrue("Should be flagged as topic suggestion", mathResponse.isTopicSuggestion)
        assertEquals("Mathematics", mathResponse.matchedSubject)
        assertTrue(mathResponse.paragraph1.contains("Mathematics Topics & Study Roadmap"))
        assertTrue(mathResponse.paragraph2.contains("Quadratic Equations"))
        assertTrue(mathResponse.paragraph2.contains("Where to Begin:"))

        val chemResponse = engine.generateResponse("suggest chemistry chapters")
        assertFalse(chemResponse.isGreeting)
        assertTrue("Should be flagged as topic suggestion", chemResponse.isTopicSuggestion)
        assertEquals("Chemistry", chemResponse.matchedSubject)
        assertTrue(chemResponse.paragraph2.contains("Where to Begin:"))

        val physicsResponse = engine.generateResponse("list physics topics")
        assertFalse(physicsResponse.isGreeting)
        assertTrue("Should be flagged as topic suggestion", physicsResponse.isTopicSuggestion)
        assertEquals("Physics", physicsResponse.matchedSubject)
    }

    @Test
    fun testTopicSuggestionIntent_general_returnsAllSubjectsCatalog() {
        val response = engine.generateResponse("what can i learn")
        assertFalse(response.isGreeting)
        assertTrue("Should be flagged as topic suggestion", response.isTopicSuggestion)
        assertTrue(response.paragraph1.contains("SYNAPSE AI Curriculum & Topic Catalog"))
        assertTrue(response.paragraph2.contains("Mathematics"))
        assertTrue(response.paragraph2.contains("Computer Science"))
        assertTrue(response.paragraph2.contains("Physics"))
        assertTrue(response.paragraph2.contains("Chemistry"))
        assertTrue(response.paragraph2.contains("Biology"))
    }

    @Test
    fun testConceptualExplanation_quadraticEquation_structuredSections() {
        val response = engine.generateResponse("explain quadratic equation and formula")
        assertFalse(response.isGreeting)
        assertEquals("Mathematics", response.matchedSubject)
        assertTrue(response.matchedChapter == "Algebra" || response.matchedChapter == "Quadratic Equations")
        assertTrue(response.paragraph1.contains("Concept & Definition"))
        assertTrue(response.paragraph2.contains("Formula / Core Principle") || response.paragraph2.contains("Quadratic Formula"))
        assertTrue(response.paragraph2.contains("Key Example & Practical Takeaway") || response.paragraph2.contains("Takeaway"))
    }

    @Test
    fun testConceptualExplanation_wavesAndSound() {
        val response = engine.generateResponse("what are waves and sound in physics?")
        assertFalse(response.isGreeting)
        assertEquals("Physics", response.matchedSubject)
        assertTrue(response.paragraph1.contains("Concept & Definition"))
        assertTrue(response.confidenceScore >= 5.0)
    }

    @Test
    fun testConceptualExplanation_covalentBond() {
        val response = engine.generateResponse("define covalent bond and chemical bonding")
        assertFalse(response.isGreeting)
        assertEquals("Chemistry", response.matchedSubject)
        assertTrue(response.paragraph1.contains("Concept & Definition"))
    }

    @Test
    fun testComputerScienceQuery_binarySearchAndSorting() {
        val response = engine.generateResponse("Explain binary search and sorting algorithms time complexity")
        assertFalse(response.isGreeting)
        assertEquals("Computer Science", response.matchedSubject)
        assertTrue(response.paragraph1.contains("Concept & Definition"))
        assertTrue(response.confidenceScore >= 5.0)
    }

    @Test
    fun testDataScienceQuery_dataVisualizationAndModelMetrics() {
        val response = engine.generateResponse("What is data-ink ratio in data visualization?")
        assertFalse(response.isGreeting)
        assertEquals("Data Science", response.matchedSubject)
        assertEquals("Data Visualization", response.matchedChapter)
        assertTrue(response.paragraph1.contains("Tufte") || response.paragraph1.contains("visual") || response.paragraph1.contains("Concept & Definition"))
    }

    @Test
    fun testAIQuery_gradientDescentAndNeuralNetworks() {
        val response = engine.generateResponse("How does gradient descent help a neural network learn?")
        assertFalse(response.isGreeting)
        assertEquals("Artificial Intelligence", response.matchedSubject)
        assertTrue(response.paragraph1.contains("Concept & Definition"))
    }

    @Test
    fun testBiologySynthesis_photosynthesisAndDNA() {
        val photoResp = engine.generateResponse("Explain photosynthesis in plant biology")
        assertEquals("Biology", photoResp.matchedSubject)
        assertTrue(photoResp.matchedTopic?.startsWith("Photosynthesis") == true)
        assertTrue(photoResp.paragraph1.contains("Concept & Definition"))
        assertTrue(photoResp.paragraph2.contains("6CO2") || photoResp.paragraph2.contains("6CO₂"))

        val dnaResp = engine.generateResponse("What is DNA and how does RNA transcription work?")
        assertEquals("Biology", dnaResp.matchedSubject)
        assertTrue(dnaResp.paragraph1.contains("Genetics") || dnaResp.paragraph1.contains("Concept & Definition"))
    }

    @Test
    fun testHistorySynthesis_worldWarAndCivilizations() {
        val ww2Resp = engine.generateResponse("Explain the causes and consequences of World War 2")
        assertTrue(ww2Resp.matchedSubject == "World History" || ww2Resp.matchedSubject == "History")
        assertTrue(ww2Resp.paragraph1.contains("1939–1945") || ww2Resp.paragraph1.contains("World War"))

        val civResp = engine.generateResponse("Tell me about the Indus Valley Civilization")
        assertTrue(civResp.matchedSubject == "World History" || civResp.matchedSubject == "History")
        assertTrue(civResp.paragraph2.contains("Mohenjo-daro") || civResp.paragraph1.contains("Indus") || civResp.paragraph1.contains("civilization"))
    }

    @Test
    fun testMultiTurnFollowUp_resolvesTopicFromPreviousTurn() {
        // Turn 1: Primary educational question
        val turn1 = engine.generateResponse("How do I solve quadratic equations?")
        assertEquals("Mathematics", turn1.matchedSubject)
        assertTrue(turn1.matchedChapter == "Algebra" || turn1.matchedChapter == "Quadratic Equations")

        // Turn 2: Follow-up requesting an example
        val turn2 = engine.generateResponse("give an example")
        assertTrue("Follow-up flag should be set", turn2.isFollowUp)
        assertEquals("Mathematics", turn2.matchedSubject)
        assertTrue("Response should contain practical example", turn2.paragraph1.contains("Practical Example"))

        // Turn 3: Follow-up requesting method / roots
        val turn3 = engine.generateResponse("what about its roots?")
        assertTrue("Follow-up flag should be set", turn3.isFollowUp)
        assertEquals("Mathematics", turn3.matchedSubject)
        assertTrue(turn3.paragraph1.contains("Method & Solving Steps"))

        // Turn 4: Follow-up requesting more details
        val turn4 = engine.generateResponse("explain more")
        assertTrue("Follow-up flag should be set", turn4.isFollowUp)
        assertEquals("Mathematics", turn4.matchedSubject)
        assertTrue(turn4.paragraph1.contains("In-Depth Breakdown"))
    }

    @Test
    fun testResetContext_clearsRollingMemory() {
        engine.generateResponse("Explain Newton's second law of motion")
        assertTrue(engine.getContextHistory().isNotEmpty())

        engine.resetContext()
        assertEquals(0, engine.getContextHistory().size)

        val response = engine.generateResponse("what is the recipe for baking a chocolate cake?")
        assertEquals(0.0, response.confidenceScore, 0.001)
        assertTrue(response.paragraph1.contains("I couldn't find a direct match"))
    }

    @Test
    fun testFallbackQuery_unrelatedQuestion_returnsHelpfulGuidance() {
        val response = engine.generateResponse("What is the recipe for baking a chocolate cake?")
        assertFalse(response.isGreeting)
        assertEquals(0.0, response.confidenceScore, 0.001)
        assertTrue(
            "Fallback response should state it could not find a match",
            response.paragraph1.contains("I couldn't find a direct match")
        )
        assertTrue(
            "Fallback response should list the available STEM subjects",
            response.paragraph2.contains("Mathematics") && response.paragraph2.contains("Chemistry")
        )
    }

    @Test
    fun testStreamingResponse_emitsThinkingAndTokensProgressively() = runBlocking {
        val events = engine.streamResponse("hi", delayPerWordMs = 1L).toList()
        assertTrue("Must emit at least 3 events", events.size >= 3)
        assertTrue("First event must be Thinking", events.first() is LocalAiStreamToken.Thinking)
        assertTrue("Last event must be Completed", events.last() is LocalAiStreamToken.Completed)
        val tokenEvents = events.filterIsInstance<LocalAiStreamToken.Token>()
        assertTrue("Must emit token events", tokenEvents.isNotEmpty())
        assertEquals("Hi! How can I help you with your studies today?", (events.last() as LocalAiStreamToken.Completed).response.paragraph1)
    }
}
