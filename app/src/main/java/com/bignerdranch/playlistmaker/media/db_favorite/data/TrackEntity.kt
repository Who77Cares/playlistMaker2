package com.bignerdranch.playlistmaker.media.db_favorite.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_favorite")
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

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
) {
}