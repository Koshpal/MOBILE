package com.app.koshpal.app.presentation.themespecifics

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.app.koshpal.core.data.entities.enums.ThemePreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


object ThemeController {
    private lateinit var themePreferenceManager: ThemePreferences
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _currentThemePreference = MutableStateFlow(ThemePreference.SYSTEM)

    private val _isDarkThemeActive = MutableStateFlow(false)
    val isDarkThemeActive: StateFlow<Boolean> = _isDarkThemeActive.asStateFlow()

    fun init(manager: ThemePreferences) {
        themePreferenceManager = manager
        _currentThemePreference.value = themePreferenceManager.getThemePreference()
        updateActiveThemeBasedOnPreference()
    }

    fun setThemePreference(preference: ThemePreference) {
        themePreferenceManager.saveThemePreference(preference)
        _currentThemePreference.value = preference
        updateActiveThemeBasedOnPreference()
    }
    private fun updateActiveThemeBasedOnPreference() {
        scope.launch {
            val currentPref = _currentThemePreference.value
            _isDarkThemeActive.value = when (currentPref) {
                ThemePreference.LIGHT_MODE -> false
                ThemePreference.DARK_MODE -> true
                ThemePreference.SYSTEM -> isSystemDark
            }
        }
    }

    private var isSystemDark: Boolean = false
    fun updateSystemDarkThemeValue(isSystemDarkValue: Boolean) {
        isSystemDark = isSystemDarkValue
        if (_currentThemePreference.value == ThemePreference.SYSTEM) {
            updateActiveThemeBasedOnPreference()
        }
    }
}

@Composable
fun SystemThemeUpdater() {
    ThemeController.updateSystemDarkThemeValue(isSystemInDarkTheme())
}
