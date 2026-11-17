package com.siamts.synapse

import com.siamts.synapse.ai.SlmInferenceEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SlmInferenceEngineTest {

    @Test
    fun testSystemPrompt_matchesRequirement() {
        assertTrue(SlmInferenceEngine.SYSTEM_PROMPT.contains("You are SYNAPSE AI, an intelligent offline college study companion"))
        assertTrue(SlmInferenceEngine.SYSTEM_PROMPT.contains("50+ university subjects"))
        assertTrue(SlmInferenceEngine.SYSTEM_PROMPT.contains("Be clear, encouraging, accurate, and concise."))
    }

    @Test
    fun testBuildAugmentedPrompt_injectsSystemPromptAndContext() {
        val engine = SlmInferenceEngine.getInstance(null)
        val prompt = engine.buildAugmentedPrompt(
            userQuery = "What is an eigenvalue?",
            retrievedContext = "Eigenvalues satisfy Av = λv."
        )

        assertTrue(prompt.contains("System: " + SlmInferenceEngine.SYSTEM_PROMPT))
        assertTrue(prompt.contains("Curriculum Context:\nEigenvalues satisfy Av = λv."))
        assertTrue(prompt.contains("Student: What is an eigenvalue?"))
        assertTrue(prompt.endsWith("SYNAPSE AI:"))
    }

    @Test
    fun testBuildAugmentedPrompt_withoutContext_formatsCleanly() {
        val engine = SlmInferenceEngine.getInstance(null)
        val prompt = engine.buildAugmentedPrompt(
            userQuery = "How are you?",
            retrievedContext = null
        )

        assertTrue(prompt.contains("System: " + SlmInferenceEngine.SYSTEM_PROMPT))
        assertFalse(prompt.contains("Curriculum Context:"))
        assertTrue(prompt.contains("Student: How are you?"))
        assertTrue(prompt.endsWith("SYNAPSE AI:"))
    }

    @Test
    fun testModelAvailability_doesNotCrashWhenModelAbsent() {
        val engine = SlmInferenceEngine.getInstance(null)
        val available = engine.isModelAvailable()
        // In local desktop test environment without device storage binary, returns false cleanly
        assertFalse(available)
        val initSuccess = engine.initialize()
        assertFalse(initSuccess)
    }
}

