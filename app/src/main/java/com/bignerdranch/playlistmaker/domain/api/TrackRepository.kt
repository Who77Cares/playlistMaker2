package com.bignerdranch.playlistmaker.domain.api

import com.bignerdranch.playlistmaker.Resource
import com.bignerdranch.playlistmaker.domain.models.Track

interface TrackRepository {

    fun searchTrack(expression: String): Resource<List<Track>>
}