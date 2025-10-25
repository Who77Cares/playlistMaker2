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
    private val tracksNumber: TextView = itemView.findViewById(R.id.tracksNumberPlaylistTextView)
    private val imgUri: ImageView = itemView.findViewById(R.id.imagePlaylist)

    fun bind(playlist: PlaylistModel) {
        title.text = playlist.name
        val trackCount = playlist.tracks.size
        tracksNumber.text = itemView.context.resources.getQuantityString(R.plurals.tracks_count, trackCount, trackCount)

        Glide.with(itemView)
            .load(playlist.coverUri)
            .centerCrop()
            // тут, кстати, мы уже скруглили углы уже у самого imgUri через атрибут app:shapeAppearanceOverlay="@style/RoundedImageStyle"
            .placeholder(R.drawable.placeholder)
            .into(imgUri)
    }

}