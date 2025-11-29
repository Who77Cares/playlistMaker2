package com.bignerdranch.playlistmaker.search.domain.prefs_storage

import com.bignerdranch.playlistmaker.search.domain.network.Track

interface SearchHistoryInteractor {

    fun getHistory(consumer: HistoryConsumer)
    fun saveToHistory(t: Track)
    fun clearHistory()

    interface HistoryConsumer {
        fun consume(searchHistory: List<Track>?)
    }

}

