package com.bignerdranch.playlistmaker.settings.data

import android.content.SharedPreferences
import com.bignerdranch.playlistmaker.settings.data.api.ThemeRepository
import androidx.core.content.edit

class ThemeRepositoryImpl(
    private val sharedPrefs: SharedPreferences
): ThemeRepository {

    companion object {
        private const val PREFS_NAME = "app_preferences"
        private const val THEME_KEY = "dark_theme"
    }

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPrefs.getBoolean(THEME_KEY, false)
    }

    override fun saveThemeToPref(theme: Boolean) {
        sharedPrefs.edit { putBoolean(THEME_KEY, theme) }
    }
}