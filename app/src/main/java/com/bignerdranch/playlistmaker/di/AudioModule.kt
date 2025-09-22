package com.bignerdranch.playlistmaker.di

import android.media.MediaPlayer
import com.bignerdranch.playlistmaker.audio.ui.TrackAudioMapper
import org.koin.dsl.module

val audioModule = module {

  factory { MediaPlayer() }

  single { TrackAudioMapper() }

}

