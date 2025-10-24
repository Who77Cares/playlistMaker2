package com.bignerdranch.playlistmaker.audio.ui.ui


import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.databinding.ItemTrackForPlaylistBinding
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class PlaylistBottomSheetAdapter(
    private var playlists: List<PlaylistModel> = emptyList()
) : RecyclerView.Adapter<PlaylistBottomSheetAdapter.PlaylistViewHolder>() {


    fun updatePlaylists(newPlaylists: List<PlaylistModel>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistViewHolder {
        val binding = ItemTrackForPlaylistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PlaylistViewHolder(binding)
    }


    override fun onBindViewHolder(
        holder: PlaylistViewHolder,
        position: Int
    ) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size


    inner class PlaylistViewHolder(
        private val binding: ItemTrackForPlaylistBinding
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(playlist: PlaylistModel) {

            binding.playlistName.text = playlist.name
            binding.playlistTrackCount.text = playlist.tracks.size.toString()

            Glide.with(itemView)
                .load(playlist.coverUri.toString())
                .centerCrop()
                .transform(RoundedCorners(2))
                .placeholder(R.drawable.placeholder_search)
                .into(binding.playlistCover)

        }
    }

}



