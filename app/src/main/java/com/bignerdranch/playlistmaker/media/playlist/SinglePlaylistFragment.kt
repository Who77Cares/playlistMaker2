package com.bignerdranch.playlistmaker.media.playlist

import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.audio.ui.ui.AudioPlayerFragment
import com.bignerdranch.playlistmaker.databinding.FragmentPlaylistSingleBinding
import com.bignerdranch.playlistmaker.media.db_favorite.ui.FavoriteAdapter
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import com.bignerdranch.playlistmaker.media.new_playlist.ui.NewPlaylistFragment
import com.bignerdranch.playlistmaker.search.domain.models.Track
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SinglePlaylistFragment : Fragment() {

    private lateinit var playlist: PlaylistModel

    private val viewModel: SinglePlaylistViewModel by viewModel()

    private lateinit var tracksSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var optionsSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback? = null

    private val tracksAdapter = FavoriteAdapter(
        onItemClick = { track -> onTrackClicked(track) },
        onLongItemClick = { trackId -> onLongTrackClicked(trackId) }
    )


    companion object {
        const val ID = "id"
        fun createArgs(playlist: PlaylistModel): Bundle {
            return Bundle().apply {
                putInt(ID, playlist.id.toInt())
            }
        }
    }


    private var _binding: FragmentPlaylistSingleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistSingleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = arguments?.getInt(ID, 0)?.toLong()!!
        viewModel.getPlaylistDataById(playlistId)




        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlistData.collect { playlistData ->
                    playlistData?.let {
                        playlist = it // сохраняем в переменную
                        setPlaylistParams()
                    }
                }
            }
        }

        setupFixedBottomSheet()
        setupSettingsBottomSheet()

        binding.tracksInSheetRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.tracksInSheetRecyclerView.adapter = tracksAdapter

        binding.arrowBack.setOnClickListener {
            findNavController().navigateUp()
        }


        viewModel.getTracksDataFromRoom(playlistId)


        //  Показать текущее состояние сразу
        setTracksParams(
            viewModel.uiState.value.tracks,
            viewModel.uiState.value.totalTracksTime
        )

        //  А потом подписаться на обновления
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .drop(1)
                    .collect { state ->
                        setTracksParams(state.tracks, state.totalTracksTime)
                    }
            }
        }


        binding.playlistShare.setOnClickListener {
            sharePlaylist()
        }

        binding.sheetShare.setOnClickListener {
            sharePlaylist()
        }

        binding.sheetDeletePlaylist.setOnClickListener {
            deletePlaylist()
        }

        binding.threeDots.setOnClickListener {
            optionsSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.overlay.visibility = View.VISIBLE
        }

        binding.sheetEditPlaylistInfo.setOnClickListener {
            navigateToEditPlaylist()
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun sharePlaylist() {
        if (playlist.tracksSize != 0) {
            viewModel.sharePlaylist(
                playlistName = playlist.name,
                playlistDescription = playlist.description
            )
        } else {
            MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialogTheme)
                .setTitle("В плейлисте нет песен")
                .setNeutralButton("ОК") {_, _, ->}
                .show()

            optionsSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        }
    }


    private fun setTracksParams(track: List<Track>, songsDuration: Int) {

        binding.songsDuration.text = resources.getQuantityString(R.plurals.minutes_count, songsDuration, songsDuration)

        tracksAdapter.tracks.clear()
        tracksAdapter.tracks.addAll(track.reversed())
        tracksAdapter.notifyDataSetChanged()
    }

    private fun setPlaylistParams() {

        val songsNumber = resources.getQuantityString(R.plurals.tracks_count, playlist.tracksSize, playlist.tracksSize)

        binding.playlistName.text = playlist.name
        binding.playlistDescription.text = playlist.description
        binding.songsNumber.text = songsNumber


        val layoutParams = binding.playlistCover.layoutParams as ConstraintLayout.LayoutParams
        if (playlist.coverUri != "".toUri()) {
            binding.playlistCover.setImageURI(playlist.coverUri)
            binding.playlistCover.scaleType = ImageView.ScaleType.FIT_XY

            layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            binding.playlistCover.layoutParams = layoutParams

        } else {
            binding.playlistCover.setImageResource(R.drawable.placeholder)
            binding.playlistCover.scaleType = ImageView.ScaleType.FIT_CENTER

            layoutParams.topToTop = R.id.arrow_back
            binding.playlistCover.layoutParams = layoutParams
        }


        Glide.with(this)
            .load(playlist.coverUri)
            .fitCenter()
            .placeholder(R.drawable.placeholder)
            .into(binding.sheetPlaylistCover)

        binding.sheetPlaylistName.text = playlist.name
        binding.sheetTrackNumber.text = songsNumber
    }

    private fun onTrackClicked(track: Track) {
            findNavController().navigate(
                R.id.action_singlePlaylistFragment_to_audioPlayerFragment,
                AudioPlayerFragment.Companion.createArgs(track)
            )
    }


    private fun onLongTrackClicked(trackId: Int) {
        MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialogTheme)
            .setTitle("Хотите удалить трек?")
            .setPositiveButton("ДА") { _, _ ->
                viewModel.deleteTrackFromPlaylist(playlist.id, trackId.toLong())
                tracksAdapter.notifyDataSetChanged()
            }
            .setNegativeButton("НЕТ") { _, _ -> }
            .show()
    }

    private fun deletePlaylist() {
        MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialogTheme)
            .setTitle("Хотите удалить плейлист ${playlist.name}?")
            .setPositiveButton("ДА") { _, _ ->
                viewModel.deletePlaylist(playlist.id)

                // Наблюдаем за состоянием удаления
                viewModel.deleteSuccess.observe(viewLifecycleOwner) { success ->
                    if (success == true) {
                        findNavController().navigateUp()
                    }
                }
            }
            .setNegativeButton("НЕТ") { _, _ -> }
            .show()
    }

    private fun navigateToEditPlaylist() {
            findNavController().navigate(
                R.id.action_singlePlaylistFragment_to_newPlaylistFragment,
                NewPlaylistFragment.Companion.createArgs(playlist)
            )

    }

    private fun setupSettingsBottomSheet() {
        optionsSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetPlaylistsOptions)
        optionsSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                        tracksSheetBehavior.isDraggable = true

                        binding.touchBlocker.visibility = View.GONE

                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.overlay.visibility = View.VISIBLE

                        // при выезжании optionsSheet блокируем tracksSheet
                        tracksSheetBehavior.isDraggable = false
                        binding.touchBlocker.visibility = View.VISIBLE
                    }
                    else -> {
                        // Обработка других состояний если нужно
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // slideOffset изменяется от -1 до 1
                // При движении вверх: от 0 до 1, при движении вниз: от 0 до -1
                val alpha = when {
                    slideOffset > 0 -> slideOffset * 0.7f // При открытии
                    else -> (1 + slideOffset) * 0.7f // При закрытии (slideOffset отрицательный)
                }
                binding.overlay.alpha = alpha.coerceIn(0f, 0.7f)
            }
        }

        // без колбэка затемнение работать не будет
        optionsSheetBehavior.addBottomSheetCallback(bottomSheetCallback!!)
    }

    private fun setupFixedBottomSheet() {
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val halfExpandedHeight = (screenHeight * 0.4f).toInt()

        tracksSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetPlaylists)
        tracksSheetBehavior.halfExpandedRatio = 0.4f
        tracksSheetBehavior.peekHeight = halfExpandedHeight
        tracksSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

}

