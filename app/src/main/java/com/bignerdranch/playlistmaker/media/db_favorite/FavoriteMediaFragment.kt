package com.bignerdranch.playlistmaker.media.db_favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bignerdranch.playlistmaker.R
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.playlistmaker.audio.ui.ui.AudioPlayerFragment
import com.bignerdranch.playlistmaker.databinding.FragmentFavoriteMediaBinding
import com.bignerdranch.playlistmaker.media.db_favorite.ui.MediaAdapter
import com.bignerdranch.playlistmaker.media.db_favorite.ui.MediaState
import com.bignerdranch.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteMediaFragment: Fragment() {

    private val adapter = MediaAdapter {track -> onTrackClicked(track) }

    companion object {
        fun newInstance(): FavoriteMediaFragment {
            return FavoriteMediaFragment().apply {
                arguments = Bundle().apply {
                    // если нужно будет передать данные — положим сюда
                } } }
    }

    private var _binding: FragmentFavoriteMediaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteMediaViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteMediaBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.favoriteRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoriteRecycleView.adapter = adapter

        viewModel.fillData()

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

    }

    private fun render(state: MediaState) {
        when(state) {
            is MediaState.Content -> showContent(state.tracks)
            is MediaState.Empty -> showEmpty()
        }
    }

    private fun showContent(track: List<Track>) {

        binding.favoriteRecycleView.visibility = View.VISIBLE
        binding.favoritePlaceholderImge.visibility = View.GONE
        binding.favoritePlaceholderText.visibility = View.GONE

        adapter.tracks.clear()
        adapter.tracks.addAll(track)
        adapter.notifyDataSetChanged()
    }

    private fun showEmpty() {
        binding.favoriteRecycleView.visibility = View.GONE
        binding.favoritePlaceholderImge.visibility = View.VISIBLE
        binding.favoritePlaceholderText.visibility = View.VISIBLE
    }

    private fun onTrackClicked(track: Track) {
        findNavController().navigate(
            R.id.action_mediaFragment_to_audioPlayerFragment,
            AudioPlayerFragment.createArgs(track)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}