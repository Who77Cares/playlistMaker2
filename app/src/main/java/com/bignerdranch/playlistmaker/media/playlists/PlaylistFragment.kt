package com.bignerdranch.playlistmaker.media.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bignerdranch.playlistmaker.R

import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import com.bignerdranch.playlistmaker.media.playlist.SinglePlaylistFragment


class PlaylistFragment: Fragment() {

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
                PlaylistsCompose(
                    onPlaylistClicked = { playlist ->
                        onPlaylistClick(playlist)
                    },
                    onCreatePlaylistClicked = {
                        findNavController().navigate(
                            R.id.action_favoriteMediaFragment_to_newPlaylistFragment
                        )
                    }
                )
            }
        }
    }

    private fun onPlaylistClick(playlist: PlaylistModel) {
        findNavController().navigate(
            R.id.action_mediaFragment_to_singlePlaylistFragment,
            SinglePlaylistFragment.Companion.createArgs(playlist)
        )
    }

}
