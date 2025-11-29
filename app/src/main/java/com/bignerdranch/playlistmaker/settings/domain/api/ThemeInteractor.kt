package com.bignerdranch.playlistmaker.settings.domain.api

import android.text.BoringLayout

interface ThemeInteractor {

    fun getCurrentTheme(): Boolean
    fun setTheme(isDark: Boolean)
    fun applyThemeToApp()

}