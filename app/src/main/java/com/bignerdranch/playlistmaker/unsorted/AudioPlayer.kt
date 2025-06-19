package com.bignerdranch.playlistmaker.unsorted

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.search.SearchActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale



class AudioPlayer : AppCompatActivity() {

    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var durationTime: TextView
    private lateinit var album: TextView
    private lateinit var songYear: TextView
    private lateinit var songStyle: TextView
    private lateinit var songCountry: TextView
    private lateinit var songCover: ImageView

    private lateinit var playPauseButton: ImageButton
    private lateinit var addToLikeButton: ImageButton
    private lateinit var arrowBackButton: ImageView

    private lateinit var durationInRealTime: TextView


    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private lateinit var previewUrl: String
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())

    private val updateRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                val currentPosition = mediaPlayer.currentPosition
                val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                durationInRealTime.text = formattedTime
                handler.postDelayed(this, 300)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)



        // значения для интентов
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        durationTime = findViewById(R.id.durationTime)
        album = findViewById(R.id.album)
        songYear = findViewById(R.id.sondYear)
        songStyle = findViewById(R.id.songStyle)
        songCountry = findViewById(R.id.songCountry)
        songCover = findViewById(R.id.songCover)

        //другие вью
        playPauseButton = findViewById(R.id.PlayOrStop)
        addToLikeButton = findViewById(R.id.addToLike)
        arrowBackButton = findViewById(R.id.arrow_back)

        durationInRealTime = findViewById(R.id.durationInRealTime)


        // Установка интентов
        val intent: Intent = intent

        val songYearIntent = intent.getStringExtra("songYear") ?: return
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(songYearIntent)
        val formattedYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(date!!)

        val durationTimeIntent = intent.getIntExtra("durationTime", 0)
        val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(durationTimeIntent)

        val songCoverIntent = intent.getStringExtra("songCover")

        previewUrl = intent.getStringExtra("previewUrl") ?: ""


        trackName.text = intent.getStringExtra("trackName")
        artistName.text = intent.getStringExtra("artistName")
        album.text = intent.getStringExtra("album")

        durationTime.text = formattedTime
        songYear.text = formattedYear

        songStyle.text = intent.getStringExtra("songStyle")
        songCountry.text = intent.getStringExtra("songCountry")

        Glide.with(applicationContext)
            .load(songCoverIntent)
            .fitCenter()
            .transform(RoundedCorners(20))
            .placeholder(R.drawable.placeholder_search)
            .into(songCover)

        preparePlayer()

        arrowBackButton .setOnClickListener {

            val _intent = Intent(this@AudioPlayer, SearchActivity::class.java)
            startActivity(_intent)
        }


//        playPauseButton.setOnClickListener {
//            val currentDrawable = playPauseButton.drawable
//            val pauseDrawable = ContextCompat.getDrawable(this, R.drawable.pause_icon)
//
//            // Сравниваем состояния drawable, а не сами объекты
//            if (currentDrawable.constantState == pauseDrawable?.constantState) {
//                playPauseButton.setImageResource(R.drawable.play_arrow_icon)
//            } else {
//                playPauseButton.setImageResource(R.drawable.pause_icon)
//            }
//        }

        addToLikeButton.setOnClickListener {
            val currentDrawable = addToLikeButton.drawable
            val likeDrawable = ContextCompat.getDrawable(this, R.drawable.empty_like)

            // Сравниваем состояния drawable, а не сами объекты
            if (currentDrawable.constantState == likeDrawable?.constantState) {
                addToLikeButton.setImageResource(R.drawable.is_liked)
            } else {
                addToLikeButton.setImageResource(R.drawable.empty_like)
            }
        }

        playPauseButton .setOnClickListener {
            playbackControl()
        }

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
        mediaPlayer.release()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playPauseButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playPauseButton.setImageResource(R.drawable.play_arrow_icon)
            playerState = STATE_PREPARED
            handler.removeCallbacks(updateRunnable)
            durationInRealTime.text = "00:00"
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playPauseButton.setImageResource(R.drawable.pause_icon)
        playerState = STATE_PLAYING
        handler.post(updateRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playPauseButton.setImageResource(R.drawable.play_arrow_icon)
        playerState = STATE_PAUSED
        handler.removeCallbacks(updateRunnable)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }
}