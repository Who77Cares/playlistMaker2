package com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain

import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.PlaylistEntity
import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    val repository: PlaylistRepository
): PlaylistInteractor {

    override suspend fun createPlaylist(playlist: PlaylistModel) {
        repository.createPlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<PlaylistModel>> = repository.getPlaylists()


    override fun addTrackToPlaylist(playlistId: Long, trackId: String): Flow<Pair<String, Boolean>> {
        return repository.addTrackToPlaylist(playlistId, trackId)
    }


}