package com.bignerdranch.playlistmaker.settings.ui.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bignerdranch.playlistmaker.settings.domain.api.SharingInteractor
import com.bignerdranch.playlistmaker.settings.domain.api.ThemeInteractor


class SettingsViewModel(
    private val themeInteractor: ThemeInteractor,
    private val sharingInteractor: SharingInteractor
): ViewModel() {

    private val themeState = MutableLiveData<Boolean>()
    fun observeTheme(): LiveData<Boolean> = themeState

    init {
        themeState.value = themeInteractor.getCurrentTheme()
    }

    fun toggleTheme(isDark: Boolean) {
        themeInteractor.setTheme(isDark)
        themeInteractor.applyThemeToApp()
        themeState.value = isDark
    }

    fun shareApp() = sharingInteractor.shareText()
    fun openSupport() = sharingInteractor.openSupport()
    fun openTerms() = sharingInteractor.openLink()

}