package com.bignerdranch.playlistmaker.domain.impl

import com.bignerdranch.playlistmaker.domain.api.TrackInteractor
import com.bignerdranch.playlistmaker.domain.api.TrackRepository
import java.util.concurrent.Executors


class TrackInteractorImpl(
    private val repository: TrackRepository
): TrackInteractor {

    private val executor = Executors.newCachedThreadPool() // приколы с многопоточкой

    override fun searchTracks(expression: String, consumer: TrackInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTrack(expression))
        }
    }
}