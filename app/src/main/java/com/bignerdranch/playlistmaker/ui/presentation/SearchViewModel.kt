package com.bignerdranch.playlistmaker.ui.presentation

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bignerdranch.playlistmaker.Creator
import com.bignerdranch.playlistmaker.TrackState
import com.bignerdranch.playlistmaker.domain.api.TrackInteractor
import com.bignerdranch.playlistmaker.domain.models.Track
import com.bignerdranch.playlistmaker.search.App
import com.bignerdranch.playlistmaker.search.SearchPreferences


const val SEARCH_LIST: String = "search_list"

class SearchViewModel(context: Context): ViewModel() {

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


    private var handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchRequest(lastSearchText) }

    private val tracks = ArrayList<Track>()
    private val historyTracks = ArrayList<Track>()
    private var lastSearchText: String = ""


    private val trackInteractor = Creator.provideTrackInteractor(context)

    private val searchPreferences = SearchPreferences()
    private val sharedPrefs = context.getSharedPreferences(SEARCH_LIST, Context.MODE_PRIVATE)



    // отложенный запрос в сеть через 2 сек после ввода текста в эдиттекст
    fun searchDebounce(newText: String) {
        handler.removeCallbacks(searchRunnable)

        if (newText.isEmpty()) {
            tracks.clear()
            renderState(TrackState.History(historyTracks))
            return
        }

        lastSearchText = newText
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }


    fun updateHistory() {

        if (historyTracks.isEmpty()) {
            return
        } else {
            renderState(TrackState.History(historyTracks))
        }

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
        historyTracks.clear()
        historyTracks.addAll(searchPreferences.read(sharedPrefs))
    }

    fun saveHistory() {
        searchPreferences.write(sharedPrefs, historyTracks)
    }

    fun addTrackToHistory(track: Track) {
        val existingTrack = historyTracks.find { it.trackId == track.trackId }
        if (existingTrack != null) {
            historyTracks.remove(existingTrack)
        } else if (historyTracks.size >= 10) {
            historyTracks.removeAt(historyTracks.size - 1)
        }
        historyTracks.add(0, track)
    }
}



