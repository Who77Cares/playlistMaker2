package com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.many_to_many

import androidx.room.Entity
import androidx.room.ForeignKey
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.PlaylistEntity

@Entity(
    tableName = "playlist_track_cross_ref",
    primaryKeys = ["playlistId", "trackId"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["playlistId"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TrackToPlaylistEntity::class,
            parentColumns = ["trackId"],
            childColumns = ["trackId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlaylistTrackCrossEntity(
    val playlistId: Long,
    val trackId: Long,
    val position: Int = 0
)
