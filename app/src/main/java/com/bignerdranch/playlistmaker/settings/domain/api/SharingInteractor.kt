package com.bignerdranch.playlistmaker.settings.domain.api

interface SharingInteractor {

    fun shareText()
    fun openSupport()
    fun openLink()

    // метод для отпправки плейлиста. Как и для метода  shareText()
    // используем shareText() репозитория но т.к текс песен мы делаем в
    // коде. то и передаем text: String в качестве параметра
    fun sharePlaylistText(text: String)

}

