package com.example.mkstore.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("mk_store_prefs", Context.MODE_PRIVATE)

    fun isFirstRun(): Boolean {
        return prefs.getBoolean(KEY_FIRST_RUN, true)
    }

    fun setFirstRunCompleted() {
        prefs.edit().putBoolean(KEY_FIRST_RUN, false).apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setLoggedIn(loggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply()
    }

    fun getUserEmail(): String {
        return prefs.getString(KEY_USER_EMAIL, "") ?: ""
    }

    fun getUserName(): String {
        return prefs.getString(KEY_USER_NAME, "") ?: ""
    }

    fun getUserPhotoUrl(): String {
        return prefs.getString(KEY_USER_PHOTO_URL, "") ?: ""
    }

    fun saveUserData(name: String, email: String, photoUrl: String = "") {
        prefs.edit()
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_PHOTO_URL, photoUrl)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }

    fun clearSession() {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .remove(KEY_USER_NAME)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_USER_PHOTO_URL)
            .apply()
    }

    companion object {
        private const val KEY_FIRST_RUN = "key_first_run"
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
        private const val KEY_USER_EMAIL = "key_user_email"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_USER_PHOTO_URL = "key_user_photo_url"
        const val GOOGLE_WEB_CLIENT_ID = "13664921779-nvlfrtjlp493uao404d3lunsjmkapeol.apps.googleusercontent.com"
    }
}
