package com.bignerdranch.playlistmaker.media.playlists

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import com.bumptech.glide.Glide

class PlaylistViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val title: TextView = itemView.findViewById(R.id.titlePlaylistItem)
    private val description: TextView = itemView.findViewById(R.id.descriptionPlaylistTextView)
    private val imgUri: ImageView = itemView.findViewById(R.id.imagePlaylist)

    fun bind(playlist: PlaylistModel) {
        title.text = playlist.name
        description.text = playlist.description

        Glide.with(itemView)
            .load(playlist.coverUri)
            .centerCrop()
            .placeholder(R.drawable.placeholder_search)
            .into(imgUri)
    }

}