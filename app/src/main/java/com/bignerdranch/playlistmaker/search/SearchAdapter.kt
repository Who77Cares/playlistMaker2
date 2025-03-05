package com.bignerdranch.playlistmaker.search

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.playlistmaker.AudioPlayer
import com.bignerdranch.playlistmaker.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class SearchAdapter(
    private val itemClickListener: OnItemClickListener,
    private val isClickable: Boolean = false
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(track: Track)
        fun isClickAllowed(): Boolean
    }


    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder =
        SearchViewHolder(parent)

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {
            if (isClickable && itemClickListener.isClickAllowed()) {
                itemClickListener.onItemClick(tracks[position])
            } // Передаем нажатый элемент в активность

            // открываем нажатый элемент в AudioPlayer
            val intent: Intent = Intent(holder.itemView.context, AudioPlayer::class.java).apply {
                putExtra("trackName", tracks[position].trackName)
                putExtra("artistName", tracks[position].artistName)
                putExtra("durationTime", tracks[position].trackTimeMillis)
                putExtra("album", tracks[position].collectionName)
                putExtra("songYear", tracks[position].releaseDate)
                putExtra("songStyle", tracks[position].primaryGenreName)
                putExtra("songCountry", tracks[position].country)
                putExtra("songCover", tracks[position].artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")) // увеличиваем разрешение картинки для AudioPlayer,
                putExtra("previewUrl", tracks[position].previewUrl)
            }

            holder.itemView.context.startActivity(intent)

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