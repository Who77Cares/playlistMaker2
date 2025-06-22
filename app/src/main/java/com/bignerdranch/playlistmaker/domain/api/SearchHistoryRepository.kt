package com.bignerdranch.playlistmaker.domain.api

import com.bignerdranch.playlistmaker.Resource
import com.bignerdranch.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun saveToHistory(t: Track)
    fun getHistory(): Resource<List<Track>>
    fun clearHistory()
}