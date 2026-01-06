package com.bignerdranch.playlistmaker.settings.ui.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment: Fragment() {

    private val viewModel: SettingsViewModel by viewModel()

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
                SettingsCompose(
                    viewModel = viewModel,
                    onShareClicked = {
                        viewModel.shareApp()
                    },
                    onSupportClicked = {
                        viewModel.openSupport()
                    },

                    onTermsClicked = {
                        viewModel.openTerms()
                    }
                )
            }

        }
    }

}