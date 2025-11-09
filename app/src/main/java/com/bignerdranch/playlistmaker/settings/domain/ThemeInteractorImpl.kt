package com.bignerdranch.playlistmaker.settings.domain

import androidx.appcompat.app.AppCompatDelegate
import com.bignerdranch.playlistmaker.settings.data.api.ThemeRepository
import com.bignerdranch.playlistmaker.settings.domain.api.ThemeInteractor

class ThemeInteractorImpl(
    private val themeRepository: ThemeRepository
): ThemeInteractor {
    override fun getCurrentTheme(): Boolean {
        return themeRepository.isDarkThemeEnabled()
    }

    override fun setTheme(isDark: Boolean) {
        themeRepository.saveThemeToPref(isDark)
    }

    override fun applyThemeToApp() {
        val isDarkTheme = themeRepository.isDarkThemeEnabled()
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}