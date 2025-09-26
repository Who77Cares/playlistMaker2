package com.bignerdranch.playlistmaker

import com.bignerdranch.playlistmaker.audio.ui.models.TrackAudioModel
import com.bignerdranch.playlistmaker.media.db_favorite.data.db.TrackEntity
import com.bignerdranch.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackMapper {
    fun mapToAudioModel(track: Track): TrackAudioModel {

        return TrackAudioModel(
            trackName = track.trackName,
            artistName = track.artistName,
            duration = formatDuration(track),
            year = formatYear(track),
            album = track.collectionName,
            coverUrl = track.artworkUrl100,
            style = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )

    }

    fun mapToEntity(track: Track): TrackEntity {
        return TrackEntity(
            id = track.trackId.toLong(),
            trackName = track.trackName,
            artistName = track.artistName,
            duration = formatDuration(track),
            year = formatYear(track),
            album = track.collectionName,
            coverUrl = track.artworkUrl100,
            style = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    fun mapToTrackFromEntity(track: TrackEntity): Track {
        return Track(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = parseDuration(track.duration).toInt(),
            artworkUrl100 = track.coverUrl,
            trackId = track.id.toInt(),
            collectionName = track.album,
            releaseDate = parseYear(track.year),
            primaryGenreName = track.style,
            country = track.country,
            previewUrl = track.previewUrl

        )
    }



    fun formatDuration(track: Track): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(Date(track.trackTimeMillis.toLong())) ?: ""
    }

    fun formatYear(track: Track): String {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            .parse(track.releaseDate)

        return SimpleDateFormat("yyyy", Locale.getDefault()).format(date!!)
    }


    fun parseDuration(duration: String): Long {
        val parts = duration.split(":")
        val minutes = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val seconds = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return (minutes * 60 + seconds) * 1000L
    }

    fun parseYear(year: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val date = SimpleDateFormat("yyyy", Locale.getDefault()).parse(year) ?: return ""
        return sdf.format(date)
    }

}