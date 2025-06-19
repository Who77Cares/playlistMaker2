package com.bignerdranch.playlistmaker.domain.impl

import com.bignerdranch.playlistmaker.Resource
import com.bignerdranch.playlistmaker.domain.api.TrackInteractor
import com.bignerdranch.playlistmaker.domain.api.TrackRepository
import java.util.concurrent.Executors


class TrackInteractorImpl(
    private val repository: TrackRepository
): TrackInteractor {

    private val executor = Executors.newCachedThreadPool() // приколы с многопоточкой

    override fun searchTracks(expression: String, consumer: TrackInteractor.TracksConsumer) {
        executor.execute {
            val resource = repository.searchTrack(expression)

            when(resource) {
                is Resource.Success -> { consumer.consume(resource.data, null) }
                is Resource.Error -> { consumer.consume(null, resource.message) }
            }
        }
    }
}