package com.bignerdranch.playlistmaker.di

import com.bignerdranch.playlistmaker.media.db_favorite.ui.FavoriteTracksViewModel
import com.bignerdranch.playlistmaker.media.new_playlist.ui.NewPlaylistViewModel
import com.bignerdranch.playlistmaker.media.playlist.SinglePlaylistViewModel
import com.bignerdranch.playlistmaker.media.playlists.PlaylistViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel

import org.koin.dsl.module

val presentationModule = module {


    viewModel { FavoriteTracksViewModel(get()) }

    viewModel { NewPlaylistViewModel(get(), get()) }

    viewModel { PlaylistViewModel(get()) }

    viewModel { SinglePlaylistViewModel(get(), get(), androidContext())}

}