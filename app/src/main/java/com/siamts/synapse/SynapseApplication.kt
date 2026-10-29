package com.siamts.synapse

import android.app.Application
import com.siamts.synapse.data.local.SessionManager

/**
 * Main application class initializing application-level singletons such as [SessionManager].
 */
class SynapseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
    }
}

