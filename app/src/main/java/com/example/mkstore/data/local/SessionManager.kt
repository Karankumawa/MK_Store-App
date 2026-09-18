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

    companion object {
        private const val KEY_FIRST_RUN = "key_first_run"
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
    }
}
