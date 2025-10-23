package com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data

import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain.PlaylistModel
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun createPlaylist(playlist: PlaylistModel)

    fun getPlaylists(): Flow<List<PlaylistModel>>

}