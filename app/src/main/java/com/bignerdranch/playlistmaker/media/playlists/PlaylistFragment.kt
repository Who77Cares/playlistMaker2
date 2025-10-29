package com.bignerdranch.playlistmaker.media.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.databinding.FragmentPlaylistMediaBinding
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment: Fragment() {

    companion object {
        fun newInstance(): PlaylistFragment {
            return PlaylistFragment().apply {
                arguments = Bundle().apply {
                    // если нужно будет передать данные — положим сюда
                } } }
    }

    private var _binding: FragmentPlaylistMediaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistViewModel by viewModel()

    private val adapter = PlaylistAdapter()


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


        viewModel.getData()
        viewModel.observeStateLiveData().observe(viewLifecycleOwner) {
            render(it)
        }


        binding.playlistRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistRecyclerView.adapter = adapter


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

    private fun render(state: PlaylistViewModel.PlaylistState) {
        when (state) {
            is PlaylistViewModel.PlaylistState.Content -> showContent(state.playlists)
            is PlaylistViewModel.PlaylistState.Empty -> empty()
        }
    }

    private fun showContent(playlists: List<PlaylistModel>) {
        adapter.playlists = playlists.toList()
        adapter.notifyDataSetChanged()

        binding.noPlaylistText.visibility = View.GONE
        binding.noPlaylistImg.visibility = View.GONE
        binding.playlistRecyclerView.visibility = View.VISIBLE
    }

    private fun empty() {
        binding.noPlaylistText.visibility = View.VISIBLE
        binding.noPlaylistImg.visibility = View.VISIBLE
        binding.playlistRecyclerView.visibility = View.GONE
    }

}