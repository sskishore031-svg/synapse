package com.siamts.synapse

import com.siamts.synapse.data.local.ChatDao
import com.siamts.synapse.data.local.ChatMessageEntity
import com.siamts.synapse.data.local.ChatSessionEntity
import com.siamts.synapse.data.local.QuizLocalDatabase
import com.siamts.synapse.ui.tutor.ChatMessage
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class ChatSessionDaoTest {

    private lateinit var chatDao: ChatDao

    @Before
    fun setUp() {
        chatDao = QuizLocalDatabase.getChatDao(null)
    }

    @Test
    fun testInsertAndRetrieveSession() = runBlocking {
        val sessionId = UUID.randomUUID().toString()
        val session = ChatSessionEntity(
            sessionId = sessionId,
            userId = 42,
            title = "Differential Equations Study Session",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        chatDao.insertOrUpdateSession(session)

        val retrieved = chatDao.getSessionById(sessionId)
        assertNotNull(retrieved)
        assertEquals("Differential Equations Study Session", retrieved?.title)
        assertEquals(42, retrieved?.userId)

        val userSessions = chatDao.getAllSessions(42)
        assertTrue(userSessions.any { it.sessionId == sessionId })
    }

    @Test
    fun testSaveAndRetrieveMessagesForSession() = runBlocking {
        val sessionId = UUID.randomUUID().toString()
        val session = ChatSessionEntity(
            sessionId = sessionId,
            userId = 1,
            title = "Quantum Mechanics"
        )
        chatDao.insertOrUpdateSession(session)

        val messages = listOf(
            ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId,
                senderName = "You",
                text = "Explain Heisenberg uncertainty principle",
                isUser = true
            ),
            ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId,
                senderName = "SYNAPSE AI",
                text = "The uncertainty principle states that delta x * delta p >= hbar / 2.",
                isUser = false,
                matchedSubject = "Physics",
                matchedTopic = "Quantum Mechanics"
            )
        )

        chatDao.saveMessages(sessionId, messages)

        val retrieved = chatDao.getMessagesForSession(sessionId)
        assertEquals(2, retrieved.size)
        assertEquals("Explain Heisenberg uncertainty principle", retrieved[0].text)
        assertTrue(retrieved[1].text.contains("uncertainty principle"))
        assertEquals("Physics", retrieved[1].matchedSubject)
    }

    @Test
    fun testUpdateSessionTitle() = runBlocking {
        val sessionId = UUID.randomUUID().toString()
        val session = ChatSessionEntity(
            sessionId = sessionId,
            userId = 99,
            title = "Old Title"
        )
        chatDao.insertOrUpdateSession(session)

        chatDao.updateSessionTitle(sessionId, "Updated Title")

        val updated = chatDao.getSessionById(sessionId)
        assertEquals("Updated Title", updated?.title)
    }

    @Test
    fun testDeleteSession_removesSessionAndMessages() = runBlocking {
        val sessionId = UUID.randomUUID().toString()
        val session = ChatSessionEntity(
            sessionId = sessionId,
            userId = 101,
            title = "To Be Deleted"
        )
        chatDao.insertOrUpdateSession(session)

        val message = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            sessionId = sessionId,
            text = "Temporary message",
            isUser = true
        )
        chatDao.saveMessages(sessionId, listOf(message))

        chatDao.deleteSession(sessionId)

        assertNull(chatDao.getSessionById(sessionId))
        val remainingMessages = chatDao.getMessagesForSession(sessionId)
        assertTrue(remainingMessages.isEmpty())
    }

    @Test
    fun testChatMessageEntity_conversionMapping() {
        val domainMessage = ChatMessage(
            id = "msg-123",
            senderName = "SYNAPSE AI",
            text = "Thermodynamics first law: dU = dQ - dW",
            paragraph2 = "Core equation for closed system energy conservation.",
            isUser = false,
            matchedSubject = "Physics",
            matchedChapter = "Thermodynamics",
            matchedTopic = "First Law",
            isConceptAnswer = true,
            isSaved = true
        )

        val entity = ChatMessageEntity.fromChatMessage("session-456", domainMessage)
        assertEquals("session-456", entity.sessionId)
        assertEquals("msg-123", entity.id)
        assertEquals("Thermodynamics first law: dU = dQ - dW", entity.text)
        assertEquals("Physics", entity.matchedSubject)

        val restored = entity.toChatMessage()
        assertEquals(domainMessage.id, restored.id)
        assertEquals(domainMessage.text, restored.text)
        assertEquals(domainMessage.matchedTopic, restored.matchedTopic)
        assertTrue(restored.isConceptAnswer)
    }
}
