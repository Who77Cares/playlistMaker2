package com.bignerdranch.playlistmaker.di

import android.media.MediaPlayer
import com.bignerdranch.playlistmaker.audio.presentation.AudioPlayerViewModel
import com.bignerdranch.playlistmaker.util.TrackMapper
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val audioModule = module {

  factory { MediaPlayer() }

  viewModel { (mapper: TrackMapper) ->
    AudioPlayerViewModel(
      mapper = get(),
      favoriteTrackInteractor = get(),
      playlistInteractor = get()
    )
  }

}

