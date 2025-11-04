package com.bignerdranch.playlistmaker.settings.domain.interactorImpl

import com.bignerdranch.playlistmaker.settings.data.sharing.SettingsNavigator
import com.bignerdranch.playlistmaker.settings.domain.ShareTextProvider
import com.bignerdranch.playlistmaker.settings.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val navigator: SettingsNavigator,
    private val textProvider: ShareTextProvider

): SharingInteractor {
    override fun shareText(text: String) {
        navigator.shareText(text)
    }

    override fun openSupport() {
        navigator.openEmail(
            email = textProvider.getSupportEmail(),
            subject = textProvider.getSupportSubject(),
            body = textProvider.getSupportBody()
        )
    }

    override fun openTerms() {
        navigator.openLink(textProvider.getTermsUrl())
    }
}