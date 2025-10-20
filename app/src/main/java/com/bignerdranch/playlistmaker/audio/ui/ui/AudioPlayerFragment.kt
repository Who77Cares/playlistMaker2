package com.bignerdranch.playlistmaker.audio.ui.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.TrackMapper
import com.bignerdranch.playlistmaker.audio.ui.models.PlayerState
import com.bignerdranch.playlistmaker.audio.ui.presentation.AudioPlayerViewModel
import com.bignerdranch.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.bignerdranch.playlistmaker.search.domain.models.Track
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
class AudioPlayerFragment(): Fragment() {

    companion object {
        const val TRACK_NAME = "trackName"
        const val ARTIST_NAME = "artistName"
        const val DURATION_TIME = "durationTime"
        const val SONG_COVER = "songCover"
        const val TRACK_ID = "trackId"
        const val ALBUM = "album"
        const val SONG_YEAR = "songYear"
        const val SONG_STYLE = "songStyle"
        const val SONG_COUNTRY = "songCountry"
        const val PREVIEW_URL = "previewUrl"

        fun createArgs(track: Track): Bundle {
            return Bundle().apply {
                putString(TRACK_NAME, track.trackName)
                putString(ARTIST_NAME, track.artistName)
                putInt(DURATION_TIME, track.trackTimeMillis)
                putString(SONG_COVER, track.artworkUrl100)
                putInt(TRACK_ID, track.trackId)
                putString(ALBUM, track.collectionName)
                putString(SONG_YEAR, track.releaseDate)
                putString(SONG_STYLE, track.primaryGenreName)
                putString(SONG_COUNTRY, track.country)
                putString(PREVIEW_URL, track.previewUrl)
            }
        }
    }

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AudioPlayerViewModel by viewModel { parametersOf(get<TrackMapper>())  }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = extractTrackFromBundle()


        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            binding.PlayOrStopButton.isEnabled = it.isPlayButtonEnabled
            binding.durationInRealTime.text = it.progress

            binding.PlayOrStopButton.setImageResource(
                if (it is PlayerState.Playing) R.drawable.pause_icon
                else R.drawable.play_arrow_icon
            )
        }

        viewModel.isFavoriteLiveData.observe(viewLifecycleOwner) { isFavorite ->
            binding.addToLike.setImageResource(
                if (isFavorite) R.drawable.is_liked
                else R.drawable.empty_like
            )
        }

        binding.PlayOrStopButton.setOnClickListener {
            Log.d("AudioPlayerFragment", "Play button clicked!")
            viewModel.onPlayButtonClicked()
        }

        viewModel.observeTrackUiModel().observe(viewLifecycleOwner) { model ->
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
            findNavController().navigateUp()
        }

        binding.addToLike.setOnClickListener {
            viewModel.toggleFavorite(track)
        }

    }


    private fun extractTrackFromBundle(): Track {
        val track = Track(
            trackName = arguments?.getString(TRACK_NAME).orEmpty(),
            artistName = arguments?.getString(ARTIST_NAME).orEmpty(),
            trackTimeMillis = arguments?.getInt(DURATION_TIME, 0) ?: 0,
            artworkUrl100 = arguments?.getString(SONG_COVER).orEmpty(),
            trackId = arguments?.getInt(TRACK_ID, 0) ?: 0,
            collectionName = arguments?.getString(ALBUM).orEmpty(),
            releaseDate = arguments?.getString(SONG_YEAR).orEmpty(),
            primaryGenreName =  arguments?.getString(SONG_STYLE).orEmpty(),
            country = arguments?.getString(SONG_COUNTRY).orEmpty(),
            previewUrl = arguments?.getString(PREVIEW_URL).orEmpty()
        )

        Log.d("AudioPlayerFragment", "Extracted track previewUrl: ${track.previewUrl}")
        return track
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}