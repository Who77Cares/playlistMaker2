package com.bignerdranch.playlistmaker.audio.ui.presentation

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bignerdranch.playlistmaker.audio.ui.TrackAudioMapper
import com.bignerdranch.playlistmaker.audio.ui.models.TrackAudioModel
import com.bignerdranch.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(private val mapper: TrackAudioMapper): ViewModel() {

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3

        fun getFactory(mapper: TrackAudioMapper): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AudioPlayerViewModel(mapper)
            }
        }
    }

    private var previewUrl: String = ""


    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData("00:00")
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    private val trackAudioModel = MutableLiveData<TrackAudioModel>()
    fun observeTrackUiModel(): LiveData<TrackAudioModel> = trackAudioModel

    private var timer = "00:00"

    private val mediaPlayer = MediaPlayer()

    private val handler = Handler(Looper.getMainLooper())

    private val timerRunnable = Runnable {
        if(playerStateLiveData.value == STATE_PLAYING) {
            startTimerUpdate()
        }
    }


    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        resetTimer()
    }

    fun setTrack(track: Track) {
        val uiModel = mapper.map(track)
        trackAudioModel.value = uiModel
        previewUrl = track.previewUrl
        preparePlayer()
    }

    fun onPlayButtonClicked() {
        when(playerStateLiveData.value) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun preparePlayer() {
            mediaPlayer.setDataSource(previewUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playerStateLiveData.postValue(STATE_PREPARED)
            }

            mediaPlayer.setOnCompletionListener {
                playerStateLiveData.postValue(STATE_PREPARED)
                resetTimer()
            }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(STATE_PLAYING)
        startTimerUpdate()
    }

    private fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        playerStateLiveData.postValue(STATE_PAUSED)
    }

    private fun startTimerUpdate() {
        progressTimeLiveData.postValue(
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        )

        handler.postDelayed(timerRunnable, 200)
    }


    private fun resetTimer() {
        handler.removeCallbacks(timerRunnable)
        timer = "00:00"
        progressTimeLiveData.postValue(timer)
    }

    private fun pauseTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    // пауза при соврачивания приложения
    fun onPause() {
        pausePlayer()
    }

}