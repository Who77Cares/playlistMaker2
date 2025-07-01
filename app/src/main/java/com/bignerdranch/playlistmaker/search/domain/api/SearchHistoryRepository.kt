package com.bignerdranch.playlistmaker.search.domain.api

import com.bignerdranch.playlistmaker.Resource
import com.bignerdranch.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {

    fun getHistory(): Resource<List<Track>>
    fun saveToHistory(t: Track)
    fun clearHistory()
}

