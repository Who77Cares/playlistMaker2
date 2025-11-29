package com.bignerdranch.playlistmaker.media.db_favorite.data

import com.bignerdranch.playlistmaker.search.domain.network.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTrackRepository {

    suspend fun addTrack(track: Track)

    suspend fun removeTrack(track: Track)

    suspend fun isTrackFavorite(trackId: Long): Boolean

    fun getAllTracks(): Flow<List<Track>>

}