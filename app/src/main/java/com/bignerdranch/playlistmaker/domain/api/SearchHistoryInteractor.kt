package com.bignerdranch.playlistmaker.domain.api

import com.bignerdranch.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {

    fun getHistory(consumer: HistoryConsumer)
    fun saveToHistory(t: Track)
    fun clearHistory()

    interface HistoryConsumer {

        fun consume(searchHistory: List<Track>?)

    }


}