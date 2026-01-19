package com.bignerdranch.playlistmaker.search.ui

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


class SearchFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {
                SearchCompose(
                    onTrackClicked = { track ->
                        findNavController().navigate(
                            R.id.action_searchFragment_to_audioPlayerFragment,
                            AudioPlayerFragment.Companion.createArgs(track)
                        )
                    }
                )
            }
        }
    }
}