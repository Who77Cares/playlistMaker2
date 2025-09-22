package com.bignerdranch.playlistmaker.search.domain.interactorImpl

import com.bignerdranch.playlistmaker.Resource
import com.bignerdranch.playlistmaker.search.domain.api.TrackInteractor
import com.bignerdranch.playlistmaker.search.domain.api.TrackRepository
import com.bignerdranch.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.time.measureDurationForResult
import java.util.concurrent.Executors


class TrackInteractorImpl(
    private val repository: TrackRepository
): TrackInteractor {

    override fun searchTrack(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTrack(expression).map { result ->
            when(result) {
                is Resource.Error -> {
                    Pair(result.data, null)
                }
                is Resource.Success -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}