package com.siamts.synapse.data.remote

import android.os.Build
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit Client configured for Synapse AI backend communication.
 * Connects via Ngrok tunnel or LAN IP, bypassing ngrok browser warning headers
 * and enabling complete HTTP body logging.
 */
object RetrofitClient {

    // Public Ngrok Base URL for mobile & emulator tunneling (captures all live traffic in ngrok)
    const val BASE_URL = "https://bakery-shallot-repeater.ngrok-free.dev/Synapse_backend/"
    const val NGROK_BASE_URL = BASE_URL

    // Host PC LAN IP accessible by physical mobile devices on the same local network
    const val PHYSICAL_DEVICE_BASE_URL = "http://172.23.176.100/Synapse_backend/"

    // Android Emulator loopback alias
    const val EMULATOR_LOCAL_BASE_URL = "http://172.23.176.100/Synapse_backend/"

    val isEmulator: Boolean
        get() = try {
            (Build.FINGERPRINT.startsWith("generic")
                    || Build.FINGERPRINT.startsWith("unknown")
                    || Build.MODEL.contains("google_sdk")
                    || Build.MODEL.contains("Emulator")
                    || Build.MODEL.contains("Android SDK built for x86")
                    || Build.MANUFACTURER.contains("Genymotion")
                    || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                    || "google_sdk" == Build.PRODUCT)
        } catch (_: Exception) {
            false
        }

    // Default to Ngrok tunnel so all mobile device and emulator traffic is captured
    val defaultBaseUrl: String
        get() = NGROK_BASE_URL

    @Volatile
    private var currentBaseUrl: String = defaultBaseUrl

    @Volatile
    private var apiInstance: AuthApi? = null

    @Volatile
    private var brainMapApiInstance: BrainMapApi? = null

    @Volatile
    private var chemistryApiInstance: ChemistryApi? = null

    @Volatile
    private var profileApiInstance: ProfileApi? = null

    @Volatile
    private var homeApiInstance: HomeApi? = null

    @Volatile
    private var chaptersApiInstance: ChaptersApi? = null

    @Volatile
    private var quizApiInstance: QuizApi? = null

    @Volatile
    private var notesApiInstance: NotesApi? = null

    @Volatile
    private var lessonApiInstance: LessonApi? = null

    @Volatile
    private var flashcardsApiInstance: FlashcardsApi? = null

    @Volatile
    private var aiTutorApiInstance: AiTutorApi? = null

    @Volatile
    private var achievementsApiInstance: AchievementsApi? = null

    @Volatile
    private var notificationsApiInstance: NotificationsApi? = null

    @Volatile
    private var subjectsApiInstance: SubjectsApi? = null

    @Volatile
    private var learningPlanApiInstance: LearningPlanApi? = null

    @Volatile
    private var analyticsApiInstance: AnalyticsApi? = null

    @Volatile
    private var progressApiInstance: ProgressApi? = null

    private val headerInterceptor by lazy {
        Interceptor { chain ->
            val originalRequest = chain.request()
            val originalUrl = originalRequest.url.toString()

            // Normalize URL to always target /Synapse_backend/auth/<endpoint>.php without duplicates,
            // while preserving static uploads/avatars/ file URLs cleanly
            val isMedia = originalUrl.contains("/uploads/") || originalUrl.contains("/upload/")
            val cleanedUrl = when {
                isMedia -> originalUrl
                originalUrl.contains("/auth/auth/") -> originalUrl.replace("/auth/auth/", "/auth/")
                !originalUrl.contains("/auth/") && originalUrl.contains("/Synapse_backend/") ->
                    originalUrl.replace("/Synapse_backend/", "/Synapse_backend/auth/")
                else -> originalUrl
            }

            val token = runCatching {
                com.siamts.synapse.data.local.SessionManager.getInstanceOrNull()?.getAuthTokenCached()
            }.getOrNull()

            val requestBuilder = originalRequest.newBuilder()
                .url(cleanedUrl)
                .header("ngrok-skip-browser-warning", "true")
                .header("User-Agent", "Synapse-AI-Android/1.0")

            if (!isMedia) {
                requestBuilder
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
            }

            if (!token.isNullOrBlank()) {
                requestBuilder.header("Authorization", "Bearer $token")
            }

            val request = requestBuilder.build()
            try {
                android.util.Log.d("RetrofitClient", "--> ${request.method} ${request.url}")
            } catch (_: Throwable) {}

            val response = chain.proceed(request)
            try {
                android.util.Log.d("RetrofitClient", "<-- ${response.code} ${request.url}")
            } catch (_: Throwable) {}
            response
        }
    }

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    val authApi: AuthApi
        get() = getOrCreateApi()

