package com.siamts.synapse

import com.siamts.synapse.ai.KnowledgeChunk
import com.siamts.synapse.ai.LocalAIEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Unit tests for SYNAPSE AI Chat Engine persona, dynamic nickname memory,
 * style/formatting preferences (suppressing formulas/examples, no emojis),
 * natural farewells, and graceful casual/unclear slang handling.
 */
class ChatEnginePreferenceAndPersonaTest {

    private lateinit var engine: LocalAIEngine

    @Before
    fun setUp() {
        engine = LocalAIEngine.getInstance()
        engine.preferredName = "Kishore"
        engine.includeFormulas = true
        engine.includeExamples = true
        engine.useEmojis = false
        engine.consecutiveUnclearCount = 0
        engine.isWaitingForName = false
        engine.clearHistory()

        // Load knowledge chunks from assets if needed
        val assetFile = File("src/main/assets/educational_knowledge_base.json")
        val altAssetFile = File("app/src/main/assets/educational_knowledge_base.json")
        val target = if (assetFile.exists()) assetFile else if (altAssetFile.exists()) altAssetFile else null
        if (target != null) {
            engine.loadFromJson(target.readText())
        }
    }

    @Test
    fun testNameSetting_englishCallMe() {
        engine.preferredName = ""
        val response = engine.generateResponse("call me Kishore")
        assertEquals("Got it, Kishore! From now on, I will call you Kishore.", response.paragraph1)
        assertEquals("Kishore", engine.preferredName)
        assertTrue(response.isNameCapture)
        assertEquals("Kishore", response.capturedName)
    }

    @Test
    fun testNameSetting_tanglishEnodaName() {
        engine.preferredName = ""
        val response = engine.generateResponse("enoda name Kishore")
        assertEquals("Got it, Kishore! From now on, I will call you Kishore.", response.paragraph1)
        assertEquals("Kishore", engine.preferredName)
        assertTrue(response.isNameCapture)
    }

    @Test
    fun testNameSetting_tanglishKupdu() {
        engine.preferredName = ""
        val response = engine.generateResponse("enakku Kishore nu kupdu")
        assertEquals("Got it, Kishore! From now on, I will call you Kishore.", response.paragraph1)
        assertEquals("Kishore", engine.preferredName)
    }

    @Test
    fun testNameSetting_askNameFollowup() {
        engine.preferredName = ""
        val promptResp = engine.generateResponse("call me by my name")
        assertEquals("Sure! What name would you like me to call you?", promptResp.paragraph1)
        assertTrue(engine.isWaitingForName)

        val answerResp = engine.generateResponse("Kishore")
        assertEquals("Got it, Kishore! From now on, I will call you Kishore.", answerResp.paragraph1)
        assertEquals("Kishore", engine.preferredName)
        assertFalse(engine.isWaitingForName)
    }

    @Test
    fun testStylePreference_disableEmoji() {
        engine.useEmojis = true
        val response = engine.generateResponse("dont use emoji")
        assertFalse(engine.useEmojis)
        assertTrue(response.paragraph1.contains("Got it, Kishore! I'll keep all responses clean with * bullet points and no emojis."))
        assertTrue(response.isPreferenceUpdate)
    }

    @Test
    fun testStylePreference_suppressFormulaAndExample() {
        val response = engine.generateResponse("no need of formula and example")
        assertFalse(engine.includeFormulas)
        assertFalse(engine.includeExamples)
        assertTrue(response.paragraph1.contains("Got it, Kishore! I'll provide only pure concepts and definitions without formulas or examples"))
        assertTrue(response.isPreferenceUpdate)
    }

    @Test
    fun testStylePreference_suppressFormulaOnly() {
        val response = engine.generateResponse("no formula")
        assertFalse(engine.includeFormulas)
        assertTrue(engine.includeExamples)
        assertTrue(response.paragraph1.contains("Got it, Kishore! I'll omit formulas and focus on concepts and practical examples."))
    }

    @Test
    fun testStylePreference_suppressExampleOnly() {
        val response = engine.generateResponse("without example")
        assertTrue(engine.includeFormulas)
        assertFalse(engine.includeExamples)
        assertTrue(response.paragraph1.contains("Got it, Kishore! I'll omit examples and focus on core definitions and formulas."))
    }

    @Test
    fun testFarewell_bye() {
        val resp1 = engine.generateResponse("bye")
        assertEquals("Okay bye, Kishore! We'll continue tomorrow.", resp1.paragraph1)
        assertTrue(resp1.isFarewell)

        val resp2 = engine.generateResponse("goodbye")
        assertEquals("Okay bye, Kishore! We'll continue tomorrow.", resp2.paragraph1)
    }

