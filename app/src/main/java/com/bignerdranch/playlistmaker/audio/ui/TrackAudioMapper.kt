package com.bignerdranch.playlistmaker.audio.ui

import com.bignerdranch.playlistmaker.audio.ui.models.TrackAudioModel
import com.bignerdranch.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackAudioMapper {

    fun map(track:Track): TrackAudioModel {
        val durationFormatted = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(Date(track.trackTimeMillis.toLong()))


        val yearFormatted = try {
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                .parse(track.releaseDate)
            SimpleDateFormat("yyyy", Locale.getDefault()).format(date!!)
        } catch (e: Exception) {
            ""
        }

        return TrackAudioModel(
            trackName = track.trackName,
            artistName = track.artistName,
            duration = durationFormatted,
            year = yearFormatted,
            album = track.collectionName,
            coverUrl = track.artworkUrl100,
            style = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )

    }
}