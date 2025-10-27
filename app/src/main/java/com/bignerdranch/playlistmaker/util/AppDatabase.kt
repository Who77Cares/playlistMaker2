package com.bignerdranch.playlistmaker.util

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bignerdranch.playlistmaker.media.db_favorite.data.TrackDao
import com.bignerdranch.playlistmaker.media.db_favorite.data.TrackEntity
import com.bignerdranch.playlistmaker.media.new_playlist.PlaylistMapper
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.PlaylistDao
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.PlaylistEntity

@Database(version = 2, entities = [TrackEntity::class, PlaylistEntity::class])
@TypeConverters(PlaylistMapper::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun favoriteTrackDao(): TrackDao

    abstract fun playlistDao(): PlaylistDao

}

