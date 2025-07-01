package com.bignerdranch.playlistmaker.settings.data.settings

import com.bignerdranch.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsRepository {

    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)

}