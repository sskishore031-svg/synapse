package com.siamts.synapse.ai

import android.content.Context
import android.util.Log
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.siamts.synapse.SynapseApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File

/**
 * On-Device Small Language Model (SLM) Inference Engine using MediaPipe Tasks GenAI.
 * Supports quantized models (.task / .bin, such as Gemma-2B or Qwen-1.5B) for private,
 * 100% offline university curriculum reasoning and generative problem solving.
 *
 * If the quantized binary is not yet pushed to the device storage, gracefully falls back
 * to the hybrid Semantic Intent + Expanded University RAG Engine ([LocalAIEngine]).
 */
class SlmInferenceEngine private constructor(private val context: Context?) {

    private var llmInference: LlmInference? = null
    private var activeModelPath: String? = null
    private var isInitialized = false

    companion object {
        private const val TAG = "SlmInferenceEngine"

        const val SYSTEM_PROMPT =
            "You are SYNAPSE AI, an intelligent college study companion built into the SYNAPSE AI Android app. " +
            "You help students with 50+ university subjects, advanced concepts, exam doubts, quizzes, and smart notes. " +
            "Be clear, encouraging, accurate, and concise."

        private val CANDIDATE_PATHS = listOf(
            "/data/local/tmp/llm/synapse_slm.task",
            "/data/local/tmp/llm/gemma-2b-it-cpu-int4.bin",
            "/data/local/tmp/synapse_slm.task",
            "synapse_slm.task",
            "gemma-2b-it-cpu-int4.bin",
            "synapse_slm.bin"
        )

        @Volatile
        private var INSTANCE: SlmInferenceEngine? = null

        fun getInstance(context: Context? = null): SlmInferenceEngine {
            return INSTANCE ?: synchronized(this) {
                val appCtx = context?.applicationContext ?: try {
                    SynapseApplication.getAppContext()
                } catch (_: Throwable) {
                    null
                }
                INSTANCE ?: SlmInferenceEngine(appCtx).also { INSTANCE = it }
            }
        }
    }

    /**
     * Checks if a valid quantized SLM model binary exists on the device storage.
     */
    fun isModelAvailable(): Boolean {
        return findExistingModelFile() != null
    }

    /**
     * Finds the first existing candidate model file path on device storage.
     */
    fun findExistingModelFile(): File? {
        val candidateFiles = mutableListOf<File>()

        // 1. Direct path candidates
        candidateFiles.add(File("/data/local/tmp/llm/synapse_slm.task"))
        candidateFiles.add(File("/data/local/tmp/llm/gemma-2b-it-cpu-int4.bin"))
        candidateFiles.add(File("/data/local/tmp/synapse_slm.task"))

        // 2. App-specific external storage candidates
        val ctx = context ?: try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }
        if (ctx != null) {
            try {
                val extDir = ctx.getExternalFilesDir(null)
                if (extDir != null) {
                    candidateFiles.add(File(extDir, "synapse_slm.task"))
                    candidateFiles.add(File(extDir, "gemma-2b-it-cpu-int4.bin"))
                    candidateFiles.add(File(extDir, "synapse_slm.bin"))
                    candidateFiles.add(File(extDir, "llm/synapse_slm.task"))
                }
                candidateFiles.add(File(ctx.filesDir, "synapse_slm.task"))
                candidateFiles.add(File(ctx.filesDir, "gemma-2b-it-cpu-int4.bin"))
            } catch (_: Throwable) {}
        }

