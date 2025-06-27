package com.bignerdranch.playlistmaker.ui.presentation

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bignerdranch.playlistmaker.Creator
import com.bignerdranch.playlistmaker.domain.api.SearchHistoryInteractor
import com.bignerdranch.playlistmaker.ui.models.TrackState
import com.bignerdranch.playlistmaker.domain.api.TrackInteractor
import com.bignerdranch.playlistmaker.domain.models.Track
import com.bignerdranch.playlistmaker.App

class SearchViewModel(context: Context): ViewModel() {

    private val trackInteractor = Creator.provideTrackInteractor(context)
    private val historyTrackInteractor = Creator.provideSearchHistoryInteractor(context)

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                SearchViewModel(app)
            }
        }
    }

    private val stateLiveData = MutableLiveData<TrackState>()
    fun observerState(): LiveData<TrackState> = stateLiveData


    private val tracks = ArrayList<Track>()
    private var lastSearchText: String = ""

    private var handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchRequest(lastSearchText) }


    // отложенный запрос в сеть через 2 сек после ввода текста в эдиттекст
    fun searchDebounce(newText: String) {
        handler.removeCallbacks(searchRunnable)

        if (newText.isEmpty()) {
            loadHistory()
            return
        }

        lastSearchText = newText
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }


    private fun searchRequest(newSearchText: String) {
        Log.d("SearchViewModel", "searchRequest called with: \"$newSearchText\"")
        if (newSearchText.isEmpty()) {
            Log.d("SearchViewModel", "searchRequest called with: \"$newSearchText\"")
            return

        }

        renderState( TrackState.Loading )

        trackInteractor.searchTracks(
            newSearchText, object : TrackInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    handler.post {

                        tracks.clear()
                        Log.d("SearchViewModel", "consume called with foundTracks size = ${foundTracks?.size}, errorMessage = $errorMessage")

                        if (foundTracks != null) {
                            tracks.addAll(foundTracks)
                        }

                        when {
                            errorMessage == "Проверьте подключение к интернету" -> {
                                Log.d("SearchViewModel", "Rendering Error state")
                                renderState(TrackState.Error("Опаньки"))
                            }
                            tracks.isEmpty() -> {
                                Log.d("SearchViewModel", "Rendering Empty state")
                                renderState(TrackState.Empty("Пусто"))
                            }
                            else -> {
                                Log.d("SearchViewModel", "Rendering Content state with ${tracks.size} tracks")
                                renderState(TrackState.Content(tracks))
                            }
                        }
                    }
                }

            }
        )

    }

    fun renderState(state: TrackState) {
        stateLiveData.postValue(state)
    }

    fun loadHistory() {
      historyTrackInteractor.getHistory(
          object : SearchHistoryInteractor.HistoryConsumer {
              override fun consume(searchHistory: List<Track>?) {
                  renderState(TrackState.History(searchHistory ?: emptyList() ))
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



