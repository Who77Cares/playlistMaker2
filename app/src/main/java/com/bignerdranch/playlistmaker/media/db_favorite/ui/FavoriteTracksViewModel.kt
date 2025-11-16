package com.bignerdranch.playlistmaker.media.db_favorite.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.playlistmaker.media.db_favorite.ui.FavoriteTrackState
import com.bignerdranch.playlistmaker.media.db_favorite.domain.FavoriteTrackInteractor
import com.bignerdranch.playlistmaker.search.domain.network.Track
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTrackInteractor: FavoriteTrackInteractor
): ViewModel() {

    private val stateLiveData = MutableLiveData<FavoriteTrackState>()
    fun observeState(): LiveData<FavoriteTrackState> = stateLiveData


    fun fillData() {
        viewModelScope.launch {
            favoriteTrackInteractor
                .getAllTrack()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }


    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoriteTrackState.Empty(message = "!!!!!!"))
        } else {
            renderState(FavoriteTrackState.Content(tracks))
        }
    }

    private fun renderState(state: FavoriteTrackState) {
        stateLiveData.postValue(state)
    }
}