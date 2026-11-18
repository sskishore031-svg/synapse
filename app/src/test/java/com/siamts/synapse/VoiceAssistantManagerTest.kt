package com.siamts.synapse

import com.siamts.synapse.ai.VoiceAssistantManager
import com.siamts.synapse.ai.VoiceState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [VoiceAssistantManager] text sanitization and state representations.
 */
class VoiceAssistantManagerTest {

    @Test
    fun testCleanTextForSpeech_stripsBoldAndItalicMarkdown() {
        val input = "Here is **Newton's Second Law** which states *F = ma* in classical physics."
        val result = VoiceAssistantManager.cleanTextForSpeech(input)
        assertEquals("Here is Newton's Second Law which states F = ma in classical physics.", result)
    }

    @Test
    fun testCleanTextForSpeech_stripsMarkdownHeaders() {
        val input = "### Chapter 1: Introduction\n## Key Formulas\n# Overview"
        val result = VoiceAssistantManager.cleanTextForSpeech(input)
        assertEquals("Chapter 1: Introduction Key Formulas Overview", result)
    }

    @Test
    fun testCleanTextForSpeech_stripsCodeBlocks() {
        val input = "To sort an array, run ```val sorted = arr.sorted()``` in Kotlin."
        val result = VoiceAssistantManager.cleanTextForSpeech(input)
        assertEquals("To sort an array, run in Kotlin.", result)
    }

    @Test
    fun testCleanTextForSpeech_removesUiEmojis() {
        val input = "⚡ Neural Core 📘 Mathematics 💡 Concept 🤖 SYNAPSE AI 🧠 Memory"
        val result = VoiceAssistantManager.cleanTextForSpeech(input)
        assertEquals("Neural Core Mathematics Concept SYNAPSE AI Memory", result)
    }

    @Test
    fun testCleanTextForSpeech_stripsBulletPointsAndNormalizesWhitespace() {
        val input = """
            Key concepts:
            • Vectors and matrices
            - Eigenvalues and eigenvectors
            + Determinant calculations
        """.trimIndent()
        val result = VoiceAssistantManager.cleanTextForSpeech(input)
        assertTrue(result.contains("Vectors and matrices"))
        assertTrue(result.contains("Eigenvalues and eigenvectors"))
        assertTrue(result.contains("Determinant calculations"))
        assertFalse(result.contains("•"))
        assertFalse(result.contains("+"))
    }

    @Test
    fun testCleanTextForSpeech_removesBrackets() {
        val input = "The kinetic energy formula is [E = 0.5 * m * v^2] (derived from work)."
        val result = VoiceAssistantManager.cleanTextForSpeech(input)
        assertFalse(result.contains("["))
        assertFalse(result.contains("]"))
        assertFalse(result.contains("("))
        assertFalse(result.contains(")"))
        assertTrue(result.contains("derived from work"))
    }

    @Test
    fun testVoiceState_enumContainsExpectedStates() {
        val states = VoiceState.values().map { it.name }.toSet()
        val expected = setOf("IDLE", "LISTENING", "THINKING", "SPEAKING", "PAUSED", "ERROR")
        assertEquals(expected, states)
    }
}

