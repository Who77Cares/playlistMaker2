package com.bignerdranch.playlistmaker.media

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bignerdranch.playlistmaker.media.db_favorite.ui.FavoriteTracksFragment
import com.bignerdranch.playlistmaker.media.playlists.PlaylistFragment

class MediaViewPagerAdapter(
    hostFragment: Fragment
) : FragmentStateAdapter(hostFragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoriteTracksFragment.newInstance()
            else -> PlaylistFragment.newInstance()
        }
    }

    override fun getItemCount(): Int = 2

}