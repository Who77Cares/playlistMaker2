package com.bignerdranch.playlistmaker

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bignerdranch.playlistmaker.media.db_favorite.data.TrackDao
import com.bignerdranch.playlistmaker.media.db_favorite.data.TrackEntity

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase: RoomDatabase() {

    abstract fun trackDao(): TrackDao

}