package com.bignerdranch.playlistmaker.di

import com.bignerdranch.playlistmaker.audio.ui.TrackAudioMapper
import com.bignerdranch.playlistmaker.audio.ui.presentation.AudioPlayerViewModel
import com.bignerdranch.playlistmaker.search.ui.presentation.SearchViewModel
import com.bignerdranch.playlistmaker.settings.ui.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel

import org.koin.dsl.module

val presentationModule = module {

    viewModel { (mapper: TrackAudioMapper) ->
        AudioPlayerViewModel(mapper = get())
    }

    viewModel {
        SearchViewModel(
            context = get(),
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

}