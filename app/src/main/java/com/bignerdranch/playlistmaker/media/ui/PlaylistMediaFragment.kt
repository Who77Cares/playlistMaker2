package com.bignerdranch.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.audio.ui.ui.AudioPlayerFragment
import com.bignerdranch.playlistmaker.databinding.FragmentPlaylistMediaBinding
import com.bignerdranch.playlistmaker.media.db_favorite.ui.FavoriteMediaViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistMediaFragment: Fragment() {

    private var _binding: FragmentPlaylistMediaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteMediaViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_favoriteMediaFragment_to_newPlaylistFragment
            )
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    companion object {
        fun newInstance(): PlaylistMediaFragment {
            return PlaylistMediaFragment().apply {
                arguments = Bundle().apply {
                    // если нужно будет передать данные — положим сюда
                }
            }
        }
    }

}