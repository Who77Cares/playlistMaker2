package com.bignerdranch.playlistmaker.audio.ui.ui


import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.databinding.ItemTrackForSheetBinding
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class PlaylistBottomSheetAdapter(
    private var playlists: List<PlaylistModel> = emptyList(),
    private val onPlaylistClick: (PlaylistModel) -> Unit
) : RecyclerView.Adapter<PlaylistBottomSheetAdapter.PlaylistViewHolder>() {


    fun updatePlaylists(newPlaylists: List<PlaylistModel>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistViewHolder {
        val binding = ItemTrackForSheetBinding.inflate(
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
        private val binding: ItemTrackForSheetBinding
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(playlist: PlaylistModel) {

            itemView.setOnClickListener {
                onPlaylistClick(playlist)
            }

            binding.playlistName.text = playlist.name

            val trackCount = playlist.tracks.size
            binding.playlistTrackCount.text =
                itemView.context.resources.getQuantityString(R.plurals.tracks_count, trackCount, trackCount)


            Glide.with(itemView)
                .load(playlist.coverUri.toString())
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(binding.playlistCover)

        }
    }

}



