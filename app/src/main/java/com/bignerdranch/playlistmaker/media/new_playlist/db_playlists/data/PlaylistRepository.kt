package com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data

import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.many_to_many.TrackToPlaylistEntity
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import com.bignerdranch.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun createPlaylist(playlist: PlaylistModel)

    fun getPlaylists(): Flow<List<PlaylistModel>>

    suspend fun addTrackToPlaylist(trackModel: Track, playlistId: Long): Boolean

    fun getTracksFromPlaylist(playlistId: Long): Flow<List<Track>>

    suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long)

    suspend fun deletePlaylist(playlistId: Long)

}