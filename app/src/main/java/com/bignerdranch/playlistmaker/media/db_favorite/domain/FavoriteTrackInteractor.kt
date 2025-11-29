package com.bignerdranch.playlistmaker.media.db_favorite.domain

import com.bignerdranch.playlistmaker.search.domain.network.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTrackInteractor {
    suspend fun addToFavorite(track: Track)
    suspend fun removeTrack(track: Track)

    suspend fun isFavorite(trackId: Long): Boolean

    fun getAllTrack(): Flow<List<Track>>
}