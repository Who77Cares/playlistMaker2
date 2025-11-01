package com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain

import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.PlaylistEntity
import com.bignerdranch.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun createPlaylist(playlist: PlaylistModel)

    fun getPlaylistById(playlistId: Long): Flow<PlaylistModel>

    fun getPlaylists(): Flow<List<PlaylistModel>>

    suspend fun addTrackToPlaylist(trackModel: Track, playlistId: Long): Boolean

    fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>>

    suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long)

    suspend fun deletePlaylist(playlistId: Long)

    suspend fun updatePlaylist(playlist: PlaylistModel)

}