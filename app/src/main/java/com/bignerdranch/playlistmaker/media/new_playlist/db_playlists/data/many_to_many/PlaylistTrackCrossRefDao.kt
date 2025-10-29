package com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.many_to_many

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bignerdranch.playlistmaker.media.db_favorite.data.FavoriteTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackCrossRefDao {

    // Проверить есть ли уже трек в плейлисте
    @Query("SELECT EXISTS(SELECT 1 FROM playlist_track_cross_ref WHERE playlistId = :playlistId AND trackId = :trackId)")
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean

    // Получить количество треков в плейлисте (для позиции)
    @Query("SELECT COUNT(*) FROM playlist_track_cross_ref WHERE playlistId = :playlistId")
    suspend fun getTrackCount(playlistId: Long): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: PlaylistTrackCrossEntity): Long


    @Query(
        """
        SELECT tracks_in_playlist.* FROM tracks_in_playlist
        INNER JOIN playlist_track_cross_ref ON tracks_in_playlist.trackId = playlist_track_cross_ref.trackId
        WHERE playlist_track_cross_ref.playlistId = :playlistId
        ORDER BY playlist_track_cross_ref.position ASC
    """
    )
    fun getTracksForPlaylist(playlistId: Long): Flow<List<TrackToPlaylistEntity>>
}