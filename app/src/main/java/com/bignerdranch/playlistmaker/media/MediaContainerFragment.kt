package com.bignerdranch.playlistmaker.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.audio.ui.AudioPlayerFragment
import com.bignerdranch.playlistmaker.media.playlist.SinglePlaylistFragment


class MediaContainerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {
                MediaContainerCompose(
                    onTrackClicked = { track ->
                        findNavController().navigate(
                            R.id.action_mediaFragment_to_audioPlayerFragment,
                            AudioPlayerFragment.Companion.createArgs(track)// передаем аргументы трека
                        )
                    },
                    onPlaylistClicked = { playlist ->
                        findNavController().navigate(
                            R.id.action_mediaFragment_to_singlePlaylistFragment,
                            SinglePlaylistFragment.Companion.createArgs(playlist)
                        )
                    },

                    onCreatePlaylistClicked = { // ← теперь есть этот параметр!
                        findNavController().navigate(
                            R.id.action_favoriteMediaFragment_to_newPlaylistFragment
                        )
                    }

                    )
            }
        }
    }
}


