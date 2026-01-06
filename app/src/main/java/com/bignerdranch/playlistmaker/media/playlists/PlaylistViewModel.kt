package com.bignerdranch.playlistmaker.media.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistInteractor
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    sealed interface PlaylistState {
        data object Empty : PlaylistState
        data class Content(val playlists: List<PlaylistModel>): PlaylistState
    }


    init {
        getData()
    }


    private val stateLiveData = MutableLiveData<PlaylistState>()
    fun observeStateLiveData(): LiveData<PlaylistState> = stateLiveData


    fun getData() {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect { playlists ->
                    if (playlists.isEmpty()) {
                        renderState(PlaylistState.Empty)
                    } else {
                        renderState(PlaylistState.Content(playlists))
                    }
                }
        }
    }


    private fun renderState(state: PlaylistState) {
        stateLiveData.postValue(state)
    }


}