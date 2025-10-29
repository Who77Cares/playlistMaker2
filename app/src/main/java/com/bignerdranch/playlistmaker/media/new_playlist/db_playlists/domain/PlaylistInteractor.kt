package com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.domain

import com.bignerdranch.playlistmaker.media.new_playlist.db_playlists.data.PlaylistEntity
import com.bignerdranch.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun createPlaylist(playlist: PlaylistModel)

    fun getPlaylists(): Flow<List<PlaylistModel>>

    suspend fun addTrackToPlaylist(trackModel: Track, playlistId: Long): Boolean


}