    @Test
    fun testCasualSlang_bhaiGracefulHandling() {
        // First time unclear input with name set
        val resp1 = engine.generateResponse("bhai")
        assertEquals("I didn't understand that, Kishore. Could you please explain clearly which subject or topic you need help with?", resp1.paragraph1)
        assertEquals(1, engine.consecutiveUnclearCount)

        // Second time unclear input with name set
        val resp2 = engine.generateResponse("bhai")
        assertEquals("I don't know about that, Kishore. Please ask something related to your subjects, study notes, or quizzes.", resp2.paragraph1)
        assertEquals(2, engine.consecutiveUnclearCount)

        // Does NOT hallucinate Euler's Formula or other random academic topics
        assertFalse(resp1.paragraph1.contains("Euler"))
        assertFalse(resp2.paragraph1.contains("Euler"))
    }

    @Test
    fun testNameFreeByDefault_unclearAndFarewells() {
        engine.preferredName = ""
        engine.consecutiveUnclearCount = 0

        // First unclear when name is blank
        val resp1 = engine.generateResponse("bhai")
        assertEquals("I didn't understand that. Could you please explain clearly which subject or topic you need help with?", resp1.paragraph1)

        // Second unclear when name is blank
        val resp2 = engine.generateResponse("bhai")
        assertEquals("I don't know about that. Please ask something related to your subjects, study notes, or quizzes.", resp2.paragraph1)

        // Farewell when name is blank
        val respBye = engine.generateResponse("bye")
        assertEquals("Okay bye! We'll continue tomorrow.", respBye.paragraph1)

        // Style preference confirmation when name is blank
        val respEmoji = engine.generateResponse("dont use emoji")
        assertTrue(respEmoji.paragraph1.contains("Got it! I'll keep all responses clean with * bullet points and no emojis."))

        // Out-of-scope response
        val respOos = engine.generateResponse("who is rajinikanth")
        assertEquals(LocalAIEngine.OUT_OF_SCOPE_RESPONSE, respOos.paragraph1)
    }

    @Test
    fun testGlobalNoEmojiFormatting() {
        engine.useEmojis = false
        val response = engine.buildEducationalResponse(
            KnowledgeChunk(
                chunkId = "TEST_001",
                subject = "Physics",
                chapter = "Mechanics",
                topic = "Newton's Second Law",
                definition = "Force equals mass times acceleration.",
                formulaOrCoreRule = "F = m * a",
                workedExample = "A 10kg object accelerates at 2 m/s^2 when 20N force is applied."
            ),
            score = 25.0
        )

        // Verify * bullet points used, zero emojis present
        assertTrue(response.paragraph1.startsWith("* **Concept & Definition**"))
        assertFalse(response.paragraph1.contains("📘"))
        assertFalse(response.paragraph2.contains("⚡"))
        assertFalse(response.paragraph2.contains("💡"))
        assertTrue(response.paragraph2.contains("* **Formula / Core Principle:** F = m * a"))
        assertTrue(response.paragraph2.contains("* **Key Example & Practical Takeaway:** A 10kg object accelerates at 2 m/s^2 when 20N force is applied."))
    }

    @Test
    fun testSubjectOverview_whatAboutPython() {
        val response = engine.generateResponse("what about python")
        assertEquals("Python & Coding Logic", response.matchedSubject)
        assertTrue(response.paragraph1.contains("* **What it is:**"))
        assertTrue(response.paragraph1.contains("Python & Coding Logic"))
        assertTrue(response.paragraph1.contains("* **Key Uses & Real-World Work:**"))
        assertTrue(response.paragraph1.contains("* **Advantages:**"))
        assertTrue(response.paragraph1.contains("* **Disadvantages / Challenges:**"))
    }

    @Test
    fun testSubjectOverview_usesOfSql() {
        val response = engine.generateResponse("uses of sql")
        assertEquals("SQL & Database Management", response.matchedSubject)
        assertTrue(response.paragraph1.contains("* **What it is:**"))
        assertTrue(response.paragraph1.contains("* **Key Uses & Real-World Work:**"))
        assertTrue(response.paragraph1.contains("* **Advantages:**"))
        assertTrue(response.paragraph1.contains("* **Disadvantages / Challenges:**"))
    }

    @Test
    fun testSubjectOverview_tellMeAboutAi() {
        val response = engine.generateResponse("tell me about modern tech")
        assertEquals("AI & Modern Tech Concepts", response.matchedSubject)
        assertTrue(response.paragraph1.contains("* **What it is:**"))
        assertTrue(response.paragraph1.contains("* **Key Uses & Real-World Work:**"))
    }

    @Test
    fun testSubjectOverview_customCollegeSubject() {
        val response = engine.generateResponse("what about marine biology")
        assertEquals("Marine Biology", response.matchedSubject)
        assertTrue(response.paragraph1.contains("* **What it is:**"))
        assertTrue(response.paragraph1.contains("Marine Biology is an academic discipline"))
        assertTrue(response.paragraph1.contains("* **Key Uses & Real-World Work:**"))
        assertTrue(response.paragraph1.contains("* **Advantages:**"))
        assertTrue(response.paragraph1.contains("* **Disadvantages / Challenges:**"))
    }
}

