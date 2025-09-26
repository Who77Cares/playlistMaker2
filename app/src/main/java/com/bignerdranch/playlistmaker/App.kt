package com.bignerdranch.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.bignerdranch.playlistmaker.di.dataModule
import com.bignerdranch.playlistmaker.di.domainModule
import com.bignerdranch.playlistmaker.di.audioModule
import com.bignerdranch.playlistmaker.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App: Application() {
    companion object {
        const val PREFS_NAME = "app_preferences" // Имя SharedPreferences
        const val THEME_KEY = "dark_theme" // Ключ для сохранения темы
    }

    var darkTheme: Boolean = false


    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidContext(this@App)
            modules(listOf(dataModule, domainModule, presentationModule, audioModule))
        }

        // Получение SharedPreferences
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        // Чтение сохранённого значения темы или использование значения по умолчанию
        darkTheme = sharedPreferences.getBoolean(THEME_KEY, false)

        // Применение темы
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        // Сохранение значения темы в SharedPreferences
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        sharedPreferences.edit()
            .putBoolean(THEME_KEY, darkThemeEnabled)
            .apply()

        // Применение новой темы
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

}