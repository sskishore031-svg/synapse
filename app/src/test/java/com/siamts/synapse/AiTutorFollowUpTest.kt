package com.siamts.synapse

import com.siamts.synapse.ai.KnowledgeChunk
import com.siamts.synapse.ai.LocalAIEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class AiTutorFollowUpTest {

    private lateinit var engine: LocalAIEngine

    @Before
    fun setUp() {
        engine = LocalAIEngine.getInstance()
        val possiblePaths = listOf(
            File("src/main/assets/educational_knowledge_base.json"),
            File("app/src/main/assets/educational_knowledge_base.json"),
            File("../app/src/main/assets/educational_knowledge_base.json"),
            File("C:/Users/SCAD LAB/AndroidStudioProjects/SYNAPSE/app/src/main/assets/educational_knowledge_base.json")
        )
        val jsonContent = possiblePaths.firstOrNull { it.exists() }?.readText()
        if (jsonContent != null) {
            engine.loadFromJson(jsonContent)
        }
        engine.resetContext()
    }

    @Test
    fun testFollowUpShortening_compressesPreviousTopic() {
        val conceptResp = engine.generateResponse("what is newton law?")
        assertNotNull(conceptResp.matchedTopic)

        val shortenQueries = listOf(
            "reply shortly",
            "make it short",
            "briefly",
            "summarize"
        )

        for (query in shortenQueries) {
            val shortResp = engine.generateResponse(query)
            assertTrue("Query '$query' should trigger context follow-up", shortResp.isFollowUp)
            assertNotNull("Should retain matched topic", shortResp.matchedTopic)
            assertTrue("Should contain concise summary points", shortResp.paragraph2.contains("Core Principle") || shortResp.paragraph2.contains("Takeaway"))
        }
    }

    @Test
    fun testFollowUpElaboration_deepensBreakdown() {
        val conceptResp = engine.generateResponse("explain eigenvalues")
        assertNotNull(conceptResp.matchedTopic)

        val elaborateQueries = listOf(
            "explain more",
            "elaborate",
            "break it down"
        )

        for (query in elaborateQueries) {
            val deepResp = engine.generateResponse(query)
            assertTrue("Query '$query' should trigger context follow-up", deepResp.isFollowUp)
            assertNotNull(deepResp.matchedTopic)
            assertTrue("Elaborated response should be detailed", deepResp.paragraph1.isNotBlank())
        }
    }

    @Test
    fun testBuildShortenedResponse_directChunk() {
        val chunk = KnowledgeChunk(
            chunkId = "chunk_math_01",
            subject = "Mathematics",
            chapter = "Calculus",
            topic = "Limits and Continuity",
            content = "A limit describes the value that a function approaches as the input approaches some value. Continuous functions have matching limits and values."
        )

        val response = engine.buildShortenedResponse(chunk)
        assertEquals("Mathematics", response.matchedSubject)
        assertEquals("Limits and Continuity", response.matchedTopic)
        assertTrue(response.isFollowUp)
        assertTrue(response.paragraph1.contains("Limits and Continuity"))
        assertTrue(response.paragraph2.contains("Core Principle"))
    }
}
