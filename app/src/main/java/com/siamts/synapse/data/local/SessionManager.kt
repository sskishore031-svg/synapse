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

    @Volatile
    private var cachedIsLoggedIn: Boolean = false

    @Volatile
    private var cachedAuthToken: String? = null

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
        private val KEY_USER_INTERESTS = stringPreferencesKey("user_interests")
        private val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_LAST_LOGIN_TIMESTAMP = longPreferencesKey("last_login_timestamp")
        private val KEY_STUDY_REMINDERS_ENABLED = booleanPreferencesKey("study_reminders_enabled")
        private val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val KEY_STUDY_REMINDER_HOUR = intPreferencesKey("study_reminder_hour")
        private val KEY_STUDY_REMINDER_MINUTE = intPreferencesKey("study_reminder_minute")
        private val KEY_STUDY_STREAK = intPreferencesKey("study_streak_days")
        private val KEY_LAST_ACTIVE_DATE = stringPreferencesKey("last_active_date")

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
            val email = preferences[KEY_USER_EMAIL].orEmpty()
            val name = preferences[KEY_USER_NAME].orEmpty()
            val token = preferences[KEY_AUTH_TOKEN]
            if (!token.isNullOrBlank()) {
                cachedAuthToken = token
            }
            val authenticated = isLoggedIn && (userId > 0 || email.isNotBlank() || name.isNotBlank())
            cachedIsLoggedIn = authenticated

            if (authenticated) {
                val resolvedId = if (userId > 0) userId else 1
                val interestsRaw = preferences[KEY_USER_INTERESTS].orEmpty()
                val parsedInterests = if (interestsRaw.isNotBlank()) {
                    interestsRaw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                } else {
                    listOf("Computer Science", "Artificial Intelligence", "Mathematics")
                }

                val user = UserDto(
                    id = resolvedId,
                    name = name,
                    email = email,
                    phone = preferences[KEY_USER_PHONE],
                    bio = preferences[KEY_USER_BIO],
                    course = preferences[KEY_USER_COURSE] ?: "B.Tech Computer Science · Year 3",
                    preferredLanguage = preferences[KEY_USER_PREFERRED_LANG] ?: "English",
                    interests = parsedInterests,
                    selectedInterests = parsedInterests,
                    studyGoal = preferences[KEY_USER_STUDY_GOAL] ?: "25 min / day",
                    photoUrl = preferences[KEY_USER_PHOTO_URL],
                    avatarUrl = preferences[KEY_USER_PHOTO_URL],
                    profilePhoto = preferences[KEY_USER_PHOTO_URL]
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
        if (user == null) return
        val resolvedId = if (user.id > 0) user.id else 1
        cachedIsLoggedIn = true

        dataStore.edit { preferences ->
            preferences[KEY_IS_LOGGED_IN] = true
            preferences[KEY_USER_ID] = resolvedId
            preferences[KEY_USER_NAME] = user.name
            preferences[KEY_USER_EMAIL] = user.email
            if (!user.phone.isNullOrBlank()) preferences[KEY_USER_PHONE] = user.phone
            if (!user.bio.isNullOrBlank()) preferences[KEY_USER_BIO] = user.bio
            if (!user.course.isNullOrBlank()) preferences[KEY_USER_COURSE] = user.course
            if (!user.preferredLanguage.isNullOrBlank()) preferences[KEY_USER_PREFERRED_LANG] = user.preferredLanguage
            if (!user.studyGoal.isNullOrBlank()) preferences[KEY_USER_STUDY_GOAL] = user.studyGoal
            val userInterests = (user.selectedInterests ?: user.interests).orEmpty()
            if (userInterests.isNotEmpty()) {
                preferences[KEY_USER_INTERESTS] = userInterests.joinToString(",")
            }
            val resolvedPhoto = listOfNotNull(user.photoUrl, user.avatarUrl, user.profilePhoto, user.profileImage).firstOrNull { it.isNotBlank() }
            if (!resolvedPhoto.isNullOrBlank()) {
                preferences[KEY_USER_PHOTO_URL] = resolvedPhoto
            } else {
                preferences.remove(KEY_USER_PHOTO_URL)
            }
            if (!token.isNullOrBlank()) preferences[KEY_AUTH_TOKEN] = token
            preferences[KEY_LAST_LOGIN_TIMESTAMP] = System.currentTimeMillis()
        }
    }

    /**
     * Direct string/id overload for saving session immediately.
     */
    suspend fun saveSession(
        name: String,
        email: String,
        userId: Int = 1,
        phone: String? = null,
        token: String? = null
    ) {
        saveSession(
            UserDto(
                id = if (userId > 0) userId else 1,
                name = name,
                email = email,
                phone = phone
            ),
            token = token
        )
    }

    val userNameFlow: Flow<String> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences -> preferences[KEY_USER_NAME].orEmpty() }

    suspend fun getUserName(): String {
        return try {
            val prefs = dataStore.data.first()
            prefs[KEY_USER_NAME]?.trim()?.takeIf { it.isNotBlank() } ?: "Kishore"
        } catch (_: Exception) {
            "Kishore"
        }
    }

    suspend fun updateUserName(name: String) {
        if (name.isBlank()) return
        dataStore.edit { preferences ->
            preferences[KEY_USER_NAME] = name
        }
    }

    val studyStreakFlow: Flow<Int> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences -> preferences[KEY_STUDY_STREAK] ?: 0 }

    suspend fun recordActiveStudyDay(): Int {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
        val yesterday = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(
            java.util.Date(System.currentTimeMillis() - 86400000L)
        )
        var streak = 1
        dataStore.edit { preferences ->
            val lastDate = preferences[KEY_LAST_ACTIVE_DATE].orEmpty()
            val currentStreak = preferences[KEY_STUDY_STREAK] ?: 0
            streak = when (lastDate) {
                today -> if (currentStreak > 0) currentStreak else 1
                yesterday -> currentStreak + 1
                else -> 1
            }
            preferences[KEY_LAST_ACTIVE_DATE] = today
            preferences[KEY_STUDY_STREAK] = streak
        }
        return streak
    }

    suspend fun getStudyStreak(): Int {
        return try {
            val prefs = dataStore.data.first()
            prefs[KEY_STUDY_STREAK] ?: 0
        } catch (_: Exception) {
            0
        }
    }

    suspend fun updateUserPhoto(photoUrl: String?) {
        dataStore.edit { preferences ->
            if (photoUrl.isNullOrBlank()) {
                preferences.remove(KEY_USER_PHOTO_URL)
            } else {
                preferences[KEY_USER_PHOTO_URL] = photoUrl
            }
        }
        try {
            com.siamts.synapse.data.repository.UserPreferencesRepository.getInstanceOrNull()?.setProfileImagePath(photoUrl)
        } catch (_: Throwable) {}
    }

    suspend fun updateInterests(interests: List<String>) {
        dataStore.edit { preferences ->
            if (interests.isNotEmpty()) {
                preferences[KEY_USER_INTERESTS] = interests.joinToString(",")
            }
        }
    }

    val studyRemindersEnabledFlow: Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences -> preferences[KEY_STUDY_REMINDERS_ENABLED] ?: true }

    val notificationsEnabledFlow: Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences -> preferences[KEY_NOTIFICATIONS_ENABLED] ?: true }

    suspend fun setStudyRemindersEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_STUDY_REMINDERS_ENABLED] = enabled
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun isStudyRemindersEnabled(): Boolean {
        return try {
            val preferences = dataStore.data.first()
            preferences[KEY_STUDY_REMINDERS_ENABLED] ?: true
        } catch (_: Exception) {
            true
        }
    }

    suspend fun isNotificationsEnabled(): Boolean {
        return try {
            val preferences = dataStore.data.first()
            preferences[KEY_NOTIFICATIONS_ENABLED] ?: true
        } catch (_: Exception) {
            true
        }
    }

    val studyReminderHourFlow: Flow<Int> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences -> preferences[KEY_STUDY_REMINDER_HOUR] ?: 19 }

    val studyReminderMinuteFlow: Flow<Int> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences -> preferences[KEY_STUDY_REMINDER_MINUTE] ?: 0 }

    val studyReminderFormattedTimeFlow: Flow<String> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            val hour = preferences[KEY_STUDY_REMINDER_HOUR] ?: 19
            val minute = preferences[KEY_STUDY_REMINDER_MINUTE] ?: 0
            val period = if (hour >= 12) "PM" else "AM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            String.format(java.util.Locale.US, "%02d:%02d %s", displayHour, minute, period)
        }

    suspend fun setStudyReminderTime(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_STUDY_REMINDER_HOUR] = hour
            preferences[KEY_STUDY_REMINDER_MINUTE] = minute
        }
    }

    suspend fun getStudyReminderHour(): Int {
        return try {
            dataStore.data.first()[KEY_STUDY_REMINDER_HOUR] ?: 19
        } catch (_: Exception) {
            19
        }
    }

    suspend fun getStudyReminderMinute(): Int {
        return try {
            dataStore.data.first()[KEY_STUDY_REMINDER_MINUTE] ?: 0
        } catch (_: Exception) {
            0
        }
    }

    /**
     * Clears active session state upon user logout while preserving persistent local user data.
     */
    suspend fun clearSession() {
        cachedIsLoggedIn = false
        cachedAuthToken = null
        dataStore.edit { preferences ->
            preferences[KEY_IS_LOGGED_IN] = false
            preferences.remove(KEY_AUTH_TOKEN)
            preferences.remove(KEY_USER_ID)
            preferences.remove(KEY_USER_NAME)
            preferences.remove(KEY_USER_EMAIL)
            preferences.remove(KEY_USER_PHONE)
            preferences.remove(KEY_USER_BIO)
            preferences.remove(KEY_USER_PHOTO_URL)
            preferences.remove(KEY_USER_INTERESTS)
        }
    }

    fun getAuthTokenCached(): String? = cachedAuthToken

    suspend fun getAuthToken(): String? {
        return try {
            val token = dataStore.data.first()[KEY_AUTH_TOKEN]
            if (!token.isNullOrBlank()) {
                cachedAuthToken = token
                token
            } else {
                cachedAuthToken
            }
        } catch (_: Exception) {
            cachedAuthToken
        }
    }

    /**
     * Synchronously returns cached login state.
     */
    fun isLoggedInCached(): Boolean = cachedIsLoggedIn

    /**
     * Returns true if user has a valid active session.
     */
    suspend fun isLoggedIn(): Boolean {
        return try {
            val preferences = dataStore.data.first()
            val isLoggedIn = preferences[KEY_IS_LOGGED_IN] ?: false
            val userId = preferences[KEY_USER_ID] ?: 0
            val email = preferences[KEY_USER_EMAIL].orEmpty()
            val name = preferences[KEY_USER_NAME].orEmpty()
            val token = preferences[KEY_AUTH_TOKEN]
            if (!token.isNullOrBlank()) {
                cachedAuthToken = token
            }
            val authenticated = isLoggedIn && (userId > 0 || email.isNotBlank() || name.isNotBlank())
            cachedIsLoggedIn = authenticated
            authenticated
        } catch (_: Exception) {
            cachedIsLoggedIn
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
            val email = preferences[KEY_USER_EMAIL].orEmpty()
            val name = preferences[KEY_USER_NAME].orEmpty()
            val authenticated = isLoggedIn && (userId > 0 || email.isNotBlank() || name.isNotBlank())
            if (authenticated) {
                UserDto(
                    id = if (userId > 0) userId else 1,
                    name = name,
                    email = email,
                    phone = preferences[KEY_USER_PHONE],
                    bio = preferences[KEY_USER_BIO],
                    course = preferences[KEY_USER_COURSE] ?: "B.Tech Computer Science · Year 3",
                    preferredLanguage = preferences[KEY_USER_PREFERRED_LANG] ?: "English",
                    studyGoal = preferences[KEY_USER_STUDY_GOAL] ?: "25 min / day",
                    photoUrl = preferences[KEY_USER_PHOTO_URL],
                    avatarUrl = preferences[KEY_USER_PHOTO_URL],
                    profilePhoto = preferences[KEY_USER_PHOTO_URL]
                )
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}

