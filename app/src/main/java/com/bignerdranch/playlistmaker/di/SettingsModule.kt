package com.bignerdranch.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import com.bignerdranch.playlistmaker.search.data.storage.PrefsStorageClient
import com.bignerdranch.playlistmaker.settings.data.api.SharingRepository
import com.bignerdranch.playlistmaker.settings.data.SharingRepositoryImpl
import com.bignerdranch.playlistmaker.settings.ShareTextProvider
import com.bignerdranch.playlistmaker.settings.data.ThemeRepositoryImpl
import com.bignerdranch.playlistmaker.settings.data.api.ThemeRepository
import com.bignerdranch.playlistmaker.settings.domain.api.SharingInteractor
import com.bignerdranch.playlistmaker.settings.domain.SharingInteractorImpl
import com.bignerdranch.playlistmaker.settings.domain.ThemeInteractorImpl
import com.bignerdranch.playlistmaker.settings.domain.api.ThemeInteractor
import com.bignerdranch.playlistmaker.settings.ui.ui.SettingsViewModel
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {

    // Предоставляем SharedPreferences
    single<SharedPreferences> {
        androidContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    single<ThemeRepository> {
        ThemeRepositoryImpl(
            sharedPrefs = get()
        )
    }

    single<ThemeInteractor> {
        ThemeInteractorImpl(
            themeRepository = get()
        )
    }




    single<SharingRepository> {
        SharingRepositoryImpl(context = get())
    }


    single<ShareTextProvider> {
        ShareTextProvider(resources = get<Context>().resources)
    }

    single<SharingInteractor> {
        SharingInteractorImpl(
            navigator = get(),
            textProvider = get()
        )
    }

    viewModel {
        SettingsViewModel(
            themeInteractor = get(),
            sharingInteractor = get()
        )
    }

}