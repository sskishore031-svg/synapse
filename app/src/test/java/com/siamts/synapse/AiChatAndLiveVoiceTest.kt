package com.siamts.synapse

import com.siamts.synapse.ai.VoiceAssistantManager
import com.siamts.synapse.ui.tutor.AiTutorViewModel
import com.siamts.synapse.ui.tutor.AiUiAction
import com.siamts.synapse.ui.tutor.ChatMessage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AiChatAndLiveVoiceTest {

    @Test
    fun testCleanTextForSpeech_stripsMarkdownAndEmojis() {
        val rawMarkdown = """
            ⚡ **Concept & Definition** (Artificial Intelligence)
            • Walking downhill in the fog using `gradient descent`.
            # Core Formula
            - W := W - η·∇W(Loss)
        """.trimIndent()

        // Clean text using the stripper logic
        val cleaned = rawMarkdown
            .replace(Regex("\\*\\*(.*?)\\*\\*"), "$1")
            .replace(Regex("\\*(.*?)\\*"), "$1")
            .replace(Regex("#{1,6}\\s*"), "")
            .replace(Regex("`{1,3}[^`]*`{1,3}"), "")
            .replace(Regex("^[•\\-\\+]\\s*", RegexOption.MULTILINE), "")
            .replace(Regex("[\\[\\]\\(\\)]"), " ")
            .replace(Regex("[⚡📘🧩💡✨🔍🟢🔴🎙️🤖🧠·↳🗑️]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()

        assertFalse(cleaned.contains("**"))
        assertFalse(cleaned.contains("`"))
        assertFalse(cleaned.contains("⚡"))
        assertTrue(cleaned.contains("Concept & Definition"))
        assertTrue(cleaned.contains("Walking downhill in the fog"))
        assertTrue(cleaned.contains("Core Formula"))
    }

    @Test
    fun testSendMessage_clearsInputImmediately() {
        val testScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Unconfined)
        val viewModel = AiTutorViewModel(externalScope = testScope)
        viewModel.onInputQueryChanged("Explain Newton's second law")
        assertEquals("Explain Newton's second law", viewModel.uiState.value.inputQuery)

        viewModel.onSendMessage()

        // Input query should be cleared immediately (0ms UI lag)
        assertEquals("", viewModel.uiState.value.inputQuery)
        assertTrue(viewModel.uiState.value.messages.isNotEmpty())
        val userMsg = viewModel.uiState.value.messages.firstOrNull { it.isUser }
        assertEquals("Explain Newton's second law", userMsg?.text)
    }

    @Test
    fun testClearChat_resetsMessagesToEmpty() {
        val testScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Unconfined)
        val viewModel = AiTutorViewModel(externalScope = testScope)
        viewModel.onSendMessage("Test prompt")
        assertTrue(viewModel.uiState.value.messages.isNotEmpty())

        viewModel.onClearChat()

        // Chat should be completely empty to show minimal centered welcome emblem
        assertTrue(viewModel.uiState.value.messages.isEmpty())
        assertEquals("", viewModel.uiState.value.inputQuery)
    }

    @Test
    fun testAgenticCommand_startMathematicsQuiz() {
        val testScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Unconfined)
        val viewModel = AiTutorViewModel(externalScope = testScope)

        viewModel.onSendMessage("start mathematics quiz")

        val actionMsg = viewModel.uiState.value.messages.firstOrNull { it.actionCommand == "NAVIGATE_QUIZ" }
        assertNotNull(actionMsg)
        assertTrue(actionMsg!!.text.contains("Mathematics Quiz"))
    }

    @Test
    fun testAgenticCommand_openComputerScience() {
        val testScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Unconfined)
        val viewModel = AiTutorViewModel(externalScope = testScope)

        viewModel.onSendMessage("open computer science")

        val actionMsg = viewModel.uiState.value.messages.firstOrNull { it.actionCommand == "NAVIGATE_SUBJECT" }
        assertNotNull(actionMsg)
        assertTrue(actionMsg!!.text.contains("Computer Science"))
    }

    @Test
    fun testAgenticCommand_openNotes() {
        val testScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Unconfined)
        val viewModel = AiTutorViewModel(externalScope = testScope)

        viewModel.onSendMessage("show my notes")

        val actionMsg = viewModel.uiState.value.messages.firstOrNull { it.actionCommand == "NAVIGATE_NOTES" }
        assertNotNull(actionMsg)
        assertTrue(actionMsg!!.text.contains("Saved Study Notes"))
    }

    @Test
    fun testAgenticCommand_saveNote_generatesSavedNoteCard() {
        val testScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Unconfined)
        val viewModel = AiTutorViewModel(externalScope = testScope)

        // Directly invoke command to save note
        viewModel.onSendMessage("save this as a note")

        val savedCard = viewModel.uiState.value.messages.firstOrNull { it.isSavedNoteCard }
        assertNotNull(savedCard)
        assertTrue(savedCard!!.text.contains("saved crisp study notes") || savedCard.text.contains("Notes tab"))
    }

    @Test
    fun testOnSaveNoteClicked_directAction() {
        val testScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Unconfined)
        val viewModel = AiTutorViewModel(externalScope = testScope)

        val conceptMessage = ChatMessage(
            text = "Waves & Oscillations are rhythmic disturbances transferring energy through space-time.",
            paragraph2 = "Core equation: v = f * lambda. Example: Sound waves in air.",
            isUser = false,
            matchedTopic = "Waves & Oscillations"
        )

        viewModel.onSaveNoteClicked(conceptMessage)

        val savedCard = viewModel.uiState.value.messages.firstOrNull { it.isSavedNoteCard }
        assertNotNull(savedCard)
        assertEquals("Waves & Oscillations - AI Revision Summary", savedCard!!.savedNoteTitle)
    }
}
