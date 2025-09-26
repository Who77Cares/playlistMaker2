package com.bignerdranch.playlistmaker.media.db_favorite.domain

import com.bignerdranch.playlistmaker.media.db_favorite.data.FavoriteRepository
import com.bignerdranch.playlistmaker.search.domain.models.Track

class FavoriteInteractorImpl(
    val repository: FavoriteRepository
): FavoriteInteractor {

    override suspend fun addToFavorite(track: Track) {
        repository.addTrack(track)
    }
}