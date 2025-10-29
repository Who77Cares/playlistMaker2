package com.bignerdranch.playlistmaker.media.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel


class PlaylistAdapter(
    private val onItemClick: (PlaylistModel) -> Unit
): RecyclerView.Adapter<PlaylistViewHolder>() {

    var playlists: List<PlaylistModel> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false)
        return PlaylistViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(
        holder: PlaylistViewHolder,
        position: Int
    ) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size
}