package com.bignerdranch.playlistmaker.settings.domain

import android.content.res.Resources
import com.bignerdranch.playlistmaker.R

class ShareTextProvider(
    private val resources: Resources
) {
    fun getSupportEmail() = resources.getString(R.string.sendToSupport_email)
    fun getSupportSubject() = resources.getString(R.string.sendToSupport_theme)
    fun getSupportBody() = resources.getString(R.string.sendToSupport_text)
    fun getTermsUrl() = resources.getString(R.string.userAgreement_url)
}