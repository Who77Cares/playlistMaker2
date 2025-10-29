package com.bignerdranch.playlistmaker.media.db_favorite.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bignerdranch.playlistmaker.media.db_favorite.data.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun addTrackToFavorite(track: TrackEntity)

    @Delete
    suspend fun removeTrackFromFavorite(track: TrackEntity)

    // проверяем имеется ли уже трек в бд room
    @Query("SELECT EXISTS(SELECT 1 FROM track_favorite WHERE id = :trackId)")
    suspend fun isTrackFavorite(trackId: Long): Boolean

    @Query("SELECT * FROM track_favorite ORDER BY createdAt DESC")
    fun getAllTracks(): Flow<List<TrackEntity>>

}