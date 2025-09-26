package com.bignerdranch.playlistmaker.search.ui.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.playlistmaker.search.ui.models.TrackState
import com.bignerdranch.playlistmaker.search.domain.api.TrackInteractor
import com.bignerdranch.playlistmaker.search.domain.models.Track
import com.bignerdranch.playlistmaker.search.domain.api.SearchHistoryInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val historyTrackInteractor: SearchHistoryInteractor
) : ViewModel() {


    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var searchJob: Job? = null

    private val stateLiveData = MutableLiveData<TrackState>()
    fun observerState(): LiveData<TrackState> = stateLiveData


    private val tracks = ArrayList<Track>()
    private var lastSearchText: String = ""


    // отложенный запрос в сеть через 2 сек после ввода текста в эдиттекст
    fun searchDebounce(newText: String, forceUpdate: Boolean = false) {

        searchJob?.cancel()

        if (newText.isEmpty()) {
            loadHistory()
            return
        }

        if (lastSearchText != newText || forceUpdate) {    // костыли чтобы не происходил повторный запрос после возврата из AdioPlayerFragment + запрос при нажатии кнопки "обновить"

            lastSearchText = newText
            searchJob = viewModelScope.launch {
                delay(SEARCH_DEBOUNCE_DELAY)
                searchRequest(newText)
            }
        }
    }


    private fun searchRequest(newSearchText: String) {
        Log.d("SearchViewModel", "searchRequest called with: \"$newSearchText\"")
        if (newSearchText.isEmpty()) {

            return

        }

        renderState( TrackState.Loading )

       viewModelScope.launch {
           trackInteractor
               .searchTrack(newSearchText)
               .collect { pair ->
                   processResult(pair.first, pair.second)
               }
       }

    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = mutableListOf<Track>()


        if(foundTracks != null) {
            tracks.addAll(foundTracks)

        }
        when (errorMessage) {
            "Проверьте подключение к интернету" -> {
                renderState(TrackState.Error(errorMessage))
            }

            "Ошибка сервера" -> {
                renderState(TrackState.Error(errorMessage))
            }

            "Ничего не найдено" -> {
                renderState(TrackState.Empty(errorMessage))
            }
            else -> {
                renderState(TrackState.Content(tracks))
            }
        }

    }

    fun renderState(state: TrackState) {
        stateLiveData.setValue(state)
    }

    fun loadHistory() {
        historyTrackInteractor.getHistory(
            object : SearchHistoryInteractor.HistoryConsumer {
                override fun consume(searchHistory: List<Track>?) {
                    tracks.clear()
                    tracks.addAll(searchHistory ?: emptyList())
                    renderState(TrackState.History(tracks))
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

}