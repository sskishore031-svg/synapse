package com.siamts.synapse

import com.siamts.synapse.ai.LocalAIEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Unit tests verifying on-device [LocalAIEngine] behavior, greetings detection,
 * educational dataset retrieval across all 6 subjects, multi-turn conversation memory,
 * and context-aware follow-up resolution.
 */
class LocalAIEngineTest {

    private lateinit var engine: LocalAIEngine

    @Before
    fun setUp() {
        engine = LocalAIEngine.getInstance()
        val possiblePaths = listOf(
            File("src/main/assets/all_subjects_expanded.json"),
            File("app/src/main/assets/all_subjects_expanded.json"),
            File("../app/src/main/assets/all_subjects_expanded.json"),
            File("C:/Users/SCAD LAB/AndroidStudioProjects/SYNAPSE/app/src/main/assets/all_subjects_expanded.json")
        )

        val jsonContent = possiblePaths.firstOrNull { it.exists() }?.readText()

        assertNotNull("Knowledge dataset json file must exist for tests", jsonContent)
        engine.loadFromJson(jsonContent!!)
        engine.resetContext()
    }

    @Test
    fun testDatasetLoaded_containsAllSubjectsAndChunks() {
        val chunks = engine.getAllChunks()
        assertTrue("Dataset should contain at least 30 chunks", chunks.size >= 30)

        val subjects = chunks.map { it.subject }.distinct()
        assertTrue("Contains Mathematics", subjects.contains("Mathematics"))
        assertTrue("Contains Computer Science", subjects.contains("Computer Science"))
        assertTrue("Contains Data Science", subjects.contains("Data Science"))
        assertTrue("Contains Artificial Intelligence", subjects.contains("Artificial Intelligence"))
        assertTrue("Contains Physics", subjects.contains("Physics"))
        assertTrue("Contains Chemistry", subjects.contains("Chemistry"))
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
    fun testCapabilitiesQuery_returnsSubjectsList() {
        val response = engine.generateResponse("what can you do?")
        assertFalse(response.isGreeting)
        assertTrue(response.paragraph1.contains("I am your SYNAPSE AI on-device tutor"))
        assertTrue(response.paragraph2.contains("Mathematics"))
        assertTrue(response.paragraph2.contains("Computer Science"))
        assertTrue(response.paragraph2.contains("Physics"))
        assertTrue(response.paragraph2.contains("Chemistry"))
    }

    @Test
    fun testMathematicsQuery_quadraticFormulaAndDiscriminant() {
        val response = engine.generateResponse("How do I find roots using the quadratic formula and discriminant?")
        assertFalse(response.isGreeting)
        assertEquals("Mathematics", response.matchedSubject)
        assertEquals("Quadratic Equations", response.matchedChapter)
        assertTrue(response.paragraph1.contains("Concept & Definition"))
        assertTrue(response.paragraph2.contains("Key Formula, Rule & Takeaway"))
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
        assertTrue(response.paragraph1.contains("Tufte") || response.paragraph1.contains("visual"))
    }

    @Test
    fun testAIQuery_gradientDescentAndNeuralNetworks() {
        val response = engine.generateResponse("How does gradient descent help a neural network learn?")
        assertFalse(response.isGreeting)
        assertEquals("Artificial Intelligence", response.matchedSubject)
        assertEquals("Neural Networks", response.matchedChapter)
        assertTrue(response.paragraph1.contains("Concept & Definition"))
    }

    @Test
    fun testPhysicsQuery_newtonsSecondLawAndWaves() {
        val response = engine.generateResponse("What is Newton's second law of motion and F = ma?")
        assertFalse(response.isGreeting)
        assertEquals("Physics", response.matchedSubject)
        assertEquals("Newton's Laws of Motion", response.matchedChapter)
        assertTrue(response.paragraph1.contains("Concept & Definition"))
    }

    @Test
    fun testChemistryQuery_acidsAndBasesBronstedLowry() {
        val response = engine.generateResponse("What are acids and bases according to Bronsted-Lowry?")
        assertFalse(response.isGreeting)
        assertEquals("Chemistry", response.matchedSubject)
        assertEquals("Acids and Bases", response.matchedChapter)
        assertTrue(response.paragraph1.contains("Concept & Definition"))
    }

    @Test
    fun testBiologySynthesis_photosynthesisAndDNA() {
        val photoResp = engine.generateResponse("Explain photosynthesis in plant biology")
        assertEquals("Biology", photoResp.matchedSubject)
        assertEquals("Photosynthesis", photoResp.matchedTopic)
        assertTrue(photoResp.paragraph1.contains("Concept & Definition"))
        assertTrue(photoResp.paragraph2.contains("6CO₂"))

        val dnaResp = engine.generateResponse("What is DNA and how does RNA transcription work?")
        assertEquals("Biology", dnaResp.matchedSubject)
        assertTrue(dnaResp.paragraph1.contains("Genetics"))
    }

    @Test
    fun testHistorySynthesis_worldWarAndCivilizations() {
        val ww2Resp = engine.generateResponse("Explain the causes and consequences of World War 2")
        assertEquals("History", ww2Resp.matchedSubject)
        assertEquals("World War II", ww2Resp.matchedTopic)
        assertTrue(ww2Resp.paragraph1.contains("1939–1945"))

        val civResp = engine.generateResponse("Tell me about the Indus Valley Civilization")
        assertEquals("History", civResp.matchedSubject)
        assertTrue(civResp.paragraph2.contains("Mohenjo-daro") || civResp.paragraph1.contains("Indus"))
    }

    @Test
    fun testGeographySynthesis_plateTectonicsAndAtmosphere() {
        val geoResp = engine.generateResponse("How does plate tectonics cause earthquakes?")
        assertEquals("Geography", geoResp.matchedSubject)
        assertEquals("Plate Tectonics and Seismic Activity", geoResp.matchedTopic)
        assertTrue(geoResp.paragraph1.contains("lithosphere"))
        assertTrue(geoResp.paragraph2.contains("Convergent"))
    }

    @Test
    fun testMultiTurnFollowUp_resolvesTopicFromPreviousTurn() {
        // Turn 1: Primary educational question
        val turn1 = engine.generateResponse("How do I solve quadratic equations?")
        assertEquals("Mathematics", turn1.matchedSubject)
        assertEquals("Quadratic Equations", turn1.matchedChapter)

        // Turn 2: Follow-up requesting an example
        val turn2 = engine.generateResponse("give an example")
        assertTrue("Follow-up flag should be set", turn2.isFollowUp)
        assertEquals("Mathematics", turn2.matchedSubject)
        assertTrue("Response should contain practical example", turn2.paragraph1.contains("Practical Example"))
        assertTrue("Response should contain projectile trajectory example", turn2.paragraph2.contains("projectile"))

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
        // Prime context
        engine.generateResponse("Explain Newton's second law of motion")
        assertTrue(engine.getContextHistory().isNotEmpty())

        // Reset
        engine.resetContext()
        assertEquals(0, engine.getContextHistory().size)

        // Unrelated query should now hit fallback instead of previous context
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
}
