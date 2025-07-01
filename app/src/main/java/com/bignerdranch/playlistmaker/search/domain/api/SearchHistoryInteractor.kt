package com.bignerdranch.playlistmaker.search.domain.api

import com.bignerdranch.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {

    fun getHistory(consumer: HistoryConsumer)
    fun saveToHistory(t: Track)
    fun clearHistory()

    interface HistoryConsumer {
        fun consume(searchHistory: List<Track>?)
    }

}

