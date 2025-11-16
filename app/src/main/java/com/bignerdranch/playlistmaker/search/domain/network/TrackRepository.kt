package com.bignerdranch.playlistmaker.search.domain.network

import com.bignerdranch.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface TrackRepository {

    fun searchTrack(expression: String): Flow<Resource<List<Track>>>
}