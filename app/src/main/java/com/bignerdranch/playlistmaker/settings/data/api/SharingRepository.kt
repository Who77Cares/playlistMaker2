package com.bignerdranch.playlistmaker.settings.data.api

interface SharingRepository {
    fun shareText(text: String)
    fun openEmail(email: String, subject: String, body: String)
    fun openLink(url: String)
}