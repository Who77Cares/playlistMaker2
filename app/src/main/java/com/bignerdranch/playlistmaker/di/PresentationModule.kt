package com.bignerdranch.playlistmaker.di

import com.bignerdranch.playlistmaker.util.TrackMapper
import com.bignerdranch.playlistmaker.audio.ui.presentation.AudioPlayerViewModel
import com.bignerdranch.playlistmaker.media.db_favorite.ui.FavoriteTracksViewModel
import com.bignerdranch.playlistmaker.media.new_playlist.ui.NewPlaylistViewModel
import com.bignerdranch.playlistmaker.media.playlist.SinglePlaylistViewModel
import com.bignerdranch.playlistmaker.media.playlists.PlaylistViewModel
import com.bignerdranch.playlistmaker.search.ui.presentation.SearchViewModel
import com.bignerdranch.playlistmaker.settings.ui.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel

import org.koin.dsl.module

val presentationModule = module {

    viewModel { (mapper: TrackMapper) ->
        AudioPlayerViewModel(
            mapper = get(),
            mediaPlayer = get(),
            favoriteTrackInteractor = get(),
            playlistInteractor = get()
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

    viewModel { FavoriteTracksViewModel(get()) }


    viewModel { NewPlaylistViewModel(get(), get()) }

    viewModel { PlaylistViewModel(get()) }

    viewModel { SinglePlaylistViewModel(get()) }

}