package com.bignerdranch.playlistmaker.search.domain.api

import com.bignerdranch.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {

    fun getHistory(): List<Track>?
    fun saveToHistory(t: Track)
    fun clearHistory()

}

