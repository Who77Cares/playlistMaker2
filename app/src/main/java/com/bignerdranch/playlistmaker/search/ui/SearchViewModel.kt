package com.bignerdranch.playlistmaker.search.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.playlistmaker.search.domain.prefs_storage.SearchHistoryInteractor
import com.bignerdranch.playlistmaker.search.domain.network.TrackInteractor
import com.bignerdranch.playlistmaker.search.domain.network.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val historyTrackInteractor: SearchHistoryInteractor
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var searchJob: Job? = null
    private var lastSearchText: String = ""

    private val stateLiveData = MutableLiveData<TrackState>()
    val state: LiveData<TrackState> = stateLiveData

    private var isClickAllowed = true

    //    fun observerState(): LiveData<TrackState> = stateLiveData
    private var lastSearchQuery: String? = null


    // метод для получения текущего состояния
    fun getCurrentState(): TrackState? = stateLiveData.value


    // Метод для повторного поиска
    fun retryLastSearch() {
        lastSearchQuery?.let { query ->
            searchRequest(query)
        }
    }

    // отложенный запрос в сеть через 2 сек после ввода текста в эдиттекст
    fun searchDebounce(newText: String, forceUpdate: Boolean = false) {

        searchJob?.cancel()

        if (newText.isEmpty()) {
            loadHistory()
            lastSearchQuery = null
            return
        }

        // костыли чтобы не происходил повторный запрос после возврата из AdioPlayerFragment
        // + запрос при нажатии кнопки "обновить"
        if (lastSearchText != newText || forceUpdate) {

            lastSearchText = newText
            lastSearchQuery = newText

            searchJob = viewModelScope.launch {
                delay(SEARCH_DEBOUNCE_DELAY)
                searchRequest(newText)
            }
        }
    }


    private fun searchRequest(searchText: String) {
        if (searchText.isEmpty()) return

        // Сохраняем запрос для возможного retry
        lastSearchQuery = searchText

        stateLiveData.value = TrackState.Loading

        viewModelScope.launch {
            trackInteractor
                .searchTrack(searchText)
                .collect { pair ->
                    processResult(pair.first, pair.second)
                }
        }
    }


    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = foundTracks ?: emptyList()

        when (errorMessage) {
            "Проверьте подключение к интернету",
            "Ошибка сервера" -> {
                stateLiveData.value = TrackState.Error(errorMessage!!)
            }
            "Ничего не найдено" -> {
                stateLiveData.value = TrackState.Empty(errorMessage!!)
            }
            else -> {
                stateLiveData.value = TrackState.Content(tracks)
            }
        }
    }


    fun loadHistory() {
        historyTrackInteractor.getHistory(
            object : SearchHistoryInteractor.HistoryConsumer {
                override fun consume(searchHistory: List<Track>?) {
                    stateLiveData.value = TrackState.History(searchHistory ?: emptyList())
                }

            }
        )
    }

    fun addTrackToHistory(track: Track) {
        historyTrackInteractor.saveToHistory(track)
    }

    fun clearHistory() {
        historyTrackInteractor.clearHistory()
        loadHistory()
    }

    // Click debounce для Compose
    suspend fun clickDebounce(): Boolean {
        return if (isClickAllowed) {
            isClickAllowed = false
            delay(CLICK_DEBOUNCE_DELAY)
            isClickAllowed = true
            true
        } else {
            false
        }
    }

}