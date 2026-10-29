package com.siamts.synapse.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.siamts.synapse.data.model.UserDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "synapse_user_session")

/**
 * Singleton managing persistent authentication sessions via Android DataStore Preferences.
 */
class SessionManager private constructor(context: Context) {

    private val dataStore = context.applicationContext.sessionDataStore

    companion object {
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_USER_ID = intPreferencesKey("user_id")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_USER_PHONE = stringPreferencesKey("user_phone")
        private val KEY_USER_BIO = stringPreferencesKey("user_bio")
        private val KEY_USER_COURSE = stringPreferencesKey("user_course")
        private val KEY_USER_PREFERRED_LANG = stringPreferencesKey("user_preferred_language")
        private val KEY_USER_STUDY_GOAL = stringPreferencesKey("user_study_goal")
        private val KEY_USER_PHOTO_URL = stringPreferencesKey("user_photo_url")
        private val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_LAST_LOGIN_TIMESTAMP = longPreferencesKey("last_login_timestamp")

        @Volatile
        private var instance: SessionManager? = null

        fun init(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context.applicationContext).also { instance = it }
            }
        }

        fun getInstance(): SessionManager {
            return checkNotNull(instance) {
                "SessionManager must be initialized before use. Call SessionManager.init(context) in Application or Activity."
            }
        }

        fun getInstanceOrNull(): SessionManager? = instance
    }

    /**
     * Flow emitting the current [AuthSessionState] based on persistent DataStore.
     */
    val sessionStateFlow: Flow<AuthSessionState> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val isLoggedIn = preferences[KEY_IS_LOGGED_IN] ?: false
            val userId = preferences[KEY_USER_ID] ?: 0

            if (isLoggedIn && userId > 0) {
                val user = UserDto(
                    id = userId,
                    name = preferences[KEY_USER_NAME].orEmpty(),
                    email = preferences[KEY_USER_EMAIL].orEmpty(),
                    phone = preferences[KEY_USER_PHONE],
                    bio = preferences[KEY_USER_BIO],
                    course = preferences[KEY_USER_COURSE] ?: "B.Tech Computer Science · Year 3",
                    preferredLanguage = preferences[KEY_USER_PREFERRED_LANG] ?: "English",
                    studyGoal = preferences[KEY_USER_STUDY_GOAL] ?: "25 min / day",
                    photoUrl = preferences[KEY_USER_PHOTO_URL]
                )
                AuthSessionState.Authenticated(user)
            } else {
                AuthSessionState.Unauthenticated
            }
        }

    /**
     * Saves user login session upon successful authentication.
     * Note: Never saves user password.
     */
    suspend fun saveSession(user: UserDto?, token: String? = null) {
        if (user == null || user.id <= 0) return

        dataStore.edit { preferences ->
            preferences[KEY_IS_LOGGED_IN] = true
            preferences[KEY_USER_ID] = user.id
            preferences[KEY_USER_NAME] = user.name
            preferences[KEY_USER_EMAIL] = user.email
            if (!user.phone.isNullOrBlank()) preferences[KEY_USER_PHONE] = user.phone
            if (!user.bio.isNullOrBlank()) preferences[KEY_USER_BIO] = user.bio
            if (!user.course.isNullOrBlank()) preferences[KEY_USER_COURSE] = user.course
            if (!user.preferredLanguage.isNullOrBlank()) preferences[KEY_USER_PREFERRED_LANG] = user.preferredLanguage
            if (!user.studyGoal.isNullOrBlank()) preferences[KEY_USER_STUDY_GOAL] = user.studyGoal
            if (!user.photoUrl.isNullOrBlank()) preferences[KEY_USER_PHOTO_URL] = user.photoUrl
            if (!token.isNullOrBlank()) preferences[KEY_AUTH_TOKEN] = token
            preferences[KEY_LAST_LOGIN_TIMESTAMP] = System.currentTimeMillis()
        }
    }

    /**
     * Clears all session keys upon user logout.
     */
    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Returns true if user has a valid active session.
     */
    suspend fun isLoggedIn(): Boolean {
        return try {
            val preferences = dataStore.data.first()
            val isLoggedIn = preferences[KEY_IS_LOGGED_IN] ?: false
            val userId = preferences[KEY_USER_ID] ?: 0
            isLoggedIn && userId > 0
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Returns the cached user object if session is active.
     */
    suspend fun getSessionUser(): UserDto? {
        return try {
            val preferences = dataStore.data.first()
            val isLoggedIn = preferences[KEY_IS_LOGGED_IN] ?: false
            val userId = preferences[KEY_USER_ID] ?: 0
            if (isLoggedIn && userId > 0) {
                UserDto(
                    id = userId,
                    name = preferences[KEY_USER_NAME].orEmpty(),
                    email = preferences[KEY_USER_EMAIL].orEmpty(),
                    phone = preferences[KEY_USER_PHONE],
                    bio = preferences[KEY_USER_BIO],
                    course = preferences[KEY_USER_COURSE] ?: "B.Tech Computer Science · Year 3",
                    preferredLanguage = preferences[KEY_USER_PREFERRED_LANG] ?: "English",
                    studyGoal = preferences[KEY_USER_STUDY_GOAL] ?: "25 min / day",
                    photoUrl = preferences[KEY_USER_PHOTO_URL]
                )
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}

