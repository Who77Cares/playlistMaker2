package com.bignerdranch.playlistmaker.di

import androidx.room.Room
import com.bignerdranch.playlistmaker.TrackMapper
import com.bignerdranch.playlistmaker.media.db_favorite.data.FavoriteRepository
import com.bignerdranch.playlistmaker.media.db_favorite.data.FavoriteRepositoryImpl
import com.bignerdranch.playlistmaker.media.db_favorite.data.db.AppDatabase
import com.bignerdranch.playlistmaker.media.db_favorite.domain.FavoriteInteractor
import com.bignerdranch.playlistmaker.media.db_favorite.domain.FavoriteInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbRoomModule = module {

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "db").build()
    }

    factory { TrackMapper() }

    single<FavoriteRepository> {
        FavoriteRepositoryImpl(get(), get())
    }

    single<FavoriteInteractor> {
        FavoriteInteractorImpl(get())
    }


}