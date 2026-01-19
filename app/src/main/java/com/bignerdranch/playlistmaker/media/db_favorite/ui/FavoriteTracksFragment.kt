package com.bignerdranch.playlistmaker.media.db_favorite.ui

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

import com.bignerdranch.playlistmaker.search.domain.network.Track

class FavoriteTracksFragment: Fragment() {

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
                FavoriteTracksCompose(
                    onTrackClicked = { track -> onTrackClicked(track) }
                )
            }
        }
    }

    private fun onTrackClicked(track: Track) {
        findNavController().navigate(
            R.id.action_mediaFragment_to_audioPlayerFragment,
            AudioPlayerFragment.Companion.createArgs(track)
        )
    }
}