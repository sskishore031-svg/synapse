package com.siamts.synapse.data.remote

import android.os.Build
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit Client configured for Synapse AI backend communication.
 * Automatically routes traffic to:
 * - Android Emulator: http://10.0.2.2/synapse_backend/
 * - Physical Mobile Phone: http://172.23.176.100/synapse_backend/
 */
object RetrofitClient {

    // Android Emulator loopback alias
    private const val EMULATOR_BASE_URL = "http://10.0.2.2/synapse_backend/"

    // Host PC LAN IP accessible by physical mobile devices on the same local network
    const val PHYSICAL_DEVICE_BASE_URL = "http://172.23.176.100/synapse_backend/"

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

    val defaultBaseUrl: String
        get() = if (isEmulator) EMULATOR_BASE_URL else PHYSICAL_DEVICE_BASE_URL

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

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
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
            }
        }
    }

    fun setBaseUrl(newUrl: String) = updateBaseUrl(newUrl)

    fun getBaseUrl(): String = currentBaseUrl

    /**
     * Resolves and formats profile photo URLs so they load reliably across Emulator (10.0.2.2),
     * physical mobile devices (LAN IP), and local storage URIs.
     */
    fun formatPhotoUrl(rawUrl: String?): String? {
        if (rawUrl.isNullOrBlank()) return null
        if (rawUrl.startsWith("content://") || rawUrl.startsWith("file://")) return rawUrl
        val baseUrl = getBaseUrl()
        return if (rawUrl.startsWith("http://", ignoreCase = true) || rawUrl.startsWith("https://", ignoreCase = true)) {
            try {
                val currentHost = java.net.URI(baseUrl).host
                val parsedUri = java.net.URI(rawUrl)
                if (parsedUri.host.equals("localhost", ignoreCase = true) || parsedUri.host.equals("127.0.0.1")) {
                    val portStr = if (parsedUri.port > 0 && parsedUri.port != 80 && parsedUri.port != 443) ":${parsedUri.port}" else ""
                    "${parsedUri.scheme}://$currentHost$portStr${parsedUri.rawPath}"
                } else {
                    rawUrl
                }
            } catch (_: Exception) {
                rawUrl
            }
        } else {
            val cleanBase = baseUrl.trimEnd('/')
            val cleanPath = rawUrl.trimStart('/')
            "$cleanBase/$cleanPath"
        }
    }
}
