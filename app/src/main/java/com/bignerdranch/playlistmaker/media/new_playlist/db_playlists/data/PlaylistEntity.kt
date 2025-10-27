package com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bignerdranch.playlistmaker.media.new_playlist.PlaylistMapper

@Entity(tableName = "playlists")
//метка, чтобы сконвертировать List<String> в JSON. Можно объявнить на уровне всего AppDatabase
@TypeConverters(PlaylistMapper::class)
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val creationTime: Long,
    val coverUri: String,
    val name: String,
    val description: String,

) {
}