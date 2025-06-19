package com.bignerdranch.playlistmaker.domain.api

import com.bignerdranch.playlistmaker.domain.models.Track

interface TrackInteractor {

    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>)
    }
}