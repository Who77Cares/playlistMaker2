package com.bignerdranch.playlistmaker.di

import com.bignerdranch.playlistmaker.TrackMapper
import com.bignerdranch.playlistmaker.audio.ui.presentation.AudioPlayerViewModel
import com.bignerdranch.playlistmaker.media.db_favorite.ui.FavoriteMediaViewModel
import com.bignerdranch.playlistmaker.media.ui.PlaylistMediaViewModel
import com.bignerdranch.playlistmaker.new_playlist.NewPlaylistViewModel
import com.bignerdranch.playlistmaker.search.ui.presentation.SearchViewModel
import com.bignerdranch.playlistmaker.settings.ui.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel

import org.koin.dsl.module

val presentationModule = module {

    viewModel { (mapper: TrackMapper) ->
        AudioPlayerViewModel(
            mapper = get(),
            mediaPlayer = get(),
            favoriteTrackInteractor = get()
        )
    }

    viewModel {
        SearchViewModel(
            trackInteractor = get(),
            historyTrackInteractor = get()
        )
    }

    viewModel {
        SettingsViewModel(
            settingsInteractor = get(),
            sharingInteractor = get()
        )
    }

    viewModel { FavoriteMediaViewModel(get()) }
    viewModel { PlaylistMediaViewModel() }

    viewModel { NewPlaylistViewModel() }

}