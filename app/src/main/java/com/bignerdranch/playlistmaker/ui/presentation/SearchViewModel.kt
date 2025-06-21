package com.bignerdranch.playlistmaker.ui.presentation

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import com.bignerdranch.playlistmaker.Creator
import com.bignerdranch.playlistmaker.TrackState
import com.bignerdranch.playlistmaker.TrackView
import com.bignerdranch.playlistmaker.domain.api.TrackInteractor
import com.bignerdranch.playlistmaker.domain.models.Track
import com.bignerdranch.playlistmaker.ui.searchTrack.SearchActivity
import com.bignerdranch.playlistmaker.ui.searchTrack.SearchActivity.Companion

class SearchViewModel(private val view: TrackView, private val context: Context) {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

    }

    private var handler = Handler(Looper.getMainLooper())

    private val tracks = ArrayList<Track>()
    private val historyTracks = ArrayList<Track>()
    private var lastSearchText: String = ""

    private val searchRunnable = Runnable { searchRequest(lastSearchText) }


    private val trackInteractor = Creator.provideTrackInteractor(context)

    // отложенный запрос в сеть через 2 сек после ввода текста в эдиттекст
    fun searchDebounce(newText: String) {
        lastSearchText = newText
        handler.removeCallbacks(searchRunnable)
        if (newText.isNotEmpty()) {
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        } else {
            // Очистить результаты, если пустой запрос
            tracks.clear()
            view.render(TrackState.Empty("Введите текст для поиска"))
        }
    }


    private fun searchRequest(newSearchText: String) {
        Log.d("SearchViewModel", "searchRequest called with: \"$newSearchText\"")
        if (newSearchText.isEmpty()) {
            Log.d("SearchViewModel", "searchRequest called with: \"$newSearchText\"")
            return

        }

        view.render( TrackState.Loading )

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
                                view.render(TrackState.Error("Опаньки"))
                            }
                            tracks.isEmpty() -> {
                                Log.d("SearchViewModel", "Rendering Empty state")
                                view.render(TrackState.Empty("Пусто"))
                            }
                            else -> {
                                Log.d("SearchViewModel", "Rendering Content state with ${tracks.size} tracks")
                                view.render(TrackState.Content(tracks))
                            }
                        }
                    }
                }

            }
        )

    }




}



