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
        engine.preferredName = ""
        engine.accountUserName = ""
        engine.resetContext()
    }

    @Test
    fun testDatasetLoaded_containsAllSubjectsAndChunks() {
        val chunks = engine.getAllChunks()
        assertTrue("Dataset should contain at least 25 chunks", chunks.size >= 25)

        val subjects = chunks.map { it.subject }.distinct()
        assertTrue("Contains Python & Coding Logic", subjects.any { it.contains("Python", ignoreCase = true) })
        assertTrue("Contains SQL & Database Management", subjects.any { it.contains("SQL", ignoreCase = true) })
        assertTrue("Contains AI & Modern Tech Concepts", subjects.any { it.contains("AI", ignoreCase = true) })
        assertTrue("Contains Aptitude & Interview Prep", subjects.any { it.contains("Aptitude", ignoreCase = true) })
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
    fun testEducationalQuery_PythonMemoryModel_intact() {
        val response = engine.generateResponse("explain python memory model and mutability")
        assertFalse(response.isGreeting)
        assertTrue(response.matchedSubject?.contains("Python") == true)
        assertTrue(response.paragraph1.contains("Concept & Definition") || response.paragraph1.contains("Python") || response.paragraph1.contains("Memory"))
        assertFalse("Must not trigger fallback", response.paragraph1.contains("couldn't find a direct match"))
    }

    @Test
    fun testTopicSuggestionIntent_pythonTopics_returnsStructuredListAndStartingPoint() {
        val pyResponse = engine.generateResponse("give me some python topic")
        assertFalse(pyResponse.isGreeting)
        assertTrue("Should be flagged as topic suggestion", pyResponse.isTopicSuggestion)
        assertTrue(pyResponse.matchedSubject?.contains("Python", ignoreCase = true) == true)
        assertTrue(pyResponse.paragraph1.contains("Topics & Study Roadmap") || pyResponse.paragraph1.contains("Python"))
        assertTrue(pyResponse.paragraph2.contains("Where to Begin:"))

        val sqlResponse = engine.generateResponse("suggest sql chapters")
        assertFalse(sqlResponse.isGreeting)
        assertTrue("Should be flagged as topic suggestion", sqlResponse.isTopicSuggestion)
        assertTrue(sqlResponse.matchedSubject?.contains("SQL", ignoreCase = true) == true)
        assertTrue(sqlResponse.paragraph2.contains("Where to Begin:"))

        val aptResponse = engine.generateResponse("list aptitude topics")
        assertFalse(aptResponse.isGreeting)
        assertTrue("Should be flagged as topic suggestion", aptResponse.isTopicSuggestion)
        assertTrue(aptResponse.matchedSubject?.contains("Aptitude", ignoreCase = true) == true)
    }

    @Test
    fun testTopicSuggestionIntent_general_returnsAllSubjectsCatalog() {
        val response = engine.generateResponse("what can i learn")
        assertFalse(response.isGreeting)
        assertTrue("Should be flagged as topic suggestion", response.isTopicSuggestion)
        assertTrue(response.paragraph1.contains("Placement & Micro-Learning Tracks") || response.paragraph1.contains("Tracks") || response.paragraph1.contains("Catalog"))
        assertTrue(response.paragraph2.contains("Python"))
        assertTrue(response.paragraph2.contains("SQL"))
        assertTrue(response.paragraph2.contains("AI"))
        assertTrue(response.paragraph2.contains("Aptitude"))
    }

    @Test
    fun testConceptualExplanation_pythonComprehensions() {
        val response = engine.generateResponse("explain list comprehensions in python")
        assertFalse(response.isGreeting)
        assertTrue(response.matchedSubject?.contains("Python") == true)
        assertTrue(response.paragraph1.contains("Concept & Definition"))
        assertTrue(response.paragraph2.contains("Formula") || response.paragraph2.contains("Core Rule") || response.paragraph2.contains("Rule") || response.paragraph2.contains("Takeaway"))
    }

    @Test
    fun testConceptualExplanation_sqlJoins() {
        val response = engine.generateResponse("what are sql joins in databases?")
        assertFalse(response.isGreeting)
        assertTrue(response.matchedSubject?.contains("SQL") == true)
        assertTrue(response.paragraph1.contains("Concept & Definition"))
        assertTrue(response.confidenceScore >= 5.0)
    }

    @Test
    fun testConceptualExplanation_acidProperties() {
        val response = engine.generateResponse("define acid properties in database transactions")
        assertFalse(response.isGreeting)
        assertTrue(response.matchedSubject?.contains("SQL") == true)
        assertTrue(response.paragraph1.contains("Concept & Definition"))
    }

    @Test
    fun testComputerScienceQuery_ragArchitecture() {
        val response = engine.generateResponse("explain rag architecture and vector search in ai")
        assertFalse(response.isGreeting)
        assertTrue(response.matchedSubject?.contains("AI") == true)
        assertTrue(response.paragraph1.contains("Concept & Definition"))
        assertTrue(response.confidenceScore >= 5.0)
    }

    @Test
    fun testDataScienceQuery_starMethod() {
        val response = engine.generateResponse("explain the star behavioral interview framework in aptitude")
        assertFalse(response.isGreeting)
        assertTrue(response.matchedSubject?.contains("Aptitude") == true)
        assertTrue(response.paragraph1.contains("Concept & Definition"))
    }

    @Test
    fun testAIQuery_largeLanguageModels() {
        val response = engine.generateResponse("explain transformers and subword tokenization in ai")
        assertFalse(response.isGreeting)
        assertTrue(response.matchedSubject?.contains("AI") == true)
        assertTrue(response.paragraph1.contains("Concept & Definition"))
    }

    @Test
    fun testMultiTurnFollowUp_resolvesTopicFromPreviousTurn() {
        // Turn 1: Primary educational question
        val turn1 = engine.generateResponse("How does Python pass by object reference work?")
        assertTrue(turn1.matchedSubject?.contains("Python") == true)

        // Turn 2: Follow-up requesting an example
        val turn2 = engine.generateResponse("give an example")
        assertTrue("Follow-up flag should be set", turn2.isFollowUp)
        assertTrue(turn2.matchedSubject?.contains("Python") == true)
        assertTrue("Response should contain practical example", turn2.paragraph1.contains("Practical Example"))

        // Turn 3: Follow-up requesting more details
        val turn3 = engine.generateResponse("explain more")
        assertTrue("Follow-up flag should be set", turn3.isFollowUp)
        assertTrue(turn3.matchedSubject?.contains("Python") == true)
        assertTrue(turn3.paragraph1.contains("In-Depth Breakdown"))
    }

    @Test
    fun testResetContext_clearsRollingMemory() {
        engine.generateResponse("Explain python mutability and memory model")
        assertTrue(engine.getContextHistory().isNotEmpty())

        engine.resetContext()
        assertEquals(0, engine.getContextHistory().size)

        val response = engine.generateResponse("what is the recipe for baking a chocolate cake?")
        assertTrue(response.confidenceScore > 0.0)
        assertFalse("Must never contain fallback error", response.paragraph1.contains("couldn't find a direct match"))
        assertEquals(LocalAIEngine.OUT_OF_SCOPE_RESPONSE, response.paragraph1)
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
        assertEquals(
            LocalAIEngine.OUT_OF_SCOPE_RESPONSE,
            response.paragraph1
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
        assertTrue(response.paragraph1.contains("career tracks") || response.paragraph1.contains("tracks"))
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
            assertTrue("Query '$q' should mention 4 Core Tracks", response.paragraph1.contains("4 Core Tracks") || response.paragraph1.contains("4") || response.paragraph1.contains("Tracks"))
            assertTrue("Query '$q' should mention core tracks", response.paragraph2.contains("Python") && response.paragraph2.contains("SQL"))
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
    fun testConceptQuery_whatIsPythonMutability_matchesMutability() {
        val response = engine.generateResponse("what is python mutability and memory model?")
        assertFalse("Must never contain fallback error", response.paragraph1.contains("couldn't find a direct match"))
        assertTrue("Must match Python Mutability", response.matchedTopic?.contains("Mutability", ignoreCase = true) == true || response.matchedSubject?.contains("Python", ignoreCase = true) == true)
    }

    @Test
    fun testConceptQuery_whatIsSqlAcid_matchesAcid() {
        val response = engine.generateResponse("what are acid properties in database transactions?")
        assertFalse("Must never contain fallback error", response.paragraph1.contains("couldn't find a direct match"))
        assertTrue("Must match ACID Properties", response.matchedTopic?.contains("ACID", ignoreCase = true) == true || response.matchedSubject?.contains("SQL", ignoreCase = true) == true)
    }

    @Test
    fun testConceptQuery_whatIsRagVectorSearch_matchesRag() {
        val response = engine.generateResponse("what is rag architecture and vector search in ai?")
        assertFalse("Must never contain fallback error", response.paragraph1.contains("couldn't find a direct match"))
        assertTrue("Must match RAG Architecture", response.matchedTopic?.contains("RAG", ignoreCase = true) == true || response.matchedSubject?.contains("AI", ignoreCase = true) == true)
    }

    @Test
    fun testConceptQuery_whatIsStarMethod_matchesStar() {
        val response = engine.generateResponse("what is the star behavioral interview framework in aptitude?")
        assertFalse("Must never contain fallback error", response.paragraph1.contains("couldn't find a direct match"))
        assertTrue("Must match STAR Framework", response.matchedTopic?.contains("STAR", ignoreCase = true) == true || response.matchedSubject?.contains("Aptitude", ignoreCase = true) == true)
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

    @Test
    fun testCodeAnalysis_pythonSnippet() {
        val code = """
            def find_duplicates(nums):
                seen = set()
                duplicates = []
                for num in nums:
                    if num in seen:
                        duplicates.append(num)
                    seen.add(num)
                return duplicates
        """.trimIndent()
        val response = engine.generateResponse(code)
        assertTrue("Should detect Python Code", response.paragraph1.contains("Python Code Logic Breakdown"))
        assertTrue("Should compute Time & Space Complexity", response.paragraph2.contains("Time & Space Complexity"))
        assertTrue("Should list Placement Interview Edge Cases", response.paragraph2.contains("Placement Interview Edge Cases"))
        assertEquals("Python & Coding Logic", response.matchedSubject)
    }

    @Test
    fun testCodeAnalysis_sqlQuery() {
        val query = """
            SELECT d.dept_name, COUNT(e.emp_id) AS total_staff, AVG(e.salary) AS avg_sal
            FROM departments d
            INNER JOIN employees e ON d.dept_id = e.dept_id
            WHERE e.active = 1
            GROUP BY d.dept_name
            HAVING AVG(e.salary) > 75000
            ORDER BY avg_sal DESC;
        """.trimIndent()
        val response = engine.generateResponse(query)
        assertTrue("Should detect SQL Query", response.paragraph1.contains("SQL Query Logic Breakdown"))
        assertTrue("Should compute Time & Space Complexity", response.paragraph2.contains("Time & Space Complexity"))
        assertTrue("Should highlight NULL and Cartesian traps", response.paragraph2.contains("Placement Interview Edge Cases"))
        assertEquals("SQL & Database Management", response.matchedSubject)
    }

    @Test
    fun testContextFollowUp_whyAndInterviewQuestions() {
        // First turn: Learn about SQL Joins
        val r1 = engine.generateResponse("what is sql inner join and left join?")
        assertNotNull(r1.matchedTopic)

        // Second turn: Ask "interview questions on this"
        val r2 = engine.generateResponse("interview questions on this")
        assertTrue("Should be follow-up", r2.isFollowUp)
        assertTrue("Should generate interview questions", r2.paragraph1.contains("Placement Interview Preparation"))
        assertTrue("Should contain takeaway", r2.paragraph2.contains("Takeaway"))

        // Third turn: Ask "why?"
        val r3 = engine.generateResponse("why?")
        assertTrue("Should be follow-up", r3.isFollowUp)
        assertTrue("Should provide engineering rationale", r3.paragraph1.contains("Engineering Rationale"))
        assertTrue("Should contain Core Principle & Takeaway", r3.paragraph2.contains("Core Principle") || r3.paragraph2.contains("Takeaway"))
    }

    @Test
    fun testEducationalResponse_containsAnalogyAndInterviewTip() {
        val response = engine.generateResponse("explain python global interpreter lock")
        assertTrue("Should contain Real-Life Analogy", response.paragraph1.contains("Real-Life Analogy") || response.paragraph1.contains("Concept & Definition"))
        assertTrue("Should contain Core Principle", response.paragraph2.contains("Core Principle"))
        assertTrue("Should contain Placement Interview Tip / Takeaway", response.paragraph2.contains("Takeaway") || response.paragraph2.contains("Placement Interview Tip"))
    }

    @Test
    fun testStrictScopeGuard_redirectsNonTechnical() {
        val nonTechQueries = listOf(
            "who is rajinikanth",
            "tell me a recipe for chicken biryani",
            "what was the cricket match score",
            "will you marry me",
            "ena pandra",
            "enna pandra"
        )
        for (q in nonTechQueries) {
            val response = engine.generateResponse(q)
            assertTrue("Query '$q' must be caught by scope guard", response.paragraph1.contains("I don't have the answer to that") || response.paragraph1.contains("falls outside my placement") || response.paragraph1.contains("I didn't understand that"))
        }
    }

    @Test
    fun testDirectQuestionIntent_pythonMeansSnake_answersMontyPythonDirectly() {
        val testQueries = listOf(
            "python means snake?",
            "python means snake",
            "is python named after snake",
            "why is it called python",
            "why python"
        )
        for (query in testQueries) {
            val response = engine.generateResponse(query)
            assertFalse("Should never dump mutability for origin query '$query'", response.paragraph1.contains("Python Memory Model & Mutability"))
            assertTrue("Should explain Monty Python origin for '$query'", response.paragraph1.contains("No, Python is not named after the snake") || response.paragraph1.contains("Monty Python"))
            assertTrue("Should contain placement topics to explore", response.paragraph2.contains("Key Placement Topics to Explore"))
            assertEquals("Python & Coding Logic", response.matchedSubject)
            assertTrue(response.confidenceScore >= 95.0)
        }
    }

    @Test
    fun testDirectQuestionIntent_whoCreatedAndFullForms() {
        val pyCreatorResp = engine.generateResponse("who created python")
        assertTrue("Should name Guido van Rossum", pyCreatorResp.paragraph1.contains("Guido van Rossum"))
        assertTrue("Should mention 1991", pyCreatorResp.paragraph1.contains("1991"))
        assertEquals("Python & Coding Logic", pyCreatorResp.matchedSubject)

        val sqlCreatorResp = engine.generateResponse("who created sql")
        assertTrue("Should name Chamberlin & Boyce", sqlCreatorResp.paragraph1.contains("Donald D. Chamberlin") && sqlCreatorResp.paragraph1.contains("Raymond F. Boyce"))
        assertTrue("Should mention IBM", sqlCreatorResp.paragraph1.contains("IBM"))
        assertEquals("SQL & Database Management", sqlCreatorResp.matchedSubject)

        val sqlFullFormResp = engine.generateResponse("what is the full form of sql")
        assertTrue("Should state Structured Query Language", sqlFullFormResp.paragraph1.contains("Structured Query Language"))
        assertTrue("Should mention DDL, DML, TCL", sqlFullFormResp.paragraph2.contains("DDL") && sqlFullFormResp.paragraph2.contains("DML"))

        val llmFullFormResp = engine.generateResponse("full form of llm")
        assertTrue("Should state Large Language Model", llmFullFormResp.paragraph1.contains("Large Language Model"))
        assertTrue("Should mention Pre-training & RAG", llmFullFormResp.paragraph2.contains("Pre-training") && llmFullFormResp.paragraph2.contains("RAG"))
        assertEquals("AI & Modern Tech Concepts", llmFullFormResp.matchedSubject)
    }

    @Test
    fun testDirectQuestionIntent_comparisons() {
        val listVsTuple = engine.generateResponse("list vs tuple")
        assertTrue("Should compare mutable vs immutable", listVsTuple.paragraph1.contains("mutable") && listVsTuple.paragraph1.contains("immutable"))
        assertEquals("List vs Tuple", listVsTuple.matchedTopic)

        val sqlVsNosql = engine.generateResponse("sql vs nosql")
        assertTrue("Should compare ACID vs BASE", sqlVsNosql.paragraph1.contains("ACID") && sqlVsNosql.paragraph1.contains("BASE"))
        assertEquals("SQL vs NoSQL", sqlVsNosql.matchedTopic)

        val delVsTrunc = engine.generateResponse("delete vs truncate")
        assertTrue("Should compare DML vs DDL", delVsTrunc.paragraph1.contains("DML") && delVsTrunc.paragraph1.contains("DDL"))
        assertEquals("DELETE vs TRUNCATE", delVsTrunc.matchedTopic)

        val pkVsFk = engine.generateResponse("primary key vs foreign key")
        assertTrue("Should compare PK and FK", pkVsFk.paragraph1.contains("Primary Key") && pkVsFk.paragraph1.contains("Foreign Key"))
        assertEquals("Primary Key vs Foreign Key", pkVsFk.matchedTopic)

        val aiVsMl = engine.generateResponse("ai vs ml")
        assertTrue("Should compare AI and ML", aiVsMl.paragraph1.contains("Artificial Intelligence") && aiVsMl.paragraph1.contains("Machine Learning"))
        assertEquals("AI vs ML", aiVsMl.matchedTopic)

        val whereVsHaving = engine.generateResponse("where vs having")
        assertTrue("Should compare WHERE and HAVING", whereVsHaving.paragraph1.contains("WHERE") && whereVsHaving.paragraph1.contains("HAVING"))
        assertEquals("WHERE vs HAVING", whereVsHaving.matchedTopic)

        val isVsEquals = engine.generateResponse("is vs ==")
        assertTrue("Should compare value equality and memory identity", isVsEquals.paragraph1.contains("value equality") && isVsEquals.paragraph1.contains("memory identity"))
        assertEquals("Object Identity vs Value Equality (`is` vs `==`)", isVsEquals.matchedTopic)
    }

    @Test
    fun testDirectQuestionIntent_howToCodeSnippets() {
        val reverseStr = engine.generateResponse("how to reverse a string in python")
        assertTrue("Should contain slicing and two-pointer", reverseStr.paragraph1.contains("[::-1]") && reverseStr.paragraph1.contains("reverse_two_pointer"))
        assertEquals("Python & Coding Logic", reverseStr.matchedSubject)

        val secondSalary = engine.generateResponse("find second highest salary in sql")
        assertTrue("Should contain subquery and DENSE_RANK", secondSalary.paragraph1.contains("MAX(salary)") && secondSalary.paragraph1.contains("DENSE_RANK"))
        assertEquals("SQL & Database Management", secondSalary.matchedSubject)

        val palindrome = engine.generateResponse("check palindrome in python")
        assertTrue("Should contain is_palindrome function", palindrome.paragraph1.contains("is_palindrome"))
        assertEquals("Python & Coding Logic", palindrome.matchedSubject)

        val fib = engine.generateResponse("fibonacci series in python")
        assertTrue("Should contain fibonacci sequence logic", fib.paragraph1.contains("fibonacci_sequence"))
        assertEquals("Python & Coding Logic", fib.matchedSubject)

        val prime = engine.generateResponse("check prime number")
        assertTrue("Should contain is_prime function", prime.paragraph1.contains("is_prime"))
        assertEquals("Python & Coding Logic", prime.matchedSubject)
    }

    @Test
    fun testDirectQuestionIntent_trackOverviews() {
        val sqlOverview = engine.generateResponse("what is sql")
        assertTrue("Should explain SQL declarative nature", sqlOverview.paragraph1.contains("Structured Query Language") && sqlOverview.paragraph1.contains("declarative"))
        assertTrue("Should list SQL placement topics", sqlOverview.paragraph2.contains("Joins") && sqlOverview.paragraph2.contains("ACID"))
        assertEquals("SQL & Database Management", sqlOverview.matchedSubject)

        val pyOverview = engine.generateResponse("what is python")
        assertTrue("Should explain Python high-level interpreted nature", pyOverview.paragraph1.contains("high-level") && pyOverview.paragraph1.contains("interpreted"))
        assertTrue("Should list Python placement topics", pyOverview.paragraph2.contains("Memory Model") && pyOverview.paragraph2.contains("GIL"))
        assertEquals("Python & Coding Logic", pyOverview.matchedSubject)

        val aiOverview = engine.generateResponse("what is ai")
        assertTrue("Should explain AI cognition and hierarchy", aiOverview.paragraph1.contains("Artificial Intelligence") && aiOverview.paragraph2.contains("Machine Learning"))
        assertEquals("AI & Modern Tech Concepts", aiOverview.matchedSubject)
    }

    @Test
    fun testUserAskingOwnName_sayMyNameAndWhatIsMyName() {
        engine.preferredName = "Kishore"

        val sayName = engine.generateResponse("say my name")
        assertFalse("Must never dump OOP/dunder concepts", sayName.paragraph1.contains("OOP") || sayName.paragraph1.contains("Classes"))
        assertTrue("Must tell user their name", sayName.paragraph1.contains("Your name is **Kishore**!"))

        val whatIsName = engine.generateResponse("what is my name")
        assertFalse("Must never return AI identity for user's name", whatIsName.paragraph1.contains("I am SYNAPSE AI"))
        assertTrue("Must tell user their name", whatIsName.paragraph1.contains("Your name is **Kishore**!"))

        val whoAmI = engine.generateResponse("who am i")
        assertTrue("Must answer who user is", whoAmI.paragraph1.contains("Kishore"))

        val tanglish = engine.generateResponse("enoda name solu")
        assertTrue("Must answer in Tanglish name", tanglish.paragraph1.contains("Kishore"))
    }

    @Test
    fun testUserAskingOwnName_whenNoNameKnown() {
        engine.resetContext()

        val sayName = engine.generateResponse("say my name")
        assertTrue("Should tell user their name is Kishore", sayName.paragraph1.contains("Kishore"))
        assertFalse("Must not return OOP dunder chunk", sayName.paragraph1.contains("dunder", ignoreCase = true))

        val whatIsName = engine.generateResponse("what is my name")
        assertTrue("Should tell user their name is Kishore", whatIsName.paragraph1.contains("Kishore"))
        assertFalse("Must never return AI identity", whatIsName.paragraph1.contains("I am SYNAPSE AI"))

        val nowWhatIsName = engine.generateResponse("now what is my name")
        assertTrue("Should tell user their name is Kishore", nowWhatIsName.paragraph1.contains("Kishore"))
    }

    @Test
    fun testUserNameCapture_andFollowUpAskingName() {
        engine.resetContext()

        val captureResp = engine.generateResponse("my name is Kishore")
        assertTrue(captureResp.isNameCapture)
        assertEquals("Kishore", engine.preferredName)
        assertTrue(captureResp.paragraph1.contains("Got it, Kishore!"))

        val followUp = engine.generateResponse("what is my name")
        assertTrue(followUp.paragraph1.contains("Your name is **Kishore**!"))

        val nowFollowUp = engine.generateResponse("now what is my name")
        assertTrue(nowFollowUp.paragraph1.contains("Your name is **Kishore**!"))
    }

    @Test
    fun testTopicMemory_explainAboutPython_thenExplainShortly() {
        engine.resetContext()
        val r1 = engine.generateResponse("explain about python")
        assertEquals("Python & Coding Logic", r1.matchedSubject)

        val r2 = engine.generateResponse("explain shortly")
        assertEquals("Python & Coding Logic", r2.matchedSubject)
        assertTrue(r2.isFollowUp)
        assertFalse(r2.paragraph1.contains("Aptitude", ignoreCase = true))
    }

    @Test
    fun testOffTopicCasualQuery_returnsCleanOutOfScopeMessage() {
        engine.resetContext()
        val r = engine.generateResponse("ena pandra")
        assertEquals(LocalAIEngine.OUT_OF_SCOPE_RESPONSE, r.paragraph1)
    }

    @Test
    fun testPythonAndCodingLogicQuery_withTypo_returnsRichPythonLogic() {
        engine.resetContext()
        val queries = listOf(
            "give me pyhon and coding logic",
            "give me python and coding logic",
            "python and coding logic",
            "coding logic in python"
        )
        for (q in queries) {
            val response = engine.generateResponse(q)
            assertEquals("Python & Coding Logic", response.matchedSubject)
            assertFalse("Must never return Aptitude for query '$q'", response.paragraph1.contains("Aptitude", ignoreCase = true))
            assertFalse("Must never return Aptitude for query '$q'", response.paragraph2.contains("Aptitude", ignoreCase = true))
            assertTrue("Must contain python code for '$q'", response.paragraph1.contains("def ") || response.paragraph1.contains("python"))
            assertTrue("Must contain coding logic breakdown for '$q'", response.paragraph1.contains("Logic") || response.paragraph2.contains("Logic"))
        }
    }

    @Test
    fun testTwoSumProblemLogic_returnsOptimalHashMapBreakdown() {
        engine.resetContext()
        val response = engine.generateResponse("two sum")
        assertEquals("Python & Coding Logic", response.matchedSubject)
        assertTrue(response.paragraph1.contains("two_sum"))
        assertTrue(response.paragraph1.contains("seen = {}"))
        assertTrue(response.paragraph2.contains("O(N)"))
    }

    @Test
    fun testProfileNameEdit_extractsNameAndConfirmsCleanly() {
        engine.resetContext()
        val queries = listOf(
            "okay edit the profile name sri sai to kishore",
            "edit the profile name sri sai to kishore",
            "change profile name to kishore",
            "set my name to kishore",
            "edit profile name to kishore"
        )
        for (q in queries) {
            val response = engine.generateResponse(q)
            assertFalse("Must NEVER dump Python OOP dunder concepts for '$q'", response.paragraph1.contains("OOP") || response.paragraph1.contains("Dunder"))
            assertFalse("Must NEVER dump concept definition for '$q'", response.paragraph1.contains("Concept & Definition"))
            assertEquals("✅ Done! Your profile name has been updated to Kishore.", response.paragraph1)
            assertEquals("Kishore", engine.preferredName)
        }
    }

    @Test
    fun testUnknownQuery_neverDumpsLessonTemplate_returnsCleanFallback() {
        engine.resetContext()
        val randomQueries = listOf(
            "can you please do that random thing for me",
            "okay what about this other thing",
            "blabla something completely random",
            "profile settings check"
        )
        for (q in randomQueries) {
            val response = engine.generateResponse(q)
            assertFalse("Query '$q' must NEVER dump Concept & Definition template", response.paragraph1.contains("Concept & Definition"))
            assertFalse("Query '$q' must NEVER dump Python OOP", response.paragraph1.contains("OOP Classes") || response.paragraph1.contains("Dunder Methods"))
            assertEquals("Query '$q' must return clean fallback", LocalAIEngine.OUT_OF_SCOPE_RESPONSE, response.paragraph1)
        }
    }
}
