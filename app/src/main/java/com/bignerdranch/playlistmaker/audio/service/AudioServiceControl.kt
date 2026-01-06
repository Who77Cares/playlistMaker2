package com.bignerdranch.playlistmaker.audio.service

import com.bignerdranch.playlistmaker.audio.models.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface AudioServiceControl {

    val playerState: StateFlow<PlayerState>
    fun startPlayer()
    fun pausePlayer()
}