package com.bignerdranch.playlistmaker.settings.domain.interactorImpl

import com.bignerdranch.playlistmaker.settings.domain.api.SettingsInteractor
import com.bignerdranch.playlistmaker.settings.data.settings.SettingsRepository
import com.bignerdranch.playlistmaker.settings.domain.model.ThemeSettings

class SettingsInteractorImpl(
    private val repository: SettingsRepository
): SettingsInteractor {
    override fun getThemeSettings(): ThemeSettings {
        return repository.getThemeSettings()
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        repository.updateThemeSetting(settings)
    }
}