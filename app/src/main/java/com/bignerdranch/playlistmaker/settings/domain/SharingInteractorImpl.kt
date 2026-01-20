package com.bignerdranch.playlistmaker.settings.domain

import com.bignerdranch.playlistmaker.settings.ShareTextProvider
import com.bignerdranch.playlistmaker.settings.data.api.SharingRepository
import com.bignerdranch.playlistmaker.settings.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val navigator: SharingRepository,
    private val textProvider: ShareTextProvider

): SharingInteractor {
    override fun shareText() {
        navigator.shareText(
            text = textProvider.getShareUrl()
        )
    }



    override fun openSupport() {
        navigator.openEmail(
            email = textProvider.getSupportEmail(),
            subject = textProvider.getSupportSubject(),
            body = textProvider.getSupportBody()
        )
    }

    override fun openLink() {
        navigator.openLink(textProvider.getTermsUrl())
    }




    override fun sharePlaylistText(text: String) {
        navigator.shareText(
            text = text
        )
    }
}