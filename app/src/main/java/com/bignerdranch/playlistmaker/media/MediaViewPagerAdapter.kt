package com.bignerdranch.playlistmaker.media

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoriteMediaFragment()
            else -> PlaylistMediaFragment()
        }
    }

    override fun getItemCount(): Int = 2

}