package com.bignerdranch.playlistmaker.search.domain.interactorImpl

import com.bignerdranch.playlistmaker.util.Resource
import com.bignerdranch.playlistmaker.search.domain.api.TrackInteractor
import com.bignerdranch.playlistmaker.search.domain.api.TrackRepository
import com.bignerdranch.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class TrackInteractorImpl(
    private val repository: TrackRepository
): TrackInteractor {

    override fun searchTrack(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTrack(expression).map { result ->
            when(result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }
                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}