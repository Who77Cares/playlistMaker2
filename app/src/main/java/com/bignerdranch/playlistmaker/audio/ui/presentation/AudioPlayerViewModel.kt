package com.bignerdranch.playlistmaker.audio.ui.presentation

import android.media.MediaPlayer
import androidx.lifecycle.LiveData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.playlistmaker.util.TrackMapper
import com.bignerdranch.playlistmaker.audio.ui.models.PlayerState
import com.bignerdranch.playlistmaker.audio.ui.models.TrackAudioModel
import com.bignerdranch.playlistmaker.media.db_favorite.domain.FavoriteTrackInteractor
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistInteractor
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import com.bignerdranch.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class AudioPlayerViewModel(
    private val mapper: TrackMapper,
    private val mediaPlayer: MediaPlayer,
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {


    private var previewUrl: String = ""
    private var timerJob: Job? = null

    private val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val trackAudioModel = MutableLiveData<TrackAudioModel>()
    fun observeTrackUiModel(): LiveData<TrackAudioModel> = trackAudioModel

    private val _isFavoriteLiveData = MutableLiveData<Boolean>()
    val isFavoriteLiveData: LiveData<Boolean> = _isFavoriteLiveData

    private val playlistDataFromRoom = MutableLiveData<List<PlaylistModel>>()
    fun observePlaylistData(): LiveData<List<PlaylistModel>> = playlistDataFromRoom

    private val _addTrackState = MutableStateFlow<Boolean?>(null)
    val addTrackState: StateFlow<Boolean?> = _addTrackState.asStateFlow()



    override fun onCleared() {
        super.onCleared()
        resetTimer()
    }

    fun setTrack(track: Track) {

        val uiModel = mapper.mapToAudioModel(track)

        trackAudioModel.value = uiModel
        previewUrl = track.previewUrl

        checkIfFavorite(track.trackId.toLong())
        preparePlayer()
    }

    fun onPlayButtonClicked() {
        when(playerStateLiveData.value) {
            is PlayerState.Playing -> pausePlayer()
            is PlayerState.Prepared, is PlayerState.Paused -> startPlayer()
            else -> { }
        }
    }

    private fun preparePlayer() {
            mediaPlayer.setDataSource(previewUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playerStateLiveData.postValue(PlayerState.Prepared())
            }

            mediaPlayer.setOnCompletionListener {
                mediaPlayer.seekTo(0)
                playerStateLiveData.postValue(PlayerState.Prepared())
            }
    }



    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        playerStateLiveData.postValue(PlayerState.Paused(getCurrentPlayerPosition()))
    }


    private fun resetTimer() {
        mediaPlayer.stop()
        mediaPlayer.release()
        playerStateLiveData.value = PlayerState.Default()
    }

    // пауза при соврачивания приложения
    fun onPause() {
        pausePlayer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                playerStateLiveData.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
                val pos = mediaPlayer.currentPosition
                delay(1000L - (pos % 1000L)) // синхронизация на каждую секунду
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return timeFormatter.format(mediaPlayer.currentPosition.toLong())
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

