package com.bignerdranch.playlistmaker.search.domain.prefs_storage

import com.bignerdranch.playlistmaker.util.Resource
import com.bignerdranch.playlistmaker.search.domain.network.Track

interface SearchHistoryRepository {

    fun getHistory(): Resource<List<Track>>
    fun saveToHistory(t: Track)
    fun clearHistory()
}

