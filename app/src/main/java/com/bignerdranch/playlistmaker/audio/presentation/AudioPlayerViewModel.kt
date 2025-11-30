package com.bignerdranch.playlistmaker.audio.presentation


import androidx.lifecycle.LiveData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.playlistmaker.util.TrackMapper
import com.bignerdranch.playlistmaker.audio.models.PlayerState
import com.bignerdranch.playlistmaker.audio.models.TrackAudioModel
import com.bignerdranch.playlistmaker.audio.service.AudioService
import com.bignerdranch.playlistmaker.audio.service.AudioServiceControl
import com.bignerdranch.playlistmaker.media.db_favorite.domain.FavoriteTrackInteractor
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistInteractor
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import com.bignerdranch.playlistmaker.search.domain.network.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val mapper: TrackMapper,
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var audioServiceControl: AudioServiceControl? = null

    private var playerStateJob: Job? = null


    // состояние плеера
    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerState


    // данные для трека
    private val trackAudioModel = MutableLiveData<TrackAudioModel>()
    fun observeTrackUiModel(): LiveData<TrackAudioModel> = trackAudioModel



    private val _isFavoriteLiveData = MutableLiveData<Boolean>()
    val isFavoriteLiveData: LiveData<Boolean> = _isFavoriteLiveData



    private val playlistDataFromRoom = MutableLiveData<List<PlaylistModel>>()
    fun observePlaylistData(): LiveData<List<PlaylistModel>> = playlistDataFromRoom

    private val _addTrackState = MutableStateFlow<Boolean?>(null)
    val addTrackState: StateFlow<Boolean?> = _addTrackState.asStateFlow()


    private var currentTrack: Track? = null

    fun setAppInBackground(isBackground: Boolean) {
        (audioServiceControl as? AudioService)?.setAppInBackground(isBackground)
    }

    fun setTrack(track: Track) {
        // заглушка для убирания краша когда возвращаемся из экрана нового плейлиста
        if (currentTrack?.trackId == track.trackId) {
            return
        }
        currentTrack = track

        trackAudioModel.value = mapper.mapToAudioModel(track)

        checkIfFavorite(track.trackId.toLong())
    }


    fun setAudioPlayerControl(audioServiceControl: AudioServiceControl) {
        this.audioServiceControl = audioServiceControl

        // Отменяем предыдущую корутину, если она была
        playerStateJob?.cancel()

        // Запускаем новую корутину и сохраняем её Job
        playerStateJob = viewModelScope.launch {
            audioServiceControl.playerState.collect { state ->
                playerState.postValue(state)
            }
        }
    }

    fun onPlayerButtonClicked() {

        if (playerState.value is PlayerState.Playing) {
            audioServiceControl?.pausePlayer()
        } else {
            audioServiceControl?.startPlayer()
        }
    }

    fun removeAudioPlayerControl() {
        audioServiceControl = null
        playerStateJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        audioServiceControl = null
        playerStateJob?.cancel()
    }




    // проверка да добавленность в избранное чтобы сразу отобразить нужную иконку
    fun checkIfFavorite(trackId: Long) {
        viewModelScope.launch {
            val isFav = favoriteTrackInteractor.isFavorite(trackId)
            _isFavoriteLiveData.postValue(isFav)
        }
    }

    // логика добавления в избранное
    fun toggleFavorite(track: Track) {
        viewModelScope.launch {
            val isFav = favoriteTrackInteractor.isFavorite(track.trackId.toLong())
            if (isFav) {
                favoriteTrackInteractor.removeTrack(track)
            } else {
                favoriteTrackInteractor.addToFavorite(track)
            }
            // обновляем флажок LiveData
            _isFavoriteLiveData.postValue(!isFav)
        }
    }

    fun getPlaylistDataFromRoom() {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect { data ->
                    playlistDataFromRoom.value = data

                }
        }
    }

    // методы для работы с сохранением трека в плейлист
    fun addTrackToPlaylist(track: Track, playlist: PlaylistModel) {
        viewModelScope.launch {
            _addTrackState.value = playlistInteractor.addTrackToPlaylist(track, playlist.id)
        }
    }

    fun clearAddTrackState() {
        _addTrackState.value = null
    }

}