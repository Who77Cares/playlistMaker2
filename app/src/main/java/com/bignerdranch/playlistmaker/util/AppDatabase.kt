package com.bignerdranch.playlistmaker.util

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bignerdranch.playlistmaker.media.db_favorite.data.TrackDao
import com.bignerdranch.playlistmaker.media.db_favorite.data.TrackEntity
import com.bignerdranch.playlistmaker.media.new_playlist.PlaylistMapper
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.PlaylistDao
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.PlaylistEntity
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.many_to_many.PlaylistTrackCrossEntity
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.many_to_many.PlaylistTrackCrossRefDao
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.many_to_many.TrackToPlaylistDao
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.many_to_many.TrackToPlaylistEntity

@Database(
    version = 3,
    entities = [
        TrackEntity::class,

        PlaylistEntity::class,
        TrackToPlaylistEntity::class,
        PlaylistTrackCrossEntity::class
    ]
)
@TypeConverters(PlaylistMapper::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun favoriteTrackDao(): TrackDao


    abstract fun playlistDao(): PlaylistDao
    abstract fun trackToPlaylistDao(): TrackToPlaylistDao
    abstract fun crossRefDao(): PlaylistTrackCrossRefDao

}

