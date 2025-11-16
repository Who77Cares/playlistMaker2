package com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain

import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.PlaylistEntity
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.PlaylistRepository
import com.bignerdranch.playlistmaker.search.domain.network.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    val repository: PlaylistRepository
): PlaylistInteractor {

    override suspend fun createPlaylist(playlist: PlaylistModel) {
        repository.createPlaylist(playlist)
    }

    override fun getPlaylistById(playlistId: Long): Flow<PlaylistModel> =
        repository.getPlaylistById(playlistId)


    override fun getPlaylists(): Flow<List<PlaylistModel>> = repository.getPlaylists()


    override suspend fun addTrackToPlaylist(
        trackModel: Track,
        playlistId: Long
    ): Boolean = repository.addTrackToPlaylist(trackModel, playlistId)


    override fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>> =
        repository.getTracksFromPlaylist(playlistId)


    override suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long) {
        repository.deleteTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        repository.deletePlaylist(playlistId)
    }

    override suspend fun updatePlaylist(playlist: PlaylistModel) {
        repository.updatePlaylist(playlist)
    }

}