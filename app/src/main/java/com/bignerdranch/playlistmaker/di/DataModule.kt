package com.bignerdranch.playlistmaker.di

import com.bignerdranch.playlistmaker.search.data.client.NetworkClient
import com.bignerdranch.playlistmaker.search.data.network.RetrofitNetworkClient
import com.bignerdranch.playlistmaker.search.data.repositoryImpl.SearchHistoryRepositoryImpl
import com.bignerdranch.playlistmaker.search.data.repositoryImpl.TrackRepositoryImpl
import com.bignerdranch.playlistmaker.search.data.storage.PrefsStorageClient
import com.bignerdranch.playlistmaker.search.domain.api.SearchHistoryRepository
import com.bignerdranch.playlistmaker.search.domain.api.TrackRepository
import com.bignerdranch.playlistmaker.search.domain.models.Track
import com.bignerdranch.playlistmaker.settings.data.settings.SettingsRepository
import com.bignerdranch.playlistmaker.settings.data.settings.SettingsRepositoryImpl
import com.bignerdranch.playlistmaker.settings.domain.model.ThemeSettings
import com.google.gson.reflect.TypeToken
import org.koin.dsl.module

val dataModule = module {

    single<NetworkClient> {
        RetrofitNetworkClient(context = get())
    }
    
    single<TrackRepository> {
        TrackRepositoryImpl(networkClient = get())
    }
    
    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(
            PrefsStorageClient(
                context = get(),
                dataKey = "HISTORY",
                type = object : TypeToken<ArrayList<Track>>() {}.type
            )
        )
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(
            PrefsStorageClient(
                context = get(),
                dataKey = "theme_settings",
                type = object : TypeToken<ThemeSettings>() {}.type
            )
        )
    }

}