    val authApiService: AuthApi
        get() = getOrCreateApi()

    val brainMapApi: BrainMapApi
        get() = getOrCreateBrainMapApi()

    val chemistryApi: ChemistryApi
        get() = getOrCreateChemistryApi()

    val profileApi: ProfileApi
        get() = getOrCreateProfileApi()

    val homeApi: HomeApi
        get() = getOrCreateHomeApi()

    val chaptersApi: ChaptersApi
        get() = getOrCreateChaptersApi()

    val quizApi: QuizApi
        get() = getOrCreateQuizApi()

    val notesApi: NotesApi
        get() = getOrCreateNotesApi()

    val lessonApi: LessonApi
        get() = getOrCreateLessonApi()

    val flashcardsApi: FlashcardsApi
        get() = getOrCreateFlashcardsApi()

    val aiTutorApi: AiTutorApi
        get() = getOrCreateAiTutorApi()

    val achievementsApi: AchievementsApi
        get() = getOrCreateAchievementsApi()

    val notificationsApi: NotificationsApi
        get() = getOrCreateNotificationsApi()

    val subjectsApi: SubjectsApi
        get() = getOrCreateSubjectsApi()

    val learningPlanApi: LearningPlanApi
        get() = getOrCreateLearningPlanApi()

    val analyticsApi: AnalyticsApi
        get() = getOrCreateAnalyticsApi()

    val progressApi: ProgressApi
        get() = getOrCreateProgressApi()

    private fun getOrCreateApi(): AuthApi {
        return apiInstance ?: synchronized(this) {
            apiInstance ?: buildRetrofit(currentBaseUrl).create(AuthApi::class.java).also {
                apiInstance = it
            }
        }
    }

    private fun getOrCreateBrainMapApi(): BrainMapApi {
        return brainMapApiInstance ?: synchronized(this) {
            brainMapApiInstance ?: buildRetrofit(currentBaseUrl).create(BrainMapApi::class.java).also {
                brainMapApiInstance = it
            }
        }
    }

    private fun getOrCreateChemistryApi(): ChemistryApi {
        return chemistryApiInstance ?: synchronized(this) {
            chemistryApiInstance ?: buildRetrofit(currentBaseUrl).create(ChemistryApi::class.java).also {
                chemistryApiInstance = it
            }
        }
    }

    private fun getOrCreateProfileApi(): ProfileApi {
        return profileApiInstance ?: synchronized(this) {
            profileApiInstance ?: buildRetrofit(currentBaseUrl).create(ProfileApi::class.java).also {
                profileApiInstance = it
            }
        }
    }

    private fun getOrCreateHomeApi(): HomeApi {
        return homeApiInstance ?: synchronized(this) {
            homeApiInstance ?: buildRetrofit(currentBaseUrl).create(HomeApi::class.java).also {
                homeApiInstance = it
            }
        }
    }

    private fun getOrCreateChaptersApi(): ChaptersApi {
        return chaptersApiInstance ?: synchronized(this) {
            chaptersApiInstance ?: buildRetrofit(currentBaseUrl).create(ChaptersApi::class.java).also {
                chaptersApiInstance = it
            }
        }
    }

    private fun getOrCreateQuizApi(): QuizApi {
        return quizApiInstance ?: synchronized(this) {
            quizApiInstance ?: buildRetrofit(currentBaseUrl).create(QuizApi::class.java).also {
                quizApiInstance = it
            }
        }
    }

    private fun getOrCreateNotesApi(): NotesApi {
        return notesApiInstance ?: synchronized(this) {
            notesApiInstance ?: buildRetrofit(currentBaseUrl).create(NotesApi::class.java).also {
                notesApiInstance = it
            }
        }
    }

