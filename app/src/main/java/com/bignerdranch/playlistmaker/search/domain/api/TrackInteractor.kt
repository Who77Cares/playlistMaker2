package com.bignerdranch.playlistmaker.search.domain.api

import com.bignerdranch.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {

    fun searchTrack(expression: String) : Flow<Pair<List<Track>?, String?>>

}