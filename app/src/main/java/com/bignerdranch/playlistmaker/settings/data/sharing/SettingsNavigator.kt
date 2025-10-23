package com.bignerdranch.playlistmaker.settings.data.sharing

interface SettingsNavigator {

    fun shareLink(url: String)
    fun openEmail(email: String, subject: String, body: String)
    fun openLink(url: String)

}