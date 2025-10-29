package com.bignerdranch.playlistmaker.search.domain.api

import com.bignerdranch.playlistmaker.util.Resource
import com.bignerdranch.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {

    fun searchTrack(expression: String): Flow<Resource<List<Track>>>
}