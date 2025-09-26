package com.bignerdranch.playlistmaker.media.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bignerdranch.playlistmaker.media.db_favorite.FavoriteMediaFragment

class MediaViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoriteMediaFragment.Companion.newInstance()
            else -> PlaylistMediaFragment.newInstance()
        }
    }

    override fun getItemCount(): Int = 2

}