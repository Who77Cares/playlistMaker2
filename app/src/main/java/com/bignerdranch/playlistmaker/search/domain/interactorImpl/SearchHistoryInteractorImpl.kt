package com.bignerdranch.playlistmaker.search.domain.interactorImpl

import com.bignerdranch.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.bignerdranch.playlistmaker.search.domain.api.SearchHistoryRepository
import com.bignerdranch.playlistmaker.search.domain.models.Track

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
): SearchHistoryInteractor {

    override fun getHistory(consumer: SearchHistoryInteractor.HistoryConsumer) {
        consumer.consume(repository.getHistory().data)
    }

    override fun saveToHistory(t: Track) {
        repository.saveToHistory(t)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}