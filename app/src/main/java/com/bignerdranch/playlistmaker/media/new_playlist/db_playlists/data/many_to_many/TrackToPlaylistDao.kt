package com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.many_to_many

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface TrackToPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackToPlaylistEntity): Long


    @Query("SELECT * FROM tracks_in_playlist WHERE trackId = :trackId LIMIT 1")
    suspend fun findTrackById(trackId: Long): TrackToPlaylistEntity?




    /**
    логика удаления трека из tracks_in_playlist если трека нет ни в одном плейлисте
     */

    //  Удаление трека из tracks_in_playlist
    @Query("DELETE FROM tracks_in_playlist WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Long)

    //  Проверка: используется ли трек хотя бы в одном плейлисте
    @Query("SELECT COUNT(*) FROM playlist_track_cross_ref WHERE trackId = :trackId")
    suspend fun getTrackUsageCount(trackId: Long): Int

    // Удалить трек, если он больше не используется ни в одном плейлисте
    @Transaction
    suspend fun deleteTrackIfUnused(trackId: Long) {
        val usageCount = getTrackUsageCount(trackId)
        if (usageCount == 0) {
            deleteTrack(trackId)
        }
    }

}