        return candidateFiles.firstOrNull { it.exists() && it.isFile && it.length() > 1024L }
    }

    /**
     * Returns the detected model path, or null if no valid model is found.
     */
    fun getActiveModelPath(): String? {
        return activeModelPath ?: findExistingModelFile()?.absolutePath
    }

    /**
     * Initializes the MediaPipe LlmInference session on a background thread.
     */
    @Synchronized
    fun initialize(): Boolean {
        if (isInitialized && llmInference != null) return true

        val modelFile = findExistingModelFile()
        if (modelFile == null) {
            logD("No SLM model binary detected on device. Running hybrid Semantic RAG Engine.")
            return false
        }

        val ctx = context ?: try { SynapseApplication.getAppContext() } catch (_: Throwable) { null }
        if (ctx == null) {
            logW("Application context unavailable for SLM initialization.")
            return false
        }

        return try {
            logD("Initializing MediaPipe SLM with model: ${modelFile.absolutePath} (${modelFile.length()} bytes)")
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelFile.absolutePath)
                .setMaxTokens(512)
                .setTopK(40)
                .setTemperature(0.7f)
                .build()

            llmInference = LlmInference.createFromOptions(ctx, options)
            activeModelPath = modelFile.absolutePath
            isInitialized = true
            logD("MediaPipe SLM Inference Engine initialized successfully!")
            true
        } catch (e: Throwable) {
            logE("Failed to initialize MediaPipe SLM: ${e.message}", e)
            llmInference = null
            isInitialized = false
            false
        }
    }

    /**
     * Formats prompt with system persona and retrieved curriculum knowledge context.
     */
    fun buildAugmentedPrompt(userQuery: String, retrievedContext: String? = null): String {
        return buildString {
            append("System: ").append(SYSTEM_PROMPT).append("\n\n")
            if (!retrievedContext.isNullOrBlank()) {
                append("Curriculum Context:\n").append(retrievedContext.trim()).append("\n\n")
            }
            append("Student: ").append(userQuery.trim()).append("\n\n")
            append("SYNAPSE AI:")
        }
    }

    /**
     * Generates a complete response synchronously using the on-device SLM.
     * Returns null if SLM is not initialized or inference encounters an error.
     */
    suspend fun generateResponse(userQuery: String, retrievedContext: String? = null): String? = withContext(Dispatchers.Default) {
        if (!isInitialized || llmInference == null) {
            val success = initialize()
            if (!success || llmInference == null) return@withContext null
        }

        try {
            val formattedPrompt = buildAugmentedPrompt(userQuery, retrievedContext)
            val result = llmInference?.generateResponse(formattedPrompt)
            result?.trim()
        } catch (e: Throwable) {
            logE("SLM generation error: ${e.message}", e)
            null
        }
    }

    /**
     * Streams generated tokens word-by-word as a Kotlin Flow for responsive UI typing effect.
     */
    fun generateResponseStream(
        userQuery: String,
        retrievedContext: String? = null,
        delayPerWordMs: Long = 18L
    ): Flow<String> = flow {
        val fullText = generateResponse(userQuery, retrievedContext)
        if (fullText.isNullOrBlank()) {
            return@flow
        }

        val words = fullText.split(Regex("\\s+")).filter { it.isNotEmpty() }
        val current = StringBuilder()
        for (i in words.indices) {
            if (i > 0) current.append(" ")
            current.append(words[i])
            emit(current.toString())
            delay(delayPerWordMs)
        }
    }.flowOn(Dispatchers.Default)

    /**
     * Releases SLM resources.
     */
    @Synchronized
    fun close() {
        try {
            llmInference?.close()
        } catch (e: Throwable) {
            logW("Error closing LlmInference: ${e.message}")
        } finally {
            llmInference = null
            isInitialized = false
        }
    }

    private fun logD(msg: String) {
        try { Log.d(TAG, msg) } catch (_: Throwable) { println("[$TAG] $msg") }
    }

    private fun logW(msg: String) {
        try { Log.w(TAG, msg) } catch (_: Throwable) { System.err.println("[$TAG] $msg") }
    }

    private fun logE(msg: String, tr: Throwable? = null) {
        try { Log.e(TAG, msg, tr) } catch (_: Throwable) { System.err.println("[$TAG] $msg ${tr?.message}") }
    }
}

