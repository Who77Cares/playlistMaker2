package com.bignerdranch.playlistmaker.media.db_favorite.data

import android.media.metrics.TrackChangeEvent
import com.bignerdranch.playlistmaker.media.db_favorite.data.db.TrackEntity
import com.bignerdranch.playlistmaker.search.domain.models.Track

interface FavoriteRepository {

    suspend fun addTrack(track: Track)

    suspend fun removeTrack(track: Track)

    suspend fun isTrackFavorite(trackId: Long): Boolean

}