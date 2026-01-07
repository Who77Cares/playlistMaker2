package com.bignerdranch.playlistmaker.media.db_favorite.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.playlistmaker.media.db_favorite.domain.FavoriteTrackInteractor
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTrackInteractor: FavoriteTrackInteractor
): ViewModel() {

    private val stateLiveData = MutableLiveData<FavoriteTrackState>()
    fun observeState(): LiveData<FavoriteTrackState> = stateLiveData

    init {
        // Автоматически загружаем при создании ViewModel
        fillData()
    }

    fun fillData() {
        viewModelScope.launch {
            favoriteTrackInteractor
                .getAllTrack()
                .collect { tracks ->
                    stateLiveData.postValue(
                        if (tracks.isEmpty()) {
                            FavoriteTrackState.Empty("empty")
                        } else {
                            FavoriteTrackState.Content(tracks)
                        }
                    )
                }
        }
    }

}