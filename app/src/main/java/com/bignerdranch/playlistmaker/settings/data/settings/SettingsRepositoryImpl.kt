package com.bignerdranch.playlistmaker.settings.data.settings

import com.bignerdranch.playlistmaker.search.data.client.StorageClient
import com.bignerdranch.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(
    private val storageClient: StorageClient<ThemeSettings>
): SettingsRepository {
    override fun getThemeSettings(): ThemeSettings {
        return storageClient.getData() ?: ThemeSettings(isDarkTheme = false)
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        storageClient.storageData(settings)
    }
}