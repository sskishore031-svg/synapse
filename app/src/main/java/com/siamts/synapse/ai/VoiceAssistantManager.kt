package com.siamts.synapse.ai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

/**
 * State representing the active live conversational voice session.
 */
enum class VoiceState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING,
    PAUSED,
    ERROR
}

/**
 * Ultra-fast, two-way conversational voice assistant manager combining on-device SpeechRecognizer (STT),
 * on-device LocalAIEngine reasoning, and Android TextToSpeech (TTS) for natural, continuous, hands-free dialogue.
 */
class VoiceAssistantManager(
    private val context: Context,
    private val localAiEngine: LocalAIEngine = LocalAIEngine.getInstance(),
    private val onConversationTurn: ((userQuery: String, aiResponseText: String) -> Unit)? = null
) {
    private val tag = "VoiceAssistantManager"
    private val mainHandler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _voiceState = MutableStateFlow(VoiceState.IDLE)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _audioLevel = MutableStateFlow(0f)
    val audioLevel: StateFlow<Float> = _audioLevel.asStateFlow()

    private val _userSpokenText = MutableStateFlow("")
    val userSpokenText: StateFlow<String> = _userSpokenText.asStateFlow()

    private val _aiSpokenText = MutableStateFlow("")
    val aiSpokenText: StateFlow<String> = _aiSpokenText.asStateFlow()

    private val _isLiveActive = MutableStateFlow(false)
    val isLiveActive: StateFlow<Boolean> = _isLiveActive.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null
    private var dictationRecognizer: SpeechRecognizer? = null
    private var lastDictatedText = ""
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false
    private var activeUtteranceId: String? = null
    private var thinkingJob: Job? = null

    init {
        initTts()
    }

    private fun initTts() {
        textToSpeech = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.let { tts ->
                    val result = tts.setLanguage(Locale.US)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts.setLanguage(Locale.getDefault())
                    }

                    // Select natural Male voice
                    selectNaturalMaleVoice(tts)

                    // Resonant, natural male pitch and conversational cadence
                    tts.setPitch(0.88f)
                    tts.setSpeechRate(0.98f)

                    tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            _voiceState.value = VoiceState.SPEAKING
                        }

                        override fun onDone(utteranceId: String?) {
                            if (utteranceId == activeUtteranceId) {
                                activeUtteranceId = null
                                onTtsFinished()
                            }
                        }

                        override fun onError(utteranceId: String?) {
                            if (utteranceId == activeUtteranceId) {
                                activeUtteranceId = null
                                onTtsFinished()
                            }
                        }
                    })
                    isTtsReady = true
                }
            } else {
                Log.e(tag, "TTS initialization failed: status=$status")
            }
        }
    }

    /**
     * Inspects available TTS voices to locate a natural English Male voice.
     */
    private fun selectNaturalMaleVoice(tts: TextToSpeech) {
        try {
            val voices = tts.voices
            if (!voices.isNullOrEmpty()) {
                val maleVoice = voices.find { voice ->
                    val name = voice.name.lowercase(Locale.ROOT)
                    val lang = voice.locale?.language?.lowercase(Locale.ROOT).orEmpty()
                    lang.startsWith("en") && (
                        name.contains("male") ||
                        name.contains("en-us-x-iom") || // Google US English Male 1
                        name.contains("en-us-x-tpd") || // Google US English Male 2
                        name.contains("en-in-x-ene") || // Indian English Male
                        name.contains("en-gb-x-rjs") || // UK English Male
                        (voice.features?.any { it.contains("male", ignoreCase = true) } == true)
                    ) && !name.contains("female")
                } ?: voices.find { voice ->
                    val name = voice.name.lowercase(Locale.ROOT)
                    (name.contains("male") || name.contains("man")) && !name.contains("female")
                } ?: voices.find { voice ->
                    val name = voice.name.lowercase(Locale.ROOT)
                    voice.locale?.language?.startsWith("en") == true && !name.contains("female")
                }

                if (maleVoice != null) {
                    tts.voice = maleVoice
                    Log.d(tag, "Selected Natural Male Voice for TTS: ${maleVoice.name}")
                }
            }
        } catch (e: Throwable) {
            Log.w(tag, "Could not set custom male voice: ${e.message}")
        }
    }

    /**
     * Starts the full-screen two-way Live Voice conversation session.
     */
    fun startLiveSession() {
        _isLiveActive.value = true
        _userSpokenText.value = ""
        _aiSpokenText.value = "Tap or speak to ask anything..."
        startListening()
    }

    /**
     * Stops and exits the Live Voice session.
     */
    fun stopLiveSession() {
        _isLiveActive.value = false
        stopListeningInternal()
        stopSpeaking()
        thinkingJob?.cancel()
        _voiceState.value = VoiceState.IDLE
        _audioLevel.value = 0f
    }

    /**
     * Toggles pause / resume within the active voice session.
     */
    fun togglePauseResume() {
        if (!_isLiveActive.value) return

        if (_voiceState.value == VoiceState.PAUSED) {
            _voiceState.value = VoiceState.LISTENING
            startListening()
        } else {
            stopListeningInternal()
            stopSpeaking()
            thinkingJob?.cancel()
            _voiceState.value = VoiceState.PAUSED
            _audioLevel.value = 0f
        }
    }

    private fun startListening() {
        if (!_isLiveActive.value || _voiceState.value == VoiceState.PAUSED) return

        mainHandler.post {
            try {
                if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                    _voiceState.value = VoiceState.ERROR
                    _userSpokenText.value = "Speech recognition unavailable on device"
                    return@post
                }

                // Clean up previous recognizer instance safely
                try {
                    speechRecognizer?.cancel()
                    speechRecognizer?.destroy()
                } catch (_: Exception) {}

                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context.applicationContext).apply {
                    setRecognitionListener(createRecognitionListener())
                }

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toLanguageTag())
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                    putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)

                    // Slow-speech tolerance extras: wait patiently for slow speech and pauses without cutting off
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000L)
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3500L)
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000L)
                }

                _voiceState.value = VoiceState.LISTENING
                speechRecognizer?.startListening(intent)
            } catch (e: Exception) {
                Log.e(tag, "Error starting speech recognition", e)
                _voiceState.value = VoiceState.ERROR
            }
        }
    }

    private fun stopListeningInternal() {
        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
                speechRecognizer?.cancel()
            } catch (e: Exception) {
                Log.e(tag, "Error stopping recognizer", e)
            }
        }
    }

    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _voiceState.value = VoiceState.LISTENING
            }

            override fun onBeginningOfSpeech() {
                _voiceState.value = VoiceState.LISTENING
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Smooth normalized float [0.0..1.0] for the animated orb
                val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                _audioLevel.value = normalized
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                _audioLevel.value = 0f
                _voiceState.value = VoiceState.THINKING
            }

            override fun onError(error: Int) {
                _audioLevel.value = 0f
                if (!_isLiveActive.value || _voiceState.value == VoiceState.PAUSED) return

                val shouldRestart = error == SpeechRecognizer.ERROR_NO_MATCH ||
                        error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT ||
                        error == SpeechRecognizer.ERROR_NETWORK_TIMEOUT

                if (shouldRestart) {
                    // Brief delay before restarting speech listener to prevent busy looping
                    scope.launch {
                        delay(400L)
                        if (_isLiveActive.value && _voiceState.value != VoiceState.PAUSED && _voiceState.value != VoiceState.SPEAKING) {
                            startListening()
                        }
                    }
                } else {
                    Log.w(tag, "SpeechRecognizer error: $error")
                }
            }

            override fun onResults(results: Bundle?) {
                _audioLevel.value = 0f
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val finalSpeech = matches?.firstOrNull()?.trim().orEmpty()

                if (finalSpeech.isNotBlank()) {
                    _userSpokenText.value = finalSpeech
                    processVoiceQuery(finalSpeech)
                } else {
                    if (_isLiveActive.value && _voiceState.value != VoiceState.PAUSED) {
                        startListening()
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                if (!partial.isNullOrBlank()) {
                    _userSpokenText.value = partial
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    /**
     * Executes fast inference on On-Device SLM / LocalAIEngine and speaks the result aloud via TextToSpeech.
     */
    private fun processVoiceQuery(query: String) {
        _voiceState.value = VoiceState.THINKING
        _aiSpokenText.value = "Thinking..."

        thinkingJob?.cancel()
        thinkingJob = scope.launch(Dispatchers.Default) {
            val slmEngine = try { SlmInferenceEngine.getInstance(context) } catch (_: Throwable) { null }
            val fullAnswer = if (slmEngine != null && slmEngine.isModelAvailable()) {
                val ragContext = localAiEngine.retrieveContext(query)
                slmEngine.generateResponse(query, ragContext) ?: run {
                    val resp = localAiEngine.generateResponse(query)
                    if (resp.paragraph2.isNotBlank()) "${resp.paragraph1}\n\n${resp.paragraph2}" else resp.paragraph1
                }
            } else {
                val response = localAiEngine.generateResponse(query)
                if (response.paragraph2.isNotBlank()) {
                    "${response.paragraph1}\n\n${response.paragraph2}"
                } else {
                    response.paragraph1
                }
            }

            val spokenAnswer = cleanTextForSpeech(fullAnswer)

            launch(Dispatchers.Main) {
                _aiSpokenText.value = fullAnswer
                onConversationTurn?.invoke(query, fullAnswer)
                speakInternal(spokenAnswer)
            }
        }
    }

    private fun speakInternal(cleanText: String) {
        if (!isTtsReady || textToSpeech == null) {
            Log.w(tag, "TTS is not ready yet, falling back to listening")
            onTtsFinished()
            return
        }

        activeUtteranceId = UUID.randomUUID().toString()
        _voiceState.value = VoiceState.SPEAKING

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, activeUtteranceId)
        }

        textToSpeech?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, activeUtteranceId)
    }

    /**
     * Speaks text directly (used e.g. when tapping "Read Aloud" on a message card).
     */
    fun speakDirect(text: String, onFinished: (() -> Unit)? = null) {
        val clean = cleanTextForSpeech(text)
        if (!isTtsReady || textToSpeech == null) return

        val utteranceId = UUID.randomUUID().toString()
        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(id: String?) {}
            override fun onDone(id: String?) {
                if (id == utteranceId) onFinished?.invoke()
            }
            override fun onError(id: String?) {
                if (id == utteranceId) onFinished?.invoke()
            }
        })

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }
        textToSpeech?.speak(clean, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    /**
     * Stops any currently ongoing speech playback.
     */
    fun stopSpeaking() {
        try {
            textToSpeech?.stop()
        } catch (_: Exception) {}
        activeUtteranceId = null
    }

    fun isSpeaking(): Boolean {
        return textToSpeech?.isSpeaking ?: false
    }

    private fun onTtsFinished() {
        scope.launch(Dispatchers.Main) {
            delay(350L) // Subtle natural conversational pause before opening microphone
            if (_isLiveActive.value && _voiceState.value != VoiceState.PAUSED) {
                _voiceState.value = VoiceState.LISTENING
                startListening()
            }
        }
    }

    /**
     * Strips Markdown formatting, symbols, and emojis for natural, fluent spoken English.
     */
    fun cleanTextForSpeech(text: String): String = Companion.cleanTextForSpeech(text)

    companion object {
        /**
         * Strips Markdown formatting, symbols, and emojis for natural, fluent spoken English.
         */
        fun cleanTextForSpeech(text: String): String {
            return text
                .replace(Regex("\\*\\*(.*?)\\*\\*"), "$1") // Bold **text** -> text
                .replace(Regex("\\*(.*?)\\*"), "$1") // Italic *text* -> text
                .replace(Regex("#{1,6}\\s*"), "") // Markdown headers
                .replace(Regex("`{1,3}[^`]*`{1,3}"), "") // Inline/block code
                .replace(Regex("^[•\\-\\+]\\s*", RegexOption.MULTILINE), "") // Bullet points
                .replace(Regex("[\\[\\]\\(\\)]"), " ") // Brackets
                .replace(Regex("[⚡📘🧩💡✨🔍🟢🔴🎙️🤖🧠·↳🗑️]"), "") // UI Emojis
                .replace(Regex("\\s+"), " ")
                .trim()
        }
    }

    /**
     * Starts single-turn speech dictation (for WhatsApp-style hold-to-talk or tap-to-dictate).
     */
    fun startDictation(
        onPartial: (String) -> Unit,
        onComplete: (String) -> Unit
    ) {
        stopSpeaking()
        stopLiveSession()
        mainHandler.post {
            try {
                if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                    onComplete("")
                    return@post
                }
                try {
                    dictationRecognizer?.cancel()
                    dictationRecognizer?.destroy()
                } catch (_: Exception) {}
                lastDictatedText = ""

                dictationRecognizer = SpeechRecognizer.createSpeechRecognizer(context.applicationContext).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {}
                        override fun onBeginningOfSpeech() {}
                        override fun onRmsChanged(rmsdB: Float) {
                            val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                            _audioLevel.value = normalized
                        }
                        override fun onBufferReceived(buffer: ByteArray?) {}
                        override fun onEndOfSpeech() {
                            _audioLevel.value = 0f
                        }
                        override fun onError(error: Int) {
                            _audioLevel.value = 0f
                            onComplete(lastDictatedText)
                        }
                        override fun onResults(results: Bundle?) {
                            _audioLevel.value = 0f
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            val text = matches?.firstOrNull()?.trim() ?: lastDictatedText
                            lastDictatedText = text
                            onComplete(text)
                        }
                        override fun onPartialResults(partialResults: Bundle?) {
                            val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
                            if (partial.isNotBlank()) {
                                lastDictatedText = partial
                                onPartial(partial)
                            }
                        }
                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                }

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toLanguageTag())
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                    putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 3000L)
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
                }
                dictationRecognizer?.startListening(intent)
            } catch (e: Exception) {
                Log.e(tag, "Dictation start error", e)
                onComplete("")
            }
        }
    }

    /**
     * Stops dictation and returns the accumulated speech text.
     */
    fun stopDictationAndGet(): String {
        mainHandler.post {
            try {
                dictationRecognizer?.stopListening()
            } catch (_: Exception) {}
        }
        return lastDictatedText
    }

    /**
     * Cancels dictation immediately without triggering results.
     */
    fun cancelDictation() {
        mainHandler.post {
            try {
                dictationRecognizer?.cancel()
                dictationRecognizer?.destroy()
                dictationRecognizer = null
            } catch (_: Exception) {}
            lastDictatedText = ""
            _audioLevel.value = 0f
        }
    }

    /**
     * Fully releases audio resources to prevent leaks.
     */
    fun destroy() {
        stopLiveSession()
        cancelDictation()
        mainHandler.post {
            try {
                speechRecognizer?.destroy()
                speechRecognizer = null
            } catch (_: Exception) {}

            try {
                textToSpeech?.stop()
                textToSpeech?.shutdown()
                textToSpeech = null
            } catch (_: Exception) {}
        }
    }
}

