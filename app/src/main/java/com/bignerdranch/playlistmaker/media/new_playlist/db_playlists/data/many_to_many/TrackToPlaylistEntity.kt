package com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.many_to_many

import androidx.room.Entity
import androidx.room.PrimaryKey

// по сути тот же класс что и TrackEntity, только для сохранения треков в плейлист
@Entity(tableName = "tracks_in_playlist")
data class TrackToPlaylistEntity(
    @PrimaryKey(autoGenerate = false)
    val trackId: Long,

    val trackName: String,
    val artistName: String,
    val duration: String,
    val year: String,
    val album: String,
    val coverUrl: String,
    val style: String,
    val country: String,
    val previewUrl: String,
    val createdAt: Long = System.currentTimeMillis()
)


