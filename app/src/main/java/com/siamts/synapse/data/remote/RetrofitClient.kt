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
            }
        }
    }

    fun setBaseUrl(newUrl: String) = updateBaseUrl(newUrl)

    fun getBaseUrl(): String = currentBaseUrl
}
