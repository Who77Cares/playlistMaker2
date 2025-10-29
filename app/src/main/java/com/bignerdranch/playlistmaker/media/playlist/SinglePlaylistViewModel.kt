package com.bignerdranch.playlistmaker.media.playlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistInteractor
import com.bignerdranch.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SinglePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    data class SinglePlaylistUiState(
        val tracks: List<Track> = emptyList(),
        val totalTracksTime: Int = 0
    )


    private val _uiState = MutableStateFlow(SinglePlaylistUiState())
    val uiState: StateFlow<SinglePlaylistUiState> = _uiState.asStateFlow()

    fun getTracksDataFromRoom(playlistId: Long) {
        viewModelScope.launch {
            playlistInteractor.getTracksForPlaylist(playlistId)
                .collect { tracks ->

                    val totalMinutes = calculateTotalMinutes(tracks)

                    _uiState.update { currentState ->
                        currentState.copy(
                            tracks = tracks,
                            totalTracksTime = totalMinutes
                        )
                    }
                }
        }
    }

    private fun calculateTotalMinutes(tracks: List<Track>): Int {
        return tracks.sumOf { it.trackTimeMillis / 1000 / 60 }
    }

}