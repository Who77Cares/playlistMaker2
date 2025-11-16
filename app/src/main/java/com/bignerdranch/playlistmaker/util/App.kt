package com.bignerdranch.playlistmaker.util

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.bignerdranch.playlistmaker.di.dataModule
import com.bignerdranch.playlistmaker.di.audioModule
import com.bignerdranch.playlistmaker.di.dbRoomModule
import com.bignerdranch.playlistmaker.di.presentationModule
import com.bignerdranch.playlistmaker.di.searchModule
import com.bignerdranch.playlistmaker.di.settingsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidContext(this@App)
            modules(listOf(dataModule, searchModule, presentationModule, audioModule, dbRoomModule,
                settingsModule
            ))
        }

    }

}