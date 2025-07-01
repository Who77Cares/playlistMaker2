package com.bignerdranch.playlistmaker.audio.ui.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.audio.ui.TrackAudioMapper
import com.bignerdranch.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.bignerdranch.playlistmaker.audio.ui.presentation.AudioPlayerViewModel
import com.bignerdranch.playlistmaker.search.domain.models.Track
import com.bignerdranch.playlistmaker.search.ui.ui.SearchActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class AudioPlayer : AppCompatActivity() {

    private lateinit var viewModel: AudioPlayerViewModel

    private lateinit var binding: ActivityAudioPlayerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Извлекаем Track из Intent
        val track = extractTrackFromIntent(intent)

        // работа с ViewModel
        val mapper = TrackAudioMapper()

        viewModel = ViewModelProvider(this, AudioPlayerViewModel.getFactory(mapper))
            .get(AudioPlayerViewModel::class.java)

        viewModel.observeProgressTime().observe(this) {
            binding.durationInRealTime.text = it
        }

        viewModel.observePlayerState().observe(this) {
            changeButtonState(it == AudioPlayerViewModel.STATE_PLAYING)

        }

        binding.PlayOrStopButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        // Подписка на данные трека для отображения
        viewModel.observeTrackUiModel().observe(this) { model ->
            binding.trackName.text = model.trackName
            binding.artistName.text = model.artistName
            binding.album.text = model.album
            binding.songYear.text = model.year
            binding.durationTime.text = model.duration
            binding.songStyle.text = model.style
            binding.songCountry.text = model.country

            Glide.with(this)
                .load(model.coverUrl)
                .fitCenter()
                .transform(RoundedCorners(20))
                .placeholder(R.drawable.placeholder_search)
                .into(binding.songCover)
        }

        // Устанавливаем трек во ViewModel
        viewModel.setTrack(track)



        binding.arrowBack.setOnClickListener {
            val _intent = Intent(this@AudioPlayer, SearchActivity::class.java)
            startActivity(_intent)
        }


        binding.addToLike.setOnClickListener {
            val currentDrawable = binding.addToLike.drawable
            val likeDrawable = ContextCompat.getDrawable(this, R.drawable.empty_like)

            // Сравниваем состояния drawable, а не сами объекты
            if (currentDrawable.constantState == likeDrawable?.constantState) {
                binding.addToLike.setImageResource(R.drawable.is_liked)
            } else {
                binding.addToLike.setImageResource(R.drawable.empty_like)
            }
        }

    }

    private fun changeButtonState(isPlaying: Boolean) {
        if (isPlaying) {
            binding.PlayOrStopButton.setImageResource(R.drawable.pause_icon)
        } else {
            binding.PlayOrStopButton.setImageResource(R.drawable.play_arrow_icon)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun extractTrackFromIntent(intent: Intent): Track {
        return Track(
            trackName = intent.getStringExtra("trackName") ?: "",
            artistName = intent.getStringExtra("artistName") ?: "",
            trackTimeMillis = intent.getIntExtra("durationTime", 0),
            artworkUrl100 = intent.getStringExtra("songCover") ?: "",
            trackId = intent.getIntExtra("trackId", 0),
            collectionName = intent.getStringExtra("album") ?: "",
            releaseDate = intent.getStringExtra("songYear") ?: "",
            primaryGenreName = intent.getStringExtra("songStyle") ?: "",
            country = intent.getStringExtra("songCountry") ?: "",
            previewUrl = intent.getStringExtra("previewUrl") ?: ""
        )
    }
}