    private fun getOrCreateLessonApi(): LessonApi {
        return lessonApiInstance ?: synchronized(this) {
            lessonApiInstance ?: buildRetrofit(currentBaseUrl).create(LessonApi::class.java).also {
                lessonApiInstance = it
            }
        }
    }

    private fun getOrCreateFlashcardsApi(): FlashcardsApi {
        return flashcardsApiInstance ?: synchronized(this) {
            flashcardsApiInstance ?: buildRetrofit(currentBaseUrl).create(FlashcardsApi::class.java).also {
                flashcardsApiInstance = it
            }
        }
    }

    private fun getOrCreateAiTutorApi(): AiTutorApi {
        return aiTutorApiInstance ?: synchronized(this) {
            aiTutorApiInstance ?: buildRetrofit(currentBaseUrl).create(AiTutorApi::class.java).also {
                aiTutorApiInstance = it
            }
        }
    }

    private fun getOrCreateAchievementsApi(): AchievementsApi {
        return achievementsApiInstance ?: synchronized(this) {
            achievementsApiInstance ?: buildRetrofit(currentBaseUrl).create(AchievementsApi::class.java).also {
                achievementsApiInstance = it
            }
        }
    }

    private fun getOrCreateNotificationsApi(): NotificationsApi {
        return notificationsApiInstance ?: synchronized(this) {
            notificationsApiInstance ?: buildRetrofit(currentBaseUrl).create(NotificationsApi::class.java).also {
                notificationsApiInstance = it
            }
        }
    }

    private fun getOrCreateSubjectsApi(): SubjectsApi {
        return subjectsApiInstance ?: synchronized(this) {
            subjectsApiInstance ?: buildRetrofit(currentBaseUrl).create(SubjectsApi::class.java).also {
                subjectsApiInstance = it
            }
        }
    }

    private fun getOrCreateLearningPlanApi(): LearningPlanApi {
        return learningPlanApiInstance ?: synchronized(this) {
            learningPlanApiInstance ?: buildRetrofit(currentBaseUrl).create(LearningPlanApi::class.java).also {
                learningPlanApiInstance = it
            }
        }
    }

    private fun getOrCreateAnalyticsApi(): AnalyticsApi {
        return analyticsApiInstance ?: synchronized(this) {
            analyticsApiInstance ?: buildRetrofit(currentBaseUrl).create(AnalyticsApi::class.java).also {
                analyticsApiInstance = it
            }
        }
    }

    private fun getOrCreateProgressApi(): ProgressApi {
        return progressApiInstance ?: synchronized(this) {
            progressApiInstance ?: buildRetrofit(currentBaseUrl).create(ProgressApi::class.java).also {
                progressApiInstance = it
            }
        }
    }

