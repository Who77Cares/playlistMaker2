package com.bignerdranch.playlistmaker.di

import com.bignerdranch.playlistmaker.media.new_playlist.img_external_storage.ImgExternalStorage
import com.bignerdranch.playlistmaker.media.new_playlist.img_external_storage.ImgExternalStorageImpl
import com.bignerdranch.playlistmaker.media.new_playlist.img_external_storage.ImgExternalStorageUseCase
import com.bignerdranch.playlistmaker.media.new_playlist.img_external_storage.ImgExternalStorageUseCaseImpl
import com.bignerdranch.playlistmaker.search.data.client.NetworkClient
import com.bignerdranch.playlistmaker.search.data.network.RetrofitNetworkClient
import com.bignerdranch.playlistmaker.search.data.network.iTunesApi
import com.bignerdranch.playlistmaker.search.data.repositoryImpl.SearchHistoryRepositoryImpl
import com.bignerdranch.playlistmaker.search.data.repositoryImpl.TrackRepositoryImpl
import com.bignerdranch.playlistmaker.search.data.storage.PrefsStorageClient
import com.bignerdranch.playlistmaker.search.domain.api.SearchHistoryRepository
import com.bignerdranch.playlistmaker.search.domain.api.TrackRepository
import com.bignerdranch.playlistmaker.search.domain.models.Track
import com.google.gson.reflect.TypeToken
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {


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
    
    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(
            PrefsStorageClient(
                context = get(),
                dataKey = "HISTORY",
                type = object : TypeToken<ArrayList<Track>>() {}.type
            )
        )
    }


    single<ImgExternalStorage> {
        ImgExternalStorageImpl(get())
    }
    single<ImgExternalStorageUseCase> {
        ImgExternalStorageUseCaseImpl(get())
    }


}