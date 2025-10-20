package com.bignerdranch.playlistmaker.media.db_favorite.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.playlistmaker.media.db_favorite.domain.FavoriteTrackInteractor
import com.bignerdranch.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class FavoriteMediaViewModel(
    private val favoriteTrackInteractor: FavoriteTrackInteractor
): ViewModel() {

    private val stateLiveData = MutableLiveData<MediaState>()
    fun observeState(): LiveData<MediaState> = stateLiveData


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
            renderState(MediaState.Empty(message = "!!!!!!"))
        } else {
            renderState(MediaState.Content(tracks))
        }
    }

    private fun renderState(state: MediaState) {
        stateLiveData.postValue(state)
    }
}