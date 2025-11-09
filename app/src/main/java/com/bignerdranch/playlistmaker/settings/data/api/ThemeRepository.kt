package com.bignerdranch.playlistmaker.settings.data.api

interface ThemeRepository {

    fun isDarkThemeEnabled(): Boolean
    fun saveThemeToPref(theme: Boolean)

}