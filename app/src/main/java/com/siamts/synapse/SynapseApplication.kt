package com.siamts.synapse

import android.app.Application
import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.siamts.synapse.ai.LocalAIEngine
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.remote.RetrofitClient
import com.siamts.synapse.data.repository.SubjectCatalogRepository
import com.siamts.synapse.notification.SynapseNotificationHelper

/**
 * Main application class initializing application-level singletons such as [SessionManager], [LocalAIEngine], [SubjectCatalogRepository], and [SynapseNotificationHelper].
 * Configures Coil ImageLoader globally to bypass ngrok browser warning headers across all image views.
 */
class SynapseApplication : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .okHttpClient { RetrofitClient.okHttpClient }
            .crossfade(true)
            .respectCacheHeaders(false)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        SessionManager.init(this)
        LocalAIEngine.init(this)
        try {
            SubjectCatalogRepository.getInstance(this)
        } catch (_: Throwable) {}
        try {
            com.siamts.synapse.data.local.SynapseDatabase.seedDatabase(this)
        } catch (_: Throwable) {}
        try {
            SynapseNotificationHelper.createNotificationChannel(this)
        } catch (_: Throwable) {}
    }

    companion object {
        private var instance: SynapseApplication? = null

        fun getInstance(): SynapseApplication {
            return checkNotNull(instance) { "SynapseApplication is not initialized yet." }
        }

        fun getAppContext(): Context {
            return checkNotNull(instance) { "SynapseApplication is not initialized yet." }.applicationContext
        }
    }
}
