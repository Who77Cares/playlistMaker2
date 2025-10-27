package com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun createPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists ORDER BY creationTime DESC")
    fun getPlaylists(): Flow<List<PlaylistEntity>>


//    @Query("SELECT * FROM playlists WHERE playlistId = :playlistId LIMIT 1")
//    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity



}