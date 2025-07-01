package com.bignerdranch.playlistmaker.settings.domain.interactorImpl

import com.bignerdranch.playlistmaker.settings.data.sharing.ExternalNavigator
import com.bignerdranch.playlistmaker.settings.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val navigator: ExternalNavigator,
    private val shareUrl: String,
    private val supportEmail: String,
    private val supportSubject: String,
    private val supportBody: String,
    private val termsUrl: String
): SharingInteractor {
    override fun shareApp() {
        navigator.shareLink(shareUrl)
    }

    override fun openSupport() {
        navigator.openEmail(supportEmail, supportSubject, supportBody)
    }

    override fun openTerms() {
        navigator.openLink(termsUrl)
    }
}