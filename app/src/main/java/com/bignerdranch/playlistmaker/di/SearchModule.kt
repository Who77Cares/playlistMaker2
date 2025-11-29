package com.bignerdranch.playlistmaker.di

import com.bignerdranch.playlistmaker.search.data.network.NetworkClient
import com.bignerdranch.playlistmaker.search.data.network.RetrofitNetworkClient
import com.bignerdranch.playlistmaker.search.data.network.TrackRepositoryImpl
import com.bignerdranch.playlistmaker.search.data.network.iTunesApi
import com.bignerdranch.playlistmaker.search.data.prefs_storage.PrefsStorageClient
import com.bignerdranch.playlistmaker.search.data.prefs_storage.SearchHistoryRepositoryImpl
import com.bignerdranch.playlistmaker.search.domain.network.Track
import com.bignerdranch.playlistmaker.search.domain.network.TrackInteractor
import com.bignerdranch.playlistmaker.search.domain.network.TrackInteractorImpl
import com.bignerdranch.playlistmaker.search.domain.network.TrackRepository
import com.bignerdranch.playlistmaker.search.domain.prefs_storage.SearchHistoryInteractor
import com.bignerdranch.playlistmaker.search.domain.prefs_storage.SearchHistoryInteractorImpl
import com.bignerdranch.playlistmaker.search.domain.prefs_storage.SearchHistoryRepository
import com.bignerdranch.playlistmaker.search.ui.SearchViewModel
import com.google.gson.reflect.TypeToken
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchModule = module {

    // для работы с iTunesAPI

    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(iTunesApi::class.java)}

    single<NetworkClient> {
        RetrofitNetworkClient(context = get(), itunesService = get())
    }

    single<TrackRepository> {
        TrackRepositoryImpl(networkClient = get())
    }

    single<TrackInteractor> {
        TrackInteractorImpl(repository = get())
    }

    viewModel {
        SearchViewModel(
            trackInteractor = get(),
            historyTrackInteractor = get()
        )
    }

    // для работы с префами

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(
            PrefsStorageClient(
                context = get(),
                dataKey = "HISTORY",
                type = object : TypeToken<ArrayList<Track>>() {}.type
            )
        )
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(repository = get())
    }

}