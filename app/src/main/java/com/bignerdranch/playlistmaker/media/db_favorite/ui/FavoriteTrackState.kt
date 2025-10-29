package com.bignerdranch.playlistmaker.media.db_favorite.ui

import com.bignerdranch.playlistmaker.search.domain.models.Track

sealed interface FavoriteTrackState {

    data class Content(
        val tracks: List<Track>
    ): FavoriteTrackState

    data class Empty(
        val message: String
    ): FavoriteTrackState

}