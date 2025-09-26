package com.bignerdranch.playlistmaker.media.db_favorite.domain

import com.bignerdranch.playlistmaker.search.domain.models.Track

interface FavoriteInteractor {
    suspend fun addToFavorite(track: Track)
}