// PreferencesManager.kt
package com.thezayin.framework.pref

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PreferencesManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "com.thezayin.app.prefs"
        private const val KEY_IS_FIRST_TIME = "is_first_time"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _isFirstTime = MutableStateFlow(true)
    val isFirstTime: StateFlow<Boolean> = _isFirstTime

    init {
        _isFirstTime.value = sharedPreferences.getBoolean(KEY_IS_FIRST_TIME, true)
    }

    /**
     * Marks that the user has completed onboarding.
     */
    fun setOnboardingCompleted() {
        sharedPreferences.edit {
            putBoolean(KEY_IS_FIRST_TIME, false)
        }
        _isFirstTime.value = false
    }
}
