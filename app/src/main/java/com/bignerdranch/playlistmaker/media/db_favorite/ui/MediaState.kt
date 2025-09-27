package com.bignerdranch.playlistmaker.media.db_favorite.ui

import com.bignerdranch.playlistmaker.search.domain.models.Track
import com.bignerdranch.playlistmaker.search.ui.models.TrackState

sealed interface MediaState {

    data class Content(
        val tracks: List<Track>
    ): MediaState

    data class Empty(
        val message: String
    ): MediaState

}