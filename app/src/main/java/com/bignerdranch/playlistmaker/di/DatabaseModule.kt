package com.bignerdranch.playlistmaker.di

import androidx.room.Room
import com.bignerdranch.playlistmaker.TrackMapper
import com.bignerdranch.playlistmaker.media.db_favorite.data.FavoriteTrackRepository
import com.bignerdranch.playlistmaker.media.db_favorite.data.FavoriteTrackRepositoryImpl
import com.bignerdranch.playlistmaker.AppDatabase
import com.bignerdranch.playlistmaker.media.db_favorite.domain.FavoriteTrackInteractor
import com.bignerdranch.playlistmaker.media.db_favorite.domain.FavoriteTrackInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbRoomModule = module {

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "db").build()
    }

    factory { TrackMapper() }

    single<FavoriteTrackRepository> {
        FavoriteTrackRepositoryImpl(get(), get())
    }

    single<FavoriteTrackInteractor> {
        FavoriteTrackInteractorImpl(get())
    }


}