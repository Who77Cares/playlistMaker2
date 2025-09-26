package com.bignerdranch.playlistmaker.media.db_favorite.data

import com.bignerdranch.playlistmaker.search.domain.models.Track

interface FavoriteRepository {

    suspend fun addTrack(track: Track)
    suspend fun removeTrack(track: Track)

}