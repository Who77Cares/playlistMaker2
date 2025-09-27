package com.bignerdranch.playlistmaker.media.db_favorite.domain

import com.bignerdranch.playlistmaker.media.db_favorite.data.db.TrackEntity
import com.bignerdranch.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {
    suspend fun addToFavorite(track: Track)
    suspend fun removeTrack(track: Track)

    suspend fun isFavorite(trackId: Long): Boolean

    fun getAllTrack(): Flow<List<Track>>
}