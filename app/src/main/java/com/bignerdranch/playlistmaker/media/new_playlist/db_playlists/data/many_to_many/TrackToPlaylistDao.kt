package com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.many_to_many

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrackToPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackToPlaylistEntity): Long


    @Query("SELECT * FROM tracks_in_playlist WHERE trackId = :trackId LIMIT 1")
    suspend fun findTrackById(trackId: Long): TrackToPlaylistEntity?

}