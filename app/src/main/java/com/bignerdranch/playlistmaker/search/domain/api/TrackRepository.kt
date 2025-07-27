package com.bignerdranch.playlistmaker.search.domain.api

import com.bignerdranch.playlistmaker.Resource
import com.bignerdranch.playlistmaker.search.domain.models.Track

interface TrackRepository {

    fun searchTrack(expression: String): Resource<List<Track>>
}