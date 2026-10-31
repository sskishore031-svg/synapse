package com.siamts.synapse.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.io.IOException

val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "synapse_theme_prefs")

/**
 * Repository managing persistent theme mode settings across app sessions
 * using Android Jetpack DataStore Preferences with synchronous cache fallback.
 */
class ThemePreferences private constructor(context: Context) {

    private val appContext = context.applicationContext
    private val dataStore = appContext.themeDataStore
    private val sharedPrefs = appContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private const val SP_NAME = "synapse_theme_cache"
        private const val SP_KEY_THEME_MODE = "cached_theme_mode"
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode_key")

        @Volatile
        private var instance: ThemePreferences? = null

        fun getInstance(context: Context): ThemePreferences {
            return instance ?: synchronized(this) {
                instance ?: ThemePreferences(context.applicationContext).also { instance = it }
            }
        }
    }

    /**
     * Synchronously returns cached theme mode to prevent cold-start UI flicker.
     */
    fun getCachedThemeMode(): ThemeMode {
        val cachedValue = sharedPrefs.getString(SP_KEY_THEME_MODE, ThemeMode.SYSTEM.value)
        return ThemeMode.fromValue(cachedValue)
    }

    /**
     * Reactive DataStore Flow emitting theme changes.
     */
    val themeModeFlow: Flow<ThemeMode> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val stored = preferences[KEY_THEME_MODE]
            if (stored != null) {
                ThemeMode.fromValue(stored)
            } else {
                getCachedThemeMode()
            }
        }

    /**
     * Eagerly shared StateFlow for instant reactive Compose consumption.
     */
    val themeModeState: StateFlow<ThemeMode> = themeModeFlow.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = getCachedThemeMode()
    )

    /**
     * Persists new theme selection to both fast-cache SharedPreferences and DataStore.
     */
    suspend fun setThemeMode(mode: ThemeMode) {
        // Fast synchronous cache for instant cold start
        sharedPrefs.edit().putString(SP_KEY_THEME_MODE, mode.value).apply()

        // Persistent Jetpack DataStore
        dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = mode.value
        }
    }
}
