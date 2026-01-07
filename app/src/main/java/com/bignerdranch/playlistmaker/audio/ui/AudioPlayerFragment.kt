package com.bignerdranch.playlistmaker.audio.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.util.TrackMapper
import com.bignerdranch.playlistmaker.audio.models.PlayerState
import com.bignerdranch.playlistmaker.audio.presentation.AudioPlayerViewModel
import com.bignerdranch.playlistmaker.audio.service.AudioService
import com.bignerdranch.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import com.bignerdranch.playlistmaker.search.domain.network.Track
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
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



    private lateinit var playlistAdapter: PlaylistBottomSheetAdapter
    private var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>


    // используется только для того чтобы по id находить такой же PlaylistEntity в room
    var lastClickedPlaylist: PlaylistModel? = null

    private val track by lazy { extractTrackFromBundle() }


    // для работы Service
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Если выдали разрешение — привязываемся к сервису.
            bindMusicService()
        } else {
            // Иначе просто покажем ошибку
            Toast.makeText(requireContext(), "Can't bind service!", Toast.LENGTH_LONG).show()
        }
    }
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) {
            val binder = service as AudioService.AudioServiceBinder

            viewModel.setAudioPlayerControl(binder.getService())

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            viewModel.removeAudioPlayerControl()
        }

    }



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

        // Устанавливаем трек во ViewModel
        viewModel.setTrack(track)

        // Прежде чем привязаться к сервису, который будет показывать уведомление
        // Мы должны проверить, выданы ли соответствующие разрешения
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // На версиях ниже Android 13 —
            // можно сразу привязаться к сервису.
            bindMusicService()
        }


        playlistAdapter = PlaylistBottomSheetAdapter(
            playlists = emptyList(),
            onPlaylistClick = { playlistModel ->
                lastClickedPlaylist = playlistModel
                viewModel.addTrackToPlaylist(track, playlistModel)
            }
        )

        setupBottomSheet()

        lifecycleScope.launch {
            viewModel.addTrackState.collect { result ->

                when(result) {
                    false -> { showSuccessTrackAddToPlaylist(lastClickedPlaylist?.name ?: "")}
                    true -> { showErrorTrackAddToPlaylist(lastClickedPlaylist?.name ?: "") }
                    else -> {}
                }
                // Очищаем состояние после показа тоста
                viewModel.clearAddTrackState()
            }
        }


        // получение данных о плейлистах из room и установка их в адаптер
        viewModel.getPlaylistDataFromRoom()

        viewModel.observePlaylistData().observe(viewLifecycleOwner) { data ->
            playlistAdapter.updatePlaylists(data)
        }


        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            updateButtonAndProgress(it)
        }



        binding.playButton.onPlaybackStateChanged = { shouldPlay ->

            viewModel.onPlayerButtonClicked()
        }



        viewModel.isFavoriteLiveData.observe(viewLifecycleOwner) { isFavorite ->
            binding.addToLike.setImageResource(
                if (isFavorite) R.drawable.is_liked
                else R.drawable.empty_like
            )
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
                .load(model.getHighQualityCover())
                .fitCenter()
                .transform(RoundedCorners(20))
                .placeholder(R.drawable.placeholder)
                .into(binding.songCover)
        }


        binding.arrowBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.addToLike.setOnClickListener {
            viewModel.toggleFavorite(track)
        }

        binding.addToPlayList.setOnClickListener {
            showPlaylistsBottomSheet()
        }

        // Overlay для закрытия Bottom Sheet
        binding.overlay.setOnClickListener {
            hidePlaylistsBottomSheet()
        }

        binding.newPlaylistButton.setOnClickListener {
            hidePlaylistsBottomSheet()
            findNavController().navigate(
                R.id.action_audioPlayerFragment_to_newPlaylistFragment
            )
        }

    }

    override fun onPause() {
        super.onPause()
        // Устанавливаем флаг что приложение уходит в фон через ViewModel
        viewModel.setAppInBackground(true)
    }

    override fun onResume() {
        super.onResume()
        // Устанавливаем флаг что приложение возвращается через ViewModel
        viewModel.setAppInBackground(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        lastClickedPlaylist = null
        // Сбрасываем флаг через ViewModel
        viewModel.setAppInBackground(false)
        unbindMusicService()


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


    // методы для работы с воспроизведением и Service
    private fun updateButtonAndProgress(playerState: PlayerState) {

        binding.playButton.apply {
            isEnabled = playerState.isPlayButtonEnabled
        }

        binding.durationInRealTime.text = playerState.progress

        val isPlaying = playerState is PlayerState.Playing
        binding.playButton.setPlaybackState(isPlaying)
    }

    private fun bindMusicService() {

        val track = extractTrackFromBundle()

        val intent = Intent(
            requireContext(),
            AudioService::class.java
        )
            .apply {
                putExtra("song_url", track.previewUrl)
                putExtra("song_info", "${track.artistName} - ${track.trackName}")
            }

        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindMusicService() {
        requireContext().unbindService(serviceConnection)
    }



// Методы для работы с Sheet
    private fun setupBottomSheet() {
// Создаётся BottomSheetBehavior, «привязанный» к layout bottomSheetPlaylists.
// По умолчанию лист скрыт (STATE_HIDDEN).
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetPlaylists)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.playlistInSheetRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playlistAdapter
        }

        bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                if (_binding == null) return

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            // По умолчанию slideOffset начинает меняться только после того, как шит "отлипнет" от peekHeight, то есть когда он выходит из состояния STATE_COLLAPSED.
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

                if (_binding == null) return

                binding.overlay.apply {
                    visibility = View.VISIBLE
                    alpha = slideOffset.coerceIn(0f, 0.7f)
                }
            }
        }
        bottomSheetCallback?.let { bottomSheetBehavior.addBottomSheetCallback(it) }
    }

    private fun showPlaylistsBottomSheet() {

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.overlay.visibility = View.VISIBLE
    }

    private fun hidePlaylistsBottomSheet() {

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.overlay.visibility = View.GONE
    }


    // методы для работы логики, связанной с Room
    private fun showSuccessTrackAddToPlaylist(playlistName: String) {
        MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialogTheme)
            .setTitle("Трек уже в плейлисте $playlistName")
            .setNeutralButton("Ок") { dialog, which -> }
            .show()
    }

    private fun showErrorTrackAddToPlaylist(playlistName: String) {
        Toast.makeText(requireContext(), "Добавлено в плейлист $playlistName", Toast.LENGTH_SHORT)
            .show()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }
}




