package com.siamts.synapse

import android.app.Application
import android.content.Context
import com.siamts.synapse.data.local.SessionManager

/**
 * Main application class initializing application-level singletons such as [SessionManager].
 */
class SynapseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        SessionManager.init(this)
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
