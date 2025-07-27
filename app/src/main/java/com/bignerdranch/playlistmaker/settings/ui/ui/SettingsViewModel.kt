package com.bignerdranch.playlistmaker.settings.ui.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bignerdranch.playlistmaker.settings.domain.api.SettingsInteractor
import com.bignerdranch.playlistmaker.settings.domain.api.SharingInteractor
import com.bignerdranch.playlistmaker.settings.domain.model.ThemeSettings


class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
): ViewModel() {



    private val theme = MutableLiveData<ThemeSettings>()
    fun observeTheme(): LiveData<ThemeSettings> = theme

    init {
        theme.value = settingsInteractor.getThemeSettings()
    }

    fun updateTheme(isDark: Boolean) {
        val newSettings = ThemeSettings(isDark)
        settingsInteractor.updateThemeSettings(newSettings)
        theme.value = newSettings
    }

    fun shareApp() = sharingInteractor.shareApp()
    fun openSupport() = sharingInteractor.openSupport()
    fun openTerms() = sharingInteractor.openTerms()



}