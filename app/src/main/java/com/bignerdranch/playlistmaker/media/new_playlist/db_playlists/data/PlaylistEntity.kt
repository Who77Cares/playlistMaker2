package com.bignerdranch.playlistmaker.new_playlist.db_playlists

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bignerdranch.playlistmaker.media.new_playlist.PlaylistMapper

@Entity(tableName = "playlists")
@TypeConverters(PlaylistMapper::class) //метка, чтобы сконвертировать List<String> в JSON. Можно объявнить на уровне всего AppDatabase
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val creationTime: Long,
    val coverUri: String,
    val name: String,
    val description: String,
    val tracks: MutableList<String> = mutableListOf<String>()
) {
}