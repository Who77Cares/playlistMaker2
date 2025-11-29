package com.bignerdranch.playlistmaker.media.playlist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistInteractor
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import com.bignerdranch.playlistmaker.media.playlists.PlaylistViewModel.PlaylistState
import com.bignerdranch.playlistmaker.search.domain.network.Track
import com.bignerdranch.playlistmaker.settings.domain.api.SharingInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class SinglePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor,
    private val context: Context
) : ViewModel() {

    data class SinglePlaylistUiState(
        val tracks: List<Track> = emptyList(),
        val totalTracksTime: Int = 0
    )


    private val _uiState = MutableStateFlow(SinglePlaylistUiState())
    val uiState: StateFlow<SinglePlaylistUiState> = _uiState.asStateFlow()


    private val _playlistData = MutableStateFlow<PlaylistModel?>(null)
    val playlistData: StateFlow<PlaylistModel?> = _playlistData.asStateFlow()

    private val _deleteSuccess = MutableLiveData<Boolean?>()
    val deleteSuccess: LiveData<Boolean?> = _deleteSuccess

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

    fun getPlaylistDataById(playlistId: Long) {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylistById(playlistId)
                .collect { data ->
                    _playlistData.value = data
                }
        }
    }



    private fun calculateTotalMinutes(tracks: List<Track>): Int {
        val totalMillis = tracks.sumOf { it.trackTimeMillis.toLong() }
        return (totalMillis / 1000.0 / 60.0).roundToInt()
    }

    fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(playlistId, trackId)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            try {
                playlistInteractor.deletePlaylist(playlistId)
                _deleteSuccess.value = true
            } catch (e: Exception) {
                _deleteSuccess.value = false
            }
        }
    }


    fun sharePlaylist(playlistName: String, playlistDescription: String) {
        val tracks = uiState.value.tracks
        val tracksNumber =
            context.resources.getQuantityString(R.plurals.tracks_count, tracks.size, tracks.size)

        val message = buildString {
            append("${playlistName}\n")
            append("${playlistDescription}\n")
            append("$tracksNumber\n\n")

            tracks.forEachIndexed { index, track ->
                append("${index + 1}. ${track.artistName} - ${track.trackName}\n")
            }
        }

        sharingInteractor.sharePlaylistText(message)

    }


}

