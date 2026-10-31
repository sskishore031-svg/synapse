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
    fun testGreetings_returnConfiguredGreetingMessage() {
        val greetingQueries = listOf("gm", "good morning", "hi", "hello", "vanakkam", "hey")

        for (query in greetingQueries) {
            val response = engine.generateResponse(query)
            assertTrue("Query '$query' should be detected as greeting", response.isGreeting)
            assertTrue(
                "Greeting response must contain 'Good morning! I am your SYNAPSE AI tutor.'",
                response.paragraph1.contains("Good morning! I am your SYNAPSE AI tutor. Which subject or chapter would you like to explore today?")
            )
            assertTrue(
                "Greeting paragraph 2 must list subject chapters",
                response.paragraph2.contains("Quadratic Equations")
            )
        }
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
