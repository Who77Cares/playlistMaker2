package com.bignerdranch.playlistmaker.media.playlist

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bignerdranch.playlistmaker.databinding.FragmentPlaylistSingleBinding
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

    class SinglePlaylistFragment : Fragment() {

        private val viewModel: SinglePlaylistViewModel by viewModel()
        private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

        companion object {
            const val ID = "id"
            const val COVER_URI = "coverUri"
            const val NAME = "name"
            const val DESCRIPTION = "description"
            const val TRACK_SIZE = "track_size"
            fun createArgs(playlist: PlaylistModel): Bundle {
                return Bundle().apply {
                    putInt(ID, playlist.id.toInt())
                    putString(COVER_URI, playlist.coverUri.toString())
                    putString(NAME, playlist.name)
                    putString(DESCRIPTION, playlist.description)
                    putInt(TRACK_SIZE, playlist.tracksSize)
                }
            }
        }


        private var _binding: FragmentPlaylistSingleBinding? = null
        private val binding get() = _binding!!

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            _binding = FragmentPlaylistSingleBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            setupFixedBottomSheet()

            binding.arrowBack.setOnClickListener {
                findNavController().navigateUp()
            }

            val playlistId = arguments?.getInt(ID, 0)?.toLong()!!
            viewModel.getTracksDataFromRoom(playlistId)


            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiState
                        .drop(1) // выбрасываем первые empty значения
                        .collect { state ->
                            val a = state.tracks

                            a.forEach {
                                Log.d("Мы в цикле", it.trackName)
                            }

                            val b = state.totalTracksTime
                            Log.d("Мы в цикле", "$b")
                        }
                }
            }


        }

        override fun onDestroy() {
            super.onDestroy()
            _binding = null
        }


    fun setupFixedBottomSheet() {
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val halfExpandedHeight = (screenHeight * 0.4f).toInt()

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetPlaylists)
        bottomSheetBehavior.halfExpandedRatio = 0.4f
        bottomSheetBehavior.peekHeight = halfExpandedHeight
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

}