package com.bignerdranch.playlistmaker.di

import android.media.MediaPlayer
import org.koin.dsl.module

val audioModule = module {

  factory { MediaPlayer() }

}

