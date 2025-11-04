package com.bignerdranch.playlistmaker.util

import com.bignerdranch.playlistmaker.audio.ui.models.TrackAudioModel
import com.bignerdranch.playlistmaker.media.db_favorite.data.FavoriteTrackEntity
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.many_to_many.TrackToPlaylistEntity
import com.bignerdranch.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackMapper {

    fun mapToEntity(track: Track): FavoriteTrackEntity {
        return FavoriteTrackEntity(
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

    fun mapToTrackFromEntity(track: FavoriteTrackEntity): Track {
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

    //по идее класс TrackAudioModel ничем не отлчиается от Track
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

    fun convertFromTrackEntity(tracks: List<FavoriteTrackEntity>): List<Track> {
        return tracks.map { track -> mapToTrackFromEntity(track) }
    }

    fun formatDuration(track: Track): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(Date(track.trackTimeMillis.toLong())) ?: ""
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

    fun formatYear(track: Track): String {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            .parse(track.releaseDate)

        return SimpleDateFormat("yyyy", Locale.getDefault()).format(date!!)
    }



    fun mapToPlaylistTrackEntity(track: Track): TrackToPlaylistEntity {
        return TrackToPlaylistEntity(
            trackId = track.trackId.toLong(),
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

    fun mapToTrackModel(track: TrackToPlaylistEntity): Track {
        return Track(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = parseDuration(track.duration).toInt(),
            artworkUrl100 = track.coverUrl,
            trackId = track.trackId.toInt(),
            collectionName = track.album,
            releaseDate = parseYear(track.year),
            primaryGenreName = track.style,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

}