package com.bignerdranch.playlistmaker.media.db_favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bignerdranch.playlistmaker.databinding.FragmentFavoriteMediaBinding
import com.bignerdranch.playlistmaker.media.db_favorite.FavoriteMediaViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteMediaFragment: Fragment() {
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance(): FavoriteMediaFragment {
            return FavoriteMediaFragment().apply {
                arguments = Bundle().apply {
                    // если нужно будет передать данные — положим сюда
                }
            }
        }
    }
}