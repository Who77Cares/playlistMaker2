package com.bignerdranch.playlistmaker.settings.domain.api

interface SharingInteractor {

    fun shareText(text: String)
    fun openSupport()
    fun openTerms()

}