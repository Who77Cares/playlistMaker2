package com.bignerdranch.playlistmaker.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.databinding.FragmentMainBinding
import com.bignerdranch.playlistmaker.media.MediaFragment
import com.bignerdranch.playlistmaker.search.ui.ui.SearchFragment
import com.bignerdranch.playlistmaker.settings.ui.ui.SettingsFragment

// фрагмент-заглушка перед использованием нижней кнопки навигации

class MainFragment: Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding  get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchButton.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.rootFragmentContainerView, SearchFragment())
                addToBackStack(null)
                commit()
            }
        }

        binding.mediaButton.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.rootFragmentContainerView, MediaFragment())
                addToBackStack(null)
                commit()
            }

        }

        binding.settingsButton.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.rootFragmentContainerView, SettingsFragment())
                addToBackStack(null)
                commit()
            }

        }


    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}

