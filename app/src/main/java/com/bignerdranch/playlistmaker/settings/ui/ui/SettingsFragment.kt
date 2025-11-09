package com.bignerdranch.playlistmaker.settings.ui.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.util.App
import com.bignerdranch.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment: Fragment() {

    private val viewModel: SettingsViewModel by viewModel()
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeTheme().observe(viewLifecycleOwner) { isDark ->
            binding.switchCompat.isChecked = isDark
        }

        binding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleTheme(isChecked)
        }




        binding.share.setOnClickListener {
            viewModel.shareApp()
        }

        binding.support.setOnClickListener {
            viewModel.openSupport()
        }

        binding.userAgreement.setOnClickListener {
            viewModel.openTerms()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}