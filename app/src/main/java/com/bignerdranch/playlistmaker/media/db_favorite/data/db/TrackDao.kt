package com.bignerdranch.playlistmaker.media.db_favorite.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.bignerdranch.playlistmaker.search.domain.models.Track

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackToFavorite(track: TrackEntity)

    @Delete
    suspend fun removeTrackFromFavorite(track: TrackEntity)

}