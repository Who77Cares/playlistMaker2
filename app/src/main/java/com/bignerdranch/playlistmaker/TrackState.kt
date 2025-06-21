package com.bignerdranch.playlistmaker

import com.bignerdranch.playlistmaker.domain.models.Track

sealed interface TrackState {

    data object Loading: TrackState

    data class Content(
        val tracks: List<Track>
    ): TrackState

    data class Error(
        val errorMessage: String
    ): TrackState

    data class Empty(
        val message: String
    ): TrackState
}