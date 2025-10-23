package com.bignerdranch.playlistmaker.di

import androidx.room.Room
import com.bignerdranch.playlistmaker.util.TrackMapper
import com.bignerdranch.playlistmaker.media.db_favorite.data.FavoriteTrackRepository
import com.bignerdranch.playlistmaker.media.db_favorite.data.FavoriteTrackRepositoryImpl
import com.bignerdranch.playlistmaker.util.AppDatabase
import com.bignerdranch.playlistmaker.media.db_favorite.domain.FavoriteTrackInteractor
import com.bignerdranch.playlistmaker.media.db_favorite.domain.FavoriteTrackInteractorImpl
import com.bignerdranch.playlistmaker.media.new_playlist.PlaylistMapper
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.PlaylistRepository
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistInteractor
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistInteractorImpl
import com.bignerdranch.playlistmaker.new_playlist.db_playlists.PlaylistRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import kotlin.math.sin

val dbRoomModule = module {

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "db")
            .fallbackToDestructiveMigration() // удаляет старую верисю бд, обновляя ее новой (самый простой способ миграции с одной версии бд на другую)
            .build()
    }

    factory { TrackMapper() }

    single<FavoriteTrackRepository> {
        FavoriteTrackRepositoryImpl(get(), get())
    }

    single<FavoriteTrackInteractor> {
        FavoriteTrackInteractorImpl(get())
    }

    // внедрение для списка Playlist

    factory { PlaylistMapper }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get())
    }

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }




}