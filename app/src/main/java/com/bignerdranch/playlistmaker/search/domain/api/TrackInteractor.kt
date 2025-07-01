package com.bignerdranch.playlistmaker.search.domain.api

import com.bignerdranch.playlistmaker.search.domain.models.Track

interface TrackInteractor {

    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
}