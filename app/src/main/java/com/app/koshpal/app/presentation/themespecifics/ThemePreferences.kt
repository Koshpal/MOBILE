package com.app.koshpal.app.presentation.themespecifics

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.app.koshpal.core.data.entities.enums.ThemePreference

class ThemePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME_PREFERENCE = "app_theme_preference"
    }

    fun saveThemePreference(preference: ThemePreference) {
        prefs.edit { putString(KEY_THEME_PREFERENCE, preference.name) }
    }

    fun getThemePreference(): ThemePreference {
        val savedName = prefs.getString(KEY_THEME_PREFERENCE, ThemePreference.SYSTEM.name)
        return try {
            ThemePreference.valueOf(savedName ?: ThemePreference.SYSTEM.name)
        } catch (_: IllegalArgumentException) {
            ThemePreference.SYSTEM
        }
    }
}