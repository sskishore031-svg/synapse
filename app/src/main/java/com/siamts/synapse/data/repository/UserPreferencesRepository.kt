package com.siamts.synapse.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Locale

private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "synapse_user_preferences")

/**
 * Repository persisting offline user preferences including custom scheduled study times.
 */
class UserPreferencesRepository private constructor(context: Context) {

    private val dataStore: DataStore<Preferences> = context.applicationContext.userPreferencesDataStore

    companion object {
        val KEY_STUDY_REMINDER_ENABLED = booleanPreferencesKey("study_reminder_enabled")
        val KEY_STUDY_REMINDER_HOUR = intPreferencesKey("study_reminder_hour")
        val KEY_STUDY_REMINDER_MINUTE = intPreferencesKey("study_reminder_minute")

        val KEY_PREFERRED_NAME = stringPreferencesKey("ai_preferred_name")
        val KEY_CHAT_PREFERRED_NAME = stringPreferencesKey("chat_preferred_name")
        val KEY_INCLUDE_FORMULAS = booleanPreferencesKey("ai_include_formulas")
        val KEY_INCLUDE_EXAMPLES = booleanPreferencesKey("ai_include_examples")
        val KEY_USE_EMOJIS = booleanPreferencesKey("ai_use_emojis")

        const val DEFAULT_HOUR = 19 // 7:00 PM
        const val DEFAULT_MINUTE = 0

        @Volatile
        private var instance: UserPreferencesRepository? = null

        fun getInstance(context: Context): UserPreferencesRepository {
            return instance ?: synchronized(this) {
                instance ?: UserPreferencesRepository(context.applicationContext).also { instance = it }
            }
        }

        fun getInstanceOrNull(): UserPreferencesRepository? = instance

        /**
         * Formats 24-hour hour & minute into standard 12-hour format e.g. "07:00 PM".
         */
        fun formatTime(hour: Int, minute: Int): String {
            val period = if (hour >= 12) "PM" else "AM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            return String.format(Locale.US, "%02d:%02d %s", displayHour, minute, period)
        }
    }

    val studyReminderEnabledFlow: Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[KEY_STUDY_REMINDER_ENABLED] ?: true }

    val studyReminderHourFlow: Flow<Int> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[KEY_STUDY_REMINDER_HOUR] ?: DEFAULT_HOUR }

    val studyReminderMinuteFlow: Flow<Int> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[KEY_STUDY_REMINDER_MINUTE] ?: DEFAULT_MINUTE }

    val studyReminderFormattedTimeFlow: Flow<String> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            val h = prefs[KEY_STUDY_REMINDER_HOUR] ?: DEFAULT_HOUR
            val m = prefs[KEY_STUDY_REMINDER_MINUTE] ?: DEFAULT_MINUTE
            formatTime(h, m)
        }

    suspend fun setStudyReminderTime(hour: Int, minute: Int) {
        dataStore.edit { prefs ->
            prefs[KEY_STUDY_REMINDER_HOUR] = hour
            prefs[KEY_STUDY_REMINDER_MINUTE] = minute
        }
    }

    suspend fun setStudyReminderEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_STUDY_REMINDER_ENABLED] = enabled
        }
    }

    suspend fun getStudyReminderHour(): Int {
        return try {
            dataStore.data.first()[KEY_STUDY_REMINDER_HOUR] ?: DEFAULT_HOUR
        } catch (_: Exception) {
            DEFAULT_HOUR
        }
    }

    suspend fun getStudyReminderMinute(): Int {
        return try {
            dataStore.data.first()[KEY_STUDY_REMINDER_MINUTE] ?: DEFAULT_MINUTE
        } catch (_: Exception) {
            DEFAULT_MINUTE
        }
    }

    suspend fun isStudyReminderEnabled(): Boolean {
        return try {
            dataStore.data.first()[KEY_STUDY_REMINDER_ENABLED] ?: true
        } catch (_: Exception) {
            true
        }
    }

    suspend fun getFormattedStudyTime(): String {
        return formatTime(getStudyReminderHour(), getStudyReminderMinute())
    }

    // --- AI Chat Formatting & Nickname Memory Preferences ---

    val preferredNameFlow: Flow<String> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[KEY_CHAT_PREFERRED_NAME]?.trim() ?: prefs[KEY_PREFERRED_NAME].orEmpty().trim() }

    val chatPreferredNameFlow: Flow<String> = preferredNameFlow

    val includeFormulasFlow: Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[KEY_INCLUDE_FORMULAS] ?: true }

    val includeExamplesFlow: Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[KEY_INCLUDE_EXAMPLES] ?: true }

    val useEmojisFlow: Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[KEY_USE_EMOJIS] ?: false }

    suspend fun setChatPreferredName(name: String) {
        dataStore.edit { prefs ->
            val clean = name.trim()
            prefs[KEY_CHAT_PREFERRED_NAME] = clean
            prefs[KEY_PREFERRED_NAME] = clean
        }
    }

    suspend fun getChatPreferredName(): String {
        return try {
            val prefs = dataStore.data.first()
            prefs[KEY_CHAT_PREFERRED_NAME]?.trim()
                ?: prefs[KEY_PREFERRED_NAME]?.trim()
                ?: ""
        } catch (_: Exception) {
            ""
        }
    }

    suspend fun setPreferredName(name: String) {
        setChatPreferredName(name)
    }

    suspend fun getPreferredName(): String {
        return getChatPreferredName()
    }

    suspend fun setIncludeFormulas(include: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_INCLUDE_FORMULAS] = include
        }
    }

    suspend fun getIncludeFormulas(): Boolean {
        return try {
            dataStore.data.first()[KEY_INCLUDE_FORMULAS] ?: true
        } catch (_: Exception) {
            true
        }
    }

    suspend fun setIncludeExamples(include: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_INCLUDE_EXAMPLES] = include
        }
    }

    suspend fun getIncludeExamples(): Boolean {
        return try {
            dataStore.data.first()[KEY_INCLUDE_EXAMPLES] ?: true
        } catch (_: Exception) {
            true
        }
    }

    suspend fun setUseEmojis(use: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_USE_EMOJIS] = use
        }
    }

    suspend fun getUseEmojis(): Boolean {
        return try {
            dataStore.data.first()[KEY_USE_EMOJIS] ?: false
        } catch (_: Exception) {
            false
        }
    }
}

