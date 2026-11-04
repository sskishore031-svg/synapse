package com.siamts.synapse

import android.app.Application
import android.content.Context
import com.siamts.synapse.ai.LocalAIEngine
import com.siamts.synapse.data.local.SessionManager
import com.siamts.synapse.data.repository.SubjectCatalogRepository

/**
 * Main application class initializing application-level singletons such as [SessionManager], [LocalAIEngine], and [SubjectCatalogRepository].
 */
class SynapseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        SessionManager.init(this)
        LocalAIEngine.init(this)
        try {
            SubjectCatalogRepository.getInstance(this)
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
