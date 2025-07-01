package com.bignerdranch.playlistmaker.settings

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bignerdranch.playlistmaker.Creator
import com.bignerdranch.playlistmaker.settings.domain.api.SettingsInteractor
import com.bignerdranch.playlistmaker.settings.domain.api.SharingInteractor
import com.bignerdranch.playlistmaker.settings.domain.model.ThemeSettings


class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
): ViewModel() {

    companion object {
        fun getFactory(application: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = application.applicationContext
                val settingsInteractor = Creator.provideSettingsInteractor(context)
                val sharingInteractor = Creator.provideSharingInteractor(context)
                SettingsViewModel(settingsInteractor, sharingInteractor)
            }
        }
    }

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