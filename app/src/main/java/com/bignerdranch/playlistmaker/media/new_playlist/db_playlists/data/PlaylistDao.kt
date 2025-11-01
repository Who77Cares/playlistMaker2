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

    @Query("SELECT * FROM playlists WHERE playlistId = :playlistId")
    fun getPlaylistById(playlistId: Long): Flow<PlaylistEntity>

    @Query("SELECT * FROM playlists ORDER BY creationTime DESC")
    fun getPlaylists(): Flow<List<PlaylistEntity>>


    @Query("UPDATE playlists SET trackSize = trackSize + 1 WHERE playlistId = :playlistId")
    suspend fun incrementTrackCount(playlistId: Long)

    @Query("UPDATE playlists SET trackSize = trackSize - 1 WHERE playlistId = :playlistId")
    suspend fun decrementTrackCount(playlistId: Long)


    // Удалить плейлист по ID
    @Query("DELETE FROM playlists WHERE playlistId = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    // Получить все trackId из определенного плейлиста
    @Query("SELECT trackId FROM playlist_track_cross_ref WHERE playlistId = :playlistId")
    suspend fun getTrackIdsFromPlaylist(playlistId: Long): List<Long>

    // метод для удаления связей
    @Query("DELETE FROM playlist_track_cross_ref WHERE playlistId = :playlistId")
    suspend fun deleteAllTracksFromPlaylist(playlistId: Long)

    @Update(entity = PlaylistEntity::class)
    suspend fun updatePlaylist(playlist: PlaylistEntity)


}