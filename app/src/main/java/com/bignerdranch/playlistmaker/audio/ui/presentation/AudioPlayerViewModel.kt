package com.bignerdranch.playlistmaker.audio.ui.presentation

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.playlistmaker.TrackMapper
import com.bignerdranch.playlistmaker.audio.ui.models.PlayerState
import com.bignerdranch.playlistmaker.audio.ui.models.TrackAudioModel
import com.bignerdranch.playlistmaker.media.db_favorite.domain.FavoriteInteractor
import com.bignerdranch.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class AudioPlayerViewModel(
    private val mapper: TrackMapper,
    private val mediaPlayer: MediaPlayer,
    private val favoriteInteractor: FavoriteInteractor
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


    override fun onCleared() {
        super.onCleared()
        resetTimer()
    }

    fun setTrack(track: Track) {

        val uiModel = mapper.mapToAudioModel(track)

        trackAudioModel.value = uiModel
        previewUrl = track.previewUrl
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


    fun addTrackToFavorite(track: Track) {
        viewModelScope.launch {
            favoriteInteractor.addToFavorite(track)
        }
    }

}