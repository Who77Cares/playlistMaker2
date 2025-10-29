package com.bignerdranch.playlistmaker.media.db_favorite.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.search.domain.models.Track
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class FavoriteAdapter(
    private val onItemClick: (Track) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.MediaViewHolder>() {

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MediaViewHolder = MediaViewHolder(parent, onItemClick)

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    class MediaViewHolder(parent: ViewGroup, private val onItemClick: (Track) -> Unit) : RecyclerView.ViewHolder(
        LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_track, parent, false)
    ) {

        private val trackNameView: TextView = itemView.findViewById(R.id.trackName)
        private val artistNameView: TextView = itemView.findViewById(R.id.artistName)
        private val trackTimeView: TextView = itemView.findViewById(R.id.trackTime)
        private val artworkUrl100View: ImageView = itemView.findViewById(R.id.artworkUrl100)

        fun bind(model: Track) {
            trackNameView.text = model.trackName
            artistNameView.text = model.artistName

            val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
            trackTimeView.text = formattedTime

            Glide.with(itemView)
                .load(model.artworkUrl100)
                .centerCrop()
                .transform(RoundedCorners(2))
                .placeholder(R.drawable.placeholder)
                .into(artworkUrl100View)


            // Клик по элементу
            itemView.setOnClickListener {
                onItemClick(model)
            }
        }

    }

}