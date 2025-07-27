package com.bignerdranch.playlistmaker.settings.domain.api

import com.bignerdranch.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsInteractor {

    fun getThemeSettings(): ThemeSettings
    fun updateThemeSettings(settings: ThemeSettings)

}