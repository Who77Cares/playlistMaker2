package com.bignerdranch.playlistmaker.search.ui.ui

import android.util.Log
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

class SearchAdapter(
    private val isHistory: Boolean, // обновляем историю только при клике на трек из сети
    private val itemClickListener: OnItemClickListener // интерфейс для обработки клика в SearchActivity
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    fun interface OnItemClickListener {
        fun onItemClick(track: Track, trackFromHistory: Boolean)
    }

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder =
        SearchViewHolder(parent)

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {

            itemClickListener.onItemClick(
                track = tracks[position],
                trackFromHistory = isHistory
            )

        }

    }

    override fun getItemCount(): Int = tracks.size




    class SearchViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(
        LayoutInflater
            .from(parent.context)
            .inflate(R.layout.track_view, parent, false)
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
                .placeholder(R.drawable.placeholder_search)
                .into(artworkUrl100View)
        }
    }
}