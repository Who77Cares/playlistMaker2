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
import com.bignerdranch.playlistmaker.databinding.FragmentMediaBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediaContainerFragment: Fragment() {

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
                            // передайте аргументы трека
                        )
                    },
                    onPlaylistClicked = { playlist ->
                        findNavController().navigate(
                            R.id.action_mediaFragment_to_singlePlaylistFragment,
                            // передайте аргументы плейлиста
                        )
                    },
                    onCreatePlaylistClicked = {
//                        findNavController().navigate(
//                            R.id.action_mediaFragment_to_newPlaylistFragment
//                        )
                    }
                )
            }
        }
    }
}