    private fun buildRetrofit(url: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Updates the base URL dynamically (e.g. for testing on physical devices with PC LAN IP).
     */
    fun updateBaseUrl(newUrl: String) {
        val formatted = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        if (formatted != currentBaseUrl) {
            synchronized(this) {
                currentBaseUrl = formatted
                apiInstance = buildRetrofit(formatted).create(AuthApi::class.java)
                brainMapApiInstance = buildRetrofit(formatted).create(BrainMapApi::class.java)
                chemistryApiInstance = buildRetrofit(formatted).create(ChemistryApi::class.java)
                profileApiInstance = buildRetrofit(formatted).create(ProfileApi::class.java)
                homeApiInstance = buildRetrofit(formatted).create(HomeApi::class.java)
                chaptersApiInstance = buildRetrofit(formatted).create(ChaptersApi::class.java)
                quizApiInstance = buildRetrofit(formatted).create(QuizApi::class.java)
                notesApiInstance = buildRetrofit(formatted).create(NotesApi::class.java)
                lessonApiInstance = buildRetrofit(formatted).create(LessonApi::class.java)
                flashcardsApiInstance = buildRetrofit(formatted).create(FlashcardsApi::class.java)
                aiTutorApiInstance = buildRetrofit(formatted).create(AiTutorApi::class.java)
                achievementsApiInstance = buildRetrofit(formatted).create(AchievementsApi::class.java)
                notificationsApiInstance = buildRetrofit(formatted).create(NotificationsApi::class.java)
                subjectsApiInstance = buildRetrofit(formatted).create(SubjectsApi::class.java)
                learningPlanApiInstance = buildRetrofit(formatted).create(LearningPlanApi::class.java)
                analyticsApiInstance = buildRetrofit(formatted).create(AnalyticsApi::class.java)
            }
        }
    }

    fun setBaseUrl(newUrl: String) = updateBaseUrl(newUrl)

    fun getBaseUrl(): String = currentBaseUrl

    /**
     * Resolves and formats profile photo URLs so they load reliably across Emulator (10.0.2.2),
     * physical mobile devices (LAN IP), local storage URIs, and Base64 data URIs.
     */
    fun formatPhotoUrl(rawUrl: String?): String? {
        if (rawUrl.isNullOrBlank()) return null
        val trimmed = rawUrl.trim()
        if (trimmed.startsWith("data:image", ignoreCase = true) ||
            trimmed.startsWith("base64,", ignoreCase = true) ||
            trimmed.startsWith("content://") ||
            trimmed.startsWith("file://") ||
            trimmed.startsWith("/") ||
            (trimmed.length > 300 && !trimmed.contains("/") && !trimmed.contains(" ") && !trimmed.contains("."))
        ) {
            return trimmed
        }
        val baseUrl = getBaseUrl()
        val rootBackendUrl = baseUrl
            .replace(Regex("/auth/?$"), "")
            .trimEnd('/')

        return if (trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true)) {
            try {
                val currentUri = java.net.URI(baseUrl)
                val currentHost = currentUri.host
                val parsedUri = java.net.URI(trimmed)
                if (parsedUri.host.equals("localhost", ignoreCase = true) ||
                    parsedUri.host.equals("127.0.0.1") ||
                    parsedUri.host != currentHost
                ) {
                    val path = parsedUri.rawPath.orEmpty()
                    val uploadPath = if (path.contains("uploads/")) {
                        path.substring(path.indexOf("uploads/"))
                    } else if (path.contains("upload/")) {
                        path.substring(path.indexOf("upload/"))
                    } else {
                        path.trimStart('/')
                    }
                    "$rootBackendUrl/$uploadPath"
                } else {
                    trimmed
                }
            } catch (_: Exception) {
                trimmed
            }
        } else {
            val cleanPath = trimmed.trimStart('/')
            val uploadPath = if (cleanPath.contains("uploads/")) {
                cleanPath.substring(cleanPath.indexOf("uploads/"))
            } else if (cleanPath.contains("upload/")) {
                cleanPath.substring(cleanPath.indexOf("upload/"))
            } else {
                cleanPath
            }
            "$rootBackendUrl/$uploadPath"
        }
    }

    fun resolveImageModel(rawUrl: String?): Any? {
        if (rawUrl.isNullOrBlank()) return null
        val trimmed = rawUrl.trim()
        if (trimmed.startsWith("data:image", ignoreCase = true)) {
            val base64Data = if (trimmed.contains(",")) trimmed.substringAfter(",") else trimmed
            return try {
                android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
            } catch (_: Exception) {
                trimmed
            }
        }
        if (trimmed.startsWith("base64,", ignoreCase = true)) {
            val base64Data = trimmed.substringAfter(",")
            return try {
                android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
            } catch (_: Exception) {
                trimmed
            }
        }
        if (trimmed.length > 300 && !trimmed.contains("/") && !trimmed.contains(" ") && !trimmed.contains(".")) {
            return try {
                android.util.Base64.decode(trimmed, android.util.Base64.DEFAULT)
            } catch (_: Exception) {
                trimmed
            }
        }
        val formatted = formatPhotoUrl(trimmed) ?: return null
        return if (formatted.startsWith("/") && !formatted.startsWith("//")) {
            java.io.File(formatted)
        } else if (formatted.startsWith("file://")) {
            java.io.File(formatted.removePrefix("file://"))
        } else {
            formatted
        }
    }
}
