package com.bignerdranch.playlistmaker.settings.domain.interactorImpl

import com.bignerdranch.playlistmaker.settings.data.sharing.SettingsNavigator
import com.bignerdranch.playlistmaker.settings.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val navigator: SettingsNavigator,
    private val supportEmail: String,
    private val supportSubject: String,
    private val supportBody: String,
    private val termsUrl: String
): SharingInteractor {
    override fun shareText(text: String) {
        navigator.shareText(text)
    }

    override fun openSupport() {
        navigator.openEmail(supportEmail, supportSubject, supportBody)
    }

    override fun openTerms() {
        navigator.openLink(termsUrl)
    }
}