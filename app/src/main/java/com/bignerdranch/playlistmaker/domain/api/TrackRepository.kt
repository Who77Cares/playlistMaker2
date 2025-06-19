package com.bignerdranch.playlistmaker.domain.api

import com.bignerdranch.playlistmaker.domain.models.Track

interface TrackRepository {

    fun searchTrack(expression: String): List<Track>
}