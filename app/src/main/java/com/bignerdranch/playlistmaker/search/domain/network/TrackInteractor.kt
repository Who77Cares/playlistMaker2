package com.bignerdranch.playlistmaker.search.domain.network

import kotlinx.coroutines.flow.Flow

interface TrackInteractor {

    fun searchTrack(expression: String) : Flow<Pair<List<Track>?, String?>>

}