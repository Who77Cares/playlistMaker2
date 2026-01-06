package com.bignerdranch.playlistmaker.audio.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.audio.models.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioService: Service(), AudioServiceControl {

    inner class AudioServiceBinder : Binder() {
        fun getService(): AudioService = this@AudioService
    }

    private companion object {
        const val SERVICE_NOTIFICATION_ID = 100
        const val NOTIFICATION_CHANNEL_ID = "music_service_channel"

        const val SONG_URL = "song_url"
        const val SONG_INFO = "song_info"

    }

    private val binder = AudioServiceBinder()


    // Переменная для хранения MediaPlayer
    private var mediaPlayer: MediaPlayer? = null

    private var songUrl = ""
    private var songInfo = ""

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Default())
    override val playerState = _playerState.asStateFlow()

    private var timerJob: Job? = null
    private var isForeground = false
    private var shouldShowNotification = false
    private var isAppInBackground = false

    override fun onBind(intent: Intent?): IBinder? {
        songUrl = intent?.getStringExtra(SONG_URL) ?: ""
        songInfo = intent?.getStringExtra(SONG_INFO) ?: ""

        initMediaPlayer()
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        releasePlayer()
        return super.onUnbind(intent)
    }

    private fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentText(songInfo)
            .setSmallIcon(R.drawable.media)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setSilent(true)
            .build()
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        createNotificationChannel()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    override fun startPlayer() {
        mediaPlayer?.start()
        _playerState.value = PlayerState.Playing(getCurrentPlayerPosition())
        startTimer()

        shouldShowNotification = true
        updateNotificationState()
    }

    override fun pausePlayer() {
        mediaPlayer?.pause()
        timerJob?.cancel()
        _playerState.value = PlayerState.Paused(getCurrentPlayerPosition())

        shouldShowNotification = false
        updateNotificationState()
    }

    private fun initMediaPlayer() {
        if (songUrl.isEmpty()) return

        mediaPlayer?.setDataSource(songUrl)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            _playerState.value = PlayerState.Prepared()
        }
        mediaPlayer?.setOnCompletionListener {
            timerJob?.cancel()
            mediaPlayer?.seekTo(0)
            _playerState.value = PlayerState.Prepared()
            shouldShowNotification = false
            updateNotificationState()
        }
    }

    // Освобождаем все ресурсы, выделенные для плеера
    private fun releasePlayer() {
        timerJob?.cancel()
        mediaPlayer?.stop()
        _playerState.value = PlayerState.Default()
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.release()
        mediaPlayer = null
        shouldShowNotification = false
        updateNotificationState()
    }

    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(200L)
                _playerState.value = PlayerState.Playing(getCurrentPlayerPosition())
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer?.currentPosition ?: 0)
    }

    private fun updateNotificationState() {
        if (shouldShowNotification && isAppInBackground) {
            startForegroundService()
        } else {
            stopForegroundService()
        }
    }

    private fun startForegroundService() {
        if (!isForeground) {
            ServiceCompat.startForeground(
                this,
                SERVICE_NOTIFICATION_ID,
                createServiceNotification(),
                getForegroundServiceTypeConstant()
            )
            isForeground = true
        }
    }

    private fun stopForegroundService() {
        if (isForeground) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            isForeground = false
        }
    }

    // Публичный метод для управления состоянием из Fragment
    fun setAppInBackground(isBackground: Boolean) {
        isAppInBackground = isBackground
        updateNotificationState()
    }

    // исправлен краш при первом запуске
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Music Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music playback service"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}



