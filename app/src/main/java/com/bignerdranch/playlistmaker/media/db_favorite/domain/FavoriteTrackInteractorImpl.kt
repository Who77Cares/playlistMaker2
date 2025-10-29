package com.bignerdranch.playlistmaker.media.db_favorite.domain

import com.bignerdranch.playlistmaker.media.db_favorite.data.FavoriteTrackRepository
import com.bignerdranch.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTrackInteractorImpl(
    val repository: FavoriteTrackRepository
): FavoriteTrackInteractor {

    override suspend fun addToFavorite(track: Track) {
        repository.addTrack(track)
    }

    override suspend fun removeTrack(track: Track) = repository.removeTrack(track)

    override suspend fun isFavorite(trackId: Long): Boolean {
        return repository.isTrackFavorite(trackId)
    }

    override fun getAllTrack(): Flow<List<Track>> = repository.getAllTracks()

}